package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataAtualizacao = this.dataCriacao;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setCep(final String cep) {
        this.cep = cep;
    }

    public void setLogradouro(final String logradouro) {
        this.logradouro = logradouro;
    }

    public void setNumero(final String numero) {
        this.numero = numero;
    }

    public void setComplemento(final String complemento) {
        this.complemento = complemento;
    }

    public void setBairro(final String bairro) {
        this.bairro = bairro;
    }

    public void setCidade(final String cidade) {
        this.cidade = cidade;
    }

    public void setUf(final String uf) {
        this.uf = uf;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    public Long getId() {
        return this.id;
    }

    public String getCep() {
        return this.cep;
    }

    public String getLogradouro() {
        return this.logradouro;
    }

    public String getNumero() {
        return this.numero;
    }

    public String getComplemento() {
        return this.complemento;
    }

    public String getBairro() {
        return this.bairro;
    }

    public String getCidade() {
        return this.cidade;
    }

    public String getUf() {
        return this.uf;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public LocalDateTime getDataCriacao() {
        return this.dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return this.dataAtualizacao;
    }

    @Override
    public String toString() {
        return ("Endereco{id=%s, cep='%s', logradouro='%s', numero='%s', complemento='%s', "
                + "bairro='%s', cidade='%s', uf='%s', latitude=%s, longitude=%s, "
                + "dataCriacao=%s, dataAtualizacao=%s}")
                .formatted(
                        this.id,
                        this.cep,
                        this.logradouro,
                        this.numero,
                        this.complemento,
                        this.bairro,
                        this.cidade,
                        this.uf,
                        this.latitude,
                        this.longitude,
                        this.dataCriacao,
                        this.dataAtualizacao);
    }

}
