package com.conecta.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurtidaResponse {

	private Long postId;
	private long totalCurtidas;
	private boolean curtidoPorMim;
}
