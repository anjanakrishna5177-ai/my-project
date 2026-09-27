@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on me
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------

@if "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%
@if "%MAVEN_BATCH_PAUSE%" == "on" set MAVEN_BATCH_PAUSE=on

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible interference, switch local alias handling on
setlocal enabledelayedexpansion

@REM Find the project root directory
set "EXEC_DIR=%CD%"
set "WDIR=%EXEC_DIR%"

:findRoot
if exist "%WDIR%\.mvn" goto foundRoot
set "WDIR_PARENT=%WDIR%\.."
for %%I in ("%WDIR_PARENT%") do set "WDIR_PARENT_FULL=%%~fI"
if "%WDIR_PARENT_FULL%" == "%WDIR%" goto fallback
set "WDIR=%WDIR_PARENT_FULL%"
goto findRoot

:fallback
set "WDIR=%EXEC_DIR%"

:foundRoot
set "WRAPPER_JAR=%WDIR%\.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_PROPERTIES=%WDIR%\.mvn\wrapper\maven-wrapper.properties"

if exist "%WRAPPER_JAR%" goto run

@REM Download wrapper JAR if missing
echo Downloading Maven Wrapper JAR...
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar', '%WRAPPER_JAR%')"
if not exist "%WRAPPER_JAR%" (
    echo Error: Could not download maven-wrapper.jar
    exit /b 1
)

:run
set "JAVA_EXE=java.exe"
if not "%JAVA_HOME%" == "" (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%WDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
if ERRORLEVEL 1 set ERROR_CODE=1

goto end

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
if %ERROR_CODE% neq 0 exit /b %ERROR_CODE%
