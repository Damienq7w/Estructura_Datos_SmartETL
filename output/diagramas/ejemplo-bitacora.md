# Flujograma de ejemplo de la bitácora

**Fecha y hora:** 2026-11-15 00:50:01

**Origen:** Generador propio

```mermaid
flowchart TD
    N1["DESENCOLAR"]
    N2{"VALIDAR"}
    N3["AGREGAR_A_LISTA"]
    N4["APILAR_ERROR"]
    N1 -->|3 veces| N2
    N2 -->|2 veces| N3
    N3 -->|1 vez| N1
    N2 -->|1 vez| N4
    N4 -->|1 vez| N1
```

La prueba registra nueve eventos de ejemplo. VALIDAR tiene dos salidas: agregar el registro a la lista o apilar el error.
