package br.edu.utfpr.poortal.controller;

import br.edu.utfpr.poortal.dto.Respostas;
import br.edu.utfpr.poortal.dto.Respostas.UsuarioResponse;
import br.edu.utfpr.poortal.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {
    private final UsuarioService usuarios;

    public AutenticacaoController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping
    public UsuarioResponse identificar(Authentication autenticacao) {
        return Respostas.usuario(usuarios.buscarPorEmail(autenticacao.getName()));
    }
}
