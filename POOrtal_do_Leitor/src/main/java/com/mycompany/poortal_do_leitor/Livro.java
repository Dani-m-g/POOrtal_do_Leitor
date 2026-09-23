package com.mycompany.poortal_do_leitor;

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

    public void marcarPendenteExclusao(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    public void incrementarDisponibilidade(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    public void decrementarExclusao(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEdicao() {
        return edicao;
    }

    public void setEdicao(String edicao) {
        this.edicao = edicao;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public boolean isPendenteExclusao() {
        return pendenteExclusao;
    }

    public void setPendenteExclusao(boolean pendenteExclusao) {
        this.pendenteExclusao = pendenteExclusao;
    }
}