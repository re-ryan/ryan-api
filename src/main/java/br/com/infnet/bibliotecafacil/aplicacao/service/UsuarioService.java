package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Bibliotecario;
import br.com.infnet.bibliotecafacil.dominio.TipoUsuario;
import br.com.infnet.bibliotecafacil.dominio.Usuario;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private static final Sort ORDENACAO_PADRAO = Sort.by(Sort.Direction.ASC, "nomeCompleto");

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(final UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario incluir(final Usuario usuario) {
        this.validarUsuario(usuario);
        return this.usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario alterar(final Usuario usuario) {
        this.validarUsuario(usuario);
        final Usuario usuarioPersistido = this.obterPorId(usuario.getId());
        this.validarMesmoTipo(usuarioPersistido, usuario);
        usuarioPersistido.atualizarDados(usuario);
        if (usuarioPersistido instanceof Bibliotecario bibliotecarioPersistido
                && usuario instanceof Bibliotecario bibliotecario) {
            bibliotecarioPersistido.setBiblioteca(bibliotecario.getBiblioteca());
        }
        return this.usuarioRepository.save(usuarioPersistido);
    }

    @Transactional
    public void excluir(final Long id) {
        final Usuario usuario = this.obterPorId(id);
        this.usuarioRepository.delete(usuario);
    }

    public Usuario obterPorId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do usuário é obrigatório.");
        }
        return this.usuarioRepository.findById(id).orElseThrow(() -> this.criarUsuarioNaoEncontrado(id));
    }

    public List<Usuario> listar() {
        return List.copyOf(this.usuarioRepository.findAll());
    }

    public List<Usuario> listarAtivos() {
        return this.usuarioRepository.findByAtivoTrue();
    }

    public List<Usuario> filtrarPorTipo(final TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new DadosInvalidosException("O tipo de usuário para filtragem é obrigatório.");
        }
        return this.usuarioRepository.findByTipoUsuario(tipoUsuario);
    }

    public List<Usuario> buscarPorNome(final String nome) {
        return this.buscarPorNome(nome, ORDENACAO_PADRAO);
    }

    public List<Usuario> buscarPorNome(final String nome, final Sort ordenacao) {
        this.validarTextoDeBusca(nome);
        return this.usuarioRepository.findByNomeCompletoContainingIgnoreCase(nome, ordenacao);
    }

    public List<Usuario> listarOrdenadosPorNome() {
        return this.usuarioRepository.findAll(ORDENACAO_PADRAO);
    }

    public List<String> listarEmails() {
        return this.usuarioRepository.findAll().stream()
                .map(Usuario::getEmail)
                .toList();
    }

    private void validarUsuario(final Usuario usuario) {
        if (usuario == null) {
            throw new DadosInvalidosException("O usuário é obrigatório.");
        }

        if (usuario.getNomeCompleto() == null || usuario.getNomeCompleto().isBlank()) {
            throw new DadosInvalidosException("O nome completo do usuário é obrigatório.");
        }

        if (usuario.getLogin() == null || usuario.getLogin().isBlank()) {
            throw new DadosInvalidosException("O login do usuário é obrigatório.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new DadosInvalidosException("O e-mail do usuário é obrigatório.");
        }

        if (usuario.getSenhaHash() == null || usuario.getSenhaHash().isBlank()) {
            throw new DadosInvalidosException("O hash da senha do usuário é obrigatório.");
        }

        if (usuario.getTipoUsuario() == null) {
            throw new DadosInvalidosException("O tipo do usuário é obrigatório.");
        }

        final boolean loginEmUso = usuario.getId() == null
                ? this.usuarioRepository.existsByLoginIgnoreCase(usuario.getLogin())
                : this.usuarioRepository.existsByLoginIgnoreCaseAndIdNot(
                        usuario.getLogin(), usuario.getId());
        if (loginEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um usuário com o login informado.");
        }

        final boolean emailEmUso = usuario.getId() == null
                ? this.usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())
                : this.usuarioRepository.existsByEmailIgnoreCaseAndIdNot(
                        usuario.getEmail(), usuario.getId());
        if (emailEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um usuário com o e-mail informado.");
        }
    }

    private void validarTextoDeBusca(final String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome para busca é obrigatório.");
        }
    }

    private void validarMesmoTipo(final Usuario usuarioPersistido, final Usuario usuario) {
        if (!usuarioPersistido.getClass().equals(usuario.getClass())) {
            throw new OperacaoNaoPermitidaException("O tipo do usuário não pode ser alterado.");
        }
    }

    private ObjetoNaoEncontradoException criarUsuarioNaoEncontrado(final Long id) {
        return new ObjetoNaoEncontradoException("Usuário não encontrado para o identificador %s.".formatted(id));
    }

}
