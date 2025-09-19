# ================================
# Makefile - fpl_br_pilot
# ================================
# Scripts de execução para desenvolvimento local

.PHONY: help backend frontend dev clean

# Comando padrão
help:
	@echo "Comandos disponíveis:"
	@echo "  make backend  - Executa o backend Quarkus"
	@echo "  make frontend - Executa o frontend Vite/React"
	@echo "  make dev      - Executa backend e frontend em paralelo"
	@echo "  make clean    - Limpa arquivos temporários"

# Backend - Quarkus
backend:
	@echo "🚀 Iniciando backend Quarkus..."
	cd backend && ./mvnw -q quarkus:dev

# Frontend - Vite/React
frontend:
	@echo "🎨 Iniciando frontend Vite/React..."
	cd frontend && npm install && npm run dev -- --port 5173

# Desenvolvimento - Backend + Frontend em paralelo
dev:
	@echo "🔥 Iniciando ambiente de desenvolvimento completo..."
	npx concurrently -k -n backend,frontend -c blue,magenta \
		"cd backend && ./mvnw -q quarkus:dev" \
		"cd frontend && npm install && npm run dev -- --port 5173"


# Limpeza
clean:
	@echo "🧹 Limpando arquivos temporários..."
	cd backend && ./mvnw clean
	cd frontend && rm -rf node_modules dist