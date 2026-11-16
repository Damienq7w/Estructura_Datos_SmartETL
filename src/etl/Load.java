package etl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import estructuras.Vertice;
import model.CasoDiario;
import model.Condado;
import model.ErrorETL;
import model.Esquema;
import model.ResumenETL;
import model.TipoError;

/**
 * Load: escribe los resultados del ETL en la carpeta de salida (por defecto
 * "output/"). Si la carpeta no existe, la crea.
 *
 * Reglas de esta clase:
 *  - cada archivo se escribe linea por linea con BufferedWriter dentro de un
 *    try con recursos (se cierra solo), nunca como un solo texto gigante;
 *  - la cabecera es igual a la del archivo original, con las columnas en el
 *    mismo orden, para que el archivo limpio se pueda volver a leer;
 *  - no imprime nada: devuelve cuantas lineas escribio y el menu muestra;
 *  - la pila de errores se recorre con su iterador, SIN desapilar.
 */
public class Load {

    public static final String CARPETA_POR_DEFECTO = "output";

    public static final String ARCHIVO_CASOS = "casos_limpios.csv";
    public static final String ARCHIVO_CONDADOS = "condados_limpios.csv";
    public static final String ARCHIVO_VECINDAD = "vecindad_limpia.csv";
    public static final String ARCHIVO_ERRORES = "errores_etl.txt";
    public static final String ARCHIVO_REPORTE = "reporte_etl.txt";

    private final Path carpeta;

    public Load() {
        this(CARPETA_POR_DEFECTO);
    }

    public Load(String carpeta) {
        Objects.requireNonNull(carpeta, "La carpeta de salida es obligatoria");
        this.carpeta = Paths.get(carpeta);
    }

    /** Ruta completa de un archivo de salida, para mostrarla en el menu. */
    public String ruta(String archivo) {
        return carpeta.resolve(archivo).toString();
    }

    // ---------------------------------------------------------------
    // Datos limpios
    // ---------------------------------------------------------------

    /** Escribe los casos validos. Devuelve cuantos registros escribio. */
    public int escribirCasos(ListaEnlazada<CasoDiario> validos) throws IOException {
        Objects.requireNonNull(validos, "La lista de casos validos es obligatoria");
        int escritos = 0;
        try (BufferedWriter out = abrir(ARCHIVO_CASOS)) {
            escribirLinea(out, unir(Esquema.CABECERA_CASOS_DIARIOS));
            for (CasoDiario c : validos) {
                // muertes "sin dato" (-1) se escribe como campo vacio, nunca como -1
                String muertes = c.getMuertes() < 0 ? "" : String.valueOf(c.getMuertes());
                escribirLinea(out, unir(c.getFecha(), c.getCondado(), c.getEstado(),
                        c.getFips(), String.valueOf(c.getCasos()), muertes));
                escritos++;
            }
        }
        return escritos;
    }

    /** Escribe los condados validos (FIPS ya de 5 digitos). */
    public int escribirCondados(ListaEnlazada<Condado> condados) throws IOException {
        Objects.requireNonNull(condados, "La lista de condados es obligatoria");
        int escritos = 0;
        try (BufferedWriter out = abrir(ARCHIVO_CONDADOS)) {
            escribirLinea(out, unir(Esquema.CABECERA_CONDADOS));
            for (Condado c : condados) {
                escribirLinea(out, unir(c.getFips(), c.getNombre(), c.getEstado()));
                escritos++;
            }
        }
        return escritos;
    }

    /**
     * Escribe las conexiones del grafo, cada par UNA sola vez (el grafo guarda
     * A-B y B-A; aqui se escribe solo cuando el FIPS de A es menor que el de B).
     */
    public int escribirVecindad(Grafo<Condado> grafo) throws IOException {
        Objects.requireNonNull(grafo, "El grafo es obligatorio");
        int escritos = 0;
        try (BufferedWriter out = abrir(ARCHIVO_VECINDAD)) {
            escribirLinea(out, unir(Esquema.CABECERA_VECINDAD));
            for (Vertice<Condado> origen : grafo.getVertices()) {
                for (Vertice<Condado> destino : origen.getAdyacentes()) {
                    Condado a = origen.getDato();
                    Condado b = destino.getDato();
                    if (a.getFips().compareTo(b.getFips()) < 0) {
                        escribirLinea(out, unir(nombreCompleto(a), a.getFips(),
                                nombreCompleto(b), b.getFips()));
                        escritos++;
                    }
                }
            }
        }
        return escritos;
    }

    /** "Autauga County" + "AL" -> "Autauga County, AL", como viene en el archivo original. */
    private static String nombreCompleto(Condado c) {
        return c.getEstado().isEmpty() ? c.getNombre() : c.getNombre() + ", " + c.getEstado();
    }

