package br.edu.utfpr.poortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "multas")
public class Multa {
    private static final BigDecimal VALOR_DIARIO = new BigDecimal("1.00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emprestimo_id", nullable = false, unique = true)
    private Emprestimo emprestimo;

    @Column(nullable = false)
    private long diasAtraso;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private boolean quitada;

    private LocalDate dataQuitacao;

    protected Multa() {
    }

    public Multa(Emprestimo emprestimo) {
        Objects.requireNonNull(emprestimo);
        if (emprestimo.getDataDevolucao() == null) {
            throw new IllegalStateException("A devolução deve ser registrada antes da multa.");
        }
        if (!emprestimo.estaAtrasado()) {
            throw new IllegalStateException("O empréstimo não possui atraso.");
        }
        this.emprestimo = emprestimo;
        this.diasAtraso = ChronoUnit.DAYS.between(
                emprestimo.getDataPrevistaDevolucao(), emprestimo.getDataDevolucao());
        this.valor = VALOR_DIARIO.multiply(BigDecimal.valueOf(diasAtraso));
    }

    public void quitar(LocalDate data) {
        Objects.requireNonNull(data);
        if (quitada) {
            throw new IllegalStateException("A multa já foi quitada.");
        }
        if (data.isBefore(emprestimo.getDataDevolucao())) {
            throw new IllegalStateException("A quitação não pode anteceder a devolução.");
        }
        quitada = true;
        dataQuitacao = data;
    }

    public Long getId() {
        return id;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public long getDiasAtraso() {
        return diasAtraso;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public boolean isQuitada() {
        return quitada;
    }

    public LocalDate getDataQuitacao() {
        return dataQuitacao;
    }
}
