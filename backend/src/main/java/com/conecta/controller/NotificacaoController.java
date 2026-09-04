package com.conecta.controller;

import com.conecta.dto.NotificacaoResponse;
import com.conecta.entity.Usuario;
import com.conecta.service.NotificacaoService;
import com.conecta.service.UsuarioService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

	private final NotificacaoService notificacaoService;
	private final UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<List<NotificacaoResponse>> listar(Authentication authentication) {
		Usuario usuario = usuarioService.buscarEntidadePorEmail(authentication.getName());
		return ResponseEntity.ok(notificacaoService.listar(usuario));
	}

	@GetMapping("/nao-lidas")
	public ResponseEntity<Map<String, Long>> naoLidas(Authentication authentication) {
		Usuario usuario = usuarioService.buscarEntidadePorEmail(authentication.getName());
		return ResponseEntity.ok(Map.of("total", notificacaoService.contarNaoLidas(usuario)));
	}

	@PutMapping("/{id}/lida")
	public ResponseEntity<NotificacaoResponse> marcarLida(
			Authentication authentication,
			@PathVariable Long id) {
		Usuario usuario = usuarioService.buscarEntidadePorEmail(authentication.getName());
		return ResponseEntity.ok(notificacaoService.marcarLida(id, usuario));
	}

	@PutMapping("/lidas")
	public ResponseEntity<Map<String, String>> marcarTodas(Authentication authentication) {
		Usuario usuario = usuarioService.buscarEntidadePorEmail(authentication.getName());
		notificacaoService.marcarTodasLidas(usuario);
		return ResponseEntity.ok(Map.of("mensagem", "Notificações marcadas como lidas"));
	}
}
