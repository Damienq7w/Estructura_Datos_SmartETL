import estructuras.Grafo;
import estructuras.Vertice;

/** Pruebas de reiniciarRecorrido y del modo dirigido. */
public class PruebaGrafoDirigido {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    public static void main(String[] args) {
        // ---------- reiniciarRecorrido ----------
        Grafo<String> g = new Grafo<>();
        g.agregarVertice("A");
        g.agregarVertice("B");
        g.agregarArista("A", "B");

        Vertice<String> a = g.buscarVertice("A");
        Vertice<String> b = g.buscarVertice("B");

        revisar("al crearse no esta visitado", !a.isVisitado());
        revisar("al crearse anterior es null", a.getAnterior() == null);
        revisar("al crearse distancia es 0", a.getDistancia() == 0);

        // Simula que un recorrido ya paso por aqui
        a.setVisitado(true);
        b.setVisitado(true);
        b.setAnterior(a);
        b.setDistancia(1);
        revisar("los setters guardan el valor", b.isVisitado() && b.getAnterior() == a && b.getDistancia() == 1);

        g.reiniciarRecorrido();
        revisar("A queda sin visitar", !a.isVisitado());
        revisar("B queda sin visitar", !b.isVisitado());
        revisar("B queda con anterior null", b.getAnterior() == null);
        revisar("B queda con distancia 0", b.getDistancia() == 0);
        revisar("reiniciar no borra aristas", g.existeArista("A", "B") && g.numeroAristas() == 1);

        // ---------- modo dirigido ----------
        //   X ---> Y ---> Z
        //   ^             |
        //   +-------------+      (ciclo X -> Y -> Z -> X)
        Grafo<String> d = new Grafo<>(true);
        d.agregarVertice("X");
        d.agregarVertice("Y");
        d.agregarVertice("Z");

        revisar("es dirigido", d.esDirigido());
        revisar("X->Y se agrega", d.agregarArista("X", "Y"));
        revisar("X->Y existe", d.existeArista("X", "Y"));
        revisar("Y->X NO existe (solo un sentido)", !d.existeArista("Y", "X"));
        revisar("Y no tiene a X de vecino", d.vecinos("Y").tamanio() == 0);

        d.agregarArista("Y", "Z");
        d.agregarArista("Z", "X");
        revisar("3 aristas en el ciclo", d.numeroAristas() == 3);
        revisar("Y->X ahora si se puede agregar (es otra flecha)", d.agregarArista("Y", "X"));
        revisar("4 aristas", d.numeroAristas() == 4);
        revisar("X->Y repetida se rechaza", !d.agregarArista("X", "Y"));
        revisar("X->X se rechaza", !d.agregarArista("X", "X"));
        revisar("siguen 4 aristas", d.numeroAristas() == 4);

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
    }
}
