package br.edu.utfpr.poortal.controller;

import br.edu.utfpr.poortal.dto.ReservaRequest;
import br.edu.utfpr.poortal.dto.Respostas;
import br.edu.utfpr.poortal.dto.Respostas.ReservaResponse;
import br.edu.utfpr.poortal.service.CirculacaoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final CirculacaoService circulacao;

    public ReservaController(CirculacaoService circulacao) {
        this.circulacao = circulacao;
    }

    @GetMapping
    public List<ReservaResponse> listar(@RequestParam Long livroId) {
        return circulacao.listarReservas(livroId).stream().map(Respostas::reserva).toList();
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody ReservaRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Respostas.reserva(circulacao.reservar(dados.usuarioId(), dados.livroId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        circulacao.removerReserva(id);
        return ResponseEntity.noContent().build();
    }
}
