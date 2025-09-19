# tools/verify-build-push.ps1
# Executa: backend verify -> (se falhar, detecta BOM) -> frontend build -> squash+push main (somente se consistente)
# Uso: powershell -NoProfile -ExecutionPolicy Bypass -File tools\verify-build-push.ps1

$ErrorActionPreference = 'Stop'
$repoRoot = Get-Location

function Title($t){ Write-Host "`n=== $t ===" -ForegroundColor Cyan }

function Run-BackendVerify {
  Title "Backend: mvnw clean verify (com ITs)"
  Push-Location backend
  try {
    if (Test-Path .\mvnw.cmd) { & .\mvnw.cmd -q clean verify -DskipITs=false } else { & mvn -q clean verify -DskipITs=false }
    if ($LASTEXITCODE -ne 0) {
      Write-Host "[ERRO] Backend verify retornou codigo $LASTEXITCODE." -ForegroundColor Red
      return $false
    }
    return $true
  } catch {
    Write-Host "[ERRO] Backend verify falhou (excecao)." -ForegroundColor Red
    return $false
  } finally {
    Pop-Location
  }
}

function Detect-BOM {
  # Varre apenas .java do backend (main e test), ignora target/.git/node_modules
  Title "Varredura BOM (UTF-8 EF BB BF) em .java do backend"
  $roots = @("backend/src/main/java","backend/src/test/java")
  $javaFiles = foreach ($r in $roots) {
    if (Test-Path $r) {
      Get-ChildItem -Path $r -Recurse -File -Filter *.java |
        Where-Object { $_.FullName -notmatch '\\(target|node_modules|\.git)\\' }
    }
  }
  $withBom = @()
  foreach ($f in $javaFiles) {
    try {
      $fs = [System.IO.File]::Open($f.FullName,'Open','Read','ReadWrite')
      try {
        if ($fs.Length -ge 3) {
          $buf = New-Object byte[] 3
          $null = $fs.Read($buf,0,3)
          if ($buf[0] -eq 0xEF -and $buf[1] -eq 0xBB -and $buf[2] -eq 0xBF) { $withBom += $f.FullName }
        }
      } finally { $fs.Dispose() }
    } catch { }
  }
  if ($withBom.Count -gt 0) {
    $withBom | Out-File -Encoding utf8 .\backend-bom-list.txt
    Write-Host "[ATENCAO] Arquivos com BOM detectados: $($withBom.Count). Lista salva em backend-bom-list.txt" -ForegroundColor Yellow
  } else {
    Write-Host "[OK] Nenhum BOM detectado em .java do backend." -ForegroundColor Green
  }
  return $withBom
}

function Run-FrontendBuild {
  Title "Frontend: instalar deps e build"
  Push-Location frontend
  try {
    $installed = $false
    if (Test-Path package-lock.json) {
      & npm ci
      if ($LASTEXITCODE -eq 0) { $installed = $true } else { Write-Host "[WARN] npm ci falhou (codigo $LASTEXITCODE). Tentando npm install..." -ForegroundColor Yellow }
    }
    if (-not $installed) {
      & npm install
      if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERRO] npm install falhou (codigo $LASTEXITCODE)." -ForegroundColor Red
        return $false
      }
    }
    & npm run build
    if ($LASTEXITCODE -ne 0) {
      Write-Host "[ERRO] npm run build falhou (codigo $LASTEXITCODE)." -ForegroundColor Red
      return $false
    }
    return $true
  } catch {
    Write-Host "[ERRO] Frontend build falhou (excecao)." -ForegroundColor Red
    return $false
  } finally {
    Pop-Location
  }
}

function Git-WorkingTreeClean {
  Title "Git: verificando working tree"
  $status = (git status --porcelain)
  if ([string]::IsNullOrWhiteSpace($status)) {
    Write-Host "[OK] Working tree limpo." -ForegroundColor Green
    return $true
  } else {
    Write-Host "[ATENCAO] Working tree sujo (arquivos pendentes):" -ForegroundColor Yellow
    $status
    return $false
  }
}

function Git-SquashPushMain {
  Title "Git: squash e push - main"
  & git fetch origin
  if ($LASTEXITCODE -ne 0) { Write-Host "[ERRO] git fetch falhou." -ForegroundColor Red; return $false }

  & git checkout main
  if ($LASTEXITCODE -ne 0) { Write-Host "[ERRO] git checkout main falhou." -ForegroundColor Red; return $false }

  & git pull --rebase
  if ($LASTEXITCODE -ne 0) { Write-Host "[ERRO] git pull --rebase falhou. Verifique working tree limpo." -ForegroundColor Red; return $false }

  & git reset --soft origin/main
  if ($LASTEXITCODE -ne 0) { Write-Host "[ERRO] git reset --soft origin/main falhou." -ForegroundColor Red; return $false }

  & git add -A
  if ($LASTEXITCODE -ne 0) { Write-Host "[ERRO] git add falhou." -ForegroundColor Red; return $false }

  & git commit -m "chore: normalize EOL & remove BOM; fix tests; backend verify (>=80%); frontend build"
  if ($LASTEXITCODE -ne 0) { Write-Host "[ERRO] git commit falhou (hook ou validacao)." -ForegroundColor Red; return $false }

  & git push -f origin HEAD:main
  if ($LASTEXITCODE -ne 0) { Write-Host "[ERRO] git push falhou." -ForegroundColor Red; return $false }

  Write-Host "[OK] Squash + push concluido na main." -ForegroundColor Green
  return $true
}

# ================== Fluxo principal ==================
Title "Inicio"

$backendOk = Run-BackendVerify
$backendBomList = @()
if (-not $backendOk) { $backendBomList = Detect-BOM }

$frontendOk = Run-FrontendBuild

# Consistencia para push: push SO se: backend OK, frontend OK e working tree limpo
$wtClean = Git-WorkingTreeClean
$canPush = $backendOk -and $frontendOk -and $wtClean
$pushOk = $false
if ($canPush) {
  $pushOk = Git-SquashPushMain
} else {
  Write-Host "[SKIP] Push na main NAO sera feito porque o repositorio nao esta consistente." -ForegroundColor Yellow
}

# Resumo final
Title "Resumo"
Write-Host ("Backend verify: {0}" -f ($(if ($backendOk){"OK"}else{"FALHOU"})))
if (-not $backendOk -and $backendBomList.Count -gt 0) { Write-Host ("Arquivos com BOM: {0} (backend-bom-list.txt)" -f $backendBomList.Count) }
Write-Host ("Frontend build: {0}" -f ($(if ($frontendOk){"OK"}else{"FALHOU"})))
Write-Host ("Working tree limpo: {0}" -f ($(if ($wtClean){"SIM"}else{"NAO"})))
Write-Host ("Push main realizado: {0}" -f ($(if ($pushOk){"SIM"}else{"NAO"})))

Write-Host "`nURLs para testar localmente:" -ForegroundColor Green
Write-Host "BACKEND:"
Write-Host "  http://localhost:8080/q/health"
Write-Host "  http://localhost:8080/q/openapi"
Write-Host "  http://localhost:8080/q/swagger-ui"
Write-Host "  http://localhost:8080/api/v1/aerodromos?query=SBSP"
Write-Host "  http://localhost:8080/api/v1/flightplans"
Write-Host "FRONTEND:"
Write-Host "  http://localhost:5173/"
Write-Host "  http://localhost:5173/aerodromos"
Write-Host "  http://localhost:5173/flightplan/novo"
Write-Host "  http://localhost:5173/flightplan/123"

Write-Host "`nFeito."
