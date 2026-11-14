# Kenneth — Pila, listas, ordenamiento y DFS

**Tu rama:** `Kenneth_Chalco`

Este archivo tiene todo lo que haces en el proyecto, de principio a fin. Léelo completo una vez y después ve parte por parte.

## Qué te toca, en simple

Tú construyes cuatro cosas:

- La **pila**, donde el programa guarda los registros rechazados con su motivo.
- Dos mejoras a las **listas** que hiciste en el primer parcial.
- Los **algoritmos de ordenamiento**, para sacar por ejemplo el ranking de condados con más casos en un día.
- El **DFS**, que recorre el mapa de condados "hasta el fondo" usando tu pila, y con el que se detectan ciclos.

Una **pila** funciona como una pila de platos: el último que pones es el primero que sacas (en inglés, LIFO). Se agrega y se saca por el mismo extremo, la cima.

## Tu camino completo

| Parte | Qué creas | Cuándo | Qué necesitas de otros |
| --- | --- | --- | --- |
| 1 | `Pila` | Sábado | Nada |
| 2 | `PruebaPila` | Sábado | Nada |
| 3 | `ultimo()` en `ListaEnlazada` | Sábado | Nada |
| 4 | Leer y escribir por posición en `ListaSecuencial` | Domingo | Nada |
| 5 | `Ordenamiento`: primer algoritmo | Domingo | Nada |
| 6 | Segundo algoritmo y tabla comparativa | Después | Lista de válidos del ETL |
| 7 | `DFS`: recorrido | Después | `Grafo` de Luis |
| 8 | Detección de ciclos | Después | `Grafo` de Luis |
| 9 | Avisos a la bitácora y flujogramas | Después | `Bitacora` de Josue |

## Con quién se conecta tu trabajo

- **Luis** usa tu `Pila`: cada registro que no tiene arreglo lo guarda con `apilar()`.
- **Sara** recorre tu pila, sin vaciarla, para escribir el archivo de errores. Por eso tu pila debe poder recorrerse.
- **Damian** y **Luis** necesitan `ultimo()` de `ListaEnlazada`: lo usan para mirar el último registro guardado de cada condado.
- **Joel** usa tu `Pila` para mostrar una ruta en el orden correcto.
- **Sara** conecta tu ordenamiento y tu DFS al menú. Tú no tocas el menú.
- **Luis** te da el `Grafo` que recorres con el DFS.
- **Josue** te da la `Bitacora` para los flujogramas.

## Antes de empezar

- Ten el repo clonado y tu rama al día con `main` (los comandos están al final).
- Abre `src/estructuras/Nodo.java`, `ListaEnlazada.java` y `ListaSecuencial.java`: la pila usa el mismo `Nodo`, y vas a agregar métodos a las dos listas.
- Abre `test/PruebaListaEnlazada.java`: tus pruebas siguen ese estilo.

---

## Parte 1 — La pila (`src/estructuras/Pila.java`)

**Qué es:** una clase genérica `Pila<T>`.

**Qué lleva dentro:**

- una referencia `cima` al nodo de arriba;
- un contador con la cantidad de elementos.

**Los métodos:**

| Método | Qué hace | Cómo pensarlo |
| --- | --- | --- |
| `void apilar(T dato)` | Agrega arriba | Crea un nodo cuyo siguiente es la `cima` actual, y ese nodo pasa a ser la nueva `cima`. Suma 1 |
| `T desapilar()` | Quita y devuelve el de arriba | Si está vacía, lanza una excepción. Guarda el dato de `cima`, mueve `cima` al siguiente, resta 1 |
| `T cima()` | Devuelve el de arriba sin quitarlo | Si está vacía, lanza una excepción |
| `boolean estaVacia()` | Dice si no hay elementos | Mira si `cima` es `null` |
| `int tamanio()` | Cuántos elementos hay | Devuelve el contador |
| `void mostrar(int n)` | Imprime los `n` de más arriba | Recorre desde `cima` hasta `n` o el final |

**Además, debe poder recorrerse sin vaciarla.** Haz que implemente `Iterable<T>`, igual que tu `ListaEnlazada`, recorriendo desde la cima hacia abajo. Sara lo necesita para escribir todos los errores en un archivo sin destruir la pila.

**Requisito de velocidad:** apilar y desapilar no recorren nada.

**Excepción a usar:** `IllegalStateException` con un mensaje claro, por ejemplo "La pila está vacía".

**Para qué sirve la pila en este proyecto (tienes que saber decirlo):** el último error registrado es el primero que se revisa, así que al mostrar la pila se ven primero los errores del final del archivo. Además se reutiliza en el DFS y para invertir una ruta.

