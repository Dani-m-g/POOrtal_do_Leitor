package br.edu.utfpr.poortal.model;

public enum TipoUsuario {
    ALUNO(5, 7),
    PROFESSOR(8, 28),
    FUNCIONARIO(8, 28);

    private final int limiteEmprestimos;
    private final int prazoEmprestimo;

    TipoUsuario(int limiteEmprestimos, int prazoEmprestimo) {
        this.limiteEmprestimos = limiteEmprestimos;
        this.prazoEmprestimo = prazoEmprestimo;
    }

    public int getLimiteEmprestimos() {
        return limiteEmprestimos;
    }

    public int getPrazoEmprestimo() {
        return prazoEmprestimo;
    }
}
