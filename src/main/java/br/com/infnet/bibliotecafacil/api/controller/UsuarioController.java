package br.com.infnet.bibliotecafacil.api.controller;

import br.com.infnet.bibliotecafacil.api.dto.UsuarioRequestDto;
import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.service.BibliotecaService;
import br.com.infnet.bibliotecafacil.aplicacao.service.UsuarioService;
import br.com.infnet.bibliotecafacil.dominio.Administrador;
import br.com.infnet.bibliotecafacil.dominio.Bibliotecario;
import br.com.infnet.bibliotecafacil.dominio.Leitor;
import br.com.infnet.bibliotecafacil.dominio.TipoUsuario;
import br.com.infnet.bibliotecafacil.dominio.Usuario;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
public final class UsuarioController {

    private final UsuarioService usuarioService;
    private final BibliotecaService bibliotecaService;

    public UsuarioController(final UsuarioService usuarioService, final BibliotecaService bibliotecaService) {
        this.usuarioService = usuarioService;
        this.bibliotecaService = bibliotecaService;
    }

    @GetMapping
    public List<Usuario> listar() {
        return this.usuarioService.listar();
    }

    @GetMapping("/busca")
    public List<Usuario> buscarPorNome(
            final @RequestParam String nome,
            final @ParameterObject @SortDefault(sort = "nomeCompleto", direction = Sort.Direction.ASC) Sort ordenacao) {
        return this.usuarioService.buscarPorNome(nome, ordenacao);
    }

    @GetMapping("/{id}")
    public Usuario obterPorId(final @PathVariable Long id) {
        return this.usuarioService.obterPorId(id);
    }

    @PostMapping
    public ResponseEntity<Usuario> incluir(final @Valid @RequestBody UsuarioRequestDto request) {
        final Usuario usuario = this.criarUsuario(request);
        final Usuario usuarioIncluido = this.usuarioService.incluir(usuario);
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuarioIncluido.getId()))
                .body(usuarioIncluido);
    }

    @PutMapping("/{id}")
    public Usuario alterar(final @PathVariable Long id, final @Valid @RequestBody UsuarioRequestDto request) {
        final Usuario usuario = this.criarUsuario(request);
        usuario.setId(id);
        return this.usuarioService.alterar(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(final @PathVariable Long id) {
        this.usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Usuario criarUsuario(final UsuarioRequestDto request) {
        final Usuario usuario = this.criarTipoDeUsuario(request.tipoUsuario());
        usuario.setNomeCompleto(request.nomeCompleto());
        usuario.setDataNascimento(request.dataNascimento());
        usuario.setLogin(request.login());
        usuario.setEmail(request.email());
        usuario.setSenhaHash(request.senhaHash());
        usuario.setTipoUsuario(request.tipoUsuario());
        if (usuario instanceof Bibliotecario bibliotecario) {
            if (request.bibliotecaId() == null) {
                throw new DadosInvalidosException("A biblioteca do bibliotecário é obrigatória.");
            }
            bibliotecario.setBiblioteca(this.bibliotecaService.obterPorId(request.bibliotecaId()));
        }
        return usuario;
    }

    private Usuario criarTipoDeUsuario(final TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new DadosInvalidosException("O tipo do usuário é obrigatório.");
        }
        return switch (tipoUsuario) {
            case LEITOR -> new Leitor();
            case BIBLIOTECARIO -> new Bibliotecario();
            case ADMINISTRADOR -> new Administrador();
        };
    }
}
