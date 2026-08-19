package br.com.infnet.bibliotecafacil.dominio;

import java.time.LocalDateTime;

public final class Categoria {

    private Long id;
    private String nome;
    private String descricao;
    private boolean ativa = true;
    private final LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataAtualizacao = this.dataCriacao;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public void ativar() {
        this.ativa = true;
        this.atualizarDataAtualizacao();
    }

    public void desativar() {
        this.ativa = false;
        this.atualizarDataAtualizacao();
    }

    public Long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public boolean isAtiva() {
        return this.ativa;
    }

    public LocalDateTime getDataCriacao() {
        return this.dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return this.dataAtualizacao;
    }

    @Override
    public String toString() {
        return ("Categoria{id=%s, nome='%s', descricao='%s', ativa=%s, "
                + "dataCriacao=%s, dataAtualizacao=%s}")
                .formatted(this.id, this.nome, this.descricao, this.ativa,
                        this.dataCriacao, this.dataAtualizacao);
    }

    private void atualizarDataAtualizacao() {
        final LocalDateTime agora = LocalDateTime.now();
        this.dataAtualizacao = agora.isAfter(this.dataAtualizacao)
                ? agora
                : this.dataAtualizacao.plusNanos(1);
    }

}
