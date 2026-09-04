package com.conecta.service;

import com.conecta.dto.ComentarioResponse;
import com.conecta.dto.CriarComentarioRequest;
import com.conecta.entity.Comentario;
import com.conecta.entity.Post;
import com.conecta.entity.Usuario;
import com.conecta.exception.AcessoNegadoException;
import com.conecta.exception.RecursoNaoEncontradoException;
import com.conecta.mapper.PostMapper;
import com.conecta.repository.ComentarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComentarioService {

	private final ComentarioRepository comentarioRepository;
	private final PostService postService;
	private final UsuarioService usuarioService;
	private final PostMapper postMapper;

	@Transactional(readOnly = true)
	public List<ComentarioResponse> listarPorPost(Long postId) {
		Post post = postService.buscarPost(postId);
		return comentarioRepository.findByPostOrderByDataCriacaoAsc(post).stream()
				.map(postMapper::toComentarioResponse)
				.toList();
	}

	@Transactional
	public ComentarioResponse criar(Long postId, String emailAutor, CriarComentarioRequest request) {
		Post post = postService.buscarPost(postId);
		Usuario autor = usuarioService.buscarEntidadePorEmail(emailAutor);

		Comentario comentario = Comentario.builder()
				.post(post)
				.autor(autor)
				.conteudo(request.getConteudo().trim())
				.build();

		return postMapper.toComentarioResponse(comentarioRepository.save(comentario));
	}

	@Transactional
	public ComentarioResponse atualizar(Long comentarioId, String emailAtual, CriarComentarioRequest request) {
		Comentario comentario = buscarComentario(comentarioId);
		garantirDono(comentario, emailAtual);
		comentario.setConteudo(request.getConteudo().trim());
		return postMapper.toComentarioResponse(comentarioRepository.save(comentario));
	}

	@Transactional
	public void excluir(Long comentarioId, String emailAtual) {
		Comentario comentario = buscarComentario(comentarioId);
		garantirDono(comentario, emailAtual);
		comentarioRepository.delete(comentario);
	}

	private Comentario buscarComentario(Long id) {
		return comentarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Comentário não encontrado"));
	}

	private void garantirDono(Comentario comentario, String emailAtual) {
		if (!comentario.getAutor().getEmail().equalsIgnoreCase(emailAtual)) {
			throw new AcessoNegadoException("Você só pode modificar seus próprios comentários");
		}
	}
}
