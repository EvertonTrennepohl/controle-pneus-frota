package com.frotapneus.usuario.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor

public class Usuario implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false , length = 100)
	private String nome;
	
	@Column(nullable = false, unique = true, length = 100)
	private String email;
	
	@Column(nullable = false, length = 255)
	private String senha; // Senha que será armazenada criptografada com BCrypt
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Perfil perfil = Perfil.OPERADOR;
	
	@Column(nullable = false)
	private boolean ativo = true;
	
	@Column(name = "criado_em", updatable = false)
	private LocalDateTime criadoEm;
	
	@Column(name = "atualizado_em")
	private LocalDateTime atualizado_em;
	
	// Métodos de ciclo de vida do JPA para preenchimento automático das datas
	@PrePersist
	protected void onCreate() {
		this.criadoEm = LocalDateTime.now();
		this.atualizado_em = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.atualizado_em = LocalDateTime.now();
	}
	
	// =========================================================================
    // MÉTODOS DO USERDETAILS (SPRING SECURITY)
    // =========================================================================

    @Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
    	// Vincula o enum Perfil às permissões (ROLE) do Spring Security
    	if (this.perfil == Perfil.ADMINISTRADOR) {
    		return List.of(
    			new SimpleGrantedAuthority("ROLE_ADMIN"),
    			new SimpleGrantedAuthority("ROLE_OPERADOR")
    		);
    	}
    	return List.of(new SimpleGrantedAuthority("ROLE_OPERADOR"));
    }
    
    @Override
    public String getPassword() {
    	return this.senha;
    }
    
    @Override
    public String getUsername() {
    	return this.email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
    	return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
    	return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
    	return true;
    }
    
    @Override 
    public boolean isEnabled() {
    	return this.ativo;
    }
}
