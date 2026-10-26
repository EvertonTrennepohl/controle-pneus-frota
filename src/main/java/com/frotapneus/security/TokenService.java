package com.frotapneus.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.frotapneus.usuario.domain.Usuario;

@Service
public class TokenService {
	
	@Value("${api.security.token.secret}")
	private String secret;
	
	public String gerarToken (Usuario usuario) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.create()
					.withIssuer("controle-pneus-frota-api")
					.withSubject(usuario.getUsername())
					.withExpiresAt(gerarDataExpiracao())
					.sign(algorithm);
		} catch (JWTCreationException exception) {
			throw new RuntimeException("Erro ao gerar token JWT", exception);
		}
	}
	
	public String validarToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm)
					.withIssuer("controle-pneus-frota-api")
					.build()
					.verify(token)
					.getSubject();
		} catch (JWTVerificationException exception) {
			return ""; // Token inválido ou expirado
		}
	}
	
	private Instant gerarDataExpiracao() {
		return LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("-03:00"));
	}
	
}
