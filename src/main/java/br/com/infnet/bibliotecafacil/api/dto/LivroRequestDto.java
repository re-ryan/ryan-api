package br.com.infnet.bibliotecafacil.api.dto;

public record LivroRequestDto(Long id, String titulo, String isbn10, String isbn13, String editora, Integer anoPublicacao, String edicao, String descricao, String urlImagemCapa) {
}
