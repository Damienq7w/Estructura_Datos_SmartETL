import algoritmos.Ordenamiento;
import estructuras.ListaSecuencial;

public class ComparativaOrdenamiento {

    public static void main(String[] args) {
        int[] tamanios = {100, 1000, 3250, 10000};

        System.out.println(
            "| Cantidad | Entrada | Algoritmo | Comparaciones | "
                + "Intercambios | Mediana aproximada (ms) |"
        );
        System.out.println(
            "| ---: | --- | --- | ---: | ---: | ---: |"
        );

        for (int tamanio : tamanios) {
            medir(tamanio, false, false);
            medir(tamanio, false, true);

            medir(tamanio, true, false);
            medir(tamanio, true, true);
        }
    }

    private static void medir(
            int tamanio,
            boolean inversa,
            boolean usarSeleccion
    ) {
        Ordenamiento ordenamiento = new Ordenamiento();
        double[] tiempos = new double[3];

        // -1 es calentamiento: no se usa para la medicion final.
        for (int repeticion = -1; repeticion < 3; repeticion++) {
            ListaSecuencial<Integer> lista = crearLista(tamanio, inversa);

            long inicio = System.nanoTime();

            if (usarSeleccion) {
                ordenamiento.seleccion(lista, Integer::compare);
            } else {
                ordenamiento.insercion(lista, Integer::compare);
            }

            double milisegundos =
                (System.nanoTime() - inicio) / 1_000_000.0;

            verificarOrdenada(lista, tamanio);

            if (repeticion >= 0) {
                tiempos[repeticion] = milisegundos;
            }
        }

        double mediana = calcularMediana(
            tiempos[0],
            tiempos[1],
            tiempos[2]
        );

        String tipoEntrada = inversa ? "Inversa" : "Ordenada";
        String algoritmo = usarSeleccion ? "Seleccion" : "Insercion";

        System.out.printf(
            "| %,d | %s | %s | %,d | %,d | %.3f |%n",
            tamanio,
            tipoEntrada,
            algoritmo,
            ordenamiento.getComparaciones(),
            ordenamiento.getIntercambios(),
            mediana
        );
    }

    private static ListaSecuencial<Integer> crearLista(
            int tamanio,
            boolean inversa
    ) {
        ListaSecuencial<Integer> lista = new ListaSecuencial<>();

        for (int i = 0; i < tamanio; i++) {
            int valor;

            if (inversa) {
                valor = tamanio - i;
            } else {
                valor = i + 1;
            }

            lista.insertar(valor);
        }

        return lista;
    }

    private static void verificarOrdenada(
            ListaSecuencial<Integer> lista,
            int tamanioEsperado
    ) {
        if (lista.tamanio() != tamanioEsperado) {
            throw new AssertionError("El ordenamiento cambio el tamanio");
        }

        for (int i = 1; i < lista.tamanio(); i++) {
            if (lista.obtener(i - 1) > lista.obtener(i)) {
                throw new AssertionError(
                    "La lista no quedo ordenada en la posicion " + i
                );
            }
        }
    }

    private static double calcularMediana(
            double primero,
            double segundo,
            double tercero
    ) {
        double menor = Math.min(
            primero,
            Math.min(segundo, tercero)
        );

        double mayor = Math.max(
            primero,
            Math.max(segundo, tercero)
        );

        return primero + segundo + tercero - menor - mayor;
    }
}