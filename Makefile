SHELL := /bin/bash

.PHONY: backend frontend dev test test-back test-front init lint fmt backend-lint frontend-lint backend-fmt frontend-fmt backend-test frontend-test up down

backend:
	cd backend && ./mvnw -q quarkus:dev

frontend:
	cd fplbr-frontend && npm i && npm run dev -- --port 5173

dev:
	npx concurrently -k -n backend,frontend -c blue,magenta \
	"cd backend && ./mvnw -q quarkus:dev" \
	"cd fplbr-frontend && npm i && npm run dev -- --port 5173"

test-back:
	cd backend && ./mvnw -q clean verify

test-front:
	cd fplbr-frontend && npm i && npm test -- --coverage

test: test-back test-front
	@echo "✅ Testes backend+frontend finalizados."

# =========================
# Alvos adicionais padronizados
# =========================

init:
	@echo "==> Instalando dependências e configurando hooks"
	@if [ -d "fplbr-frontend" ]; then cd fplbr-frontend && npm install --no-audit --no-fund; fi
	@git config core.hooksPath .githooks
	@[ -f .githooks/pre-commit ] && chmod +x .githooks/pre-commit || true
	@echo "OK"

lint: backend-lint frontend-lint

fmt: backend-fmt frontend-fmt

backend-lint:
	@echo "==> Lint backend (Spotless/Checkstyle se configurado; fallback formatter)"
	@if [ -f "backend/mvnw" ]; then \
	  cd backend && ( ./mvnw -q spotless:check checkstyle:check || ./mvnw -q formatter:validate || ./mvnw -q -DskipTests -DskipITs package ); \
	else \
	  cd backend && ( mvn -q spotless:check checkstyle:check || mvn -q formatter:validate || mvn -q -DskipTests -DskipITs package ); \
	fi

frontend-lint:
	@echo "==> Lint frontend (ESLint)"
	@if [ -d "fplbr-frontend" ]; then \
	  cd fplbr-frontend && ( npm run lint || npx --yes eslint . ); \
	fi

backend-fmt:
	@echo "==> Format backend (Spotless/Formatter)"
	@if [ -f "backend/mvnw" ]; then \
	  cd backend && ( ./mvnw -q spotless:apply || ./mvnw -q formatter:format ); \
	else \
	  cd backend && ( mvn -q spotless:apply || mvn -q formatter:format ); \
	fi

frontend-fmt:
	@echo "==> Format frontend (Prettier)"
	@if [ -d "fplbr-frontend" ]; then \
	  cd fplbr-frontend && ( npm run format || npx --yes prettier -w . ); \
	fi

backend-test:
	@echo "==> Backend tests (com cobertura)"
	@if [ -f "backend/mvnw" ]; then \
	  cd backend && ./mvnw -q clean verify -DskipITs=false; \
	else \
	  cd backend && mvn -q clean verify -DskipITs=false; \
	fi

frontend-test:
	@echo "==> Frontend tests (com cobertura)"
	@if [ -d "fplbr-frontend" ]; then \
	  cd fplbr-frontend && npm test -- --coverage; \
	fi

up:
	@echo "==> Subindo ambiente com docker compose"
	@(docker compose up -d || docker-compose up -d)

down:
	@echo "==> Derrubando ambiente com docker compose"
	@(docker compose down || docker-compose down)
