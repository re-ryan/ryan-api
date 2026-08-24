package br.com.infnet.bibliotecafacil.aplicacao.exception;

public final class DadosInvalidosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(final String mensagem) {
        super(mensagem);
    }

}
