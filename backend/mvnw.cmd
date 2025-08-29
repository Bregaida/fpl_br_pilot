@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

REM ----------------------------------------------------------------------------
REM Licensed to the Apache Software Foundation (ASF) under one
REM or more contributor license agreements.  See the NOTICE file
REM distributed with this work for additional information
REM regarding copyright ownership.  The ASF licenses this file
REM to you under the Apache License, Version 2.0 (the
REM "License"); you may not use this file except in compliance
REM with the License.  You may obtain a copy of the License at
REM
REM    https://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing,
REM software distributed under the License is distributed on an
REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM KIND, either express or implied.  See the License for the
REM specific language governing permissions and limitations
REM under the License.
REM ----------------------------------------------------------------------------

REM ----------------------------------------------------------------------------
REM Apache Maven Wrapper startup script (Windows)
REM ----------------------------------------------------------------------------

SET BASE_DIR=%~dp0
SET WRAPPER_DIR=%BASE_DIR%.mvn\wrapper
SET PROPS_FILE=%WRAPPER_DIR%\maven-wrapper.properties
SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

REM Find Java
SET JAVACMD=
IF NOT "%JAVA_HOME%"=="" (
  SET "JAVACMD=%JAVA_HOME%\bin\java.exe"
)
IF "%JAVACMD%"=="" (
  SET "JAVACMD=java"
)

WHERE /Q "%JAVACMD%"
IF ERRORLEVEL 1 (
  ECHO Error: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
  ECHO Please set the JAVA_HOME variable in your environment to match the location of your Java installation. 1>&2
  EXIT /B 1
)

REM Load properties
SET DISTRIBUTION_URL=
SET WRAPPER_URL=
IF EXIST "%PROPS_FILE%" (
  FOR /F "usebackq tokens=1,* delims==" %%A IN ("%PROPS_FILE%") DO (
    IF /I "%%A"=="distributionUrl" SET "DISTRIBUTION_URL=%%B"
    IF /I "%%A"=="wrapperUrl" SET "WRAPPER_URL=%%B"
  )
)

REM Ensure wrapper jar exists, download if missing
IF NOT EXIST "%WRAPPER_JAR%" (
  IF NOT EXIST "%WRAPPER_DIR%" (
    MKDIR "%WRAPPER_DIR%"
  )
  IF "%WRAPPER_URL%"=="" (
    ECHO wrapperUrl not set in %PROPS_FILE% 1>&2
    EXIT /B 1
  )
  ECHO Downloading Maven Wrapper Jar from: %WRAPPER_URL%
  CALL :download "%WRAPPER_URL%" "%WRAPPER_JAR%"
  IF ERRORLEVEL 1 (
    ECHO Failed to download %WRAPPER_URL% 1>&2
    EXIT /B 1
  )
)

REM Build the classpath
SET CLASSPATH=%WRAPPER_JAR%

REM Run Maven Wrapper
"%JAVACMD%" -classpath "%CLASSPATH%" -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" %MAVEN_OPTS% %WRAPPER_LAUNCHER% %*
EXIT /B %ERRORLEVEL%

:download
REM args: url, dest
SET "_URL=%~1"
SET "_DEST=%~2"

REM Try PowerShell
WHERE /Q powershell
IF NOT ERRORLEVEL 1 (
  POWERSHELL -NoLogo -NoProfile -ExecutionPolicy Bypass -Command "^$ErrorActionPreference='Stop';(New-Object System.Net.WebClient).DownloadFile('%_URL%','%_DEST%')"
  IF NOT ERRORLEVEL 1 EXIT /B 0
)

REM Try curl
WHERE /Q curl
IF NOT ERRORLEVEL 1 (
  curl -fsSL "%_URL%" -o "%_DEST%"
  IF NOT ERRORLEVEL 1 EXIT /B 0
)

REM Try wget
WHERE /Q wget
IF NOT ERRORLEVEL 1 (
  wget -q "%_URL%" -O "%_DEST%"
  IF NOT ERRORLEVEL 1 EXIT /B 0
)

EXIT /B 1
