package br.com.infnet.bibliotecafacil.api.dto;

import jakarta.validation.constraints.NotNull;

public record ProcessamentoReservaRequestDto(
        @NotNull(message = "O bibliotecário é obrigatório.") Long bibliotecarioId) {
}
