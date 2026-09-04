package com.conecta.controller;

import com.conecta.dto.ComentarioResponse;
import com.conecta.dto.CriarComentarioRequest;
import com.conecta.service.ComentarioService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class ComentarioController {

	private final ComentarioService comentarioService;

	@GetMapping("/posts/{postId}/comentarios")
	public ResponseEntity<List<ComentarioResponse>> listar(@PathVariable Long postId) {
		return ResponseEntity.ok(comentarioService.listarPorPost(postId));
	}

	@PostMapping("/posts/{postId}/comentarios")
	public ResponseEntity<ComentarioResponse> criar(
			Authentication authentication,
			@PathVariable Long postId,
			@Valid @RequestBody CriarComentarioRequest request) {
		ComentarioResponse response = comentarioService.criar(postId, authentication.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/comentarios/{id}")
	public ResponseEntity<ComentarioResponse> atualizar(
			Authentication authentication,
			@PathVariable Long id,
			@Valid @RequestBody CriarComentarioRequest request) {
		return ResponseEntity.ok(comentarioService.atualizar(id, authentication.getName(), request));
	}

	@DeleteMapping("/comentarios/{id}")
	public ResponseEntity<Void> excluir(Authentication authentication, @PathVariable Long id) {
		comentarioService.excluir(id, authentication.getName());
		return ResponseEntity.noContent().build();
	}
}
