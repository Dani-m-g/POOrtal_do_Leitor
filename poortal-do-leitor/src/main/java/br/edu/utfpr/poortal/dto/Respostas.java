package br.edu.utfpr.poortal.dto;

import br.edu.utfpr.poortal.model.Emprestimo;
import br.edu.utfpr.poortal.model.Livro;
import br.edu.utfpr.poortal.model.Multa;
import br.edu.utfpr.poortal.model.Reserva;
import br.edu.utfpr.poortal.model.TipoUsuario;
import br.edu.utfpr.poortal.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Representações HTTP sem credenciais nem objetos de persistência. */
public final class Respostas {
    private Respostas() {
    }

    public record UsuarioResponse(Long id, String nome, String email, TipoUsuario tipo) {
    }

    public record LivroResponse(Long id, String titulo, String genero, int ano,
            String autor, String edicao, int quantidadeTotal, int quantidadeDisponivel,
            boolean pendenteExclusao) {
    }

    public record EmprestimoResponse(Long id, Long usuarioId, Long livroId,
            LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao,
            LocalDate dataDevolucao, boolean renovado) {
    }

    public record ReservaResponse(Long id, Long usuarioId, Long livroId,
            LocalDateTime dataSolicitacao) {
    }

    public record MultaResponse(Long id, Long emprestimoId, long diasAtraso,
            BigDecimal valor, boolean quitada, LocalDate dataQuitacao) {
    }

    public static UsuarioResponse usuario(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTipo());
    }

    public static LivroResponse livro(Livro livro) {
        return new LivroResponse(livro.getId(), livro.getTitulo(), livro.getGenero(), livro.getAno(),
                livro.getAutor(), livro.getEdicao(), livro.getQuantidadeTotal(),
                livro.getQuantidadeDisponivel(), livro.isPendenteExclusao());
    }

    public static EmprestimoResponse emprestimo(Emprestimo emprestimo) {
        Long usuarioId = emprestimo.getUsuario() == null ? null : emprestimo.getUsuario().getId();
        Long livroId = emprestimo.getLivro() == null ? null : emprestimo.getLivro().getId();
        return new EmprestimoResponse(emprestimo.getId(), usuarioId, livroId, emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(), emprestimo.getDataDevolucao(), emprestimo.isRenovado());
    }

    public static ReservaResponse reserva(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getUsuario().getId(),
                reserva.getLivro().getId(), reserva.getDataSolicitacao());
    }

    public static MultaResponse multa(Multa multa) {
        return new MultaResponse(multa.getId(), multa.getEmprestimo().getId(), multa.getDiasAtraso(),
                multa.getValor(), multa.isQuitada(), multa.getDataQuitacao());
    }
}
