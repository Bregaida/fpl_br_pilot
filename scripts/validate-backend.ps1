param(
  [string[]]$Urls = @(
    'http://localhost:8080/q/health',
    'http://localhost:8080/q/openapi',
    'http://localhost:8080/q/swagger-ui',
    'http://localhost:8080/api/v1/health',
    'http://localhost:8080/api/v1/aerodromos',
    'http://localhost:8080/api/v1/aerodromos/SBSP',
    'http://localhost:8080/api/v1/aerodromos/contar?busca=SB',
    'http://localhost:8080/api/v1/aerodromos/buscar?q=SBSP',
    'http://localhost:8080/api/v1/aerodromos/detalhes/SBSP',
    'http://localhost:8080/api/v1/aerodromos/existe/SBSP',
    'http://localhost:8080/api/v1/aerodromos/SBSP/terminal',
    'http://localhost:8080/api/v1/aerodromos/SBSP/frequencias',
    'http://localhost:8080/api/v1/flightplans',
    'http://localhost:8080/api/v1/flightplans/1',
    'http://localhost:8080/api/v1/flightplans/pilot/JOHN',
    'http://localhost:8080/api/v1/flightplans/aircraft/PT-ABC'
  )
)

foreach ($u in $Urls) {
  Write-Host $u -ForegroundColor Yellow
  curl.exe -s -o $null -w '%{http_code}`n' $u
}

Write-Host "Concluído." -ForegroundColor Green


