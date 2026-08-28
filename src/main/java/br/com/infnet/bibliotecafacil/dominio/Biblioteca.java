package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Biblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Endereco endereco;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Acervo> acervos = new ArrayList<>();
    private boolean ativa = true;
    private LocalDateTime dataAtualizacao = this.dataCriacao;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public void setCpfCnpj(final String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setTelefone(final String telefone) {
        this.telefone = telefone;
    }

    public void setEndereco(final Endereco endereco) {
        if (endereco == null) {
            throw new NullPointerException("O endereço é obrigatório.");
        }
        this.endereco = endereco;
    }

    public void atualizarDados(final Biblioteca biblioteca) {
        this.nome = biblioteca.nome;
        this.cpfCnpj = biblioteca.cpfCnpj;
        this.email = biblioteca.email;
        this.telefone = biblioteca.telefone;
        this.endereco = biblioteca.endereco;
        this.atualizarDataAtualizacao();
    }

    public Acervo adicionarLivro(final Long idAcervo, final Livro livro, final int quantidadeReal) {
        return this.adicionarLivro(idAcervo, livro, quantidadeReal, quantidadeReal);
    }

    public Acervo adicionarLivro(final Long idAcervo, final Livro livro, final int quantidadeReal, final int quantidadeDisponivel) {
        this.validarBibliotecaAtiva();
        if (livro == null) {
            throw new NullPointerException("O livro é obrigatório.");
        }
        if (idAcervo != null && this.contemAcervoComId(idAcervo)) {
            throw new IllegalArgumentException("O identificador do acervo já está em uso nesta biblioteca.");
        }
        if (this.localizarAcervo(livro).isPresent()) {
            throw new IllegalArgumentException("O livro já pertence ao acervo desta biblioteca.");
        }

        final Acervo acervo = new Acervo();
        if (idAcervo != null) {
            acervo.setId(idAcervo);
        }
        acervo.setBiblioteca(this);
        acervo.setLivro(livro);
        acervo.setQuantidadeReal(quantidadeReal);
        acervo.setQuantidadeDisponivel(quantidadeDisponivel);
        this.acervos.add(acervo);
        this.atualizarDataAtualizacao();
        return acervo;
    }

    public Optional<Acervo> localizarAcervo(final Livro livro) {
        if (livro == null) {
            throw new NullPointerException("O livro é obrigatório.");
        }
        for (final Acervo acervo : this.acervos) {
            if (acervo.getLivro().getId().equals(livro.getId())) {
                return Optional.of(acervo);
            }
        }
        return Optional.empty();
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

    public String getCpfCnpj() {
        return this.cpfCnpj;
    }

    public String getEmail() {
        return this.email;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public Endereco getEndereco() {
        return this.endereco;
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

    public List<Acervo> getAcervos() {
        return List.copyOf(this.acervos);
    }

    @Override
    public String toString() {
        return ("Biblioteca{id=%s, nome='%s', cpfCnpj='%s', email='%s', telefone='%s', ativa=%s, "
                + "dataCriacao=%s, dataAtualizacao=%s, endereco=%s, itensAcervo=%s}")
                .formatted(
                        this.id,
                        this.nome,
                        this.cpfCnpj,
                        this.email,
                        this.telefone,
                        this.ativa,
                        this.dataCriacao,
                        this.dataAtualizacao,
                        this.endereco,
                        this.acervos.size());
    }

    private void validarBibliotecaAtiva() {
        if (!this.ativa) {
            throw new IllegalStateException("A biblioteca precisa estar ativa para realizar esta operação.");
        }
    }

    private boolean contemAcervoComId(final Long idAcervo) {
        for (final Acervo acervo : this.acervos) {
            if (acervo.getId().equals(idAcervo)) {
                return true;
            }
        }
        return false;
    }

    private void atualizarDataAtualizacao() {
        final LocalDateTime agora = LocalDateTime.now();
        this.dataAtualizacao = agora.isAfter(this.dataAtualizacao)
                ? agora
                : this.dataAtualizacao.plusNanos(1);
    }

}
