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

    void setLivro(final Livro livro) {
        this.livro = livro;
    }

    void setAutor(final Autor autor) {
        this.autor = autor;
    }

    void setOrdem(final int ordem) {
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
