# Joel — Cola, lectura de datos y BFS

**Tu rama:** `Rama-Joel`

Este archivo tiene todo lo que haces en el proyecto, de principio a fin. Léelo completo una vez y después ve parte por parte.

## Qué te toca, en simple

Tú construyes la **puerta de entrada** del programa y el **recorrido por niveles** del grafo.

- Los datos sucios se leen del archivo y entran, uno por uno y en orden, a una **cola** que tú programas. Luis saca los registros de tu cola para limpiarlos.
- Más adelante, esa misma cola te sirve para recorrer el mapa de condados vecinos: desde un condado, visitar primero sus vecinos, luego los vecinos de sus vecinos, y así. Eso es el **BFS**, y con él se encuentra la ruta más corta entre dos condados.

Una **cola** funciona como la fila de un banco: el primero que llega es el primero que sale (en inglés, FIFO). Se agrega por el final y se saca por el frente.

## Tu camino completo

| Parte | Qué creas | Cuándo | Qué necesitas de otros |
| --- | --- | --- | --- |
| 1 | `Cola` | Sábado | Nada |
| 2 | `PruebaCola` | Sábado | Nada |
| 3 | `RegistroCrudo` | Domingo | Nada |
| 4 | Lectura que encola, en `Extract` | Domingo | Datos de prueba de Sara |
| 5 | Lectura de la vecindad | Después | Nada |
| 6 | `BFS`: recorrido | Después | `Grafo` de Luis |
| 7 | Ruta más corta y grupos separados | Después | `Pila` de Kenneth |
| 8 | Avisos a la bitácora y flujogramas | Después | `Bitacora` de Josue |

## Con quién se conecta tu trabajo

- **Luis** usa tu `Cola`. Su `Transform` hace esto: mientras tu cola no esté vacía (`estaVacia()`), saca un registro (`desencolar()`) y lo revisa. Si tu cola falla, él no puede avanzar.
- **Sara** te da los archivos pequeños de `data/pruebas/` para probar, y llama a tus métodos de lectura desde el menú. Tú no tocas el menú.
- **Kenneth** te presta su `Pila` para mostrar una ruta en el orden correcto (parte 7).
- **Luis** te da el `Grafo` que recorres con el BFS.
- **Josue** te da la `Bitacora`, donde tu cola y tu BFS anotan sus pasos para dibujar los flujogramas.

## Antes de empezar

- Ten el repo clonado y tu rama al día con `main` (los comandos están al final de este archivo).
- Abre `src/estructuras/Nodo.java` y `src/estructuras/ListaEnlazada.java`: tu cola usa el mismo `Nodo` y se parece mucho a la lista.
- Abre `test/PruebaListaEnlazada.java`: tus pruebas siguen ese mismo estilo.
- Abre `src/etl/Extract.java` y mira cómo `cargarCasos` lee el archivo: tu método nuevo lee igual, pero en vez de validar, encola.

---

## Parte 1 — La cola (`src/estructuras/Cola.java`)

**Qué es:** una clase genérica `Cola<T>`, es decir, sirve para guardar cualquier tipo de dato.

**Qué lleva dentro:**

- una referencia `frente` al primer nodo (el próximo en salir);
- una referencia `fin` al último nodo (el último que entró);
- un contador con la cantidad de elementos.

**Los seis métodos** (los nombres son los que pide el enunciado; no los cambies):

| Método | Qué hace | Cómo pensarlo |
| --- | --- | --- |
| `void encolar(T dato)` | Agrega al final | Crea un nodo. Si la cola está vacía, ese nodo es a la vez `frente` y `fin`. Si no, se engancha después de `fin` y pasa a ser el nuevo `fin`. Suma 1 al contador |
| `T desencolar()` | Quita y devuelve el primero | Si está vacía, lanza una excepción. Guarda el dato de `frente`, avanza `frente` al siguiente nodo, resta 1 al contador. **Si `frente` quedó en `null`, pon también `fin` en `null`** |
| `T frente()` | Devuelve el primero sin quitarlo | Si está vacía, lanza una excepción |
| `boolean estaVacia()` | Dice si no hay elementos | Basta mirar si `frente` es `null` |
| `int tamanio()` | Cuántos elementos hay | Devuelve el contador; no recorre |
| `void mostrar(int n)` | Imprime los primeros `n` desde el frente | Recorre desde `frente` y se detiene al llegar a `n` o al final |

