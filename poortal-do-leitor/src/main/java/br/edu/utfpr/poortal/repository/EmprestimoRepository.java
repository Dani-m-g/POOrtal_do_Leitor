package br.edu.utfpr.poortal.repository;

import br.edu.utfpr.poortal.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByUsuarioId(Long usuarioId);

    List<Emprestimo> findByLivroId(Long livroId);

    long countByUsuarioIdAndDataDevolucaoIsNull(Long usuarioId);

    boolean existsByUsuarioIdAndDataDevolucaoIsNull(Long usuarioId);

    boolean existsByLivroIdAndDataDevolucaoIsNull(Long livroId);

    boolean existsByUsuarioIdAndLivroIdAndDataDevolucaoIsNull(Long usuarioId, Long livroId);
}
