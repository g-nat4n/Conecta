package com.conecta.service;

import com.conecta.dto.AtualizarPostRequest;
import com.conecta.dto.CriarPostRequest;
import com.conecta.dto.CurtidaResponse;
import com.conecta.dto.PostResponse;
import com.conecta.entity.Curtida;
import com.conecta.entity.Post;
import com.conecta.entity.Usuario;
import com.conecta.exception.AcessoNegadoException;
import com.conecta.exception.RecursoNaoEncontradoException;
import com.conecta.mapper.PostMapper;
import com.conecta.repository.ComentarioRepository;
import com.conecta.repository.CurtidaRepository;
import com.conecta.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final CurtidaRepository curtidaRepository;
	private final ComentarioRepository comentarioRepository;
	private final UsuarioService usuarioService;
	private final FileStorageService fileStorageService;
	private final PostMapper postMapper;

	@Transactional
	public PostResponse criar(String emailAutor, CriarPostRequest request, MultipartFile imagem) {
		if (request.getConteudo() == null || request.getConteudo().isBlank()) {
			throw new IllegalArgumentException("O conteúdo é obrigatório");
		}

		Usuario autor = usuarioService.buscarEntidadePorEmail(emailAutor);
		String imagemPath = null;
		if (imagem != null && !imagem.isEmpty()) {
			imagemPath = fileStorageService.salvar(imagem, "posts");
		}

		Post post = Post.builder()
				.autor(autor)
				.conteudo(request.getConteudo().trim())
				.imagem(imagemPath)
				.build();

		Post salvo = postRepository.save(post);
		return postMapper.toResponse(salvo, 0, 0, false);
	}

	@Transactional(readOnly = true)
	public Page<PostResponse> listarFeed(String emailAtual, int page, int size) {
		Usuario usuario = usuarioService.buscarEntidadePorEmail(emailAtual);
		return postRepository.findAllByOrderByDataCriacaoDesc(PageRequest.of(page, size))
				.map(post -> toResponse(post, usuario));
	}

	@Transactional
	public PostResponse atualizar(Long postId, String emailAtual, AtualizarPostRequest request) {
		Post post = buscarPost(postId);
		garantirDono(post, emailAtual);
		post.setConteudo(request.getConteudo().trim());
		Post salvo = postRepository.save(post);
		Usuario usuario = usuarioService.buscarEntidadePorEmail(emailAtual);
		return toResponse(salvo, usuario);
	}

	@Transactional
	public void excluir(Long postId, String emailAtual) {
		Post post = buscarPost(postId);
		garantirDono(post, emailAtual);
		curtidaRepository.deleteByPost(post);
		comentarioRepository.deleteByPost(post);
		String imagem = post.getImagem();
		postRepository.delete(post);
		fileStorageService.removerSeExistir(imagem);
	}

	@Transactional
	public CurtidaResponse alternarCurtida(Long postId, String emailAtual) {
		Post post = buscarPost(postId);
		Usuario usuario = usuarioService.buscarEntidadePorEmail(emailAtual);

		curtidaRepository.findByPostAndUsuario(post, usuario).ifPresentOrElse(
				curtidaRepository::delete,
				() -> curtidaRepository.save(Curtida.builder().post(post).usuario(usuario).build()));

		boolean curtido = curtidaRepository.existsByPostAndUsuario(post, usuario);
		long total = curtidaRepository.countByPost(post);

		return CurtidaResponse.builder()
				.postId(post.getId())
				.totalCurtidas(total)
				.curtidoPorMim(curtido)
				.build();
	}

	@Transactional(readOnly = true)
	public Post buscarPost(Long id) {
		return postRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Post não encontrado"));
	}

	private PostResponse toResponse(Post post, Usuario usuarioAtual) {
		return postMapper.toResponse(
				post,
				curtidaRepository.countByPost(post),
				comentarioRepository.countByPost(post),
				curtidaRepository.existsByPostAndUsuario(post, usuarioAtual));
	}

	private void garantirDono(Post post, String emailAtual) {
		if (!post.getAutor().getEmail().equalsIgnoreCase(emailAtual)) {
			throw new AcessoNegadoException("Você só pode modificar seus próprios posts");
		}
	}
}
