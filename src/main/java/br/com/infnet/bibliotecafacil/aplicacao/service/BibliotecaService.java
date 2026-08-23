package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Biblioteca;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public final class BibliotecaService {

    private final Map<Long, Biblioteca> bibliotecas = new LinkedHashMap<>();

    public void incluir(final Biblioteca biblioteca) {
        this.validarDados(biblioteca);
        if (this.bibliotecas.containsKey(biblioteca.getId())) {
            throw new OperacaoNaoPermitidaException("Já existe uma biblioteca com o identificador informado.");
        }
        this.validarDadosUnicos(biblioteca);
        this.bibliotecas.put(biblioteca.getId(), biblioteca);
    }

    public void alterar(final Biblioteca biblioteca) {
        this.validarDados(biblioteca);
        this.validarExistencia(biblioteca.getId());
        this.validarDadosUnicos(biblioteca);
        this.bibliotecas.put(biblioteca.getId(), biblioteca);
    }

    public void excluir(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        this.bibliotecas.remove(id);
    }

    public Biblioteca obterPorId(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        return this.bibliotecas.get(id);
    }

    public List<Biblioteca> listar() {
        return List.copyOf(this.bibliotecas.values());
    }

    public List<Biblioteca> listarAtivas() {
        return this.bibliotecas.values().stream()
                .filter(Biblioteca::isAtiva)
                .toList();
    }

    public List<Biblioteca> buscarPorNome(final String nome) {
        this.validarTextoDeBusca(nome);
        final String nomeNormalizado = nome.toLowerCase(Locale.ROOT);
        return this.bibliotecas.values().stream()
                .filter(biblioteca -> biblioteca.getNome().toLowerCase(Locale.ROOT).contains(nomeNormalizado))
                .toList();
    }

    public List<Biblioteca> listarOrdenadasPorNome() {
        return this.bibliotecas.values().stream()
                .sorted(Comparator.comparing(Biblioteca::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<String> listarNomes() {
        return this.bibliotecas.values().stream()
                .map(Biblioteca::getNome)
                .toList();
    }

    private void validarDados(final Biblioteca biblioteca) {
        if (biblioteca == null) {
            throw new DadosInvalidosException("A biblioteca é obrigatória.");
        }
        this.validarId(biblioteca.getId());
        if (biblioteca.getNome() == null || biblioteca.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome da biblioteca é obrigatório.");
        }
        if (biblioteca.getCpfCnpj() == null || biblioteca.getCpfCnpj().isBlank()) {
            throw new DadosInvalidosException("O CPF ou CNPJ da biblioteca é obrigatório.");
        }
        if (biblioteca.getEmail() == null || biblioteca.getEmail().isBlank()) {
            throw new DadosInvalidosException("O e-mail da biblioteca é obrigatório.");
        }
    }

    private void validarId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador da biblioteca é obrigatório.");
        }
    }

    private void validarExistencia(final Long id) {
        if (!this.bibliotecas.containsKey(id)) {
            throw new ObjetoNaoEncontradoException("Biblioteca não encontrada para o identificador %s.".formatted(id));
        }
    }

    private void validarDadosUnicos(final Biblioteca biblioteca) {
        final boolean nomeEmUso = this.bibliotecas.values().stream()
                .anyMatch(bibliotecaAtual -> !bibliotecaAtual.getId().equals(biblioteca.getId())
                        && bibliotecaAtual.getNome().equalsIgnoreCase(biblioteca.getNome()));
        if (nomeEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma biblioteca com o nome informado.");
        }

        final boolean cpfCnpjEmUso = this.bibliotecas.values().stream()
                .anyMatch(bibliotecaAtual -> !bibliotecaAtual.getId().equals(biblioteca.getId())
                        && bibliotecaAtual.getCpfCnpj().equals(biblioteca.getCpfCnpj()));
        if (cpfCnpjEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma biblioteca com o CPF ou CNPJ informado.");
        }

        final boolean emailEmUso = this.bibliotecas.values().stream()
                .anyMatch(bibliotecaAtual -> !bibliotecaAtual.getId().equals(biblioteca.getId())
                        && bibliotecaAtual.getEmail().equalsIgnoreCase(biblioteca.getEmail()));
        if (emailEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma biblioteca com o e-mail informado.");
        }
    }

    private void validarTextoDeBusca(final String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome para busca é obrigatório.");
        }
    }

}
