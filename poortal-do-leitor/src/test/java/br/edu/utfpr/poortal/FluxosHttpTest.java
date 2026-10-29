package br.edu.utfpr.poortal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes HTTP opcionais, sem mocks. Use uma aplicação local descartável e o banco poortal_teste.
 * Configure POORTAL_TEST_URL (ex.: http://127.0.0.1:18080), POORTAL_TEST_EMAIL,
 * POORTAL_TEST_SENHA, POORTAL_TEST_DB_URL, POORTAL_TEST_DB_USUARIO e POORTAL_TEST_DB_SENHA.
 * A aplicação deve iniciar com um único funcionário e nenhum empréstimo.
 * Estes testes criam e excluem registros; nunca use o banco normal da biblioteca.
 */
@EnabledIfEnvironmentVariable(named = "POORTAL_TEST_URL", matches = ".+")
class FluxosHttpTest {
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private static final JsonMapper JSON = new JsonMapper();
    private static String endereco;
    private static String autorizacao;

    @BeforeAll
    static void configurar() throws Exception {
        endereco = System.getenv("POORTAL_TEST_URL");
        assertTrue(URI.create(endereco).getHost().matches("localhost|127\\.0\\.0\\.1"));
        assertTrue(System.getenv("POORTAL_TEST_DB_URL").matches("jdbc:mysql://[^/]+/poortal_teste(\\?.*)?"));
        String credenciais = System.getenv("POORTAL_TEST_EMAIL") + ":" + System.getenv("POORTAL_TEST_SENHA");
        autorizacao = "Basic " + Base64.getEncoder().encodeToString(credenciais.getBytes(StandardCharsets.UTF_8));
        long funcionarios = 0;
        for (JsonNode usuario : api("GET", "usuarios", null, 200)) {
            if (usuario.get("tipo").asString().equals("FUNCIONARIO")) funcionarios++;
        }
        assertEquals(1, funcionarios, "Use uma instância de teste com apenas o funcionário inicial.");
    }

    @Test
    void protegeUltimoFuncionarioSemBloquearEdicaoOuSegundoFuncionario() throws Exception {
        JsonNode atual = api("GET", "autenticacao", null, 200);
        long id = atual.get("id").asLong();
        assertTrue(api("DELETE", "usuarios/" + id, null, 409).get("mensagem").asString().contains("último funcionário"));
        for (String tipo : new String[]{"ALUNO", "PROFESSOR"}) {
            api("PUT", "usuarios/" + id, dadosUsuario(atual, tipo), 409);
        }
        api("PUT", "usuarios/" + id, dadosUsuario(atual, "FUNCIONARIO"), 200);
        assertEquals("FUNCIONARIO", api("GET", "autenticacao", null, 200).get("tipo").asString());

        JsonNode segundo = usuario("FUNCIONARIO");
        api("PUT", "usuarios/" + segundo.get("id").asLong(), dadosUsuario(segundo, "ALUNO"), 200);
        api("DELETE", "usuarios/" + segundo.get("id").asLong(), null, 204);
        long outro = usuario("FUNCIONARIO").get("id").asLong();
        api("DELETE", "usuarios/" + outro, null, 204);
        api("DELETE", "usuarios/" + id, null, 409);
    }

    @Test
    void preservaFilaMultaEHistoricoNasExclusoesFisicas() throws Exception {
        JsonNode ana = usuario("ALUNO");
        long a = ana.get("id").asLong();
        long b = usuario("ALUNO").get("id").asLong();
        long c = usuario("ALUNO").get("id").asLong();
        JsonNode livro = livro(1);
        long livroId = livro.get("id").asLong();
        long outroLivro = livro(1).get("id").asLong();
        long emprestimo = emprestar(a, livroId, 201).get("id").asLong();
        long outroEmprestimo = emprestar(a, outroLivro, 201).get("id").asLong();
        api("POST", "reservas", vinculo(a, livroId), 409);
        api("POST", "reservas", vinculo(b, livroId), 201);
        api("POST", "reservas", vinculo(b, livroId), 409);
        api("POST", "reservas", vinculo(c, livroId), 201);
        api("POST", "emprestimos/" + emprestimo + "/renovacao", null, 409);
        api("DELETE", "usuarios/" + b, null, 409);
        assertTrue(api("DELETE", "livros/" + livroId, null, 202).get("pendenteExclusao").asBoolean());
        assertTrue(api("POST", "reservas", vinculo(a, livroId), 409).get("mensagem").asString().contains("pendente de exclusão"));

        // Somente no banco descartável: simula um empréstimo com três dias de atraso.
        try (var conexao = conexao(); var sql = conexao.prepareStatement(
                "UPDATE emprestimos SET data_emprestimo=DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), "
                        + "data_prevista_devolucao=DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY) WHERE id=?")) {
            sql.setLong(1, emprestimo);
            assertEquals(1, sql.executeUpdate());
        }
        api("POST", "emprestimos/" + emprestimo + "/devolucao", null, 200);
        JsonNode multa = porCampo(api("GET", "multas", null, 200), "emprestimoId", emprestimo);
        assertEquals(3.0, multa.get("valor").asDouble());
        api("POST", "emprestimos/" + outroEmprestimo + "/renovacao", null, 409);
        emprestar(a, livroId, 409);
        assertTrue(api("POST", "reservas", vinculo(a, livroId), 409).get("mensagem").asString().contains("multa pendente"));
        emprestar(c, livroId, 409);

        for (long leitor : new long[]{b, c}) {
            long atendido = emprestar(leitor, livroId, 201).get("id").asLong();
            api("POST", "emprestimos/" + atendido + "/devolucao", null, 200);
        }
        assertEquals(0, api("GET", "reservas?livroId=" + livroId, null, 200).size());
        assertTrue(emprestar(a, livroId, 409).get("mensagem").asString().contains("multa pendente"));
        assertTrue(emprestar(b, livroId, 409).get("mensagem").asString().contains("pendente de exclusão"));
        api("DELETE", "livros/" + livroId, null, 204);
        assertEquals(0, contar("SELECT COUNT(*) FROM livros WHERE id=?", livroId));
        api("POST", "emprestimos/" + outroEmprestimo + "/devolucao", null, 200);
        api("DELETE", "usuarios/" + a, null, 409);
        api("POST", "multas/" + multa.get("id").asLong() + "/quitacao", null, 200);
        api("POST", "multas/" + multa.get("id").asLong() + "/quitacao", null, 409);
        api("DELETE", "usuarios/" + a, null, 204);
        assertEquals(0, contar("SELECT COUNT(*) FROM usuarios WHERE id=?", a));

        JsonNode historico = porCampo(api("GET", "emprestimos", null, 200), "id", emprestimo);
        assertTrue(historico.get("usuarioId").isNull());
        assertTrue(historico.get("livroId").isNull());
        assertEquals(ana.get("nome").asString(), historico.get("nomeUsuarioHistorico").asString());
        assertEquals(livro.get("titulo").asString(), historico.get("tituloLivroHistorico").asString());
        assertFalse(historico.get("dataDevolucao").isNull());
        assertTrue(porCampo(api("GET", "multas", null, 200), "id", multa.get("id").asLong()).get("quitada").asBoolean());
        api("POST", "emprestimos/" + emprestimo + "/devolucao", null, 409);
        api("POST", "emprestimos/" + emprestimo + "/renovacao", null, 409);
    }

    @ParameterizedTest
    @CsvSource({"ALUNO,5,7", "PROFESSOR,8,28", "FUNCIONARIO,8,28"})
    void aplicaLimitesPrazosERenovacao(String tipo, int limite, int dias) throws Exception {
        long leitor = usuario(tipo).get("id").asLong();
        long livroId = livro(limite + 1).get("id").asLong();
        long[] ativos = new long[limite];
        for (int i = 0; i < limite; i++) {
            JsonNode emprestimo = emprestar(leitor, livroId, 201);
            ativos[i] = emprestimo.get("id").asLong();
            assertEquals(LocalDate.parse(emprestimo.get("dataEmprestimo").asString()).plusDays(dias),
                    LocalDate.parse(emprestimo.get("dataPrevistaDevolucao").asString()));
        }
        emprestar(leitor, livroId, 409);
        api("DELETE", "usuarios/" + leitor, null, 409);
        JsonNode renovado = api("POST", "emprestimos/" + ativos[0] + "/renovacao", null, 200);
        assertEquals(LocalDate.parse(renovado.get("dataEmprestimo").asString()).plusDays(dias * 2L),
                LocalDate.parse(renovado.get("dataPrevistaDevolucao").asString()));
        api("POST", "emprestimos/" + ativos[0] + "/renovacao", null, 409);
        for (long id : ativos) api("POST", "emprestimos/" + id + "/devolucao", null, 200);
        api("DELETE", "usuarios/" + leitor, null, 204);
    }

    @Test
    void mantemConsultaPublicaEValidaEntradas() throws Exception {
        var publico = HttpRequest.newBuilder(URI.create(endereco + "/api/livros")).GET().build();
        assertEquals(200, HTTP.send(publico, HttpResponse.BodyHandlers.ofString()).statusCode());
        var privado = HttpRequest.newBuilder(URI.create(endereco + "/api/usuarios")).GET().build();
        assertEquals(401, HTTP.send(privado, HttpResponse.BodyHandlers.ofString()).statusCode());
        api("POST", "livros", Map.of("titulo", "Incompleto"), 400);
        api("POST", "emprestimos", Map.of("usuarioId", 1.9, "livroId", 1), 400);
        long leitor = usuario("ALUNO").get("id").asLong();
        long disponivel = livro(1).get("id").asLong();
        api("POST", "reservas", vinculo(leitor, disponivel), 409);
        long semEstoque = livro(0).get("id").asLong();
        emprestar(leitor, semEstoque, 409);
        long reserva = api("POST", "reservas", vinculo(leitor, semEstoque), 201).get("id").asLong();
        api("DELETE", "reservas/" + reserva, null, 204);
        assertEquals(0, api("GET", "reservas?livroId=" + semEstoque, null, 200).size());
    }

    private static JsonNode api(String metodo, String caminho, Object dados, int status) throws Exception {
        var corpo = dados == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(dados));
        var requisicao = HttpRequest.newBuilder(URI.create(endereco + "/api/" + caminho))
                .timeout(Duration.ofSeconds(10)).header("Authorization", autorizacao)
                .header("Content-Type", "application/json").method(metodo, corpo).build();
        var resposta = HTTP.send(requisicao, HttpResponse.BodyHandlers.ofString());
        assertEquals(status, resposta.statusCode(), metodo + " " + caminho + ": " + resposta.body());
        return JSON.readTree(resposta.body().isBlank() ? "null" : resposta.body());
    }

    private static JsonNode usuario(String tipo) throws Exception {
        String codigo = UUID.randomUUID().toString();
        return api("POST", "usuarios", Map.of("nome", "Leitor " + codigo, "email", codigo + "@teste.local",
                "tipo", tipo, "senha", UUID.randomUUID().toString()), 201);
    }

    private static Map<String, Object> dadosUsuario(JsonNode usuario, String tipo) {
        return Map.of("nome", usuario.get("nome").asString(), "email", usuario.get("email").asString(), "tipo", tipo);
    }

    private static JsonNode livro(int quantidade) throws Exception {
        return api("POST", "livros", Map.of("titulo", "Livro " + UUID.randomUUID(), "autor", "Autor de teste",
                "genero", "Romance", "ano", 2026, "edicao", "1", "quantidadeTotal", quantidade), 201);
    }

    private static Map<String, Long> vinculo(long usuario, long livro) {
        return Map.of("usuarioId", usuario, "livroId", livro);
    }

    private static JsonNode emprestar(long usuario, long livro, int status) throws Exception {
        return api("POST", "emprestimos", vinculo(usuario, livro), status);
    }

    private static JsonNode porCampo(JsonNode lista, String campo, long valor) {
        for (JsonNode item : lista) if (item.get(campo).asLong() == valor) return item;
        throw new AssertionError("Registro não encontrado: " + campo + "=" + valor);
    }

    private static java.sql.Connection conexao() throws Exception {
        return DriverManager.getConnection(System.getenv("POORTAL_TEST_DB_URL"),
                System.getenv("POORTAL_TEST_DB_USUARIO"), System.getenv().getOrDefault("POORTAL_TEST_DB_SENHA", ""));
    }

    private static long contar(String consulta, long id) throws Exception {
        try (var conexao = conexao(); var sql = conexao.prepareStatement(consulta)) {
            sql.setLong(1, id);
            try (var resultado = sql.executeQuery()) {
                assertTrue(resultado.next());
                return resultado.getLong(1);
            }
        }
    }
}
