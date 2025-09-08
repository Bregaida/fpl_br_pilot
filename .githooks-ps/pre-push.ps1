#!/usr/bin/env pwsh
$ErrorActionPreference = 'Stop'

# Se 'make' não estiver no PATH, pular sem erro
if (-not (Get-Command make -ErrorAction SilentlyContinue)) {
  Write-Host "[pre-push] 'make' não encontrado no PATH; pulando validação."
  exit 0
}

Write-Host "[pre-push] validação rápida"
& make backend-lint
& make frontend-lint


