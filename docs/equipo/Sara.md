# Sara — Carga, menús y datos de prueba

**Tu rama:** `Sara-Tacuri`

Este archivo tiene todo lo que haces en el proyecto, de principio a fin. Léelo completo una vez y después ve parte por parte.

## Qué te toca, en simple

Tú haces las tres cosas que unen el trabajo de todos:

1. Los **datos de prueba**: archivos pequeños con errores puestos a propósito, para que cada uno pruebe su parte sin cargar más de un millón de filas.
2. El **menú**: eres la única que toca `Main`. Cuando un compañero termina una clase, tú la conectas a una opción del menú.
3. La **salida** (`Load`): guardas en archivos los datos limpios, los rechazados y el reporte del proceso.

Si tu parte no está, el trabajo de los demás no se ve: nadie puede ejecutarlo desde el programa.

## Tu camino completo

| Parte | Qué creas | Cuándo | Qué necesitas de otros |
| --- | --- | --- | --- |
| 1 | Datos de prueba | Sábado | Nada |
| 2 | Paquete `menu/` | Sábado | Nada |
| 3 | `ResumenETL` | Sábado | Nada |
| 4 | `Load` | Domingo | `Transform` de Luis, `Pila` de Kenneth |
| 5 | Opción "Ejecutar ETL completo" | Domingo | Lectura de Joel, `Transform` de Luis |
| 6 | Menú de búsqueda, ordenamiento y árbol | Después | Clases de Kenneth y Damian |
| 7 | Menú del grafo | Después | Clases de Luis, Joel y Kenneth |
| 8 | Menú de IA | Después | Clases de Josue y Damian |
| 9 | Pruebas de todo junto y capturas | Al final | Todo |

## Con quién se conecta tu trabajo

- **Todos** usan tus datos de prueba y comparan sus resultados con tu archivo de resultados esperados.
- **Joel** te da los métodos que leen y encolan (`encolarCasos`, `encolarCondados`, `encolarVecindad`).
- **Luis** te da, desde `Transform`: la lista de válidos, la pila de errores y los contadores.
- **Kenneth** hizo la `Pila` que tú recorres (sin vaciarla) para escribir los errores.
- **Josue** usa tu `ResumenETL` para armar el texto que se le envía a la IA.
- **Damian** hace contigo la corrida completa con la base real.
- **Todos** te entregan su clase terminada; tú decides cómo se ve en el menú.

## Antes de empezar

- Ten el repo clonado y tu rama al día con `main` (los comandos están al final).
- Abre `src/Main.java`: es tuyo del primer parcial y lo vas a separar en partes.
- Abre los tres archivos de `data/` y mira su primera línea (la cabecera): tus archivos de prueba deben tener exactamente la misma.
- Si no tienes `data/casos_diarios.csv`, el programa te dice cómo generarlo (`python tools/preparar_base.py`).

---

## Parte 1 — Los datos de prueba (`data/pruebas/`)

**Qué creas:** una carpeta con tres archivos pequeños y un archivo de resultados esperados. Deben ser pocos datos, para poder contar a mano qué debe pasar con cada fila.

### `casos_prueba.csv` (unas 40 filas)

Misma cabecera que `casos_diarios.csv`. Usa 5 o 6 condados reales, con varias fechas seguidas cada uno y **ordenado por fecha**, como el archivo real. Entre las filas buenas, pon estas a propósito:

| Fila mala | Qué debe pasar |
| --- | --- |
| Sin FIPS y condado `Unknown` | Rechazada: `CONDADO_DESCONOCIDO` |
| Sin FIPS y condado `New York City` | Rechazada: `AREA_SIN_FIPS` |
| Casos con el texto `abc` | Rechazada: `VALOR_NO_NUMERICO` |
| Casos con `-5` | Rechazada: `VALOR_NEGATIVO` |
| Fecha `2021-13-40` | Rechazada: `FECHA_INVALIDA` |
| Fila con menos columnas | Rechazada: `COLUMNAS_INCOMPLETAS` |
| Dos filas idénticas, seguidas | La segunda rechazada: `DUPLICADO` |
| Dos filas de Puerto Rico con muertes vacías | Válidas, con advertencia |

### `condados_prueba.csv` (unas 12 filas)

Misma cabecera que `condados.csv`. Los condados que usaste en los casos, y además: **dos con FIPS de 4 dígitos** (deben repararse), **una fila de estado** con FIPS terminado en `000` (rechazada: `NO_ES_CONDADO`).

### `vecindad_prueba.tsv` (unas 20 filas)

