$ErrorActionPreference = 'Stop'

# --- caminhos ---
$root = (Get-Location)
$ap = Join-Path $root "modulo-aplicacao"
$apMain = Join-Path $ap "src\main\java"
$apTest = Join-Path $ap "src\test\java"

# cria test root
New-Item -ItemType Directory -Force -Path $apTest | Out-Null

# 1) mover arquivos de teste do main → test (padrões comuns)
$patterns = @("*.Test.java","*Test.java","*Tests.java","*IT.java")
foreach ($p in $patterns) {
  Get-ChildItem -Path $apMain -Recurse -Filter $p | ForEach-Object {
    $rel = $_.FullName.Substring($apMain.Length).TrimStart('\')
    $dest = Join-Path $apTest $rel
    New-Item -ItemType Directory -Force -Path (Split-Path $dest -Parent) | Out-Null
    git mv $_.FullName $dest
  }
}

# 2) normalizar pacotes (substituições de texto)
# mapeamentos conhecidos que precisam unificar no novo raiz
$replacements = @(
  @{ from = "fplbr\.pilot\.fpl\.aplicacao"; to = "br.com.fplbr.pilot.fpl.aplicacao" },
  @{ from = "br\.com\.fpl\.aplicacao\.dto\.fpl"; to = "br.com.fplbr.pilot.fpl.aplicacao.dto" },
  @{ from = "br\.com\.fpl\.aplicacao"; to = "br.com.fplbr.pilot.fpl.aplicacao" }
)

function Replace-InFile {
  param([string]$file)
  $txt = Get-Content -Raw -LiteralPath $file
  if ($null -eq $txt) { $txt = "" }
  $orig = $txt
  foreach ($r in $replacements) {
    # somente em package/import para evitar reintroduzir prefixos
    $txt = [regex]::Replace($txt, "(?m)^\s*package\s+$($r.from)\b", "package $($r.to)")
    $txt = [regex]::Replace($txt, "(?m)^\s*import\s+$($r.from)\b",  "import $($r.to)")
    if ($r.from -ne "fplbr\.pilot\.fpl\.aplicacao") {
      # para casos específicos (ex.: dto.fpl) podemos substituir em qualquer lugar
      $txt = [regex]::Replace($txt, $r.from, $r.to)
    }
  }
  # normalização defensiva: colapsar repetições de br.com. antes de fplbr
  $txt = [regex]::Replace($txt, "(?m)^(\s*(package|import)\s+)(?:br\.com\.)+fplbr", '${1}br.com.fplbr')
  if ($txt -ne $orig) { Set-Content -LiteralPath $file -Value $txt -Encoding UTF8 }
}

# aplica a troca a todos os .java do módulo de aplicação (main + test)
Get-ChildItem -Path (Join-Path $ap "src") -Recurse -Filter "*.java" |
  ForEach-Object { Replace-InFile -file $_.FullName }

# 3) reposicionar fisicamente os arquivos conforme o package
function Move-ByPackage {
  param([string]$file)
  $content = Get-Content -Raw -LiteralPath $file
  if ($content -match '(?m)^\s*package\s+([a-z0-9_.]+)\s*;') {
    $pkg = $matches[1]
    $base = if ($file -like "*\src\main\java\*") { $apMain } else { $apTest }
    $destDir = Join-Path $base ($pkg -replace '\.', '\\')
    New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    $dest = Join-Path $destDir (Split-Path $file -Leaf)
    $current = (Resolve-Path $file).Path
    $target = (Resolve-Path $dest -ErrorAction SilentlyContinue).Path
    if ($target -ne $current) {
      git mv $file $dest
    }
  }
}

# move todos os fontes para diretórios condizentes com seu package
Get-ChildItem -Path (Join-Path $ap "src") -Recurse -Filter "*.java" |
  ForEach-Object { Move-ByPackage -file $_.FullName }

git status
Write-Host "`nOK - Estrutura e pacotes normalizados no modulo-aplicacao."
