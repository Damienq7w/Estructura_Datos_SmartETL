# Luis — Transformación de datos y grafo

**Tu rama:** `Rama-Luis-Silva`

Este archivo tiene todo lo que haces en el proyecto, de principio a fin. Léelo completo una vez y después ve parte por parte.

## Qué te toca, en simple

Tú construyes dos piezas centrales:

- El **filtro** del programa (`Transform`). Recibes los registros sucios que Joel dejó en la cola y, uno por uno, decides: si se puede arreglar, lo arreglas y va a la lista de válidos; si no tiene arreglo, va a la pila de errores con su motivo.
- El **grafo** de condados vecinos: un mapa donde cada condado sabe con cuáles colinda. Joel y Kenneth lo recorren después.

## Tu camino completo

| Parte | Qué creas | Cuándo | Qué necesitas de otros |
| --- | --- | --- | --- |
| 1 | `ErrorETL` | Sábado | Nada |
| 2 | Métodos de arreglo en `Transform` | Sábado | Nada |
| 3 | `transformarCasos` | Domingo | `Cola` de Joel, `Pila` de Kenneth, datos de Sara |
| 4 | `transformarCondados` y contadores | Domingo | Lo mismo |
| 5 | Duplicados | Después | Árbol de Damian, `ultimo()` de Kenneth |
| 6 | `Vertice` y `Grafo` | Después | Nada |
| 7 | `transformarVecindad` y construir el grafo | Después | Lectura de vecindad de Joel |
| 8 | Explicación del grafo y análisis de errores con IA | Después | Clases de IA de Josue y Damian |

## Con quién se conecta tu trabajo

- **Joel** te entrega una `Cola<RegistroCrudo>`. Cada `RegistroCrudo` trae el número de línea (`getNumeroLinea()`) y los campos como texto (`getCampos()`), sin revisar.
- **Kenneth** te da la `Pila`, donde guardas los errores con `apilar()`.
- **Sara** recibe tu lista de válidos, tu pila de errores y tus contadores, y con eso escribe los archivos y el reporte.
- **Damian** recibe tus condados limpios para llenar el árbol, y te presta el árbol para detectar duplicados.
- **Joel** (BFS) y **Kenneth** (DFS) recorren tu `Grafo`.
- **Josue** y **Damian** te dan las clases para pedirle a la IA una explicación.

## Antes de empezar

- Ten el repo clonado y tu rama al día con `main` (los comandos están al final).
- Abre `src/model/CasoDiario.java`, `Condado.java`, `Vecindad.java` y `Esquema.java`: son tuyas del primer parcial y `Transform` crea objetos de esas clases. En `Esquema` están las posiciones de cada columna.
- Abre `src/etl/Extract.java`: hoy cuenta los problemas al leer. Tu `Transform` se queda con ese trabajo y lo hace bien.

---

## Parte 1 — El error (`src/model/ErrorETL.java`)

**Qué es:** una clase que representa **un registro rechazado**.

**Qué lleva dentro** (todo `final`, con constructor y getters):

- `archivo`: de qué archivo vino (`casos`, `condados` o `vecindad`);
- `linea`: el número de línea;
- `tipo`: el motivo, en una palabra clave;
- `detalle`: un texto que explique qué pasó, por ejemplo "casos = abc".

Agrega un `mostrar()` que devuelva todo en una línea de texto; Sara lo usa para escribir el archivo de errores.

**Los tipos de error.** Defínelos en un solo lugar (un `enum` llamado `TipoError` es lo más claro):

| Tipo | Cuándo se usa |
| --- | --- |
| `COLUMNAS_INCOMPLETAS` | La fila trae menos campos de los que dice la cabecera |
| `CONDADO_DESCONOCIDO` | Sin FIPS y el condado dice `Unknown` |
| `AREA_SIN_FIPS` | Sin FIPS, pero con nombre (New York City, Kansas City, Joplin) |
| `VALOR_NO_NUMERICO` | Casos o muertes no son un número |
| `VALOR_NEGATIVO` | Casos o muertes menores que cero |
| `FECHA_INVALIDA` | La fecha no tiene la forma `AAAA-MM-DD` |
| `NO_ES_CONDADO` | Fila del catálogo que es un país o un estado |
| `DUPLICADO` | El mismo registro ya se había cargado |
| `AUTO_VECINDAD` | Un condado que aparece como vecino de sí mismo |

**Prueba (`test/PruebaErrorETL.java`):** crear un error y comprobar que los getters y `mostrar()` devuelven lo esperado.

## Parte 2 — Los métodos de arreglo (`src/etl/Transform.java`)

Antes de recorrer la cola, escribe las herramientas pequeñas. Cada una es un método corto que recibe un texto y devuelve el texto arreglado o una señal de que no sirve. **No necesitan ni la cola ni la pila**, por eso se pueden hacer el sábado.

