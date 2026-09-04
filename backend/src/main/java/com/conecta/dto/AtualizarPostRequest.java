package com.conecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarPostRequest {

	@NotBlank(message = "O conteúdo é obrigatório")
	@Size(max = 2000, message = "O conteúdo deve ter no máximo 2000 caracteres")
	private String conteudo;
}
