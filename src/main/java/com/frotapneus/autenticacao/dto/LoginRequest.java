package com.frotapneus.autenticacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para autenticação do usuário")
public record LoginRequest(
	@Schema(description = "E-mail do usuário", example = "admin@frota.com")
	@NotBlank @Email String email,
	
	@Schema(description = "Senha de acesso", example = "Senha@123")
	@NotBlank String senha
	) 
{
}
