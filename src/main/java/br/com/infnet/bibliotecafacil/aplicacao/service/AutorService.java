package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Autor;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public final class AutorService {

    private final Map<Long, Autor> autores = new LinkedHashMap<>();

    public void incluir(final Autor autor) {
        this.validarDados(autor);
        if (this.autores.containsKey(autor.getId())) {
            throw new OperacaoNaoPermitidaException("Já existe um autor com o identificador informado.");
        }
        this.validarNomeDisponivel(autor);
        this.autores.put(autor.getId(), autor);
    }

    public void alterar(final Autor autor) {
        this.validarDados(autor);
        this.validarExistencia(autor.getId());
        this.validarNomeDisponivel(autor);
        this.autores.put(autor.getId(), autor);
    }

    public void excluir(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        this.autores.remove(id);
    }

    public Autor obterPorId(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        return this.autores.get(id);
    }

    public List<Autor> listar() {
        return List.copyOf(this.autores.values());
    }

    public List<Autor> listarAtivos() {
        return this.autores.values().stream()
                .filter(Autor::isAtivo)
                .toList();
    }

    public List<Autor> buscarPorNome(final String nome) {
        this.validarTextoDeBusca(nome);
        final String nomeNormalizado = nome.toLowerCase(Locale.ROOT);
        return this.autores.values().stream()
                .filter(autor -> autor.getNome().toLowerCase(Locale.ROOT).contains(nomeNormalizado))
                .toList();
    }

    public List<Autor> listarOrdenadosPorNome() {
        return this.autores.values().stream()
                .sorted(Comparator.comparing(Autor::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<String> listarNomes() {
        return this.autores.values().stream()
                .map(Autor::getNome)
                .toList();
    }

    private void validarDados(final Autor autor) {
        if (autor == null) {
            throw new DadosInvalidosException("O autor é obrigatório.");
        }
        this.validarId(autor.getId());
        if (autor.getNome() == null || autor.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome do autor é obrigatório.");
        }
        if (autor.getNomeCatalogacao() == null || autor.getNomeCatalogacao().isBlank()) {
            throw new DadosInvalidosException("O nome de catalogação do autor é obrigatório.");
        }
    }

    private void validarId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do autor é obrigatório.");
        }
    }

    private void validarExistencia(final Long id) {
        if (!this.autores.containsKey(id)) {
            throw new ObjetoNaoEncontradoException("Autor não encontrado para o identificador %s.".formatted(id));
        }
    }

    private void validarNomeDisponivel(final Autor autor) {
        final boolean nomeEmUso = this.autores.values().stream()
                .anyMatch(autorAtual -> !autorAtual.getId().equals(autor.getId())
                        && autorAtual.getNome().equalsIgnoreCase(autor.getNome()));
        if (nomeEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um autor com o nome informado.");
        }
    }

    private void validarTextoDeBusca(final String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome para busca é obrigatório.");
        }
    }

}
