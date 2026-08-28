package br.com.infnet.bibliotecafacil.infraestrutura.repository;

import br.com.infnet.bibliotecafacil.dominio.Autor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    List<Autor> findByAtivoTrue();

    List<Autor> findByNomeContainingIgnoreCase(String nome);

    List<Autor> findAllByOrderByNomeAsc();
}
