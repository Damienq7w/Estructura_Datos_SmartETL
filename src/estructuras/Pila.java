package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Pila generica LIFO.
 * El ultimo dato apilado es el primero que se desapila.
 */
public class Pila<T> implements Iterable<T> {

    private Nodo<T> cima;
    private int tamanio;

    /**
     * Agrega un dato sobre la cima. Complejidad O(1).
     */
    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamanio++;
    }

    /**
     * Quita y devuelve el dato que esta en la cima.
     */
    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila esta vacia");
        }

        T dato = cima.getDato();
        cima = cima.getSiguiente();
        tamanio--;
        return dato;
    }

    /**
     * Devuelve la cima sin quitarla.
     */
    public T cima() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila esta vacia");
        }

        return cima.getDato();
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /**
     * Muestra hasta n elementos desde la cima hacia abajo.
     */
    public void mostrar(int n) {
        int mostrados = 0;

        for (T dato : this) {
            if (mostrados >= n) {
                break;
            }

            System.out.println(dato);
            mostrados++;
        }
    }

    /**
     * Permite recorrer sin vaciar la pila, desde la cima hacia abajo.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Nodo<T> actual = cima;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}