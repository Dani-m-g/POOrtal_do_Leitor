package com.mycompany.poortal_do_leitor.model;

import java.time.LocalDateTime;

public class Reserva {
    private long id;
    private Usuario usuario;
    private Livro livro;
    private LocalDateTime dataSolicitacao;

    public Reserva(Usuario usuario, Livro livro) {
        this.usuario = usuario;
        this.livro = livro;
        this.dataSolicitacao = LocalDateTime.now();
    }
}