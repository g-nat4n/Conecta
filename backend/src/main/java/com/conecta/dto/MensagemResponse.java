package com.conecta.dto;

import com.conecta.entity.StatusMensagem;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MensagemResponse {
	private Long id;
	private UsuarioResponse remetente;
	private UsuarioResponse destinatario;
	private String conteudo;
	private LocalDateTime dataHora;
	private StatusMensagem status;
	private LocalDateTime dataAtualizacao;
}
