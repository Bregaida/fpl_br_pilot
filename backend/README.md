## PostgreSQL (Docker) + Quarkus (Dev/Prod)

### Subir banco com Docker

```bash
docker compose up -d db
# opcional: pgAdmin em http://localhost:8081
```

Volume persistente: `fpl_pgdata`

### Variáveis (.env)
Crie um arquivo `.env` (na raiz do repo) ou use `backend/.env.example` como referência:

```env
POSTGRES_DB=fpl
POSTGRES_USER=fpl_user
POSTGRES_PASSWORD=fpl_pass
JDBC_URL=jdbc:postgresql://localhost:5432/fpl
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USER=
SMTP_PASS=
APP_SECURITY_PASSWORD_PEPPER=changeme_pepper
APP_SECURITY_TOTP_CRYPTO_KEY=changeme_crypto_key
JWT_SIGN_KEY=changeme_jwt_dev
JWT_VERIFY_KEY=changeme_jwt_dev
CORS_ORIGINS=http://localhost:5173,http://localhost:5180
SEED_ENABLED=true
SEED_ADMIN_EMAIL=admin@fpl.dev
SEED_ADMIN_PASSWORD=Admin!234
SEED_ADMIN_TOTP_SECRET=
```

### Limpeza de cache Maven (quando falhar resolução do Flyway)

```bash
rm -rf ~/.m2/repository/org/flywaydb
cd backend
./mvnw -U -DskipTests clean verify
```

### Quarkus Dev

```bash
cd backend
./mvnw -U -DskipTests quarkus:dev
```

- Flyway migra automaticamente em `dev` e `prod`.
- Datasource em `application.properties` e overrides por env.

### Produção
- Use `application-prod.properties` e defina `POSTGRES_*`/`JDBC_URL` via env.
- Ative JWT RS256 e segredos via secret manager.

### Migrações
- Em `src/main/resources/db/migration` (ex.: `V1__init_auth.sql`).
- Próximas: `V2__*.sql`, `V3__*.sql`.

### URLs úteis
- Health: `http://localhost:8080/q/health`
- OpenAPI: `http://localhost:8080/q/openapi`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
