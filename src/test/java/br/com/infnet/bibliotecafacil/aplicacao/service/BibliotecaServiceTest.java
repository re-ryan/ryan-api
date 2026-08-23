package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Biblioteca;
import java.util.List;
import org.junit.jupiter.api.Test;

class BibliotecaServiceTest {

    private final BibliotecaService bibliotecaService = new BibliotecaService();

    @Test
    public void deveExecutarOperacoesCrud() {
        final Biblioteca biblioteca = this.criarBiblioteca(1L, "Biblioteca Central", "12345678000199", "central@biblioteca.com");
        this.bibliotecaService.incluir(biblioteca);

        assertSame(biblioteca, this.bibliotecaService.obterPorId(1L));
        assertEquals(List.of(biblioteca), this.bibliotecaService.listar());

        final Biblioteca bibliotecaAlterada = this.criarBiblioteca(1L, "Biblioteca Central Renovada", "12345678000199", "central@biblioteca.com");
        this.bibliotecaService.alterar(bibliotecaAlterada);
        assertSame(bibliotecaAlterada, this.bibliotecaService.obterPorId(1L));

        this.bibliotecaService.excluir(1L);
        assertEquals(List.of(), this.bibliotecaService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidos() {
        final Biblioteca semId = this.criarBiblioteca(null, "Biblioteca Central", "12345678000199", "central@biblioteca.com");
        final Biblioteca semNome = this.criarBiblioteca(1L, " ", "12345678000199", "central@biblioteca.com");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.bibliotecaService.incluir(null)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.bibliotecaService.incluir(semId)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.bibliotecaService.incluir(semNome)));
    }

    @Test
    public void naoDeveAceitarDadosUnicosDuplicados() {
        this.bibliotecaService.incluir(this.criarBiblioteca(1L, "Biblioteca Central", "12345678000199", "central@biblioteca.com"));

        final Biblioteca nomeDuplicado = this.criarBiblioteca(2L, "BIBLIOTECA CENTRAL", "98765432000188", "outra@biblioteca.com");
        final Biblioteca documentoDuplicado = this.criarBiblioteca(3L, "Biblioteca Sul", "12345678000199", "sul@biblioteca.com");
        final Biblioteca emailDuplicado = this.criarBiblioteca(4L, "Biblioteca Norte", "11222333000144", "CENTRAL@BIBLIOTECA.COM");

        assertAll(
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.bibliotecaService.incluir(nomeDuplicado)),
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.bibliotecaService.incluir(documentoDuplicado)),
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.bibliotecaService.incluir(emailDuplicado)));
    }

    @Test
    public void deveTratarBibliotecaInexistente() {
        final Biblioteca biblioteca = this.criarBiblioteca(99L, "Biblioteca Central", "12345678000199", "central@biblioteca.com");

        assertAll(
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.bibliotecaService.obterPorId(99L)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.bibliotecaService.alterar(biblioteca)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.bibliotecaService.excluir(99L)));
    }

    @Test
    public void deveFiltrarBuscarOrdenarETransformarBibliotecas() {
        final Biblioteca central = this.criarBiblioteca(1L, "Biblioteca Central", "12345678000199", "central@biblioteca.com");
        final Biblioteca bairro = this.criarBiblioteca(2L, "Biblioteca do Bairro", "98765432000188", "bairro@biblioteca.com");
        central.desativar();
        this.bibliotecaService.incluir(central);
        this.bibliotecaService.incluir(bairro);

        assertAll(
                () -> assertEquals(List.of(bairro), this.bibliotecaService.listarAtivas()),
                () -> assertEquals(List.of(central), this.bibliotecaService.buscarPorNome("CENTRAL")),
                () -> assertEquals(List.of(central, bairro), this.bibliotecaService.listarOrdenadasPorNome()),
                () -> assertEquals(List.of("Biblioteca Central", "Biblioteca do Bairro"), this.bibliotecaService.listarNomes()));
    }

    @Test
    public void naoDeveExporAListaInterna() {
        this.bibliotecaService.incluir(this.criarBiblioteca(1L, "Biblioteca Central", "12345678000199", "central@biblioteca.com"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.bibliotecaService.listar().add(this.criarBiblioteca(2L, "Biblioteca do Bairro", "98765432000188", "bairro@biblioteca.com")));
    }

    private Biblioteca criarBiblioteca(final Long id, final String nome, final String cpfCnpj, final String email) {
        final Biblioteca biblioteca = new Biblioteca();
        biblioteca.setId(id);
        biblioteca.setNome(nome);
        biblioteca.setCpfCnpj(cpfCnpj);
        biblioteca.setEmail(email);
        return biblioteca;
    }

}
