package com.conecta.exception;

import com.conecta.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String mensagem = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining("; "));

		return build(HttpStatus.BAD_REQUEST, "Bad Request", mensagem, request.getRequestURI());
	}

	@ExceptionHandler(EmailJaCadastradoException.class)
	public ResponseEntity<ApiErrorResponse> handleEmailDuplicado(
			EmailJaCadastradoException ex,
			HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(SenhaNaoConfereException.class)
	public ResponseEntity<ApiErrorResponse> handleSenha(
			SenhaNaoConfereException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(
			RecursoNaoEncontradoException ex,
			HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
			IllegalArgumentException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(ArquivoInvalidoException.class)
	public ResponseEntity<ApiErrorResponse> handleArquivo(
			ArquivoInvalidoException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(AcessoNegadoException.class)
	public ResponseEntity<ApiErrorResponse> handleAcessoNegado(
			AcessoNegadoException ex,
			HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
	public ResponseEntity<ApiErrorResponse> handleAuth(
			RuntimeException ex,
			HttpServletRequest request) {
		return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "Email ou senha inválidos", request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(
			Exception ex,
			HttpServletRequest request) {
		return build(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Internal Server Error",
				"Ocorreu um erro interno. Tente novamente mais tarde.",
				request.getRequestURI());
	}

	private ResponseEntity<ApiErrorResponse> build(
			HttpStatus status,
			String erro,
			String mensagem,
			String caminho) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.status(status.value())
				.erro(erro)
				.mensagem(mensagem)
				.caminho(caminho)
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.status(status).body(body);
	}
}
