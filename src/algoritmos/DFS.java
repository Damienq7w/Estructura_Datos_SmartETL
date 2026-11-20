package algoritmos;

import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.OyenteEventos;
import estructuras.Pila;
import estructuras.Vertice;

/**
 * Recorrido en profundidad iterativo con la pila propia del proyecto.
 * Tambien detecta ciclos en grafos dirigidos y no dirigidos.
 */
public class DFS<T> {

    private OyenteEventos oyente;

    public DFS() {
        this(null);
    }

    public DFS(OyenteEventos oyente) {
        this.oyente = oyente;
    }

    public void setOyente(OyenteEventos oyente) {
        this.oyente = oyente;
    }

    /**
     * Recorre el componente del origen. Si el origen no existe, devuelve
     * una lista vacia. Las marcas del grafo se reinician en cada llamada.
     */
    public ListaEnlazada<T> recorrer(Grafo<T> grafo, T origen) {
        if (grafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser null");
        }

        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        grafo.reiniciarRecorrido();

        Vertice<T> inicial = grafo.buscarVertice(origen);
        if (inicial == null) {
            return resultado;
        }

        Pila<Vertice<T>> pila = new Pila<>();
        pila.apilar(inicial);
        avisar("APILAR_VECINO", "origen=" + inicial);

        while (!pila.estaVacia()) {
            Vertice<T> actual = pila.desapilar();
            avisar("DESAPILAR", "vertice=" + actual
                    + ", pendientes=" + pila.tamanio());

            if (actual.isVisitado()) {
                avisar("YA_VISITADO", "vertice=" + actual);
                continue;
            }

            actual.setVisitado(true);
            resultado.insertar(actual.getDato());
            avisar("VISITAR", "vertice=" + actual);

            for (Vertice<T> vecino : actual.getAdyacentes()) {
                if (!vecino.isVisitado()) {
                    vecino.setAnterior(actual);
                    pila.apilar(vecino);
                    avisar("APILAR_VECINO", "desde=" + actual
                            + ", vecino=" + vecino);
                } else {
                    avisar("YA_VISITADO", "desde=" + actual
                            + ", vecino=" + vecino);
                }
            }
        }

        return resultado;
    }

    /** Devuelve true si cualquier componente del grafo contiene un ciclo. */
    public boolean tieneCiclo(Grafo<T> grafo) {
        return !ejemploCiclo(grafo).estaVacia();
    }

    /**
     * Devuelve un ciclo cerrado (el primer dato se repite al final), o una
     * lista vacia cuando el grafo no contiene ciclos.
     */
    public ListaEnlazada<T> ejemploCiclo(Grafo<T> grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser null");
        }
        return grafo.esDirigido()
                ? cicloDirigido(grafo)
                : cicloNoDirigido(grafo);
    }

    private ListaEnlazada<T> cicloNoDirigido(Grafo<T> grafo) {
        grafo.reiniciarRecorrido();

        for (Vertice<T> raiz : grafo.getVertices()) {
            if (raiz.isVisitado()) {
                continue;
            }

            Pila<Vertice<T>> pila = new Pila<>();
            raiz.setVisitado(true);
            pila.apilar(raiz);

            while (!pila.estaVacia()) {
                Vertice<T> actual = pila.desapilar();

                for (Vertice<T> vecino : actual.getAdyacentes()) {
                    if (!vecino.isVisitado()) {
                        vecino.setVisitado(true);
                        vecino.setAnterior(actual);
                        pila.apilar(vecino);
                    } else if (vecino != actual.getAnterior()) {
                        return construirCicloNoDirigido(actual, vecino);
                    }
                }
            }
        }

        return new ListaEnlazada<>();
    }

    private ListaEnlazada<T> cicloDirigido(Grafo<T> grafo) {
        grafo.reiniciarRecorrido();

        for (Vertice<T> raiz : grafo.getVertices()) {
            if (raiz.getDistancia() != 0) {
                continue;
            }

            Pila<Marco<T>> pila = new Pila<>();
            raiz.setDistancia(1); // gris: esta en la ruta activa
            pila.apilar(new Marco<>(raiz));

            while (!pila.estaVacia()) {
                Marco<T> marco = pila.cima();
                Vertice<T> actual = marco.vertice;

                if (marco.siguiente < actual.getAdyacentes().tamanio()) {
                    Vertice<T> vecino = actual.getAdyacentes()
                            .obtener(marco.siguiente++);

                    if (vecino.getDistancia() == 0) {
                        vecino.setAnterior(actual);
                        vecino.setDistancia(1);
                        pila.apilar(new Marco<>(vecino));
                    } else if (vecino.getDistancia() == 1) {
                        return construirCicloDirigido(actual, vecino);
                    }
                } else {
                    pila.desapilar();
                    actual.setDistancia(2); // negro: ya termino
                    actual.setVisitado(true);
                }
            }
        }

        return new ListaEnlazada<>();
    }

    private ListaEnlazada<T> construirCicloNoDirigido(
            Vertice<T> extremoA, Vertice<T> extremoB) {
        Vertice<T> comun = extremoA;

        while (comun != null && !esAntecesor(comun, extremoB)) {
            comun = comun.getAnterior();
        }

        if (comun == null) {
            return new ListaEnlazada<>();
        }

        ListaEnlazada<T> ciclo = new ListaEnlazada<>();
        Vertice<T> cursor = extremoA;
        while (cursor != comun) {
            ciclo.insertar(cursor.getDato());
            cursor = cursor.getAnterior();
        }
        ciclo.insertar(comun.getDato());

        Pila<T> ladoB = new Pila<>();
        cursor = extremoB;
        while (cursor != comun) {
            ladoB.apilar(cursor.getDato());
            cursor = cursor.getAnterior();
        }
        while (!ladoB.estaVacia()) {
            ciclo.insertar(ladoB.desapilar());
        }

        ciclo.insertar(extremoA.getDato());
        return ciclo;
    }

    private boolean esAntecesor(Vertice<T> posible, Vertice<T> vertice) {
        Vertice<T> cursor = vertice;
        while (cursor != null) {
            if (cursor == posible) {
                return true;
            }
            cursor = cursor.getAnterior();
        }
        return false;
    }

    private ListaEnlazada<T> construirCicloDirigido(
            Vertice<T> actual, Vertice<T> antecesor) {
        Pila<T> inversa = new Pila<>();
        Vertice<T> cursor = actual;

        while (cursor != antecesor) {
            inversa.apilar(cursor.getDato());
            cursor = cursor.getAnterior();
        }

        ListaEnlazada<T> ciclo = new ListaEnlazada<>();
        ciclo.insertar(antecesor.getDato());
        while (!inversa.estaVacia()) {
            ciclo.insertar(inversa.desapilar());
        }
        ciclo.insertar(antecesor.getDato());
        return ciclo;
    }

    private void avisar(String accion, String detalle) {
        if (oyente != null) {
            oyente.evento("DFS", accion, detalle);
        }
    }

    private static class Marco<T> {
        private final Vertice<T> vertice;
        private int siguiente;

        private Marco(Vertice<T> vertice) {
            this.vertice = vertice;
        }
    }
}
