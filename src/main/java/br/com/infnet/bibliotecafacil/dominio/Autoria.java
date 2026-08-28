package br.com.infnet.bibliotecafacil.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Autoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "livro_id")
    private Livro livro;
    @ManyToOne(optional = false)
    @JoinColumn(name = "autor_id")
    private Autor autor;
    private int ordem;

    public void setLivro(final Livro livro) {
        if (livro == null) {
            throw new NullPointerException("O livro é obrigatório.");
        }
        this.livro = livro;
    }

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

    public Long getId() {
        return this.id;
    }

    @JsonIgnore
    public Livro getLivro() {
        return this.livro;
    }

    public int getOrdem() {
        return this.ordem;
    }

    @Override
    public String toString() {
        return "Autoria{autor='%s', ordem=%s}".formatted(this.autor.getNome(), this.ordem);
    }

}
