# Evidencia de pruebas de Kenneth

Pruebas ejecutadas el 2026-11-14 con OpenJDK Temurin 21.0.12.1 LTS.

| Suite | Resultado |
| --- | ---: |
| `PruebaPila` | 9 correctas, 0 fallidas |
| `PruebaListaEnlazada` | 12 correctas, 0 fallidas |
| `PruebaListaSecuencial` | 3 correctas, 0 fallidas |
| `PruebaOrdenamiento` | 62 verificaciones correctas |

## Casos comprobados

### Pila

- Estado inicial vacío.
- Apilar y consultar la cima.
- Orden LIFO con cinco elementos.
- Reutilización después de vaciarla.
- Excepciones al desapilar o consultar una pila vacía.
- Consulta de la cima sin eliminar datos.
- Iteración sin vaciar la pila.
- Un millón de operaciones de apilar y desapilar: 0.147 segundos en esta ejecución.

### Lista enlazada

- Inserción, obtención y eliminación en distintas posiciones.
- Tamaño correcto después de las operaciones.
- `ultimo()` con lista vacía, con un elemento y después de inserciones y eliminaciones.

### Lista secuencial

- `establecer()` reemplaza únicamente la posición indicada.
- El tamaño no cambia al reemplazar.
- Los índices inválidos producen `IndexOutOfBoundsException`.

### Ordenamiento

- Inserción y selección con listas vacías y de uno o dos elementos.
- Entradas ordenadas, inversas y con repetidos.
- Conservación del tamaño y verificación automática del orden final.

## Reproducción

```powershell
javac -encoding UTF-8 -d bin -sourcepath "src;test" test/PruebaPila.java test/PruebaListaEnlazada.java test/PruebaListaSecuencial.java test/PruebaOrdenamiento.java
java -cp bin PruebaPila
java -cp bin PruebaListaEnlazada
java -cp bin PruebaListaSecuencial
java -cp bin PruebaOrdenamiento
```
