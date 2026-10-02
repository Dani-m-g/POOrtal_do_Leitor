package com.mycompany.poortal_do_leitor.model;

import java.time.LocalDate;

public class Emprestimo {
    private long id;
    private Usuario usuario;
    private Livro livro;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private boolean renovado;

    public Emprestimo(Usuario usuario, Livro livro, LocalDate dataEmprestimo) {
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = calcularDataDevolucao();
        this.renovado = false;
    }

    public LocalDate calcularDataDevolucao() {
        return dataEmprestimo.plusDays(
                usuario.getPrazoEmprestimo()
        );
    }

    public boolean estaAtrasado() {
        LocalDate dataReferencia = dataDevolucao != null
                ? dataDevolucao
                : LocalDate.now();

        return dataReferencia.isAfter(dataPrevistaDevolucao);
    }

    public void renovar() {
        if (renovado) {
            throw new IllegalStateException(
                    "O empréstimo já foi renovado."
            );
        }

        if (dataDevolucao != null) {
            throw new IllegalStateException(
                    "O empréstimo já foi devolvido."
            );
        }

        if (estaAtrasado()) {
            throw new IllegalStateException(
                    "O empréstimo está atrasado."
            );
        }

        dataPrevistaDevolucao =
                dataPrevistaDevolucao.plusDays(
                        usuario.getPrazoEmprestimo()
                );

        renovado = true;
    }

    public void registrarDevolucao(LocalDate data) {
        dataDevolucao = data;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }
}