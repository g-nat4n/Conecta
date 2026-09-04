package com.conecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensagemRequest {
	@NotBlank(message = "O conteúdo é obrigatório")
	@Size(max = 4000, message = "A mensagem deve ter no máximo 4000 caracteres")
	private String conteudo;
}
