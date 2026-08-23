package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Categoria;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public final class CategoriaService {

    private final Map<Long, Categoria> categorias = new LinkedHashMap<>();

    public void incluir(final Categoria categoria) {
        this.validarDados(categoria);
        if (this.categorias.containsKey(categoria.getId())) {
            throw new OperacaoNaoPermitidaException("Já existe uma categoria com o identificador informado.");
        }
        this.validarNomeDisponivel(categoria);
        this.categorias.put(categoria.getId(), categoria);
    }

    public void alterar(final Categoria categoria) {
        this.validarDados(categoria);
        this.validarExistencia(categoria.getId());
        this.validarNomeDisponivel(categoria);
        this.categorias.put(categoria.getId(), categoria);
    }

    public void excluir(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        this.categorias.remove(id);
    }

    public Categoria obterPorId(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        return this.categorias.get(id);
    }

    public List<Categoria> listar() {
        return List.copyOf(this.categorias.values());
    }

    public List<Categoria> listarAtivas() {
        return this.categorias.values().stream()
                .filter(Categoria::isAtiva)
                .toList();
    }

    public List<Categoria> buscarPorNome(final String nome) {
        this.validarTextoDeBusca(nome);
        final String nomeNormalizado = nome.toLowerCase(Locale.ROOT);
        return this.categorias.values().stream()
                .filter(categoria -> categoria.getNome().toLowerCase(Locale.ROOT).contains(nomeNormalizado))
                .toList();
    }

    public List<Categoria> listarOrdenadasPorNome() {
        return this.categorias.values().stream()
                .sorted(Comparator.comparing(Categoria::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<String> listarNomes() {
        return this.categorias.values().stream()
                .map(Categoria::getNome)
                .toList();
    }

    private void validarDados(final Categoria categoria) {
        if (categoria == null) {
            throw new DadosInvalidosException("A categoria é obrigatória.");
        }
        this.validarId(categoria.getId());
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome da categoria é obrigatório.");
        }
    }

    private void validarId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador da categoria é obrigatório.");
        }
    }

    private void validarExistencia(final Long id) {
        if (!this.categorias.containsKey(id)) {
            throw new ObjetoNaoEncontradoException("Categoria não encontrada para o identificador %s.".formatted(id));
        }
    }

    private void validarNomeDisponivel(final Categoria categoria) {
        final boolean nomeEmUso = this.categorias.values().stream()
                .anyMatch(categoriaAtual -> !categoriaAtual.getId().equals(categoria.getId())
                        && categoriaAtual.getNome().equalsIgnoreCase(categoria.getNome()));
        if (nomeEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma categoria com o nome informado.");
        }
    }

    private void validarTextoDeBusca(final String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome para busca é obrigatório.");
        }
    }

}
