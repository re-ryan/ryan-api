package br.com.infnet.bibliotecafacil.infraestrutura.repository;

import br.com.infnet.bibliotecafacil.dominio.Acervo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcervoRepository extends JpaRepository<Acervo, Long> {
}
