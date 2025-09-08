#!/usr/bin/env pwsh
$ErrorActionPreference = 'Stop'
$branch = (git rev-parse --abbrev-ref HEAD).Trim()
Write-Host "[post-commit] push automático -> origin/$branch"
git push -u origin $branch | Out-Null


