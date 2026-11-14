# Damian — Árbol de búsqueda, conexión con la IA e integración

**Tu rama:** `Damian_Cunalata`

Este archivo tiene todo lo que haces en el proyecto, de principio a fin. Léelo completo una vez y después ve parte por parte.

## Qué te toca, en simple

Tú haces tres cosas:

- El **árbol binario de búsqueda**: permite encontrar un condado por su código en unos 12 pasos, en lugar de recorrer más de un millón de registros.
- La clase que **habla con Gemini**: envía el resumen y recibe el reporte.
- La **integración**: como líder, cuidas que las piezas de todos encajen y que `main` siempre funcione.

Un **árbol binario de búsqueda** guarda los datos de forma que, desde cualquier nodo, todo lo menor queda a su izquierda y todo lo mayor a su derecha. Para buscar, en cada nodo descartas la mitad.

## Tu camino completo

| Parte | Qué creas | Cuándo | Qué necesitas de otros |
| --- | --- | --- | --- |
| 1 | `NodoArbol` y `ArbolBST`: insertar, buscar, contar | Sábado | Nada |
| 2 | Recorridos | Sábado | Nada |
| 3 | Altura y construcción por medios | Sábado | Nada |
| 4 | Eliminar | Domingo | Nada |
| 5 | `IndiceCondado` y árbol de condados | Domingo | Condados limpios de Luis, `ultimo()` de Kenneth |
| 6 | `AnalizadorIA` en modo manual | Domingo | `GeneradorPrompt` de Josue |
| 7 | `AnalizadorIA` automático | Después | Clave de Gemini, permiso del docente |
| 8 | Historiales y búsqueda comparada | Después | ETL integrado |
| 9 | Datos limpios para el resumen | Después | `ResumenETL` de Sara |
| 10 | Avisos a la bitácora, flujograma y explicación del árbol | Después | `Bitacora` de Josue |
| 11 | Tareas de líder | Siempre | — |

## Con quién se conecta tu trabajo

- **Luis** te entrega la lista de condados limpios para llenar el árbol, y usa tu árbol para detectar duplicados.
- **Kenneth** te da `ultimo()` en `ListaEnlazada` y el acceso por posición en `ListaSecuencial`.
- **Josue** te entrega el prompt ya armado; tu `AnalizadorIA` lo envía y devuelve la respuesta.
- **Sara** conecta tus clases al menú y te pide los datos limpios para el resumen.

## Antes de empezar

- Ten tu rama al día con `main` (los comandos están al final).
- Abre `src/model/Condado.java` y mira su `compareTo` y su `equals`: el árbol decide solo con `compareTo`.
- Abre `src/algoritmos/Busqueda.java`: ya cuenta comparaciones en la lista; después la comparas con el árbol.

---

## Parte 1 — El árbol: insertar, buscar y contar

**Qué creas:**

- `src/estructuras/NodoArbol.java`: `NodoArbol<T>` con tres atributos: `dato`, `izquierdo` y `derecho`.
- `src/estructuras/ArbolBST.java`: `ArbolBST<T extends Comparable<T>>` con la referencia `raiz` y un contador de nodos.

**Los primeros métodos:**

| Método | Qué hace | Cómo pensarlo |
| --- | --- | --- |
| `boolean insertar(T dato)` | Agrega un dato | Si no hay raíz, el dato es la raíz. Si hay, baja desde la raíz: si es menor, a la izquierda; si es mayor, a la derecha; hasta encontrar un lugar vacío. Si encuentra uno **igual**, no lo repite y devuelve `false` |
| `T buscar(T dato)` | Devuelve el dato guardado, o `null` | Baja igual que al insertar, comparando en cada nodo. Cuenta cuántas comparaciones hizo |
| `int contarNodos()` | Cuántos nodos hay | Recorriendo el árbol (así se puede comparar con el contador interno) |
| `boolean estaVacio()` | | |
| `int getComparaciones()` | Las comparaciones de la última búsqueda | |

**`insertar` y `buscar` van con un `while`, no con recursión.** En el experimento de la parte 3 el árbol llega a tener miles de niveles.

**Usa solo `compareTo` para decidir.** No mezcles `equals`.

## Parte 2 — Los recorridos

| Método | Orden |
| --- | --- |
| `ListaEnlazada<T> inOrden()` | Izquierda, nodo, derecha |
| `ListaEnlazada<T> preOrden()` | Nodo, izquierda, derecha |
| `ListaEnlazada<T> postOrden()` | Izquierda, derecha, nodo |

**Devuelven una lista, no imprimen.** Así se pueden probar, mostrar con límite y enviar a la IA.

Estos sí van recursivos: cada uno son tres líneas, y es lo que se espera que expliques.

