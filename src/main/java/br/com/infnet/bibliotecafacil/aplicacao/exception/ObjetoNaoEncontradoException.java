package br.com.infnet.bibliotecafacil.aplicacao.exception;

public final class ObjetoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ObjetoNaoEncontradoException(final String mensagem) {
        super(mensagem);
    }

}
