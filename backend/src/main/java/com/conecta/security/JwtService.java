package com.conecta.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMs;

	public JwtService(
			@Value("${conecta.jwt.secret}") String secret,
			@Value("${conecta.jwt.expiration-ms}") long expirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(resolveKeyBytes(secret));
		this.expirationMs = expirationMs;
	}

	public String gerarToken(String email, Long usuarioId) {
		Date agora = new Date();
		Date expiracao = new Date(agora.getTime() + expirationMs);

		return Jwts.builder()
				.subject(email)
				.claim("uid", usuarioId)
				.issuedAt(agora)
				.expiration(expiracao)
				.signWith(secretKey)
				.compact();
	}

	public String extrairEmail(String token) {
		return parseClaims(token).getSubject();
	}

	public Long extrairUsuarioId(String token) {
		Object uid = parseClaims(token).get("uid");
		if (uid instanceof Integer integer) {
			return integer.longValue();
		}
		if (uid instanceof Long longValue) {
			return longValue;
		}
		return null;
	}

	public boolean isTokenValido(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date());
		} catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException
				| SignatureException | IllegalArgumentException ex) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private byte[] resolveKeyBytes(String secret) {
		try {
			byte[] decoded = Decoders.BASE64.decode(secret);
			if (decoded.length >= 32) {
				return decoded;
			}
		} catch (RuntimeException ignored) {
			// usa bytes UTF-8 abaixo
		}
		byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
		if (raw.length < 32) {
			throw new IllegalArgumentException("JWT secret deve ter pelo menos 32 caracteres");
		}
		return raw;
	}
}
