package com.conecta.dto;

import com.conecta.entity.StatusDenuncia;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DenunciaResponse {

	private Long id;
	private String tipo;
	private Long referenciaId;
	private String motivo;
	private StatusDenuncia status;
	private LocalDateTime dataCriacao;
	private Long usuarioId;
	private String usuarioNome;
}