Misma cabecera que `vecindad.tsv`, separado por tabulaciones. De 8 a 10 condados formando **dos grupos conectados y un condado aislado**. Además: un condado vecino de sí mismo (`AUTO_VECINDAD`) y un par repetido al revés, A–B y B–A (`DUPLICADO`). Dentro de un grupo, que haya tres condados que colinden entre sí, para que Kenneth pueda probar ciclos.

Dibuja ese grafo en papel y guarda una foto o un esquema: Joel, Kenneth y Luis lo necesitan.

### `LEEME.md`

El resultado esperado, escrito por ti contando a mano:

- por cada archivo: cuántas filas se leen, cuántas quedan válidas, cuántas se reparan, cuántas llevan advertencia y cuántas se rechazan **por cada motivo**;
- del grafo: cuántos vértices, cuántas aristas, cuántos grupos, y los vecinos de cada condado.

Este archivo es contra el que todos comparan. Si está mal contado, todos fallan sus pruebas.

## Parte 2 — Separar el menú (`src/menu/`)

**El problema:** hoy `Main.java` tiene todo. Si seis personas agregan opciones al mismo archivo, los cambios chocan.

**Qué creas:** un paquete `menu` con una clase por bloque. Empieza con una sola, que contenga lo que hay hoy:

- `MenuETL`: las opciones actuales de carga, consulta e incidencias.

Y deja en `Main` únicamente: comprobar que los archivos existen, mostrar el menú principal y llamar a la clase que corresponda.

**Qué necesita cada clase de menú:** las listas y objetos cargados deben poder compartirse entre menús (el menú del árbol necesita los datos que cargó el menú del ETL). Lo más simple es una clase pequeña que guarde ese estado compartido, y que cada menú la reciba.

**La regla de este paso:** al terminar, las **9 opciones actuales funcionan exactamente igual** que antes. Pruébalas una por una. Es solo mover código, no cambiar lo que hace.

## Parte 3 — El resumen (`src/model/ResumenETL.java`)

**Qué es:** una clase que guarda **los números del proceso**. No calcula nada: solo los almacena y los devuelve.

**Qué lleva dentro**, para casos y para condados (y más adelante vecindad):

- leídos, válidos, reparados, advertencias, rechazados;
- los rechazados por cada tipo de error;
- el tiempo que tardó el proceso, en milisegundos;
- más adelante: los datos del resultado limpio que da Damian (rango de fechas, cantidad de condados, los condados con más casos).

Con sus getters y setters, y un método que diga si **cuadra**: leídos = válidos + rechazados.

**Por qué es importante:** es lo único que se le envía a la IA. Josue arma el prompt con este objeto. Si un número no está aquí, la IA no lo conoce.

**Prueba (`test/PruebaResumenETL.java`):** guardar valores y leerlos; el método de cuadre da verdadero con números correctos y falso si falta uno.

**Pasos y commits del sábado:**

1. `data/pruebas/` completo, con su `LEEME.md`.
   Commit: `Datos de prueba con errores a proposito y resultados esperados`
2. El paquete `menu/` con las 9 opciones funcionando igual.
   Commit: `Menu: separar Main en el paquete menu`
3. `ResumenETL` con su prueba.
   Commit: `ResumenETL: numeros del proceso con prueba`

**Al terminar el sábado:** abre tu Pull Request **apenas tengas el paso 1**, sin esperar a los otros dos. Joel y Luis necesitan los datos de prueba el domingo por la mañana, y Josue necesita `ResumenETL`.

---

## Parte 4 — La carga (`src/etl/Load.java`)

**Qué es:** la clase que escribe los resultados en la carpeta `output/`. Si la carpeta no existe, la crea.

**Qué archivos escribe:**

| Archivo | Qué contiene | De dónde sale |
| --- | --- | --- |
| `casos_limpios.csv` | Los casos válidos | Lista de válidos de Luis |
| `condados_limpios.csv` | Los condados válidos, con FIPS de 5 dígitos | Lista de condados de Luis |
| `vecindad_limpia.csv` | Las conexiones sin repetidos (más adelante) | Grafo de Luis |
| `errores_etl.txt` | Un rechazado por línea: archivo, línea, tipo y detalle | Pila de errores |
| `reporte_etl.txt` | El resumen en texto claro | `ResumenETL` |

**Cómo se hace cada uno:**

