package br.com.infnet.bibliotecafacil.api.dto;

import jakarta.validation.constraints.NotNull;

public record ReservaRequestDto(
        @NotNull(message = "O leitor é obrigatório.") Long leitorId,
        @NotNull(message = "O acervo é obrigatório.") Long acervoId) {
}
