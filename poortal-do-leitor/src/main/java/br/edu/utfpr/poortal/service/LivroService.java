package br.edu.utfpr.poortal.service;

import br.edu.utfpr.poortal.exception.NotFoundException;
import br.edu.utfpr.poortal.model.Livro;
import br.edu.utfpr.poortal.model.Emprestimo;
import br.edu.utfpr.poortal.repository.EmprestimoRepository;
import br.edu.utfpr.poortal.repository.LivroRepository;
import br.edu.utfpr.poortal.repository.ReservaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LivroService {
    private final LivroRepository livros;
    private final EmprestimoRepository emprestimos;
    private final ReservaRepository reservas;

    public LivroService(LivroRepository livros, EmprestimoRepository emprestimos, ReservaRepository reservas) {
        this.livros = livros;
        this.emprestimos = emprestimos;
        this.reservas = reservas;
    }

    @Transactional(readOnly = true)
    public List<Livro> listar() {
        return livros.findAll();
    }

    @Transactional(readOnly = true)
    public Livro buscar(Long id) {
        return livros.findById(id)
                .orElseThrow(() -> new NotFoundException("Livro não encontrado."));
    }

    public Livro cadastrar(String titulo, String genero, int ano, String autor, String edicao, int quantidadeTotal) {
        return livros.save(new Livro(titulo, genero, ano, autor, edicao, quantidadeTotal));
    }

    public Livro atualizar(Long id, String titulo, String genero, int ano, String autor,
                           String edicao, int quantidadeTotal) {
        Livro livro = buscar(id);
        livro.atualizar(titulo, genero, ano, autor, edicao, quantidadeTotal);
        return livros.save(livro);
    }

    public boolean excluir(Long id) {
        Livro livro = buscar(id);
        if (emprestimos.existsByLivroIdAndDataDevolucaoIsNull(id) || reservas.existsByLivroId(id)) {
            livro.marcarPendenteExclusao();
            livros.save(livro);
            return false;
        }
        // A exclusão do livro não apaga multas ligadas aos empréstimos encerrados.
        List<Emprestimo> historico = emprestimos.findByLivroId(id);
        for (Emprestimo emprestimo : historico) {
            emprestimo.desvincularLivro();
        }
        emprestimos.saveAll(historico);
        livros.delete(livro);
        return true;
    }
}
