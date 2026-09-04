package com.conecta.dto;

import com.conecta.entity.TipoNotificacao;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificacaoResponse {
	private Long id;
	private TipoNotificacao tipo;
	private String mensagem;
	private boolean lida;
	private Long referenciaId;
	private LocalDateTime dataHora;
	private UsuarioResponse origemUsuario;
}
