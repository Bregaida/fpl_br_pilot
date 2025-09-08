param(
  [string[]]$Roots = @("backend/src/main/java","backend/src/test/java")
)

$ErrorActionPreference = 'Stop'
$skipped   = New-Object System.Collections.Generic.List[string]
$modified  = New-Object System.Collections.Generic.List[string]
$total     = 0

# Coleta de arquivos .java, ignorando dirs pesados
$files = foreach ($root in $Roots) {
  if (Test-Path $root) {
    Get-ChildItem -Path $root -Recurse -File -Filter *.java |
      Where-Object { $_.FullName -notmatch '\\(target|node_modules|\.git)\\' }
  }
}
$files = $files | Sort-Object FullName -Unique
$total = $files.Count

if ($total -eq 0) {
  Write-Host "Nenhum .java encontrado nos roots informados." -ForegroundColor Yellow
  exit 0
}

$idx = 0
foreach ($f in $files) {
  $idx++
  Write-Progress -Activity "Removendo UTF-8 BOM" -Status "$idx / ${total}: $($f.FullName)" -PercentComplete (($idx/$total)*100)

  # Tenta abrir com exclusividade por até ~2s (10 tentativas x 200ms)
  $fs = $null
  $opened = $false
  for ($i=0; $i -lt 10 -and -not $opened; $i++) {
    try {
      $fs = [System.IO.File]::Open($f.FullName, [System.IO.FileMode]::Open, [System.IO.FileAccess]::ReadWrite, [System.IO.FileShare]::None)
      $opened = $true
    } catch {
      Start-Sleep -Milliseconds 200
    }
  }
  if (-not $opened) {
    $skipped.Add($f.FullName) | Out-Null
    continue
  }

  try {
    if ($fs.Length -ge 3) {
      $buf = New-Object byte[] 3
      $null = $fs.Read($buf,0,3)
      $hasBom = ($buf[0] -eq 0xEF -and $buf[1] -eq 0xBB -and $buf[2] -eq 0xBF)
      if ($hasBom) {
        $restLen = [int]($fs.Length - 3)
        $rest = New-Object byte[] $restLen
        $null = $fs.Read($rest,0,$restLen)
        $fs.SetLength(0)
        $fs.Position = 0
        $fs.Write($rest,0,$restLen)
        $modified.Add($f.FullName) | Out-Null
      }
    }
  } finally {
    $fs.Dispose()
  }

  if (($idx % 200) -eq 0) {
    [System.GC]::Collect()
  }
}

Write-Host "✅ Concluído. Arquivos com BOM removido: $($modified.Count)" -ForegroundColor Green
if ($skipped.Count -gt 0) {
  Write-Host "⚠️  Arquivos pulados (bloqueados): $($skipped.Count) — listados em skipped-bom.txt" -ForegroundColor Yellow
  $skipped | Out-File -Encoding utf8 skipped-bom.txt
}
"Removidos:" | Out-File -Encoding utf8 modified-bom.txt
$modified | Out-File -Append -Encoding utf8 modified-bom.txt
