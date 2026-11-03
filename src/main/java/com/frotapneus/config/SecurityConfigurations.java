package com.frotapneus.config;

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

import com.frotapneus.autenticacao.controller.AutenticacaoController;
import com.frotapneus.security.SecurityAuthenticationEntryPoint;
import com.frotapneus.security.SecurityFilter;

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
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
					// Liberar as rotas de autenticação (Login)
					.requestMatchers(HttpMethod.POST, AutenticacaoController.BASE_URL + "/login").permitAll()
					// Liberar as rotas públicas de documentação do Swagger/OpenAPI
					.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
					.anyRequest().authenticated()
				)
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(authenticationEntryPoint)
				)
				.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
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
