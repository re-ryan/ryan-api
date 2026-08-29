package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Reserva {

    @Id
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "leitor_id")
    private Leitor leitor;
    @ManyToOne(optional = false)
    @JoinColumn(name = "acervo_id")
    private Acervo acervo;
    private LocalDateTime dataReserva;
    @Enumerated(EnumType.STRING)
    private StatusReserva status;

    void setId(final Long id) {
        this.id = id;
    }

    void setLeitor(final Leitor leitor) {
        this.leitor = leitor;
    }

    void setAcervo(final Acervo acervo) {
        this.acervo = acervo;
    }

    void iniciar() {
        this.acervo.reservarUnidade();
        this.dataReserva = LocalDateTime.now();
        this.status = StatusReserva.PENDENTE;
    }

    void confirmar() {
        this.validarStatusPendente();
        this.status = StatusReserva.CONFIRMADA;
    }

    void rejeitar() {
        this.validarStatusPendente();
        this.acervo.liberarUnidade();
        this.status = StatusReserva.REJEITADA;
    }

    public Long getId() {
        return this.id;
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
