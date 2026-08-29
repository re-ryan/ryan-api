package br.com.infnet.bibliotecafacil.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ReservaTest {

    private final String senhaHash = "hash-seguro-do-usuario";

    @Test
    public void deveCriarReservaPendenteEReservarUmaUnidade() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 2);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");

        final Reserva reserva = leitor.reservar(5L, acervo);

        assertEquals(StatusReserva.PENDENTE, reserva.getStatus());
        assertEquals(1, acervo.getQuantidadeDisponivel());
        assertEquals(1, leitor.getReservas().size());
        assertNotNull(reserva.getDataReserva());
    }

    @Test
    public void deveConfirmarReservaPendente() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Bibliotecario bibliotecario = criarBibliotecario(biblioteca);
        final Reserva reserva = leitor.reservar(6L, acervo);

        bibliotecario.confirmar(reserva);

        assertEquals(StatusReserva.CONFIRMADA, reserva.getStatus());
        assertEquals(0, acervo.getQuantidadeDisponivel());
    }

    @Test
    public void deveLiberarUnidadeAoRejeitarReserva() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Bibliotecario bibliotecario = criarBibliotecario(biblioteca);
        final Reserva reserva = leitor.reservar(6L, acervo);

        bibliotecario.rejeitar(reserva);

        assertEquals(StatusReserva.REJEITADA, reserva.getStatus());
        assertEquals(1, acervo.getQuantidadeDisponivel());
    }

    @Test
    public void naoDeveProcessarReservaDuasVezes() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Bibliotecario bibliotecario = criarBibliotecario(biblioteca);
        final Reserva reserva = leitor.reservar(6L, acervo);
        bibliotecario.confirmar(reserva);

        assertThrows(IllegalStateException.class, () -> bibliotecario.rejeitar(reserva));
        assertEquals(0, acervo.getQuantidadeDisponivel());
    }

    @Test
    public void leitorInativoNaoDeveReservar() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        leitor.desativar();

        assertThrows(IllegalStateException.class, () -> leitor.reservar(5L, acervo));
        assertEquals(1, acervo.getQuantidadeDisponivel());
    }

    @Test
    public void naoDeveReservarQuandoNaoHaDisponibilidade() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor primeiroLeitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Leitor segundoLeitor = criarLeitor(5L, "Bruno Souza", "bruno@email.com");
        primeiroLeitor.reservar(6L, acervo);

        assertThrows(IllegalStateException.class, () -> segundoLeitor.reservar(7L, acervo));
        assertEquals(0, acervo.getQuantidadeDisponivel());
    }

    @Test
    public void leitorNaoDeveDuplicarReservaPendenteParaMesmoAcervo() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 2);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        leitor.reservar(5L, acervo);

        assertThrows(IllegalStateException.class, () -> leitor.reservar(6L, acervo));
        assertEquals(1, leitor.getReservas().size());
        assertEquals(1, acervo.getQuantidadeDisponivel());
    }

    @Test
    public void leitorPodeReservarMesmoLivroEmBibliotecasDiferentes() {
        final Livro livro = criarLivro();
        final Biblioteca primeiraBiblioteca = criarBiblioteca(2L, "Biblioteca Central");
        final Biblioteca segundaBiblioteca = criarBiblioteca(3L, "Biblioteca Bairro");
        final Acervo primeiroAcervo = primeiraBiblioteca.adicionarLivro(4L, livro, 1);
        final Acervo segundoAcervo = segundaBiblioteca.adicionarLivro(5L, livro, 1);
        final Leitor leitor = criarLeitor(6L, "Ana Souza", "ana@email.com");

        leitor.reservar(7L, primeiroAcervo);
        leitor.reservar(8L, segundoAcervo);

        assertEquals(2, leitor.getReservas().size());
        assertEquals(0, primeiroAcervo.getQuantidadeDisponivel());
        assertEquals(0, segundoAcervo.getQuantidadeDisponivel());
    }

    @Test
    public void bibliotecarioDeOutraBibliotecaNaoDeveProcessarReserva() {
        final Biblioteca bibliotecaDaReserva = criarBiblioteca(1L, "Biblioteca Central");
        final Biblioteca outraBiblioteca = criarBiblioteca(2L, "Biblioteca Bairro");
        final Acervo acervo = criarAcervo(bibliotecaDaReserva, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Bibliotecario bibliotecario = criarBibliotecario(outraBiblioteca);
        final Reserva reserva = leitor.reservar(6L, acervo);

        assertThrows(IllegalArgumentException.class, () -> bibliotecario.confirmar(reserva));
        assertEquals(StatusReserva.PENDENTE, reserva.getStatus());
    }

    @Test
    public void bibliotecarioInativoNaoDeveProcessarReserva() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Bibliotecario bibliotecario = criarBibliotecario(biblioteca);
        final Reserva reserva = leitor.reservar(6L, acervo);
        bibliotecario.desativar();

        assertThrows(IllegalStateException.class, () -> bibliotecario.confirmar(reserva));
        assertEquals(StatusReserva.PENDENTE, reserva.getStatus());
    }

    @Test
    public void bibliotecaInativaNaoDeveProcessarReserva() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Bibliotecario bibliotecario = criarBibliotecario(biblioteca);
        final Reserva reserva = leitor.reservar(6L, acervo);
        biblioteca.desativar();

        assertThrows(IllegalStateException.class, () -> bibliotecario.confirmar(reserva));
        assertEquals(StatusReserva.PENDENTE, reserva.getStatus());
    }

    @Test
    public void colecaoDeReservasDoLeitorDeveSerImutavel() {
        final Biblioteca biblioteca = criarBiblioteca(1L, "Biblioteca Central");
        final Acervo acervo = criarAcervo(biblioteca, 1);
        final Leitor leitor = criarLeitor(4L, "Ana Souza", "ana@email.com");
        final Reserva reserva = leitor.reservar(5L, acervo);

        assertThrows(UnsupportedOperationException.class,
                () -> leitor.getReservas().add(reserva));
        assertEquals(1, leitor.getReservas().size());
    }

    private Biblioteca criarBiblioteca(final Long id, final String nome) {
        final Endereco endereco = new Endereco();
        endereco.setId(id);
        endereco.setCep("20040020");
        endereco.setLogradouro("Rua Principal");
        endereco.setNumero("10");
        endereco.setComplemento(null);
        endereco.setBairro("Centro");
        endereco.setCidade("Rio de Janeiro");
        endereco.setUf("RJ");
        endereco.setLatitude(-22.9);
        endereco.setLongitude(-43.2);

        final Biblioteca biblioteca = new Biblioteca();
        biblioteca.setId(id);
        biblioteca.setNome(nome);
        biblioteca.setCpfCnpj("%014d".formatted(id));
        biblioteca.setEmail("biblioteca%s@teste.com".formatted(id));
        biblioteca.setTelefone(null);
        biblioteca.setEndereco(endereco);
        return biblioteca;
    }

    private Acervo criarAcervo(final Biblioteca biblioteca, final int quantidade) {
        return biblioteca.adicionarLivro(3L, this.criarLivro(), quantidade);
    }

    private Leitor criarLeitor(final Long id, final String nome, final String email) {
        final Leitor leitor = new Leitor();
        leitor.setId(id);
        leitor.setNomeCompleto(nome);
        leitor.setDataNascimento(null);
        leitor.setLogin("leitor" + id);
        leitor.setEmail(email);
        leitor.setSenhaHash(this.senhaHash);
        leitor.setTipoUsuario(TipoUsuario.LEITOR);
        return leitor;
    }

    private Bibliotecario criarBibliotecario(final Biblioteca biblioteca) {
        final Bibliotecario bibliotecario = new Bibliotecario();
        bibliotecario.setId(5L);
        bibliotecario.setNomeCompleto("Carlos Lima");
        bibliotecario.setDataNascimento(null);
        bibliotecario.setLogin("carlos.lima");
        bibliotecario.setEmail("carlos@biblioteca.com");
        bibliotecario.setSenhaHash(this.senhaHash);
        bibliotecario.setTipoUsuario(TipoUsuario.BIBLIOTECARIO);
        bibliotecario.setBiblioteca(biblioteca);
        return bibliotecario;
    }

    private Livro criarLivro() {
        final Livro livro = new Livro();
        livro.setId(2L);
        livro.setTitulo("Dom Casmurro");
        livro.setIsbn(null, "9788532508126");
        return livro;
    }
}
