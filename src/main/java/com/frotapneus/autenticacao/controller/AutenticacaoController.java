package com.frotapneus.autenticacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frotapneus.autenticacao.dto.LoginRequest;
import com.frotapneus.autenticacao.dto.LoginResponse;
import com.frotapneus.security.TokenService;
import com.frotapneus.usuario.domain.Usuario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoint de login do sistema")
public class AutenticacaoController {
	
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	
	public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService) {
		this.authenticationManager = authenticationManager;
		this.tokenService = tokenService;
	}
	
	@PostMapping("/login")
	@Operation(summary = "Realizar Login", description = "Autentica o usuário e devolve o token JWT")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login efetuado com sucesso"),
			@ApiResponse(responseCode = "403", description = "Credenciais inválidas")
	})
	public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest dados) {
		var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
		
		// O Spring chama o AutenticacaoService.LoadUserByName e valida o BCrypt sozinho aqui
		var authentication = authenticationManager.authenticate(authenticationToken);
		
		var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
		
		return ResponseEntity.ok(new LoginResponse(tokenJWT, "Bearer"));
		
	}

}
