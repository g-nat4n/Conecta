package com.conecta.controller;

import com.conecta.dto.AlterarSenhaRequest;
import com.conecta.dto.AtualizarPerfilRequest;
import com.conecta.dto.AuthResponse;
import com.conecta.dto.UsuarioEstatisticasResponse;
import com.conecta.dto.UsuarioResponse;
import com.conecta.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService usuarioService;

	@GetMapping("/me")
	public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
		return ResponseEntity.ok(usuarioService.buscarPorEmail(authentication.getName()));
	}

	@GetMapping("/me/estatisticas")
	public ResponseEntity<UsuarioEstatisticasResponse> estatisticas(Authentication authentication) {
		return ResponseEntity.ok(usuarioService.estatisticas(authentication.getName()));
	}

	@PutMapping("/me")
	public ResponseEntity<AuthResponse> atualizarPerfil(
			Authentication authentication,
			@Valid @RequestBody AtualizarPerfilRequest request) {
		return ResponseEntity.ok(usuarioService.atualizarPerfil(authentication.getName(), request));
	}

	@PutMapping("/me/senha")
	public ResponseEntity<Map<String, String>> alterarSenha(
			Authentication authentication,
			@Valid @RequestBody AlterarSenhaRequest request) {
		usuarioService.alterarSenha(authentication.getName(), request);
		return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso"));
	}

	@PostMapping(value = "/me/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UsuarioResponse> atualizarFoto(
			Authentication authentication,
			@RequestPart("foto") MultipartFile foto) {
		return ResponseEntity.ok(usuarioService.atualizarFoto(authentication.getName(), foto));
	}
}
