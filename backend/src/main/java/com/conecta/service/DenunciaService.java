package com.conecta.service;

import com.conecta.dto.CriarDenunciaRequest;
import com.conecta.dto.DenunciaResponse;
import com.conecta.entity.Denuncia;
import com.conecta.entity.StatusDenuncia;
import com.conecta.entity.Usuario;
import com.conecta.repository.DenunciaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DenunciaService {

	private final DenunciaRepository denunciaRepository;
	private final UsuarioService usuarioService;

	@Transactional
	public DenunciaResponse criar(String emailAtual, CriarDenunciaRequest request) {
		Usuario usuario = usuarioService.buscarEntidadePorEmail(emailAtual);

		Denuncia denuncia = Denuncia.builder()
				.usuario(usuario)
				.tipo(request.getTipo().trim().toUpperCase())
				.referenciaId(request.getReferenciaId())
				.motivo(request.getMotivo().trim())
				.status(StatusDenuncia.PENDENTE)
				.build();

		return toResponse(denunciaRepository.save(denuncia));
	}

	@Transactional(readOnly = true)
	public List<DenunciaResponse> listar() {
		return denunciaRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public List<DenunciaResponse> listarPorUsuario(Long usuarioId) {
		return denunciaRepository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
	}

	private DenunciaResponse toResponse(Denuncia denuncia) {
		Usuario usuario = denuncia.getUsuario();
		return DenunciaResponse.builder()
				.id(denuncia.getId())
				.tipo(denuncia.getTipo())
				.referenciaId(denuncia.getReferenciaId())
				.motivo(denuncia.getMotivo())
				.status(denuncia.getStatus())
				.dataCriacao(denuncia.getDataCriacao())
				.usuarioId(usuario != null ? usuario.getId() : null)
				.usuarioNome(usuario != null ? usuario.getNome() : null)
				.build();
	}
}
