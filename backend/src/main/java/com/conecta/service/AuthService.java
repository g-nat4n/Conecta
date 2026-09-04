package com.conecta.service;

import com.conecta.dto.AuthResponse;
import com.conecta.dto.LoginRequest;
import com.conecta.dto.RegistroRequest;
import com.conecta.dto.UsuarioResponse;
import com.conecta.entity.Usuario;
import com.conecta.exception.EmailJaCadastradoException;
import com.conecta.exception.RecursoNaoEncontradoException;
import com.conecta.exception.SenhaNaoConfereException;
import com.conecta.mapper.UsuarioMapper;
import com.conecta.repository.UsuarioRepository;
import com.conecta.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final UsuarioMapper usuarioMapper;

	@Transactional
	public UsuarioResponse registrar(RegistroRequest request) {
		if (!request.getSenha().equals(request.getConfirmarSenha())) {
			throw new SenhaNaoConfereException("A confirmação de senha não confere");
		}

		String email = request.getEmail().trim().toLowerCase();

		if (usuarioRepository.existsByEmail(email)) {
			throw new EmailJaCadastradoException("Este email já está cadastrado");
		}

		Usuario usuario = Usuario.builder()
				.nome(request.getNome().trim())
				.email(email)
				.senha(passwordEncoder.encode(request.getSenha()))
				.foto(null)
				.build();

		Usuario salvo = usuarioRepository.save(usuario);
		return usuarioMapper.toResponse(salvo);
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		String email = request.getEmail().trim().toLowerCase();

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, request.getSenha()));

		Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

		String token = jwtService.gerarToken(usuario.getEmail(), usuario.getId());

		return AuthResponse.builder()
				.token(token)
				.tipo("Bearer")
				.usuario(usuarioMapper.toResponse(usuario))
				.build();
	}
}