**Requisito de velocidad:** encolar y desencolar no recorren nada. Por eso existen las dos referencias. Van a pasar por tu cola 1.185.373 registros.

**Excepción a usar:** `IllegalStateException` con un mensaje claro, por ejemplo "La cola está vacía".

**Terminado cuando:** compila y pasa todas las pruebas de la parte 2.

## Parte 2 — Las pruebas de la cola (`test/PruebaCola.java`)

**Qué es:** una clase con `main` que usa tu cola y dice si cada caso salió bien o mal, igual que `PruebaListaEnlazada`.

**Casos que debe tener:**

1. Cola recién creada: `estaVacia()` es verdadero y `tamanio()` es 0.
2. Encolar un elemento: ya no está vacía, tamaño 1, y `frente()` devuelve ese elemento.
3. Encolar 5 y desencolar 5: salen en el mismo orden en que entraron.
4. Un solo elemento: lo encolas, lo desencolas, y la cola queda vacía. Luego encolas otro y funciona (este caso detecta el error de `fin`).
5. `desencolar()` sobre cola vacía lanza la excepción.
6. `frente()` sobre cola vacía lanza la excepción.
7. `frente()` no cambia el tamaño.
8. Un millón de `encolar` seguidos de un millón de `desencolar` terminan en un segundo o dos. Si tarda mucho, alguna operación está recorriendo.

Al final imprime cuántas pruebas pasaron y cuántas fallaron.

**Pasos y commits del sábado:**

1. `Cola` con `encolar`, `estaVacia`, `tamanio`, y `PruebaCola` con los casos 1 y 2.
   Commit: `Cola: encolar, estaVacia y tamanio con pruebas`
2. `desencolar` y `frente`, con los casos 3 a 8.
   Commit: `Cola: desencolar y frente con casos limite`
3. `mostrar(int n)`.
   Commit: `Cola: mostrar`

**Al terminar el sábado:** abre tu Pull Request a `main` y avisa en el grupo. Luis necesita tu cola el domingo.

---

## Parte 3 — El registro crudo (`src/model/RegistroCrudo.java`)

**Qué es:** una clase pequeña que representa **una fila del archivo tal como vino**, sin revisar nada.

**Por qué hace falta:** hoy `Extract` convierte los textos a números al leer, y si encuentra un número inválido lo cambia por 0 sin avisar. Así el error se pierde. Con `RegistroCrudo`, la fila viaja intacta hasta Luis, que es quien decide si está bien o mal.

**Qué lleva dentro:**

- `numeroLinea` (un `int`): la línea del archivo, para que el error diga dónde está;
- `campos` (un `String[]`): los textos de la fila ya separados.

Solo atributos `final`, un constructor, los dos getters y un `mostrar()` que devuelva la fila como texto.

## Parte 4 — La lectura que encola (`src/etl/Extract.java`)

**Qué creas:** dos métodos nuevos dentro de `Extract`. **No borres ni cambies `cargarCasos` ni `cargarCondados`**: el menú actual los sigue usando hasta que Sara lo cambie.

- `void encolarCasos(String ruta, Cola<RegistroCrudo> cola)`
- `void encolarCondados(String ruta, Cola<RegistroCrudo> cola)`

**Qué hace cada uno, paso a paso:**

1. Abre el archivo con `BufferedReader`, igual que `cargarCasos`.
2. Lee la primera línea (la cabecera) y comprueba que sea la esperada, como ya se hace con `Esquema.cabeceraValida`. Si no coincide, no encola nada.
3. Por cada línea siguiente: aumenta el número de línea, separa los campos con `separarCsv` (ya existe) y encola un `RegistroCrudo` con ese número y esos campos.
4. Cuenta cuántas filas leyó y guárdalo en un contador con su getter, para que Sara lo use en el resumen.

**Qué NO hace:** no revisa si falta el FIPS, no convierte números, no descarta filas con columnas de menos. Todo eso es de Luis.

