package br.com.infnet.bibliotecafacil.aplicacao.exception;

public final class OperacaoNaoPermitidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperacaoNaoPermitidaException(final String mensagem) {
        super(mensagem);
    }

}
