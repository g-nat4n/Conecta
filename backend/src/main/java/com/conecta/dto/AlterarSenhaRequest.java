package com.conecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlterarSenhaRequest {

	@NotBlank(message = "A senha atual é obrigatória")
	private String senhaAtual;

	@NotBlank(message = "A nova senha é obrigatória")
	@Size(min = 6, max = 100, message = "A nova senha deve ter entre 6 e 100 caracteres")
	private String novaSenha;

	@NotBlank(message = "A confirmação da nova senha é obrigatória")
	private String confirmarNovaSenha;
}
