package br.com.infnet.bibliotecafacil.aplicacao.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(UsuarioService.class)
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void deveExecutarOperacoesCrudComPolimorfismo() {
        final Leitor leitor = (Leitor) this.usuarioService.incluir(
                this.criarLeitor(null, "Ana Souza", "ana.souza", "ana@email.com"));
        final Long id = leitor.getId();

        assertNotNull(id);
        assertEquals(leitor, this.usuarioService.obterPorId(id));
        assertInstanceOf(Leitor.class, this.usuarioService.obterPorId(id));

        final Leitor leitorAlterado = this.criarLeitor(id, "Ana Souza Lima", "ana.souza", "ana@email.com");
        final Usuario usuarioSalvo = this.usuarioService.alterar(leitorAlterado);
        assertEquals("Ana Souza Lima", usuarioSalvo.getNomeCompleto());
        assertInstanceOf(Leitor.class, this.usuarioService.obterPorId(id));

        this.usuarioService.excluir(id);
        assertEquals(List.of(), this.usuarioService.listar());
    }

    @Test
    public void naoDeveAceitarDadosInvalidos() {
        final Leitor comId = this.criarLeitor(1L, "Ana Souza", "ana.souza", "ana@email.com");
        final Leitor semNome = this.criarLeitor(null, " ", "ana.souza", "ana@email.com");

        assertAll(
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.incluir(null)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.incluir(comId)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.incluir(semNome)),
                () -> assertThrows(DadosInvalidosException.class, () -> this.usuarioService.filtrarPorTipo(null)));
    }

    @Test
    public void naoDeveAceitarLoginOuEmailDuplicado() {
        this.usuarioService.incluir(this.criarLeitor(null, "Ana Souza", "ana.souza", "ana@email.com"));

        final Leitor loginDuplicado = this.criarLeitor(null, "Bruno Lima", "ANA.SOUZA", "bruno@email.com");
        final Leitor emailDuplicado = this.criarLeitor(null, "Carla Dias", "carla.dias", "ANA@EMAIL.COM");

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
        final Leitor ana = this.criarLeitor(null, "Ana Souza", "ana.souza", "ana@email.com");
        final Administrador marina = this.criarAdministrador(null, "Marina Alves", "marina.alves", "marina@email.com");
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
        this.usuarioService.incluir(this.criarLeitor(null, "Ana Souza", "ana.souza", "ana@email.com"));

        assertThrows(UnsupportedOperationException.class,
                () -> this.usuarioService.listar().add(this.criarLeitor(null, "Bruno Lima", "bruno.lima", "bruno@email.com")));
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
