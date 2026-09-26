#!/usr/bin/env bash
# Compila y ejecuta SmartETL-DS en Linux / Mac.
# Se puede ejecutar desde cualquier carpeta: siempre trabaja en la raiz del proyecto.

cd "$(dirname "$0")" || exit 1
mkdir -p bin

echo "Compilando..."
if ! javac -encoding UTF-8 -d bin -sourcepath src src/Main.java; then
    echo "Error de compilacion. Revise los mensajes de arriba."
    exit 1
fi

echo "Ejecutando (memoria maxima: 1 GB)..."
java -Xmx1g -Dfile.encoding=UTF-8 -cp bin Main