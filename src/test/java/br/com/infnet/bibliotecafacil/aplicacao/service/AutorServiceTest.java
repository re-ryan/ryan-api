package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Autor;
import java.util.List;
import org.junit.jupiter.api.Test;

class AutorServiceTest {

    private final AutorService autorService = new AutorService();

    @Test
    public void deveExecutarOperacoesCrud() {
        final Autor autor = this.criarAutor(1L, "Machado de Assis");
        this.autorService.incluir(autor);

        assertSame(autor, this.autorService.obterPorId(1L));
        assertEquals(List.of(autor), this.autorService.listar());

        final Autor autorAlterado = this.criarAutor(1L, "Joaquim Maria Machado de Assis");
        this.autorService.alterar(autorAlterado);
        assertSame(autorAlterado, this.autorService.obterPorId(1L));

        this.autorService.excluir(1L);
        assertEquals(List.of(), this.autorService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidosOuNomeDuplicado() {
        final Autor semId = this.criarAutor(null, "Machado de Assis");
        final Autor semNome = this.criarAutor(1L, " ");
        final Autor machado = this.criarAutor(1L, "Machado de Assis");
        this.autorService.incluir(machado);
        final Autor nomeDuplicado = this.criarAutor(2L, "MACHADO DE ASSIS");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.autorService.incluir(null)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.autorService.incluir(semId)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.autorService.incluir(semNome)),
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.autorService.incluir(nomeDuplicado)));
    }

    @Test
    public void deveTratarAutorInexistente() {
        final Autor autor = this.criarAutor(99L, "Machado de Assis");

        assertAll(
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.autorService.obterPorId(99L)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.autorService.alterar(autor)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.autorService.excluir(99L)));
    }

    @Test
    public void deveFiltrarBuscarOrdenarETransformarAutores() {
        final Autor machado = this.criarAutor(1L, "Machado de Assis");
        final Autor clarice = this.criarAutor(2L, "Clarice Lispector");
        machado.desativar();
        this.autorService.incluir(machado);
        this.autorService.incluir(clarice);

        assertAll(
                () -> assertEquals(List.of(clarice), this.autorService.listarAtivos()),
                () -> assertEquals(List.of(clarice), this.autorService.buscarPorNome("LISPECTOR")),
                () -> assertEquals(List.of(clarice, machado), this.autorService.listarOrdenadosPorNome()),
                () -> assertEquals(List.of("Machado de Assis", "Clarice Lispector"), this.autorService.listarNomes()));
    }

    @Test
    public void naoDeveExporAListaInterna() {
        this.autorService.incluir(this.criarAutor(1L, "Machado de Assis"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.autorService.listar().add(this.criarAutor(2L, "Clarice Lispector")));
    }

    private Autor criarAutor(final Long id, final String nome) {
        final Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        autor.setNomeCatalogacao("Nome de catalogação");
        return autor;
    }

}
