package br.com.infnet.bibliotecafacil.api.controller;

import br.com.infnet.bibliotecafacil.api.dto.ProcessamentoReservaRequestDto;
import br.com.infnet.bibliotecafacil.api.dto.ReservaRequestDto;
import br.com.infnet.bibliotecafacil.api.dto.ReservaResponseDto;
import br.com.infnet.bibliotecafacil.aplicacao.service.ReservaService;
import br.com.infnet.bibliotecafacil.dominio.Reserva;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas")
public final class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(final ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<ReservaResponseDto> listar() {
        return this.reservaService.listar().stream()
                .map(ReservaResponseDto::criar)
                .toList();
    }

    @GetMapping("/{id}")
    public ReservaResponseDto obterPorId(final @PathVariable Long id) {
        return ReservaResponseDto.criar(this.reservaService.obterPorId(id));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDto> solicitar(
            final @Valid @RequestBody ReservaRequestDto request) {
        final Reserva reserva = this.reservaService.solicitar(request.leitorId(), request.acervoId());
        return ResponseEntity.created(URI.create("/api/reservas/" + reserva.getId()))
                .body(ReservaResponseDto.criar(reserva));
    }

    @PutMapping("/{id}/confirmacao")
    public ReservaResponseDto confirmar(
            final @PathVariable Long id,
            final @Valid @RequestBody ProcessamentoReservaRequestDto request) {
        return ReservaResponseDto.criar(
                this.reservaService.confirmar(id, request.bibliotecarioId()));
    }

    @PutMapping("/{id}/rejeicao")
    public ReservaResponseDto rejeitar(
            final @PathVariable Long id,
            final @Valid @RequestBody ProcessamentoReservaRequestDto request) {
        return ReservaResponseDto.criar(
                this.reservaService.rejeitar(id, request.bibliotecarioId()));
    }
}
