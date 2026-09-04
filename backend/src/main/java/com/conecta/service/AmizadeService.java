package com.conecta.service;

import com.conecta.dto.SolicitacaoAmizadeResponse;
import com.conecta.dto.UsuarioResponse;
import com.conecta.entity.Amizade;
import com.conecta.entity.SolicitacaoAmizade;
import com.conecta.entity.StatusSolicitacao;
import com.conecta.entity.TipoNotificacao;
import com.conecta.entity.Usuario;
import com.conecta.exception.AcessoNegadoException;
import com.conecta.exception.RecursoNaoEncontradoException;
import com.conecta.mapper.UsuarioMapper;
import com.conecta.repository.AmizadeRepository;
import com.conecta.repository.SolicitacaoAmizadeRepository;
import com.conecta.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AmizadeService {

	private final AmizadeRepository amizadeRepository;
	private final SolicitacaoAmizadeRepository solicitacaoRepository;
	private final UsuarioRepository usuarioRepository;
	private final UsuarioService usuarioService;
	private final UsuarioMapper usuarioMapper;
	private final NotificacaoService notificacaoService;

	@Transactional(readOnly = true)
	public List<UsuarioResponse> pesquisar(String emailAtual, String query) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		return usuarioRepository.buscarPorNomeOuEmail(query.trim()).stream()
				.filter(u -> !u.getId().equals(eu.getId()))
				.map(usuarioMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponse> listarAmigos(String emailAtual) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		return amizadeRepository.findByUsuarioOrderByDataCriacaoDesc(eu).stream()
				.map(Amizade::getAmigo)
				.map(usuarioMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public long contarAmigos(String emailAtual) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		return amizadeRepository.countByUsuario(eu);
	}

	@Transactional
	public SolicitacaoAmizadeResponse enviarSolicitacao(String emailAtual, Long destinatarioId) {
		Usuario remetente = usuarioService.buscarEntidadePorEmail(emailAtual);
		Usuario destinatario = usuarioService.buscarEntidadePorId(destinatarioId);

		if (remetente.getId().equals(destinatario.getId())) {
			throw new IllegalArgumentException("Você não pode adicionar a si mesmo");
		}
		if (amizadeRepository.saoAmigos(remetente, destinatario)) {
			throw new IllegalArgumentException("Vocês já são amigos");
		}
		if (solicitacaoRepository.findEntreUsuarios(remetente, destinatario, StatusSolicitacao.PENDENTE).isPresent()) {
			throw new IllegalArgumentException("Já existe uma solicitação pendente");
		}

		SolicitacaoAmizade solicitacao = solicitacaoRepository
				.findByRemetenteAndDestinatario(remetente, destinatario)
				.orElseGet(() -> SolicitacaoAmizade.builder()
						.remetente(remetente)
						.destinatario(destinatario)
						.build());
		solicitacao.setStatus(StatusSolicitacao.PENDENTE);
		SolicitacaoAmizade salva = solicitacaoRepository.save(solicitacao);

		notificacaoService.criar(
				destinatario,
				remetente,
				TipoNotificacao.SOLICITACAO_AMIZADE,
				remetente.getNome() + " enviou uma solicitação de amizade",
				salva.getId());

		return toResponse(salva);
	}

	@Transactional(readOnly = true)
	public List<SolicitacaoAmizadeResponse> listarPendentesRecebidas(String emailAtual) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		return solicitacaoRepository
				.findByDestinatarioAndStatusOrderByDataCriacaoDesc(eu, StatusSolicitacao.PENDENTE)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<SolicitacaoAmizadeResponse> listarPendentesEnviadas(String emailAtual) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		return solicitacaoRepository
				.findByRemetenteAndStatusOrderByDataCriacaoDesc(eu, StatusSolicitacao.PENDENTE)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public SolicitacaoAmizadeResponse aceitar(String emailAtual, Long solicitacaoId) {
		SolicitacaoAmizade solicitacao = buscarPendenteDoDestinatario(emailAtual, solicitacaoId);
		solicitacao.setStatus(StatusSolicitacao.ACEITA);
		criarAmizadeBidirecional(solicitacao.getRemetente(), solicitacao.getDestinatario());

		notificacaoService.criar(
				solicitacao.getRemetente(),
				solicitacao.getDestinatario(),
				TipoNotificacao.NOVA_AMIZADE,
				solicitacao.getDestinatario().getNome() + " aceitou sua solicitação de amizade",
				solicitacao.getDestinatario().getId());

		return toResponse(solicitacaoRepository.save(solicitacao));
	}

	@Transactional
	public SolicitacaoAmizadeResponse recusar(String emailAtual, Long solicitacaoId) {
		SolicitacaoAmizade solicitacao = buscarPendenteDoDestinatario(emailAtual, solicitacaoId);
		solicitacao.setStatus(StatusSolicitacao.RECUSADA);
		return toResponse(solicitacaoRepository.save(solicitacao));
	}

	@Transactional
	public void removerAmigo(String emailAtual, Long amigoId) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		Usuario amigo = usuarioService.buscarEntidadePorId(amigoId);
		amizadeRepository.findByUsuarioAndAmigo(eu, amigo).ifPresent(amizadeRepository::delete);
		amizadeRepository.findByUsuarioAndAmigo(amigo, eu).ifPresent(amizadeRepository::delete);
	}

	private SolicitacaoAmizade buscarPendenteDoDestinatario(String emailAtual, Long solicitacaoId) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		SolicitacaoAmizade solicitacao = solicitacaoRepository.findById(solicitacaoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada"));
		if (!solicitacao.getDestinatario().getId().equals(eu.getId())) {
			throw new AcessoNegadoException("Você não pode gerenciar esta solicitação");
		}
		if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
			throw new IllegalArgumentException("Esta solicitação já foi respondida");
		}
		return solicitacao;
	}

	private void criarAmizadeBidirecional(Usuario a, Usuario b) {
		if (!amizadeRepository.existsByUsuarioAndAmigo(a, b)) {
			amizadeRepository.save(Amizade.builder().usuario(a).amigo(b).build());
		}
		if (!amizadeRepository.existsByUsuarioAndAmigo(b, a)) {
			amizadeRepository.save(Amizade.builder().usuario(b).amigo(a).build());
		}
	}

	private SolicitacaoAmizadeResponse toResponse(SolicitacaoAmizade s) {
		return SolicitacaoAmizadeResponse.builder()
				.id(s.getId())
				.remetente(usuarioMapper.toResponse(s.getRemetente()))
				.destinatario(usuarioMapper.toResponse(s.getDestinatario()))
				.status(s.getStatus())
				.dataCriacao(s.getDataCriacao())
				.build();
	}
}
