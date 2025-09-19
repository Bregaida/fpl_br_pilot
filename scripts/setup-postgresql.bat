@echo off
echo =============================================
echo CONFIGURACAO DO POSTGRESQL - FPL BR Pilot
echo =============================================
echo.

echo Verificando se o PostgreSQL esta instalado...
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: PostgreSQL nao encontrado!
    echo.
    echo Por favor, instale o PostgreSQL:
    echo 1. Baixe em: https://www.postgresql.org/download/windows/
    echo 2. Instale com as configuracoes padrao
    echo 3. Execute este script novamente
    echo.
    pause
    exit /b 1
)

echo PostgreSQL encontrado!
echo.

echo Executando script de configuracao...
psql -U postgres -f setup-postgresql.sql

if %errorlevel% equ 0 (
    echo.
    echo =============================================
    echo CONFIGURACAO CONCLUIDA COM SUCESSO!
    echo =============================================
    echo.
    echo Banco de dados: fpl
    echo Usuario: fpl_user
    echo Senha: fpl_pass
    echo.
    echo Agora voce pode:
    echo 1. Iniciar o backend: ./mvnw quarkus:dev
    echo 2. Conectar via SQLTools no VS Code
    echo 3. Executar as queries em .vscode/rotaer-queries.sql
    echo.
) else (
    echo.
    echo =============================================
    echo ERRO NA CONFIGURACAO!
    echo =============================================
    echo.
    echo Verifique se:
    echo 1. O PostgreSQL esta rodando
    echo 2. O usuario postgres tem privilegios
    echo 3. A senha do postgres esta correta
    echo.
)

pause
