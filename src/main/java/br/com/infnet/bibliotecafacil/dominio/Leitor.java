package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("LEITOR")
public class Leitor extends Usuario {

    @OneToMany(mappedBy = "leitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    public Reserva reservar(final Acervo acervo) {
        this.validarUsuarioAtivo();
        if (acervo == null) {
            throw new NullPointerException("O acervo é obrigatório.");
        }
        this.validarReservaPendenteDuplicada(acervo);
        final Reserva reserva = new Reserva();
        reserva.setLeitor(this);
        reserva.setAcervo(acervo);
        reserva.iniciar();
        this.reservas.add(reserva);
        return reserva;
    }

    public List<Reserva> getReservas() {
        return List.copyOf(this.reservas);
    }

    @Override
    public String toString() {
        return "Leitor{%s, reservas=%s}"
                .formatted(this.descreverUsuario(), this.reservas.size());
    }

    private void validarReservaPendenteDuplicada(final Acervo acervo) {
        for (final Reserva reserva : this.reservas) {
            final boolean mesmoAcervo = reserva.getAcervo().getId().equals(acervo.getId());
            if (mesmoAcervo && reserva.getStatus() == StatusReserva.PENDENTE) {
                throw new IllegalStateException(
                        "O leitor já possui uma reserva pendente para este acervo.");
            }
        }
    }
}
