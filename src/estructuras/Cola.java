package estructuras;

/** Cola FIFO enlazada: insertar y retirar cuestan O(1). */
public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) frente = nuevo;
        else fin.setSiguiente(nuevo);
        fin = nuevo;
        tamanio++;
    }

    public T desencolar() {
        T dato = frente();
        frente = frente.getSiguiente();
        tamanio--;
        if (frente == null) fin = null;
        return dato;
    }

    public T frente() {
        if (estaVacia()) throw new IllegalStateException("La cola esta vacia");
        return frente.getDato();
    }

    public boolean estaVacia() { return frente == null; }
    public int tamanio() { return tamanio; }

    public void mostrar(int n) {
        Nodo<T> actual = frente;
        for (int i = 0; i < n && actual != null; i++) {
            System.out.println(actual.getDato());
            actual = actual.getSiguiente();
        }
    }
}
