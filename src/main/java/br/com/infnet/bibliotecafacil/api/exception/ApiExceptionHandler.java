package br.com.infnet.bibliotecafacil.api.exception;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import java.time.LocalDateTime;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public final class ApiExceptionHandler {

    @ExceptionHandler(ObjetoNaoEncontradoException.class)
    public ResponseEntity<ErroApi> tratarNaoEncontrado(final ObjetoNaoEncontradoException exception) {
        return this.criarResposta(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApi> tratarValidacao(final MethodArgumentNotValidException exception) {
        final String mensagem = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Os dados informados são inválidos.");
        return this.criarResposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroApi> tratarViolacaoDeIntegridade(
            final DataIntegrityViolationException exception) {
        return this.criarResposta(
                HttpStatus.BAD_REQUEST,
                "Os dados informados violam uma restrição de integridade.");
    }

    @ExceptionHandler({
            DadosInvalidosException.class,
            OperacaoNaoPermitidaException.class,
            IllegalArgumentException.class,
            IllegalStateException.class,
            NullPointerException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErroApi> tratarRequisicaoInvalida(final Exception exception) {
        return this.criarResposta(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<ErroApi> criarResposta(final HttpStatus status, final String mensagem) {
        final ErroApi erro = new ErroApi(LocalDateTime.now(), status.value(), mensagem);
        return ResponseEntity.status(status).body(erro);
    }
}
