package br.com.infnet.bibliotecafacil.infraestrutura.repository;

import br.com.infnet.bibliotecafacil.dominio.Biblioteca;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    boolean existsByCpfCnpj(String cpfCnpj);

    boolean existsByCpfCnpjAndIdNot(String cpfCnpj, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Biblioteca> findByAtivaTrue();

    List<Biblioteca> findByNomeContainingIgnoreCase(String nome);

    List<Biblioteca> findAllByOrderByNomeAsc();
}
