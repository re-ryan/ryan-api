package br.com.infnet.bibliotecafacil.api.controller;

import br.com.infnet.bibliotecafacil.api.dto.LivroRequestDto;
import br.com.infnet.bibliotecafacil.aplicacao.service.LivroService;
import br.com.infnet.bibliotecafacil.dominio.Livro;
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
@RequestMapping("/api/livros")
@Tag(name = "Livros")
public final class LivroController {

    private final LivroService livroService;

    public LivroController(final LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping
    public List<Livro> listar() {
        return this.livroService.listar();
    }

    @GetMapping("/busca")
    public List<Livro> buscarPorTitulo(
            final @RequestParam String titulo,
            final @ParameterObject @SortDefault(sort = "titulo", direction = Sort.Direction.ASC) Sort ordenacao) {
        return this.livroService.buscarPorTitulo(titulo, ordenacao);
    }

    @GetMapping("/{id}")
    public Livro obterPorId(final @PathVariable Long id) {
        return this.livroService.obterPorId(id);
    }

    @PostMapping
    public ResponseEntity<Livro> incluir(final @Valid @RequestBody LivroRequestDto request) {
        final Livro livro = this.criarLivro(request);
        final Livro livroIncluido = this.livroService.incluir(livro);
        return ResponseEntity.created(URI.create("/api/livros/" + livroIncluido.getId()))
                .body(livroIncluido);
    }

    @PutMapping("/{id}")
    public Livro alterar(final @PathVariable Long id, final @Valid @RequestBody LivroRequestDto request) {
        final Livro livroAlterado = this.criarLivro(request);
        livroAlterado.setId(id);
        return this.livroService.alterar(livroAlterado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(final @PathVariable Long id) {
        this.livroService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Livro criarLivro(final LivroRequestDto request) {
        final Livro livro = new Livro();
        livro.setTitulo(request.titulo());
        livro.setIsbn(request.isbn10(), request.isbn13());
        livro.setEditora(request.editora());
        livro.setAnoPublicacao(request.anoPublicacao());
        livro.setEdicao(request.edicao());
        livro.setDescricao(request.descricao());
        livro.setUrlImagemCapa(request.urlImagemCapa());
        return livro;
    }
}
