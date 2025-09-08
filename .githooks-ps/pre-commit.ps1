#!/usr/bin/env pwsh
$ErrorActionPreference = 'Stop'
Write-Host "[pre-commit] fmt+lint"
& make fmt
& make lint


