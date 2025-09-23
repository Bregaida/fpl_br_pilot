-- Migração para tabela de credenciais com AES-256-GCM
-- Substitui a tabela encrypted_credentials antiga

-- Remover tabela antiga se existir
DROP TABLE IF EXISTS encrypted_credentials;

-- Criar nova tabela de credenciais
CREATE TABLE credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service VARCHAR(100) UNIQUE NOT NULL,
    iv BYTEA NOT NULL,
    ciphertext BYTEA NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Criar índices para performance
CREATE INDEX idx_credentials_service ON credentials(service);
CREATE INDEX idx_credentials_created_at ON credentials(created_at);

-- Comentários para documentação
COMMENT ON TABLE credentials IS 'Tabela para armazenar credenciais criptografadas com AES-256-GCM';
COMMENT ON COLUMN credentials.service IS 'Nome do serviço (ex: aisweb, oauth)';
COMMENT ON COLUMN credentials.iv IS 'Initialization Vector (12 bytes para GCM)';
COMMENT ON COLUMN credentials.ciphertext IS 'Dados criptografados com tag de autenticação';
