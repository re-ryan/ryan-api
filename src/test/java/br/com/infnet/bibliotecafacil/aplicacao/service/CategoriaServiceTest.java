package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Categoria;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategoriaServiceTest {

    private final CategoriaService categoriaService = new CategoriaService();

    @Test
    public void deveExecutarOperacoesCrud() {
        final Categoria categoria = this.criarCategoria(1L, "Romance");
        this.categoriaService.incluir(categoria);

        assertSame(categoria, this.categoriaService.obterPorId(1L));
        assertEquals(List.of(categoria), this.categoriaService.listar());

        final Categoria categoriaAlterada = this.criarCategoria(1L, "Romance brasileiro");
        this.categoriaService.alterar(categoriaAlterada);
        assertSame(categoriaAlterada, this.categoriaService.obterPorId(1L));

        this.categoriaService.excluir(1L);
        assertEquals(List.of(), this.categoriaService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidosOuNomeDuplicado() {
        final Categoria semId = this.criarCategoria(null, "Romance");
        final Categoria semNome = this.criarCategoria(1L, " ");
        this.categoriaService.incluir(this.criarCategoria(1L, "Romance"));
        final Categoria nomeDuplicado = this.criarCategoria(2L, "ROMANCE");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.categoriaService.incluir(null)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.categoriaService.incluir(semId)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.categoriaService.incluir(semNome)),
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.categoriaService.incluir(nomeDuplicado)));
    }

    @Test
    public void deveTratarCategoriaInexistente() {
        final Categoria categoria = this.criarCategoria(99L, "Romance");

        assertAll(
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.categoriaService.obterPorId(99L)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.categoriaService.alterar(categoria)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.categoriaService.excluir(99L)));
    }

    @Test
    public void deveFiltrarBuscarOrdenarETransformarCategorias() {
        final Categoria romance = this.criarCategoria(1L, "Romance");
        final Categoria literatura = this.criarCategoria(2L, "Literatura brasileira");
        romance.desativar();
        this.categoriaService.incluir(romance);
        this.categoriaService.incluir(literatura);

        assertAll(
                () -> assertEquals(List.of(literatura), this.categoriaService.listarAtivas()),
                () -> assertEquals(List.of(literatura), this.categoriaService.buscarPorNome("BRASILEIRA")),
                () -> assertEquals(List.of(literatura, romance), this.categoriaService.listarOrdenadasPorNome()),
                () -> assertEquals(List.of("Romance", "Literatura brasileira"), this.categoriaService.listarNomes()));
    }

    @Test
    public void naoDeveExporAListaInterna() {
        this.categoriaService.incluir(this.criarCategoria(1L, "Romance"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.categoriaService.listar().add(this.criarCategoria(2L, "Literatura brasileira")));
    }

    private Categoria criarCategoria(final Long id, final String nome) {
        final Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        categoria.setDescricao("Descrição da categoria");
        return categoria;
    }

}
