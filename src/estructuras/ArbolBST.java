package estructuras;

// arbol binario de busqueda: en cada nodo, los menores van a la izquierda
// y los mayores a la derecha. Solo se usa compareTo para decidir.
public class ArbolBST<T extends Comparable<T>> {

    private NodoArbol<T> raiz;
    private int cantidad;

    // comparaciones hechas en la ultima busqueda
    private int comparaciones;

    // baja desde la raiz hasta encontrar un lugar vacio.
    // si la clave ya existe no la repite y devuelve false
    public boolean insertar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se puede insertar un dato nulo");
        }

        NodoArbol<T> nuevo = new NodoArbol<>(dato);
        if (raiz == null) {
            raiz = nuevo;
            cantidad++;
            return true;
        }

        NodoArbol<T> actual = raiz;
        while (true) {
            int resultado = dato.compareTo(actual.getDato());

            if (resultado == 0) {
                return false;
            }

            if (resultado < 0) {
                if (actual.getIzquierdo() == null) {
                    actual.setIzquierdo(nuevo);
                    cantidad++;
                    return true;
                }
                actual = actual.getIzquierdo();
            } else {
                if (actual.getDerecho() == null) {
                    actual.setDerecho(nuevo);
                    cantidad++;
                    return true;
                }
                actual = actual.getDerecho();
            }
        }
    }

    // devuelve el dato guardado en el arbol, o null si no esta.
    // en cada nodo descarta una rama completa
    public T buscar(T dato) {
        comparaciones = 0;
        if (dato == null) {
            return null;
        }

        NodoArbol<T> actual = raiz;
        while (actual != null) {
            comparaciones++;
            int resultado = dato.compareTo(actual.getDato());

            if (resultado == 0) {
                return actual.getDato();
            }
            if (resultado < 0) {
                actual = actual.getIzquierdo();
            } else {
                actual = actual.getDerecho();
            }
        }
        return null;
    }

    // cuenta recorriendo el arbol, para poder contrastarlo con tamanio().
    // usa una pila hecha con Nodo en lugar de recursion, porque un arbol
    // construido con datos ordenados puede tener miles de niveles
    public int contarNodos() {
        int total = 0;
        Nodo<NodoArbol<T>> pendientes = null;

        if (raiz != null) {
            pendientes = new Nodo<>(raiz);
        }

        while (pendientes != null) {
            NodoArbol<T> actual = pendientes.getDato();
            pendientes = pendientes.getSiguiente();
            total++;

            if (actual.getIzquierdo() != null) {
                Nodo<NodoArbol<T>> nuevo = new Nodo<>(actual.getIzquierdo());
                nuevo.setSiguiente(pendientes);
                pendientes = nuevo;
            }
            if (actual.getDerecho() != null) {
                Nodo<NodoArbol<T>> nuevo = new Nodo<>(actual.getDerecho());
                nuevo.setSiguiente(pendientes);
                pendientes = nuevo;
            }
        }
        return total;
    }

    // izquierda, nodo, derecha: en un arbol de busqueda sale ordenado de menor a mayor
    public ListaEnlazada<T> inOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        inOrden(raiz, resultado);
        return resultado;
    }

    private void inOrden(NodoArbol<T> nodo, ListaEnlazada<T> resultado) {
        if (nodo == null) {
            return;
        }
        inOrden(nodo.getIzquierdo(), resultado);
        resultado.insertar(nodo.getDato());
        inOrden(nodo.getDerecho(), resultado);
    }

    // nodo, izquierda, derecha: la raiz sale primero
    public ListaEnlazada<T> preOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        preOrden(raiz, resultado);
        return resultado;
    }

    private void preOrden(NodoArbol<T> nodo, ListaEnlazada<T> resultado) {
        if (nodo == null) {
            return;
        }
        resultado.insertar(nodo.getDato());
        preOrden(nodo.getIzquierdo(), resultado);
        preOrden(nodo.getDerecho(), resultado);
    }

    // izquierda, derecha, nodo: la raiz sale al final
    public ListaEnlazada<T> postOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        postOrden(raiz, resultado);
        return resultado;
    }

    private void postOrden(NodoArbol<T> nodo, ListaEnlazada<T> resultado) {
        if (nodo == null) {
            return;
        }
        postOrden(nodo.getIzquierdo(), resultado);
        postOrden(nodo.getDerecho(), resultado);
        resultado.insertar(nodo.getDato());
    }

    // contador interno: se actualiza al insertar, no recorre nada
    public int tamanio() { return cantidad; }

    public boolean estaVacio() { return raiz == null; }

    public int getComparaciones() { return comparaciones; }
}
