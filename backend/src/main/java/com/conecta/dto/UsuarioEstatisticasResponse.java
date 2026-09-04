package com.conecta.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioEstatisticasResponse {
	private long totalAmigos;
	private long totalPosts;
}
