package br.edu.utfpr.poortal;

import br.edu.utfpr.poortal.model.Emprestimo;
import br.edu.utfpr.poortal.model.Livro;
import br.edu.utfpr.poortal.model.Multa;
import br.edu.utfpr.poortal.model.TipoUsuario;
import br.edu.utfpr.poortal.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    @ParameterizedTest
    @CsvSource({"ALUNO, 5, 7", "PROFESSOR, 8, 28", "FUNCIONARIO, 8, 28"})
    void aplicaLimitePrazoERenovacaoDoTipo(TipoUsuario tipo, int limite, int prazo) {
        Usuario usuario = new Usuario("Leitor", "leitor@utfpr.edu.br", null, tipo);
        LocalDate inicio = LocalDate.now();
        Emprestimo emprestimo = new Emprestimo(usuario, livro(), inicio);

        assertEquals(limite, usuario.getLimiteEmprestimos());
        assertEquals(inicio.plusDays(prazo), emprestimo.getDataPrevistaDevolucao());

        emprestimo.renovar();

        assertEquals(inicio.plusDays(2L * prazo), emprestimo.getDataPrevistaDevolucao());
        assertTrue(emprestimo.isRenovado());
        assertThrows(IllegalStateException.class, emprestimo::renovar);
    }

    @Test
    void permiteRenovacaoNoPrazoMasBloqueiaAtrasoEDevolucao() {
        LocalDate hoje = LocalDate.now();
        Emprestimo noPrazo = emprestimo(hoje.minusDays(7));
        Emprestimo atrasado = emprestimo(hoje.minusDays(8));
        Emprestimo devolvido = emprestimo(hoje.minusDays(2));
        devolvido.registrarDevolucao(hoje);

        noPrazo.renovar();

        assertEquals(hoje.plusDays(7), noPrazo.getDataPrevistaDevolucao());
        assertThrows(IllegalStateException.class, atrasado::renovar);
        assertThrows(IllegalStateException.class, devolvido::renovar);
        assertThrows(IllegalStateException.class, () -> devolvido.registrarDevolucao(hoje));
    }

    @Test
    void calculaMultaPelaDataEfetivaDeDevolucao() {
        Emprestimo emprestimo = emprestimo(LocalDate.of(2026, 1, 1));
        emprestimo.registrarDevolucao(LocalDate.of(2026, 1, 11));

        Multa multa = new Multa(emprestimo);

        assertEquals(3, multa.getDiasAtraso());
        assertEquals(new BigDecimal("3.00"), multa.getValor());
        assertFalse(multa.isQuitada());
        multa.quitar(LocalDate.of(2026, 1, 12));
        assertTrue(multa.isQuitada());
        assertEquals(LocalDate.of(2026, 1, 12), multa.getDataQuitacao());
    }

    @Test
    void devolucaoNoPrazoNaoGeraMultaMesmoConsultadaDepois() {
        Emprestimo emprestimo = emprestimo(LocalDate.of(2026, 1, 1));
        emprestimo.registrarDevolucao(LocalDate.of(2026, 1, 8));

        assertFalse(emprestimo.estaAtrasado());
        assertThrows(IllegalStateException.class, () -> new Multa(emprestimo));
    }

    @Test
    void atualizacaoDoAcervoPreservaExemplaresEmprestados() {
        Livro livro = livro();
        livro.decrementarDisponibilidade();

        livro.atualizar("Livro atualizado", "Romance", 2020, "Autor", "2", 3);

        assertEquals(3, livro.getQuantidadeTotal());
        assertEquals(2, livro.getQuantidadeDisponivel());
        assertThrows(IllegalStateException.class,
                () -> livro.atualizar("Livro", "Romance", 2020, "Autor", "2", 0));
        assertEquals(3, livro.getQuantidadeTotal());
    }

    private Emprestimo emprestimo(LocalDate inicio) {
        Usuario aluno = new Usuario("Aluno", "aluno@utfpr.edu.br", null, TipoUsuario.ALUNO);
        return new Emprestimo(aluno, livro(), inicio);
    }

    private Livro livro() {
        return new Livro("Livro", "Romance", 2020, "Autor", "1", 1);
    }
}
