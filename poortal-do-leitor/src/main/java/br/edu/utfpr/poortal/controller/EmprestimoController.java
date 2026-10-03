package br.edu.utfpr.poortal.controller;

import br.edu.utfpr.poortal.dto.EmprestimoRequest;
import br.edu.utfpr.poortal.dto.Respostas;
import br.edu.utfpr.poortal.dto.Respostas.EmprestimoResponse;
import br.edu.utfpr.poortal.service.CirculacaoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {
    private final CirculacaoService circulacao;

    public EmprestimoController(CirculacaoService circulacao) {
        this.circulacao = circulacao;
    }

    @GetMapping
    public List<EmprestimoResponse> listar() {
        return circulacao.listarEmprestimos().stream().map(Respostas::emprestimo).toList();
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponse> emprestar(@Valid @RequestBody EmprestimoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Respostas.emprestimo(circulacao.emprestar(dados.usuarioId(), dados.livroId())));
    }

    @PostMapping("/{id}/devolucao")
    public EmprestimoResponse devolver(@PathVariable Long id) {
        return Respostas.emprestimo(circulacao.devolver(id));
    }

    @PostMapping("/{id}/renovacao")
    public EmprestimoResponse renovar(@PathVariable Long id) {
        return Respostas.emprestimo(circulacao.renovar(id));
    }
}