| Método | Qué hace |
| --- | --- |
| Limpiar texto | Quita los espacios del principio y del final. Si llega `null`, devuelve texto vacío |
| Arreglar FIPS | Si el texto tiene solo dígitos y menos de 5, le pone ceros a la izquierda hasta llegar a 5. `1001` pasa a ser `01001`. Si está vacío o tiene letras, indica que no es válido |
| Leer número | Convierte un texto a número entero. Si no es un número, **no devuelve 0**: indica que falló, para poder rechazar el registro |
| Revisar fecha | Comprueba que tenga 10 caracteres con la forma `AAAA-MM-DD` |

**Por qué el FIPS se arregla así.** El FIPS tiene siempre 5 dígitos. En `condados.csv` hay 316 códigos que llegaron con 4 porque alguien los guardó como número y el cero de la izquierda se perdió. `"1001"` y `"01001"` son textos distintos: sin arreglarlo, un caso con `01001` no encontraría su condado. **Nunca conviertas el FIPS a `int`.**

**Prueba (`test/PruebaTransform.java`):** `1001` da `01001`; `01001` queda igual; vacío es inválido; `" Texas "` queda `Texas`; `abc` no es número; `2021-13-45` o `21-1-1` no son fechas válidas.

**Pasos y commits del sábado:**

1. `ErrorETL`, `TipoError` y su prueba.
   Commit: `ErrorETL: tipos de error con prueba`
2. Los métodos de arreglo con sus pruebas.
   Commit: `Transform: normalizacion de FIPS, texto y numeros`

**Al terminar el sábado:** abre tu Pull Request a `main`.

---

## Parte 3 — Transformar los casos (`transformarCasos`)

**Qué creas:** el método `transformarCasos(Cola<RegistroCrudo> cola)` en `Transform`.

**Qué guarda `Transform` por dentro:**

- una `ListaEnlazada<CasoDiario>` con los casos válidos;
- una `Pila<ErrorETL>` con los rechazados;
- los contadores (parte 4).

**El recorrido, paso a paso:**

```
mientras la cola no esté vacía:
    sacar un registro de la cola
    contar uno más como leído
    revisar las reglas en orden; la primera que falla decide el rechazo
    si falla alguna → apilar un ErrorETL con el tipo y el detalle
    si pasa todas   → crear el CasoDiario ya arreglado y agregarlo a la lista
```

**Las reglas, en este orden:**

1. ¿Trae todas las columnas? Si no: `COLUMNAS_INCOMPLETAS`.
2. ¿Tiene FIPS? Si está vacío: `CONDADO_DESCONOCIDO` cuando el condado dice `Unknown`, y `AREA_SIN_FIPS` en los demás casos.
3. ¿La fecha es válida? Si no: `FECHA_INVALIDA`.
4. ¿Los casos son un número? Si no: `VALOR_NO_NUMERICO`. ¿Son negativos? `VALOR_NEGATIVO`.
5. Las muertes: si el campo está **vacío**, el registro es **válido** y se guarda como "sin dato" (cuenta una advertencia). Si tiene texto que no es número: `VALOR_NO_NUMERICO`.

**Escribe un método privado y corto por cada regla.** En la defensa pueden pedirte agregar una regla nueva en vivo: así es un método más y una línea.

**Sobre "arreglar" un registro.** `CasoDiario` tiene sus campos `final`, y está bien así. Arreglar significa crear un `CasoDiario` nuevo con los valores ya corregidos. No les quites el `final`.

**Cómo probarlo:** arma a mano una cola con pocas filas (una buena, una sin FIPS con `Unknown`, una sin FIPS con nombre, una con casos `abc`, una con muertes vacías) y verifica que cada una termina donde debe y con el tipo correcto. Después, con `data/pruebas/casos_prueba.csv`, los resultados deben coincidir con el `LEEME.md` de Sara.

## Parte 4 — Transformar los condados y los contadores

**`transformarCondados(Cola<RegistroCrudo> cola)`**, con el mismo recorrido y estas reglas:

1. ¿Trae todas las columnas? Si no: `COLUMNAS_INCOMPLETAS`.
2. ¿Es un país o un estado y no un condado? Lo es si el FIPS está vacío, es `0`, termina en `000`, o el estado dice `NA` (esta condición ya está escrita en `Extract`). Si lo es: `NO_ES_CONDADO`.
3. ¿El FIPS tiene menos de 5 dígitos? **Se arregla** con ceros a la izquierda y cuenta como **reparado**.

Guarda los válidos en una `ListaEnlazada<Condado>`.

**Los contadores** que Sara necesita, con sus getters, por separado para casos y para condados:

- leídos;
- válidos;
- reparados;
- advertencias;
- rechazados;
- **rechazados por cada tipo**.

