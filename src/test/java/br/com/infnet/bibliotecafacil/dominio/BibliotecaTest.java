package br.com.infnet.bibliotecafacil.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BibliotecaTest {

    @Test
    public void deveRepresentarCamposDaV1() {
        final Endereco endereco = criarEndereco();
        final Biblioteca biblioteca = criarBiblioteca(
                1L,
                "Biblioteca Central",
                "12345678000199",
                "contato@biblioteca.com",
                "(21) 2222-3333",
                endereco);

        assertEquals(1L, biblioteca.getId());
        assertEquals("Biblioteca Central", biblioteca.getNome());
        assertEquals("12345678000199", biblioteca.getCpfCnpj());
        assertEquals("contato@biblioteca.com", biblioteca.getEmail());
        assertEquals("(21) 2222-3333", biblioteca.getTelefone());
        assertSame(endereco, biblioteca.getEndereco());
        assertTrue(biblioteca.isAtiva());
        assertNotNull(biblioteca.getDataCriacao());
        assertEquals(biblioteca.getDataCriacao(), biblioteca.getDataAtualizacao());
    }

    @Test
    public void deveRepresentarTelefoneAusenteComoNull() {
        final Biblioteca semTelefone = criarBiblioteca(
                1L,
                "Biblioteca Central",
                "12345678000199",
                "contato@biblioteca.com",
                null,
                criarEndereco());
        assertNull(semTelefone.getTelefone());
    }

    @Test
    public void deveAceitarCpfComOnzeNumeros() {
        final Biblioteca biblioteca = criarBiblioteca(
                1L,
                "Biblioteca Comunitária",
                "12345678901",
                "contato@biblioteca.com",
                null,
                criarEndereco());

        assertEquals("12345678901", biblioteca.getCpfCnpj());
    }

    @Test
    public void deveManterVariosLivrosNoAcervoComQuantidadesIndependentes() {
        final Biblioteca biblioteca = criarBiblioteca();
        final Livro primeiroLivro = criarLivro(2L, "Primeiro livro", "9788532508102");
        final Livro segundoLivro = criarLivro(3L, "Segundo livro", "9788532508119");

        final Acervo primeiroAcervo = biblioteca.adicionarLivro(4L, primeiroLivro, 3, 1);
        biblioteca.adicionarLivro(5L, segundoLivro, 2);

        assertEquals(2, biblioteca.getAcervos().size());
        assertEquals(3, primeiroAcervo.getQuantidadeReal());
        assertEquals(1, primeiroAcervo.getQuantidadeDisponivel());
        assertEquals(2, biblioteca.localizarAcervo(segundoLivro).orElseThrow().getQuantidadeDisponivel());
    }

    @Test
    public void naoDeveAdicionarDuasVezesOLivroComMesmoIdentificador() {
        final Biblioteca biblioteca = criarBiblioteca();
        final Livro primeiroObjeto = criarLivro(2L, "Livro", "9788532508126");
        final Livro segundoObjeto = criarLivro(2L, "Outro título", "9788532508133");
        biblioteca.adicionarLivro(3L, primeiroObjeto, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> biblioteca.adicionarLivro(4L, segundoObjeto, 1));
    }

    @Test
    public void naoDeveAdicionarDoisAcervosComMesmoIdentificador() {
        final Biblioteca biblioteca = criarBiblioteca();
        biblioteca.adicionarLivro(3L, criarLivro(2L, "Primeiro livro", "9788532508126"), 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> biblioteca.adicionarLivro(
                        3L,
                        criarLivro(4L, "Segundo livro", "9788532508133"),
                        1));
    }

    @Test
    public void bibliotecaInativaNaoDeveAlterarAcervo() {
        final Biblioteca biblioteca = criarBiblioteca();
        final Livro livro = criarLivro(2L, "Livro", "9788532508126");
        biblioteca.desativar();

        assertThrows(IllegalStateException.class, () -> biblioteca.adicionarLivro(3L, livro, 1));
    }

    @Test
    public void deveAlterarEstadoEDataDeAtualizacao() {
        final Biblioteca biblioteca = criarBiblioteca();
        final LocalDateTime dataAnterior = biblioteca.getDataAtualizacao();

        biblioteca.desativar();

        assertFalse(biblioteca.isAtiva());
        assertTrue(biblioteca.getDataAtualizacao().isAfter(dataAnterior));

        biblioteca.ativar();
        assertTrue(biblioteca.isAtiva());
    }

    @Test
    public void deveAtualizarAuditoriaAoAdicionarLivro() {
        final Biblioteca biblioteca = criarBiblioteca();
        final LocalDateTime dataAnterior = biblioteca.getDataAtualizacao();

        biblioteca.adicionarLivro(3L, criarLivro(2L, "Livro", "9788532508126"), 1);

        assertTrue(biblioteca.getDataAtualizacao().isAfter(dataAnterior));
    }

    @Test
    public void deveExporColecaoImutavelDeAcervos() {
        final Biblioteca biblioteca = criarBiblioteca();
        biblioteca.adicionarLivro(3L, criarLivro(2L, "Livro", "9788532508126"), 1);

        assertThrows(UnsupportedOperationException.class, () -> biblioteca.getAcervos().clear());
    }

    private Biblioteca criarBiblioteca() {
        return criarBiblioteca(
                1L,
                "Biblioteca Central",
                "12345678000199",
                "contato@biblioteca.com",
                null,
                criarEndereco());
    }

    private Endereco criarEndereco() {
        final Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("20040020");
        endereco.setLogradouro("Rua Principal");
        endereco.setNumero("10");
        endereco.setComplemento(null);
        endereco.setBairro("Centro");
        endereco.setCidade("Rio de Janeiro");
        endereco.setUf("RJ");
        endereco.setLatitude(-22.9);
        endereco.setLongitude(-43.2);
        return endereco;
    }

    private Livro criarLivro(final Long id, final String titulo, final String isbn13) {
        final Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setIsbn(null, isbn13);
        return livro;
    }

    private Biblioteca criarBiblioteca(final Long id, final String nome, final String cpfCnpj, final String email, final String telefone, final Endereco endereco) {
        final Biblioteca biblioteca = new Biblioteca();
        biblioteca.setId(id);
        biblioteca.setNome(nome);
        biblioteca.setCpfCnpj(cpfCnpj);
        biblioteca.setEmail(email);
        biblioteca.setTelefone(telefone);
        biblioteca.setEndereco(endereco);
        return biblioteca;
    }
}
