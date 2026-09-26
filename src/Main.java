import algoritmos.Busqueda;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import etl.Extract;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import model.CasoDiario;
import model.Condado;
import model.Esquema;
import model.Registro;
import model.Vecindad;

public class Main {

    private static final String RUTA_CASOS = "data/casos_diarios.csv";
    private static final String RUTA_CONDADOS = "data/condados.csv";
    private static final String RUTA_VECINDAD = "data/vecindad.tsv";

    private static final Extract extract = new Extract();

    private static ListaEnlazada<CasoDiario> casos;
    private static ListaEnlazada<Condado> condados;
    private static ListaEnlazada<Vecindad> vecindad;

    public static void main(String[] args) {
        if (!verificarArchivos()) {
            return;
        }

        Scanner teclado = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            opcion = leerOpcion(teclado);

            switch (opcion) {
                case 1:
                    cargarCasos();
                    break;
                case 2:
                    mostrarRegistros();
                    break;
                case 3:
                    buscarPorFipsYFecha(teclado);
                    break;
                case 4:
                    buscarPorNombre(teclado);
                    break;
                case 5:
                    mostrarIncidencias();
                    break;
                case 6:
                    mostrarEstadisticas();
                    break;
                case 7:
                    verCondadosYVecindad();
                    break;
                case 8:
                    compararListas();
                    break;
                case 9:
                    mostrarEsquema();
                    break;
                case 0:
                    System.out.println("Hasta luego");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }

        teclado.close();
    }

    private static boolean verificarArchivos() {
        String[] rutas = { RUTA_CASOS, RUTA_CONDADOS, RUTA_VECINDAD };

        for (String ruta : rutas) {
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                System.out.println("Falta el archivo: " + ruta);
                System.out.println("Se busco en: " + archivo.getAbsolutePath());
                System.out.println("Ejecute: python tools/preparar_base.py");
                return false;
            }
        }