**Cómo probarlo:** con `data/pruebas/casos_prueba.csv` de Sara, el tamaño de la cola después de leer debe ser igual al número de filas del archivo (sin contar la cabecera), y el primer elemento debe tener el número de línea 2.

**Pasos y commits del domingo** (primero pon tu rama al día con `main`):

4. `RegistroCrudo`.
   Commit: `RegistroCrudo: fila sin validar con su numero de linea`
5. `encolarCasos` y `encolarCondados`, probados con los datos de Sara.
   Commit: `Extract: encolar registros crudos de casos y condados`

**Al terminar:** abre el PR temprano. Es lo primero que se necesita el domingo para conectar el ETL.

---

## Parte 5 — Lectura de la vecindad

`void encolarVecindad(String ruta, Cola<RegistroCrudo> cola)`: igual que los otros dos, pero el archivo `vecindad.tsv` se separa con tabulaciones (usa `separarTsv`, que ya existe).

Hoy `Extract` revisa pares repetidos comparando cada fila con todas las anteriores, lo cual es muy lento. Tu método nuevo no revisa nada: solo encola. Luis detecta los repetidos al construir el grafo.

Commit: `Extract: encolar vecindad`

## Parte 6 — El recorrido BFS (`src/algoritmos/BFS.java`)

**Qué es:** recorrer el grafo de condados **por niveles**, usando tu `Cola`.

**Lo que te da Luis:** un `Grafo` donde cada `Vertice` es un condado y tiene su lista de vecinos, y tres campos que tú usas: `visitado`, `anterior` (desde qué condado se llegó) y `distancia` (cuántos saltos hay desde el origen).

**Qué creas:** un método que recibe el grafo y el condado de origen, y devuelve una `ListaEnlazada` con los condados en el orden en que se visitaron.

**El algoritmo, paso a paso:**

1. Pide al grafo que reinicie las marcas (`reiniciarRecorrido()`).
2. Marca el origen como visitado, con distancia 0, y encólalo.
3. Mientras la cola no esté vacía:
   - desencola un vértice y agrégalo al resultado;
   - por cada vecino que **no** esté visitado: márcalo como visitado, anota su `anterior` (el vértice que acabas de sacar) y su `distancia` (la del actual más 1), y encólalo.

**El detalle que más falla:** se marca como visitado **al encolar**, no al desencolar. Si lo haces al desencolar, un mismo condado entra varias veces a la cola.

**Debe poder mostrar la cola en cada paso:** en la defensa piden ver cómo cambia la cola. Deja una forma de activar ese detalle (se resuelve con la bitácora, parte 8).

**Cómo probarlo:** con el grafo pequeño de `data/pruebas/` dibujado en papel. El BFS desde un condado debe visitar exactamente los de su grupo, en el orden que tú calculaste a mano.

Commit: `BFS: recorrido por niveles con la cola propia`

## Parte 7 — Ruta más corta y grupos separados

Tres métodos más en `BFS`, que resuelven tres de los problemas que pide el enunciado:

**a) ¿Existe ruta entre dos condados?** Haz el BFS desde el primero y mira si el segundo quedó visitado. Entre un condado de Texas y uno de Hawái la respuesta es no.

**b) Mostrar la ruta más corta.** El BFS garantiza la ruta con menos condados intermedios. Para armarla:

1. Haz el BFS desde el origen.
2. Empieza en el destino y retrocede siguiendo `anterior` hasta llegar al origen.
3. Ese camino sale al revés. Ve **apilando** cada condado en la `Pila` de Kenneth y luego desapila: sale en el orden correcto.

**c) Condados a `k` saltos o menos.** Es el mismo BFS, pero solo agregas al resultado los vértices cuya `distancia` sea menor o igual a `k`.

**d) Contar los grupos separados (componentes).** Recorre todos los vértices; cada vez que encuentres uno sin visitar, lanza un BFS desde él **sin reiniciar las marcas** y suma 1. Con la base real deben salir **17**.

**Cómo probarlo:** en el grafo pequeño, una ruta dentro del mismo grupo tiene el largo mínimo; entre grupos distintos, "no existe ruta"; la ruta de un condado a sí mismo es solo él; los grupos son los del dibujo.

Commits: `BFS: existe ruta y ruta mas corta` · `BFS: condados a k saltos y componentes`

