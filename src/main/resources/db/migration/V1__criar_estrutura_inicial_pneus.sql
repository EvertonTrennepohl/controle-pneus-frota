CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL, -- Senha criptografada com BCrypt
    perfil VARCHAR(20) NOT NULL DEFAULT 'OPERADOR', -- 'ADMINISTRADOR', 'OPERADOR'
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE filial (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(50)
);

CREATE TABLE fornecedor (
    id SERIAL PRIMARY KEY,
    razao_social VARCHAR(100) NOT NULL,
    nome_fantasia VARCHAR(100) NOT NULL,
    cnpj VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    cidade VARCHAR(50),
    
    -- Identificadores de tipo para ajudar nos filtros do Angular
    pode_vender_pneu_novo BOOLEAN DEFAULT TRUE,
    pode_recapar_pneu BOOLEAN DEFAULT FALSE,
    
    ativo BOOLEAN DEFAULT TRUE,
    criado_por VARCHAR(100),
    criado_em TIMESTAMP,
    atualizado_por VARCHAR(100),
    atualizado_em TIMESTAMP
);

CREATE TABLE caminhao (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(7) UNIQUE NOT NULL,
    modelo VARCHAR(50),
    km_atual DECIMAL(10,2) DEFAULT 0.00,
    criado_por VARCHAR(100),
    criado_em TIMESTAMP,
    atualizado_por VARCHAR(100),
    atualizado_em TIMESTAMP
);

CREATE TABLE eixo_caminhao (
    id SERIAL PRIMARY KEY,
    caminhao_id INT NOT NULL REFERENCES caminhao(id) ON DELETE CASCADE,
    numero_eixo INT NOT NULL, 
    tipo_eixo VARCHAR(20) NOT NULL, 
    qtd_pneus_por_lado INT NOT NULL,
    CONSTRAINT uq_caminhao_eixo UNIQUE (caminhao_id, numero_eixo),
    CONSTRAINT chk_qtd_pneus CHECK (qtd_pneus_por_lado IN (1, 2)),
    CONSTRAINT chk_tipo_eixo CHECK (tipo_eixo IN ('DIRECIONAL', 'TRASEIRO'))
);

CREATE TABLE pneu (
    numero_fogo VARCHAR(20) NOT NULL,
    medida VARCHAR(15) NOT NULL, 
    marca VARCHAR(50) NOT NULL,
    estado VARCHAR(30) NOT NULL, 
    tipo_banda VARCHAR(20) NOT NULL, 
    km_atual DECIMAL(10,2) DEFAULT 0.00,
    qtd_recapagens INT DEFAULT 0,
    local_atual VARCHAR(30) NOT NULL, 
    
    -- Vínculo de Origem/Compra:
    fornecedor_compra_id INT REFERENCES fornecedor(id), 
    valor_compra DECIMAL(10,2) DEFAULT 0.00,
    
    -- Vínculos dinâmicos de localização atual:
    filial_id INT REFERENCES filial(id),
    caminhao_id INT REFERENCES caminhao(id),
    eixo_caminhao_id INT REFERENCES eixo_caminhao(id),
    posicao_pneu VARCHAR(20), 
    fornecedor_servico_id INT REFERENCES fornecedor(id), -- Preenchido se estiver enviado para recapagem
    
    criado_por VARCHAR(100),
    criado_em TIMESTAMP,
    atualizado_por VARCHAR(100),
    atualizado_em TIMESTAMP,
    
    PRIMARY KEY (numero_fogo, medida),
    CONSTRAINT chk_local CHECK (local_atual IN ('ESTOQUE_FILIAL', 'NO_CAMINHAO', 'AGUARDANDO_RECAPAGEM', 'EM_RECAPAGEM', 'DESCARTADO'))
);

CREATE TABLE historico_pneu (
    id SERIAL PRIMARY KEY,
    numero_fogo VARCHAR(20) NOT NULL,
    medida VARCHAR(15) NOT NULL,
    data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    usuario_id INT REFERENCES usuario(id),
    usuario_nome VARCHAR(100) NOT NULL, 
    
    tipo_evento VARCHAR(50) NOT NULL, -- 'COMPRA', 'INSTALACAO', 'RETIRADA', 'ENVIO_RECAPAGEM', 'RETORNO_RECAPAGEM', etc.
    local_relacionado VARCHAR(100),   
    fornecedor_id INT REFERENCES fornecedor(id), -- Registra o fornecedor envolvido no evento (venda ou serviço)
    
    km_no_evento DECIMAL(10,2) NOT NULL,
    custo_evento DECIMAL(10,2) DEFAULT 0.00, -- Valor da compra (se evento COMPRA) ou da recapagem (se RETORNO_RECAPAGEM)
    observacao TEXT,
    
    FOREIGN KEY (numero_fogo, medida) REFERENCES pneu(numero_fogo, medida) ON DELETE CASCADE
);

-- =========================================================================
-- MILESTONE 5: ÍNDICES DE OTIMIZAÇÃO (MUITO IMPORTANTE PARA A NUVEM)
-- =========================================================================

CREATE INDEX idx_pneu_local ON pneu(local_atual, filial_id);
CREATE INDEX idx_pneu_caminhao ON pneu(caminhao_id);
CREATE INDEX idx_pneu_fornecedor_servico ON pneu(fornecedor_servico_id) WHERE local_atual = 'EM_RECAPAGEM';