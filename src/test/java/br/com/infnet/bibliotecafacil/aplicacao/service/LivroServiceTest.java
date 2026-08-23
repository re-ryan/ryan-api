package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Livro;
import java.util.List;
import org.junit.jupiter.api.Test;

class LivroServiceTest {

    private final LivroService livroService = new LivroService();

    @Test
    public void deveExecutarOperacoesCrud() {
        final Livro livro = this.criarLivro(1L, "Dom Casmurro", "9788535902778");
        this.livroService.incluir(livro);

        assertSame(livro, this.livroService.obterPorId(1L));
        assertEquals(List.of(livro), this.livroService.listar());

        final Livro livroAlterado = this.criarLivro(1L, "Dom Casmurro - edição especial", "9788535902778");
        this.livroService.alterar(livroAlterado);
        assertSame(livroAlterado, this.livroService.obterPorId(1L));

        this.livroService.excluir(1L);
        assertEquals(List.of(), this.livroService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidos() {
        final Livro semId = this.criarLivro(null, "Dom Casmurro", "9788535902778");
        final Livro semTitulo = this.criarLivro(1L, " ", "9788535902778");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.livroService.incluir(null)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.livroService.incluir(semId)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.livroService.incluir(semTitulo)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.livroService.buscarPorTitulo(" ")));
    }

    @Test
    public void naoDeveAceitarIdentificadorOuIsbnDuplicado() {
        final Livro livro = this.criarLivro(1L, "Dom Casmurro", "9788535902778");
        this.livroService.incluir(livro);

        final Livro idDuplicado = this.criarLivro(1L, "A Hora da Estrela", "9788532508126");
        final Livro isbnDuplicado = this.criarLivro(2L, "Outra edição", "9788535902778");

        assertAll(
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.livroService.incluir(idDuplicado)),
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.livroService.incluir(isbnDuplicado)));
    }

    @Test
    public void deveTratarLivroInexistente() {
        final Livro livro = this.criarLivro(99L, "Dom Casmurro", "9788535902778");

        assertAll(
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.livroService.obterPorId(99L)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.livroService.alterar(livro)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.livroService.excluir(99L)));
    }

    @Test
    public void deveFiltrarBuscarOrdenarETransformarLivros() {
        final Livro domCasmurro = this.criarLivro(1L, "Dom Casmurro", "9788535902778");
        final Livro aHoraDaEstrela = this.criarLivro(2L, "A Hora da Estrela", "9788532508126");
        domCasmurro.desativar();
        this.livroService.incluir(domCasmurro);
        this.livroService.incluir(aHoraDaEstrela);

        assertAll(
                () -> assertEquals(List.of(aHoraDaEstrela), this.livroService.listarAtivos()),
                () -> assertEquals(List.of(aHoraDaEstrela), this.livroService.buscarPorTitulo("ESTRELA")),
                () -> assertEquals(List.of(aHoraDaEstrela, domCasmurro), this.livroService.listarOrdenadosPorTitulo()),
                () -> assertEquals(List.of("Dom Casmurro", "A Hora da Estrela"), this.livroService.listarTitulos()));
    }

    @Test
    public void naoDeveExporAListaInterna() {
        this.livroService.incluir(this.criarLivro(1L, "Dom Casmurro", "9788535902778"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.livroService.listar().add(this.criarLivro(2L, "A Hora da Estrela", "9788532508126")));
    }

    private Livro criarLivro(final Long id, final String titulo, final String isbn13) {
        final Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setIsbn(null, isbn13);
        return livro;
    }

}
