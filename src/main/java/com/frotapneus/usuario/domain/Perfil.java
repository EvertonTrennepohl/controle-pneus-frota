package com.frotapneus.usuario.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Níveis de acesso permitidos para os usuários no sistema de frota")
public enum Perfil {
    
    @Schema(description = "Acesso total ao sistema, incluindo configurações e cadastros críticos")
    ADMINISTRADOR,
    
    @Schema(description = "Acesso restrito às operações cotidianas de movimentação e controle de pneus")
    OPERADOR

}
