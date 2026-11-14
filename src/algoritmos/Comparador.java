package algoritmos;

/**
 * Compara dos datos.
 * Devuelve negativo si a debe ir antes que b,
 * cero si son equivalentes y positivo si a debe ir despues que b.
 */
@FunctionalInterface
public interface Comparador<T> {

    int comparar(T a, T b);
}