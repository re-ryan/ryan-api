package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Categoria;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.CategoriaRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private static final Sort ORDENACAO_PADRAO = Sort.by(Sort.Direction.ASC, "nome");

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(final CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Categoria incluir(final Categoria categoria) {
        this.validarCategoria(categoria);
        return this.categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria alterar(final Categoria categoria) {
        this.validarCategoria(categoria);
        final Categoria categoriaPersistida = this.obterPorId(categoria.getId());
        categoriaPersistida.atualizarDados(categoria.getNome(), categoria.getDescricao());
        return this.categoriaRepository.save(categoriaPersistida);
    }

    @Transactional
    public void excluir(final Long id) {
        final Categoria categoria = this.obterPorId(id);
        this.categoriaRepository.delete(categoria);
    }

    public Categoria obterPorId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador da categoria é obrigatório.");
        }
        return this.categoriaRepository.findById(id)
                .orElseThrow(() -> this.criarCategoriaNaoEncontrada(id));
    }

    public List<Categoria> listar() {
        return List.copyOf(this.categoriaRepository.findAll());
    }

    public List<Categoria> listarAtivas() {
        return this.categoriaRepository.findByAtivaTrue();
    }

    public List<Categoria> buscarPorNome(final String nome) {
        return this.buscarPorNome(nome, ORDENACAO_PADRAO);
    }

    public List<Categoria> buscarPorNome(final String nome, final Sort ordenacao) {
        this.validarTextoDeBusca(nome);
        return this.categoriaRepository.findByNomeContainingIgnoreCase(nome, ordenacao);
    }

    public List<Categoria> listarOrdenadasPorNome() {
        return this.categoriaRepository.findAll(ORDENACAO_PADRAO);
    }

    public List<String> listarNomes() {
        return this.categoriaRepository.findAll().stream()
                .map(Categoria::getNome)
                .toList();
    }

    private void validarCategoria(final Categoria categoria) {
        if (categoria == null) {
            throw new DadosInvalidosException("A categoria é obrigatória.");
        }
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome da categoria é obrigatório.");
        }

        final boolean categoriaEmUso = categoria.getId() == null
                ? this.categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())
                : this.categoriaRepository.existsByNomeIgnoreCaseAndIdNot(
                        categoria.getNome(), categoria.getId());
        if (categoriaEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma categoria com o nome informado.");
        }
    }

    private void validarTextoDeBusca(final String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome para busca é obrigatório.");
        }
    }

    private ObjetoNaoEncontradoException criarCategoriaNaoEncontrada(final Long id) {
        return new ObjetoNaoEncontradoException("Categoria não encontrada para o identificador %s.".formatted(id));
    }

}
