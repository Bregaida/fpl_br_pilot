## Objetivo
Descreva o que este PR entrega.

## Mudanças
- [ ] Backend
- [ ] Frontend
- [ ] Infra/Docs

## Testes
- [ ] Unit
- [ ] Integration
- [ ] Cobertura atualizada

## Como validar
Passos curtos e objetivos para QA.

## Checklist
- [ ] Build verde (GH Actions)
- [ ] Make targets rodam (init/fmt/lint/test)
- [ ] URLs testadas (back e front)

## Screenshots/Logs
(opcional)

## Descrição
Explique o que foi feito e por quê. Contexto, motivação e escopo.

## Tipo de mudança
- [ ] Feature
- [ ] Fix
- [ ] Refactor
- [ ] Docs
- [ ] Chore (build, CI, lint, EOL)

## Como testar
Passos de reprodução, comandos e dados de exemplo.

```bash
# Backend
cd backend
./mvnw clean verify

# Frontend
cd fplbr-frontend
npm ci
npm test -- --coverage
npm run build
```

## Checklist de qualidade
- [ ] Backend: testes e cobertura (JaCoCo ≥ 80%)
- [ ] Frontend: testes e cobertura (c8 ≥ 80%)
- [ ] Lint/fmt aplicados (Java + TS)
- [ ] EOL normalizado (LF) e sem BOM
- [ ] Sem segredos no repo (.env ignorado)
- [ ] CI verde

## Screenshots (se aplicável)
Adicione prints/gifs.

## Breaking changes
- [ ] Sim (descreva migrações)
- [ ] Não

## URLs úteis
- Backend: `/q/health`, `/q/openapi`, `/q/swagger-ui`, `/api/v1/aerodromos?query=SBSP`, `/api/v1/flightplans`
- Frontend: `/`, `/aerodromos`, `/flightplan/novo`, `/flightplan/:id`

## Tarefas futuras (se houver)
Lista de melhorias sugeridas e backlog.


