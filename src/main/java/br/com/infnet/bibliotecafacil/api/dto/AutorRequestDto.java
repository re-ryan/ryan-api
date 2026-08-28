package br.com.infnet.bibliotecafacil.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AutorRequestDto(
        @NotBlank(message = "O nome do autor é obrigatório.") String nome,
        @NotBlank(message = "O nome de catalogação do autor é obrigatório.") String nomeCatalogacao) {
}
