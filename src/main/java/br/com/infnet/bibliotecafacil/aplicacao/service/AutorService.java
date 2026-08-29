package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Autor;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.AutorRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AutorService {

    private static final Sort ORDENACAO_PADRAO = Sort.by(Sort.Direction.ASC, "nome");

    private final AutorRepository autorRepository;

    public AutorService(final AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Autor incluir(final Autor autor) {
        this.validarAutor(autor);
        return this.autorRepository.save(autor);
    }

    @Transactional
    public Autor alterar(final Autor autor) {
        this.validarAutor(autor);
        final Autor autorPersistido = this.obterPorId(autor.getId());
        autorPersistido.atualizarDados(autor.getNome(), autor.getNomeCatalogacao());
        return this.autorRepository.save(autorPersistido);
    }

    @Transactional
    public void excluir(final Long id) {
        final Autor autor = this.obterPorId(id);
        this.autorRepository.delete(autor);
    }

    public Autor obterPorId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do autor é obrigatório.");
        }
        return this.autorRepository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Autor não encontrado para o identificador %s.".formatted(id)));
    }

    public List<Autor> listar() {
        return List.copyOf(this.autorRepository.findAll());
    }

    public List<Autor> listarAtivos() {
        return this.autorRepository.findByAtivoTrue();
    }

    public List<Autor> buscarPorNome(final String nome) {
        return this.buscarPorNome(nome, ORDENACAO_PADRAO);
    }

    public List<Autor> buscarPorNome(final String nome, final Sort ordenacao) {
        this.validarTextoDeBusca(nome);
        return this.autorRepository.findByNomeContainingIgnoreCase(nome, ordenacao);
    }

    public List<Autor> listarOrdenadosPorNome() {
        return this.autorRepository.findAll(ORDENACAO_PADRAO);
    }

    public List<String> listarNomes() {
        return this.autorRepository.findAll().stream()
                .map(Autor::getNome)
                .toList();
    }

    private void validarAutor(final Autor autor) {
        if (autor == null) {
            throw new DadosInvalidosException("O autor é obrigatório.");
        }

        if (autor.getNome() == null || autor.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome do autor é obrigatório.");
        }

        if (autor.getNomeCatalogacao() == null || autor.getNomeCatalogacao().isBlank()) {
            throw new DadosInvalidosException("O nome de catalogação do autor é obrigatório.");
        }

        final boolean nomeEmUso = autor.getId() == null
                ? this.autorRepository.existsByNomeIgnoreCase(autor.getNome())
                : this.autorRepository.existsByNomeIgnoreCaseAndIdNot(autor.getNome(), autor.getId());

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
