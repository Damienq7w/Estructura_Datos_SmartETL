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
    private boolean visitado;      // empieza en false
    private Vertice<T> anterior;   // desde que vertice se llego; empieza en null
    private int distancia;         // saltos desde el origen; empieza en 0

    public Vertice(T dato) {
        this.dato = dato;
        this.adyacentes = new ListaEnlazada<>();
    }

    public T getDato() { return dato; }
    public ListaEnlazada<Vertice<T>> getAdyacentes() { return adyacentes; }

    public boolean isVisitado() { return visitado; }
    public void setVisitado(boolean visitado) { this.visitado = visitado; }

    public Vertice<T> getAnterior() { return anterior; }
    public void setAnterior(Vertice<T> anterior) { this.anterior = anterior; }

    public int getDistancia() { return distancia; }
    public void setDistancia(int distancia) { this.distancia = distancia; }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
