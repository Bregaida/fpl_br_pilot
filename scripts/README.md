# Scripts de Configuração - FPL BR Pilot

## Configuração do PostgreSQL

### Pré-requisitos

1. **PostgreSQL instalado** (versão 12 ou superior)
2. **Usuário postgres** com privilégios de administrador
3. **Serviço PostgreSQL** rodando

### Instalação do PostgreSQL

#### Windows
1. Baixe o PostgreSQL em: https://www.postgresql.org/download/windows/
2. Execute o instalador com as configurações padrão
3. Anote a senha do usuário `postgres`

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Linux (CentOS/RHEL)
```bash
sudo yum install postgresql postgresql-server
sudo postgresql-setup initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### macOS
```bash
brew install postgresql
brew services start postgresql
```

### Configuração do Banco

#### Windows
```cmd
cd scripts
setup-postgresql.bat
```

#### Linux/macOS
```bash
cd scripts
./setup-postgresql.sh
```

#### Manual
```bash
psql -U postgres -f setup-postgresql.sql
```

### Verificação

Após a configuração, verifique se tudo está funcionando:

```bash
# Testar conexão
psql -U fpl_user -d fpl -c "SELECT current_database(), current_user;"

# Verificar tabelas (após iniciar o backend)
psql -U fpl_user -d fpl -c "\dt"
```

### Configuração do Backend

1. **Iniciar o backend**:
   ```bash
   cd backend
   ./mvnw quarkus:dev
   ```

2. **Verificar health check**:
   ```bash
   curl http://localhost:8080/q/health
   ```

3. **Verificar OpenAPI**:
   ```bash
   curl http://localhost:8080/q/openapi
   ```

### Configuração do SQLTools

1. **Instalar extensões no VS Code**:
   - SQLTools
   - SQLTools PostgreSQL Driver

2. **Conectar ao banco**:
   - Pressione `Ctrl+Shift+P`
   - Digite "SQLTools: Connect"
   - Selecione "FPL BR Pilot - PostgreSQL"

3. **Executar queries**:
   - Abra `.vscode/rotaer-queries.sql`
   - Selecione uma query e pressione `Ctrl+E`

### Troubleshooting

#### Erro de Conexão
```bash
# Verificar se o PostgreSQL está rodando
sudo systemctl status postgresql  # Linux
brew services list | grep postgresql  # macOS
```

#### Erro de Permissão
```bash
# Conceder privilégios manualmente
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE fpl TO fpl_user;"
```

#### Erro de Senha
```bash
# Alterar senha do usuário postgres
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'nova_senha';"
```

### Estrutura do Banco

Após iniciar o backend, as seguintes tabelas serão criadas automaticamente:

- `aerodromos` - Informações dos aeródromos
- `pistas` - Pistas de pouso e decolagem
- `frequencias` - Frequências de comunicação
- `aerodromos_completos` - Dados completos do ROTAER
- `pistas_completas` - Pistas completas do ROTAER
- E outras tabelas relacionadas

### URLs Úteis

- **Health Check**: http://localhost:8080/q/health
- **OpenAPI**: http://localhost:8080/q/openapi
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **API Aeródromos**: http://localhost:8080/api/v1/aerodromos
- **API ROTAER**: http://localhost:8080/api/v1/rotaer/aerodromos