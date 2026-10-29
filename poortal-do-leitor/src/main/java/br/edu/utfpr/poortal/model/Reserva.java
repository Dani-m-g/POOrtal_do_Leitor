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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_reserva_usuario_livro", columnNames = {"usuario_id", "livro_id"})
})
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    protected Reserva() {
    }

    public Reserva(Usuario usuario, Livro livro) {
        this.usuario = Objects.requireNonNull(usuario);
        this.livro = Objects.requireNonNull(livro);
        this.dataSolicitacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }
}
