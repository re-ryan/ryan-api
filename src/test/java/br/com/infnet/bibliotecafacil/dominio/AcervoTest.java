package br.com.infnet.bibliotecafacil.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AcervoTest {

    @Test
    public void deveRepresentarCamposDaV1() {
        final Biblioteca biblioteca = criarBiblioteca();
        final Livro livro = criarLivro();
        final Acervo acervo = biblioteca.adicionarLivro(3L, livro, 5, 2);

        assertEquals(3L, acervo.getId());
        assertSame(biblioteca, acervo.getBiblioteca());
        assertSame(livro, acervo.getLivro());
        assertEquals(5, acervo.getQuantidadeReal());
        assertEquals(2, acervo.getQuantidadeDisponivel());
        assertTrue(acervo.isAtivo());
        assertNotNull(acervo.getDataCriacao());
        assertEquals(acervo.getDataCriacao(), acervo.getDataAtualizacao());
    }

    @Test
    public void deveAceitarQuantidadeDisponivelIgualAZeroOuAQuantidadeReal() {
        final Biblioteca primeiraBiblioteca = criarBiblioteca();
        final Biblioteca segundaBiblioteca = criarBiblioteca(2L, "12345678000270");

        final Acervo indisponivel = primeiraBiblioteca.adicionarLivro(3L, criarLivro(), 5, 0);
        final Acervo totalmenteDisponivel = segundaBiblioteca.adicionarLivro(4L, criarLivro(), 5, 5);

        assertEquals(0, indisponivel.getQuantidadeDisponivel());
        assertEquals(5, totalmenteDisponivel.getQuantidadeDisponivel());
    }

    @Test
    public void naoDeveAceitarQuantidadeRealInvalida() {
        final Biblioteca biblioteca = criarBiblioteca();

        assertThrows(
                IllegalArgumentException.class,
                () -> biblioteca.adicionarLivro(3L, criarLivro(), 0, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> biblioteca.adicionarLivro(3L, criarLivro(), -1, 0));
    }

    @Test
    public void naoDeveAceitarQuantidadeDisponivelForaDoIntervalo() {
        final Biblioteca biblioteca = criarBiblioteca();

        assertThrows(
                IllegalArgumentException.class,
                () -> biblioteca.adicionarLivro(3L, criarLivro(), 5, -1));
        assertThrows(
                IllegalArgumentException.class,
                () -> biblioteca.adicionarLivro(3L, criarLivro(), 5, 6));
    }

    @Test
    public void deveReservarELiberarUnidadeAtualizandoAuditoria() {
        final Acervo acervo = criarBiblioteca().adicionarLivro(3L, criarLivro(), 3, 2);
        final LocalDateTime dataCriacao = acervo.getDataCriacao();

        acervo.reservarUnidade();

        assertEquals(1, acervo.getQuantidadeDisponivel());
        assertTrue(acervo.getDataAtualizacao().isAfter(dataCriacao));
        final LocalDateTime dataAposReserva = acervo.getDataAtualizacao();

        acervo.liberarUnidade();

        assertEquals(2, acervo.getQuantidadeDisponivel());
        assertTrue(acervo.getDataAtualizacao().isAfter(dataAposReserva));
    }

    @Test
    public void naoDeveReservarSemDisponibilidade() {
        final Acervo acervo = criarBiblioteca().adicionarLivro(3L, criarLivro(), 3, 0);

        assertFalse(acervo.temDisponibilidade());
        assertThrows(IllegalStateException.class, acervo::reservarUnidade);
    }

    @Test
    public void naoDeveLiberarAcimaDaQuantidadeReal() {
        final Acervo acervo = criarBiblioteca().adicionarLivro(3L, criarLivro(), 3);

        assertThrows(IllegalStateException.class, acervo::liberarUnidade);
    }

    @Test
    public void deveAlterarEstadoEDataDeAtualizacao() {
        final Acervo acervo = criarBiblioteca().adicionarLivro(3L, criarLivro(), 3);
        final LocalDateTime dataAnterior = acervo.getDataAtualizacao();

        acervo.desativar();

        assertFalse(acervo.isAtivo());
        assertFalse(acervo.temDisponibilidade());
        assertTrue(acervo.getDataAtualizacao().isAfter(dataAnterior));
        assertThrows(IllegalStateException.class, acervo::reservarUnidade);

        acervo.ativar();
        assertTrue(acervo.isAtivo());
        assertTrue(acervo.temDisponibilidade());
    }

    @Test
    public void bibliotecaInativaNaoDevePermitirReserva() {
        final Biblioteca biblioteca = criarBiblioteca();
        final Acervo acervo = biblioteca.adicionarLivro(3L, criarLivro(), 3);
        biblioteca.desativar();

        assertFalse(acervo.temDisponibilidade());
        assertThrows(IllegalStateException.class, acervo::reservarUnidade);
    }

    @Test
    public void livroInativoNaoDevePossuirDisponibilidadeParaReserva() {
        final Biblioteca biblioteca = criarBiblioteca();
        final Livro livro = criarLivro();
        final Acervo acervo = biblioteca.adicionarLivro(3L, livro, 3);
        livro.desativar();

        assertFalse(acervo.temDisponibilidade());
        assertThrows(IllegalStateException.class, acervo::reservarUnidade);
    }

    private Biblioteca criarBiblioteca() {
        return this.criarBiblioteca(1L, "12345678000199");
    }

    private Biblioteca criarBiblioteca(final Long id, final String cpfCnpj) {
        final Endereco endereco = new Endereco();
        endereco.setId(id);
        endereco.setCep("20040020");
        endereco.setLogradouro("Rua Principal");
        endereco.setNumero("10");
        endereco.setComplemento(null);
        endereco.setBairro("Centro");
        endereco.setCidade("Rio de Janeiro");
        endereco.setUf("RJ");
        endereco.setLatitude(null);
        endereco.setLongitude(null);

        final Biblioteca biblioteca = new Biblioteca();
        biblioteca.setId(id);
        biblioteca.setNome("Biblioteca " + id);
        biblioteca.setCpfCnpj(cpfCnpj);
        biblioteca.setEmail("biblioteca%s@teste.com".formatted(id));
        biblioteca.setTelefone(null);
        biblioteca.setEndereco(endereco);
        return biblioteca;
    }

    private Livro criarLivro() {
        final Livro livro = new Livro();
        livro.setId(2L);
        livro.setTitulo("Livro");
        livro.setIsbn(null, "9788532508126");
        return livro;
    }
}
