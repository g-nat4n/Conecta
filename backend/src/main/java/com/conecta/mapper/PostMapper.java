package com.conecta.mapper;

import com.conecta.dto.ComentarioResponse;
import com.conecta.dto.PostResponse;
import com.conecta.entity.Comentario;
import com.conecta.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

	private final UsuarioMapper usuarioMapper;

	public PostResponse toResponse(Post post, long totalCurtidas, long totalComentarios, boolean curtidoPorMim) {
		return PostResponse.builder()
				.id(post.getId())
				.autor(usuarioMapper.toResponse(post.getAutor()))
				.conteudo(post.getConteudo())
				.imagem(post.getImagem())
				.dataCriacao(post.getDataCriacao())
				.dataAtualizacao(post.getDataAtualizacao())
				.totalCurtidas(totalCurtidas)
				.totalComentarios(totalComentarios)
				.curtidoPorMim(curtidoPorMim)
				.build();
	}

	public ComentarioResponse toComentarioResponse(Comentario comentario) {
		return ComentarioResponse.builder()
				.id(comentario.getId())
				.postId(comentario.getPost().getId())
				.autor(usuarioMapper.toResponse(comentario.getAutor()))
				.conteudo(comentario.getConteudo())
				.dataCriacao(comentario.getDataCriacao())
				.dataAtualizacao(comentario.getDataAtualizacao())
				.build();
	}
}
