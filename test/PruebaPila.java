import estructuras.Pila;

public class PruebaPila {

    private static int pruebasOk = 0;
    private static int pruebasFalla = 0;

    public static void main(String[] args) {
        probarPilaVacia();
        probarApilarUno();
        probarOrdenLifo();
        probarReutilizarPila();
        probarDesapilarVacia();
        probarCimaVacia();
        probarCimaNoElimina();
        probarIterador();
        probarUnMillonOperaciones();

        System.out.println();
        System.out.println("Pruebas OK: " + pruebasOk);
        System.out.println("Pruebas con FALLA: " + pruebasFalla);

        if (pruebasFalla > 0) {
            throw new AssertionError("Fallaron pruebas de Pila");
        }
    }

    private static void reportar(String nombre, boolean condicion, String motivo) {
        if (condicion) {
            System.out.println(nombre + ": OK");
            pruebasOk++;
        } else {
            System.out.println(nombre + ": FALLA - " + motivo);
            pruebasFalla++;
        }
    }

    private static void probarPilaVacia() {
        Pila<Integer> pila = new Pila<>();

        reportar(
            "Pila vacia",
            pila.estaVacia() && pila.tamanio() == 0,
            "una pila nueva debe estar vacia y tener tamanio 0"
        );
    }

    private static void probarApilarUno() {
        Pila<String> pila = new Pila<>();
        pila.apilar("Error 1");

        reportar(
            "Apilar un elemento",
            !pila.estaVacia()
                && pila.tamanio() == 1
                && "Error 1".equals(pila.cima()),
            "la cima debe ser Error 1 y el tamanio debe ser 1"
        );
    }

    private static void probarOrdenLifo() {
        Pila<Integer> pila = new Pila<>();

        for (int i = 1; i <= 5; i++) {
            pila.apilar(i);
        }

        boolean correcto = true;

        for (int esperado = 5; esperado >= 1; esperado--) {
            if (pila.desapilar() != esperado) {
                correcto = false;
                break;
            }
        }

        reportar(
            "Orden LIFO con cinco elementos",
            correcto && pila.estaVacia() && pila.tamanio() == 0,
            "debe salir 5, 4, 3, 2, 1"
        );
    }

    private static void probarReutilizarPila() {
        Pila<String> pila = new Pila<>();

        pila.apilar("Primero");
        boolean sacoPrimero = "Primero".equals(pila.desapilar());

        pila.apilar("Nuevo");
        boolean sacoNuevo = "Nuevo".equals(pila.desapilar());

        reportar(
            "Reutilizar pila despues de vaciar",
            sacoPrimero && sacoNuevo && pila.estaVacia(),
            "la pila debe funcionar despues de quedar vacia"
        );
    }

    private static void probarDesapilarVacia() {
        Pila<Integer> pila = new Pila<>();
        boolean lanzoExcepcion = false;

        try {
            pila.desapilar();
        } catch (IllegalStateException e) {
            lanzoExcepcion = true;
        }

        reportar(
            "Desapilar pila vacia",
            lanzoExcepcion,
            "desapilar debe lanzar IllegalStateException"
        );
    }

    private static void probarCimaVacia() {
        Pila<Integer> pila = new Pila<>();
        boolean lanzoExcepcion = false;

        try {
            pila.cima();
        } catch (IllegalStateException e) {
            lanzoExcepcion = true;
        }

        reportar(
            "Cima de pila vacia",
            lanzoExcepcion,
            "cima debe lanzar IllegalStateException"
        );
    }

    private static void probarCimaNoElimina() {
        Pila<Integer> pila = new Pila<>();
        pila.apilar(10);

        int primeraConsulta = pila.cima();
        int segundaConsulta = pila.cima();

        reportar(
            "Cima no elimina el dato",
            primeraConsulta == 10
                && segundaConsulta == 10
                && pila.tamanio() == 1,
            "consultar cima no debe cambiar el tamanio"
        );
    }

    private static void probarIterador() {
        Pila<Integer> pila = new Pila<>();

        for (int i = 1; i <= 5; i++) {
            pila.apilar(i);
        }

        int esperado = 5;
        boolean correcto = true;

        for (int dato : pila) {
            if (dato != esperado) {
                correcto = false;
                break;
            }
            esperado--;
        }

        reportar(
            "Iterador recorre sin vaciar",
            correcto
                && esperado == 0
                && pila.tamanio() == 5
                && pila.cima() == 5,
            "debe recorrer 5, 4, 3, 2, 1 sin modificar la pila"
        );
    }

    private static void probarUnMillonOperaciones() {
        Pila<Integer> pila = new Pila<>();

        long inicio = System.nanoTime();

        for (int i = 0; i < 1_000_000; i++) {
            pila.apilar(i);
        }

        boolean correcto = pila.tamanio() == 1_000_000;

        for (int i = 999_999; i >= 0 && correcto; i--) {
            if (pila.desapilar() != i) {
                correcto = false;
            }
        }

        double segundos = (System.nanoTime() - inicio) / 1_000_000_000.0;

        System.out.printf("  Duracion medida: %.3f s%n", segundos);

        reportar(
            "Un millon de apilar y desapilar",
            correcto && pila.estaVacia() && pila.tamanio() == 0,
            "la pila debe conservar el orden y terminar vacia"
        );
    }
}