# Evidencia de la tabla comparativa de ordenamiento

Resultado generado por `test/ComparativaOrdenamiento.java` en una ejecución real.

## Entorno de ejecución

- Fecha: 2026-11-14 22:03:15 (UTC-05:00)
- Sistema: Microsoft Windows NT 10.0.26300.0, arquitectura AMD64
- Procesador informado por el sistema: Intel64 Family 6 Model 140 Stepping 1, GenuineIntel
- Java: OpenJDK Temurin 21.0.12.1 LTS, 64 bits
- Medición: mediana de tres repeticiones, después de una repetición de calentamiento

## Resultados

| Cantidad | Entrada | Algoritmo | Comparaciones | Intercambios | Mediana aproximada (ms) |
| ---: | --- | --- | ---: | ---: | ---: |
| 100 | Ordenada | Inserción | 99 | 0 | 0.018 |
| 100 | Ordenada | Selección | 4,950 | 0 | 0.419 |
| 100 | Inversa | Inserción | 4,950 | 4,950 | 0.502 |
| 100 | Inversa | Selección | 4,950 | 50 | 0.413 |
| 1,000 | Ordenada | Inserción | 999 | 0 | 0.121 |
| 1,000 | Ordenada | Selección | 499,500 | 0 | 0.698 |
| 1,000 | Inversa | Inserción | 499,500 | 499,500 | 6.610 |
| 1,000 | Inversa | Selección | 499,500 | 500 | 1.089 |
| 3,250 | Ordenada | Inserción | 3,249 | 0 | 0.076 |
| 3,250 | Ordenada | Selección | 5,279,625 | 0 | 6.837 |
| 3,250 | Inversa | Inserción | 5,279,625 | 5,279,625 | 19.703 |
| 3,250 | Inversa | Selección | 5,279,625 | 1,625 | 6.942 |
| 10,000 | Ordenada | Inserción | 9,999 | 0 | 0.206 |
| 10,000 | Ordenada | Selección | 49,995,000 | 0 | 71.509 |
| 10,000 | Inversa | Inserción | 49,995,000 | 49,995,000 | 185.956 |
| 10,000 | Inversa | Selección | 49,995,000 | 5,000 | 62.207 |

## Conclusiones

- Inserción aprovecha una entrada ya ordenada: realiza solamente `n - 1` comparaciones y ningún intercambio.
- En una entrada inversa, inserción realiza una cantidad cuadrática de comparaciones e intercambios.
- Selección realiza `n(n - 1) / 2` comparaciones tanto con la entrada ordenada como con la inversa.
- Selección hace muchos menos intercambios que inserción cuando la entrada está en orden inverso.
- Los tiempos son aproximados y dependen de la JVM y de la carga del equipo; los conteos de operaciones son deterministas.

## Reproducción

```powershell
javac -encoding UTF-8 -d bin -sourcepath "src;test" test/ComparativaOrdenamiento.java
java -cp bin ComparativaOrdenamiento
```
