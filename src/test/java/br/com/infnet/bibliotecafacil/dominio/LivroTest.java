package br.com.infnet.bibliotecafacil.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class LivroTest {

    @Test
    public void deveCriarLivroComDadosObrigatoriosDaV1() {
        final Livro livro = criarLivro(1L, "Dom Casmurro", null, "978-85-325-0812-6");

        assertEquals(1L, livro.getId());
        assertEquals("Dom Casmurro", livro.getTitulo());
        assertNull(livro.getIsbn10());
        assertEquals("9788532508126", livro.getIsbn13());
        assertNull(livro.getEditora());
        assertNull(livro.getAnoPublicacao());
        assertNull(livro.getEdicao());
        assertNull(livro.getDescricao());
        assertNull(livro.getUrlImagemCapa());
        assertTrue(livro.isAtivo());
        assertNotNull(livro.getDataCriacao());
        assertEquals(livro.getDataCriacao(), livro.getDataAtualizacao());
    }

    @Test
    public void deveInformarCamposOpcionaisDaV1() {
        final Livro livro = criarLivro();
        livro.setEditora("Companhia das Letras");
        livro.setAnoPublicacao(1899);
        livro.setEdicao("1ª edição");
        livro.setDescricao("Romance brasileiro.");
        livro.setUrlImagemCapa("https://exemplo.com/dom-casmurro.jpg");

        assertEquals("Companhia das Letras", livro.getEditora());
        assertEquals(1899, livro.getAnoPublicacao());
        assertEquals("1ª edição", livro.getEdicao());
        assertEquals("Romance brasileiro.", livro.getDescricao());
        assertEquals("https://exemplo.com/dom-casmurro.jpg", livro.getUrlImagemCapa());
    }

    @Test
    public void deveRepresentarAusenciaDosCamposOpcionaisComNulo() {
        final Livro livro = criarLivro();

        livro.setEditora(null);
        livro.setAnoPublicacao(null);
        livro.setEdicao(null);
        livro.setDescricao(null);
        livro.setUrlImagemCapa(null);

        assertNull(livro.getEditora());
        assertNull(livro.getAnoPublicacao());
        assertNull(livro.getEdicao());
        assertNull(livro.getDescricao());
        assertNull(livro.getUrlImagemCapa());
    }

    @Test
    public void deveAlterarEstadoEDataAtualizacao() {
        final Livro livro = criarLivro();
        final LocalDateTime dataCriacao = livro.getDataCriacao();

        livro.desativar();
        final LocalDateTime dataDesativacao = livro.getDataAtualizacao();
        livro.ativar();

        assertFalse(dataDesativacao.equals(dataCriacao));
        assertTrue(livro.isAtivo());
        assertTrue(livro.getDataAtualizacao().isAfter(dataDesativacao));
        assertEquals(dataCriacao, livro.getDataCriacao());
    }

    @Test
    public void deveRelacionarAutorAoLivro() {
        final Livro livro = criarLivro();
        final Autor autor = criarAutor(2L, "Machado de Assis", "ASSIS, Machado de");
        final LocalDateTime dataAnterior = livro.getDataAtualizacao();

        livro.adicionarAutor(autor, 1);

        assertEquals(1, livro.getAutorias().size());
        assertEquals(autor.getId(), livro.getAutorias().getFirst().getAutor().getId());
        assertEquals(1, livro.getAutorias().getFirst().getOrdem());
        assertTrue(livro.getDataAtualizacao().isAfter(dataAnterior));
    }

    @Test
    public void naoDeveRelacionarMesmoAutorDuasVezes() {
        final Livro livro = criarLivro();
        final Autor autor = criarAutor(2L, "Machado de Assis", "ASSIS, Machado de");
        livro.adicionarAutor(autor, 1);

        final Autor autorComMesmoId = criarAutor(2L, "M. de Assis", "ASSIS, M. de");
        assertThrows(
                IllegalArgumentException.class,
                () -> livro.adicionarAutor(autorComMesmoId, 2));
    }

    @Test
    public void naoDeveRelacionarMesmaOrdemDeAutoriaDuasVezes() {
        final Livro livro = criarLivro();
        final Autor primeiroAutor = criarAutor(1L, "Machado de Assis", "ASSIS, Machado de");
        final Autor segundoAutor = criarAutor(2L, "José de Alencar", "ALENCAR, José de");
        livro.adicionarAutor(primeiroAutor, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> livro.adicionarAutor(segundoAutor, 1));
    }

    @Test
    public void deveOrdenarAutoriasEProtegerColecao() {
        final Livro livro = criarLivro();
        final Autor primeiroAutor = criarAutor(1L, "Machado de Assis", "ASSIS, Machado de");
        final Autor segundoAutor = criarAutor(2L, "José de Alencar", "ALENCAR, José de");

        livro.adicionarAutor(segundoAutor, 2);
        livro.adicionarAutor(primeiroAutor, 1);

        assertEquals(primeiroAutor, livro.getAutorias().getFirst().getAutor());
        assertEquals(segundoAutor, livro.getAutorias().getLast().getAutor());
        assertThrows(
                UnsupportedOperationException.class,
                () -> livro.getAutorias().add(criarAutoria(primeiroAutor, 3)));
    }

    @Test
    public void deveRelacionarVariasCategoriasAoLivro() {
        final Livro livro = criarLivro();
        final Categoria romance = criarCategoria(1L, "Romance");
        final Categoria literaturaBrasileira = criarCategoria(2L, "Literatura brasileira");
        final LocalDateTime dataAnterior = livro.getDataAtualizacao();

        livro.adicionarCategoria(romance);
        livro.adicionarCategoria(literaturaBrasileira);

        assertEquals(2, livro.getCategorias().size());
        assertEquals(romance.getId(), livro.getCategorias().getFirst().getId());
        assertEquals(literaturaBrasileira.getId(), livro.getCategorias().getLast().getId());
        assertTrue(livro.getDataAtualizacao().isAfter(dataAnterior));
        assertThrows(
                UnsupportedOperationException.class,
                () -> livro.getCategorias().add(criarCategoria(3L, "Ficção")));
    }

    @Test
    public void naoDeveRelacionarMesmaCategoriaDuasVezes() {
        final Livro livro = criarLivro();
        final Categoria categoria = criarCategoria(1L, "Romance");
        livro.adicionarCategoria(categoria);

        final Categoria categoriaComMesmoId = criarCategoria(1L, "Literatura");
        assertThrows(
                IllegalArgumentException.class,
                () -> livro.adicionarCategoria(categoriaComMesmoId));
    }

    @Test
    public void deveNormalizarIsbn13() {
        final Livro livro = criarLivro(1L, "Dom Casmurro", null, "978-85-325-0812-6");

        assertEquals("9788532508126", livro.getIsbn13());
        assertNull(livro.getIsbn10());
    }

    @Test
    public void deveConverterIsbn10ParaIsbn13() {
        final Livro livro = criarLivro(1L, "Dom Casmurro", "85-325-0812-x", null);

        assertEquals("853250812X", livro.getIsbn10());
        assertEquals("9788532508126", livro.getIsbn13());
    }

    @Test
    public void deveValidarIsbnsEquivalentesInformados() {
        final Livro livro = criarLivro(
                1L, "Dom Casmurro", "85-325-0812-X", "978-85-325-0812-6");

        assertEquals("853250812X", livro.getIsbn10());
        assertEquals("9788532508126", livro.getIsbn13());
    }

    @Test
    public void naoDeveAceitarIsbn13Invalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> criarLivro(1L, "Dom Casmurro", null, "123"));
        assertThrows(
                IllegalArgumentException.class,
                () -> criarLivro(1L, "Dom Casmurro", null, "9788532508127"));
        assertThrows(
                IllegalArgumentException.class,
                () -> criarLivro(1L, "Dom Casmurro", null, "4006381333931"));
    }

    @Test
    public void naoDeveAceitarIsbn10InvalidoOuIsbnsIncompativeis() {
        assertThrows(
                IllegalArgumentException.class,
                () -> criarLivro(1L, "Dom Casmurro", "8532508120", null));
        assertThrows(
                IllegalArgumentException.class,
                () -> criarLivro(1L, "Dom Casmurro", "853250812X", "9788532508133"));
    }

    @Test
    public void naoDeveCriarLivroSemIsbn() {
        assertThrows(
                IllegalArgumentException.class,
                () -> criarLivro(1L, "Dom Casmurro", null, null));
    }

    @Test
    public void deveAceitarIsbn13ComPrefixo979SemIsbn10() {
        final Livro livro = criarLivro(1L, "Dom Casmurro", null, "9791234567896");

        assertEquals("9791234567896", livro.getIsbn13());
        assertNull(livro.getIsbn10());
    }

    @Test
    public void deveApresentarDadosDaV1() {
        final Livro livro = criarLivro();
        livro.setEditora("Companhia das Letras");
        livro.setAnoPublicacao(1899);

        final String apresentacao = livro.toString();

        assertTrue(apresentacao.contains("titulo='Dom Casmurro'"));
        assertTrue(apresentacao.contains("editora='Companhia das Letras'"));
        assertTrue(apresentacao.contains("anoPublicacao=1899"));
        assertTrue(apresentacao.contains("ativo=true"));
    }

    private Livro criarLivro() {
        return this.criarLivro(1L, "Dom Casmurro", null, "9788532508126");
    }

    private Livro criarLivro(final Long id, final String titulo, final String isbn10, final String isbn13) {
        final Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setIsbn(isbn10, isbn13);
        return livro;
    }

    private Autor criarAutor(final Long id, final String nome, final String nomeCatalogacao) {
        final Autor autor = new Autor();
        autor.setId(id);
        autor.setNome(nome);
        autor.setNomeCatalogacao(nomeCatalogacao);
        return autor;
    }

    private Categoria criarCategoria(final Long id, final String nome) {
        final Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        categoria.setDescricao(null);
        return categoria;
    }

    private Autoria criarAutoria(final Autor autor, final int ordem) {
        final Autoria autoria = new Autoria();
        autoria.setAutor(autor);
        autoria.setOrdem(ordem);
        return autoria;
    }
}
