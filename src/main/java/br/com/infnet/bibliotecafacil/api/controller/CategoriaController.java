package br.com.infnet.bibliotecafacil.api.controller;

import br.com.infnet.bibliotecafacil.api.dto.CategoriaRequestDto;
import br.com.infnet.bibliotecafacil.aplicacao.service.CategoriaService;
import br.com.infnet.bibliotecafacil.dominio.Categoria;
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
@RequestMapping("/api/categorias")
@Tag(name = "Categorias")
public final class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(final CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> listar() {
        return this.categoriaService.listar();
    }

    @GetMapping("/busca")
    public List<Categoria> buscarPorNome(
            final @RequestParam String nome,
            final @ParameterObject @SortDefault(sort = "nome", direction = Sort.Direction.ASC) Sort ordenacao) {
        return this.categoriaService.buscarPorNome(nome, ordenacao);
    }

    @GetMapping("/{id}")
    public Categoria obterPorId(final @PathVariable Long id) {
        return this.categoriaService.obterPorId(id);
    }

    @PostMapping
    public ResponseEntity<Categoria> incluir(final @Valid @RequestBody CategoriaRequestDto request) {
        final Categoria categoria = this.criarCategoria(request);
        final Categoria categoriaIncluida = this.categoriaService.incluir(categoria);
        return ResponseEntity.created(URI.create("/api/categorias/" + categoriaIncluida.getId()))
                .body(categoriaIncluida);
    }

    @PutMapping("/{id}")
    public Categoria alterar(final @PathVariable Long id, final @Valid @RequestBody CategoriaRequestDto request) {
        final Categoria categoria = this.criarCategoria(request);
        categoria.setId(id);
        return this.categoriaService.alterar(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(final @PathVariable Long id) {
        this.categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Categoria criarCategoria(final CategoriaRequestDto request) {
        final Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        return categoria;
    }
}
