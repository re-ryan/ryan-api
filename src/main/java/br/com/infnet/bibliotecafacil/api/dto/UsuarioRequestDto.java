package br.com.infnet.bibliotecafacil.api.dto;

import br.com.infnet.bibliotecafacil.dominio.TipoUsuario;
import java.time.LocalDate;

public record UsuarioRequestDto(Long id, String nomeCompleto, LocalDate dataNascimento, String login, String email, String senhaHash, TipoUsuario tipoUsuario, Long bibliotecaId) {
}
