$ErrorActionPreference = 'Stop'

# Setup Java for this session (user-provided JDK 21 path)
$javaHome = 'C:\\Program Files\\Java\\jdk-21'
if (Test-Path $javaHome) {
  $env:JAVA_HOME = $javaHome
  if (-not ($env:Path -like "*$javaHome\\bin*")) {
    $env:Path = "$javaHome\bin;" + $env:Path
  }
}

# Invoke Maven Wrapper directly via Java (bypasses mvnw.cmd PATH quirks)
function Invoke-Mvn {
  param([Parameter(Mandatory=$true)][string[]]$Args)
  $java = Join-Path $env:JAVA_HOME 'bin/java.exe'
  if (-not (Test-Path $java)) { throw "JAVA not found at $java. Set JAVA_HOME correctly." }
  $cp = Join-Path (Get-Location) '.mvn/wrapper/maven-wrapper.jar'
  if (-not (Test-Path $cp)) { throw "Wrapper jar not found: $cp" }
  $jargs = @('-Dmaven.multiModuleProjectDirectory=.', '-cp', $cp, 'org.apache.maven.wrapper.MavenWrapperMain') + $Args
  & $java @jargs
  if ($LASTEXITCODE -ne 0) { throw "Maven failed with exit code $LASTEXITCODE" }
}

Write-Host "==> Verificando Java no PATH..."
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
  Write-Warning "Java não encontrado no PATH. Abra um terminal com JDK 21 no PATH. Prosseguindo assim mesmo..."
} else { java -version }

Write-Host "==> Atualizando branch (pull --rebase)..."
git status
git pull --rebase origin main

Write-Host "==> Colapsando pacotes e padronizando..."
powershell -ExecutionPolicy Bypass -File .\scripts\collapse-packages.ps1
# Opcional (só use se ainda ver sujeira na infra após o collapse):
# powershell -ExecutionPolicy Bypass -File .\scripts\padronizar-infra.ps1

Write-Host "==> Build (sem testes) para aquecer o cache..."
Invoke-Mvn -Args @('-q','-DskipTests','package')

Write-Host "==> Rodando testes de modulo-aplicacao..."
Invoke-Mvn -Args @('-q','-am','-pl','modulo-aplicacao','-DskipTests=false','test')

Write-Host "==> Rodando testes de modulo-infraestrutura..."
Invoke-Mvn -Args @('-q','-am','-pl','modulo-infraestrutura','-DskipTests=false','test')

Write-Host "==> Commit & push..."
git add -A
git commit -m "Padroniza pacotes/paths (colapso br.com.*), reposiciona fontes por package, normaliza imports e passa nos testes" | Out-Null
git push

Write-Host "==> (Opcional) Subir Quarkus em dev para validar endpoints..."
Write-Host 'Comando: set JAVA_HOME=C:\Program Files\Java\jdk-21 && set PATH=%JAVA_HOME%\bin;%PATH% && .\mvnw.cmd -f modulo-infraestrutura quarkus:dev'
Write-Host "Health:   GET http://localhost:8080/api/v1/saude/ping"
Write-Host "Swagger:  http://localhost:8080/q/swagger-ui"

# Dicas de teste rápido no PowerShell (quando o dev estiver rodando):
# Invoke-RestMethod http://localhost:8080/api/v1/saude/ping
# Invoke-RestMethod -Uri http://localhost:8080/api/v1/fpl/preview -Method Post `
#   -ContentType 'application/json' `
#   -Body (@{identificacaoAeronave='PTABC';regrasDeVoo='IFR';tipoDeVoo='GERAL'} | ConvertTo-Json)

Write-Host "==> Finalizado!"
