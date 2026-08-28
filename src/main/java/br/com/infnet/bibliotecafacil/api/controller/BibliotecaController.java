package br.com.infnet.bibliotecafacil.api.controller;

import br.com.infnet.bibliotecafacil.api.dto.BibliotecaRequestDto;
import br.com.infnet.bibliotecafacil.aplicacao.service.BibliotecaService;
import br.com.infnet.bibliotecafacil.dominio.Biblioteca;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bibliotecas")
@Tag(name = "Bibliotecas")
public final class BibliotecaController {

    private final BibliotecaService bibliotecaService;

    public BibliotecaController(final BibliotecaService bibliotecaService) {
        this.bibliotecaService = bibliotecaService;
    }

    @GetMapping
    public List<Biblioteca> listar() {
        return this.bibliotecaService.listar();
    }

    @GetMapping("/{id}")
    public Biblioteca obterPorId(final @PathVariable Long id) {
        return this.bibliotecaService.obterPorId(id);
    }

    @PostMapping
    public ResponseEntity<Biblioteca> incluir(final @Valid @RequestBody BibliotecaRequestDto request) {
        final Biblioteca biblioteca = this.criarBiblioteca(request);
        final Biblioteca bibliotecaIncluida = this.bibliotecaService.incluir(biblioteca);
        return ResponseEntity.created(URI.create("/api/bibliotecas/" + bibliotecaIncluida.getId()))
                .body(bibliotecaIncluida);
    }

    @PutMapping("/{id}")
    public Biblioteca alterar(final @PathVariable Long id, final @Valid @RequestBody BibliotecaRequestDto request) {
        final Biblioteca biblioteca = this.criarBiblioteca(request);
        biblioteca.setId(id);
        return this.bibliotecaService.alterar(biblioteca);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(final @PathVariable Long id) {
        this.bibliotecaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Biblioteca criarBiblioteca(final BibliotecaRequestDto request) {
        final Biblioteca biblioteca = new Biblioteca();
        biblioteca.setNome(request.nome());
        biblioteca.setCpfCnpj(request.cpfCnpj());
        biblioteca.setEmail(request.email());
        biblioteca.setTelefone(request.telefone());
        if (request.endereco() != null) {
            biblioteca.setEndereco(request.endereco());
        }
        return biblioteca;
    }
}
