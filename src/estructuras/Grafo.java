package estructuras;

/**
 * Grafo con listas de adyacencia: cada vertice guarda solo sus vecinos.
 *
 * Por que listas y no una tabla: con 3.234 condados la tabla tendria
 * mas de 10 millones de casillas para solo 9.483 conexiones; con
 * listas cada condado guarda sus vecinos (14 como maximo).
 *
 * El grafo real es NO dirigido (si A es vecino de B, B lo es de A).
 * El modo dirigido sirve para el grafo pequeno con el que Kenneth
 * prueba ciclos.
 */
public class Grafo<T> {

    private final ListaEnlazada<Vertice<T>> vertices;
    private final boolean dirigido;
    private int aristas;   // en el no dirigido, A-B cuenta una sola vez

    /** Grafo no dirigido (el normal). */
    public Grafo() {
        this(false);
    }

    public Grafo(boolean dirigido) {
        this.vertices = new ListaEnlazada<>();
        this.dirigido = dirigido;
    }

    /**
     * Agrega un vertice con ese dato si todavia no existe.
     * @return true si lo agrego, false si ya existia.
     */
    public boolean agregarVertice(T dato) {
        if (buscarVertice(dato) != null) {
            return false;
        }
        vertices.insertar(new Vertice<>(dato));
        return true;
    }

    /**
     * Conecta origen con destino.
     * @return true si agrego la arista; false si se rechazo
     *         (vertice consigo mismo, vertice inexistente o arista repetida).
     */
    public boolean agregarArista(T origen, T destino) {
        // 1. Un vertice no puede ser vecino de si mismo
        if (origen.equals(destino)) {
            return false;
        }
        // 2. Los dos vertices tienen que existir
        Vertice<T> a = buscarVertice(origen);
        Vertice<T> b = buscarVertice(destino);
        if (a == null || b == null) {
            return false;
        }
        // 3. No se repiten aristas
        if (existeArista(origen, destino)) {
            return false;
        }
        // 4. destino entra en la lista de vecinos de origen
        a.getAdyacentes().insertar(b);
        // 5. Si no es dirigido, tambien origen en la de destino
        if (!dirigido) {
            b.getAdyacentes().insertar(a);
        }
        // 6. Una arista mas
        aristas++;
        return true;
    }

    /** Devuelve el vertice que tiene ese dato, o null si no existe. */
    public Vertice<T> buscarVertice(T dato) {
        for (Vertice<T> v : vertices) {
            if (v.getDato().equals(dato)) {
                return v;
            }
        }
        return null;
    }

    /**
     * true si destino esta en la lista de vecinos de origen.
     * Solo mira la lista del origen (14 elementos como maximo).
     */
    public boolean existeArista(T origen, T destino) {
        Vertice<T> a = buscarVertice(origen);
        if (a == null) {
            return false;
        }
        for (Vertice<T> vecino : a.getAdyacentes()) {
            if (vecino.getDato().equals(destino)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lista de vecinos del vertice con ese dato, o null si no existe.
     * Esto resuelve el problema "condados que colindan con uno dado".
     */
    public ListaEnlazada<Vertice<T>> vecinos(T dato) {
        Vertice<T> v = buscarVertice(dato);
        if (v == null) {
            return null;
        }
        return v.getAdyacentes();
    }

    public int numeroVertices() {
        return vertices.tamanio();
    }

    public int numeroAristas() {
        return aristas;
    }

    public boolean esDirigido() {
        return dirigido;
    }

    public ListaEnlazada<Vertice<T>> getVertices() {
        return vertices;
    }

    /**
     * Deja todos los vertices listos para un recorrido nuevo:
     * visitado = false, anterior = null, distancia = 0.
     */
    public void reiniciarRecorrido() {
        for (Vertice<T> v : vertices) {
            v.setVisitado(false);
            v.setAnterior(null);
            v.setDistancia(0);
        }
    }
}
