package com.conecta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

	@NotBlank(message = "O email é obrigatório")
	@Email(message = "Informe um email válido")
	private String email;

	@NotBlank(message = "A senha é obrigatória")
	private String senha;
}
