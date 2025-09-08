Monorepo inicial para o projeto **FPL-BR-PILOT** (Quarkus + React + Android). 
Este repositÃ³rio serÃ¡ usado para gerar/validar o Plano de Voo (MCA 100-11/2020),
consolidar meteorologia (REDEMET) e consultar NOTAM/cartas (AISWEB).

RepositÃ³rio: https://github.com/bregaida/fpl_br_pilot 

## Requisitos de Java (JDK 17)

- Configure o JAVA_HOME para um JDK 17.
- No Windows PowerShell (sessÃ£o atual):

```powershell
$env:JAVA_HOME="C:\\Program Files\\Eclipse Adoptium\\jdk-17"
$env:Path = "$env:JAVA_HOME\\bin;" + $env:Path
java -version
mvn -version
```

- Persistente (PowerShell profile): use $PROFILE para adicionar as linhas acima, se desejar.

## IDEs

- IntelliJ IDEA: Settings â†’ Build, Execution, Deployment â†’ Build Tools â†’ Maven â†’ JDK = 17.
- IntelliJ IDEA: Settings â†’ Editor â†’ Code Style â†’ Line Separator = LF (\n).
- VS Code: `.vscode/settings.json` jÃ¡ define `"files.eol": "\n"`.

## Limpar caches de IDE

- IntelliJ: File â†’ Invalidate Caches / Restart.
- VS Code: Recarregar janela e limpar `node_modules` do frontend, se necessÃ¡rio.

## Build Backend

```bash
cd backend
./mvnw clean compile
./mvnw clean test
./mvnw clean verify # gera cobertura JaCoCo
```