package com.conecta.service;

import com.conecta.dto.ConversaResponse;
import com.conecta.dto.MensagemRequest;
import com.conecta.dto.MensagemResponse;
import com.conecta.entity.Mensagem;
import com.conecta.entity.StatusMensagem;
import com.conecta.entity.TipoNotificacao;
import com.conecta.entity.Usuario;
import com.conecta.exception.AcessoNegadoException;
import com.conecta.exception.RecursoNaoEncontradoException;
import com.conecta.mapper.UsuarioMapper;
import com.conecta.repository.AmizadeRepository;
import com.conecta.repository.MensagemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MensagemService {

	private final MensagemRepository mensagemRepository;
	private final AmizadeRepository amizadeRepository;
	private final UsuarioService usuarioService;
	private final UsuarioMapper usuarioMapper;
	private final NotificacaoService notificacaoService;
	private final SimpMessagingTemplate messagingTemplate;

	@Transactional(readOnly = true)
	public List<ConversaResponse> listarConversas(String emailAtual) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		return mensagemRepository.findUltimasPorConversa(eu).stream()
				.map(m -> {
					Usuario outro = m.getRemetente().getId().equals(eu.getId())
							? m.getDestinatario()
							: m.getRemetente();
					return ConversaResponse.builder()
							.usuario(usuarioMapper.toResponse(outro))
							.ultimaMensagem(m.getConteudo())
							.horario(m.getDataHora())
							.naoLidas(mensagemRepository.countNaoLidas(eu, outro))
							.build();
				})
				.toList();
	}

	@Transactional(readOnly = true)
	public List<MensagemResponse> listarConversa(String emailAtual, Long amigoId) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		Usuario outro = usuarioService.buscarEntidadePorId(amigoId);
		garantirAmizade(eu, outro);
		return mensagemRepository.findConversa(eu, outro).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public MensagemResponse enviar(String emailAtual, Long destinatarioId, MensagemRequest request) {
		Usuario remetente = usuarioService.buscarEntidadePorEmail(emailAtual);
		Usuario destinatario = usuarioService.buscarEntidadePorId(destinatarioId);
		garantirAmizade(remetente, destinatario);

		Mensagem mensagem = Mensagem.builder()
				.remetente(remetente)
				.destinatario(destinatario)
				.conteudo(request.getConteudo().trim())
				.status(StatusMensagem.ENVIADA)
				.build();
		Mensagem salva = mensagemRepository.save(mensagem);
		MensagemResponse response = toResponse(salva);

		messagingTemplate.convertAndSendToUser(destinatario.getEmail(), "/queue/mensagens", response);
		messagingTemplate.convertAndSendToUser(remetente.getEmail(), "/queue/mensagens", response);

		notificacaoService.criar(
				destinatario,
				remetente,
				TipoNotificacao.NOVA_MENSAGEM,
				"Você recebeu uma nova mensagem de " + remetente.getNome() + ".",
				remetente.getId());

		return response;
	}

	@Transactional
	public MensagemResponse editar(String emailAtual, Long mensagemId, MensagemRequest request) {
		Mensagem mensagem = buscarPropria(emailAtual, mensagemId);
		mensagem.setConteudo(request.getConteudo().trim());
		mensagem.setStatus(StatusMensagem.EDITADA);
		Mensagem salva = mensagemRepository.save(mensagem);
		MensagemResponse response = toResponse(salva);
		messagingTemplate.convertAndSendToUser(mensagem.getDestinatario().getEmail(), "/queue/mensagens", response);
		messagingTemplate.convertAndSendToUser(mensagem.getRemetente().getEmail(), "/queue/mensagens", response);
		return response;
	}

	@Transactional
	public void excluir(String emailAtual, Long mensagemId) {
		Mensagem mensagem = buscarPropria(emailAtual, mensagemId);
		mensagem.setStatus(StatusMensagem.EXCLUIDA);
		mensagem.setConteudo("");
		Mensagem salva = mensagemRepository.save(mensagem);
		MensagemResponse response = toResponse(salva);
		messagingTemplate.convertAndSendToUser(mensagem.getDestinatario().getEmail(), "/queue/mensagens", response);
		messagingTemplate.convertAndSendToUser(mensagem.getRemetente().getEmail(), "/queue/mensagens", response);
	}

	@Transactional
	public void marcarConversaComoLida(String emailAtual, Long amigoId) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		Usuario outro = usuarioService.buscarEntidadePorId(amigoId);
		mensagemRepository.findConversa(eu, outro).stream()
				.filter(m -> m.getDestinatario().getId().equals(eu.getId()))
				.filter(m -> m.getStatus() != StatusMensagem.LIDA && m.getStatus() != StatusMensagem.EXCLUIDA)
				.forEach(m -> {
					m.setStatus(StatusMensagem.LIDA);
					mensagemRepository.save(m);
				});
	}

	private Mensagem buscarPropria(String emailAtual, Long mensagemId) {
		Usuario eu = usuarioService.buscarEntidadePorEmail(emailAtual);
		Mensagem mensagem = mensagemRepository.findById(mensagemId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Mensagem não encontrada"));
		if (!mensagem.getRemetente().getId().equals(eu.getId())) {
			throw new AcessoNegadoException("Você só pode alterar suas próprias mensagens");
		}
		if (mensagem.getStatus() == StatusMensagem.EXCLUIDA) {
			throw new RecursoNaoEncontradoException("Mensagem não encontrada");
		}
		return mensagem;
	}

	private void garantirAmizade(Usuario a, Usuario b) {
		if (!amizadeRepository.saoAmigos(a, b)) {
			throw new AcessoNegadoException("Vocês precisam ser amigos para conversar");
		}
	}

	private MensagemResponse toResponse(Mensagem m) {
		return MensagemResponse.builder()
				.id(m.getId())
				.remetente(usuarioMapper.toResponse(m.getRemetente()))
				.destinatario(usuarioMapper.toResponse(m.getDestinatario()))
				.conteudo(m.getConteudo())
				.dataHora(m.getDataHora())
				.status(m.getStatus())
				.dataAtualizacao(m.getDataAtualizacao())
				.build();
	}
}
