package com.conecta.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioResponse {

	private Long id;
	private String nome;
	private String email;
	private String foto;
	private LocalDateTime dataCriacao;
}
