# SmartETL_DS

# SmartETL-DS + IA — Estructura de Datos (Java)

# Grupo - 04

Proyecto final integrador de Estructura de Datos (UTA) — **Entrega del primer parcial (1 de octubre)**.
Sistema ETL que lee más de **1,18 millones de registros reales** de casos de COVID-19 por condado de
Estados Unidos y los carga en estructuras de datos implementadas manualmente (sin colecciones de
`java.util`).

## Problema y dominio

**Dominio:** epidemiología territorial (caso *Epidemiología* de la sección 23 del enunciado).

**Problema:** durante 2021, los departamentos de salud de Estados Unidos reportaron cada día los
casos y muertes por COVID-19 de cada condado. Esos reportes llegan como un archivo de más de un
millón de filas con datos incompletos, códigos faltantes y correcciones que contradicen los días
anteriores. El sistema debe leer ese volumen, organizarlo en estructuras de datos propias y
permitir consultarlo de forma eficiente, sin depender de las colecciones de Java ni de un motor de
base de datos.

## ¿Qué hace el programa en esta entrega?

1. **Verifica** que los tres archivos de la base de datos estén en la carpeta `data/`. Si falta
   alguno, indica cuál y qué script ejecutar para obtenerlo.
2. **Valida** que cada archivo tenga exactamente las columnas esperadas. Si no coinciden, lo rechaza
   en lugar de leer datos en la columna equivocada.
3. **Extrae** los 1.185.373 registros de `casos_diarios.csv` y los carga en una **lista enlazada
   propia**. También lee `condados.csv` y `vecindad.tsv` y los carga en sus propias listas, sin
   procesarlos todavía.
4. **Muestra un resumen de la carga** al terminar la extracción: cuántas filas se leyeron, cuántas
   se cargaron y cuántas se descartaron por formato en cada archivo, y cuántos **posibles errores**
   trae la base (ver la tabla de la sección *Base de datos*). En esta entrega los errores solo se
   **detectan y cuentan**; separarlos, corregirlos y registrarlos con su motivo corresponde al
   Transform (Hito 2).
5. **Permite consultar** los datos: mostrar registros, buscar un condado por código FIPS o por
   nombre, ver estadísticas de la base e incidencias de lectura.
6. **Mide** cuántas comparaciones cuesta cada búsqueda, y compara la lista enlazada contra una
   lista secuencial.

## Estructura del repositorio

```
SmartETL_DS/
├── .vscode/
│   ├── settings.json                    → indica a VS Code que el código fuente empieza en src/
│   └── launch.json                      → ejecuta Main con -Xmx1g (memoria suficiente para la base)
├── Capturas_Ejecucion/
│   ├── Ejecucion_01.png                 → capturas de pantalla de la ejecución
│   └── Ejecucion_02.png
├── Diagrama de clases/
│   ├── Diagrama de Clases.png           → diagrama de clases del primer parcial
│   └── Arquitectura.png                 → arquitectura propuesta del proyecto completo
├── Documento/
│   └── Documentacion_SmartETL.pdf       → informe: problema, arquitectura, base de datos y pruebas
├── data/
│   ├── casos_diarios.csv                → NO está en el repo: se descarga con el script (48 MB)
│   ├── condados.csv                     → catálogo de condados por código FIPS (original, sin limpiar)
│   └── vecindad.tsv                     → qué condado colinda con cuál (original, sin limpiar)
├── tools/
│   └── preparar_base.py                 → descarga los 3 archivos de data/ tal como los publica su fuente
├── src/
│   ├── Main.java                        → verificación de archivos y menú de consola
│   ├── model/
│   │   ├── Esquema.java                 → columnas fijas de la base y validación de cabecera
│   │   ├── CasoDiario.java              → una fila de casos_diarios.csv
│   │   ├── Condado.java                 → una fila de condados.csv
│   │   └── Vecindad.java                → una fila de vecindad.tsv
│   ├── estructuras/
│   │   ├── Nodo.java                    → nodo con dato y referencia al siguiente
│   │   ├── ListaEnlazada.java           → estructura principal de esta entrega
│   │   └── ListaSecuencial.java         → para comparar contra la lista enlazada
│   ├── etl/
│   │   └── Extract.java                 → lectura de los 3 archivos de la base
│   └── algoritmos/
│       └── Busqueda.java                → búsqueda secuencial con conteo de comparaciones
├── test/
│   └── PruebaListaEnlazada.java         → pruebas de la lista enlazada
├── compilar.bat                         → compila y ejecuta en Windows
├── compilar.sh                          → compila y ejecuta en Linux / Mac
├── .gitignore
└── README.md
```

