package br.com.infnet.bibliotecafacil.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Acervo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "biblioteca_id")
    private Biblioteca biblioteca;
    @ManyToOne(optional = false)
    @JoinColumn(name = "livro_id")
    private Livro livro;
    private int quantidadeReal;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private int quantidadeDisponivel;
    private boolean ativo = true;
    private LocalDateTime dataAtualizacao = this.dataCriacao;

    public void setId(final Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O identificador de acervo deve ser positivo.");
        }
        this.id = id;
    }

    public void setBiblioteca(final Biblioteca biblioteca) {
        if (biblioteca == null) {
            throw new NullPointerException("A biblioteca é obrigatória.");
        }
        this.biblioteca = biblioteca;
    }

    public void setLivro(final Livro livro) {
        if (livro == null) {
            throw new NullPointerException("O livro é obrigatório.");
        }
        this.livro = livro;
    }

    public void setQuantidadeReal(final int quantidadeReal) {
        if (quantidadeReal <= 0) {
            throw new IllegalArgumentException("A quantidade real do acervo deve ser positiva.");
        }
        if (this.quantidadeDisponivel > quantidadeReal) {
            throw new IllegalArgumentException("A quantidade disponível não pode superar a quantidade real.");
        }
        this.quantidadeReal = quantidadeReal;
    }

    public void setQuantidadeDisponivel(final int quantidadeDisponivel) {
        if (quantidadeDisponivel < 0 || quantidadeDisponivel > this.quantidadeReal) {
            throw new IllegalArgumentException("A quantidade disponível deve estar entre zero e a quantidade real.");
        }
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public boolean temDisponibilidade() {
        return this.ativo
                && this.biblioteca.isAtiva()
                && this.livro.isAtivo()
                && this.quantidadeDisponivel > 0;
    }

    protected void reservarUnidade() {
        this.validarBibliotecaAtiva();
        this.validarAcervoAtivo();
        this.validarLivroAtivo();
        if (!this.temDisponibilidade()) {
            throw new IllegalStateException("Não há unidade disponível para reserva.");
        }
        this.quantidadeDisponivel--;
        this.atualizarDataAtualizacao();
    }

    protected void liberarUnidade() {
        if (this.quantidadeDisponivel >= this.quantidadeReal) {
            throw new IllegalStateException("Todas as unidades do acervo já estão disponíveis.");
        }
        this.quantidadeDisponivel++;
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

    public Long getId() {
        return this.id;
    }

    @JsonIgnore
    public Biblioteca getBiblioteca() {
        return this.biblioteca;
    }

    public Livro getLivro() {
        return this.livro;
    }

    public int getQuantidadeReal() {
        return this.quantidadeReal;
    }

    public int getQuantidadeDisponivel() {
        return this.quantidadeDisponivel;
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

    @Override
    public String toString() {
        return ("Acervo{id=%s, biblioteca='%s', livro='%s', quantidadeReal=%s, "
                + "quantidadeDisponivel=%s, ativo=%s, dataCriacao=%s, dataAtualizacao=%s}")
                .formatted(this.id,
                        this.biblioteca.getNome(),
                        this.livro.getTitulo(),
                        this.quantidadeReal,
                        this.quantidadeDisponivel,
                        this.ativo,
                        this.dataCriacao,
                        this.dataAtualizacao);
    }

    private void validarBibliotecaAtiva() {
        if (!this.biblioteca.isAtiva()) {
            throw new IllegalStateException("A biblioteca precisa estar ativa para realizar esta operação.");
        }
    }

    private void validarAcervoAtivo() {
        if (!this.ativo) {
            throw new IllegalStateException("O acervo precisa estar ativo para realizar esta operação.");
        }
    }

    private void validarLivroAtivo() {
        if (!this.livro.isAtivo()) {
            throw new IllegalStateException("O livro precisa estar ativo para realizar esta operação.");
        }
    }

    private void atualizarDataAtualizacao() {
        final LocalDateTime agora = LocalDateTime.now();
        this.dataAtualizacao = agora.isAfter(this.dataAtualizacao) ? agora : this.dataAtualizacao.plusNanos(1);
    }

}
