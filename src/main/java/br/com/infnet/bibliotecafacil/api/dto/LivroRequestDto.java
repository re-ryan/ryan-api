package br.com.infnet.bibliotecafacil.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LivroRequestDto(
        @NotBlank(message = "O título do livro é obrigatório.") String titulo,
        String isbn10,
        String isbn13,
        String editora,
        Integer anoPublicacao,
        String edicao,
        String descricao,
        String urlImagemCapa) {
}
