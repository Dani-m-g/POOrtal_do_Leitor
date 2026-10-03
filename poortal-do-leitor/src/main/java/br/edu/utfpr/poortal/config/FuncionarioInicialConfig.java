package br.edu.utfpr.poortal.config;

import br.edu.utfpr.poortal.model.TipoUsuario;
import br.edu.utfpr.poortal.repository.UsuarioRepository;
import br.edu.utfpr.poortal.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Provisionamento local opcional para permitir o primeiro acesso administrativo. */
@Component
public class FuncionarioInicialConfig implements ApplicationRunner {
    private final UsuarioRepository usuarios;
    private final UsuarioService cadastro;
    private final String nome;
    private final String email;
    private final String senha;

    public FuncionarioInicialConfig(UsuarioRepository usuarios, UsuarioService cadastro,
            @Value("${poortal.funcionario-inicial.nome:}") String nome,
            @Value("${poortal.funcionario-inicial.email:}") String email,
            @Value("${poortal.funcionario-inicial.senha:}") String senha) {
        this.usuarios = usuarios;
        this.cadastro = cadastro;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (nome.isBlank() && email.isBlank() && senha.isBlank()) {
            return;
        }
        if (usuarios.count() != 0) {
            return;
        }
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            throw new IllegalArgumentException("Informe nome, e-mail e senha do funcionário inicial.");
        }
        cadastro.cadastrar(nome, email, senha, TipoUsuario.FUNCIONARIO);
    }
}
