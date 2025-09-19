@echo off
echo Iniciando Redis...
docker run -d --name fpl_redis -p 6379:6379 redis:7-alpine
echo Redis iniciado na porta 6379
pause
