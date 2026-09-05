package com.conecta.controller;

import com.conecta.dto.CriarDenunciaRequest;
import com.conecta.dto.DenunciaResponse;
import com.conecta.service.DenunciaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/denuncias")
@RequiredArgsConstructor
public class DenunciaController {

	private final DenunciaService denunciaService;

	@PostMapping
	public ResponseEntity<DenunciaResponse> criar(
			Authentication authentication,
			@Valid @RequestBody CriarDenunciaRequest request) {
		DenunciaResponse response = denunciaService.criar(authentication.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<DenunciaResponse>> listar() {
		return ResponseEntity.ok(denunciaService.listar());
	}
}