## Parte 8 — Avisos a la bitácora y tus flujogramas

Te tocan **dos de los siete flujogramas**: el de la **cola** y el del **BFS**.

1. Josue crea una interfaz pequeña (un "oyente") con un método para avisar un paso. Tu `Cola` y tu `BFS` reciben ese oyente de forma opcional: si existe, avisan; si no, no hacen nada. Así tu cola sigue funcionando sola.
2. Avisos de la cola: `ENCOLAR` y `DESENCOLAR`, con el dato y el tamaño resultante.
3. Avisos del BFS: `VISITAR`, `ENCOLAR_VECINO`, `VECINO_YA_VISITADO`, con el condado.
4. Con la bitácora encendida, ejecuta una corrida pequeña (5 elementos en la cola; un BFS en el grafo de prueba) y genera `output/diagramas/cola.md` y `output/diagramas/bfs.md` con las clases de Josue.
5. Comprueba en GitHub que los dos diagramas **se dibujan**, y que cada paso del dibujo ocurrió de verdad.

Commits: `Cola: avisos a la bitacora` · `BFS: avisos a la bitacora y flujograma`

---

## Errores comunes que debes evitar

- **Al desencolar el último elemento**, `fin` también debe quedar en `null`. Es el error más frecuente.
- No hagas la cola con un arreglo moviendo todos los elementos al sacar: con más de un millón de filas no termina.
- No devuelvas `null` cuando la cola está vacía: lanza la excepción.
- `mostrar` siempre con límite.
- No imprimas dentro de `encolar` ni `desencolar`.
- No uses `java.util.Queue`, `LinkedList`, `ArrayDeque` ni `HashSet`.
- En el BFS, no olvides reiniciar las marcas antes de cada recorrido: el segundo BFS daría un resultado vacío.
- No asumas que el grafo está todo conectado: hay 17 grupos.
- No imprimas los 3.109 condados de un recorrido: muestra la cantidad y los primeros.

## Cómo explicarlo en la defensa

- **¿Por qué una cola para leer y no una pila?** Porque respeta el orden de llegada: el registro de la línea 2 se procesa antes que el de la línea 3, y el número de línea del error coincide con el archivo.
- **¿Qué pasa con `frente` y `fin` al sacar el último?** Los dos quedan en `null`; si `fin` siguiera apuntando al nodo viejo, el siguiente `encolar` se engancharía a un nodo que ya no está en la cola.
- **¿Por qué encolar y desencolar son inmediatos?** Porque tienes una referencia a cada extremo y no recorres nada.
- **¿Por qué el BFS da la ruta más corta?** Porque visita por niveles: primero todo lo que está a 1 salto, luego a 2, y así. La primera vez que llega a un condado es por el camino con menos saltos.
- **Te pueden pedir:** ejecutar un BFS a mano mostrando la cola en cada paso, o agregar en vivo un método a la cola (por ejemplo, contar cuántas veces aparece un dato).

## Lista final: tu parte está completa cuando

- [ ] `Cola` tiene sus seis métodos y `PruebaCola` pasa completa.
- [ ] `Extract` encola casos, condados y vecindad sin validar.
- [ ] El BFS recorre, encuentra la ruta más corta, dice si existe ruta y cuenta 17 grupos en la base real.
- [ ] Existen y se dibujan `cola.md` y `bfs.md`.
- [ ] Puedes explicar cada método sin mirar el código.

---

## Cómo trabajar y subir

**Antes de empezar** (y cada vez que se fusione algo a `main`), pon tu rama al día. Hoy tu rama está atrasada respecto a `main`:

```bash
git fetch origin
git checkout Rama-Joel
git merge origin/main
```

**Compilar y correr una prueba** (ejemplo con `PruebaCola`; se ejecuta desde la carpeta principal del proyecto):

```bash
javac -encoding UTF-8 -d bin -sourcepath "src;test" test/PruebaCola.java
java -cp bin PruebaCola
```

**Subir un paso** (ejemplo con el primero):

```bash
git add src/estructuras/Cola.java test/PruebaCola.java
git commit -m "Cola: encolar, estaVacia y tamanio con pruebas"
git push origin Rama-Joel
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
