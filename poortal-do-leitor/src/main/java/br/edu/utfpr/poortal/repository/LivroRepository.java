package br.edu.utfpr.poortal.repository;

import br.edu.utfpr.poortal.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
