package br.com.infnet.bibliotecafacil.loader;

import br.com.infnet.bibliotecafacil.dominio.Acervo;
import br.com.infnet.bibliotecafacil.dominio.Administrador;
import br.com.infnet.bibliotecafacil.dominio.Autor;
import br.com.infnet.bibliotecafacil.dominio.Biblioteca;
import br.com.infnet.bibliotecafacil.dominio.Bibliotecario;
import br.com.infnet.bibliotecafacil.dominio.Categoria;
import br.com.infnet.bibliotecafacil.dominio.Endereco;
import br.com.infnet.bibliotecafacil.dominio.Leitor;
import br.com.infnet.bibliotecafacil.dominio.Livro;
import br.com.infnet.bibliotecafacil.dominio.Reserva;
import br.com.infnet.bibliotecafacil.dominio.TipoUsuario;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public final class BibliotecaFacilLoader implements CommandLineRunner {

    @Override
    public void run(final String... args) {
        final Autor machadoDeAssis = new Autor();
        machadoDeAssis.setId(1L);
        machadoDeAssis.setNome("Machado de Assis");
        machadoDeAssis.setNomeCatalogacao("ASSIS, Machado de");

        final Autor clariceLispector = new Autor();
        clariceLispector.setId(2L);
        clariceLispector.setNome("Clarice Lispector");
        clariceLispector.setNomeCatalogacao("LISPECTOR, Clarice");

        final Categoria romance = new Categoria();
        romance.setId(1L);
        romance.setNome("Romance");
        romance.setDescricao("Narrativas de ficção em prosa.");

        final Categoria literaturaBrasileira = new Categoria();
        literaturaBrasileira.setId(2L);
        literaturaBrasileira.setNome("Literatura brasileira");
        literaturaBrasileira.setDescricao("Obras publicadas por autores brasileiros.");

        final Livro domCasmurro = new Livro();
        domCasmurro.setId(3L);
        domCasmurro.setTitulo("Dom Casmurro");
        domCasmurro.setIsbn(null, "978-85-359-0277-8");
        domCasmurro.setEditora("Companhia das Letras");
        domCasmurro.setAnoPublicacao(1899);
        domCasmurro.setEdicao("1ª edição");
        domCasmurro.setDescricao("Romance de Machado de Assis.");
        domCasmurro.adicionarAutor(machadoDeAssis, 1);
        domCasmurro.adicionarCategoria(romance);
        domCasmurro.adicionarCategoria(literaturaBrasileira);

        final Livro aHoraDaEstrela = new Livro();
        aHoraDaEstrela.setId(4L);
        aHoraDaEstrela.setTitulo("A Hora da Estrela");
        aHoraDaEstrela.setIsbn("85-325-0812-X", "9788532508126");
        aHoraDaEstrela.setEditora("Rocco");
        aHoraDaEstrela.setAnoPublicacao(1977);
        aHoraDaEstrela.setEdicao("1ª edição");
        aHoraDaEstrela.setDescricao("Romance de Clarice Lispector.");
        aHoraDaEstrela.adicionarAutor(clariceLispector, 1);
        aHoraDaEstrela.adicionarCategoria(romance);
        aHoraDaEstrela.adicionarCategoria(literaturaBrasileira);

        final Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("22230060");
        endereco.setLogradouro("Rua Marquês de Abrantes");
        endereco.setNumero("55");
        endereco.setComplemento(null);
        endereco.setBairro("Flamengo");
        endereco.setCidade("Rio de Janeiro");
        endereco.setUf("RJ");
        endereco.setLatitude(-22.932337);
        endereco.setLongitude(-43.177787);

        final Biblioteca biblioteca = new Biblioteca();
        biblioteca.setId(5L);
        biblioteca.setNome("Biblioteca Parceira Flamengo");
        biblioteca.setCpfCnpj("12345678000199");
        biblioteca.setEmail("contato@bibliotecafacil.com");
        biblioteca.setTelefone("(21) 2222-3333");
        biblioteca.setEndereco(endereco);
        final Acervo acervoDomCasmurro = biblioteca.adicionarLivro(6L, domCasmurro, 3);
        biblioteca.adicionarLivro(7L, aHoraDaEstrela, 2);

        final Leitor leitor = new Leitor();
        leitor.setId(8L);
        leitor.setNomeCompleto("Ana Souza");
        leitor.setDataNascimento(LocalDate.of(1992, 5, 14));
        leitor.setLogin("ana.souza");
        leitor.setEmail("ana.souza@email.com");
        leitor.setSenhaHash("hash-seguro-ana");
        leitor.setTipoUsuario(TipoUsuario.LEITOR);

        final Bibliotecario bibliotecario = new Bibliotecario();
        bibliotecario.setId(9L);
        bibliotecario.setNomeCompleto("Carlos Lima");
        bibliotecario.setDataNascimento(LocalDate.of(1985, 8, 20));
        bibliotecario.setLogin("carlos.lima");
        bibliotecario.setEmail("carlos.lima@biblioteca.com");
        bibliotecario.setSenhaHash("hash-seguro-carlos");
        bibliotecario.setTipoUsuario(TipoUsuario.BIBLIOTECARIO);
        bibliotecario.setBiblioteca(biblioteca);

        final Administrador administrador = new Administrador();
        administrador.setId(10L);
        administrador.setNomeCompleto("Marina Alves");
        administrador.setDataNascimento(null);
        administrador.setLogin("marina.alves");
        administrador.setEmail("marina@bibliotecafacil.com");
        administrador.setSenhaHash("hash-seguro-marina");
        administrador.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        final Reserva reserva = leitor.reservar(10L, acervoDomCasmurro);

        this.apresentarBiblioteca(biblioteca);
        System.out.println(leitor);
        System.out.println(bibliotecario);
        System.out.println(administrador);
        System.out.println(reserva);
        bibliotecario.confirmar(reserva);

        System.out.println("\nApós a confirmação:");
        System.out.println(reserva);
        System.out.println(acervoDomCasmurro);
    }

    private void apresentarBiblioteca(final Biblioteca biblioteca) {
        System.out.println("\n=== Biblioteca Fácil - Dados iniciais ===");
        System.out.println(biblioteca);
        for (final Acervo acervo : biblioteca.getAcervos()) {
            System.out.println(acervo);
            System.out.println(acervo.getLivro());
        }
    }
}