## Parte 2 — Las pruebas de la pila (`test/PruebaPila.java`)

**Casos que debe tener:**

1. Pila recién creada: `estaVacia()` verdadero y `tamanio()` 0.
2. Apilar uno: tamaño 1 y `cima()` devuelve ese elemento.
3. Apilar 5 y desapilar 5: salen en orden inverso.
4. Un solo elemento: apilas, desapilas, queda vacía; apilas otro y funciona.
5. `desapilar()` sobre pila vacía lanza la excepción.
6. `cima()` sobre pila vacía lanza la excepción.
7. `cima()` no cambia el tamaño.
8. Recorrerla con el iterador visita todos los elementos desde la cima y **no cambia el tamaño**.
9. Un millón de `apilar` y un millón de `desapilar` terminan en uno o dos segundos.

Al final imprime cuántas pasaron y cuántas fallaron.

## Parte 3 — `ultimo()` en `ListaEnlazada`

**Qué creas:** un método `T ultimo()` en `src/estructuras/ListaEnlazada.java`.

**Qué hace:** devuelve el dato del último nodo. Tu lista ya guarda una referencia al final (por eso inserta rápido), así que **no hay que recorrer nada**. Si la lista está vacía, devuelve `null`.

**Para qué lo usan los demás:** cada condado tendrá una lista con su historial de casos. Para saber si un registro nuevo está repetido, basta compararlo con el último de esa lista.

**Prueba:** agrega tres casos a `PruebaListaEnlazada`: lista vacía devuelve `null`; con un elemento devuelve ese; con varios devuelve el que se insertó al final.

**Pasos y commits del sábado:**

1. `Pila` con `apilar`, `estaVacia`, `tamanio`, y `PruebaPila` con los casos 1 y 2.
   Commit: `Pila: apilar, estaVacia y tamanio con pruebas`
2. `desapilar` y `cima`, con los casos 3 a 7.
   Commit: `Pila: desapilar y cima con casos limite`
3. `mostrar(int n)` y el recorrido con iterador, con los casos 8 y 9.
   Commit: `Pila: mostrar y recorrido`
4. `ultimo()` con sus pruebas.
   Commit: `ListaEnlazada: ultimo() con prueba`

**Al terminar el sábado:** abre tu Pull Request a `main` y avisa en el grupo. Luis necesita la pila el domingo y Damian necesita `ultimo()`.

---

## Parte 4 — Leer y escribir por posición en `ListaSecuencial`

**Por qué:** para ordenar hay que intercambiar elementos, y eso exige llegar rápido a cualquier posición. Tu `ListaSecuencial` guarda los datos en un arreglo, así que es la estructura correcta para ordenar. La `ListaEnlazada` no sirve aquí: llegar a la posición 2.000 obliga a pasar por las 1.999 anteriores.

**Qué creas** en `src/estructuras/ListaSecuencial.java`:

- `T obtener(int i)`: devuelve el elemento de la posición `i` (si ya existe con otro nombre, úsalo y no lo dupliques).
- `void establecer(int i, T dato)`: reemplaza el elemento de la posición `i`.

Los dos deben lanzar `IndexOutOfBoundsException` si `i` es negativo o mayor o igual al tamaño.

**Prueba:** escribir en una posición y leerla; las demás no cambian; el tamaño no cambia; una posición inválida lanza la excepción.

## Parte 5 — Ordenamiento (`src/algoritmos/Ordenamiento.java`)

**Qué pide el enunciado:** al menos **dos** algoritmos hechos por el grupo, y una comparación entre ellos.

**Qué se ordena:** no el archivo completo. Con 1.185.373 registros, un algoritmo simple haría más de un billón de operaciones y no termina. Se ordenan **los registros de un solo día** (unos 3.250), para sacar el ranking de condados con más casos en esa fecha.

**Primero, cómo comparar.** Crea una interfaz propia y pequeña, por ejemplo `Comparador<T>`, con un solo método `int comparar(T a, T b)` que devuelve negativo, cero o positivo. El algoritmo recibe un comparador y no sabe qué está ordenando. Así el mismo algoritmo sirve para ordenar por casos, por muertes o por nombre: solo cambias el comparador.

**Los dos algoritmos recomendados** (son los más fáciles de explicar a mano):

- **Inserción:** recorre de izquierda a derecha; toma cada elemento y lo va corriendo hacia la izquierda hasta dejarlo en su lugar entre los que ya están ordenados.
- **Selección:** busca el menor de todos y lo pone en la primera posición; luego el menor del resto en la segunda; y así.

**Cada algoritmo debe contar** cuántas **comparaciones** y cuántos **intercambios** hizo, y ofrecer getters para esos dos números. Es lo que se muestra en la tabla comparativa.

