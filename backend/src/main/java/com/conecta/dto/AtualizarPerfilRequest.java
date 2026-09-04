package com.conecta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarPerfilRequest {

	@NotBlank(message = "O nome é obrigatório")
	@Size(min = 2, max = 120, message = "O nome deve ter entre 2 e 120 caracteres")
	private String nome;

	@NotBlank(message = "O email é obrigatório")
	@Email(message = "Informe um email válido")
	@Size(max = 180, message = "O email deve ter no máximo 180 caracteres")
	private String email;
}
