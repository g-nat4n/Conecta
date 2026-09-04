package com.conecta.controller;

import com.conecta.dto.ConversaResponse;
import com.conecta.dto.MensagemRequest;
import com.conecta.dto.MensagemResponse;
import com.conecta.service.MensagemService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final MensagemService mensagemService;

	@GetMapping("/conversas")
	public ResponseEntity<List<ConversaResponse>> conversas(Authentication authentication) {
		return ResponseEntity.ok(mensagemService.listarConversas(authentication.getName()));
	}

	@GetMapping("/{amigoId}")
	public ResponseEntity<List<MensagemResponse>> conversa(
			Authentication authentication,
			@PathVariable Long amigoId) {
		mensagemService.marcarConversaComoLida(authentication.getName(), amigoId);
		return ResponseEntity.ok(mensagemService.listarConversa(authentication.getName(), amigoId));
	}

	@PostMapping("/{amigoId}")
	public ResponseEntity<MensagemResponse> enviar(
			Authentication authentication,
			@PathVariable Long amigoId,
			@Valid @RequestBody MensagemRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(mensagemService.enviar(authentication.getName(), amigoId, request));
	}

	@PutMapping("/mensagens/{id}")
	public ResponseEntity<MensagemResponse> editar(
			Authentication authentication,
			@PathVariable Long id,
			@Valid @RequestBody MensagemRequest request) {
		return ResponseEntity.ok(mensagemService.editar(authentication.getName(), id, request));
	}

	@DeleteMapping("/mensagens/{id}")
	public ResponseEntity<Map<String, String>> excluir(
			Authentication authentication,
			@PathVariable Long id) {
		mensagemService.excluir(authentication.getName(), id);
		return ResponseEntity.ok(Map.of("mensagem", "Mensagem excluída"));
	}
}
