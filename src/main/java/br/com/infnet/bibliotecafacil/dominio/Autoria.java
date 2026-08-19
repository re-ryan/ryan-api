package br.com.infnet.bibliotecafacil.dominio;

public final class Autoria {

    private Autor autor;
    private int ordem;

    public void setAutor(final Autor autor) {
        if (autor == null) {
            throw new NullPointerException("O autor é obrigatório.");
        }
        this.autor = autor;
    }

    public void setOrdem(final int ordem) {
        if (ordem <= 0) {
            throw new IllegalArgumentException("A ordem de autoria deve ser positiva.");
        }
        this.ordem = ordem;
    }

    public Autor getAutor() {
        return this.autor;
    }

    public int getOrdem() {
        return this.ordem;
    }

    @Override
    public String toString() {
        return "Autoria{autor='%s', ordem=%s}".formatted(this.autor.getNome(), this.ordem);
    }

}
