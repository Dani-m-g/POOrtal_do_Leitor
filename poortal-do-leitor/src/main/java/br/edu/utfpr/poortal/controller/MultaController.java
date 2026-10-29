package br.edu.utfpr.poortal.controller;

import br.edu.utfpr.poortal.dto.Respostas;
import br.edu.utfpr.poortal.dto.Respostas.MultaResponse;
import br.edu.utfpr.poortal.service.CirculacaoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/multas")
public class MultaController {
    private final CirculacaoService circulacao;

    public MultaController(CirculacaoService circulacao) {
        this.circulacao = circulacao;
    }

    @GetMapping
    public List<MultaResponse> listar() {
        return circulacao.listarMultas().stream().map(Respostas::multa).toList();
    }

    @PostMapping("/{id}/quitacao")
    public MultaResponse quitar(@PathVariable Long id) {
        return Respostas.multa(circulacao.quitarMulta(id));
    }
}