**La propiedad que debes saber:** el `inOrden` de un árbol de búsqueda **siempre sale ordenado** de menor a mayor.

## Parte 3 — Altura y construcción por medios

**`int altura()`:** la cantidad de niveles. Convención del grupo: árbol vacío = 0, un solo nodo = 1.

**El problema que hay que mostrar.** `condados.csv` viene **ordenado por FIPS**. Si insertas en ese orden, cada condado es mayor que el anterior y siempre va a la derecha: el árbol queda como una lista de 3.143 niveles, y buscar ya no descarta nada.

**La solución, sin árboles avanzados:** construir "por medios".

1. Pasa los condados (ya ordenados) a una `ListaSecuencial`.
2. Inserta primero el que está **en la mitad**.
3. Repite lo mismo con la mitad izquierda y con la mitad derecha.

Así el árbol queda con unos 12 niveles.

**Qué creas:** un método que recibe una `ListaSecuencial` ordenada y construye el árbol de esa forma.

**El experimento que pide el enunciado** ("qué pasa cuando el árbol se desbalancea"), con tus propios números:

| Cómo se construyó | Nodos | Altura | Comparaciones al buscar el último FIPS |
| --- | --- | --- | --- |
| En el orden del archivo | | (debe ser igual al número de nodos) | |
| Por medios | | (cerca de 12) | |

Llena la tabla con lo que mida tu programa; va al informe.

**Las pruebas (`test/PruebaArbolBST.java`)**, con un árbol de 7 a 10 claves cuyos resultados conoces de antemano:

1. Árbol vacío: `estaVacio()`, 0 nodos, altura 0, buscar devuelve `null`.
2. Insertar las claves: los tres recorridos y la altura coinciden con lo que calculaste a mano.
3. `inOrden()` sale ordenado.
4. Buscar una clave que existe y una que no.
5. Insertar una clave repetida devuelve `false` y no cambia el tamaño.
6. `contarNodos()` coincide con el contador interno.
7. Insertar 1.000 números en orden da altura 1.000; por medios, altura 10.

**Pasos y commits del sábado:**

1. `NodoArbol` y `ArbolBST` con `insertar`, `buscar`, `contarNodos`, y sus pruebas.
   Commit: `ArbolBST: insertar, buscar y contarNodos con pruebas`
2. Los tres recorridos.
   Commit: `ArbolBST: recorridos inOrden, preOrden y postOrden`
3. `altura` y la construcción por medios.
   Commit: `ArbolBST: altura y construccion por medios`

**Al terminar el sábado:** abre tu Pull Request y revisa los de tus compañeros.

---

## Parte 4 — Eliminar

**`boolean eliminar(T dato)`:** quita un dato del árbol. Devuelve `false` si no estaba. Tiene tres casos, y los tres se califican:

1. **El nodo es una hoja** (no tiene hijos): simplemente se quita.
2. **El nodo tiene un solo hijo:** ese hijo sube a ocupar su lugar.
3. **El nodo tiene dos hijos:** se busca el **menor del subárbol derecho** (su sucesor), se copia ese dato en el nodo, y se elimina el sucesor de donde estaba (que es un caso 1 o 2).

Es el método que más preguntan. Tienes que poder dibujar los tres casos.

**Pruebas:** eliminar una hoja, un nodo con un hijo, uno con dos hijos, la raíz, una clave que no existe, y todas hasta vaciar el árbol. Después de cada eliminación: `inOrden()` sigue ordenado y `contarNodos()` bajó en 1.

## Parte 5 — El índice de condados (`src/model/IndiceCondado.java`)

**La decisión de diseño.** El árbol **no** guarda los 1.185.373 registros diarios. Guarda **un nodo por condado** (unos 3.200), y cada nodo lleva adentro la lista de casos de ese condado.

**`IndiceCondado` lleva dentro:**

- FIPS, nombre y estado;
- `historial`: una `ListaEnlazada<CasoDiario>` con los casos de ese condado, en orden de fecha.

Implementa `Comparable<IndiceCondado>` comparando **solo el FIPS** (texto de 5 dígitos).

**Cómo se busca "el condado X en la fecha Y":** se busca el condado en el árbol (unas 12 comparaciones) y luego la fecha en su historial (365 como máximo).

**Qué creas el domingo:** `IndiceCondado` y un método que, con la lista de condados limpios de Luis, construye el árbol por medios.

**Prueba:** con `data/pruebas/condados_prueba.csv`, buscar cada condado lo encuentra, y los que tenían FIPS de 4 dígitos se encuentran con su código de 5.

## Parte 6 — `AnalizadorIA` en modo manual (`src/ia/AnalizadorIA.java`)

**Qué es:** la clase que recibe un prompt y devuelve la respuesta de la IA. Empieza por el modo manual, que funciona sin internet y sin clave.

