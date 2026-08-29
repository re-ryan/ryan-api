package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.dominio.Livro;
import org.springframework.stereotype.Service;

@Service
public final class CadastroLivroService {

    private final ConsultaIsbnService consultaIsbnService;
    private final LivroService livroService;

    public CadastroLivroService(final ConsultaIsbnService consultaIsbnService, final LivroService livroService) {
        this.consultaIsbnService = consultaIsbnService;
        this.livroService = livroService;
    }

    public Livro incluir(final Livro livro) {
        this.consultaIsbnService.consultarApi(livro);
        return this.livroService.incluir(livro);
    }
}
