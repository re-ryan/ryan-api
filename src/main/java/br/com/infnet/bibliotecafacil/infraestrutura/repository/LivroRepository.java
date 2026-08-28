package br.com.infnet.bibliotecafacil.infraestrutura.repository;

import br.com.infnet.bibliotecafacil.dominio.Livro;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    boolean existsByIsbn13(String isbn13);

    boolean existsByIsbn13AndIdNot(String isbn13, Long id);

    boolean existsByIsbn10(String isbn10);

    boolean existsByIsbn10AndIdNot(String isbn10, Long id);

    List<Livro> findByAtivoTrue();

    List<Livro> findByTituloContainingIgnoreCase(String titulo, Sort ordenacao);
}
