import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Vertice;

/**
 * Grafo pequeno "dibujado en papel" (no dirigido):
 *
 *      A --- B --- D --- F
 *       \    |     |     |
 *        \   |     |     |
 *          C ----- E --- G          H --- I   (grupo separado)
 *
 * Aristas (10): A-B, A-C, B-C, B-D, C-E, D-E, D-F, E-G, F-G, H-I
 */
public class PruebaGrafo {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    /** true si la lista de vecinos tiene exactamente esos datos (en cualquier orden). */
    private static boolean vecinosSon(Grafo<String> g, String dato, String... esperados) {
        ListaEnlazada<Vertice<String>> lista = g.vecinos(dato);
        if (lista == null || lista.tamanio() != esperados.length) {
            return false;
        }
        for (String e : esperados) {
            boolean esta = false;
            for (Vertice<String> v : lista) {
                if (v.getDato().equals(e)) esta = true;
            }
            if (!esta) return false;
        }
        return true;
    }

    private static Grafo<String> grafoDePapel() {
        Grafo<String> g = new Grafo<>();
        String[] nombres = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        for (String n : nombres) {
            g.agregarVertice(n);
        }
        String[][] aristas = {
            {"A", "B"}, {"A", "C"}, {"B", "C"}, {"B", "D"}, {"C", "E"},
            {"D", "E"}, {"D", "F"}, {"E", "G"}, {"F", "G"}, {"H", "I"}
        };
        for (String[] a : aristas) {
            g.agregarArista(a[0], a[1]);
        }
        return g;
    }

    public static void main(String[] args) {
        Grafo<String> g = grafoDePapel();

        // Cantidades
        revisar("9 vertices", g.numeroVertices() == 9);
        revisar("10 aristas", g.numeroAristas() == 10);
        revisar("no es dirigido", !g.esDirigido());

        // Los vecinos de cada uno son los del dibujo
        revisar("vecinos de A = B, C", vecinosSon(g, "A", "B", "C"));
        revisar("vecinos de B = A, C, D", vecinosSon(g, "B", "A", "C", "D"));
        revisar("vecinos de C = A, B, E", vecinosSon(g, "C", "A", "B", "E"));
        revisar("vecinos de D = B, E, F", vecinosSon(g, "D", "B", "E", "F"));
        revisar("vecinos de E = C, D, G", vecinosSon(g, "E", "C", "D", "G"));
        revisar("vecinos de F = D, G", vecinosSon(g, "F", "D", "G"));
        revisar("vecinos de G = E, F", vecinosSon(g, "G", "E", "F"));
        revisar("vecinos de H = I", vecinosSon(g, "H", "I"));
        revisar("vecinos de I = H", vecinosSon(g, "I", "H"));

        // Si B esta en la lista de A, A esta en la de B
        revisar("A-B existe en los dos sentidos", g.existeArista("A", "B") && g.existeArista("B", "A"));
        revisar("F-G existe en los dos sentidos", g.existeArista("F", "G") && g.existeArista("G", "F"));
        revisar("A-D no existe", !g.existeArista("A", "D") && !g.existeArista("D", "A"));
        revisar("no hay arista entre grupos (A-H)", !g.existeArista("A", "H"));

        // Arista de un vertice consigo mismo: se rechaza
        revisar("A-A se rechaza", !g.agregarArista("A", "A"));
        revisar("A no es vecino de si mismo", !g.existeArista("A", "A"));

        // Arista repetida: no cambia el numero de aristas
        revisar("A-B repetida se rechaza", !g.agregarArista("A", "B"));
        revisar("B-A (la misma al reves) se rechaza", !g.agregarArista("B", "A"));
        revisar("siguen 10 aristas", g.numeroAristas() == 10);
        revisar("A sigue con 2 vecinos", vecinosSon(g, "A", "B", "C"));

        // Vertices repetidos o inexistentes
        revisar("vertice A repetido se rechaza", !g.agregarVertice("A"));
        revisar("siguen 9 vertices", g.numeroVertices() == 9);
        revisar("buscar Z da null", g.buscarVertice("Z") == null);
        revisar("buscar C lo encuentra", g.buscarVertice("C") != null
                && g.buscarVertice("C").getDato().equals("C"));
        revisar("arista con un vertice que no existe se rechaza", !g.agregarArista("A", "Z"));
        revisar("vecinos de Z da null", g.vecinos("Z") == null);

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
    }
}
