package com.conecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarDenunciaRequest {

	@NotBlank(message = "O tipo é obrigatório")
	@Size(max = 40)
	private String tipo;

	@NotNull(message = "A referência é obrigatória")
	private Long referenciaId;

	@NotBlank(message = "O motivo é obrigatório")
	@Size(min = 5, max = 500, message = "O motivo deve ter entre 5 e 500 caracteres")
	private String motivo;
}
