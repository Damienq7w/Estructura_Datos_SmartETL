# SmartETL — El proyecto completo

Este es el único documento general. Explica qué hacemos, cómo y con qué reglas. Lo que le toca a cada persona está en `docs/equipo/`, en el archivo con su nombre.

Repositorio: https://github.com/Damienq7w/Estructura_Datos_SmartETL

## 1. Qué vamos a lograr

Un programa en Java que recibe datos reales y sucios de COVID-19 por condado de Estados Unidos (año 2021) y hace cuatro cosas:

1. **Carga** los datos en nuestras propias listas.
2. **Arregla** lo que tiene arreglo y **rechaza con motivo** lo que no.
3. **Permite buscar fácil**: un árbol para encontrar un condado por su código, y un grafo para ver vecinos y rutas entre condados.
4. **Genera un reporte con IA**: el programa calcula un resumen de los datos limpios y Gemini redacta el reporte y dibuja los flujogramas.

## 2. Cómo funciona

```
data/ (datos sucios)
   │  Extract: lee cada fila y la mete en la Cola
   ▼
Cola de registros crudos
   │  Transform: saca de la cola y decide
   ├──► Reparado o válido ──► Lista de válidos
   └──► Sin arreglo       ──► Pila de errores (con el motivo)
   ▼
Load: escribe los limpios y los rechazados en output/
   ▼
Árbol BST (un nodo por condado)  y  Grafo (condados vecinos)
   ▼
Resumen calculado por el programa
   ├──► output/reporte_etl.txt   (siempre, sin internet)
   └──► Gemini ──► reporte y flujogramas en output/
```

## 3. Los datos y lo que encontramos en ellos

| Archivo | Filas | Qué tiene |
| --- | --- | --- |
| `data/casos_diarios.csv` | 1.185.373 | fecha, condado, estado, FIPS, casos y muertes acumulados |
| `data/condados.csv` | 3.195 | catálogo: FIPS, nombre, estado |
| `data/vecindad.tsv` | 22.200 | qué condado colinda con cuál |

El **FIPS** es el código del condado: 5 dígitos, 2 del estado y 3 del condado (`01001` es Autauga, Alabama). **Siempre es `String`, nunca `int`**: como número perdería el cero inicial.

Lo que revisamos en los datos reales y cómo lo tratamos:

| Problema encontrado | Cantidad | Qué hace el programa |
| --- | --- | --- |
| Casos sin FIPS con condado `Unknown` | 9.708 | Rechazado |
| Casos sin FIPS de New York City, Kansas City y Joplin | 1.095 | Rechazado |
| Casos con muertes vacías (todos de Puerto Rico) | 28.470 | Válido, marcado "sin dato" |
| Condados con FIPS de 4 dígitos (perdieron el cero) | 316 | Reparado: se completa a 5 dígitos |
| Filas del catálogo que son país o estado, no condado | 52 | Rechazado |
| Vecindad: un condado vecino de sí mismo | 3.234 | Rechazado |
| Vecindad: el mismo par repetido al revés | 9.483 | Rechazado (se guarda una sola vez) |

Otros hechos que cambian el diseño:

- **No hay registros duplicados** en los casos reales. La detección de duplicados se prueba con el dataset pequeño.
- **El catálogo viene ordenado por FIPS.** Si se inserta así en el árbol, queda como una lista. Por eso el árbol se construye "por medios" (ver el archivo de Damian).
- **El grafo no es conexo:** tiene 17 grupos separados (Hawái, Alaska y los territorios no tocan al resto).
- **No se puede ordenar el archivo completo** con un algoritmo simple: se ordenan los registros de un solo día (unos 3.250).

Resultado esperado con la base real: **1.174.570 casos válidos y 10.803 rechazados**. Siempre debe cumplirse: leídos = válidos + rechazados.

## 4. Quién hace qué

| Integrante | Rama | Se encarga de | Su archivo |
| --- | --- | --- | --- |
| Joel | `Rama-Joel` | Cola, lectura de datos, BFS | `docs/equipo/Joel.md` |
| Kenneth | `Kenneth_Chalco` | Pila, listas, ordenamiento, DFS | `docs/equipo/Kenneth.md` |
| Luis | `Rama-Luis-Silva` | Transformación de datos, grafo | `docs/equipo/Luis.md` |
| Sara | `Sara-Tacuri` | Carga, menús, datos de prueba | `docs/equipo/Sara.md` |
| Damian | `Damian_Cunalata` | Árbol BST, conexión con Gemini, integración | `docs/equipo/Damian.md` |
| Josue | `rama--Josue` | Bitácora, flujogramas, prompts, informe | `docs/equipo/Josue.md` |

Dónde va cada cosa:

```
src/
├── Main.java        Sara     menú principal
├── menu/            Sara     un archivo por bloque del menú
├── model/           Joel     RegistroCrudo
│                    Luis     ErrorETL, TipoError
│                    Sara     ResumenETL
│                    Damian   IndiceCondado
├── estructuras/     Joel     Cola
│                    Kenneth  Pila, ajustes a las listas
│                    Damian   NodoArbol, ArbolBST
│                    Luis     Vertice, Grafo
├── etl/             Joel     Extract
│                    Luis     Transform
│                    Sara     Load
├── algoritmos/      Kenneth  Ordenamiento, DFS
│                    Joel     BFS
│                    Damian   Busqueda
└── ia/              Josue    Bitacora, GeneradorFlujograma, GeneradorPrompt
                     Damian   AnalizadorIA
test/                Todos    una clase de prueba por cada clase propia
data/pruebas/        Sara     datos pequeños con errores a propósito
output/              —        lo genera el programa
```

