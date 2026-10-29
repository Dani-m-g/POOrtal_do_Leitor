package br.edu.utfpr.poortal.service;

import br.edu.utfpr.poortal.exception.NotFoundException;
import br.edu.utfpr.poortal.exception.RegraNegocioException;
import br.edu.utfpr.poortal.model.TipoUsuario;
import br.edu.utfpr.poortal.model.Usuario;
import br.edu.utfpr.poortal.model.Emprestimo;
import br.edu.utfpr.poortal.repository.EmprestimoRepository;
import br.edu.utfpr.poortal.repository.MultaRepository;
import br.edu.utfpr.poortal.repository.ReservaRepository;
import br.edu.utfpr.poortal.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final EmprestimoRepository emprestimos;
    private final MultaRepository multas;
    private final ReservaRepository reservas;
    private final PasswordEncoder senhas;

    public UsuarioService(UsuarioRepository usuarios, EmprestimoRepository emprestimos,
                          MultaRepository multas, ReservaRepository reservas, PasswordEncoder senhas) {
        this.usuarios = usuarios;
        this.emprestimos = emprestimos;
        this.multas = multas;
        this.reservas = reservas;
        this.senhas = senhas;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarios.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscar(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarios.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado."));
    }

    public Usuario cadastrar(String nome, String email, String senha, TipoUsuario tipo) {
        String emailNormalizado = normalizarEmail(email);
        if (usuarios.existsByEmail(emailNormalizado)) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail.");
        }
        return usuarios.save(new Usuario(nome, emailNormalizado, definirSenha(senha, null, tipo), tipo));
    }

    public Usuario atualizar(Long id, String nome, String email, String senha, TipoUsuario tipo) {
        Usuario usuario = buscar(id);
        if (tipo != null && tipo != TipoUsuario.FUNCIONARIO) {
            verificarUltimoFuncionario(usuario);
        }
        String emailNormalizado = normalizarEmail(email);
        if (usuarios.existsByEmailAndIdNot(emailNormalizado, id)) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail.");
        }
        usuario.atualizar(nome, emailNormalizado, definirSenha(senha, usuario.getSenhaHash(), tipo), tipo);
        return usuarios.save(usuario);
    }

    public void excluir(Long id) {
        Usuario usuario = buscar(id);
        verificarUltimoFuncionario(usuario);
        if (emprestimos.existsByUsuarioIdAndDataDevolucaoIsNull(id)
                || multas.existsByEmprestimoUsuarioIdAndQuitadaFalse(id)
                || reservas.existsByUsuarioId(id)) {
            throw new RegraNegocioException("Usuário possui empréstimos, multas ou reservas pendentes.");
        }
        // Desvincula os empréstimos encerrados antes da exclusão física.
        List<Emprestimo> historico = emprestimos.findByUsuarioId(id);
        for (Emprestimo emprestimo : historico) {
            emprestimo.desvincularUsuario();
        }
        emprestimos.saveAll(historico);
        usuarios.delete(usuario);
    }

    private void verificarUltimoFuncionario(Usuario usuario) {
        if (usuario.getTipo() == TipoUsuario.FUNCIONARIO
                && usuarios.countByTipo(TipoUsuario.FUNCIONARIO) <= 1) {
            throw new RegraNegocioException(
                    "O último funcionário não pode ser excluído nem ter sua categoria alterada. Cadastre outro funcionário primeiro.");
        }
    }

    private String definirSenha(String senha, String hashAtual, TipoUsuario tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Informe o tipo de usuário.");
        }
        String hash = hashAtual;
        if (senha != null && !senha.isBlank()) {
            if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalArgumentException("A senha deve ocupar no máximo 72 bytes em UTF-8.");
            }
            hash = senhas.encode(senha);
        }
        if (tipo == TipoUsuario.FUNCIONARIO && hash == null) {
            throw new IllegalArgumentException("Informe uma senha para o funcionário.");
        }
        return hash;
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Informe o e-mail do usuário.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
