package com.conecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarComentarioRequest {

	@NotBlank(message = "O comentário é obrigatório")
	@Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres")
	private String conteudo;
}
