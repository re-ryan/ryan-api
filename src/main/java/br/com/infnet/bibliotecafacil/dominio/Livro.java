package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Entity
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String titulo;
    @Column(unique = true)
    private String isbn10;
    @Column(nullable = false, unique = true)
    private String isbn13;
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<Autoria> autorias = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "livro_categoria",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private List<Categoria> categorias = new ArrayList<>();
    private String editora;
    private Integer anoPublicacao;
    private String edicao;
    private String descricao;
    private String urlImagemCapa;
    private boolean ativo = true;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataAtualizacao = this.dataCriacao;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public void setIsbn(final String isbn10, final String isbn13) {
        this.validarIsbn(isbn10, isbn13);
        this.isbn10 = this.possuiValor(isbn10) ? this.normalizarIsbn(isbn10) : null;
        this.isbn13 = this.possuiValor(isbn13)
                ? this.normalizarIsbn(isbn13)
                : this.converterIsbn10ParaIsbn13(this.isbn10);
    }

    public void setEditora(final String editora) {
        this.editora = editora;
    }

    public void setAnoPublicacao(final Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public void setEdicao(final String edicao) {
        this.edicao = edicao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public void setUrlImagemCapa(final String urlImagemCapa) {
        this.urlImagemCapa = urlImagemCapa;
    }

    public void atualizarDados(final Livro livro) {
        this.titulo = livro.titulo;
        this.isbn10 = livro.isbn10;
        this.isbn13 = livro.isbn13;
        this.editora = livro.editora;
        this.anoPublicacao = livro.anoPublicacao;
        this.edicao = livro.edicao;
        this.descricao = livro.descricao;
        this.urlImagemCapa = livro.urlImagemCapa;
        this.atualizarDataAtualizacao();
    }

    public void ativar() {
        this.ativo = true;
        this.atualizarDataAtualizacao();
    }

    public void desativar() {
        this.ativo = false;
        this.atualizarDataAtualizacao();
    }

    public void adicionarAutor(final Autor autor, final int ordem) {
        if (autor == null) {
            throw new NullPointerException("O autor é obrigatório.");
        }
        if (this.contemAutor(autor)) {
            throw new IllegalArgumentException("O autor já está relacionado ao livro.");
        }
        if (this.contemOrdemAutoria(ordem)) {
            throw new IllegalArgumentException("A ordem de autoria já está relacionada ao livro.");
        }
        final Autoria autoria = new Autoria();
        autoria.setLivro(this);
        autoria.setAutor(autor);
        autoria.setOrdem(ordem);
        this.autorias.add(autoria);
        this.autorias.sort(Comparator.comparingInt(Autoria::getOrdem));
        this.atualizarDataAtualizacao();
    }

    public void adicionarCategoria(final Categoria categoria) {
        if (categoria == null) {
            throw new NullPointerException("A categoria é obrigatória.");
        }
        if (this.contemCategoria(categoria)) {
            throw new IllegalArgumentException("A categoria já está relacionada ao livro.");
        }
        this.categorias.add(categoria);
        this.atualizarDataAtualizacao();
    }

    public Long getId() {
        return this.id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getIsbn10() {
        return this.isbn10;
    }

    public String getIsbn13() {
        return this.isbn13;
    }

    public String getEditora() {
        return this.editora;
    }

    public Integer getAnoPublicacao() {
        return this.anoPublicacao;
    }

    public String getEdicao() {
        return this.edicao;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String getUrlImagemCapa() {
        return this.urlImagemCapa;
    }

    public boolean isAtivo() {
        return this.ativo;
    }

    public LocalDateTime getDataCriacao() {
        return this.dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return this.dataAtualizacao;
    }

    public List<Autoria> getAutorias() {
        return List.copyOf(this.autorias);
    }

    public List<Categoria> getCategorias() {
        return List.copyOf(this.categorias);
    }

    @Override
    public String toString() {
        final List<String> nomesDosAutores = new ArrayList<>();
        for (final Autoria autoria : this.autorias) {
            nomesDosAutores.add(autoria.getAutor().getNome());
        }
        final List<String> nomesDasCategorias = new ArrayList<>();
        for (final Categoria categoria : this.categorias) {
            nomesDasCategorias.add(categoria.getNome());
        }

        return ("Livro{id=%s, titulo='%s', isbn10='%s', isbn13='%s', editora='%s', "
                + "anoPublicacao=%s, edicao='%s', descricao='%s', urlImagemCapa='%s', ativo=%s, "
                + "dataCriacao=%s, dataAtualizacao=%s, autores=%s, categorias=%s}")
                .formatted(this.id, this.titulo, this.isbn10, this.isbn13,
                        this.editora, this.anoPublicacao, this.edicao, this.descricao, this.urlImagemCapa,
                        this.ativo, this.dataCriacao, this.dataAtualizacao,
                        nomesDosAutores, nomesDasCategorias);
    }

    private void atualizarDataAtualizacao() {
        final LocalDateTime agora = LocalDateTime.now();
        this.dataAtualizacao = agora.isAfter(this.dataAtualizacao)
                ? agora
                : this.dataAtualizacao.plusNanos(1);
    }

    private boolean contemAutor(final Autor autor) {
        for (final Autoria autoria : this.autorias) {
            if (autoria.getAutor().getId().equals(autor.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean contemOrdemAutoria(final int ordem) {
        for (final Autoria autoria : this.autorias) {
            if (autoria.getOrdem() == ordem) {
                return true;
            }
        }
        return false;
    }

    private boolean contemCategoria(final Categoria categoria) {
        for (final Categoria categoriaAtual : this.categorias) {
            if (categoriaAtual.getId().equals(categoria.getId())) {
                return true;
            }
        }
        return false;
    }

    private void validarIsbn(final String isbn10, final String isbn13) {
        final boolean temIsbn10 = this.possuiValor(isbn10);
        final boolean temIsbn13 = this.possuiValor(isbn13);
        if (!temIsbn10 && !temIsbn13) {
            throw new IllegalArgumentException("O ISBN-13 ou o ISBN-10 deve ser informado.");
        }

        final String isbn10Normalizado = temIsbn10 ? this.normalizarIsbn(isbn10) : null;
        final String isbn13Normalizado = temIsbn13 ? this.normalizarIsbn(isbn13) : null;
        if (temIsbn10 && (!isbn10Normalizado.matches("\\d{9}[\\dX]")
                || this.calcularSomaPonderadaIsbn10(isbn10Normalizado) % 11 != 0)) {
            throw new IllegalArgumentException("O ISBN-10 informado é inválido.");
        }
        if (temIsbn13) {
            if (!isbn13Normalizado.matches("97[89]\\d{10}")) {
                throw new IllegalArgumentException("O ISBN-13 informado é inválido.");
            }
            final int digitoEsperado = this.calcularDigitoVerificadorIsbn13(isbn13Normalizado.substring(0, 12));
            final int digitoInformado = Character.digit(isbn13Normalizado.charAt(12), 10);
            if (digitoInformado != digitoEsperado) {
                throw new IllegalArgumentException("O ISBN-13 informado é inválido.");
            }
        }
        if (temIsbn10 && temIsbn13
                && !this.converterIsbn10ParaIsbn13(isbn10Normalizado).equals(isbn13Normalizado)) {
            throw new IllegalArgumentException("O ISBN-10 e o ISBN-13 informados não são equivalentes.");
        }
    }

    private boolean possuiValor(final String isbn) {
        return isbn != null && !isbn.isBlank();
    }

    private String normalizarIsbn(final String isbn) {
        return isbn.replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
    }

    private String converterIsbn10ParaIsbn13(final String isbn10) {
        final String baseIsbn13 = "978" + isbn10.substring(0, 9);
        return baseIsbn13 + this.calcularDigitoVerificadorIsbn13(baseIsbn13);
    }

    private int calcularDigitoVerificadorIsbn13(final String baseIsbn13) {
        int soma = 0;
        for (int indice = 0; indice < 12; indice++) {
            final int digito = Character.digit(baseIsbn13.charAt(indice), 10);
            final int peso = indice % 2 == 0 ? 1 : 3;
            soma += digito * peso;
        }
        return (10 - soma % 10) % 10;
    }

    private int calcularSomaPonderadaIsbn10(final String isbn10) {
        int soma = 0;
        for (int indice = 0; indice < 10; indice++) {
            final char caractere = isbn10.charAt(indice);
            final int digito = caractere == 'X' ? 10 : Character.digit(caractere, 10);
            soma += digito * (10 - indice);
        }
        return soma;
    }

}
