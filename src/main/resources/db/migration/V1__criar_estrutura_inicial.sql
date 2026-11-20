-- =========================================================================
-- V1__criar_estrutura_inicial.sql
-- Gestão de Frota
-- =========================================================================

-- =========================================================================
-- USUÁRIOS
-- =========================================================================

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL DEFAULT 'OPERADOR',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_usuario_perfil
        CHECK (perfil IN ('ADMINISTRADOR', 'OPERADOR'))
);

-- =========================================================================
-- EMPRESAS
-- Unidade, Fornecedor, Oficina, Recapadora, etc.
-- =========================================================================

CREATE TABLE empresa (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(100) NOT NULL,
    nome_fantasia VARCHAR(100),
    cnpj VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(100),
    contato VARCHAR(100),
    cidade VARCHAR(50),
    estado VARCHAR(2),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================================================
-- MEDIDAS DE PNEU
-- =========================================================================

CREATE TABLE medida_pneu (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(20) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO medida_pneu (descricao)
VALUES
('295/80 R22.5'),
('275/80 R22.5');

-- =========================================================================
-- CAMINHÕES
-- =========================================================================

CREATE TABLE caminhao (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    ano INT,
    km_atual DECIMAL(10,2) NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================================================
-- EIXOS DOS CAMINHÕES
-- =========================================================================

CREATE TABLE eixo_caminhao (
    id BIGSERIAL PRIMARY KEY,
    caminhao_id INT NOT NULL
        REFERENCES caminhao(id)
        ON DELETE CASCADE,
    numero_eixo INT NOT NULL,
    tipo_eixo VARCHAR(20) NOT NULL,
    qtd_pneus_por_lado INT NOT NULL,

    CONSTRAINT uq_eixo_caminhao
        UNIQUE (caminhao_id, numero_eixo),

    CONSTRAINT chk_tipo_eixo
        CHECK (
            tipo_eixo IN (
                'DIRECIONAL',
                'TRACAO'
            )
        ),

    CONSTRAINT chk_qtd_pneus
        CHECK (
            qtd_pneus_por_lado IN (1, 2)
        )
);

-- =========================================================================
-- POSIÇÕES DOS EIXOS
-- =========================================================================

CREATE TABLE posicao_eixo (
    id BIGSERIAL PRIMARY KEY,
    eixo_caminhao_id INT NOT NULL
        REFERENCES eixo_caminhao(id)
        ON DELETE CASCADE,
    lado VARCHAR(10) NOT NULL,
    posicao VARCHAR(10) NOT NULL,

    CONSTRAINT chk_posicao_lado
        CHECK (
            lado IN (
                'ESQUERDO',
                'DIREITO'
            )
        ),

    CONSTRAINT chk_posicao_tipo
        CHECK (
            posicao IN (
                'UNICO',
                'INTERNO',
                'EXTERNO'
            )
        ),

    CONSTRAINT uq_posicao_eixo
        UNIQUE (
            eixo_caminhao_id,
            lado,
            posicao
        )
);

-- =========================================================================
-- PNEUS
-- =========================================================================

CREATE TABLE pneu (
    id BIGSERIAL PRIMARY KEY,
    numero_fogo VARCHAR(20) NOT NULL,
    medida_id INT NOT NULL
        REFERENCES medida_pneu(id),
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50),
    tipo_banda VARCHAR(20),
    estado VARCHAR(20) NOT NULL,
    km_atual DECIMAL(10,2) NOT NULL DEFAULT 0,
    qtd_recapagens INT NOT NULL DEFAULT 0,
    local_atual VARCHAR(30) NOT NULL,
    empresa_id INT
        REFERENCES empresa(id),
    caminhao_id INT
        REFERENCES caminhao(id),
    eixo_caminhao_id INT
        REFERENCES eixo_caminhao(id),
    posicao_eixo_id INT
        REFERENCES posicao_eixo(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

       CONSTRAINT uq_pneu_numero_fogo_medida
        UNIQUE (
            numero_fogo,
            medida_id
        ),

    CONSTRAINT chk_estado_pneu
        CHECK (
            estado IN (
                'NOVO',
                'USADO',
                'RECAPADO',
                'INSERVIVEL'
            )
        ),

    CONSTRAINT chk_local_atual
        CHECK (
            local_atual IN (
                'ESTOQUE_UNIDADE',
                'NO_CAMINHAO',
                'EM_RECAPAGEM',
                'DESCARTADO'
            )
        )
);

-- Impede dois pneus ocuparem a mesma posição

CREATE UNIQUE INDEX uq_pneu_posicao_instalada
ON pneu(posicao_eixo_id)
WHERE local_atual = 'NO_CAMINHAO';

-- =========================================================================
-- MOVIMENTAÇÕES DOS PNEUS
-- =========================================================================

CREATE TABLE movimentacao_pneu (
    id BIGSERIAL PRIMARY KEY,
    pneu_id BIGINT NOT NULL
        REFERENCES pneu(id)
        ON DELETE CASCADE,
    data_movimentacao TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,
    tipo_movimentacao VARCHAR(50) NOT NULL,
    empresa_id INT
        REFERENCES empresa(id),
    caminhao_id INT
        REFERENCES caminhao(id),
    eixo_caminhao_id INT
        REFERENCES eixo_caminhao(id),
    posicao_eixo_id INT
        REFERENCES posicao_eixo(id),
    km_caminhao DECIMAL(10,2),
    valor DECIMAL(12,2),
    observacao TEXT,
    usuario_id INT
        REFERENCES usuario(id),

    CONSTRAINT chk_tipo_movimentacao
        CHECK (
            tipo_movimentacao IN (
                'CADASTRO_INICIAL',
                'COMPRA',
                'INSTALACAO',
                'RETIRADA',
                'RODIZIO',
                'TRANSFERENCIA',
                'ENVIO_RECAPAGEM',
                'RETORNO_RECAPAGEM',
                'DESCARTE'
            )
        )
);

-- =========================================================================
-- ÍNDICES
-- =========================================================================

CREATE INDEX idx_pneu_numero_fogo
    ON pneu(numero_fogo);

CREATE INDEX idx_pneu_local
    ON pneu(local_atual);

CREATE INDEX idx_pneu_empresa
    ON pneu(empresa_id);

CREATE INDEX idx_pneu_caminhao
    ON pneu(caminhao_id);

CREATE INDEX idx_movimentacao_pneu
    ON movimentacao_pneu(pneu_id);

CREATE INDEX idx_movimentacao_data
    ON movimentacao_pneu(data_movimentacao);

CREATE INDEX idx_movimentacao_tipo
    ON movimentacao_pneu(tipo_movimentacao);
          