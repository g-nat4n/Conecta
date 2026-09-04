package com.conecta.controller;

import com.conecta.dto.SolicitacaoAmizadeResponse;
import com.conecta.dto.UsuarioResponse;
import com.conecta.service.AmizadeService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amigos")
@RequiredArgsConstructor
public class AmizadeController {

	private final AmizadeService amizadeService;

	@GetMapping
	public ResponseEntity<List<UsuarioResponse>> listar(Authentication authentication) {
		return ResponseEntity.ok(amizadeService.listarAmigos(authentication.getName()));
	}

	@GetMapping("/pesquisar")
	public ResponseEntity<List<UsuarioResponse>> pesquisar(
			Authentication authentication,
			@RequestParam String q) {
		return ResponseEntity.ok(amizadeService.pesquisar(authentication.getName(), q));
	}

	@GetMapping("/solicitacoes")
	public ResponseEntity<List<SolicitacaoAmizadeResponse>> solicitacoes(Authentication authentication) {
		return ResponseEntity.ok(amizadeService.listarPendentesRecebidas(authentication.getName()));
	}

	@GetMapping("/solicitacoes/enviadas")
	public ResponseEntity<List<SolicitacaoAmizadeResponse>> solicitacoesEnviadas(Authentication authentication) {
		return ResponseEntity.ok(amizadeService.listarPendentesEnviadas(authentication.getName()));
	}

	@PostMapping("/solicitacoes/{usuarioId}")
	public ResponseEntity<SolicitacaoAmizadeResponse> enviar(
			Authentication authentication,
			@PathVariable Long usuarioId) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(amizadeService.enviarSolicitacao(authentication.getName(), usuarioId));
	}

	@PostMapping("/solicitacoes/{id}/aceitar")
	public ResponseEntity<SolicitacaoAmizadeResponse> aceitar(
			Authentication authentication,
			@PathVariable Long id) {
		return ResponseEntity.ok(amizadeService.aceitar(authentication.getName(), id));
	}

	@PostMapping("/solicitacoes/{id}/recusar")
	public ResponseEntity<SolicitacaoAmizadeResponse> recusar(
			Authentication authentication,
			@PathVariable Long id) {
		return ResponseEntity.ok(amizadeService.recusar(authentication.getName(), id));
	}

	@DeleteMapping("/{amigoId}")
	public ResponseEntity<Map<String, String>> remover(
			Authentication authentication,
			@PathVariable Long amigoId) {
		amizadeService.removerAmigo(authentication.getName(), amigoId);
		return ResponseEntity.ok(Map.of("mensagem", "Amizade removida"));
	}
}
