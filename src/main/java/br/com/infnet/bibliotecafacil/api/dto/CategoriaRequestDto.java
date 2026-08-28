package br.com.infnet.bibliotecafacil.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDto(
        @NotBlank(message = "O nome da categoria é obrigatório.") String nome,
        String descricao) {
}
