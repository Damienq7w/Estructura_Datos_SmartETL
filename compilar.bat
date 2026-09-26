@echo off
REM Compila y ejecuta SmartETL-DS en Windows.
REM Se puede ejecutar con doble clic: siempre trabaja en la raiz del proyecto.

chcp 65001 >nul
cd /d "%~dp0"
if not exist bin mkdir bin

echo Compilando...
javac -encoding UTF-8 -d bin -sourcepath src src\Main.java
if errorlevel 1 (
    echo.
    echo Error de compilacion. Revise los mensajes de arriba.
    pause
    exit /b 1
)

echo Ejecutando (memoria maxima: 1 GB)...
java -Xmx1g -Dfile.encoding=UTF-8 -cp bin Main
pause