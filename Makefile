.PHONY: backend frontend dev test

backend:
	cd backend && ./mvnw -q quarkus:dev

frontend:
	cd fplbr-frontend && npm i && npm run dev -- --port 5173

dev:
	( cd backend && ./mvnw -q quarkus:dev ) & ( cd fplbr-frontend && npm i && npm run dev -- --port 5173 )

test:
	cd backend && ./mvnw -q clean verify && cd ../fplbr-frontend && npm i && npm run test
