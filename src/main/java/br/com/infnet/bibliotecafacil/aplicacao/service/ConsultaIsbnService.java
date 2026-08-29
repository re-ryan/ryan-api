package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.dominio.Livro;
import br.com.infnet.bibliotecafacil.infraestrutura.integracao.isbn.BrasilApiIsbnClient;
import br.com.infnet.bibliotecafacil.infraestrutura.integracao.isbn.BrasilApiLivroResponseDto;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ConsultaIsbnService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaIsbnService.class);

    private final BrasilApiIsbnClient brasilApiIsbnClient;

    public ConsultaIsbnService(final BrasilApiIsbnClient brasilApiIsbnClient) {
        this.brasilApiIsbnClient = brasilApiIsbnClient;
    }

    public void consultarApi(final Livro livro) {
        if (livro == null || livro.getIsbn13() == null || livro.getIsbn13().isBlank()) {
            return;
        }

        try {
            final BrasilApiLivroResponseDto dadosExternos = this.brasilApiIsbnClient.consultar(livro.getIsbn13());
            this.copiarDados(livro, dadosExternos);
        } catch (final FeignException.NotFound exception) {
            LOGGER.info("ISBN {} não encontrado na BrasilAPI. O cadastro usará os dados informados.", livro.getIsbn13());
        } catch (final FeignException exception) {
            LOGGER.warn("Não foi possível consultar o ISBN {} na BrasilAPI. "
                    + "O cadastro usará os dados informados. Status: {}.", livro.getIsbn13(), exception.status());
        }
    }

    private void copiarDados(final Livro livro, final BrasilApiLivroResponseDto dadosExternos) {
        if (dadosExternos == null) {
            return;
        }
        if (this.possuiTexto(dadosExternos.titulo())) {
            livro.setTitulo(dadosExternos.titulo());
        }
        if (this.possuiTexto(dadosExternos.editora())) {
            livro.setEditora(dadosExternos.editora());
        }
        if (this.possuiTexto(dadosExternos.descricao())) {
            livro.setDescricao(dadosExternos.descricao());
        }
        if (dadosExternos.anoPublicacao() != null && dadosExternos.anoPublicacao() > 0) {
            livro.setAnoPublicacao(dadosExternos.anoPublicacao());
        }
        if (this.possuiTexto(dadosExternos.urlImagemCapa())) {
            livro.setUrlImagemCapa(dadosExternos.urlImagemCapa());
        }
    }

    private boolean possuiTexto(final String valor) {
        return valor != null && !valor.isBlank();
    }
}