        return true;
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println(" 1. Cargar casos diarios (Extract)");
        System.out.println(" 2. Mostrar registros");
        System.out.println(" 3. Buscar por codigo FIPS y fecha");
        System.out.println(" 4. Buscar por nombre de condado");
        System.out.println(" 5. Ver incidencias de lectura");
        System.out.println(" 6. Estadisticas de la base");
        System.out.println(" 7. Ver condados y vecindad");
        System.out.println(" 8. Comparar ListaEnlazada vs ListaSecuencial");
        System.out.println(" 9. Ver esquema fijo de la base");
        System.out.println(" 0. Salir");
        System.out.print("Elija una opcion: ");
    }

    private static int leerOpcion(Scanner teclado) {
        String entrada = teclado.nextLine().trim();
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void cargarCasos() {
        if (casos != null) {
            System.out.println("Los casos ya estaban cargados, no se vuelven a leer");
            return;
        }

        long inicio = System.currentTimeMillis();
        try {
            casos = extract.cargarCasos(RUTA_CASOS);
        } catch (IOException e) {
            System.out.println("Error leyendo " + RUTA_CASOS + ": " + e.getMessage());
            return;
        }
        long tiempoMs = System.currentTimeMillis() - inicio;

        System.out.println("Filas leidas: " + extract.getCasosLeidas());
        System.out.println("Filas cargadas: " + extract.getCasosCargadas());
        System.out.println("Filas descartadas: " + extract.getCasosDescartadas());
        System.out.println("Tiempo de carga: " + tiempoMs + " ms");
        System.out.println("Sin FIPS: " + extract.getCasosSinFips());
        System.out.println("Condado Unknown: " + extract.getCasosCondadoUnknown());
        System.out.println("Muertes vacias: " + extract.getCasosMuertesVacias());
        System.out.println("Estos casos se procesaran en el Transform (Hito 2)");
    }

    private static boolean sinCargar() {
        if (casos == null) {
            System.out.println("Primero ejecute la opcion 1");
            return true;
        }
        return false;
    }

    private static void mostrarRegistros() {
        if (sinCargar()) {
            return;
        }
        System.out.println("Total de registros: " + casos.tamanio());
        System.out.println("El archivo viene ordenado por fecha y luego por condado.");
        System.out.println();
        System.out.println("--- Primeros 5 registros ---");
        mostrarPrimeros(casos, 5);
        System.out.println("--- Ultimos 5 registros ---");
        mostrarUltimos(casos, 5);
    }

    // polimorfismo: sirve para CasoDiario, Condado y Vecindad porque todos son Registro
    private static void mostrarPrimeros(ListaEnlazada<? extends Registro> lista, int n) {
        int contador = 0;
        for (Registro r : lista) {
            if (contador >= n) {
                break;
            }
            System.out.println(r.mostrar());
            contador++;
        }
    }

    // recorre una sola vez con el iterador y solo imprime desde la posicion tamanio - n
    private static void mostrarUltimos(ListaEnlazada<? extends Registro> lista, int n) {
        int desde = lista.tamanio() - n;
        int posicion = 0;
        for (Registro r : lista) {
            if (posicion >= desde) {
                System.out.println(r.mostrar());
            }
            posicion++;
        }
    }

    private static void buscarPorFipsYFecha(Scanner teclado) {
        if (sinCargar()) {
            return;
        }
        System.out.print("FIPS: ");
        String fips = teclado.nextLine().trim();
        System.out.print("Fecha (aaaa-mm-dd): ");
        String fecha = teclado.nextLine().trim();

        Busqueda busqueda = new Busqueda();
        CasoDiario encontrado = busqueda.porFipsYFecha(casos, fips, fecha);

        if (encontrado == null) {
            System.out.println("No se encontro ese registro");
        } else {
            System.out.println(encontrado.mostrar());
        }
        System.out.println("Comparaciones realizadas: " + busqueda.getComparaciones());
    }

    private static void buscarPorNombre(Scanner teclado) {
        if (sinCargar()) {
            return;
        }
        System.out.print("Nombre del condado: ");
        String nombre = teclado.nextLine().trim();

        Busqueda busqueda = new Busqueda();
        ListaEnlazada<CasoDiario> encontrados = busqueda.porCondado(casos, nombre);

        System.out.println("Encontrados: " + encontrados.tamanio());
        mostrarPrimeros(encontrados, 10);
        System.out.println("Comparaciones realizadas: " + busqueda.getComparaciones());
    }

    private static void mostrarIncidencias() {
        if (sinCargar()) {
            return;
        }
        System.out.println("Total de incidencias registradas: " + extract.getIncidencias().tamanio());
        extract.getIncidencias().mostrar(10);
    }

    private static void mostrarEstadisticas() {
        if (sinCargar()) {
            return;
        }
        String primeraFecha = null;
        String ultimaFecha = null;
        int fechasDistintas = 0;
        int registrosUltimoDia = 0;

        // como el archivo viene ordenado por fecha, cada cambio de fecha es un dia nuevo
        for (CasoDiario caso : casos) {
            if (!caso.getFecha().equals(ultimaFecha)) {
                fechasDistintas++;
                ultimaFecha = caso.getFecha();
                registrosUltimoDia = 0;
                if (primeraFecha == null) {
                    primeraFecha = caso.getFecha();
                }
            }
            registrosUltimoDia++;
        }

        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long memoriaMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);

        System.out.println("Registros cargados: " + casos.tamanio());
        System.out.println("Fechas distintas: " + fechasDistintas + " (" + primeraFecha + " -> " + ultimaFecha + ")");
        System.out.println("Registros del ultimo dia: " + registrosUltimoDia);
        System.out.println("Sin FIPS: " + extract.getCasosSinFips());
        System.out.println("Condado Unknown: " + extract.getCasosCondadoUnknown());
        System.out.println("Muertes vacias: " + extract.getCasosMuertesVacias());
        System.out.println("Memoria usada por el programa: " + memoriaMb + " MB");
    }

    private static void verCondadosYVecindad() {
        if (condados == null || vecindad == null) {
            try {
                condados = extract.cargarCondados(RUTA_CONDADOS);
                vecindad = extract.cargarVecindad(RUTA_VECINDAD);
            } catch (IOException e) {
                System.out.println("Error leyendo condados/vecindad: " + e.getMessage());
                return;
            }

            System.out.println("Filas leidas de condados.csv: " + extract.getCondadosLeidas());
            System.out.println("Filas cargadas de condados.csv: " + extract.getCondadosCargadas());
            System.out.println("Filas que no son condados: " + extract.getCondadosNoSonCondado());
            System.out.println("FIPS sin cero: " + extract.getCondadosFipsSinCero());
            System.out.println("Filas leidas de vecindad.tsv: " + extract.getVecindadLeidas());
            System.out.println("Filas cargadas de vecindad.tsv: " + extract.getVecindadCargadas());
            System.out.println("Condado vecino de si mismo: " + extract.getVecindadVecinoDeSiMismo());
            System.out.println("Vecindades repetidas: " + extract.getVecindadRepetida());
        }

        System.out.println();
        System.out.println("Condados:");
        mostrarPrimeros(condados, 10);
        System.out.println("Vecindad:");
        mostrarPrimeros(vecindad, 10);
    }

    private static void compararListas() {
        if (sinCargar()) {
            return;
        }

        // se copian los mismos registros a las dos estructuras y se mide cada una
        long inicio = System.currentTimeMillis();
        ListaEnlazada<CasoDiario> enlazada = new ListaEnlazada<>();
        for (CasoDiario caso : casos) {
            enlazada.insertar(caso);
        }
        long tiempoEnlazada = System.currentTimeMillis() - inicio;

        inicio = System.currentTimeMillis();
        ListaSecuencial<CasoDiario> secuencial = new ListaSecuencial<>();
        for (CasoDiario caso : casos) {
            secuencial.insertar(caso);
        }
        long tiempoSecuencial = System.currentTimeMillis() - inicio;

        System.out.println("Registros insertados en cada lista: " + casos.tamanio());
        System.out.println("ListaEnlazada   -> " + tiempoEnlazada + " ms (un nodo nuevo por registro)");
        System.out.println("ListaSecuencial -> " + tiempoSecuencial + " ms (capacidad final: "
                + secuencial.capacidad() + ")");
        System.out.println("La ListaEnlazada inserta al final en O(1) gracias a la referencia a la cola.");
        System.out.println("La ListaSecuencial accede por posicion en O(1), pero al llenarse duplica");
        System.out.println("el arreglo y copia todo; ademas deja espacio sin usar.");
    }

    private static void mostrarEsquema() {
        System.out.println("casos_diarios.csv: " + Arrays.toString(Esquema.CABECERA_CASOS_DIARIOS));
        System.out.println("condados.csv: " + Arrays.toString(Esquema.CABECERA_CONDADOS));
        System.out.println("vecindad.tsv: " + Arrays.toString(Esquema.CABECERA_VECINDAD));
    }
}
