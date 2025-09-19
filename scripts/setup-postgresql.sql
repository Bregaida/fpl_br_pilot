-- =============================================
-- SCRIPT DE CONFIGURAÇÃO DO POSTGRESQL
-- FPL BR Pilot - Banco de Dados
-- =============================================

-- 1. Criar banco de dados
CREATE DATABASE fpl;

-- 2. Criar usuário
CREATE USER fpl_user WITH PASSWORD 'fpl_pass';

-- 3. Conceder privilégios
GRANT ALL PRIVILEGES ON DATABASE fpl TO fpl_user;

-- 4. Conectar ao banco fpl
\c fpl;

-- 5. Conceder privilégios no schema public
GRANT ALL ON SCHEMA public TO fpl_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO fpl_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO fpl_user;

-- 6. Configurar privilégios padrão para objetos futuros
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO fpl_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO fpl_user;

-- 7. Verificar configuração
SELECT 
    datname as database_name,
    datowner as owner_id,
    pg_catalog.pg_get_userbyid(datowner) as owner_name
FROM pg_catalog.pg_database 
WHERE datname = 'fpl';

-- 8. Verificar usuário
SELECT 
    usename as username,
    usesuper as is_superuser,
    usecreatedb as can_create_db
FROM pg_user 
WHERE usename = 'fpl_user';

-- 9. Verificar conexão
SELECT current_database(), current_user, version();
