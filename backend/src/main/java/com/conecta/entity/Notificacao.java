package com.conecta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notificacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "origem_usuario_id")
	private Usuario origemUsuario;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private TipoNotificacao tipo;

	@Column(nullable = false, length = 500)
	private String mensagem;

	@Column(nullable = false)
	private boolean lida;

	@Column(name = "referencia_id")
	private Long referenciaId;

	@Column(name = "data_hora", nullable = false, updatable = false)
	private LocalDateTime dataHora;

	@PrePersist
	protected void onCreate() {
		dataHora = LocalDateTime.now();
	}
}
