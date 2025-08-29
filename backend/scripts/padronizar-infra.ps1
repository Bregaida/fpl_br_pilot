$ErrorActionPreference = 'Stop'

# --- caminhos ---
$root = (Get-Location)
$mod = "modulo-infraestrutura"
$mf = Join-Path $root $mod
$mainRoot = Join-Path $mf "src\main\java"
$testRoot = Join-Path $mf "src\test\java"
New-Item -ItemType Directory -Force -Path $testRoot | Out-Null

# 1) mover testes (se alguém salvou testes em main)
$patterns = @("*.Test.java","*Test.java","*Tests.java","*IT.java")
foreach ($p in $patterns) {
  Get-ChildItem -Path $mainRoot -Recurse -Filter $p | ForEach-Object {
    $rel = $_.FullName.Substring($mainRoot.Length).TrimStart('\\')
    $dest = Join-Path $testRoot $rel
    New-Item -ItemType Directory -Force -Path (Split-Path $dest -Parent) | Out-Null
    git mv $_.FullName $dest
  }
}

# 2) normalizar pacotes (substituições)
# ajuste/complete os "from" caso existam outros prefixos antigos no projeto
$replacements = @(
  # raízes antigas comuns -> nova raiz de infraestrutura
  @{ from = "fplbr\.pilot\.fpl\.infraestrutura"; to = "br.com.fplbr.pilot.fpl.infraestrutura" },
  @{ from = "br\.com\.fpl\.infraestrutura";       to = "br.com.fplbr.pilot.fpl.infraestrutura" },
  @{ from = "br\.com\.fplbr\.pilot\.fpl\.infra";  to = "br.com.fplbr.pilot.fpl.infraestrutura" },
  @{ from = "br\.com\.fpl\.aplicacao\.web";       to = "br.com.fplbr.pilot.fpl.infraestrutura.rest" }
)

# mapeamentos por "papel" (opcional, recomendado)
# controllers → infraestrutura.rest ; adapters → infraestrutura.adapters ; clients → infraestrutura.restclients
$roleMaps = @(
  @{ from = "(\.controller[s]?)"; to = ".infraestrutura.rest" },
  @{ from = "(\.resource[s]?)";   to = ".infraestrutura.rest" },
  @{ from = "(\.adapter[s]?)";    to = ".infraestrutura.adapters" },
  @{ from = "(\.client[s]?)";     to = ".infraestrutura.restclients" }
)

function Replace-InFile {
  param([string]$file)
  $txt = Get-Content -Raw -LiteralPath $file
  if ($null -eq $txt) { $txt = "" }
  $orig = $txt

  # trocas de raiz
  foreach ($r in $replacements) {
    $txt = [regex]::Replace($txt, "(?m)\\bpackage\\s+$($r.from)\\b", "package $($r.to)")
    $txt = [regex]::Replace($txt, "(?m)\\bimport\\s+$($r.from)\\b",  "import $($r.to)")
    $txt = [regex]::Replace($txt, $r.from, $r.to)
  }

  # trocas de papéis (somente em declarações package/import)
  foreach ($rm in $roleMaps) {
    $txt = [regex]::Replace($txt, "(?m)\\bpackage\\s+(.*)$($rm.from)\\b", { param($m) "package $($m.Groups[1].Value)$($rm.to)" })
    $txt = [regex]::Replace($txt, "(?m)\\bimport\\s+(.*)$($rm.from)\\b",  { param($m) "import $($m.Groups[1].Value)$($rm.to)" })
  }

  if ($txt -ne $orig) { Set-Content -LiteralPath $file -Value $txt -Encoding UTF8 }
}

Get-ChildItem -Path (Join-Path $mf "src") -Recurse -Filter "*.java" |
  ForEach-Object { Replace-InFile -file $_.FullName }

# 3) reposicionar arquivos de acordo com o package
function Move-ByPackage {
  param([string]$file)
  $content = Get-Content -Raw -LiteralPath $file
  if ($content -match '(?m)^\s*package\s+([a-z0-9_.]+)\s*;') {
    $pkg = $matches[1]
    $base = if ($file -like "*\src\main\java\*") { $mainRoot } else { $testRoot }
    $destDir = Join-Path $base ($pkg -replace '\.', '\\')
    New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    $dest = Join-Path $destDir (Split-Path $file -Leaf)
    if ((Resolve-Path $file).Path -ne (Resolve-Path $dest -ErrorAction SilentlyContinue).Path) {
      git mv $file $dest
    }
  }
}

Get-ChildItem -Path (Join-Path $mf "src") -Recurse -Filter "*.java" |
  ForEach-Object { Move-ByPackage -file $_.FullName }

git status
Write-Host "`nOK - modulo-infraestrutura padronizado (pacotes + diretorios)."
