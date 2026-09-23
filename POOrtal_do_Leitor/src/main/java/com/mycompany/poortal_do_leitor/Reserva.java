package com.mycompany.poortal_do_leitor;

import java.time.LocalDateTime;

public class Reserva {
    private long id;
    private LocalDateTime dataSolicitacao;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
}