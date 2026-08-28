package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Autor;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(AutorService.class)
class AutorServiceTest {

    @Autowired
    private AutorService autorService;

    @Test
    public void deveExecutarOperacoesCrud() {
        final Autor autor = this.autorService.incluir(this.criarAutor(null, "Machado de Assis"));
        final Long id = autor.getId();

        assertNotNull(id);
        assertEquals(autor, this.autorService.obterPorId(id));
        assertEquals(List.of(autor), this.autorService.listar());

        final LocalDateTime dataCriacao = autor.getDataCriacao();
        final Autor autorAlterado = this.criarAutor(id, "Joaquim Maria Machado de Assis");
        final Autor autorSalvo = this.autorService.alterar(autorAlterado);
        assertEquals("Joaquim Maria Machado de Assis", autorSalvo.getNome());
        assertEquals(dataCriacao, autorSalvo.getDataCriacao());
        assertEquals("Joaquim Maria Machado de Assis", this.autorService.obterPorId(id).getNome());

        this.autorService.excluir(id);
        assertEquals(List.of(), this.autorService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidosOuNomeDuplicado() {
        final Autor semNome = this.criarAutor(null, " ");
        final Autor machado = this.criarAutor(null, "Machado de Assis");
        this.autorService.incluir(machado);
        final Autor nomeDuplicado = this.criarAutor(null, "MACHADO DE ASSIS");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.autorService.incluir(null)),
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
        final Autor machado = this.criarAutor(null, "Machado de Assis");
        final Autor clarice = this.criarAutor(null, "Clarice Lispector");
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
        this.autorService.incluir(this.criarAutor(null, "Machado de Assis"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.autorService.listar().add(this.criarAutor(null, "Clarice Lispector")));
    }

    private Autor criarAutor(final Long id, final String nome) {
        final Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        autor.setNomeCatalogacao("Nome de catalogação");
        return autor;
    }

}
