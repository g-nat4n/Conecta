package com.conecta.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {

	private Long id;
	private UsuarioResponse autor;
	private String conteudo;
	private String imagem;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAtualizacao;
	private long totalCurtidas;
	private long totalComentarios;
	private boolean curtidoPorMim;
}
