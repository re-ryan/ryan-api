package br.com.infnet.bibliotecafacil.dominio;

import java.util.ArrayList;
import java.util.List;

public final class Leitor extends Usuario {

    private final List<Reserva> reservas = new ArrayList<>();

    public Reserva reservar(final Long idReserva, final Acervo acervo) {
        this.validarUsuarioAtivo();
        if (acervo == null) {
            throw new NullPointerException("O acervo é obrigatório.");
        }
        this.validarReservaPendenteDuplicada(acervo);
        final Reserva reserva = new Reserva();
        reserva.setId(idReserva);
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