    // ---------------------------------------------------------------
    // Rechazados
    // ---------------------------------------------------------------

    /**
     * Escribe un rechazado por linea: archivo, linea, tipo y detalle.
     * La pila NO se vacia. Sale en el orden en que se rechazaron (el primero
     * rechazado, primero): como la pila se recorre de la cima hacia abajo,
     * se copia a una pila auxiliar para darle la vuelta sin tocar la original.
     */
    public int escribirErrores(Pila<ErrorETL> errores) throws IOException {
        Objects.requireNonNull(errores, "La pila de errores es obligatoria");

        Pila<ErrorETL> alReves = new Pila<>();
        for (ErrorETL e : errores) {
            alReves.apilar(e);
        }

        int escritos = 0;
        try (BufferedWriter out = abrir(ARCHIVO_ERRORES)) {
            for (ErrorETL e : alReves) {
                escribirLinea(out, e.mostrar());
                escritos++;
            }
        }
        return escritos;
    }

    // ---------------------------------------------------------------
    // Reporte en texto
    // ---------------------------------------------------------------

    /** Escribe reporte_etl.txt con el resumen en texto claro. No necesita internet. */
    public void escribirReporte(ResumenETL resumen) throws IOException {
        Objects.requireNonNull(resumen, "El resumen es obligatorio");
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (BufferedWriter out = abrir(ARCHIVO_REPORTE)) {
            escribirLinea(out, "REPORTE DEL PROCESO ETL - SmartETL");
            escribirLinea(out, "Fecha y hora: " + fecha);
            escribirLinea(out, "");

            escribirBloque(out, "Casos diarios (casos_diarios.csv)", resumen.getCasos());
            escribirBloque(out, "Condados (condados.csv)", resumen.getCondados());
            escribirBloque(out, "Vecindad (vecindad.tsv)", resumen.getVecindad());

            if (!resumen.getFechaMinima().isEmpty() || resumen.getCondadosConCasos() > 0) {
                escribirLinea(out, "Resultado limpio");
                escribirLinea(out, "  Rango de fechas: " + resumen.getFechaMinima()
                        + " a " + resumen.getFechaMaxima());
                escribirLinea(out, "  Condados con casos: " + resumen.getCondadosConCasos());
                for (String linea : resumen.getCondadosConMasCasos()) {
                    escribirLinea(out, "  - " + linea);
                }
                escribirLinea(out, "");
            }

            escribirLinea(out, "Cuadre: " + (resumen.cuadra()
                    ? "SI (leidos = validos + rechazados en todos los archivos)"
                    : "NO (algun archivo no cumple leidos = validos + rechazados)"));
            escribirLinea(out, "Tiempo del proceso: " + resumen.getTiempoMs() + " ms");
        }
    }

    private void escribirBloque(BufferedWriter out, String titulo, ResumenETL.Contadores c)
            throws IOException {
        escribirLinea(out, titulo);
        escribirLinea(out, "  Leidos: " + c.getLeidos());
        escribirLinea(out, "  Validos: " + c.getValidos());
        escribirLinea(out, "  Reparados: " + c.getReparados());
        escribirLinea(out, "  Advertencias: " + c.getAdvertencias());
        escribirLinea(out, "  Rechazados: " + c.getRechazados());
        boolean hayMotivos = false;
        for (TipoError tipo : TipoError.values()) {
            int cantidad = c.getRechazadosPorTipo(tipo);
            if (cantidad > 0) {
                escribirLinea(out, "    " + tipo + ": " + cantidad);
                hayMotivos = true;
            }
        }
        if (!hayMotivos) {
            escribirLinea(out, "    (sin rechazados por motivo)");
        }
        escribirLinea(out, "  Cuadra: " + (c.cuadra() ? "si" : "NO"));
        escribirLinea(out, "");
    }

    // ---------------------------------------------------------------
    // Ayudas internas
    // ---------------------------------------------------------------

    private BufferedWriter abrir(String archivo) throws IOException {
        Files.createDirectories(carpeta);
        return Files.newBufferedWriter(carpeta.resolve(archivo), StandardCharsets.UTF_8);
    }

    private static void escribirLinea(BufferedWriter out, String linea) throws IOException {
        out.write(linea);
        out.newLine();
    }

    private static String unir(String... campos) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                linea.append(',');
            }
            linea.append(campo(campos[i]));
        }
        return linea.toString();
    }

    /**
     * Si el texto trae coma o comillas, va entre comillas para no romper el CSV.
     * Las comillas internas se quitan: el lector de Extract no entiende comillas dobles.
     */
    private static String campo(String texto) {
        if (texto == null) {
            return "";
        }
        if (texto.indexOf(',') >= 0 || texto.indexOf('"') >= 0) {
            return "\"" + texto.replace("\"", "") + "\"";
        }
        return texto;
    }
}
