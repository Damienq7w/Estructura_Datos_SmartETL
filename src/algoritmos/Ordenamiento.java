package algoritmos;

import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import model.CasoDiario;

/**
 * Algoritmos de ordenamiento propios para ListaSecuencial.
 * Cuenta comparaciones e intercambios de cada ejecucion.
 */
public class Ordenamiento {

    private long comparaciones;
    private long intercambios;

    /**
     * Ordenamiento por insercion.
     * Recorre de izquierda a derecha e inserta cada dato en su posicion.
     */
    public <T> void insercion(
            ListaSecuencial<T> lista,
            Comparador<? super T> comparador
    ) {
        validarArgumentos(lista, comparador);
        reiniciarContadores();

        for (int i = 1; i < lista.tamanio(); i++) {
            int j = i;

            while (j > 0) {
                comparaciones++;

                if (comparador.comparar(
                        lista.obtener(j - 1),
                        lista.obtener(j)
                    ) <= 0) {
                    break;
                }

                intercambiar(lista, j - 1, j);
                j--;
            }
        }
    }

    /**
     * Ordenamiento por seleccion.
     * Busca el menor elemento restante y lo coloca al inicio.
     */
    public <T> void seleccion(
            ListaSecuencial<T> lista,
            Comparador<? super T> comparador
    ) {
        validarArgumentos(lista, comparador);
        reiniciarContadores();

        for (int i = 0; i < lista.tamanio() - 1; i++) {
            int posicionMenor = i;

            for (int j = i + 1; j < lista.tamanio(); j++) {
                comparaciones++;

                if (comparador.comparar(
                        lista.obtener(j),
                        lista.obtener(posicionMenor)
                    ) < 0) {
                    posicionMenor = j;
                }
            }

            if (posicionMenor != i) {
                intercambiar(lista, i, posicionMenor);
            }
        }
    }

    /**
     * Copia los casos de una fecha y los ordena de mayor a menor por casos.
     * La lista original no se modifica.
     */
    public ListaSecuencial<CasoDiario> rankingPorCasosEnFecha(
            ListaEnlazada<CasoDiario> casosValidos,
            String fecha
    ) {
        if (casosValidos == null || fecha == null) {
            throw new IllegalArgumentException(
                "La lista de casos y la fecha son obligatorias"
            );
        }

        ListaSecuencial<CasoDiario> ranking = new ListaSecuencial<>();

        for (CasoDiario caso : casosValidos) {
            if (fecha.equals(caso.getFecha())) {
                ranking.insertar(caso);
            }
        }

        insercion(ranking, (casoA, casoB) -> {
            int comparacionPorCasos = Integer.compare(
                casoB.getCasos(),
                casoA.getCasos()
            );

            if (comparacionPorCasos != 0) {
                return comparacionPorCasos;
            }

            return casoA.getFips().compareTo(casoB.getFips());
        });

        return ranking;
    }

    public long getComparaciones() {
        return comparaciones;
    }

    public long getIntercambios() {
        return intercambios;
    }

    private void reiniciarContadores() {
        comparaciones = 0;
        intercambios = 0;
    }

    private static <T> void validarArgumentos(
            ListaSecuencial<T> lista,
            Comparador<? super T> comparador
    ) {
        if (lista == null || comparador == null) {
            throw new IllegalArgumentException(
                "La lista y el comparador son obligatorios"
            );
        }
    }

    private <T> void intercambiar(ListaSecuencial<T> lista, int posicionA, int posicionB) {
        T temporal = lista.obtener(posicionA);

        lista.establecer(posicionA, lista.obtener(posicionB));
        lista.establecer(posicionB, temporal);

        intercambios++;
    }
}