**Cada uno recibe** una `ListaSecuencial<T>` y un comparador, y la deja ordenada.

**Pruebas (`test/PruebaOrdenamiento.java`):** lista vacía, un elemento, dos elementos, ya ordenada, ordenada al revés, con valores repetidos. Después de ordenar, recorre y verifica automáticamente que cada elemento es menor o igual que el siguiente y que el tamaño no cambió.

**Pasos y commits del domingo** (primero pon tu rama al día con `main`):

5. `obtener` y `establecer` con su prueba.
   Commit: `ListaSecuencial: leer y escribir por posicion`
6. La interfaz de comparación y el primer algoritmo, con conteo y pruebas.
   Commit: `Ordenamiento: insercion con conteo de comparaciones`

El domingo no dependes de nadie.

---

## Parte 6 — Segundo algoritmo y tabla comparativa

1. El segundo algoritmo, con las mismas pruebas.
   Commit: `Ordenamiento: seleccion con conteo`
2. **El ranking de un día.** Un método que recibe la lista de casos válidos y una fecha, copia los registros de esa fecha a una `ListaSecuencial` **nueva** y la ordena por casos de mayor a menor. Sara lo conecta a una opción del menú que muestra los 10 primeros.
   Commit: `Ordenamiento: ranking de condados por fecha`
3. **La tabla comparativa**, que va al informe y a la demostración. Mide los dos algoritmos con cantidades crecientes:

   | Cantidad | Algoritmo | Comparaciones | Intercambios | Tiempo aproximado |
   | --- | --- | --- | --- | --- |
   | 100 | Inserción / Selección | | | |
   | 1.000 | | | | |
   | 3.250 (un día) | | | | |
   | 10.000 | | | | |

   Repite con datos **ya ordenados** y **ordenados al revés**. Vas a ver dos cosas que debes saber explicar: al multiplicar la cantidad por 10, el tiempo se multiplica por unas 100; y con datos ya ordenados, inserción casi no trabaja mientras que selección trabaja igual.

   Mide el tiempo con `System.nanoTime()`. Escribe "aproximado": una sola corrida no es exacta.
   Commit: `Ordenamiento: tabla comparativa`

## Parte 7 — El recorrido DFS (`src/algoritmos/DFS.java`)

**Qué es:** recorrer el grafo de condados yendo **lo más lejos posible** por un camino antes de volver, usando tu `Pila`.

**Lo que te da Luis:** un `Grafo` donde cada `Vertice` es un condado con su lista de vecinos y un campo `visitado`.

**Qué creas:** un método que recibe el grafo y el condado de origen, y devuelve una `ListaEnlazada` con los condados en el orden en que se visitaron.

**El algoritmo, paso a paso:**

1. Pide al grafo que reinicie las marcas (`reiniciarRecorrido()`).
2. Apila el origen.
3. Mientras la pila no esté vacía:
   - desapila un vértice;
   - si **ya** está visitado, sáltalo;
   - si no, márcalo como visitado, agrégalo al resultado y apila todos sus vecinos no visitados.

**Hazlo con tu pila, no con recursión.** La rúbrica pide reutilizar las estructuras propias, y el grupo más grande tiene 3.109 condados.

**El orden de visita depende del orden en que apilas los vecinos.** No hay un único resultado correcto; tienes que saber explicar el tuyo.

**Debe poder mostrar la pila en cada paso** (se resuelve con la bitácora, parte 9).

**Cómo probarlo:** con el grafo pequeño de `data/pruebas/` dibujado en papel: el DFS desde un condado visita exactamente los de su grupo, en el orden que calculaste a mano.

Commit: `DFS: recorrido en profundidad con la pila propia`

## Parte 8 — Detección de ciclos

**Qué es un ciclo en este grafo.** El grafo de condados es de ida y vuelta: si A es vecino de B, B es vecino de A. Ir de A a B y regresar por el mismo borde **no es un ciclo**. Hay ciclo cuando, durante el recorrido, encuentras un vecino **ya visitado que no es el condado desde el que llegaste**. Ejemplo: tres condados que colindan entre sí.

**Qué creas:** un método `tieneCiclo` que recibe el grafo y devuelve verdadero o falso, y otro que devuelve un ejemplo concreto (los condados que forman un ciclo). Para saber "desde dónde llegaste", usa el campo `anterior` del vértice.

**También el caso con dirección.** La clase `Grafo` de Luis puede ser dirigida (con flechas de un solo sentido). El enunciado pide detectar el ciclo A → B → C → A. Pruébalo con un grafo pequeño dirigido: con esas tres flechas hay ciclo; quitando la flecha C → A, no lo hay.

**Qué se muestra en la demostración:**

