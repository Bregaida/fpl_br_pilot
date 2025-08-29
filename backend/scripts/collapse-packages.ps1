$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.UTF8Encoding]::UTF8

$modules = @("modulo-aplicacao","modulo-infraestrutura")

function Normalize-Content {
  param([string]$path)
  $txt = [IO.File]::ReadAllText($path, [Text.Encoding]::UTF8)
  $orig = $txt

  # 1) Normaliza duplicações br.com. em package/import
  # package br.com.br.com.foo -> package br.com.foo
  $txt = [Regex]::Replace($txt, '(?m)^(\s*package\s+)(?:br\.com\.){2,}', '${1}br.com.')
  $txt = [Regex]::Replace($txt, '(?m)^(\s*import\s+)(?:br\.com\.){2,}',  '${1}br.com.')

  # 2) Também corrige qualquer br.com.br.com em outros pontos
  $txt = [Regex]::Replace($txt, '(?<![A-Za-z0-9_])(br\.com\.)(?:br\.com\.)+', 'br.com.')

  # 3) Remove pontos duplos ocasionais em packages (ex.: br..com)
  $txt = $txt -replace '\.\.', '.'

  if ($txt -ne $orig) {
    [IO.File]::WriteAllText($path, $txt, [Text.Encoding]::UTF8)
  }
}

function Move-By-Package {
  param([string]$file, [string]$mainRoot, [string]$testRoot)
  $txt = [IO.File]::ReadAllText($file, [Text.Encoding]::UTF8)
  if ($txt -match '(?m)^\s*package\s+([a-z0-9_.]+)\s*;') {
    $pkg = $matches[1]
    $base = if ($file.StartsWith($mainRoot)) { $mainRoot } else { $testRoot }
    $destDir = Join-Path $base ($pkg -replace '\.', '\')
    if (-not (Test-Path $destDir)) { New-Item -ItemType Directory -Force -Path $destDir | Out-Null }
    $dest = Join-Path $destDir (Split-Path $file -Leaf)
    if (-not (Test-Path $dest)) {
      $p2 = Start-Process -FilePath git -ArgumentList @('mv','--',"$file","$dest") -NoNewWindow -Wait -PassThru -RedirectStandardError "$env:TEMP\gitmv.err" -RedirectStandardOutput "$env:TEMP\gitmv.out"
      if ($p2.ExitCode -ne 0) { Move-Item -Force -- "$file" "$dest" }
    }
  }
}

function Remove-Empty-Dirs {
  param([string]$root)
  # remove de baixo pra cima
  Get-ChildItem -Directory -Recurse $root |
    Sort-Object FullName -Descending |
    ForEach-Object {
      if (-not (Get-ChildItem -Force -LiteralPath $_.FullName)) {
        Remove-Item -Force -Recurse -LiteralPath $_.FullName
      }
    }
}

foreach ($m in $modules) {
  $modRoot = Join-Path (Get-Location) $m
  $mainRoot = Join-Path $modRoot 'src\main\java'
  $testRoot = Join-Path $modRoot 'src\test\java'

  if (-not (Test-Path $mainRoot)) { continue }
  if (-not (Test-Path $testRoot)) { New-Item -ItemType Directory -Force -Path $testRoot | Out-Null }

  # 0) Corrige árvores físicas óbvias: ...\br\com\br\com -> ...\br\com
  Get-ChildItem -Recurse -Directory $modRoot | Where-Object {
    $_.FullName -match '\\br\\com\\br\\com\\'
  } | ForEach-Object {
    $dup = $_.FullName
    $target = $dup -replace '\\br\\com\\br\\com\\', '\\br\\com\\'
    if (-not (Test-Path $target)) { New-Item -ItemType Directory -Force -Path $target | Out-Null }
    Get-ChildItem -Recurse -File $dup | ForEach-Object {
      $rel = $_.FullName.Substring($dup.Length).TrimStart('\')
      $dest = Join-Path $target $rel
      New-Item -ItemType Directory -Force -Path (Split-Path $dest -Parent) | Out-Null
      $p = Start-Process -FilePath git -ArgumentList @('mv','--',"$($_.FullName)","$dest") -NoNewWindow -Wait -PassThru -RedirectStandardError "$env:TEMP\gitmv.err" -RedirectStandardOutput "$env:TEMP\gitmv.out"
      if ($p.ExitCode -ne 0) { Move-Item -Force -- "$($_.FullName)" "$dest" }
    }
  }

  # 1) Normaliza conteúdo e packages/imports
  Get-ChildItem -Path $modRoot -Recurse -Filter '*.java' | ForEach-Object {
    Normalize-Content -path $_.FullName
  }

  # 2) Reposiciona arquivos segundo o package
  Get-ChildItem -Path $modRoot -Recurse -Filter '*.java' | ForEach-Object {
    Move-By-Package -file $_.FullName -mainRoot $mainRoot -testRoot $testRoot
  }

  # 3) Limpa diretórios vazios
  Remove-Empty-Dirs -root (Join-Path $modRoot 'src')
}

git status
Write-Host "OK - Estrutura normalizada. Pronto para build."
