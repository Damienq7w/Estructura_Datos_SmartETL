import estructuras.ArbolBST;
import estructuras.ListaEnlazada;

public class PruebaArbolBST {

    private static int pruebasOk = 0;
    private static int pruebasFalla = 0;

    public static void main(String[] args) {
        probarArbolVacio();
        probarInsertarUno();
        probarInsertarVarios();
        probarBuscarExistente();
        probarBuscarInexistente();
        probarInsertarRepetido();
        probarComparaciones();
        probarConteoCoincide();
        probarInsertarOrdenado();
        probarClavesDeTexto();
        probarInsertarNulo();
        probarRecorridosVacio();
        probarInOrden();
        probarPreOrden();
        probarPostOrden();
        probarInOrdenSiempreOrdenado();
        probarRecorridosArbolDegenerado();

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

    // arbol de ejemplo usado en varias pruebas:
    //
    //            50
    //          /    \
    //        30      70
    //       /  \    /  \
    //     20   40  60   80
    private static ArbolBST<Integer> arbolDeEjemplo() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        int[] claves = { 50, 30, 70, 20, 40, 60, 80 };
        for (int clave : claves) {
            arbol.insertar(clave);
        }
        return arbol;
    }

    private static void probarArbolVacio() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        reportar("Arbol vacio",
                arbol.estaVacio() && arbol.tamanio() == 0 && arbol.contarNodos() == 0
                        && arbol.buscar(10) == null,
                "un arbol recien creado deberia estar vacio, con 0 nodos y sin encontrar nada");
    }

    private static void probarInsertarUno() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        boolean insertado = arbol.insertar(50);
        reportar("Insertar un elemento",
                insertado && !arbol.estaVacio() && arbol.contarNodos() == 1 && arbol.buscar(50) == 50,
                "despues de insertar 50 deberia haber 1 nodo y encontrarse el 50");
    }

    private static void probarInsertarVarios() {
        ArbolBST<Integer> arbol = arbolDeEjemplo();
        reportar("Insertar varios elementos", arbol.contarNodos() == 7,
                "despues de insertar 7 claves distintas deberia haber 7 nodos");
    }

    private static void probarBuscarExistente() {
        ArbolBST<Integer> arbol = arbolDeEjemplo();
        boolean todos = true;
        int[] claves = { 50, 30, 70, 20, 40, 60, 80 };
        for (int clave : claves) {
            Integer encontrado = arbol.buscar(clave);
            if (encontrado == null || encontrado != clave) {
                todos = false;
            }
        }
        reportar("Buscar existentes", todos, "todas las claves insertadas deberian encontrarse");
    }

    private static void probarBuscarInexistente() {
        ArbolBST<Integer> arbol = arbolDeEjemplo();
        reportar("Buscar inexistente",
                arbol.buscar(10) == null && arbol.buscar(55) == null && arbol.buscar(99) == null,
                "una clave que no se inserto deberia devolver null");
    }

    private static void probarInsertarRepetido() {
        ArbolBST<Integer> arbol = arbolDeEjemplo();
        boolean insertado = arbol.insertar(40);
        reportar("Insertar repetido", !insertado && arbol.contarNodos() == 7,
                "insertar una clave que ya existe deberia devolver false y no cambiar el tamanio");
    }

    private static void probarComparaciones() {
        ArbolBST<Integer> arbol = arbolDeEjemplo();

        arbol.buscar(50);
        int enRaiz = arbol.getComparaciones();

        arbol.buscar(80);
        int enHoja = arbol.getComparaciones();

        arbol.buscar(99);
        int inexistente = arbol.getComparaciones();

        reportar("Conteo de comparaciones", enRaiz == 1 && enHoja == 3 && inexistente == 3,
                "la raiz deberia costar 1 comparacion y una hoja del tercer nivel 3; salio "
                        + enRaiz + ", " + enHoja + " y " + inexistente);
    }

    private static void probarConteoCoincide() {
        ArbolBST<Integer> arbol = arbolDeEjemplo();
        arbol.insertar(40);
        arbol.insertar(65);
        reportar("contarNodos coincide con tamanio", arbol.contarNodos() == arbol.tamanio(),
                "el recorrido cuenta " + arbol.contarNodos() + " y el contador interno " + arbol.tamanio());
    }

    // insertar claves ya ordenadas deja el arbol como una lista:
    // buscar la ultima cuesta tantas comparaciones como nodos hay
    private static void probarInsertarOrdenado() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        int total = 5000;
        for (int i = 1; i <= total; i++) {
            arbol.insertar(i);
        }
        arbol.buscar(total);
        reportar("Insertar en orden (arbol degenerado)",
                arbol.contarNodos() == total && arbol.getComparaciones() == total,
                "con " + total + " claves ordenadas, buscar la ultima deberia costar " + total
                        + " comparaciones; salio " + arbol.getComparaciones());
    }

    // los FIPS son texto de 5 digitos: el cero inicial debe conservarse
    private static void probarClavesDeTexto() {
        ArbolBST<String> arbol = new ArbolBST<>();
        arbol.insertar("06037");
        arbol.insertar("01001");
        arbol.insertar("48201");
        reportar("Claves de texto (FIPS)",
                arbol.contarNodos() == 3 && "01001".equals(arbol.buscar("01001"))
                        && arbol.buscar("1001") == null,
                "01001 deberia encontrarse y 1001 no: son textos distintos");
    }

    // compara una lista con los valores esperados, en el mismo orden
    private static boolean coincide(ListaEnlazada<Integer> lista, int[] esperado) {
        if (lista.tamanio() != esperado.length) {
            return false;
        }
        int posicion = 0;
        for (int valor : lista) {
            if (valor != esperado[posicion]) {
                return false;
            }
            posicion++;
        }
        return true;
    }

    private static String texto(ListaEnlazada<Integer> lista) {
        StringBuilder sb = new StringBuilder();
        for (int valor : lista) {
            sb.append(valor).append(' ');
        }
        return sb.toString().trim();
    }

    private static void probarRecorridosVacio() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        reportar("Recorridos de arbol vacio",
                arbol.inOrden().estaVacia() && arbol.preOrden().estaVacia() && arbol.postOrden().estaVacia(),
                "los tres recorridos de un arbol vacio deberian devolver una lista vacia");
    }

    private static void probarInOrden() {
        ListaEnlazada<Integer> recorrido = arbolDeEjemplo().inOrden();
        reportar("InOrden", coincide(recorrido, new int[] { 20, 30, 40, 50, 60, 70, 80 }),
                "se esperaba 20 30 40 50 60 70 80 y salio " + texto(recorrido));
    }

    private static void probarPreOrden() {
        ListaEnlazada<Integer> recorrido = arbolDeEjemplo().preOrden();
        reportar("PreOrden", coincide(recorrido, new int[] { 50, 30, 20, 40, 70, 60, 80 }),
                "se esperaba 50 30 20 40 70 60 80 y salio " + texto(recorrido));
    }

    private static void probarPostOrden() {
        ListaEnlazada<Integer> recorrido = arbolDeEjemplo().postOrden();
        reportar("PostOrden", coincide(recorrido, new int[] { 20, 40, 30, 60, 80, 70, 50 }),
                "se esperaba 20 40 30 60 80 70 50 y salio " + texto(recorrido));
    }

    // propiedad del arbol de busqueda: sin importar el orden de insercion,
    // el inOrden sale de menor a mayor
    private static void probarInOrdenSiempreOrdenado() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        int valor = 7;
        for (int i = 0; i < 2000; i++) {
            valor = (valor * 31 + 17) % 10007;
            arbol.insertar(valor);
        }

        ListaEnlazada<Integer> recorrido = arbol.inOrden();
        boolean ordenado = true;
        Integer anterior = null;
        for (int actual : recorrido) {
            if (anterior != null && anterior >= actual) {
                ordenado = false;
            }
            anterior = actual;
        }
        reportar("InOrden siempre ordenado", ordenado && recorrido.tamanio() == arbol.contarNodos(),
                "el inOrden deberia salir de menor a mayor y con tantos elementos como nodos");
    }

    // el catalogo real tiene unos 3.200 condados y llega ordenado:
    // los recorridos deben aguantar un arbol de esa profundidad
    private static void probarRecorridosArbolDegenerado() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        int total = 3300;
        for (int i = 1; i <= total; i++) {
            arbol.insertar(i);
        }
        reportar("Recorridos en arbol degenerado",
                arbol.inOrden().tamanio() == total && arbol.preOrden().tamanio() == total
                        && arbol.postOrden().tamanio() == total,
                "los tres recorridos deberian devolver los " + total + " elementos");
    }

    private static void probarInsertarNulo() {
        ArbolBST<Integer> arbol = new ArbolBST<>();
        boolean lanzo = false;
        try {
            arbol.insertar(null);
        } catch (IllegalArgumentException e) {
            lanzo = true;
        }
        reportar("Insertar nulo", lanzo && arbol.estaVacio(),
                "insertar null deberia lanzar IllegalArgumentException y no cambiar el arbol");
    }
}
