import estructuras.ListaSecuencial;

public class PruebaListaSecuencial {

    private static int pruebasOk = 0;
    private static int pruebasFalla = 0;

    public static void main(String[] args) {
        probarEstablecer();
        probarTamanioNoCambia();
        probarIndicesInvalidos();

        System.out.println();
        System.out.println("Pruebas OK: " + pruebasOk);
        System.out.println("Pruebas con FALLA: " + pruebasFalla);

        if (pruebasFalla > 0) {
            throw new AssertionError("Fallaron pruebas de ListaSecuencial");
        }
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

    private static ListaSecuencial<String> crearLista() {
        ListaSecuencial<String> lista = new ListaSecuencial<>();
        lista.insertar("A");
        lista.insertar("B");
        lista.insertar("C");
        return lista;
    }

    private static void probarEstablecer() {
        ListaSecuencial<String> lista = crearLista();
        lista.establecer(1, "X");

        reportar(
            "Establecer reemplaza una posicion",
            "A".equals(lista.obtener(0))
                && "X".equals(lista.obtener(1))
                && "C".equals(lista.obtener(2)),
            "solo la posicion 1 debe cambiar de B a X"
        );
    }

    private static void probarTamanioNoCambia() {
        ListaSecuencial<String> lista = crearLista();
        int tamanioAntes = lista.tamanio();

        lista.establecer(0, "Nuevo");

        reportar(
            "Establecer conserva el tamanio",
            lista.tamanio() == tamanioAntes
                && "Nuevo".equals(lista.obtener(0)),
            "reemplazar no debe insertar ni eliminar elementos"
        );
    }

    private static void probarIndicesInvalidos() {
        ListaSecuencial<String> lista = crearLista();

        boolean negativoLanzaExcepcion = false;
        boolean fueraDeRangoLanzaExcepcion = false;
        boolean obtenerFueraDeRangoLanzaExcepcion = false;

        try {
            lista.establecer(-1, "X");
        } catch (IndexOutOfBoundsException e) {
            negativoLanzaExcepcion = true;
        }

        try {
            lista.establecer(3, "X");
        } catch (IndexOutOfBoundsException e) {
            fueraDeRangoLanzaExcepcion = true;
        }

        try {
            lista.obtener(3);
        } catch (IndexOutOfBoundsException e) {
            obtenerFueraDeRangoLanzaExcepcion = true;
        }

        reportar(
            "Indices invalidos lanzan excepcion",
            negativoLanzaExcepcion
                && fueraDeRangoLanzaExcepcion
                && obtenerFueraDeRangoLanzaExcepcion,
            "obtener y establecer deben rechazar posiciones invalidas"
        );
    }
}