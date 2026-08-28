package br.com.infnet.bibliotecafacil.api.dto;

import br.com.infnet.bibliotecafacil.dominio.Endereco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BibliotecaRequestDto(
        @NotBlank(message = "O nome da biblioteca é obrigatório.") String nome,
        @NotBlank(message = "O CPF ou CNPJ da biblioteca é obrigatório.") String cpfCnpj,
        @NotBlank(message = "O e-mail da biblioteca é obrigatório.")
        @Email(message = "O e-mail da biblioteca deve ser válido.")
        String email,
        String telefone,
        Endereco endereco) {
}