## 5. En qué orden se arma

| Etapa | Qué queda funcionando | Necesita antes |
| --- | --- | --- |
| A. Bases | `Cola`, `Pila`, `ErrorETL`, `ArbolBST`, `Bitacora`, datos de prueba, menú separado | — |
| B. ETL completo | Leer, transformar, cargar, `reporte_etl.txt` y reporte con Gemini | Etapa A |
| C. Procesamiento | Duplicados, ordenamiento, búsqueda comparada | Árbol en `main` |
| D. Grafo | `Grafo`, `BFS`, `DFS`, rutas, ciclos | `Cola` y `Pila` |
| E. Resto de la IA | Los 7 flujogramas, explicación del árbol y del grafo, evidencias | Bitácora funcionando |
| F. Cierre | Pruebas, README, informe, capturas | Todo lo anterior |

**Este fin de semana:** etapas A (sábado) y B (domingo).

Fechas que pone el docente: control de grafos el 25 de noviembre, control de IA el 30, sin funciones nuevas del 1 al 3 de diciembre, defensa el 4.

## 6. Reglas para todos

- **Cada uno sube su propio código desde su cuenta.** Nadie sube por otro: el docente revisa GitHub.
- **Un paso = un commit pequeño** que compila y trae su prueba.
- **Nada de colecciones de `java.util`**: `ArrayList`, `LinkedList`, `Stack`, `Queue`, `HashMap`, `TreeMap`, `Arrays.sort`, `Collections.sort`. Sí se pueden usar `Scanner`, `Iterator` y `Objects`.
- **Las estructuras no imprimen dentro de sus operaciones**: devuelven valores, y el menú imprime.
- **Operar sobre una estructura vacía lanza una excepción con mensaje**, no devuelve `null` en silencio.
- **Solo Sara toca `Main.java` y `menu/`.** Los demás entregan su clase y su prueba.
- **Cada uno toca solo sus archivos.** Si hay que cambiar algo de otro, se le avisa.
- **`main` siempre compila.**
- **La IA no escribe las estructuras** (regla 18 del enunciado). Sirve para entender, revisar y probar. Guarden los prompts que usen: son evidencia.
- **Ninguna clave va al repositorio.** La de Gemini va en la variable de entorno `GEMINI_API_KEY`.

## 7. Cómo se sube el trabajo

Cada uno trabaja en su rama personal. Todas están atrasadas respecto a `main`, así que antes del primer commit hay que ponerlas al día (los comandos están en cada archivo personal).

1. Programar un paso, compilar, correr la prueba y subir el commit.
2. Al terminar un bloque (por ejemplo, la `Cola` completa), abrir un Pull Request a `main`.
3. Un compañero lo revisa: compila y corre las pruebas.
4. Mientras el PR está en revisión, no subir a la rama pasos del bloque siguiente.
5. Cuando se fusiona, poner la rama al día con `main` y seguir.

Si alguien se atasca, avisa en el grupo ese mismo día y se programa en pareja; el commit lo hace el dueño del módulo.

## 8. La parte de IA

- **Los números los calcula el programa.** Gemini recibe solo el resumen y redacta; nunca recibe los registros.
- **`reporte_etl.txt` se escribe siempre**, haya o no internet.
- **Modo manual primero:** el programa guarda el prompt en un archivo; se pega la respuesta de Gemini en otro archivo y el programa arma el reporte. Después se agrega la llamada automática.
- **Se revisa lo que devuelve la IA:** si cambia una cifra o inventa un paso, se corrige y se anota.
- Hay que entregar **7 flujogramas** en Mermaid (ETL, cola, pila, lista, árbol, BFS y DFS), y al menos uno debe salir de una ejecución real del programa.

## 9. Qué entrega el programa

En pantalla, un menú para ejecutar el ETL, buscar un condado, ordenar los registros de un día, recorrer el árbol, ver vecinos y rutas en el grafo, y generar flujogramas y reportes.

En la carpeta `output/`:

| Archivo | Qué contiene |
| --- | --- |
| `casos_limpios.csv`, `condados_limpios.csv`, `vecindad_limpia.csv` | Los datos válidos ya normalizados |
| `errores_etl.txt` | Cada registro rechazado con su línea y su motivo |
| `reporte_etl.txt` | Resumen calculado por el programa |
| `reportes/reporte_etl_ia.md` | Reporte redactado por Gemini |
| `diagramas/` | Los 7 flujogramas |
| `ia/prompts/` y `ia/respuestas/` | Lo que se le pidió a la IA y lo que respondió |

## 10. Lo que no se puede recortar

Las 6 operaciones de la cola, las 8 del árbol, los 6 problemas del grafo, dos algoritmos de ordenamiento, los 7 flujogramas, las evidencias de la IA y las pruebas.

Puntos de la rúbrica (10 en total): ETL 1,25 · Listas 0,75 · Pilas y colas 1,00 · Árbol 1,00 · Grafos 1,00 · BFS y DFS 0,75 · Búsqueda y ordenamiento 0,50 · IA y flujogramas 1,50 · Calidad del código 0,75 · GitHub 0,75 · Defensa individual 0,75.

## 11. La defensa

Es individual: a cada uno le pueden pedir que explique una estructura, que la recorra a mano o que la modifique en vivo. Por eso cada uno escribe su código y también debe entender el de los demás. Cada archivo personal trae las preguntas que le pueden hacer.
