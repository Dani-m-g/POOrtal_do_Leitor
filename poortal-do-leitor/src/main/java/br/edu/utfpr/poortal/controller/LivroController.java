package br.edu.utfpr.poortal.controller;

import br.edu.utfpr.poortal.dto.LivroRequest;
import br.edu.utfpr.poortal.dto.Respostas;
import br.edu.utfpr.poortal.dto.Respostas.LivroResponse;
import br.edu.utfpr.poortal.service.LivroService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/livros")
public class LivroController {
    private final LivroService livros;

    public LivroController(LivroService livros) {
        this.livros = livros;
    }

    @GetMapping
    public List<LivroResponse> listar() {
        return livros.listar().stream().map(Respostas::livro).toList();
    }

    @GetMapping("/{id}")
    public LivroResponse buscar(@PathVariable Long id) {
        return Respostas.livro(livros.buscar(id));
    }

    @PostMapping
    public ResponseEntity<LivroResponse> cadastrar(@Valid @RequestBody LivroRequest dados) {
        LivroResponse livro = Respostas.livro(livros.cadastrar(dados.titulo(), dados.genero(), dados.ano(),
                dados.autor(), dados.edicao(), dados.quantidadeTotal()));
        return ResponseEntity.created(URI.create("/api/livros/" + livro.id())).body(livro);
    }

    @PutMapping("/{id}")
    public LivroResponse atualizar(@PathVariable Long id, @Valid @RequestBody LivroRequest dados) {
        return Respostas.livro(livros.atualizar(id, dados.titulo(), dados.genero(), dados.ano(),
                dados.autor(), dados.edicao(), dados.quantidadeTotal()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LivroResponse> excluir(@PathVariable Long id) {
        boolean excluido = livros.excluir(id);
        if (!excluido) {
            return ResponseEntity.accepted().body(Respostas.livro(livros.buscar(id)));
        }
        return ResponseEntity.noContent().build();
    }
}