Para contar por tipo no hay `HashMap`. Si usaste un `enum`, basta un arreglo de enteros con una casilla por tipo.

**Cómo saber que quedó bien, con la base real:**

| Archivo | Leídos | Válidos | Rechazados | Otros |
| --- | --- | --- | --- | --- |
| Casos | 1.185.373 | 1.174.570 | 10.803 (9.708 `CONDADO_DESCONOCIDO` + 1.095 `AREA_SIN_FIPS`) | 28.470 advertencias por muertes vacías |
| Condados | 3.195 | 3.143 | 52 `NO_ES_CONDADO` | 316 reparados |

Y siempre: leídos = válidos + rechazados. Si tus números coinciden, tu `Transform` está bien.

**Pasos y commits del domingo** (primero pon tu rama al día con `main`):

3. `transformarCasos` con sus reglas y pruebas.
   Commit: `Transform: casos, rechazos a la pila de errores`
4. `transformarCondados` y todos los contadores.
   Commit: `Transform: condados, advertencias y conteos por tipo`

**Al terminar:** abre el PR enseguida. Sara necesita tus contadores para el reporte.

---

## Parte 5 — Duplicados

**El problema:** saber si un registro ya se había cargado (mismo condado y misma fecha). Compararlo con todos los anteriores es imposible con más de un millón de registros.

**La solución:** el árbol de Damian tiene un nodo por condado, y cada nodo guarda la lista de casos de ese condado en orden de fecha. Entonces:

1. Busca el condado del registro en el árbol.
2. Mira el **último** caso de su lista (con `ultimo()`, de Kenneth).
3. Si tiene la misma fecha, es un `DUPLICADO` y se rechaza. Si no, se agrega a la lista de válidos y al historial del condado.

Funciona porque el archivo viene ordenado por fecha.

En la base real hay **0 duplicados**; la regla se prueba con el archivo pequeño de Sara, que trae dos filas idénticas a propósito.

Commit: `Transform: deteccion de duplicados con el indice`

## Parte 6 — El grafo (`src/estructuras/Vertice.java` y `Grafo.java`)

**Qué es:** un mapa de condados. Cada condado es un **vértice** y cada "colinda con" es una **arista**.

**`Vertice<T>` lleva dentro:**

- `dato`: el condado;
- `adyacentes`: una `ListaEnlazada<Vertice<T>>` con sus vecinos;
- tres campos que usan Joel y Kenneth al recorrer: `visitado`, `anterior` (desde qué vértice se llegó) y `distancia` (saltos desde el origen).

**`Grafo<T>` lleva dentro:**

- `vertices`: una `ListaEnlazada<Vertice<T>>` con todos;
- `dirigido`: un indicador. El grafo real es **no dirigido** (si A es vecino de B, B lo es de A). El indicador permite crear además un grafo pequeño con flechas de un solo sentido, que Kenneth necesita para probar ciclos.

**Los métodos:**

| Método | Qué hace |
| --- | --- |
| `agregarVertice(T dato)` | Agrega un vértice si no existe todavía |
| `agregarArista(T origen, T destino)` | Conecta dos vértices. Si el grafo no es dirigido, agrega cada uno a la lista del otro. Rechaza la arista de un vértice consigo mismo y la que ya existe |
| `buscarVertice(T dato)` | Devuelve el vértice con ese dato, o `null` |
| `existeArista(T origen, T destino)` | Mira si `destino` está en la lista de vecinos de `origen` |
| `vecinos(T dato)` | Devuelve la lista de vecinos de un vértice |
| `numeroVertices()` y `numeroAristas()` | Cantidades |
| `reiniciarRecorrido()` | Pone `visitado` en falso, `anterior` en `null` y `distancia` en 0 en todos los vértices |

**Por qué listas y no una tabla.** Una tabla de 3.234 por 3.234 tendría más de 10 millones de casillas para guardar solo 9.483 conexiones. Con listas, cada condado guarda solo sus vecinos (14 como máximo).

**Pruebas (`test/PruebaGrafo.java`):** con un grafo de 8 a 10 condados dibujado en papel: los vecinos de cada uno son los del dibujo; si B está en la lista de A, A está en la de B; una arista de un vértice consigo mismo se rechaza; una arista repetida no cambia el número de aristas.

Commits: `Vertice y Grafo: listas de adyacencia con pruebas` · `Grafo: reiniciar recorrido y modo dirigido`

## Parte 7 — Limpiar la vecindad y construir el grafo real

**`transformarVecindad(Cola<RegistroCrudo> cola)`** recorre la cola de vecindad y:

1. Arregla los dos FIPS a 5 dígitos.
2. Si origen y destino son el mismo: `AUTO_VECINDAD`.
3. Agrega los dos vértices al grafo si no existen, y pide `agregarArista`. Si la arista ya existía (el archivo trae cada par dos veces, A–B y B–A): `DUPLICADO`.

