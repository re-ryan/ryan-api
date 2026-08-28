package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Autor;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.AutorRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public final class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(final AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public Autor incluir(final Autor autor) {
        this.validarCamposObrigatorios(autor);
        if (autor.getId() != null) {
            throw new DadosInvalidosException("O identificador do autor deve ser gerado pelo banco de dados.");
        }
        this.validarNomeDisponivel(autor);
        return this.autorRepository.save(autor);
    }

    public Autor alterar(final Autor autor) {
        this.validarCamposObrigatorios(autor);
        final Autor autorPersistido = this.obterPorId(autor.getId());
        this.validarNomeDisponivel(autor);
        autorPersistido.atualizarDados(autor.getNome(), autor.getNomeCatalogacao());
        return this.autorRepository.save(autorPersistido);
    }

    public void excluir(final Long id) {
        final Autor autor = this.obterPorId(id);
        this.autorRepository.delete(autor);
    }

    public Autor obterPorId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do autor é obrigatório.");
        }
        return this.autorRepository.findById(id)
                .orElseThrow(() -> this.criarAutorNaoEncontrado(id));
    }

    public List<Autor> listar() {
        return List.copyOf(this.autorRepository.findAll());
    }

    public List<Autor> listarAtivos() {
        return this.autorRepository.findByAtivoTrue();
    }

    public List<Autor> buscarPorNome(final String nome) {
        this.validarTextoDeBusca(nome);
        return this.autorRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Autor> listarOrdenadosPorNome() {
        return this.autorRepository.findAllByOrderByNomeAsc();
    }

    public List<String> listarNomes() {
        return this.autorRepository.findAll().stream()
                .map(Autor::getNome)
                .toList();
    }

    private void validarCamposObrigatorios(final Autor autor) {
        if (autor == null) {
            throw new DadosInvalidosException("O autor é obrigatório.");
        }
        if (autor.getNome() == null || autor.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome do autor é obrigatório.");
        }
        if (autor.getNomeCatalogacao() == null || autor.getNomeCatalogacao().isBlank()) {
            throw new DadosInvalidosException("O nome de catalogação do autor é obrigatório.");
        }
    }

    private void validarNomeDisponivel(final Autor autor) {
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

    private ObjetoNaoEncontradoException criarAutorNaoEncontrado(final Long id) {
        return new ObjetoNaoEncontradoException("Autor não encontrado para o identificador %s.".formatted(id));
    }

}
