package com.gestaofrota.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gestaofrota.autenticacao.controller.AutenticacaoController;
import com.gestaofrota.security.SecurityAuthenticationEntryPoint;
import com.gestaofrota.security.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
	
	private final SecurityFilter securityFilter;
	private final SecurityAuthenticationEntryPoint authenticationEntryPoint;
	
	public SecurityConfigurations(SecurityFilter securityFilter, SecurityAuthenticationEntryPoint authenticationEntryPoint) {
		this.securityFilter = securityFilter;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				// Ativa o CORS apontando para o Bean
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
					// Liberar as rotas de autenticação (Login)
					.requestMatchers(HttpMethod.POST, AutenticacaoController.BASE_URL + "/login").permitAll()
					// Liberar as rotas públicas de documentação do Swagger/OpenAPI
					.requestMatchers(
							"/v3/api-docs",
							"/v3/api-docs/**", 
							"/swagger-ui/**", 
							"/swagger-ui.html",
							"/swagger-resources/**",
							"/webjars/**"
					).permitAll()
					
					// Qualquer outra requisição exige autenticação via token
					.anyRequest().authenticated()
				)
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(authenticationEntryPoint)
				)
				.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	//  Define as regras do CORS liberando o Angular localmente e a Vercel em produção
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
				"http://localhost:4200",  // Libera o front-end Angular local
				"https://controle-pneus-front.vercel.app")); // Libera as requisições da Vercel
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
		configuration.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Aplica essa regra em todos os endpoints
		return source;
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
