package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.OperacaoNaoPermitidaException;
import br.com.infnet.bibliotecafacil.dominio.Administrador;
import br.com.infnet.bibliotecafacil.dominio.Leitor;
import br.com.infnet.bibliotecafacil.dominio.TipoUsuario;
import br.com.infnet.bibliotecafacil.dominio.Usuario;
import java.util.List;
import org.junit.jupiter.api.Test;

class UsuarioServiceTest {

    private final UsuarioService usuarioService = new UsuarioService();

    @Test
    public void deveExecutarOperacoesCrudComPolimorfismo() {
        final Leitor leitor = this.criarLeitor(1L, "Ana Souza", "ana.souza", "ana@email.com");
        this.usuarioService.incluir(leitor);

        assertSame(leitor, this.usuarioService.obterPorId(1L));
        assertInstanceOf(Leitor.class, this.usuarioService.obterPorId(1L));

        final Administrador administrador = this.criarAdministrador(1L, "Ana Souza", "ana.souza", "ana@email.com");
        this.usuarioService.alterar(administrador);
        assertSame(administrador, this.usuarioService.obterPorId(1L));

        this.usuarioService.excluir(1L);
        assertEquals(List.of(), this.usuarioService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidos() {
        final Leitor semId = this.criarLeitor(null, "Ana Souza", "ana.souza", "ana@email.com");
        final Leitor semNome = this.criarLeitor(1L, " ", "ana.souza", "ana@email.com");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.incluir(null)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.incluir(semId)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.incluir(semNome)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.filtrarPorTipo(null)));
    }

    @Test
    public void naoDeveAceitarLoginOuEmailDuplicado() {
        this.usuarioService.incluir(this.criarLeitor(1L, "Ana Souza", "ana.souza", "ana@email.com"));

        final Leitor loginDuplicado = this.criarLeitor(2L, "Bruno Lima", "ANA.SOUZA", "bruno@email.com");
        final Leitor emailDuplicado = this.criarLeitor(3L, "Carla Dias", "carla.dias", "ANA@EMAIL.COM");

        assertAll(
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.usuarioService.incluir(loginDuplicado)),
                () -> assertThrows(OperacaoNaoPermitidaException.class, () -> this.usuarioService.incluir(emailDuplicado)));
    }

    @Test
    public void deveTratarUsuarioInexistente() {
        final Leitor leitor = this.criarLeitor(99L, "Ana Souza", "ana.souza", "ana@email.com");

        assertAll(
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.usuarioService.obterPorId(99L)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.usuarioService.alterar(leitor)),
                () -> assertThrows(ObjetoNaoEncontradoException.class, () -> this.usuarioService.excluir(99L)));
    }

    @Test
    public void deveFiltrarBuscarOrdenarETransformarUsuarios() {
        final Leitor ana = this.criarLeitor(1L, "Ana Souza", "ana.souza", "ana@email.com");
        final Administrador marina = this.criarAdministrador(2L, "Marina Alves", "marina.alves", "marina@email.com");
        marina.desativar();
        this.usuarioService.incluir(marina);
        this.usuarioService.incluir(ana);

        assertAll(
                () -> assertEquals(List.of(ana), this.usuarioService.listarAtivos()),
                () -> assertEquals(List.of(ana), this.usuarioService.filtrarPorTipo(TipoUsuario.LEITOR)),
                () -> assertEquals(List.of(marina), this.usuarioService.buscarPorNome("ALVES")),
                () -> assertEquals(List.of(ana, marina), this.usuarioService.listarOrdenadosPorNome()),
                () -> assertEquals(List.of("marina@email.com", "ana@email.com"), this.usuarioService.listarEmails()));
    }

    @Test
    public void naoDeveExporAListaInterna() {
        this.usuarioService.incluir(this.criarLeitor(1L, "Ana Souza", "ana.souza", "ana@email.com"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.usuarioService.listar().add(this.criarLeitor(2L, "Bruno Lima", "bruno.lima", "bruno@email.com")));
    }

    private Leitor criarLeitor(final Long id, final String nome, final String login, final String email) {
        final Leitor leitor = new Leitor();
        this.preencherUsuario(leitor, id, nome, login, email);
        leitor.setTipoUsuario(TipoUsuario.LEITOR);
        return leitor;
    }

    private Administrador criarAdministrador(final Long id, final String nome, final String login, final String email) {
        final Administrador administrador = new Administrador();
        this.preencherUsuario(administrador, id, nome, login, email);
        administrador.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        return administrador;
    }

    private void preencherUsuario(final Usuario usuario, final Long id, final String nome, final String login, final String email) {
        usuario.setId(id);
        usuario.setNomeCompleto(nome);
        usuario.setLogin(login);
        usuario.setEmail(email);
        usuario.setSenhaHash("hash-seguro");
    }

}
