package com.mycompany.poortal_do_leitor.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Multa {
    private long id;
    private Emprestimo emprestimo;
    private int diasAtraso;
    private int valor;
    private boolean quitada;
    private LocalDate dataQuitacao;

    public Multa(Emprestimo emprestimo) {
        if (emprestimo.getDataDevolucao() == null) {
            throw new IllegalStateException(
                    "A devolução deve ser registrada antes da multa."
            );
        }

        if (!emprestimo.estaAtrasado()) {
            throw new IllegalStateException(
                    "O empréstimo não possui atraso."
            );
        }

        this.emprestimo = emprestimo;

        this.diasAtraso = (int) ChronoUnit.DAYS.between(
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDataDevolucao()
        );

        this.valor = calcularValor();
        this.quitada = false;
    }

    public int calcularValor() {
        return diasAtraso;
    }

    public void quitar(LocalDate data) {
        quitada = true;
        dataQuitacao = data;
    }
}