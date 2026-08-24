package br.com.infnet.bibliotecafacil.api.controller;

import br.com.infnet.bibliotecafacil.aplicacao.service.AutorService;
import br.com.infnet.bibliotecafacil.dominio.Autor;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/autores")
@Tag(name = "Autores")
public final class AutorController {

    private final AutorService autorService;

    public AutorController(final AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public List<Autor> listar() {
        return this.autorService.listar();
    }

    @GetMapping("/{id}")
    public Autor obterPorId(final @PathVariable Long id) {
        return this.autorService.obterPorId(id);
    }

    @PostMapping
    public ResponseEntity<Autor> incluir(final @RequestBody Autor autor) {
        this.autorService.incluir(autor);
        return ResponseEntity.created(URI.create("/api/autores/" + autor.getId())).body(autor);
    }

    @PutMapping("/{id}")
    public Autor alterar(final @PathVariable Long id, final @RequestBody Autor autor) {
        autor.setId(id);
        this.autorService.alterar(autor);
        return autor;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(final @PathVariable Long id) {
        this.autorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
