# -*- coding: utf-8 -*-
"""
SmartETL-DS + IA  -  Preparacion de la base de datos
=====================================================

Descarga las fuentes publicas y deja los 3 archivos de entrada en la carpeta
data/ del proyecto, EXACTAMENTE como los publica su fuente (sin limpiar nada):

    data/casos_diarios.csv   casos y muertes por condado, 2021 (New York Times)
    data/condados.csv        catalogo de condados por codigo FIPS
    data/vecindad.tsv        que condado colinda con cual (separado por tabulaciones)

Los errores que traen (filas que no son condados, FIPS sin ceros, bucles,
vecindades repetidas, campos vacios) se dejan a proposito: los detecta y
corrige el programa Java en su fase Transform.

Se puede ejecutar desde CUALQUIER carpeta (raiz del repo, tools/, VS Code,
doble clic...). Los archivos siempre se guardan en la carpeta data/ que esta
al lado de tools/, es decir, en la raiz del repositorio.

Uso:
    python tools/preparar_base.py        (Windows)
    python3 tools/preparar_base.py       (Linux / Mac)

Requisitos: Python 3 e internet. No necesita librerias adicionales.
"""
import os
import sys
import urllib.request

# ------------------------------------------------------------------ rutas
# La carpeta data/ se calcula a partir de DONDE ESTA ESTE ARCHIVO,
# no desde la carpeta en la que se ejecuta el comando.
CARPETA_TOOLS = os.path.dirname(os.path.abspath(__file__))
RAIZ_REPO = os.path.dirname(CARPETA_TOOLS)
CARPETA_DATA = os.path.join(RAIZ_REPO, "data")

FUENTES = {
    "casos":    "https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-counties-2021.csv",
    "fips":     "https://raw.githubusercontent.com/kjhealy/fips-codes/master/state_and_county_fips_master.csv",
    "vecindad": "https://raw.githubusercontent.com/turibe/us-county-adjacency/main/county_adjacency.tsv",
}


def bajar(url):
    nombre = url.rsplit("/", 1)[-1]
    print("  Descargando %s ..." % nombre)
    with urllib.request.urlopen(url, timeout=120) as respuesta:
        return respuesta.read().decode("utf-8")


def guardar(nombre_archivo, contenido):
    """Escribe el archivo completo. Si ya existe, lo reemplaza (no agrega filas)."""
    ruta = os.path.join(CARPETA_DATA, nombre_archivo)
    with open(ruta, "w", encoding="utf-8", newline="") as f:
        f.write(contenido)
    return ruta


ARCHIVOS = [
    # (clave de la fuente, nombre en data/)
    ("casos",    "casos_diarios.csv"),
    ("fips",     "condados.csv"),
    ("vecindad", "vecindad.tsv"),
]


def preparar(clave, nombre):
    """Descarga el archivo y lo guarda TAL CUAL, sin limpiar ni corregir nada."""
    texto = bajar(FUENTES[clave])
    guardar(nombre, texto)
    filas = texto.count("\n") - 1          # sin contar la cabecera
    if not texto.endswith("\n"):
        filas += 1
    print("    OK  %-20s %10s filas" % (nombre, format(filas, ",")))


def main():
    print("=" * 60)
    print(" SmartETL-DS + IA  -  Preparacion de la base de datos")
    print("=" * 60)
    print(" Carpeta destino: %s" % CARPETA_DATA)
    print()
    os.makedirs(CARPETA_DATA, exist_ok=True)

    try:
        for clave, nombre in ARCHIVOS:
            preparar(clave, nombre)
    except Exception as e:
        print()
        print(" ERROR: no se pudo completar la descarga.")
        print(" Motivo: %s" % e)
        print(" Revise su conexion a internet y vuelva a ejecutar el script.")
        print(" Los archivos que ya existian en data/ NO fueron modificados")
        print(" si el error ocurrio durante su descarga.")
        sys.exit(1)

    print()
    print(" Listo. Los 3 archivos estan en:")
    print("   %s" % CARPETA_DATA)
    print(" Ya puede ejecutar el programa Java.")


if __name__ == "__main__":
    main()