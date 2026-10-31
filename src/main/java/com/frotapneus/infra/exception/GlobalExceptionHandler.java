package com.frotapneus.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// Tratamento para falhas de autenticação (401)
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> tratarErroAuthentication(AuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos");
	}
	
	// Tratamento para erros de validação de DTOs (@Valid) -> Retorna 400 Bad Request
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> tratarErro400(MethodArgumentNotValidException ex) {
		var erros = ex.getFieldErrors();
		
		// Mapeia os erros para uma lista limpa contendo apenas o campo e amensagem
		var dadosErros = erros.stream()
				.map(DadosErroValidacao::new)
				.toList();
		return ResponseEntity.badRequest().body(dadosErros);
	}

}
