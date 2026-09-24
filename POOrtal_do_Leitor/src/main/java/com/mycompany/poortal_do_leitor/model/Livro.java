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

    public void marcarPendenteExclusao() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    public void incrementarDisponibilidade() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    public void decrementarDisponibilidade() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
}