#!/usr/bin/env pwsh
$ErrorActionPreference = 'Stop'
Write-Host "[post-merge] npm i no frontend"
if (Test-Path "frontend") { Push-Location frontend; npm i --no-fund --no-audit; Pop-Location }


