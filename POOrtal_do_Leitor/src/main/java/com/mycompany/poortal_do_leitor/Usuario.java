package com.mycompany.poortal_do_leitor;

public class Usuario {
    private long  id;
    private String nome;
    private String email;
    private String senhaHash;
    private TipoUsuario tipo;
    
    public int getLimiteEmprestimos(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }
    
    public int getPrazoEmprestimo(){
        throw new UnsupportedOperationException("Ainda não implementado.");
    }

    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
}