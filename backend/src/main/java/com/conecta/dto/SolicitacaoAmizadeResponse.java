package com.conecta.dto;

import com.conecta.entity.StatusSolicitacao;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SolicitacaoAmizadeResponse {
	private Long id;
	private UsuarioResponse remetente;
	private UsuarioResponse destinatario;
	private StatusSolicitacao status;
	private LocalDateTime dataCriacao;
}