1. Que el grafo real tiene al menos un ciclo, con un ejemplo.
2. Que el archivo original traía 3.234 condados "vecinos de sí mismos", que Luis eliminó al limpiar.
3. El caso dirigido de prueba.

Commit: `DFS: deteccion de ciclos`

## Parte 9 — Avisos a la bitácora y tus flujogramas

Te tocan **tres de los siete flujogramas**: el de la **pila**, el de la **lista enlazada** y el del **DFS**.

1. Josue crea una interfaz pequeña (un "oyente") con un método para avisar un paso. Tus clases lo reciben de forma opcional: si existe, avisan; si no, no hacen nada.
2. Avisos de la pila: `APILAR` y `DESAPILAR`, con el dato y el tamaño.
3. Avisos de la lista: `INSERTAR`, `COMPARAR`, `ENLAZAR`, en una inserción o una búsqueda.
4. Avisos del DFS: `DESAPILAR`, `VISITAR`, `APILAR_VECINO`, `YA_VISITADO`.
5. Con la bitácora encendida y una corrida pequeña, genera `output/diagramas/pila.md`, `lista.md` y `dfs.md` con las clases de Josue.
6. Comprueba en GitHub que los tres **se dibujan** y que cada paso ocurrió de verdad.

Commits: `Pila y ListaEnlazada: avisos a la bitacora` · `DFS: avisos a la bitacora y flujogramas`

---

## Errores comunes que debes evitar

- No hagas la pila insertando al final de una lista: desapilar sería lento.
- No devuelvas `null` cuando la pila está vacía: lanza la excepción.
- No imprimas dentro de `apilar` ni `desapilar`.
- No ordenes el archivo completo "a ver si termina".
- Ordena sobre `ListaSecuencial`, nunca sobre `ListaEnlazada`.
- Ordena una **copia**. Si reordenas la lista principal de casos, se rompen las estadísticas que suponen orden por fecha.
- No saques conclusiones de tiempo con 100 elementos: es ruido.
- No uses `java.util.Stack`, `Arrays.sort`, `Collections.sort`, `Comparator`, `HashSet` ni streams.
- En el DFS, no olvides reiniciar las marcas antes de cada recorrido.
- No cuentes como ciclo el ir y volver por el mismo borde.

## Cómo explicarlo en la defensa

- **¿Para qué una pila en los errores?** El último error es el primero que se revisa; y la misma pila se reutiliza en el DFS y para invertir rutas.
- **¿Por qué no se ordena el millón de registros?** Porque estos algoritmos hacen un número de operaciones parecido a la cantidad al cuadrado: con un millón, más de un billón.
- **¿Por qué se ordena sobre el arreglo y no sobre la lista enlazada?** Porque ordenar necesita llegar a cualquier posición de inmediato, y la lista enlazada tiene que recorrer.
- **¿En qué se diferencian inserción y selección?** Inserción aprovecha lo que ya está ordenado; selección hace siempre el mismo trabajo, pero menos intercambios.
- **¿Por qué el BFS da la ruta más corta y el DFS no?** El DFS se va por un camino hasta el fondo, y ese camino puede ser largo.
- **Te pueden pedir:** ordenar 6 números a mano, mostrar apilar y desapilar con un error real, ejecutar un DFS mostrando la pila, o agregar un método en vivo.

## Lista final: tu parte está completa cuando

- [ ] `Pila` tiene sus métodos, se puede recorrer y `PruebaPila` pasa completa.
- [ ] `ultimo()`, `obtener` y `establecer` existen con sus pruebas.
- [ ] Hay dos algoritmos de ordenamiento con conteo, el ranking de un día y la tabla comparativa con tus números.
- [ ] El DFS recorre y detecta ciclos, también en el caso dirigido.
- [ ] Existen y se dibujan `pila.md`, `lista.md` y `dfs.md`.
- [ ] Puedes explicar cada método sin mirar el código.

---

## Cómo trabajar y subir

**Antes de empezar** (y cada vez que se fusione algo a `main`), pon tu rama al día. Hoy tu rama está atrasada respecto a `main`:

```bash
git fetch origin
git checkout Kenneth_Chalco
git merge origin/main
```

**Compilar y correr una prueba** (ejemplo con `PruebaPila`; se ejecuta desde la carpeta principal del proyecto):

```bash
javac -encoding UTF-8 -d bin -sourcepath "src;test" test/PruebaPila.java
java -cp bin PruebaPila
```

**Subir un paso** (ejemplo con el primero):

```bash
git add src/estructuras/Pila.java test/PruebaPila.java
git commit -m "Pila: apilar, estaVacia y tamanio con pruebas"
git push origin Kenneth_Chalco
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
