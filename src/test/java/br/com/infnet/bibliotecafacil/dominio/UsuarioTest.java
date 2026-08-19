package br.com.infnet.bibliotecafacil.dominio;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    private final String senhaHash = "hash-seguro-do-usuario";

    @Test
    public void deveRepresentarOsCamposDaV1NoLeitor() {
        final LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        final Leitor leitor = criarLeitor(
                1L,
                "Ana Souza",
                dataNascimento,
                "ana.souza",
                "ana@email.com",
                this.senhaHash);

        assertAll(
                () -> assertEquals(1L, leitor.getId()),
                () -> assertEquals("Ana Souza", leitor.getNomeCompleto()),
                () -> assertEquals(dataNascimento, leitor.getDataNascimento()),
                () -> assertEquals("ana.souza", leitor.getLogin()),
                () -> assertEquals("ana@email.com", leitor.getEmail()),
                () -> assertEquals(this.senhaHash, leitor.getSenhaHash()),
                () -> assertEquals(TipoUsuario.LEITOR, leitor.getTipoUsuario()),
                () -> assertTrue(leitor.isAtivo()),
                () -> assertNotNull(leitor.getDataCriacao()),
                () -> assertEquals(leitor.getDataCriacao(), leitor.getDataAtualizacao()));
    }

    @Test
    public void devePermitirDataNascimentoAusente() {
        final Leitor leitor = criarLeitor(1L, null);

        assertNull(leitor.getDataNascimento());
    }

    @Test
    public void deveRepresentarCadaTipoDeUsuario() {
        final Biblioteca biblioteca = criarBiblioteca();
        final Leitor leitor = criarLeitor(1L, LocalDate.of(1990, 5, 20));
        final Bibliotecario bibliotecario = criarBibliotecario(
                2L,
                "Carlos Lima",
                LocalDate.of(1985, 3, 10),
                "carlos.lima",
                "carlos@biblioteca.com",
                this.senhaHash,
                biblioteca);
        final Administrador administrador = criarAdministrador(
                3L,
                "Marina Alves",
                null,
                "marina.alves",
                "marina@bibliotecafacil.com",
                this.senhaHash);

        assertAll(
                () -> assertEquals(TipoUsuario.LEITOR, leitor.getTipoUsuario()),
                () -> assertEquals(TipoUsuario.BIBLIOTECARIO, bibliotecario.getTipoUsuario()),
                () -> assertEquals(TipoUsuario.ADMINISTRADOR, administrador.getTipoUsuario()),
                () -> assertEquals(biblioteca, bibliotecario.getBiblioteca()));
    }

    @Test
    public void bibliotecarioDevePossuirBiblioteca() {
        assertThrows(NullPointerException.class, () -> criarBibliotecario(
                1L,
                "Carlos Lima",
                null,
                "carlos.lima",
                "carlos@biblioteca.com",
                this.senhaHash,
                null));
    }

    @Test
    public void deveAtualizarTimestampAoAlterarEstado() throws InterruptedException {
        final Leitor leitor = criarLeitor(1L, null);
        final LocalDateTime dataCriacao = leitor.getDataCriacao();
        final LocalDateTime atualizacaoInicial = leitor.getDataAtualizacao();

        Thread.sleep(2L);
        leitor.desativar();
        final LocalDateTime atualizacaoDaDesativacao = leitor.getDataAtualizacao();

        Thread.sleep(2L);
        leitor.ativar();

        assertAll(
                () -> assertEquals(dataCriacao, leitor.getDataCriacao()),
                () -> assertTrue(atualizacaoDaDesativacao.isAfter(atualizacaoInicial)),
                () -> assertTrue(leitor.getDataAtualizacao().isAfter(atualizacaoDaDesativacao)),
                () -> assertTrue(leitor.isAtivo()));
    }

    @Test
    public void naoDeveExporSenhaHashNoToString() {
        final Leitor leitor = criarLeitor(1L, null);

        assertFalse(leitor.toString().contains(this.senhaHash));
        assertTrue(leitor.toString().contains("ana.souza"));
    }

    private Leitor criarLeitor(final Long id, final LocalDate dataNascimento) {
        return this.criarLeitor(id, "Ana Souza", dataNascimento, "ana.souza", "ana@email.com", this.senhaHash);
    }

    private Leitor criarLeitor(final Long id, final String nomeCompleto, final LocalDate dataNascimento, final String login, final String email, final String senhaHash) {
        final Leitor leitor = new Leitor();
        this.preencherUsuario(leitor, id, nomeCompleto, dataNascimento, login, email, senhaHash);
        leitor.setTipoUsuario(TipoUsuario.LEITOR);
        return leitor;
    }

    private Bibliotecario criarBibliotecario(final Long id, final String nomeCompleto, final LocalDate dataNascimento, final String login, final String email, final String senhaHash, final Biblioteca biblioteca) {
        final Bibliotecario bibliotecario = new Bibliotecario();
        this.preencherUsuario(bibliotecario, id, nomeCompleto, dataNascimento, login, email, senhaHash);
        bibliotecario.setTipoUsuario(TipoUsuario.BIBLIOTECARIO);
        bibliotecario.setBiblioteca(biblioteca);
        return bibliotecario;
    }

    private Administrador criarAdministrador(final Long id, final String nomeCompleto, final LocalDate dataNascimento, final String login, final String email, final String senhaHash) {
        final Administrador administrador = new Administrador();
        this.preencherUsuario(administrador, id, nomeCompleto, dataNascimento, login, email, senhaHash);
        administrador.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        return administrador;
    }

    private void preencherUsuario(final Usuario usuario, final Long id, final String nomeCompleto, final LocalDate dataNascimento, final String login, final String email, final String senhaHash) {
        usuario.setId(id);
        usuario.setNomeCompleto(nomeCompleto);
        usuario.setDataNascimento(dataNascimento);
        usuario.setLogin(login);
        usuario.setEmail(email);
        usuario.setSenhaHash(senhaHash);
    }

    private Biblioteca criarBiblioteca() {
        final Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("20040020");
        endereco.setLogradouro("Rua Principal");
        endereco.setNumero("10");
        endereco.setComplemento("");
        endereco.setBairro("Centro");
        endereco.setCidade("Rio de Janeiro");
        endereco.setUf("RJ");
        endereco.setLatitude(-22.9);
        endereco.setLongitude(-43.2);

        final Biblioteca biblioteca = new Biblioteca();
        biblioteca.setId(10L);
        biblioteca.setNome("Biblioteca Central");
        biblioteca.setCpfCnpj("12345678000199");
        biblioteca.setEmail("contato@biblioteca.com");
        biblioteca.setTelefone(null);
        biblioteca.setEndereco(endereco);
        return biblioteca;
    }

}
