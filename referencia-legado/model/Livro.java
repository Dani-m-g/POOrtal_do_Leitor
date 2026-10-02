package com.mycompany.poortal_do_leitor.model;

public class Livro {
    private long id;
    private String titulo;
    private String genero;
    private int ano;
    private String autor;
    private String edicao;
    private int quantidadeTotal;
    private int quantidadeDisponivel;
    private boolean pendenteExclusao;

    public Livro(String titulo, String genero, int ano,
                 String autor, String edicao, int quantidadeTotal) {

        this.titulo = titulo;
        this.genero = genero;
        this.ano = ano;
        this.autor = autor;
        this.edicao = edicao;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal;
        this.pendenteExclusao = false;
    }

    public void marcarPendenteExclusao() {
        pendenteExclusao = true;
    }

    public void incrementarDisponibilidade() {
        if (quantidadeDisponivel >= quantidadeTotal) {
            throw new IllegalStateException(
                    "A quantidade disponível não pode ultrapassar a quantidade total."
            );
        }

        quantidadeDisponivel++;
    }
    
    public void decrementarDisponibilidade() {
        if (quantidadeDisponivel <= 0) {
            throw new IllegalStateException(
                    "Não há unidades disponíveis."
            );
        }

        quantidadeDisponivel--;
    }
}