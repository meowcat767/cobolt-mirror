@echo off
REM Cobolt VCS executable wrapper for Windows

set SCRIPT_DIR=%~dp0
set JAR_PATH=%SCRIPT_DIR%target\cobolt.jar

if not exist "%JAR_PATH%" (
    echo Error: cobolt.jar not found at %JAR_PATH%
    echo Please run 'mvn package' to build the project first
    exit /b 1
)

java -jar "%JAR_PATH%" %*
