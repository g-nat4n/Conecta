package com.conecta.service;

import com.conecta.dto.AlterarSenhaRequest;
import com.conecta.dto.AtualizarPerfilRequest;
import com.conecta.dto.AuthResponse;
import com.conecta.dto.UsuarioEstatisticasResponse;
import com.conecta.dto.UsuarioResponse;
import com.conecta.entity.Usuario;
import com.conecta.exception.EmailJaCadastradoException;
import com.conecta.exception.RecursoNaoEncontradoException;
import com.conecta.exception.SenhaNaoConfereException;
import com.conecta.mapper.UsuarioMapper;
import com.conecta.repository.AmizadeRepository;
import com.conecta.repository.PostRepository;
import com.conecta.repository.UsuarioRepository;
import com.conecta.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final UsuarioMapper usuarioMapper;
	private final PasswordEncoder passwordEncoder;
	private final FileStorageService fileStorageService;
	private final JwtService jwtService;
	private final AmizadeRepository amizadeRepository;
	private final PostRepository postRepository;

	@Transactional(readOnly = true)
	public UsuarioResponse buscarPorEmail(String email) {
		return usuarioMapper.toResponse(buscarEntidadePorEmail(email));
	}

	@Transactional(readOnly = true)
	public UsuarioEstatisticasResponse estatisticas(String email) {
		Usuario usuario = buscarEntidadePorEmail(email);
		return UsuarioEstatisticasResponse.builder()
				.totalAmigos(amizadeRepository.countByUsuario(usuario))
				.totalPosts(postRepository.countByAutor(usuario))
				.build();
	}

	@Transactional(readOnly = true)
	public Usuario buscarEntidadePorEmail(String email) {
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
	}

	@Transactional(readOnly = true)
	public Usuario buscarEntidadePorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
	}

	@Transactional
	public AuthResponse atualizarPerfil(String emailAutenticado, AtualizarPerfilRequest request) {
		Usuario usuario = buscarEntidadePorEmail(emailAutenticado);
		String novoEmail = request.getEmail().trim().toLowerCase();

		if (!usuario.getEmail().equalsIgnoreCase(novoEmail) && usuarioRepository.existsByEmail(novoEmail)) {
			throw new EmailJaCadastradoException("Este email já está cadastrado");
		}

		usuario.setNome(request.getNome().trim());
		usuario.setEmail(novoEmail);
		Usuario salvo = usuarioRepository.save(usuario);

		String token = jwtService.gerarToken(salvo.getEmail(), salvo.getId());
		return AuthResponse.builder()
				.token(token)
				.tipo("Bearer")
				.usuario(usuarioMapper.toResponse(salvo))
				.build();
	}

	@Transactional
	public void alterarSenha(String emailAutenticado, AlterarSenhaRequest request) {
		if (!request.getNovaSenha().equals(request.getConfirmarNovaSenha())) {
			throw new SenhaNaoConfereException("A confirmação da nova senha não confere");
		}

		Usuario usuario = buscarEntidadePorEmail(emailAutenticado);

		if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
			throw new SenhaNaoConfereException("A senha atual está incorreta");
		}

		usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
		usuarioRepository.save(usuario);
	}

	@Transactional
	public UsuarioResponse atualizarFoto(String emailAutenticado, MultipartFile arquivo) {
		Usuario usuario = buscarEntidadePorEmail(emailAutenticado);
		String caminhoAnterior = usuario.getFoto();
		String novoCaminho = fileStorageService.salvar(arquivo, "perfis");
		usuario.setFoto(novoCaminho);
		Usuario salvo = usuarioRepository.save(usuario);
		fileStorageService.removerSeExistir(caminhoAnterior);
		return usuarioMapper.toResponse(salvo);
	}
}
