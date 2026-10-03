package br.edu.utfpr.poortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false)
    private String edicao;

    @Column(nullable = false)
    private int quantidadeTotal;

    @Column(nullable = false)
    private int quantidadeDisponivel;

    @Column(nullable = false)
    private boolean pendenteExclusao;

    protected Livro() {
    }

    public Livro(String titulo, String genero, int ano, String autor, String edicao,
                 int quantidadeTotal) {
        atualizar(titulo, genero, ano, autor, edicao, quantidadeTotal);
    }

    public void atualizar(String titulo, String genero, int ano, String autor, String edicao,
                          int quantidadeTotal) {
        int quantidadeEmprestada = this.quantidadeTotal - quantidadeDisponivel;
        if (quantidadeTotal < quantidadeEmprestada) {
            throw new IllegalStateException(
                    "A quantidade total não pode ser inferior à quantidade emprestada.");
        }
        this.titulo = titulo;
        this.genero = genero;
        this.ano = ano;
        this.autor = autor;
        this.edicao = edicao;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal - quantidadeEmprestada;
    }

    public void incrementarDisponibilidade() {
        if (quantidadeDisponivel >= quantidadeTotal) {
            throw new IllegalStateException(
                    "A quantidade disponível não pode ultrapassar a quantidade total.");
        }
        quantidadeDisponivel++;
    }

    public void decrementarDisponibilidade() {
        if (quantidadeDisponivel <= 0) {
            throw new IllegalStateException("Não há unidades disponíveis.");
        }
        quantidadeDisponivel--;
    }

    public void marcarPendenteExclusao() {
        pendenteExclusao = true;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getGenero() {
        return genero;
    }

    public int getAno() {
        return ano;
    }

    public String getAutor() {
        return autor;
    }

    public String getEdicao() {
        return edicao;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public boolean isPendenteExclusao() {
        return pendenteExclusao;
    }
}
