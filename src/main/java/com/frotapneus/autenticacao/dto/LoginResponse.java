package com.frotapneus.autenticacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Retorno de autenticação bem sucedida")
public record LoginResponse(
	@Schema(description = "Token JWT gerado", example = "eyJhbGciOiJIUzI1Ni...")
	String token,
	
	@Schema(description = "Tipo do token", example = "Bearer")
	String tipo
	) 
{

}
