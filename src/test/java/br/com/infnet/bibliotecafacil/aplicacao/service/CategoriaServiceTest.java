package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Categoria;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(CategoriaService.class)
class CategoriaServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @Test
    public void deveExecutarOperacoesCrud() {
        final Categoria categoria = this.categoriaService.incluir(this.criarCategoria(null, "Romance"));
        final Long id = categoria.getId();

        assertNotNull(id);
        assertEquals(categoria, this.categoriaService.obterPorId(id));
        assertEquals(List.of(categoria), this.categoriaService.listar());

        final LocalDateTime dataCriacao = categoria.getDataCriacao();
        final Categoria categoriaAlterada = this.criarCategoria(id, "Romance brasileiro");
        final Categoria categoriaSalva = this.categoriaService.alterar(categoriaAlterada);
        assertEquals("Romance brasileiro", categoriaSalva.getNome());
        assertEquals(dataCriacao, categoriaSalva.getDataCriacao());
        assertEquals("Romance brasileiro", this.categoriaService.obterPorId(id).getNome());

        this.categoriaService.excluir(id);
        assertEquals(List.of(), this.categoriaService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidosOuNomeDuplicado() {
        final Categoria semNome = this.criarCategoria(null, " ");
        this.categoriaService.incluir(this.criarCategoria(null, "Romance"));
        final Categoria nomeDuplicado = this.criarCategoria(null, "ROMANCE");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.categoriaService.incluir(null)),
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
        final Categoria romance = this.criarCategoria(null, "Romance");
        final Categoria literatura = this.criarCategoria(null, "Literatura brasileira");
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
        this.categoriaService.incluir(this.criarCategoria(null, "Romance"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.categoriaService.listar().add(this.criarCategoria(null, "Literatura brasileira")));
    }

    private Categoria criarCategoria(final Long id, final String nome) {
        final Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        categoria.setDescricao("Descrição da categoria");
        return categoria;
    }

}
