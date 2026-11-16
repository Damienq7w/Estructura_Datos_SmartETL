# Datos de prueba — resultado esperado

Tres archivos pequeños, con errores puestos a propósito, para probar cada parte sin cargar un millón de filas. Las cabeceras son **idénticas** a las de `data/`. Los números de abajo se contaron fila por fila y se comprobaron con un script independiente; si tu resultado es distinto, el error está en tu código (o avisa a Sara si crees que está mal contado).

Regla que siempre se cumple: **leídos = válidos + rechazados**.

## 1. `casos_prueba.csv`

42 filas de datos (43 líneas con la cabecera), ordenadas por fecha. 5 condados reales × 7 días (2021-03-01 a 2021-03-07) más 7 filas malas.

| Condado | FIPS | Filas buenas |
| --- | --- | --- |
| Autauga, Alabama | 01001 | 7 |
| Chilton, Alabama | 01021 | 7 (más 1 duplicada) |
| Elmore, Alabama | 01051 | 7 |
| Adjuntas, Puerto Rico | 72001 | 7 (las 2 primeras con muertes vacías) |
| Honolulu, Hawaii | 15003 | 7 |

| Concepto | Esperado |
| --- | --- |
| Leídos | **42** |
| Válidos | **35** |
| Reparados | 0 |
| Advertencias (muertes vacías) | **2** (Adjuntas, 2021-03-01 y 2021-03-02) |
| Rechazados | **7** |

Las 7 filas malas, con su línea en el archivo (la línea 1 es la cabecera):

| Línea | Fila | Motivo | Cantidad |
| --- | --- | --- | --- |
| 12 | `2021-03-02,Unknown,Alabama,,12,0` | `CONDADO_DESCONOCIDO` | 1 |
| 18 | `2021-03-03,New York City,New York,,8420,312` | `AREA_SIN_FIPS` | 1 |
| 24 | `2021-03-04,Montgomery,Alabama,01101,abc,12` | `VALOR_NO_NUMERICO` | 1 |
| 30 | `2021-03-05,Montgomery,Alabama,01101,-5,3` | `VALOR_NEGATIVO` | 1 |
| 31 | `2021-13-40,Montgomery,Alabama,01101,50,2` | `FECHA_INVALIDA` | 1 |
| 34 | segunda fila idéntica de Chilton, 2021-03-06 | `DUPLICADO` | 1 |
| 38 | `2021-03-06,Elmore,Alabama` (3 columnas) | `COLUMNAS_INCOMPLETAS` | 1 |

Total por motivo: 1 + 1 + 1 + 1 + 1 + 1 + 1 = 7. Válidos: 42 − 7 = 35.

> **Duplicados:** detectarlos es la Parte 5 de Luis. Mientras su `Transform` no los detecte, saldrán **36 válidos y 6 rechazados** (sin la fila `DUPLICADO`). Cuando lo termine deben salir 35 y 7.

Para el ordenamiento de Kenneth, el ranking por casos acumulados del **2021-03-07** es: Honolulu 277, Autauga 149, Elmore 129, Chilton 109, Adjuntas 79.

## 2. `condados_prueba.csv`

9 filas de datos.

| Concepto | Esperado |
| --- | --- |
| Leídos | **9** |
| Válidos | **8** |
| Reparados | **2** (`1001` → `01001` Autauga, `1021` → `01021` Chilton) |
| Advertencias | 0 |
| Rechazados | **1** |

Rechazado: `1000,ALABAMA,NA` → `NO_ES_CONDADO` (línea 2). Los 8 condados válidos son exactamente los 8 vértices del grafo.

## 3. `vecindad_prueba.tsv`

8 filas de datos, separadas por tabulaciones (los nombres van entre comillas, como en el archivo real).

| Concepto | Esperado |
| --- | --- |
| Leídos | **8** |
| Válidas (aristas únicas) | **6** |
| Rechazados | **2** |

| Línea | Fila | Motivo |
| --- | --- | --- |
| 3 | Chilton → Autauga (ya estaba Autauga → Chilton en la línea 2) | `DUPLICADO` |
| 9 | Honolulu → Honolulu | `AUTO_VECINDAD` |

### El grafo

```
   Grupo 1                              Grupo 2                  Grupo 3
   (triángulo + una cola)               (camino)                 (aislado)

   01001 Autauga                        72081 Lares              15003 Honolulu
     |  \                                  |
     |   \                              72001 Adjuntas
   01021----01051 Elmore                   |
   Chilton    |                         72141 Utuado
            01101 Montgomery
```

| Dato | Esperado |
| --- | --- |
| Vértices | **8** |
| Aristas | **6** |
| Grupos (componentes conexas) | **3** |
| Ciclos | **sí**: el triángulo Autauga–Chilton–Elmore (en el grupo 2 no hay ciclos) |
| Máximo de vecinos | 3 (Elmore) |

Vecinos de cada condado:

| Condado | Vecinos |
| --- | --- |
| 01001 Autauga | 01021, 01051 |
| 01021 Chilton | 01001, 01051 |
| 01051 Elmore | 01001, 01021, 01101 |
| 01101 Montgomery | 01051 |
| 72001 Adjuntas | 72081, 72141 |
| 72081 Lares | 72001 |
| 72141 Utuado | 72001 |
| 15003 Honolulu | (ninguno) |

Rutas de ejemplo: Montgomery → Chilton pasa por Elmore (2 saltos); Autauga → Lares no existe (grupos distintos).

## Cómo se usan

- **Joel / Luis:** encolar cada archivo, transformar y comparar los contadores con las tablas de arriba.
- **Kenneth:** usar el grafo para probar BFS, DFS y ciclos.
- **Damian / Josue:** las cifras de este archivo sirven para revisar lo que devuelva la IA.
