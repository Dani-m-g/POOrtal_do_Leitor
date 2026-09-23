package com.mycompany.poortal_do_leitor;

import java.time.LocalDate;

public class Multa {
    long id;
    int diasAtraso;
    int valor;
    boolean quitada;
    LocalDate dataQuitacao;

    public int calcularValor(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    public void quitar(LocalDate data){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(int diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public boolean isQuitada() {
        return quitada;
    }

    public void setQuitada(boolean quitada) {
        this.quitada = quitada;
    }

    public LocalDate getDataQuitacao() {
        return dataQuitacao;
    }

    public void setDataQuitacao(LocalDate dataQuitacao) {
        this.dataQuitacao = dataQuitacao;
    }
}