package br.com.infnet.bibliotecafacil.infraestrutura.integracao.isbn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrasilApiLivroResponseDto(
        @JsonProperty("title") String titulo,
        @JsonProperty("publisher") String editora,
        @JsonProperty("synopsis") String descricao,
        @JsonProperty("year") Integer anoPublicacao,
        @JsonProperty("cover_url") String urlImagemCapa) {
}
