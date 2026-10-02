package com.mycompany.poortal_do_leitor.model;

public class Usuario {
    private long id;
    private String nome;
    private String email;
    private String senhaHash;
    private TipoUsuario tipo;

    public Usuario(String nome, String email, String senhaHash, TipoUsuario tipo) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipo = tipo;
    }

    public int getLimiteEmprestimos() {
        return switch (tipo) {
            case ALUNO -> 5;
            case PROFESSOR, FUNCIONARIO -> 8;
        };
    }

    public int getPrazoEmprestimo() {
        return switch (tipo) {
            case ALUNO -> 7;
            case PROFESSOR, FUNCIONARIO -> 28;
        };
    }
}