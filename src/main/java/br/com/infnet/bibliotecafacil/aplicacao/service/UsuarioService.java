package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.TipoUsuario;
import br.com.infnet.bibliotecafacil.dominio.Usuario;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public final class UsuarioService {

    private final Map<Long, Usuario> usuarios = new LinkedHashMap<>();

    public void incluir(final Usuario usuario) {
        this.validarDados(usuario);
        if (this.usuarios.containsKey(usuario.getId())) {
            throw new OperacaoNaoPermitidaException("Já existe um usuário com o identificador informado.");
        }
        this.validarDadosUnicos(usuario);
        this.usuarios.put(usuario.getId(), usuario);
    }

    public void alterar(final Usuario usuario) {
        this.validarDados(usuario);
        this.validarExistencia(usuario.getId());
        this.validarDadosUnicos(usuario);
        this.usuarios.put(usuario.getId(), usuario);
    }

    public void excluir(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        this.usuarios.remove(id);
    }

    public Usuario obterPorId(final Long id) {
        this.validarId(id);
        this.validarExistencia(id);
        return this.usuarios.get(id);
    }

    public List<Usuario> listar() {
        return List.copyOf(this.usuarios.values());
    }

    public List<Usuario> listarAtivos() {
        return this.usuarios.values().stream()
                .filter(Usuario::isAtivo)
                .toList();
    }

    public List<Usuario> filtrarPorTipo(final TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new DadosInvalidosException("O tipo de usuário para filtragem é obrigatório.");
        }
        return this.usuarios.values().stream()
                .filter(usuario -> usuario.getTipoUsuario() == tipoUsuario)
                .toList();
    }

    public List<Usuario> buscarPorNome(final String nome) {
        this.validarTextoDeBusca(nome);
        final String nomeNormalizado = nome.toLowerCase(Locale.ROOT);
        return this.usuarios.values().stream()
                .filter(usuario -> usuario.getNomeCompleto().toLowerCase(Locale.ROOT).contains(nomeNormalizado))
                .toList();
    }

    public List<Usuario> listarOrdenadosPorNome() {
        return this.usuarios.values().stream()
                .sorted(Comparator.comparing(Usuario::getNomeCompleto, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<String> listarEmails() {
        return this.usuarios.values().stream()
                .map(Usuario::getEmail)
                .toList();
    }

    private void validarDados(final Usuario usuario) {
        if (usuario == null) {
            throw new DadosInvalidosException("O usuário é obrigatório.");
        }
        this.validarId(usuario.getId());
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
    }

    private void validarId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do usuário é obrigatório.");
        }
    }

    private void validarExistencia(final Long id) {
        if (!this.usuarios.containsKey(id)) {
            throw new ObjetoNaoEncontradoException("Usuário não encontrado para o identificador %s.".formatted(id));
        }
    }

    private void validarDadosUnicos(final Usuario usuario) {
        final boolean loginEmUso = this.usuarios.values().stream()
                .anyMatch(usuarioAtual -> !usuarioAtual.getId().equals(usuario.getId())
                        && usuarioAtual.getLogin().equalsIgnoreCase(usuario.getLogin()));
        if (loginEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um usuário com o login informado.");
        }

        final boolean emailEmUso = this.usuarios.values().stream()
                .anyMatch(usuarioAtual -> !usuarioAtual.getId().equals(usuario.getId())
                        && usuarioAtual.getEmail().equalsIgnoreCase(usuario.getEmail()));
        if (emailEmUso) {
            throw new OperacaoNaoPermitidaException("Já existe um usuário com o e-mail informado.");
        }
    }

    private void validarTextoDeBusca(final String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome para busca é obrigatório.");
        }
    }

}
