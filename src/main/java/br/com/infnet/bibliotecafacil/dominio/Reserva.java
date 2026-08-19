package br.com.infnet.bibliotecafacil.dominio;

import java.time.LocalDateTime;

public final class Reserva {

    private Long id;
    private Leitor leitor;
    private Acervo acervo;
    private LocalDateTime dataReserva;
    private StatusReserva status;

    public void setId(final Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O identificador de reserva deve ser positivo.");
        }
        this.id = id;
    }

    public void setLeitor(final Leitor leitor) {
        if (leitor == null) {
            throw new NullPointerException("O leitor é obrigatório.");
        }
        this.leitor = leitor;
    }

    public void setAcervo(final Acervo acervo) {
        if (acervo == null) {
            throw new NullPointerException("O acervo é obrigatório.");
        }
        this.acervo = acervo;
    }

    protected void iniciar() {
        if (this.id == null) {
            throw new NullPointerException("O identificador da reserva é obrigatório.");
        }
        if (this.leitor == null) {
            throw new NullPointerException("O leitor é obrigatório.");
        }
        if (this.acervo == null) {
            throw new NullPointerException("O acervo é obrigatório.");
        }
        this.acervo.reservarUnidade();
        this.dataReserva = LocalDateTime.now();
        this.status = StatusReserva.PENDENTE;
    }

    protected void confirmar() {
        this.validarStatusPendente();
        this.status = StatusReserva.CONFIRMADA;
    }

    protected void rejeitar() {
        this.validarStatusPendente();
        this.acervo.liberarUnidade();
        this.status = StatusReserva.REJEITADA;
    }

    public Long getId() {
        return this.id;
    }

    public Leitor getLeitor() {
        return this.leitor;
    }

    public Acervo getAcervo() {
        return this.acervo;
    }

    public LocalDateTime getDataReserva() {
        return this.dataReserva;
    }

    public StatusReserva getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "Reserva{id=%s, leitor='%s', biblioteca='%s', livro='%s', data=%s, status=%s}"
                .formatted(this.getId(), this.leitor.getNomeCompleto(),
                        this.acervo.getBiblioteca().getNome(),
                        this.acervo.getLivro().getTitulo(), this.dataReserva, this.status);
    }

    private void validarStatusPendente() {
        if (this.status != StatusReserva.PENDENTE) {
            throw new IllegalStateException("Somente uma reserva pendente pode ser processada.");
        }
    }

}
