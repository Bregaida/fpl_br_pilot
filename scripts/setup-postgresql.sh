#!/bin/bash

echo "============================================="
echo "CONFIGURAÇÃO DO POSTGRESQL - FPL BR Pilot"
echo "============================================="
echo

echo "Verificando se o PostgreSQL está instalado..."
if ! command -v psql &> /dev/null; then
    echo "ERRO: PostgreSQL não encontrado!"
    echo
    echo "Por favor, instale o PostgreSQL:"
    echo "Ubuntu/Debian: sudo apt-get install postgresql postgresql-contrib"
    echo "CentOS/RHEL: sudo yum install postgresql postgresql-server"
    echo "macOS: brew install postgresql"
    echo
    exit 1
fi

echo "PostgreSQL encontrado!"
echo

echo "Executando script de configuração..."
psql -U postgres -f setup-postgresql.sql

if [ $? -eq 0 ]; then
    echo
    echo "============================================="
    echo "CONFIGURAÇÃO CONCLUÍDA COM SUCESSO!"
    echo "============================================="
    echo
    echo "Banco de dados: fpl"
    echo "Usuário: fpl_user"
    echo "Senha: fpl_pass"
    echo
    echo "Agora você pode:"
    echo "1. Iniciar o backend: ./mvnw quarkus:dev"
    echo "2. Conectar via SQLTools no VS Code"
    echo "3. Executar as queries em .vscode/rotaer-queries.sql"
    echo
else
    echo
    echo "============================================="
    echo "ERRO NA CONFIGURAÇÃO!"
    echo "============================================="
    echo
    echo "Verifique se:"
    echo "1. O PostgreSQL está rodando"
    echo "2. O usuário postgres tem privilégios"
    echo "3. A senha do postgres está correta"
    echo
fi
