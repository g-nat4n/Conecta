package com.conecta.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {

	private int status;
	private String erro;
	private String mensagem;
	private String caminho;
	private LocalDateTime timestamp;
}
