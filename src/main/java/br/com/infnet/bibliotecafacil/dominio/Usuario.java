package br.com.infnet.bibliotecafacil.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Usuario {

    private Long id;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private String login;
    private String email;
    private String senhaHash;
    private TipoUsuario tipoUsuario;
    private final LocalDateTime dataCriacao = LocalDateTime.now();
    private boolean ativo = true;
    private LocalDateTime dataAtualizacao = this.dataCriacao;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNomeCompleto(final String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public void setDataNascimento(final LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setSenhaHash(final String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public void setTipoUsuario(final TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new NullPointerException("O tipo de usuário é obrigatório.");
        }
        this.tipoUsuario = tipoUsuario;
    }

    public void ativar() {
        this.ativo = true;
        this.atualizarDataAtualizacao();
    }

    public void desativar() {
        this.ativo = false;
        this.atualizarDataAtualizacao();
    }

    protected final void validarUsuarioAtivo() {
        if (!this.ativo) {
            throw new IllegalStateException("O usuário precisa estar ativo para realizar esta operação.");
        }
    }

    public Long getId() {
        return this.id;
    }

    public String getNomeCompleto() {
        return this.nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return this.dataNascimento;
    }

    public String getLogin() {
        return this.login;
    }

    public String getEmail() {
        return this.email;
    }

    @JsonIgnore
    public String getSenhaHash() {
        return this.senhaHash;
    }

    public TipoUsuario getTipoUsuario() {
        return this.tipoUsuario;
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

    protected final String descreverUsuario() {
        return ("id=%s, nomeCompleto='%s', dataNascimento=%s, login='%s', email='%s', "
                + "tipoUsuario=%s, ativo=%s, dataCriacao=%s, dataAtualizacao=%s")
                .formatted(this.id, this.nomeCompleto, this.dataNascimento, this.login, this.email,
                        this.getTipoUsuario(), this.ativo, this.dataCriacao, this.dataAtualizacao);
    }

    @Override
    public String toString() {
        return "%s{%s}".formatted(this.getClass().getSimpleName(), this.descreverUsuario());
    }

    private void atualizarDataAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }

}
