package com.frotapneus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
			.title("Controle Pneus Frota API")
			.version("1.0.0")
			.description("API para gerenciamento de pneus de frota"))
			// Adiciona o suporte ao Bearer Token (JWT) no Swagger UI
			.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
			.components(new Components()
					.addSecuritySchemes("BearerAuth", new SecurityScheme()
					.name("BearerAuth")
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")));
	}

}
