import estructuras.Cola;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PruebaCola {
    private static int ok, fallas;

    private static void comprobar(String nombre, boolean resultado) {
        System.out.println(nombre + (resultado ? ": OK" : ": FALLA"));
        if (resultado) ok++; else fallas++;
    }

    public static void main(String[] args) {
        Cola<Integer> cola = new Cola<>();
        comprobar("Recien creada", cola.estaVacia() && cola.tamanio() == 0);
        cola.encolar(10);
        comprobar("Un elemento", !cola.estaVacia() && cola.tamanio() == 1 && cola.frente() == 10);
        int antes = cola.tamanio();
        cola.frente();
        comprobar("Frente no retira", cola.tamanio() == antes);
        cola.desencolar();
        boolean vacia = cola.estaVacia() && cola.tamanio() == 0;
        cola.encolar(20);
        comprobar("Reutilizar tras vaciar", vacia && cola.desencolar() == 20 && cola.estaVacia());
        for (int i = 0; i < 5; i++) cola.encolar(i);
        boolean orden = true;
        for (int i = 0; i < 5; i++) orden &= cola.desencolar() == i;
        comprobar("FIFO cinco elementos", orden && cola.estaVacia());
        boolean excepcion = false;
        try { cola.desencolar(); } catch (IllegalStateException e) { excepcion = true; }
        comprobar("Desencolar vacia", excepcion);
        excepcion = false;
        try { cola.frente(); } catch (IllegalStateException e) { excepcion = true; }
        comprobar("Frente vacia", excepcion);

        long inicio = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) cola.encolar(i);
        orden = cola.tamanio() == 1_000_000;
        for (int i = 0; i < 1_000_000; i++) orden &= cola.desencolar() == i;
        long milisegundos = (System.nanoTime() - inicio) / 1_000_000;
        comprobar("Un millon FIFO", orden && cola.estaVacia() && cola.tamanio() == 0);
        System.out.println("Tiempo del millon: " + milisegundos + " ms (referencia: 1-2 segundos)");

        cola.encolar(7);
        cola.encolar(8);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try (PrintStream captura = new PrintStream(salida)) {
            System.setOut(captura);
            cola.mostrar(1);
            cola.mostrar(0);
            cola.mostrar(-1);
        } finally { System.setOut(original); }
        comprobar("Mostrar respeta limite", salida.toString().trim().equals("7") && cola.tamanio() == 2);
        System.out.println("Pruebas OK: " + ok + "; pruebas con FALLA: " + fallas);
        if (fallas > 0) throw new AssertionError("Fallaron pruebas de Cola");
    }
}
