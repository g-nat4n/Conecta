package com.conecta.controller;

import com.conecta.dto.AtualizarPostRequest;
import com.conecta.dto.CriarPostRequest;
import com.conecta.dto.CurtidaResponse;
import com.conecta.dto.PostResponse;
import com.conecta.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@GetMapping
	public ResponseEntity<Page<PostResponse>> listar(
			Authentication authentication,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(postService.listarFeed(authentication.getName(), page, Math.min(size, 50)));
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<PostResponse> criar(
			Authentication authentication,
			@RequestPart("conteudo") String conteudo,
			@RequestPart(value = "imagem", required = false) MultipartFile imagem) {
		CriarPostRequest request = new CriarPostRequest();
		request.setConteudo(conteudo);
		PostResponse response = postService.criar(authentication.getName(), request, imagem);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PostResponse> criarJson(
			Authentication authentication,
			@Valid @RequestBody CriarPostRequest request) {
		PostResponse response = postService.criar(authentication.getName(), request, null);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PostResponse> atualizar(
			Authentication authentication,
			@PathVariable Long id,
			@Valid @RequestBody AtualizarPostRequest request) {
		return ResponseEntity.ok(postService.atualizar(id, authentication.getName(), request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(Authentication authentication, @PathVariable Long id) {
		postService.excluir(id, authentication.getName());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/curtidas")
	public ResponseEntity<CurtidaResponse> alternarCurtida(
			Authentication authentication,
			@PathVariable Long id) {
		return ResponseEntity.ok(postService.alternarCurtida(id, authentication.getName()));
	}
}
