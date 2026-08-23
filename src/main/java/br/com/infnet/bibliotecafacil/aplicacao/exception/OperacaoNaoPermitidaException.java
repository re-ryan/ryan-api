package br.com.infnet.bibliotecafacil.aplicacao.exception;

public final class OperacaoNaoPermitidaException extends RuntimeException {

    public OperacaoNaoPermitidaException(final String mensagem) {
        super(mensagem);
    }

}
