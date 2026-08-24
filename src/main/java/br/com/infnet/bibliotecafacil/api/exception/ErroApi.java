package br.com.infnet.bibliotecafacil.api.exception;

import java.time.LocalDateTime;

public record ErroApi(LocalDateTime momento, int status, String mensagem) {
}