Fíjate que el repetido se detecta mirando solo la lista de vecinos del origen, de 14 elementos como máximo. Hoy `Extract` lo hace comparando con las 22.200 filas anteriores, que es lentísimo.

**Cómo saber que quedó bien, con la base real:** 22.200 filas leídas, 3.234 `AUTO_VECINDAD`, 9.483 `DUPLICADO`, y un grafo de **3.234 vértices y 9.483 aristas**, con 14 vecinos como máximo en un condado.

**Y con esto resuelves el primero de los seis problemas del grafo:** "condados que colindan con uno dado" es simplemente `vecinos(...)`.

Commit: `Transform: vecindad limpia y construccion del grafo`

## Parte 8 — Explicación del grafo y análisis de errores con IA

Te tocan dos textos generados con IA, usando las clases de Josue (arma el prompt) y Damian (habla con Gemini):

**a) Explicación del grafo real.** Se le envía a la IA: número de vértices y aristas, cantidad de grupos separados (17, lo calcula Joel), el máximo de vecinos y la vecindad de un condado concreto. La IA explica **este** grafo, no la definición de libro.

**b) Análisis de errores del ETL.** Se le envía **solo tu tabla de conteos por tipo**, nunca los registros. La IA debe poder señalar dos patrones reales, y tú debes confirmarlos: todas las muertes vacías son de Puerto Rico, y los registros sin FIPS se reparten entre `Unknown` y tres ciudades.

Revisa lo que responde: si cambia una cifra, corrígela y anótalo en las evidencias.

Commit: `IA: explicacion del grafo y analisis de errores`

---

## Errores comunes que debes evitar

- **Primero arreglar, después rechazar.** Un FIPS de 4 dígitos se repara, no se rechaza.
- **El FIPS es `String`.** Nunca lo conviertas a `int`, ni siquiera para compararlo.
- Un número inválido no se cambia por 0: se rechaza.
- No valides dentro de `Extract` ni escribas archivos dentro de `Transform`: cada clase hace una sola cosa.
- No inventes reglas que no puedas justificar con los datos.
- No detectes duplicados comparando con todos los registros anteriores.
- No guardes a la vez la lista vieja, la cola y la lista de válidos: la cola se vacía al transformar.
- No uses `HashMap` ni `HashSet` para contar o para marcar visitados.
- No construyas el grafo con FIPS sin arreglar.
- No intentes dibujar el grafo completo: solo un condado y sus vecinos.

## Cómo explicarlo en la defensa

- **¿Por qué las muertes vacías son advertencia y el FIPS vacío es rechazo?** Sin FIPS no se puede saber de qué condado es el registro, así que no sirve para nada. Sin muertes, el resto del registro sigue siendo útil.
- **¿Por qué el FIPS es texto?** Porque como número perdería el cero inicial, y `01001` y `1001` dejarían de coincidir.
- **¿Cómo detectas duplicados sin comparar con todo?** Con el árbol: voy al condado y miro solo su último registro.
- **¿Por qué listas de adyacencia y no una tabla?** Porque hay pocos vecinos por condado; la tabla estaría casi vacía.
- **¿Por qué no hay ruta entre Texas y Hawái?** Porque el grafo tiene 17 grupos separados.
- **Te pueden pedir:** agregar una regla de validación en vivo, o explicar la lista de vecinos de un condado concreto.

## Lista final: tu parte está completa cuando

- [ ] `ErrorETL` y sus tipos existen con su prueba.
- [ ] `Transform` procesa casos, condados y vecindad, y tus números coinciden con los esperados.
- [ ] Los duplicados se detectan con el árbol.
- [ ] `Grafo` tiene sus métodos, sus pruebas, y el grafo real tiene 3.234 vértices y 9.483 aristas.
- [ ] Existen la explicación del grafo y el análisis de errores hechos con IA y revisados por ti.
- [ ] Puedes explicar cada regla sin mirar el código.

---

## Cómo trabajar y subir

**Antes de empezar** (y cada vez que se fusione algo a `main`), pon tu rama al día. Hoy tu rama está atrasada respecto a `main`:

```bash
git fetch origin
git checkout Rama-Luis-Silva
git merge origin/main
```

**Compilar y correr una prueba** (ejemplo con `PruebaErrorETL`; se ejecuta desde la carpeta principal del proyecto):

```bash
javac -encoding UTF-8 -d bin -sourcepath "src;test" test/PruebaErrorETL.java
java -cp bin PruebaErrorETL
```

**Subir un paso** (ejemplo con el primero):

```bash
git add src/model/ErrorETL.java test/PruebaErrorETL.java
git commit -m "ErrorETL: tipos de error con prueba"
git push origin Rama-Luis-Silva
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
