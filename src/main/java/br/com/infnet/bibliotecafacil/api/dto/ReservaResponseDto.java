package br.com.infnet.bibliotecafacil.api.dto;

import br.com.infnet.bibliotecafacil.dominio.Reserva;
import br.com.infnet.bibliotecafacil.dominio.StatusReserva;
import java.time.LocalDateTime;

public record ReservaResponseDto(
        Long id,
        Long leitorId,
        String leitorNome,
        Long acervoId,
        Long bibliotecaId,
        String bibliotecaNome,
        Long livroId,
        String livroTitulo,
        LocalDateTime dataReserva,
        StatusReserva status) {

    public static ReservaResponseDto criar(final Reserva reserva) {
        return new ReservaResponseDto(
                reserva.getId(),
                reserva.getLeitor().getId(),
                reserva.getLeitor().getNomeCompleto(),
                reserva.getAcervo().getId(),
                reserva.getAcervo().getBiblioteca().getId(),
                reserva.getAcervo().getBiblioteca().getNome(),
                reserva.getAcervo().getLivro().getId(),
                reserva.getAcervo().getLivro().getTitulo(),
                reserva.getDataReserva(),
                reserva.getStatus());
    }
}
