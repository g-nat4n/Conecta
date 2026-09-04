package com.conecta.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ComentarioResponse {

	private Long id;
	private Long postId;
	private UsuarioResponse autor;
	private String conteudo;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAtualizacao;
}
