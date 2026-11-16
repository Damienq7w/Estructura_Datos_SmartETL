import estructuras.ArbolBST;
import estructuras.Cola;
import estructuras.ListaEnlazada;
import etl.Extract;
import etl.Transform;
import model.Condado;
import model.IndiceCondado;
import model.RegistroCrudo;

// usa data/pruebas/condados_prueba.csv: 9 filas, 8 condados validos,
// dos de ellos con FIPS de 4 digitos (1001 y 1021)
public class PruebaIndiceCondado {

    private static int pruebasOk = 0;
    private static int pruebasFalla = 0;

    public static void main(String[] args) throws Exception {
        Cola<RegistroCrudo> cola = new Cola<>();
        new Extract().encolarCondados("data/pruebas/condados_prueba.csv", cola);
        Transform transform = new Transform();
        transform.transformarCondados(cola);
        ListaEnlazada<Condado> limpios = transform.getCondadosValidos();

        ArbolBST<IndiceCondado> arbol = IndiceCondado.construirArbol(limpios);

        reportar("Un nodo por condado valido",
                arbol.tamanio() == 8 && arbol.contarNodos() == 8,
                "se esperaban 8 nodos y hay " + arbol.tamanio());

        boolean todos = true;
        for (Condado condado : limpios) {
            IndiceCondado encontrado = IndiceCondado.buscar(arbol, condado.getFips());
            todos = todos && encontrado != null
                    && encontrado.getNombre().equals(condado.getNombre())
                    && encontrado.getEstado().equals(condado.getEstado());
        }
        reportar("Cada condado se encuentra", todos,
                "todos los condados limpios deberian estar en el arbol con su nombre y estado");

        IndiceCondado autauga = IndiceCondado.buscar(arbol, "01001");
        IndiceCondado chilton = IndiceCondado.buscar(arbol, "01021");
        reportar("FIPS de 4 digitos se encuentran con 5",
                autauga != null && autauga.getNombre().equals("Autauga County")
                        && chilton != null && chilton.getNombre().equals("Chilton County")
                        && IndiceCondado.buscar(arbol, "1001") == null,
                "01001 y 01021 deberian encontrarse, y 1001 (sin cero) no");

        reportar("Lo que no esta devuelve null",
                IndiceCondado.buscar(arbol, "01000") == null      // ALABAMA: no es condado
                        && IndiceCondado.buscar(arbol, "99999") == null
                        && IndiceCondado.buscar(arbol, null) == null,
                "el estado rechazado, un FIPS inexistente y null deberian dar null");

        // 8 nodos por medios caben en 4 niveles; insertados en orden serian 8
        reportar("Arbol balanceado", arbol.altura() == 4,
                "altura esperada 4, obtenida " + arbol.altura());

        String anterior = "";
        boolean ordenado = true;
        for (IndiceCondado indice : arbol.inOrden()) {
            ordenado = ordenado && indice.getFips().compareTo(anterior) > 0;
            anterior = indice.getFips();
        }
        reportar("InOrden sale ordenado por FIPS", ordenado,
                "el recorrido inOrden deberia dar los FIPS de menor a mayor");

        reportar("Historial vacio al crear", autauga != null && autauga.getHistorial().estaVacia(),
                "el historial se llena despues, al transformar los casos");

        ArbolBST<IndiceCondado> vacio = IndiceCondado.construirArbol(new ListaEnlazada<>());
        reportar("Lista vacia da arbol vacio",
                vacio.estaVacio() && IndiceCondado.buscar(vacio, "01001") == null,
                "sin condados el arbol deberia quedar vacio");

        System.out.println();
        System.out.println("Pruebas OK: " + pruebasOk);
        System.out.println("Pruebas con FALLA: " + pruebasFalla);
    }

    private static void reportar(String nombrePrueba, boolean condicion, String motivoSiFalla) {
        if (condicion) {
            System.out.println(nombrePrueba + ": OK");
            pruebasOk++;
        } else {
            System.out.println(nombrePrueba + ": FALLA - " + motivoSiFalla);
            pruebasFalla++;
        }
    }
}
