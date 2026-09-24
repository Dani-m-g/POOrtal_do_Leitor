package com.mycompany.poortal_do_leitor.model;

import java.time.LocalDate;

public class Emprestimo {
    private long id;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private boolean renovado;

    public LocalDate calcularDataDevolucao() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    public boolean estaAtrasado() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    public void renovar() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    public void registrarDevolucao(LocalDate data) {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
}