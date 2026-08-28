package br.com.infnet.bibliotecafacil.api.dto;

import br.com.infnet.bibliotecafacil.dominio.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UsuarioRequestDto(
        @NotBlank(message = "O nome completo do usuário é obrigatório.") String nomeCompleto,
        LocalDate dataNascimento,
        @NotBlank(message = "O login do usuário é obrigatório.") String login,
        @NotBlank(message = "O e-mail do usuário é obrigatório.")
        @Email(message = "O e-mail do usuário deve ser válido.")
        String email,
        @NotBlank(message = "O hash da senha do usuário é obrigatório.") String senhaHash,
        @NotNull(message = "O tipo do usuário é obrigatório.") TipoUsuario tipoUsuario,
        Long bibliotecaId) {
}
