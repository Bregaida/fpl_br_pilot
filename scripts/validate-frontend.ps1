param(
  [int]$DelaySeconds = 5,
  [string[]]$Urls = @(
    'http://localhost:5173/',
    'http://localhost:5173/flightplan/novo',
    'http://localhost:5173/flightplan/listar'
  )
)

Write-Host "Aguardando $DelaySeconds s..." -ForegroundColor Cyan
Start-Sleep -Seconds $DelaySeconds

foreach ($u in $Urls) {
  Write-Host $u -ForegroundColor Yellow
  curl.exe -s -o $null -w '%{http_code}`n' $u
}

Write-Host "Concluído." -ForegroundColor Green


