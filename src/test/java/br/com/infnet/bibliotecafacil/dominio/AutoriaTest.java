package br.com.infnet.bibliotecafacil.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AutoriaTest {

    @Test
    public void deveRelacionarAutorComOrdem() {
        final Autor autor = criarAutor();

        final Autoria autoria = criarAutoria(autor, 1);

        assertEquals(autor, autoria.getAutor());
        assertEquals(1, autoria.getOrdem());
    }

    @Test
    public void naoDeveAceitarOrdemInvalida() {
        final Autor autor = criarAutor();

        assertThrows(IllegalArgumentException.class, () -> criarAutoria(autor, 0));
        assertThrows(IllegalArgumentException.class, () -> criarAutoria(autor, -1));
    }

    @Test
    public void naoDeveAceitarAutorAusente() {
        assertThrows(NullPointerException.class, () -> criarAutoria(null, 1));
    }

    @Test
    public void deveApresentarAutorEOrdem() {
        final Autor autor = criarAutor();
        final Autoria autoria = criarAutoria(autor, 1);

        assertTrue(autoria.toString().contains("autor='Machado de Assis'"));
        assertTrue(autoria.toString().contains("ordem=1"));
    }
    private Autor criarAutor() {
        final Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Machado de Assis");
        autor.setNomeCatalogacao("ASSIS, Machado de");
        return autor;
    }

    private Autoria criarAutoria(final Autor autor, final int ordem) {
        final Autoria autoria = new Autoria();
        autoria.setAutor(autor);
        autoria.setOrdem(ordem);
        return autoria;
    }
}