**Cómo funciona el modo manual:**

1. Recibe el prompt (se lo da Josue) y un nombre, por ejemplo `reporte_etl`.
2. Lo guarda en `output/ia/prompts/reporte_etl.txt`.
3. Busca si existe `output/ia/respuestas/reporte_etl.txt`.
4. Si existe, lo lee y devuelve ese texto. Si no, avisa: "Copia el prompt en Gemini y pega la respuesta en este archivo".

**Qué más creas:** el método que toma la respuesta y escribe `output/reportes/reporte_etl_ia.md`, con: título, fecha y hora, **de dónde salió** (Gemini manual, Gemini automático o generador propio) y el texto.

**Pasos y commits del domingo** (primero pon tu rama al día con `main`):

4. `eliminar` con sus pruebas.
   Commit: `ArbolBST: eliminar con sus tres casos`
5. `IndiceCondado` y la construcción del árbol de condados.
   Commit: `IndiceCondado: indice por FIPS con historial`
6. `AnalizadorIA` en modo manual.
   Commit: `AnalizadorIA: modo manual desde archivo`

**Al terminar:** haz con Sara la corrida completa con la base real y comprueba que sale el reporte.

---

## Parte 7 — `AnalizadorIA` automático

**Antes de programarlo:**

1. Pregunta al docente si autoriza usar `java.net.http`, que viene con Java y sirve para hacer peticiones por internet. No es una colección ni una estructura evaluada, pero confírmalo.
2. Crea tu clave de Gemini y guárdala en la variable de entorno `GEMINI_API_KEY` de tu computadora. **Nunca** en el código ni en ningún archivo del repo.
3. Abre la documentación oficial de la API de Gemini y toma de ahí la dirección del servicio, el nombre del modelo y el formato de la petición y de la respuesta. Cambian con frecuencia: no los copies de un tutorial viejo.

**Qué hace el modo automático:**

1. Lee la clave de la variable de entorno. Si no existe, avisa y pasa al modo manual.
2. Arma el cuerpo de la petición. No hay librería para esto, así que se arma como texto, y hay que **escapar** las comillas, las barras invertidas y los saltos de línea del prompt; si no, la petición falla.
3. Envía la petición con un **tiempo límite** (por ejemplo 30 segundos).
4. De la respuesta, extrae el texto generado. Si no lo encuentra, guarda la respuesta completa y pasa al modo manual.
5. Guarda el prompt y la respuesta en `output/ia/`.

**La regla principal:** nada de esto puede tumbar el programa. Sin internet, sin clave, con clave inválida o sin cuota: mensaje claro y modo manual.

**Pruebas:** un texto con comillas y saltos de línea queda bien escapado; sin variable de entorno no hay excepción; sin internet no hay excepción; una respuesta con texto alrededor del diagrama permite extraer solo el diagrama.

Commit: `AnalizadorIA: llamada a Gemini con tiempo limite y respaldo manual`

## Parte 8 — Historiales y búsqueda comparada

**Llenar los historiales.** Por cada caso válido, se busca su condado en el árbol y se agrega el caso a su historial. Esto se hace **mientras Luis transforma**, y por eso el orden del proceso completo es:

1. Leer y transformar los condados.
2. Construir el árbol de condados por medios.
3. Leer y transformar los casos; por cada uno, Luis busca el condado en tu árbol, mira su último registro (duplicados) y, si es válido, lo agrega a la lista y al historial.
4. Hay 87 códigos FIPS que aparecen en los casos y no en el catálogo. Se agregan al árbol como condados nuevos, con una advertencia. Como casi todos son mayores que el mayor del catálogo, cuelgan todos a la derecha y **desbalancean** el árbol.
5. Al terminar, **reconstruye el árbol por medios** a partir de su `inOrden()`. Muestra la altura antes y después.

Acuerda este orden con Luis y con Sara.

**Búsqueda comparada.** Para el mismo FIPS y fecha:

- en la lista (ya existe en `Busqueda`): cuenta comparaciones;
- en el árbol: las comparaciones del árbol más las del historial.

Pruébalo con un registro del principio del archivo, uno del final y uno que no existe. Sara lo muestra lado a lado en el menú. En el README pon **lo que midas**, no un número de memoria.

Commits: `IndiceCondado: historial de casos por condado` · `Busqueda: comparacion entre lista y arbol`

## Parte 9 — Datos limpios para el resumen

El reporte de la IA debe hablar de los datos, no solo de los errores. Con el árbol ya lleno, calcula y entrega a Sara para el `ResumenETL`:

- la primera y la última fecha;
- cuántos condados hay;
- los 5 condados con más casos al último día (el último de cada historial es su total);
- los estados con más casos.

Todo lo calcula el programa. La IA solo lo redacta.

Commit: `Resumen: datos limpios agregados desde el indice`

