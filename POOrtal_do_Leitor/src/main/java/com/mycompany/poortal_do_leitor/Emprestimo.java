package com.mycompany.poortal_do_leitor;

import java.time.LocalDate;

public class Emprestimo {
    private long id;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataRevolucao;
    private boolean renovado;

    public LocalDate calcularDataDevolucao(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    public boolean estaAtrasado(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    public void renovar(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    public void registrarDevolucao(LocalDate data){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public LocalDate getDataRevolucao() {
        return dataRevolucao;
    }

    public void setDataRevolucao(LocalDate dataRevolucao) {
        this.dataRevolucao = dataRevolucao;
    }

    public boolean isRenovado() {
        return renovado;
    }

    public void setRenovado(boolean renovado) {
        this.renovado = renovado;
    }
}
