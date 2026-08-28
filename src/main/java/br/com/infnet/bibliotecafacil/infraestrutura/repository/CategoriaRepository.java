package br.com.infnet.bibliotecafacil.infraestrutura.repository;

import br.com.infnet.bibliotecafacil.dominio.Categoria;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    List<Categoria> findByAtivaTrue();

    List<Categoria> findByNomeContainingIgnoreCase(String nome, Sort ordenacao);
}
