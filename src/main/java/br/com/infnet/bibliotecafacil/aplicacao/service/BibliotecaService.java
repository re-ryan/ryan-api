package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Biblioteca;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.BibliotecaRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BibliotecaService {

    private static final Sort ORDENACAO_PADRAO = Sort.by(Sort.Direction.ASC, "nome");

    private final BibliotecaRepository bibliotecaRepository;

    public BibliotecaService(final BibliotecaRepository bibliotecaRepository) {
        this.bibliotecaRepository = bibliotecaRepository;
    }

    @Transactional
    public Biblioteca incluir(final Biblioteca biblioteca) {
        this.validarBiblioteca(biblioteca);
        return this.bibliotecaRepository.save(biblioteca);
    }

    @Transactional
    public Biblioteca alterar(final Biblioteca biblioteca) {
        this.validarBiblioteca(biblioteca);
        final Biblioteca bibliotecaPersistida = this.obterPorId(biblioteca.getId());
        bibliotecaPersistida.atualizarDados(biblioteca);
        return this.bibliotecaRepository.save(bibliotecaPersistida);
    }

    @Transactional
    public void excluir(final Long id) {
        final Biblioteca biblioteca = this.obterPorId(id);
        this.bibliotecaRepository.delete(biblioteca);
    }

    public Biblioteca obterPorId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador da biblioteca é obrigatório.");
        }
        return this.bibliotecaRepository.findById(id)
                .orElseThrow(() -> this.criarBibliotecaNaoEncontrada(id));
    }

    public List<Biblioteca> listar() {
        return List.copyOf(this.bibliotecaRepository.findAll());
    }

    public List<Biblioteca> listarAtivas() {
        return this.bibliotecaRepository.findByAtivaTrue();
    }

    public List<Biblioteca> buscarPorNome(final String nome) {
        return this.buscarPorNome(nome, ORDENACAO_PADRAO);
    }

    public List<Biblioteca> buscarPorNome(final String nome, final Sort ordenacao) {
        this.validarTextoDeBusca(nome);
        return this.bibliotecaRepository.findByNomeContainingIgnoreCase(nome, ordenacao);
    }

    public List<Biblioteca> listarOrdenadasPorNome() {
        return this.bibliotecaRepository.findAll(ORDENACAO_PADRAO);
    }

    public List<String> listarNomes() {
        return this.bibliotecaRepository.findAll().stream()
                .map(Biblioteca::getNome)
                .toList();
    }

    private void validarBiblioteca(final Biblioteca biblioteca) {
        if (biblioteca == null) {
            throw new DadosInvalidosException("A biblioteca é obrigatória.");
        }

        if (biblioteca.getNome() == null || biblioteca.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome da biblioteca é obrigatório.");
        }

        if (biblioteca.getCpfCnpj() == null || biblioteca.getCpfCnpj().isBlank()) {
            throw new DadosInvalidosException("O CPF ou CNPJ da biblioteca é obrigatório.");
        }

        if (biblioteca.getEmail() == null || biblioteca.getEmail().isBlank()) {
            throw new DadosInvalidosException("O e-mail da biblioteca é obrigatório.");
        }

        final boolean nomeEmUso = biblioteca.getId() == null
                ? this.bibliotecaRepository.existsByNomeIgnoreCase(biblioteca.getNome())
                : this.bibliotecaRepository.existsByNomeIgnoreCaseAndIdNot(
                        biblioteca.getNome(), biblioteca.getId());

        if (nomeEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma biblioteca com o nome informado.");
        }

        final boolean cpfCnpjEmUso = biblioteca.getId() == null
                ? this.bibliotecaRepository.existsByCpfCnpj(biblioteca.getCpfCnpj())
                : this.bibliotecaRepository.existsByCpfCnpjAndIdNot(
                        biblioteca.getCpfCnpj(), biblioteca.getId());
        if (cpfCnpjEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma biblioteca com o CPF ou CNPJ informado.");
        }

        final boolean emailEmUso = biblioteca.getId() == null
                ? this.bibliotecaRepository.existsByEmailIgnoreCase(biblioteca.getEmail())
                : this.bibliotecaRepository.existsByEmailIgnoreCaseAndIdNot(
                        biblioteca.getEmail(), biblioteca.getId());
        if (emailEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe uma biblioteca com o e-mail informado.");
        }
    }

    private void validarTextoDeBusca(final String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome para busca é obrigatório.");
        }
    }

    private ObjetoNaoEncontradoException criarBibliotecaNaoEncontrada(final Long id) {
        return new ObjetoNaoEncontradoException(
                "Biblioteca não encontrada para o identificador %s.".formatted(id));
    }

}
