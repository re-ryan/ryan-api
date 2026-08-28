package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String nomeCatalogacao;
    private boolean ativo = true;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataAtualizacao = this.dataCriacao;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public void setNomeCatalogacao(final String nomeCatalogacao) {
        this.nomeCatalogacao = nomeCatalogacao;
    }

    public void atualizarDados(final String nome, final String nomeCatalogacao) {
        this.nome = nome;
        this.nomeCatalogacao = nomeCatalogacao;
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

    public String getNome() {
        return this.nome;
    }

    public String getNomeCatalogacao() {
        return this.nomeCatalogacao;
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
        return ("Autor{id=%s, nome='%s', nomeCatalogacao='%s', ativo=%s, "
                + "dataCriacao=%s, dataAtualizacao=%s}")
                .formatted(this.id, this.nome, this.nomeCatalogacao, this.ativo,
                        this.dataCriacao, this.dataAtualizacao);
    }

    private void atualizarDataAtualizacao() {
        final LocalDateTime agora = LocalDateTime.now();
        this.dataAtualizacao = agora.isAfter(this.dataAtualizacao)
                ? agora
                : this.dataAtualizacao.plusNanos(1);
    }

}
