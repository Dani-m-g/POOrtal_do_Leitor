package br.edu.utfpr.poortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "emprestimos")
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id")
    private Livro livro;

    // Preserva a identificação no momento da exclusão, sem manter os cadastros.
    private String nomeUsuarioHistorico;

    private String tituloLivroHistorico;

    @Column(nullable = false)
    private LocalDate dataEmprestimo;

    @Column(nullable = false)
    private LocalDate dataPrevistaDevolucao;

    private LocalDate dataDevolucao;

    @Column(nullable = false)
    private boolean renovado;

    protected Emprestimo() {
    }

    public Emprestimo(Usuario usuario, Livro livro, LocalDate dataEmprestimo) {
        this.usuario = Objects.requireNonNull(usuario);
        this.livro = Objects.requireNonNull(livro);
        this.dataEmprestimo = Objects.requireNonNull(dataEmprestimo);
        this.dataPrevistaDevolucao = dataEmprestimo.plusDays(usuario.getPrazoEmprestimo());
    }

    public boolean estaAtrasado() {
        LocalDate dataReferencia = dataDevolucao != null ? dataDevolucao : LocalDate.now();
        return dataReferencia.isAfter(dataPrevistaDevolucao);
    }

    public void renovar() {
        if (dataDevolucao != null) {
            throw new IllegalStateException("O empréstimo já foi devolvido.");
        }
        if (renovado) {
            throw new IllegalStateException("O empréstimo já foi renovado.");
        }
        if (estaAtrasado()) {
            throw new IllegalStateException("O empréstimo está atrasado.");
        }
        dataPrevistaDevolucao = dataPrevistaDevolucao.plusDays(usuario.getPrazoEmprestimo());
        renovado = true;
    }

    public void registrarDevolucao(LocalDate data) {
        Objects.requireNonNull(data);
        if (dataDevolucao != null) {
            throw new IllegalStateException("O empréstimo já foi devolvido.");
        }
        if (data.isBefore(dataEmprestimo)) {
            throw new IllegalStateException("A devolução não pode anteceder o empréstimo.");
        }
        dataDevolucao = data;
    }

    public void desvincularUsuario() {
        if (dataDevolucao == null) {
            throw new IllegalStateException("Não é possível excluir o usuário de um empréstimo ativo.");
        }
        if (usuario != null) {
            nomeUsuarioHistorico = usuario.getNome();
            usuario = null;
        }
    }

    public void desvincularLivro() {
        if (dataDevolucao == null) {
            throw new IllegalStateException("Não é possível excluir o livro de um empréstimo ativo.");
        }
        if (livro != null) {
            tituloLivroHistorico = livro.getTitulo();
            livro = null;
        }
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public String getNomeUsuarioHistorico() {
        return nomeUsuarioHistorico;
    }

    public String getTituloLivroHistorico() {
        return tituloLivroHistorico;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public boolean isRenovado() {
        return renovado;
    }
}
