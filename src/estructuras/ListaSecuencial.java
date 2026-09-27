package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

// fila de casilleros con un arreglo, solo para comparar con el tren
public class ListaSecuencial<T> implements Iterable<T> {

    private Object[] datos;
    private int tamanio;
    private static final int CAPACIDAD_INICIAL = 16;

    public ListaSecuencial() {
        datos = new Object[CAPACIDAD_INICIAL];
        tamanio = 0;
    }

    // si esta lleno duplica el arreglo y copia todo
    public void insertar(T dato) {
        if (tamanio == datos.length) {
            crecer();
        }
        datos[tamanio] = dato;
        tamanio++;
    }

    // acceso directo a la casilla
    @SuppressWarnings("unchecked")
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamanio) {
            throw new IndexOutOfBoundsException("Posicion invalida: " + posicion);
        }
        return (T) datos[posicion];
    }

    public boolean eliminar(T dato) {
        for (int i = 0; i < tamanio; i++) {
            if (datos[i].equals(dato)) {
                for (int j = i; j < tamanio - 1; j++) {
                    datos[j] = datos[j + 1];
                }
                datos[tamanio - 1] = null;
                tamanio--;
                return true;
            }
        }
        return false;
    }

    public int tamanio() { return tamanio; }

    public boolean estaVacia() { return tamanio == 0; }

    // espacio total reservado, para ver cuanto ha crecido
    public int capacidad() { return datos.length; }

    public void mostrar(int n) {
        int contador = 0;
        for (T dato : this) {
            if (contador >= n) break;
            System.out.println(dato);
            contador++;
        }
    }

    private void crecer() {
        Object[] nuevo = new Object[datos.length * 2];
        System.arraycopy(datos, 0, nuevo, 0, datos.length);
        datos = nuevo;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int posicion = 0;

            @Override
            public boolean hasNext() {
                return posicion < tamanio;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) datos[posicion++];
            }
        };
    }
}
