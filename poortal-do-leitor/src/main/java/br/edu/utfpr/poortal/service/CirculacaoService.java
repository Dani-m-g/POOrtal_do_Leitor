package br.edu.utfpr.poortal.service;

import br.edu.utfpr.poortal.exception.NotFoundException;
import br.edu.utfpr.poortal.exception.RegraNegocioException;
import br.edu.utfpr.poortal.model.Emprestimo;
import br.edu.utfpr.poortal.model.Livro;
import br.edu.utfpr.poortal.model.Multa;
import br.edu.utfpr.poortal.model.Reserva;
import br.edu.utfpr.poortal.model.Usuario;
import br.edu.utfpr.poortal.repository.EmprestimoRepository;
import br.edu.utfpr.poortal.repository.LivroRepository;
import br.edu.utfpr.poortal.repository.MultaRepository;
import br.edu.utfpr.poortal.repository.ReservaRepository;
import br.edu.utfpr.poortal.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CirculacaoService {
    private final UsuarioRepository usuarios;
    private final LivroRepository livros;
    private final EmprestimoRepository emprestimos;
    private final ReservaRepository reservas;
    private final MultaRepository multas;

    public CirculacaoService(UsuarioRepository usuarios, LivroRepository livros, EmprestimoRepository emprestimos,
                             ReservaRepository reservas, MultaRepository multas) {
        this.usuarios = usuarios;
        this.livros = livros;
        this.emprestimos = emprestimos;
        this.reservas = reservas;
        this.multas = multas;
    }

    public Emprestimo emprestar(Long usuarioId, Long livroId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Livro livro = buscarLivro(livroId);
        verificarMultas(usuarioId);
        if (emprestimos.countByUsuarioIdAndDataDevolucaoIsNull(usuarioId) >= usuario.getLimiteEmprestimos()) {
            throw new RegraNegocioException("Usuário atingiu o limite de empréstimos ativos.");
        }
        Reserva primeira = reservas.findFirstByLivroIdOrderByIdAsc(livroId).orElse(null);
        if (primeira != null && !primeira.getUsuario().getId().equals(usuarioId)) {
            throw new RegraNegocioException("O primeiro usuário da fila de reserva tem prioridade.");
        }
        if (livro.isPendenteExclusao() && primeira == null) {
            throw new RegraNegocioException("Livro pendente de exclusão só pode atender reservas existentes.");
        }
        livro.decrementarDisponibilidade();
        livros.save(livro);
        Emprestimo emprestimo = emprestimos.save(new Emprestimo(usuario, livro, LocalDate.now()));
        if (primeira != null) {
            reservas.delete(primeira);
        }
        return emprestimo;
    }

    public Emprestimo devolver(Long emprestimoId) {
        Emprestimo emprestimo = buscarEmprestimo(emprestimoId);
        emprestimo.registrarDevolucao(LocalDate.now());
        Livro livro = emprestimo.getLivro();
        livro.incrementarDisponibilidade();
        livros.save(livro);
        if (emprestimo.estaAtrasado()) {
            multas.save(new Multa(emprestimo));
        }
        return emprestimos.save(emprestimo);
    }

    public Emprestimo renovar(Long emprestimoId) {
        Emprestimo emprestimo = buscarEmprestimo(emprestimoId);
        if (emprestimo.getDataDevolucao() != null) {
            throw new RegraNegocioException("O empréstimo já foi devolvido.");
        }
        verificarMultas(emprestimo.getUsuario().getId());
        if (reservas.existsByLivroId(emprestimo.getLivro().getId())) {
            throw new RegraNegocioException("Livro possui reservas ativas.");
        }
        emprestimo.renovar();
        return emprestimos.save(emprestimo);
    }

    public Reserva reservar(Long usuarioId, Long livroId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Livro livro = buscarLivro(livroId);
        verificarMultas(usuarioId);
        if (livro.isPendenteExclusao()) {
            throw new RegraNegocioException("Livro pendente de exclusão não aceita novas reservas.");
        }
        if (livro.getQuantidadeDisponivel() > 0) {
            throw new RegraNegocioException("Livro possui unidades disponíveis para empréstimo.");
        }
        if (reservas.existsByUsuarioIdAndLivroId(usuarioId, livroId)) {
            throw new RegraNegocioException("Usuário já está na fila de reserva desse livro.");
        }
        if (emprestimos.existsByUsuarioIdAndLivroIdAndDataDevolucaoIsNull(usuarioId, livroId)) {
            throw new RegraNegocioException("Usuário possui empréstimo ativo desse livro.");
        }
        return reservas.save(new Reserva(usuario, livro));
    }

    public void removerReserva(Long reservaId) {
        Reserva reserva = reservas.findById(reservaId)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada."));
        reservas.delete(reserva);
    }

    public Multa quitarMulta(Long multaId) {
        Multa multa = multas.findById(multaId)
                .orElseThrow(() -> new NotFoundException("Multa não encontrada."));
        multa.quitar(LocalDate.now());
        return multas.save(multa);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimos() {
        return emprestimos.findAll();
    }

    @Transactional(readOnly = true)
    public List<Multa> listarMultas() {
        return multas.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarReservas(Long livroId) {
        buscarLivro(livroId);
        return reservas.findByLivroIdOrderByIdAsc(livroId);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    private Livro buscarLivro(Long id) {
        return livros.findById(id)
                .orElseThrow(() -> new NotFoundException("Livro não encontrado."));
    }

    private Emprestimo buscarEmprestimo(Long id) {
        return emprestimos.findById(id)
                .orElseThrow(() -> new NotFoundException("Empréstimo não encontrado."));
    }

    private void verificarMultas(Long usuarioId) {
        if (multas.existsByEmprestimoUsuarioIdAndQuitadaFalse(usuarioId)) {
            throw new RegraNegocioException("Usuário possui multa pendente.");
        }
    }
}
