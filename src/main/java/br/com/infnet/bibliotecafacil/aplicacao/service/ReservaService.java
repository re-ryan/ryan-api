package br.com.infnet.bibliotecafacil.aplicacao.service;

import br.com.infnet.bibliotecafacil.aplicacao.exception.DadosInvalidosException;
import br.com.infnet.bibliotecafacil.aplicacao.exception.ObjetoNaoEncontradoException;
import br.com.infnet.bibliotecafacil.dominio.Acervo;
import br.com.infnet.bibliotecafacil.dominio.Bibliotecario;
import br.com.infnet.bibliotecafacil.dominio.Leitor;
import br.com.infnet.bibliotecafacil.dominio.Reserva;
import br.com.infnet.bibliotecafacil.dominio.Usuario;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.AcervoRepository;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.ReservaRepository;
import br.com.infnet.bibliotecafacil.infraestrutura.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcervoRepository acervoRepository;

    public ReservaService(
            final ReservaRepository reservaRepository,
            final UsuarioRepository usuarioRepository,
            final AcervoRepository acervoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.acervoRepository = acervoRepository;
    }

    @Transactional
    public Reserva solicitar(final Long leitorId, final Long acervoId) {
        final Leitor leitor = this.obterLeitor(leitorId);
        final Acervo acervo = this.obterAcervo(acervoId);
        final Reserva reserva = leitor.reservar(acervo);
        return this.reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva confirmar(final Long reservaId, final Long bibliotecarioId) {
        final Reserva reserva = this.obterPorId(reservaId);
        final Bibliotecario bibliotecario = this.obterBibliotecario(bibliotecarioId);
        bibliotecario.confirmar(reserva);
        return this.reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva rejeitar(final Long reservaId, final Long bibliotecarioId) {
        final Reserva reserva = this.obterPorId(reservaId);
        final Bibliotecario bibliotecario = this.obterBibliotecario(bibliotecarioId);
        bibliotecario.rejeitar(reserva);
        return this.reservaRepository.save(reserva);
    }

    public Reserva obterPorId(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador da reserva é obrigatório.");
        }
        return this.reservaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Reserva não encontrada para o identificador %s.".formatted(id)));
    }

    public List<Reserva> listar() {
        return List.copyOf(this.reservaRepository.findAll());
    }

    private Leitor obterLeitor(final Long id) {
        final Usuario usuario = this.obterUsuario(id);
        if (usuario instanceof Leitor leitor) {
            return leitor;
        }
        throw new DadosInvalidosException("O usuário informado não é um leitor.");
    }

    private Bibliotecario obterBibliotecario(final Long id) {
        final Usuario usuario = this.obterUsuario(id);
        if (usuario instanceof Bibliotecario bibliotecario) {
            return bibliotecario;
        }
        throw new DadosInvalidosException("O usuário informado não é um bibliotecário.");
    }

    private Usuario obterUsuario(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do usuário é obrigatório.");
        }
        return this.usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Usuário não encontrado para o identificador %s.".formatted(id)));
    }

    private Acervo obterAcervo(final Long id) {
        if (id == null) {
            throw new DadosInvalidosException("O identificador do acervo é obrigatório.");
        }
        return this.acervoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        "Acervo não encontrado para o identificador %s.".formatted(id)));
    }
}