- Abre el archivo con `BufferedWriter` dentro de un `try` con recursos, para que se cierre solo.
- Escribe primero la cabecera, **igual a la del archivo original**, con las columnas en el mismo orden.
- Recorre la lista con su iterador y escribe **una línea por registro**.
- Para los errores, recorre la pila con su iterador, **sin desapilar**: después hay que mostrarla y analizarla.

**Cómo debe verse `reporte_etl.txt`:** un texto que cualquiera entienda, con fecha y hora, y por cada archivo: leídos, válidos, reparados, advertencias, rechazados, y el detalle por motivo. Al final, si cuadra o no, y el tiempo.

**La mejor prueba de `Load`:** vuelve a leer `casos_limpios.csv` con el mismo programa (leer → transformar) y deben salir **0 rechazados**. Si el archivo limpio no se puede volver a leer limpio, algo se escribió mal.

## Parte 5 — La opción "Ejecutar ETL completo"

**Qué creas:** una opción en `MenuETL` que haga todo el proceso de una vez, y una prueba.

**Qué hace, en orden:**

1. Anota la hora de inicio.
2. Crea una cola y pide a `Extract` que encole los condados (Joel). Pide a `Transform` que los transforme (Luis).
3. Lo mismo con los casos.
4. Llena un `ResumenETL` con los contadores de `Transform` y el tiempo.
5. Pide a `Load` que escriba todos los archivos.
6. Muestra el resumen en pantalla.
7. Más adelante: le pasa el resumen a las clases de IA de Josue y Damian para el reporte.

Además de la opción completa, deja las seis opciones separadas que pide el enunciado: extraer, mostrar la cola, transformar, mostrar los válidos, mostrar la pila de errores y cargar. Cada una avisa si falta el paso anterior, como ya hace hoy el programa cuando no se han cargado los casos.

**Prueba (`test/PruebaETLCompleto.java`):** ejecuta el proceso con `data/pruebas/` y comprueba que los números coinciden con tu `LEEME.md` y que leídos = válidos + rechazados.

**Pasos y commits del domingo** (primero pon tu rama al día con `main`):

4. `Load` con los archivos limpios y el de errores.
   Commit: `Load: escribe validos y rechazados en output`
5. `reporte_etl.txt`.
   Commit: `Load: reporte del ETL en texto`
6. La opción "Ejecutar ETL completo", las seis opciones separadas y la prueba.
   Commit: `Menu: ejecutar ETL completo con prueba de integracion`

**Al terminar:** haz con Damian la corrida con la base real. Deben salir 1.174.570 casos válidos y 10.803 rechazados.

---

## Parte 6 — Menú de búsqueda, ordenamiento y árbol

Cuando Kenneth y Damian entreguen, creas `MenuArbol` (y agregas a `MenuETL` lo de ordenar):

- **Ordenar:** "Ranking de condados en una fecha": pide la fecha, llama al ordenamiento de Kenneth y muestra los 10 primeros con el número de comparaciones.
- **Buscar comparado:** pide FIPS y fecha, busca en la lista y en el árbol, y muestra las comparaciones y el tiempo de cada una, lado a lado.
- **Árbol** (las ocho opciones del enunciado): construir (preguntando si en el orden del archivo o balanceado), insertar, buscar, eliminar, inOrden, preOrden, postOrden, altura y número de nodos.
- **Árbol de demostración:** permitir armar un árbol pequeño con claves que escriba el usuario. En la defensa van a pedir "inserte estos números y muestre el inOrden", y con 3.143 condados no se ve nada.

Los recorridos muestran solo los primeros elementos, nunca los 3.143.

Commit: `Menu: busqueda, ordenamiento y arbol`

## Parte 7 — Menú del grafo

Creas `MenuGrafo` con las opciones del enunciado:

- construir el grafo;
- mostrar los vecinos de un condado;
- recorrido BFS desde un condado (Joel);
- recorrido DFS desde un condado (Kenneth);
- buscar la ruta entre dos condados (Joel);
- detectar ciclos (Kenneth) y contar los grupos separados (Joel).

Cuando un recorrido tenga miles de condados, muestra la cantidad y los primeros 20.

Commit: `Menu: grafo, BFS, DFS, rutas y ciclos`

## Parte 8 — Menú de IA

Creas `MenuIA` con:

- generar el flujograma del ETL;
- generar el flujograma de una estructura (cola, pila, lista o árbol);
- generar el flujograma del BFS y el del DFS;
- explicar el árbol;
- explicar el grafo;
- analizar los errores del ETL.

En cada opción, el menú debe decir **de dónde salió el resultado**: de Gemini automático, de Gemini pegado a mano, o del generador propio sin IA.

