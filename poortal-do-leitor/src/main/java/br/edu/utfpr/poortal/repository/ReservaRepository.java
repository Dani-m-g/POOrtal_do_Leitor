package br.edu.utfpr.poortal.repository;

import br.edu.utfpr.poortal.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByUsuarioId(Long usuarioId);

    boolean existsByLivroId(Long livroId);

    boolean existsByUsuarioIdAndLivroId(Long usuarioId, Long livroId);

    Optional<Reserva> findFirstByLivroIdOrderByIdAsc(Long livroId);

    List<Reserva> findByLivroIdOrderByIdAsc(Long livroId);
}
