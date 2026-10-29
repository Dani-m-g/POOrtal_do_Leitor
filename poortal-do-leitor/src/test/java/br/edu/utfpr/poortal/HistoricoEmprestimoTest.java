package br.edu.utfpr.poortal;

import br.edu.utfpr.poortal.dto.Respostas;
import br.edu.utfpr.poortal.model.Emprestimo;
import br.edu.utfpr.poortal.model.Livro;
import br.edu.utfpr.poortal.model.Multa;
import br.edu.utfpr.poortal.model.TipoUsuario;
import br.edu.utfpr.poortal.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HistoricoEmprestimoTest {
    private final Usuario usuario = new Usuario("Ana", "ana@teste.local", null, TipoUsuario.ALUNO);
    private final Livro livro = new Livro("Livro original", "Romance", 2024, "Autor", "1", 1);

    @Test
    void impedeDesvincularCadastrosDeEmprestimoAtivo() {
        Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDate.now());

        assertThrows(IllegalStateException.class, emprestimo::desvincularUsuario);
        assertThrows(IllegalStateException.class, emprestimo::desvincularLivro);
        assertSame(usuario, emprestimo.getUsuario());
        assertSame(livro, emprestimo.getLivro());
        assertNull(emprestimo.getNomeUsuarioHistorico());
        assertNull(emprestimo.getTituloLivroHistorico());
    }

    @Test
    void preservaIdentificacaoDatasEMultaAposExclusoes() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate devolucao = inicio.plusDays(10);
        Emprestimo emprestimo = new Emprestimo(usuario, livro, inicio);
        emprestimo.registrarDevolucao(devolucao);
        Multa multa = new Multa(emprestimo);
        usuario.atualizar("Ana atualizada", "ana@teste.local", null, TipoUsuario.ALUNO);
        livro.atualizar("Livro atualizado", "Romance", 2024, "Autor", "2", 1);

        emprestimo.desvincularLivro();
        multa.quitar(devolucao);
        emprestimo.desvincularUsuario();
        // Chamadas repetidas não devem apagar os nomes já preservados.
        emprestimo.desvincularLivro();
        emprestimo.desvincularUsuario();

        var resposta = Respostas.emprestimo(emprestimo);
        assertNull(resposta.usuarioId());
        assertNull(resposta.livroId());
        assertEquals("Ana atualizada", resposta.nomeUsuarioHistorico());
        assertEquals("Livro atualizado", resposta.tituloLivroHistorico());
        assertEquals(inicio, resposta.dataEmprestimo());
        assertEquals(inicio.plusDays(7), resposta.dataPrevistaDevolucao());
        assertEquals(devolucao, resposta.dataDevolucao());
        assertEquals(new BigDecimal("3.00"), multa.getValor());
        assertTrue(multa.isQuitada());
        assertThrows(IllegalStateException.class, emprestimo::renovar);
    }

    @Test
    void disponibilidadeNuncaUltrapassaLimitesDoAcervo() {
        assertThrows(IllegalStateException.class, livro::incrementarDisponibilidade);
        livro.decrementarDisponibilidade();
        assertThrows(IllegalStateException.class, livro::decrementarDisponibilidade);
        livro.incrementarDisponibilidade();
        assertEquals(1, livro.getQuantidadeDisponivel());
    }

    @Test
    void impedeQuitacaoDuplicadaEAnteriorADevolucao() {
        Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDate.of(2026, 1, 1));
        LocalDate devolucao = LocalDate.of(2026, 1, 10);
        emprestimo.registrarDevolucao(devolucao);
        Multa multa = new Multa(emprestimo);

        assertThrows(IllegalStateException.class, () -> multa.quitar(devolucao.minusDays(1)));
        assertFalse(multa.isQuitada());
        multa.quitar(devolucao);
        assertThrows(IllegalStateException.class, () -> multa.quitar(devolucao));
    }
}
