package com.mycompany.poortal_do_leitor.model;

import java.time.LocalDate;

public class Multa {
    private long id;
    private int diasAtraso;
    private int valor;
    private boolean quitada;
    private LocalDate dataQuitacao;

    public int calcularValor() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    public void quitar(LocalDate data) {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
}
