# ================================
# Makefile - fpl_br_pilot
# ================================
# Scripts de execução para desenvolvimento local

.PHONY: help backend frontend dev clean install

# Comando padrão
help:
	@echo "Comandos disponíveis:"
	@echo "  backend    - Executa o backend Quarkus em modo dev"
	@echo "  frontend   - Executa o frontend React em modo dev"
	@echo "  dev        - Executa backend e frontend simultaneamente"
	@echo "  clean      - Limpa builds e dependências"
	@echo "  install    - Instala dependências do frontend"

# Backend - Quarkus
backend:
	@echo "🚀 Iniciando backend Quarkus..."
	cd backend && mvnw.cmd -q quarkus:dev

# Frontend - React/Vite
frontend:
	@echo "🌐 Iniciando frontend React..."
	cd frontend && npm install && npm run dev -- --port 5173

# Desenvolvimento completo (backend + frontend)
dev:
	@echo "🔧 Iniciando ambiente de desenvolvimento completo..."
	@echo "Backend: http://localhost:8080"
	@echo "Frontend: http://localhost:5173"
	@echo "Swagger: http://localhost:8080/q/swagger-ui"
	@echo "Health: http://localhost:8080/q/health"
	@echo ""
	@echo "Pressione Ctrl+C para parar ambos os serviços"
	npx concurrently -k -n backend,frontend -c blue,magenta "cd backend && mvnw.cmd -q quarkus:dev" "cd frontend && npm install && npm run dev -- --port 5173"

# Limpeza
clean:
	@echo "🧹 Limpando builds e dependências..."
	cd backend && mvnw.cmd clean
	cd frontend && rm -rf node_modules dist
	@echo "✅ Limpeza concluída"

# Instalação de dependências
install:
	@echo "📦 Instalando dependências do frontend..."
	cd frontend && npm install
	@echo "✅ Dependências instaladas"

# URLs de acesso
urls:
	@echo "🔗 URLs de acesso:"
	@echo "Backend:"
	@echo "  - Health: http://localhost:8080/q/health"
	@echo "  - OpenAPI: http://localhost:8080/q/openapi"
	@echo "  - Swagger: http://localhost:8080/q/swagger-ui"
	@echo "  - API: http://localhost:8080/api/v1/aerodromos?query=SBSP"
	@echo ""
	@echo "Frontend:"
	@echo "  - Home: http://localhost:5173/"
	@echo "  - Aerodromos: http://localhost:5173/aerodromos"
	@echo "  - Novo Flight Plan: http://localhost:5173/flightplan/novo"
	@echo "  - Flight Plan: http://localhost:5173/flightplan/123"