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
        // TODO: crear la lista de vertices vacia y guardar si es dirigido.
        this.vertices = null;   // <- cambia esto
        this.dirigido = dirigido;
    }

    /**
     * Agrega un vertice con ese dato si todavia no existe.
     * @return true si lo agrego, false si ya existia.
     *
     * Pasos:
     *  1. Si buscarVertice(dato) no es null, devolver false.
     *  2. Crear un Vertice nuevo e insertarlo en la lista de vertices.
     *  3. Devolver true.
     */
    public boolean agregarVertice(T dato) {
        // TODO
        return false;
    }

    /**
     * Conecta origen con destino.
     * @return true si agrego la arista; false si se rechazo.
     *
     * Pasos:
     *  1. Si origen y destino son iguales (equals), devolver false:
     *     un vertice no puede ser vecino de si mismo.
     *  2. Buscar los dos vertices. Si alguno no existe, devolver false.
     *  3. Si existeArista(origen, destino), devolver false (repetida).
     *  4. Insertar el vertice destino en los adyacentes del origen.
     *  5. Si NO es dirigido, insertar tambien el origen en los
     *     adyacentes del destino.
     *  6. Sumar 1 a aristas y devolver true.
     */
    public boolean agregarArista(T origen, T destino) {
        // TODO
        return false;
    }

    /**
     * Devuelve el vertice que tiene ese dato, o null si no existe.
     *
     * Pasos: recorrer la lista de vertices (for-each) y devolver el
     * primero cuyo dato sea equals al buscado.
     */
    public Vertice<T> buscarVertice(T dato) {
        // TODO
        return null;
    }

    /**
     * true si destino esta en la lista de vecinos de origen.
     * Solo mira la lista del origen (14 elementos como maximo).
     *
     * Pasos:
     *  1. Buscar el vertice origen. Si no existe, devolver false.
     *  2. Recorrer sus adyacentes; si alguno tiene el dato destino,
     *     devolver true.
     *  3. Si no aparecio, devolver false.
     */
    public boolean existeArista(T origen, T destino) {
        // TODO
        return false;
    }

    /**
     * Lista de vecinos del vertice con ese dato, o null si no existe.
     * Esto resuelve el problema "condados que colindan con uno dado".
     */
    public ListaEnlazada<Vertice<T>> vecinos(T dato) {
        // TODO
        return null;
    }

    public int numeroVertices() {
        // TODO: tamanio de la lista de vertices
        return 0;
    }

    public int numeroAristas() {
        // TODO
        return 0;
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
        // TODO: recorrer los vertices y reiniciar los tres campos
    }
}