## Parte 10 — Bitácora, flujograma y explicación del árbol

Te toca **uno de los siete flujogramas**, el del **árbol**, y la explicación del árbol real.

1. Tu árbol recibe de forma opcional el "oyente" que crea Josue. Avisos: `COMPARAR`, `IR_IZQUIERDA`, `IR_DERECHA`, `INSERTAR`, `ENCONTRADO`, con las claves reales.
2. Con la bitácora encendida, busca o inserta un FIPS real y genera `output/diagramas/bst.md`. Este diagrama sale de una ejecución real: muestra las decisiones izquierda/derecha que de verdad se tomaron.
3. **Explicación del árbol:** se le envía a la IA el número de nodos, la altura, la raíz, los primeros 3 o 4 niveles y la comparación entre el árbol en orden de archivo y el balanceado. La IA explica **este** árbol.
4. Revisa lo que responde y corrige lo que esté mal, anotándolo.

Commits: `ArbolBST: avisos a la bitacora y flujograma` · `IA: explicacion del arbol real`

## Parte 11 — Tus tareas de líder

- **Antes del sábado:** sube la carpeta `docs/` al repo y avisa al grupo.
- **Cada día:** pregunta en el grupo qué subió cada uno y qué lo frena.
- **Cada Pull Request:** pide la revisión al agente (escribe "revisa el PR" y el número en la sesión de Claude) y pasa el resultado al autor. Tú decides cuándo se fusiona.
- **Habla con el docente:** los hitos atrasados y el permiso para `java.net.http`.
- **Cuida que `main` compile siempre.**
- **Antes de la defensa:** ensayo completo sin internet.
- **No hagas el trabajo de otro:** si alguien se atrasa, programen juntos y que suba él.

---

## Errores comunes que debes evitar

- Insertar el catálogo ordenado y darlo por bueno.
- Guardar en el árbol cada registro diario: construirlo tardaría minutos y quedaría muy profundo.
- Comparar FIPS como números, o mezclar `1001` con `01001`.
- Imprimir dentro de los métodos del árbol.
- Hacer `eliminar` solo para hojas.
- Usar `TreeMap` o `TreeSet`.
- Subir la clave de Gemini. Si pasa, hay que anularla y crear otra: borrar el commit no basta.
- Enviar a la IA los registros: solo el resumen.
- Depender de internet en la defensa: lleva los reportes ya generados.

## Cómo explicarlo en la defensa

- **¿Cuál es la regla del árbol?** Menores a la izquierda, mayores a la derecha, en todos los nodos.
- **¿Por qué el inOrden sale ordenado?** Porque visita primero todo lo menor, luego el nodo, luego todo lo mayor.
- **¿Qué pasa si los datos llegan ordenados?** El árbol queda como una lista. Lo resolvimos insertando por medios.
- **¿Por qué un nodo por condado y no por registro?** Son 3.200 nodos en vez de 1,18 millones, y el archivo llega casi ordenado, así que el árbol de registros quedaría muy profundo.
- **¿Qué pasa si no hay internet?** El ETL termina igual, escribe su reporte y deja el prompt guardado.
- **Te pueden pedir:** insertar una secuencia a mano y dar el inOrden, eliminar un nodo con dos hijos en el pizarrón, o agregar en vivo un método (contar hojas, obtener el mayor, recorrer por niveles usando la `Cola` de Joel).

## Lista final: tu parte está completa cuando

- [ ] El árbol tiene sus 8 operaciones y `PruebaArbolBST` pasa completa.
- [ ] La tabla del experimento tiene tus números medidos.
- [ ] El árbol de condados se construye, se llena con los casos y se reconstruye balanceado.
- [ ] La búsqueda comparada funciona y sus números están en el README.
- [ ] `AnalizadorIA` funciona en manual y en automático, y nunca tumba el programa.
- [ ] Existen `bst.md` y la explicación del árbol, revisados por ti.

---

## Cómo trabajar y subir

**Antes de empezar** (y cada vez que se fusione algo a `main`), pon tu rama al día. Hoy tu rama está atrasada respecto a `main`:

```bash
git fetch origin
git checkout Damian_Cunalata
git merge origin/main
```

**Compilar y correr una prueba** (ejemplo con `PruebaArbolBST`; se ejecuta desde la carpeta principal del proyecto):

```bash
javac -encoding UTF-8 -d bin -sourcepath "src;test" test/PruebaArbolBST.java
java -cp bin PruebaArbolBST
```

**Subir un paso** (ejemplo con el primero):

```bash
git add src/estructuras/NodoArbol.java src/estructuras/ArbolBST.java test/PruebaArbolBST.java
git commit -m "ArbolBST: insertar, buscar y contarNodos con pruebas"
git push origin Damian_Cunalata
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
