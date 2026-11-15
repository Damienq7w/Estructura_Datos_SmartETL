# Preparación de DFS, ciclos y defensa

Este documento deja preparados los casos que se convertirán en pruebas automáticas cuando estén disponibles `Grafo` y `Vertice`. No sustituye la implementación pendiente.

## Casos previstos para DFS

| Caso | Grafo | Inicio | Resultado esperado |
| --- | --- | --- | --- |
| Grafo vacío | Sin vértices | Cualquiera | Resultado vacío o error claro, según el contrato de `Grafo` |
| Inicio inexistente | `A—B` | `X` | Resultado vacío o error claro, según el contrato de `Grafo` |
| Un vértice | `A` | `A` | Visita únicamente `A` |
| Camino simple | `A—B—C` | `A` | Visita exactamente `A`, `B` y `C` |
| Dos componentes | `A—B` y `C—D` | `A` | No visita `C` ni `D` |
| Vecinos repetidos | Dos referencias al mismo vecino | Origen | Cada vértice aparece una sola vez |

El orden exacto dependerá del orden en que `Grafo` entregue los vecinos y de cómo se apilen. La prueba debe fijar ese orden antes de afirmar una secuencia concreta.

## Casos previstos para ciclos

| Caso | Tipo | Resultado esperado |
| --- | --- | --- |
| `A—B—C` | No dirigido | Sin ciclo |
| `A—B—C—A` | No dirigido | Con ciclo |
| `A—B` | No dirigido | Regresar por el mismo borde no cuenta como ciclo |
| `A→B→C` | Dirigido | Sin ciclo |
| `A→B→C→A` | Dirigido | Con ciclo |
| Vértice aislado | Cualquiera | Sin ciclo |

Para el grafo no dirigido debe ignorarse el borde hacia el vértice anterior. Para el dirigido será necesario distinguir los vértices que están en la ruta activa de los que ya terminaron de procesarse; marcar solamente `visitado` puede producir falsos positivos.

## Puntos para explicar en la defensa

- La pila es LIFO: el último dato que entra es el primero que sale.
- `apilar()` y `desapilar()` son O(1) porque trabajan directamente sobre la cima.
- `ultimo()` es O(1) porque `ListaEnlazada` mantiene una referencia al último nodo.
- Inserción aprovecha datos ya ordenados; selección conserva el mismo número de comparaciones.
- No se ordenan 1,185,373 registros con algoritmos cuadráticos; se trabaja con aproximadamente 3,250 registros de un día.
- DFS profundiza por un camino y no garantiza una ruta mínima; BFS recorre por niveles y sí permite obtenerla en un grafo sin pesos.
- DFS deberá usar `Pila`, no recursión ni colecciones de `java.util`.

## Dependencias pendientes

- Parte 7: `Grafo` y `Vertice` de Luis.
- Parte 8: el grafo dirigido/no dirigido y el campo o mecanismo para conocer el vértice anterior.
- Parte 9: la interfaz de eventos y `Bitacora` de Josue.
