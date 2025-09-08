SHELL := /bin/bash

BACKEND_DIR := backend
FRONTEND_DIR := fplbr-frontend

.PHONY: init fmt lint test back front dev up down commit ac ci info backend-fmt frontend-fmt backend-lint frontend-lint test-back test-front

info:
	@echo "Targets: init fmt lint test back front dev up down commit ac ci"

init:
	@echo "==> Configurando githooks (bash por padrão). Para PowerShell: git config core.hooksPath .githooks-ps"
	@git config core.hooksPath .githooks || true
	@[ -d ".githooks" ] && chmod +x .githooks/* || true
	@if [ -d "$(FRONTEND_DIR)" ]; then cd $(FRONTEND_DIR) && npm install --no-fund --no-audit; fi
	@echo "OK"

fmt: backend-fmt frontend-fmt
lint: backend-lint frontend-lint
test: test-back test-front

backend-fmt:
	@echo "==> Format backend"
	@if [ -f "$(BACKEND_DIR)/mvnw" ]; then \
	  cd $(BACKEND_DIR) && ( ./mvnw -q spotless:apply || ./mvnw -q formatter:format || true ); \
	else \
	  cd $(BACKEND_DIR) && ( mvn -q spotless:apply || mvn -q formatter:format || true ); \
	fi

frontend-fmt:
	@echo "==> Format frontend"
	@if [ -d "$(FRONTEND_DIR)" ]; then \
	  cd $(FRONTEND_DIR) && ( npm run format || npx --yes prettier -w . ); \
	fi

backend-lint:
	@echo "==> Lint backend (rápido)"
	@if [ -f "$(BACKEND_DIR)/mvnw" ]; then \
	  cd $(BACKEND_DIR) && ( ./mvnw -q spotless:check checkstyle:check || ./mvnw -q -DskipTests -DskipITs package ); \
	else \
	  cd $(BACKEND_DIR) && ( mvn -q spotless:check checkstyle:check || mvn -q -DskipTests -DskipITs package ); \
	fi

frontend-lint:
	@echo "==> Lint frontend"
	@if [ -d "$(FRONTEND_DIR)" ]; then \
	  cd $(FRONTEND_DIR) && ( npm run lint || npx --yes eslint . ); \
	fi

test-back:
	@echo "==> Tests backend (com ITs)"
	@if [ -f "$(BACKEND_DIR)/mvnw" ]; then \
	  cd $(BACKEND_DIR) && ./mvnw -q clean verify -DskipITs=false; \
	else \
	  cd $(BACKEND_DIR) && mvn -q clean verify -DskipITs=false; \
	fi

test-front:
	@echo "==> Tests frontend (com cobertura)"
	@if [ -d "$(FRONTEND_DIR)" ]; then \
	  cd $(FRONTEND_DIR) && npm test -- --coverage; \
	fi

back:
	@echo "==> Quarkus dev (8080)"
	@if [ -f "$(BACKEND_DIR)/mvnw" ]; then \
	  cd $(BACKEND_DIR) && ./mvnw -q quarkus:dev; \
	else \
	  cd $(BACKEND_DIR) && mvn -q quarkus:dev; \
	fi

front:
	@echo "==> Vite dev (5173)"
	@cd $(FRONTEND_DIR) && npm i --no-fund --no-audit && npm run dev -- --port 5173

dev:
	@echo "==> Backend + Frontend (concurrently)"
	@npx --yes concurrently -k -n backend,frontend -c blue,magenta \
	"cd $(BACKEND_DIR) && ./mvnw -q quarkus:dev" \
	"cd $(FRONTEND_DIR) && npm i --no-fund --no-audit && npm run dev -- --port 5173"

up:
	@echo "==> docker compose up"
	@(docker compose up -d || docker-compose up -d)

down:
	@echo "==> docker compose down"
	@(docker compose down || docker-compose down)

# Uso: make commit m="feat: mensagem"
commit:
	@git add -A
	@git commit -m "$${m:-chore: auto-commit}"
	@echo "Commit criado."

ac:
	@$(MAKE) commit --no-print-directory
	@branch="$$(git rev-parse --abbrev-ref HEAD)"; git push -u origin "$$branch" || git push -u origin HEAD
	@echo "Pushed para origem."

ci: fmt lint test
