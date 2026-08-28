package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Livro;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.LivroRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LivroService {

    private static final Sort ORDENACAO_PADRAO = Sort.by(Sort.Direction.ASC, "titulo");

    private final LivroRepository livroRepository;

    public LivroService(final LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public Livro incluir(final Livro livro) {
        this.validarLivro(livro);
        return this.livroRepository.save(livro);
    }

    @Transactional
    public Livro alterar(final Livro livro) {
        this.validarLivro(livro);
        final Livro livroPersistido = this.obterPorId(livro.getId());
        livroPersistido.atualizarDados(livro);
        return this.livroRepository.save(livroPersistido);
    }

    @Transactional
    public void excluir(final Long id) {
        final Livro livro = this.obterPorId(id);
        this.livroRepository.delete(livro);
    }

    public Livro obterPorId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do livro é obrigatório.");
        }
        return this.livroRepository.findById(id)
                .orElseThrow(() -> this.criarLivroNaoEncontrado(id));
    }

    public List<Livro> listar() {
        return List.copyOf(this.livroRepository.findAll());
    }

    public List<Livro> listarAtivos() {
        return this.livroRepository.findByAtivoTrue();
    }

    public List<Livro> buscarPorTitulo(final String titulo) {
        return this.buscarPorTitulo(titulo, ORDENACAO_PADRAO);
    }

    public List<Livro> buscarPorTitulo(final String titulo, final Sort ordenacao) {
        this.validarTextoDeBusca(titulo);
        return this.livroRepository.findByTituloContainingIgnoreCase(titulo, ordenacao);
    }

    public List<Livro> listarOrdenadosPorTitulo() {
        return this.livroRepository.findAll(ORDENACAO_PADRAO);
    }

    public List<String> listarTitulos() {
        return this.livroRepository.findAll().stream()
                .map(Livro::getTitulo)
                .toList();
    }

    private void validarLivro(final Livro livro) {
        if (livro == null) {
            throw new DadosInvalidosException("O livro é obrigatório.");
        }
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new DadosInvalidosException("O título do livro é obrigatório.");
        }
        if (livro.getIsbn13() == null || livro.getIsbn13().isBlank()) {
            throw new DadosInvalidosException("O ISBN-13 do livro é obrigatório.");
        }

        final boolean isbn13EmUso = livro.getId() == null
                ? this.livroRepository.existsByIsbn13(livro.getIsbn13())
                : this.livroRepository.existsByIsbn13AndIdNot(livro.getIsbn13(), livro.getId());
        if (isbn13EmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um livro com o ISBN-13 informado.");
        }

        if (livro.getIsbn10() == null) {
            return;
        }
        final boolean isbn10EmUso = livro.getId() == null
                ? this.livroRepository.existsByIsbn10(livro.getIsbn10())
                : this.livroRepository.existsByIsbn10AndIdNot(livro.getIsbn10(), livro.getId());
        if (isbn10EmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um livro com o ISBN-10 informado.");
        }
    }

    private void validarTextoDeBusca(final String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new DadosInvalidosException("O título para busca é obrigatório.");
        }
    }

    private ObjetoNaoEncontradoException criarLivroNaoEncontrado(final Long id) {
        return new ObjetoNaoEncontradoException("Livro não encontrado para o identificador %s.".formatted(id));
    }

}
