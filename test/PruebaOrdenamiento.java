import algoritmos.Comparador;
import algoritmos.Ordenamiento;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import model.CasoDiario;

public class PruebaOrdenamiento {

    private static final Comparador<Integer> COMPARADOR_NUMEROS = Integer::compare;
    private static int pruebasOk = 0;

    public static void main(String[] args) {
        int[][] entradas = {
            {},
            {7},
            {2, 1},
            {1, 2, 3, 4, 5},
            {5, 4, 3, 2, 1},
            {3, 1, 3, 2, 1}
        };

        int[][] esperados = {
            {},
            {7},
            {1, 2},
            {1, 2, 3, 4, 5},
            {1, 2, 3, 4, 5},
            {1, 1, 2, 3, 3}
        };

        for (int i = 0; i < entradas.length; i++) {
            probarOrdenamiento(entradas[i], esperados[i], false);
            probarOrdenamiento(entradas[i], esperados[i], true);
        }

        probarConteos();
        probarComparadorConTexto();
        probarRankingPorFecha();

        System.out.println();
        System.out.println("Pruebas OK: " + pruebasOk);
    }

    private static void probarOrdenamiento(
            int[] entrada,
            int[] esperado,
            boolean usarSeleccion
    ) {
        ListaSecuencial<Integer> lista = crearLista(entrada);
        Ordenamiento ordenamiento = new Ordenamiento();

        if (usarSeleccion) {
            ordenamiento.seleccion(lista, COMPARADOR_NUMEROS);
        } else {
            ordenamiento.insercion(lista, COMPARADOR_NUMEROS);
        }

        comprobar(
            lista.tamanio() == esperado.length,
            "El ordenamiento conserva el tamanio"
        );

        for (int i = 0; i < esperado.length; i++) {
            comprobar(
                lista.obtener(i) == esperado[i],
                "Orden y datos esperados"
            );
        }

        if (entrada.length > 1) {
            comprobar(
                ordenamiento.getComparaciones() > 0,
                "El algoritmo cuenta comparaciones"
            );
        }

        System.out.println(
            (usarSeleccion ? "Seleccion" : "Insercion")
                + " con "
                + entrada.length
                + " elementos: OK"
        );
    }

    private static void probarConteos() {
        ListaSecuencial<Integer> ordenada = crearLista(1, 2, 3, 4, 5);
        Ordenamiento ordenamiento = new Ordenamiento();

        ordenamiento.insercion(ordenada, COMPARADOR_NUMEROS);

        comprobar(
            ordenamiento.getComparaciones() == 4
                && ordenamiento.getIntercambios() == 0,
            "Insercion aprovecha datos ya ordenados"
        );

        ordenamiento.seleccion(ordenada, COMPARADOR_NUMEROS);

        comprobar(
            ordenamiento.getComparaciones() == 10
                && ordenamiento.getIntercambios() == 0,
            "Seleccion compara todos los elementos restantes"
        );

        ListaSecuencial<Integer> inversa = crearLista(5, 4, 3, 2, 1);

        ordenamiento.insercion(inversa, COMPARADOR_NUMEROS);

        comprobar(
            ordenamiento.getComparaciones() == 10
                && ordenamiento.getIntercambios() == 10,
            "Insercion cuenta intercambios en entrada inversa"
        );
    }

    private static void probarComparadorConTexto() {
        ListaSecuencial<String> nombres = new ListaSecuencial<>();
        nombres.insertar("Zoe");
        nombres.insertar("Ana");
        nombres.insertar("Luis");

        Ordenamiento ordenamiento = new Ordenamiento();
        ordenamiento.seleccion(nombres, String::compareTo);

        comprobar(
            "Ana".equals(nombres.obtener(0))
                && "Luis".equals(nombres.obtener(1))
                && "Zoe".equals(nombres.obtener(2)),
            "El comparador propio permite ordenar texto"
        );
    }

    private static void probarRankingPorFecha() {
        ListaEnlazada<CasoDiario> casos = new ListaEnlazada<>();

        casos.insertar(crearCaso("2021-01-01", "A", "01001", 20));
        casos.insertar(crearCaso("2021-01-02", "B", "01003", 100));
        casos.insertar(crearCaso("2021-01-01", "C", "01005", 30));
        casos.insertar(crearCaso("2021-01-01", "D", "01002", 30));

        Ordenamiento ordenamiento = new Ordenamiento();

        ListaSecuencial<CasoDiario> ranking =
            ordenamiento.rankingPorCasosEnFecha(casos, "2021-01-01");

        comprobar(
            ranking.tamanio() == 3
                && "01002".equals(ranking.obtener(0).getFips())
                && "01005".equals(ranking.obtener(1).getFips())
                && "01001".equals(ranking.obtener(2).getFips()),
            "Ranking filtra y ordena por casos de mayor a menor"
        );

        comprobar(
            "01001".equals(casos.obtener(0).getFips())
                && "01002".equals(casos.ultimo().getFips()),
            "Ranking no modifica la lista original"
        );
    }

    private static ListaSecuencial<Integer> crearLista(int... datos) {
        ListaSecuencial<Integer> lista = new ListaSecuencial<>();

        for (int dato : datos) {
            lista.insertar(dato);
        }

        return lista;
    }

    private static CasoDiario crearCaso(
            String fecha,
            String condado,
            String fips,
            int cantidadCasos
    ) {
        return new CasoDiario(
            fecha,
            condado,
            "AL",
            fips,
            cantidadCasos,
            0
        );
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }

        pruebasOk++;
    }
}