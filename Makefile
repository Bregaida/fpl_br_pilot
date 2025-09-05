SHELL := /bin/bash

.PHONY: backend frontend dev test test-back test-front

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
