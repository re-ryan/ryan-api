package br.com.infnet.bibliotecafacil.infraestrutura.repository;

import br.com.infnet.bibliotecafacil.dominio.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
