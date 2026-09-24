package com.mycompany.poortal_do_leitor.model;

public class Usuario {
    private long id;
    private String nome;
    private String email;
    private String senhaHash;
    private TipoUsuario tipo;

    public int getLimiteEmprestimos() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    public int getPrazoEmprestimo() {
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
}