Commit: `Menu: flujogramas y explicaciones con IA`

## Parte 9 — Pruebas de todo junto y capturas

Cuando ya no se agreguen funciones nuevas:

1. Ejecuta el programa completo de principio a fin con la base real y anota cualquier fallo, con la opción y el mensaje, para pasárselo al dueño de esa clase.
2. Prueba el programa **sin internet**: el ETL debe terminar y dejar `reporte_etl.txt`.
3. Comprueba en GitHub que los 7 diagramas de `output/diagramas/` se dibujan.
4. Toma las capturas de pantalla para el informe de Josue.
5. Revisa el `.gitignore`: `output/casos_limpios.csv` y `data/casos_diarios.csv` no se suben (pesan demasiado); el resto de `output/` sí.

---

## Errores comunes que debes evitar

- Datos de prueba demasiado grandes: si no puedes contar el resultado a mano, no sirven.
- Cabeceras distintas a las reales: el programa rechazaría el archivo entero.
- Cambiar lo que hace una opción al separar el menú: primero mover, después mejorar.
- Armar todo el archivo de salida en un solo texto gigante: se escribe línea por línea.
- Escribir `-1` para "sin dato": deja el campo vacío.
- Vaciar la pila al escribir los errores.
- Hacer cálculos dentro del menú: el menú pide, llama a la clase y muestra.
- Imprimir listas completas de miles de elementos.

## Cómo explicarlo en la defensa

- **El recorrido de un registro:** del archivo a la cola, de la cola a `Transform`, de ahí a la lista o a la pila, y de ahí a `output/`.
- **¿Cómo sabes que el ETL no perdió registros?** Porque leídos es igual a válidos más rechazados, y porque el archivo limpio se puede volver a leer con cero rechazados.
- **¿Por qué el menú está separado en clases?** Para que cada bloque se pueda cambiar sin tocar los demás.
- **¿Por qué datos de prueba pequeños?** Porque así el resultado esperado se calcula a mano y se puede comprobar.
- **Te pueden pedir:** agregar en vivo una opción nueva al menú, o agregar una línea al reporte.

## Lista final: tu parte está completa cuando

- [ ] `data/pruebas/` tiene los tres archivos y un `LEEME.md` bien contado.
- [ ] `Main` solo tiene el menú principal y cada bloque está en `menu/`.
- [ ] `Load` escribe todos los archivos y el limpio se vuelve a leer sin rechazados.
- [ ] El ETL completo se ejecuta desde una opción y cuadra con la base real.
- [ ] Todas las opciones que pide el enunciado están en el menú.
- [ ] El programa funciona sin internet.

---

## Cómo trabajar y subir

**Antes de empezar** (y cada vez que se fusione algo a `main`), pon tu rama al día. Hoy tu rama está atrasada respecto a `main`:

```bash
git fetch origin
git checkout Sara-Tacuri
git merge origin/main
```

**Compilar y correr una prueba** (ejemplo con `PruebaResumenETL`; se ejecuta desde la carpeta principal del proyecto):

```bash
javac -encoding UTF-8 -d bin -sourcepath "src;test" test/PruebaResumenETL.java
java -cp bin PruebaResumenETL
```

**Subir un paso** (ejemplo con el primero):

```bash
git add src/model/ResumenETL.java test/PruebaResumenETL.java
git commit -m "ResumenETL: numeros del proceso con prueba"
git push origin Sara-Tacuri
```

**Pull Request:** cuando termines un bloque, ábrelo en GitHub desde tu rama hacia `main` y pide revisión en el grupo. Mientras esté en revisión, no subas a tu rama pasos del bloque siguiente: entrarían en el mismo Pull Request. Cuando se fusione, vuelve a poner tu rama al día.

## Reglas del grupo

- Subes tu propio código desde tu cuenta. Nadie sube por otro.
- Un paso = un commit pequeño que compila y trae su prueba.
- No uses colecciones de `java.util` (`ArrayList`, `LinkedList`, `Stack`, `Queue`, `HashMap`…). Sí puedes usar `Scanner`, `Iterator` y `Objects`.
- No toques `Main.java` ni `menu/` si no eres Sara, ni archivos de otro compañero sin avisarle.
- La IA sirve para entender, revisar y probar, no para escribir tus estructuras: en la defensa las explicas y las modificas en vivo. Guarda los prompts que uses.
- Si te atascas, avisa en el grupo el mismo día.

El proyecto completo está explicado en [PROYECTO.md](../PROYECTO.md).
