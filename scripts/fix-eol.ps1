$root = Get-Location
$patterns = @("*.java","*.ts","*.tsx","*.js","*.jsx","*.json","*.yml","*.yaml","*.md","*.xml","*.css","*.html")

foreach ($pat in $patterns) {
  Get-ChildItem -Path $root -Recurse -Include $pat -File | ForEach-Object {
    $content = Get-Content -LiteralPath $_.FullName -Raw
    $content = $content -replace "`r`n", "`n"
    Set-Content -LiteralPath $_.FullName -Value $content -NoNewline -Encoding utf8
  }
}
Write-Host "✅ Todos os arquivos normalizados para LF."
