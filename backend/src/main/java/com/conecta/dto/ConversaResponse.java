package com.conecta.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversaResponse {
	private UsuarioResponse usuario;
	private String ultimaMensagem;
	private LocalDateTime horario;
	private long naoLidas;
}
