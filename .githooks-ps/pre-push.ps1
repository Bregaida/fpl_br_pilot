#!/usr/bin/env pwsh
$ErrorActionPreference = 'Stop'
Write-Host "[pre-push] validação rápida"
& make backend-lint
& make frontend-lint


