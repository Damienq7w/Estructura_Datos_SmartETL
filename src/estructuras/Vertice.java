package estructuras;

/**
 * Un vertice del grafo: un condado y la lista de sus vecinos.
 * Los campos visitado, anterior y distancia los usan Joel (BFS)
 * y Kenneth (DFS) al recorrer el grafo.
 */
public class Vertice<T> {

    private final T dato;
    private final ListaEnlazada<Vertice<T>> adyacentes;

    // Campos para los recorridos
    private boolean visitado;
    private Vertice<T> anterior;   // desde que vertice se llego
    private int distancia;         // saltos desde el origen

    public Vertice(T dato) {
        // TODO: guardar el dato y crear la lista de adyacentes vacia.
        //       visitado empieza en false, anterior en null y distancia en 0.
        this.dato = dato;
        this.adyacentes = null;   // <- cambia esto
    }

    // TODO: getters de dato y adyacentes.
    public T getDato() { return null; }
    public ListaEnlazada<Vertice<T>> getAdyacentes() { return null; }

    // TODO: getters y setters de visitado, anterior y distancia.
    public boolean isVisitado() { return false; }
    public void setVisitado(boolean visitado) { }

    public Vertice<T> getAnterior() { return null; }
    public void setAnterior(Vertice<T> anterior) { }

    public int getDistancia() { return 0; }
    public void setDistancia(int distancia) { }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
