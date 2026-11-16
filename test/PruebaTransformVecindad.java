import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Vertice;
import etl.Conteo;
import etl.Transform;
import model.Condado;
import model.RegistroCrudo;
import model.TipoError;

/**
 * Vecindad armada a mano, con el mismo formato que vecindad.tsv:
 * cada par viene dos veces (A-B y B-A) y cada condado es "vecino de si mismo".
 *
 *   Autauga (01001) --- Chilton (01021) --- Bibb (01007)
 *   Hawaii  (15001)  (isla: solo aparece como vecino de si mismo)
 */
public class PruebaTransformVecindad {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    private static RegistroCrudo fila(int linea, String... campos) {
        return new RegistroCrudo(linea, campos);
    }

    private static Condado condado(String fips) {
        return new Condado(fips, "", "");
    }

    public static void main(String[] args) {
        Cola<RegistroCrudo> cola = new Cola<>();
        //           county_name            county_id  adjacent_county_name   adjacent_county_id
        cola.encolar(fila(2,  "Autauga County, AL", "1001",  "Autauga County, AL", "1001"));   // AUTO (fips de 4)
        cola.encolar(fila(3,  "Autauga County, AL", "1001",  "Chilton County, AL", "01021"));  // arista nueva
        cola.encolar(fila(4,  "Chilton County, AL", "01021", "Autauga County, AL", "01001"));  // DUPLICADO (B-A)
        cola.encolar(fila(5,  "Chilton County, AL", "01021", "Chilton County, AL", "01021"));  // AUTO
        cola.encolar(fila(6,  "Chilton County, AL", "01021", "Bibb County, AL",    "01007"));  // arista nueva
        cola.encolar(fila(7,  "Bibb County, AL",    "01007", "Bibb County, AL",    "01007"));  // AUTO
        cola.encolar(fila(8,  "Bibb County, AL",    "01007", "Chilton County, AL", "01021"));  // DUPLICADO
        cola.encolar(fila(9,  "Hawaii County, HI",  "15001", "Hawaii County, HI",  "15001"));  // AUTO (isla)
        cola.encolar(fila(10, "Bibb County, AL",    "01007"));                                 // COLUMNAS_INCOMPLETAS

        Transform t = new Transform();
        t.transformarVecindad(cola);
        Conteo k = t.getConteoVecindad();
        Grafo<Condado> g = t.getGrafo();

        // Conteos
        revisar("la cola queda vacia", cola.estaVacia());
        revisar("9 leidas", k.getLeidos() == 9);
        revisar("2 validas (aristas nuevas)", k.getValidos() == 2);
        revisar("4 AUTO_VECINDAD", k.getRechazados(TipoError.AUTO_VECINDAD) == 4);
        revisar("2 DUPLICADO", k.getRechazados(TipoError.DUPLICADO) == 2);
        revisar("1 COLUMNAS_INCOMPLETAS", k.getRechazados(TipoError.COLUMNAS_INCOMPLETAS) == 1);
        revisar("leidas = validas + rechazadas", k.getLeidos() == k.getValidos() + k.getRechazados());

        // Grafo
        revisar("4 vertices (la isla tambien esta)", g.numeroVertices() == 4);
        revisar("2 aristas", g.numeroAristas() == 2);
        revisar("1001 se arreglo: existe el vertice 01001", g.buscarVertice(condado("01001")) != null);
        revisar("no existe un vertice 1001 sin arreglar", g.buscarVertice(condado("1001")) == null);
        revisar("Autauga - Chilton en los dos sentidos",
                g.existeArista(condado("01001"), condado("01021"))
                && g.existeArista(condado("01021"), condado("01001")));
        revisar("Autauga no es vecino de si mismo", !g.existeArista(condado("01001"), condado("01001")));
        revisar("Autauga y Bibb no son vecinos", !g.existeArista(condado("01001"), condado("01007")));

        ListaEnlazada<Vertice<Condado>> vecinosChilton = g.vecinos(condado("01021"));
        revisar("Chilton tiene 2 vecinos", vecinosChilton.tamanio() == 2);
        revisar("la isla no tiene vecinos", g.vecinos(condado("15001")).tamanio() == 0);

        // El nombre y el estado salen de "Autauga County, AL"
        Condado autauga = g.buscarVertice(condado("01001")).getDato();
        revisar("nombre = Autauga County", autauga.getNombre().equals("Autauga County"));
        revisar("estado = AL", autauga.getEstado().equals("AL"));

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
    }
}
