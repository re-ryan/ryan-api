package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("BIBLIOTECARIO")
public class Bibliotecario extends Usuario {

    @ManyToOne
    @JoinColumn(name = "biblioteca_id")
    private Biblioteca biblioteca;

    public void setBiblioteca(final Biblioteca biblioteca) {
        if (biblioteca == null) {
            throw new NullPointerException("A biblioteca é obrigatória.");
        }
        this.biblioteca = biblioteca;
    }

    public void confirmar(final Reserva reserva) {
        this.validarProcessamento(reserva);
        reserva.confirmar();
    }

    public void rejeitar(final Reserva reserva) {
        this.validarProcessamento(reserva);
        reserva.rejeitar();
    }

    public Biblioteca getBiblioteca() {
        return this.biblioteca;
    }

    @Override
    public String toString() {
        return "Bibliotecario{%s, biblioteca='%s'}"
                .formatted(this.descreverUsuario(), this.biblioteca.getNome());
    }

    private void validarProcessamento(final Reserva reserva) {
        this.validarUsuarioAtivo();
        if (reserva == null) {
            throw new NullPointerException("A reserva é obrigatória.");
        }

        if (!this.biblioteca.isAtiva()) {
            throw new IllegalStateException("A biblioteca precisa estar ativa para processar reservas.");
        }
        if (!this.biblioteca.getId().equals(reserva.getAcervo().getBiblioteca().getId())) {
            throw new IllegalArgumentException("O bibliotecário não pertence à biblioteca da reserva.");
        }
    }
}
