package com.conecta.service;

import com.conecta.dto.NotificacaoResponse;
import com.conecta.dto.UsuarioResponse;
import com.conecta.entity.Notificacao;
import com.conecta.entity.SolicitacaoAmizade;
import com.conecta.entity.TipoNotificacao;
import com.conecta.entity.Usuario;
import com.conecta.exception.AcessoNegadoException;
import com.conecta.exception.RecursoNaoEncontradoException;
import com.conecta.mapper.UsuarioMapper;
import com.conecta.repository.NotificacaoRepository;
import com.conecta.repository.SolicitacaoAmizadeRepository;
import com.conecta.repository.UsuarioRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

	private final NotificacaoRepository notificacaoRepository;
	private final SolicitacaoAmizadeRepository solicitacaoAmizadeRepository;
	private final UsuarioRepository usuarioRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final UsuarioMapper usuarioMapper;

	@Transactional
	public Notificacao criar(
			Usuario usuario,
			Usuario origemUsuario,
			TipoNotificacao tipo,
			String mensagem,
			Long referenciaId) {
		Notificacao notificacao = Notificacao.builder()
				.usuario(usuario)
				.origemUsuario(origemUsuario)
				.tipo(tipo)
				.mensagem(mensagem)
				.lida(false)
				.referenciaId(referenciaId)
				.build();
		Notificacao salva = notificacaoRepository.save(notificacao);
		NotificacaoResponse response = toResponse(salva);

		messagingTemplate.convertAndSendToUser(
				usuario.getEmail(),
				"/queue/notificacoes",
				response);

		Map<String, Object> toast = new HashMap<>();
		toast.put("tipo", tipo.name());
		toast.put("mensagem", mensagem);
		toast.put("referenciaId", referenciaId == null ? 0 : referenciaId);
		if (origemUsuario != null) {
			toast.put("origemUsuario", usuarioMapper.toResponse(origemUsuario));
		}
		messagingTemplate.convertAndSendToUser(usuario.getEmail(), "/queue/toast", toast);
		return salva;
	}

	@Transactional
	public List<NotificacaoResponse> listar(Usuario usuario) {
		List<Notificacao> lista = notificacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario);
		lista.forEach(this::preencherOrigemSeNecessario);
		return lista.stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public long contarNaoLidas(Usuario usuario) {
		return notificacaoRepository.countByUsuarioAndLidaFalse(usuario);
	}

	@Transactional
	public NotificacaoResponse marcarLida(Long id, Usuario usuario) {
		Notificacao notificacao = notificacaoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Notificação não encontrada"));
		if (!notificacao.getUsuario().getId().equals(usuario.getId())) {
			throw new AcessoNegadoException("Você não pode acessar esta notificação");
		}
		notificacao.setLida(true);
		preencherOrigemSeNecessario(notificacao);
		return toResponse(notificacaoRepository.save(notificacao));
	}

	@Transactional
	public void marcarTodasLidas(Usuario usuario) {
		notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataHoraDesc(usuario)
				.forEach(n -> n.setLida(true));
	}

	private void preencherOrigemSeNecessario(Notificacao n) {
		if (n.getOrigemUsuario() != null || n.getReferenciaId() == null || n.getTipo() == null) {
			return;
		}

		Usuario origem = resolverOrigem(n.getTipo(), n.getReferenciaId());
		if (origem != null) {
			n.setOrigemUsuario(origem);
			notificacaoRepository.save(n);
		}
	}

	private Usuario resolverOrigem(TipoNotificacao tipo, Long referenciaId) {
		return switch (tipo) {
			case NOVA_MENSAGEM, NOVA_AMIZADE, CURTIDA, COMENTARIO ->
					usuarioRepository.findById(referenciaId).orElse(null);
			case SOLICITACAO_AMIZADE -> solicitacaoAmizadeRepository.findById(referenciaId)
					.map(SolicitacaoAmizade::getRemetente)
					.orElseGet(() -> usuarioRepository.findById(referenciaId).orElse(null));
		};
	}

	private NotificacaoResponse toResponse(Notificacao n) {
		UsuarioResponse origem = null;
		if (n.getOrigemUsuario() != null) {
			origem = usuarioMapper.toResponse(n.getOrigemUsuario());
		} else if (n.getReferenciaId() != null && n.getTipo() != null) {
			Usuario resolvido = resolverOrigem(n.getTipo(), n.getReferenciaId());
			if (resolvido != null) {
				origem = usuarioMapper.toResponse(resolvido);
			}
		}

		return NotificacaoResponse.builder()
				.id(n.getId())
				.tipo(n.getTipo())
				.mensagem(n.getMensagem())
				.lida(n.isLida())
				.referenciaId(n.getReferenciaId())
				.dataHora(n.getDataHora())
				.origemUsuario(origem)
				.build();
	}
}