Son **11 clases**, solo las que usa esta entrega. La arquitectura propuesta del proyecto completo,
que también pide el enunciado para el 1 de octubre, se presenta como diagrama en
`Diagrama de clases/Arquitectura.png`.

## Base de datos

La base es **fija**: el sistema trabaja contra estas columnas exactas y rechaza cualquier archivo
con otro formato.

| Archivo | Formato | Columnas originales | Filas | En el repo | Fuente | Descarga directa |
| --- | --- | --- | --- | --- | --- | --- |
| `casos_diarios.csv` | comas | `date,county,state,fips,cases,deaths` | 1.185.373 | ❌ | [The New York Times — covid-19-data](https://github.com/nytimes/covid-19-data) | [us-counties-2021.csv](https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-counties-2021.csv) |
| `condados.csv` | comas | `fips,name,state` | 3.195 | ✅ | [kjhealy/fips-codes](https://github.com/kjhealy/fips-codes) | [state_and_county_fips_master.csv](https://raw.githubusercontent.com/kjhealy/fips-codes/master/state_and_county_fips_master.csv) |
| `vecindad.tsv` | tabulaciones, con comillas | `county_name`, `county_id`, `adjacent_county_name`, `adjacent_county_id` | 22.200 | ✅ | [turibe/us-county-adjacency](https://github.com/turibe/us-county-adjacency) | [county_adjacency.tsv](https://raw.githubusercontent.com/turibe/us-county-adjacency/main/county_adjacency.tsv) |

Los tres archivos se guardan **exactamente como los publica su fuente**, sin limpiar ni corregir
nada. Los problemas que traen son reales y forman parte del trabajo del proyecto: en esta entrega
el programa los **detecta y cuenta**; desde el Hito 2, el Transform los separará y corregirá.

### Posibles errores que trae la base

| Archivo | Problema | Cantidad | Qué pide el enunciado |
| --- | --- | --- | --- |
| `casos_diarios.csv` | `deaths` vacío | 28.470 | Campos incompletos |
| `casos_diarios.csv` | Sin código FIPS | 10.803 | Validar identificadores |
| `casos_diarios.csv` | `county = Unknown` | 9.708 | Validar identificadores |
| `condados.csv` | Fila del país mezclada con los condados | 1 | Validar identificadores |
| `condados.csv` | Filas de estados mezcladas con los condados | 51 | Validar identificadores |
| `condados.csv` | FIPS de 4 dígitos, sin el cero (`1001` en vez de `01001`) | 316 | Normalizar |
| `vecindad.tsv` | Condado vecino de sí mismo | 3.234 | Relaciones inconsistentes |
| `vecindad.tsv` | Vecindad repetida en los dos sentidos | 9.483 | Detectar duplicados |

En `vecindad.tsv` las cuentas cuadran exactas: 3.234 bucles + 9.483 vecindades + 9.483 repetidas =
22.200 filas. Además, los nombres no coinciden entre archivos (`Autauga County` y `AL` en
`condados.csv`; `Autauga` y `Alabama` en `casos_diarios.csv`), por eso los archivos se relacionan
por el código FIPS.

**Importante sobre los datos:**

- `cases` y `deaths` son **acumulados**, no casos nuevos del día.
- El código `fips` se maneja siempre como **texto**: `01001` como número se vuelve `1001` y deja de
  coincidir con los otros archivos.
- En `vecindad.tsv` los nombres llevan una coma adentro (`"Autauga County, AL"`): se separa por
  **tabulación** y se respetan las comillas.
- **No abrir `casos_diarios.csv` con Excel**: Excel admite 1.048.576 filas y el archivo tiene
  1.185.373. Corta las últimas filas sin avisar.

## Preparar la base (hacer esto primero)

`casos_diarios.csv` no se sube al repositorio porque pesa 48 MB. Antes de ejecutar el programa por
primera vez, desde la carpeta raíz del proyecto:

```bash
python tools/preparar_base.py      # Windows
python3 tools/preparar_base.py     # Linux / Mac
```

El script se puede ejecutar desde cualquier carpeta o desde VS Code: **siempre guarda los archivos
en `data/` de la raíz del repositorio**, sin modificar su contenido. Si se ejecuta de nuevo, reemplaza los archivos por otros
idénticos (no duplica datos). Si se corta internet a mitad de la descarga, los archivos existentes
no se modifican. Al terminar muestra:

```
    OK  casos_diarios.csv     1,185,373 filas
    OK  condados.csv              3,195 filas
    OK  vecindad.tsv             22,200 filas
```

Sin Python, se puede descargar `casos_diarios.csv` desde el enlace directo de la tabla anterior y
guardarlo manualmente en `data/` con ese nombre.

## Compilar y ejecutar

Desde la carpeta raíz del proyecto:

```bash
javac -encoding UTF-8 -d bin -sourcepath src src/Main.java
java -Xmx1g -cp bin Main
```

O con doble clic en `compilar.bat` (Windows) / `./compilar.sh` (Linux / Mac), que ya incluyen
`-Xmx1g`.

**Memoria:** con la base completa la lista enlazada ocupa ≈304 MB. Sin `-Xmx1g`, en equipos con
poca RAM el programa puede terminar con `OutOfMemoryError`.

### Desde VS Code

Abrir la carpeta `SmartETL_DS` completa. El archivo `.vscode/settings.json` le indica a VS Code que
el código fuente empieza en `src/`, y `.vscode/launch.json` ejecuta `Main` con `-Xmx1g`. Si al
ejecutar aparece `ClassNotFoundException`, usar `Ctrl+Shift+P` → **Java: Force Java Compilation**
→ **Full** y volver a ejecutar.

## Reparto del equipo (6 integrantes)

| Integrante | Rol | Clases a cargo | Rama |
| --- | --- | --- | --- |
| Cunalata Mendoza Damian Alexander | Líder técnico e integración | Este README, `tools/preparar_base.py`, carpeta `data/` (los 3 archivos de la base), `Busqueda` (búsqueda secuencial con conteo de comparaciones), `compilar.bat` / `compilar.sh`; integración de todas las ramas en `main` y prueba final con la base completa | `Damian_Cunalata` |
| Chalco Tasna Kenneth Mateo | Desarrollo | `Nodo`, `ListaEnlazada` (insertar al final, insertar al inicio, obtener, eliminar, mostrar e iterador propio) y `ListaSecuencial` | `Mateo-Chalco` |
| Tisalema Guashco Darwin Joel | Desarrollo | `Extract` (lectura de los 3 archivos: CSV y TSV con comillas, validación de cabecera, conteo de incidencias y de los posibles errores de la base) | `Rama-Joel` |
| Silva Camuendo Luis Alexander | Desarrollo | Paquete `model/`: `Esquema`, `CasoDiario`, `Condado` y `Vecindad` | `Rama-Luis-Silva` |
| Tacuri Santillan Mónica Sara | Desarrollo | `Main` (verificación de archivos, menú de consola y resumen de la carga) y `PruebaListaEnlazada` | `Sara-Tacuri` |
| Camacho Monta Josue Jampier | Documentación | `Documentacion_SmartETL`, diagrama de clases, arquitectura propuesta y capturas de ejecución | `rama---Josue` |

Cada integrante desarrolla sus clases en su propia rama y las integra a `main` mediante Pull Request
revisado por otro compañero; el líder integra todo en `main` y valida la ejecución con la base
completa antes de la entrega.

## Breve explicación del uso de cada estructura de datos

| Estructura | Dónde | Por qué |
| --- | --- | --- |
| **Lista simplemente enlazada con cabeza y cola** (`Nodo` con `siguiente`) | `ListaEnlazada` | No se sabe de antemano cuántos registros llegan; insertar al final es O(1) sin redimensionar nada. |
| **Lista secuencial** (arreglo + `tamanio`) | `ListaSecuencial` | Solo para comparar: acceso por posición O(1), pero debe copiar todo el arreglo cada vez que se llena. |

### Lista simplemente enlazada (`ListaEnlazada`)

Cada registro vive en un `Nodo` con una referencia `siguiente`, y la lista mantiene `cabeza`, `cola`
y `tamanio`. `insertar` engancha el nuevo nodo después de `cola` y lo convierte en la nueva `cola`
en O(1): sin esa referencia habría que recorrer toda la lista en cada inserción, y cargar 1,18
millones de registros pasaría de segundos a horas. `obtener` recorre desde `cabeza` hasta la
posición pedida (O(n)); por eso la lista se recorre siempre con su **iterador propio** y nunca con
`obtener(i)` dentro de un bucle, que costaría unos 700 mil millones de pasos. `mostrar(n)` imprime
solo los primeros `n` registros para no congelar la consola. Se eligió esta estructura porque la
cantidad de registros no se conoce antes de leer el archivo y porque la carga solo necesita
agregar al final y recorrer de principio a fin.

### Lista secuencial (`ListaSecuencial`)

Guarda los registros en un arreglo de capacidad inicial fija, con un contador `tamanio`. `insertar`
agrega al final en O(1) mientras hay espacio; cuando el arreglo se llena, crea uno del doble de
tamaño y copia todos los elementos. `obtener` accede directamente a la posición en O(1). Se
implementó para medir en la misma ejecución la diferencia con la lista enlazada (opción 8 del
menú): la secuencial gana en acceso por posición, la enlazada en inserción sin copias.

## Menú de consola

```
 1. Cargar casos diarios (Extract)
 2. Mostrar registros
 3. Buscar por código FIPS y fecha
 4. Buscar por nombre de condado
 5. Ver incidencias de lectura
 6. Estadísticas de la base
 7. Ver condados y vecindad
 8. Comparar ListaEnlazada vs ListaSecuencial
 9. Ver esquema fijo de la base
 0. Salir
```

Los casos se cargan **una sola vez** (opción 1) y todas las demás opciones reutilizan la misma lista.

## Caso de prueba — valores esperados

Con la base completa, el programa debe mostrar aproximadamente:

| Medida | Valor esperado |
| --- | --- |
| Filas leídas | 1.185.373 |
| Registros cargados en la lista | 1.185.373 |
| Filas descartadas por formato | 0 |
| Condados distintos | 3.262 |
| Fechas | 365 (2021-01-01 → 2021-12-31) |
| Registros sin FIPS | 10.803 |
| `county = Unknown` | 9.708 |
| `deaths` vacío | 28.470 |
| Filas de `condados.csv` / que no son condados | 3.195 / 52 |
| Filas de `vecindad.tsv` / bucles / repetidas | 22.200 / 3.234 / 9.483 |
| Tiempo de carga | ≈1,5 s |
| Memoria de la lista | ≈304 MB |
| Búsqueda de FIPS `48201` en `2021-12-31` | 1.184.850 comparaciones |

Los posibles errores son los que muestra el **resumen de la carga**. Estos errores **vienen en los datos públicos originales**: no fueron agregados por el equipo. Su
detalle está en `Documento/Documentacion_SmartETL.pdf`.

## Restricciones respetadas

Sin `ArrayList`, `LinkedList`, `Stack`, `Queue`, `Deque`, `HashMap` ni colecciones de `java.util`
para guardar datos (de ese paquete solo se usa `Scanner` para leer el teclado). La lectura de
archivos usa `BufferedReader`, y la separación de columnas respeta las comillas. Listas implementadas manualmente con arreglos y referencias entre
objetos. Clases separadas en archivos `.java` y organizadas en paquetes por responsabilidad, con
`Main` conteniendo `main`.

## Y después se podrá realizar esto

Sobre esta base, en las siguientes entregas el sistema incorporará:

- **Cola y Pila** para el ETL completo: los registros entrarán en orden de llegada y los inválidos
  se apilarán con su motivo.
- **Árbol BST** para buscar un condado en ≈21 comparaciones en lugar de más de un millón.
- **Grafo de vecindad con BFS y DFS** para responder a cuántos condados de distancia está uno de otro.
- **IA (Gemini)** para generar automáticamente reportes y flujogramas a partir de lo que el programa
  ya calculó.
