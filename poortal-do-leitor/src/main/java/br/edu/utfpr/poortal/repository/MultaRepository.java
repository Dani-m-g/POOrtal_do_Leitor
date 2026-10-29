package br.edu.utfpr.poortal.repository;

import br.edu.utfpr.poortal.model.Multa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultaRepository extends JpaRepository<Multa, Long> {
    boolean existsByEmprestimoUsuarioIdAndQuitadaFalse(Long usuarioId);
}
