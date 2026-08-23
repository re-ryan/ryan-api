package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Livro;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public final class LivroService {

    private final Map<Long, Livro> livros = new LinkedHashMap<>();

    public void incluir(final Livro livro) {
        this.validarDados(livro);
        if (this.livros.containsKey(livro.getId())) {
            throw new OperacaoNaoPermitidaException("Já existe um livro com o identificador informado.");
        }
        this.validarIsbnsDisponiveis(livro);
        this.livros.put(livro.getId(), livro);
    }

    public void alterar(final Livro livro) {
        this.validarDados(livro);
        this.validarExistencia(livro.getId());
        this.validarIsbnsDisponiveis(livro);
        this.livros.put(livro.getId(), livro);
    }

    public void excluir(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        this.livros.remove(id);
    }

    public Livro obterPorId(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        return this.livros.get(id);
    }

    public List<Livro> listar() {
        return List.copyOf(this.livros.values());
    }

    public List<Livro> listarAtivos() {
        return this.livros.values().stream()
                .filter(Livro::isAtivo)
                .toList();
    }

    public List<Livro> buscarPorTitulo(final String titulo) {
        this.validarTextoDeBusca(titulo);
        final String tituloNormalizado = titulo.toLowerCase(Locale.ROOT);
        return this.livros.values().stream()
                .filter(livro -> livro.getTitulo().toLowerCase(Locale.ROOT).contains(tituloNormalizado))
                .toList();
    }

    public List<Livro> listarOrdenadosPorTitulo() {
        return this.livros.values().stream()
                .sorted(Comparator.comparing(Livro::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<String> listarTitulos() {
        return this.livros.values().stream()
                .map(Livro::getTitulo)
                .toList();
    }

    private void validarDados(final Livro livro) {
        if (livro == null) {
            throw new DadosInvalidosException("O livro é obrigatório.");
        }
        this.validarId(livro.getId());
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new DadosInvalidosException("O título do livro é obrigatório.");
        }
        if (livro.getIsbn13() == null || livro.getIsbn13().isBlank()) {
            throw new DadosInvalidosException("O ISBN-13 do livro é obrigatório.");
        }
    }

    private void validarId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do livro é obrigatório.");
        }
    }

    private void validarExistencia(final Long id) {
        if (!this.livros.containsKey(id)) {
            throw new ObjetoNaoEncontradoException("Livro não encontrado para o identificador %s.".formatted(id));
        }
    }

    private void validarIsbnsDisponiveis(final Livro livro) {
        final boolean isbn13EmUso = this.livros.values().stream()
                .anyMatch(livroAtual -> !livroAtual.getId().equals(livro.getId())
                        && livroAtual.getIsbn13().equals(livro.getIsbn13()));
        if (isbn13EmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um livro com o ISBN-13 informado.");
        }

        if (livro.getIsbn10() == null) {
            return;
        }
        final boolean isbn10EmUso = this.livros.values().stream()
                .anyMatch(livroAtual -> !livroAtual.getId().equals(livro.getId())
                        && livro.getIsbn10().equals(livroAtual.getIsbn10()));
        if (isbn10EmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um livro com o ISBN-10 informado.");
        }
    }

    private void validarTextoDeBusca(final String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new DadosInvalidosException("O título para busca é obrigatório.");
        }
    }

}
