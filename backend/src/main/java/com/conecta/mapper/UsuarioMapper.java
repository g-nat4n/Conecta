package com.conecta.mapper;

import com.conecta.dto.UsuarioResponse;
import com.conecta.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

	public UsuarioResponse toResponse(Usuario usuario) {
		return UsuarioResponse.builder()
				.id(usuario.getId())
				.nome(usuario.getNome())
				.email(usuario.getEmail())
				.foto(usuario.getFoto())
				.dataCriacao(usuario.getDataCriacao())
				.build();
	}
}
