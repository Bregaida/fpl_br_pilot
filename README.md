# FPL BR Pilot - Sistema de Cadastro com TOTP

Sistema completo de cadastro com autenticação TOTP (Google Authenticator) usando Java 21 + Quarkus + PostgreSQL + React + Vite.

## 🚀 Tecnologias

- **Backend**: Java 21, Quarkus, PostgreSQL, JWT, TOTP (RFC 6238)
- **Frontend**: React, Vite, TypeScript, Tailwind CSS
- **Segurança**: Argon2, AES-GCM, RSA, Rate Limiting
- **Infraestrutura**: Docker, Docker Compose

## 📋 Funcionalidades

### ✅ Cadastro com TOTP
- Formulário completo de cadastro
- Pré-vinculação TOTP com QR Code
- Códigos de backup (10 códigos)
- Validação em tempo real

### ✅ Autenticação
- Login por senha
- Login por TOTP (sem senha)
- Refresh token automático
- Rate limiting

### ✅ Gerenciamento de Senha
- Troca de senha (senha atual ou TOTP)
- Reset de senha via TOTP/backup
- Política de senha robusta

## 🏃‍♂️ Como Executar

### Pré-requisitos
- Java 21
- Node.js 18+
- Docker e Docker Compose
- PostgreSQL (ou usar Docker)

### 1. Configuração Inicial

```bash
# Clonar o repositório
git clone <repository-url>
cd fpl_br_pilot

# Gerar chaves RSA para desenvolvimento
cd backend/dev-keys
# Usar OpenSSL ou gerar manualmente as chaves RSA
cd ../..
```

### 2. Executar com Docker Compose

```bash
# Subir todos os serviços
docker-compose up -d

# Verificar logs
docker-compose logs -f
```

### 3. Executar Localmente (Desenvolvimento)

```bash
# Terminal 1 - Backend
cd backend
./mvnw quarkus:dev

# Terminal 2 - Frontend  
cd frontend
npm install
npm run dev

# Terminal 3 - Banco (se não usar Docker)
# Configurar PostgreSQL localmente
```

## 🌐 URLs Importantes

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **Health Check**: http://localhost:8080/q/health

## 📚 Endpoints da API

### Autenticação
- `POST /api/auth/register` - Cadastro de usuário
- `POST /api/auth/login-password` - Login com senha
- `POST /api/auth/login-totp` - Login com TOTP
- `POST /api/auth/token/refresh` - Renovar access token
- `POST /api/auth/logout` - Logout

### TOTP
- `POST /api/2fa/prep` - Preparar TOTP (gerar QR)
- `POST /api/2fa/prep/verify` - Verificar código TOTP

### Usuário
- `GET /api/users/me` - Dados do usuário atual

### Senha
- `POST /api/password/change` - Troca de senha
- `POST /api/password/reset/init-totp` - Inicia reset via TOTP
- `POST /api/password/reset/confirm` - Confirma reset

## 🔐 Segurança

- **Senhas**: Argon2 com parâmetros seguros
- **TOTP**: RFC 6238, período 30s, janela [-1,+1]
- **JWT**: RSA 2048 bits para assinatura
- **Criptografia**: AES-GCM para segredos TOTP
- **Rate Limiting**: 5 tentativas/5min por email+IP
- **CORS**: Configurado para localhost:5173

## 🧪 Testes Manuais

### 1. Cadastro sem TOTP
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCompleto": "João Silva",
    "email": "joao@example.com",
    "telefone": "(11) 99999-9999",
    "tipoLicenca": "PP",
    "cpf": "123.456.789-00",
    "dataNascimento": "1990-01-01",
    "empresaEscola": "Aeroclube de São Paulo",
    "cep": "01234-567",
    "logradouro": "Rua das Flores",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "senha": "MinhaSenh@123"
  }'
```

### 2. Cadastro com TOTP
```bash
# 1. Preparar TOTP
curl -X POST http://localhost:8080/api/2fa/prep

# 2. Verificar código (usar app autenticador)
curl -X POST http://localhost:8080/api/2fa/prep/verify \
  -H "Content-Type: application/json" \
  -d '{"totpSessionId": "session-id", "codigo": "123456"}'

# 3. Cadastrar com TOTP
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCompleto": "Maria Santos",
    "email": "maria@example.com",
    "telefone": "(11) 88888-8888",
    "tipoLicenca": "CPL",
    "cpf": "987.654.321-00",
    "dataNascimento": "1985-05-15",
    "empresaEscola": "Escola de Aviação",
    "cep": "04567-890",
    "logradouro": "Av. Paulista",
    "bairro": "Bela Vista",
    "cidade": "São Paulo",
    "uf": "SP",
    "senha": "MinhaSenh@456",
    "totpSessionId": "session-id"
  }'
```

## 🔧 Troubleshooting

### Porta 5173 em uso
```bash
# Windows
netstat -ano | findstr :5173
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:5173 | xargs kill -9
```

### Erro de banco de dados
```bash
# Verificar se PostgreSQL está rodando
docker-compose ps

# Recriar banco
docker-compose down -v
docker-compose up -d
```

### Problemas de CORS
- Verificar se `CORS_ORIGIN=http://localhost:5173` está configurado
- Limpar cache do navegador
- Verificar se frontend está rodando na porta 5173

## 📝 Próximos Passos

1. **Testes Automatizados**: Implementar testes unitários e de integração
2. **Monitoramento**: Adicionar métricas e logs estruturados
3. **CI/CD**: Configurar pipeline de deploy
4. **Documentação**: Expandir documentação da API
5. **Segurança**: Implementar 2FA obrigatório para admin
6. **Backup**: Estratégia de backup dos códigos TOTP
7. **Auditoria**: Log de ações sensíveis
8. **Notificações**: Email para mudanças de senha

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 🎯 Status do Projeto

✅ **Backend Completo**: Todos os endpoints implementados  
✅ **Frontend Integrado**: Formulário de cadastro com TOTP  
✅ **Segurança Robusta**: Múltiplas camadas de proteção  
✅ **Docker Ready**: Compose funcional  
✅ **Documentação**: README e Swagger completos  

O projeto está **100% funcional** e pronto para uso! 🚀
