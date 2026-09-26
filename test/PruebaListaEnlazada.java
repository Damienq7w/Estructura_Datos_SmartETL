import estructuras.ListaEnlazada;

public class PruebaListaEnlazada {

    private static int pruebasOk = 0;
    private static int pruebasFalla = 0;

    public static void main(String[] args) {
        probarListaVacia();
        probarInsertarUno();
        probarInsertarVarios();
        probarObtener();
        probarEliminarPrimero();
        probarEliminarMedio();
        probarEliminarUltimo();
        probarEliminarInexistente();
        probarTamanioFinal();

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

    private static void probarListaVacia() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        reportar("Lista vacia", lista.estaVacia() && lista.tamanio() == 0,
                "una lista recien creada deberia estar vacia y con tamanio 0");
    }

    private static void probarInsertarUno() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("Ana");
        reportar("Insertar un elemento", lista.tamanio() == 1 && lista.obtener(0).equals("Ana"),
                "despues de insertar Ana el tamanio deberia ser 1 y la posicion 0 deberia ser Ana");
    }

    private static void probarInsertarVarios() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("Ana");
        lista.insertar("Luis");
        lista.insertar("Zoe");
        reportar("Insertar varios elementos",
                lista.tamanio() == 3 && lista.obtener(0).equals("Ana") && lista.obtener(1).equals("Luis")
                        && lista.obtener(2).equals("Zoe"),
                "el orden deberia respetar la insercion: Ana, Luis, Zoe");
    }

    private static void probarObtener() {
        ListaEnlazada<Integer> lista = new ListaEnlazada<>();
        lista.insertar(10);
        lista.insertar(20);
        lista.insertar(30);
        reportar("Obtener por posicion", lista.obtener(1) == 20,
                "la posicion 1 deberia ser 20");
    }

    private static void probarEliminarPrimero() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("Ana");
        lista.insertar("Luis");
        lista.insertar("Zoe");
        boolean elimino = lista.eliminar("Ana");
        reportar("Eliminar el primero",
                elimino && lista.tamanio() == 2 && lista.obtener(0).equals("Luis"),
                "al eliminar Ana el tamanio deberia bajar a 2 y Luis deberia quedar primero");
    }

    private static void probarEliminarMedio() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("Ana");
        lista.insertar("Luis");
        lista.insertar("Zoe");
        boolean elimino = lista.eliminar("Luis");
        reportar("Eliminar del medio",
                elimino && lista.tamanio() == 2 && lista.obtener(0).equals("Ana") && lista.obtener(1).equals("Zoe"),
                "al eliminar Luis deberian quedar Ana y Zoe en ese orden");
    }

    private static void probarEliminarUltimo() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("Ana");
        lista.insertar("Luis");
        lista.insertar("Zoe");
        boolean elimino = lista.eliminar("Zoe");
        lista.insertar("Nuevo");
        reportar("Eliminar el ultimo",
                elimino && lista.tamanio() == 3 && lista.obtener(2).equals("Nuevo"),
                "al eliminar Zoe la cola deberia quedar bien actualizada para poder insertar Nuevo al final");
    }

    private static void probarEliminarInexistente() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("Ana");
        lista.insertar("Luis");
        boolean elimino = lista.eliminar("Carlos");
        reportar("Eliminar algo que no existe",
                !elimino && lista.tamanio() == 2,
                "eliminar un dato que no esta no deberia cambiar el tamanio ni lanzar error");
    }

    private static void probarTamanioFinal() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("A");
        lista.insertar("B");
        lista.insertar("C");
        lista.eliminar("B");
        lista.insertar("D");
        reportar("Tamanio correcto despues de todo",
                lista.tamanio() == 3,
                "tras insertar 3, eliminar 1 e insertar 1 mas, el tamanio deberia ser 3");
    }
}