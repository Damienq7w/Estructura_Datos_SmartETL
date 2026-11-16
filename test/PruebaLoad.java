import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import etl.Extract;
import etl.Load;
import model.CasoDiario;
import model.Condado;
import model.ErrorETL;
import model.ResumenETL;
import model.TipoError;

/** Prueba de Load: escribe en una carpeta temporal y vuelve a leer con Extract. */
public class PruebaLoad {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    /** Lee la linea numero n (desde 1) sin usar colecciones; null si no existe. */
    private static String linea(Path archivo, int n) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            String l = null;
            for (int i = 0; i < n; i++) {
                l = br.readLine();
                if (l == null) return null;
            }
            return l;
        }
    }

    private static int contarLineas(Path archivo) throws IOException {
        int n = 0;
        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            while (br.readLine() != null) n++;
        }
        return n;
    }

    private static String contenido(Path archivo) throws IOException {
        return Files.readString(archivo);
    }

    public static void main(String[] args) throws Exception {
        Path base = Files.createTempDirectory("prueba-load-");
        Path salida = base.resolve("no-existe-aun").resolve("output");
        try {
            Load load = new Load(salida.toString());

            // ---------- casos ----------
            ListaEnlazada<CasoDiario> casos = new ListaEnlazada<>();
            casos.insertar(new CasoDiario("2021-03-01", "Autauga", "Alabama", "01001", 107, 2));
            casos.insertar(new CasoDiario("2021-03-01", "Adjuntas", "Puerto Rico", "72001", 37, -1));
            casos.insertar(new CasoDiario("2021-03-02", "Washington, DC", "District of Columbia", "11001", 500, 9));

            int n = load.escribirCasos(casos);
            Path archivoCasos = salida.resolve(Load.ARCHIVO_CASOS);
            revisar("crea la carpeta si no existe", Files.isDirectory(salida));
            revisar("devuelve cuantos escribio", n == 3);
            revisar("cabecera igual a la original",
                    "date,county,state,fips,cases,deaths".equals(linea(archivoCasos, 1)));
            revisar("una linea por registro (cabecera + 3)", contarLineas(archivoCasos) == 4);
            revisar("muertes sin dato queda vacio, no -1",
                    "2021-03-01,Adjuntas,Puerto Rico,72001,37,".equals(linea(archivoCasos, 3)));
            revisar("conserva el cero inicial del FIPS",
                    linea(archivoCasos, 2).contains(",01001,"));
            revisar("nombre con coma va entre comillas",
                    linea(archivoCasos, 4).contains("\"Washington, DC\""));

            // el limpio se vuelve a leer con el mismo lector, sin perder nada
            Extract lector = new Extract();
            ListaEnlazada<CasoDiario> releidos = lector.cargarCasos(archivoCasos.toString());
            revisar("se vuelve a leer: mismas filas", releidos.tamanio() == 3 && lector.getCasosLeidas() == 3);
            revisar("se vuelve a leer: sin FIPS vacios", lector.getCasosSinFips() == 0);
            revisar("se vuelve a leer: sin descartadas", lector.getCasosDescartadas() == 0);
            revisar("se vuelve a leer: misma muerte sin dato", releidos.obtener(1).getMuertes() == -1
                    && lector.getCasosMuertesVacias() == 1);
            revisar("se vuelve a leer: registros iguales",
                    releidos.obtener(0).equals(casos.obtener(0))
                            && releidos.obtener(1).equals(casos.obtener(1))
                            && releidos.obtener(2).equals(casos.obtener(2)));

            // ---------- lista vacia ----------
            n = load.escribirCasos(new ListaEnlazada<>());
            revisar("lista vacia: solo la cabecera", n == 0 && contarLineas(archivoCasos) == 1);

            // ---------- condados ----------
            ListaEnlazada<Condado> condados = new ListaEnlazada<>();
            condados.insertar(new Condado("01001", "Autauga County", "AL"));
            condados.insertar(new Condado("72001", "Adjuntas Municipio", "PR"));
            n = load.escribirCondados(condados);
            Path archivoCondados = salida.resolve(Load.ARCHIVO_CONDADOS);
            revisar("condados: cabecera y filas", n == 2 && contarLineas(archivoCondados) == 3
                    && "fips,name,state".equals(linea(archivoCondados, 1)));
            Extract lectorCondados = new Extract();
            ListaEnlazada<Condado> condadosReleidos = lectorCondados.cargarCondados(archivoCondados.toString());
            revisar("condados: se vuelven a leer sin reparar ni rechazar",
                    condadosReleidos.tamanio() == 2 && lectorCondados.getCondadosFipsSinCero() == 0
                            && lectorCondados.getCondadosNoSonCondado() == 0
                            && condadosReleidos.obtener(0).getFips().equals("01001"));

            // ---------- vecindad ----------
            Grafo<Condado> grafo = new Grafo<>();
            Condado autauga = new Condado("01001", "Autauga County", "AL");
            Condado chilton = new Condado("01021", "Chilton County", "AL");
            grafo.agregarVertice(autauga);
            grafo.agregarVertice(chilton);
            grafo.agregarArista(autauga, chilton);
            n = load.escribirVecindad(grafo);
            Path archivoVecindad = salida.resolve(Load.ARCHIVO_VECINDAD);
            revisar("vecindad: el par A-B se escribe UNA sola vez", n == 1 && contarLineas(archivoVecindad) == 2
                    && "county_name,county_id,adjacent_county_name,adjacent_county_id"
                            .equals(linea(archivoVecindad, 1)));
            revisar("vecindad: nombres con coma entre comillas",
                    "\"Autauga County, AL\",01001,\"Chilton County, AL\",01021".equals(linea(archivoVecindad, 2)));

            // ---------- errores ----------
            Pila<ErrorETL> errores = new Pila<>();
            errores.apilar(new ErrorETL("casos", 12, TipoError.CONDADO_DESCONOCIDO, "condado = Unknown"));
            errores.apilar(new ErrorETL("casos", 18, TipoError.AREA_SIN_FIPS, "condado = New York City"));
            errores.apilar(new ErrorETL("condados", 2, TipoError.NO_ES_CONDADO, "fips = 1000"));
            ErrorETL cimaAntes = errores.cima();

            n = load.escribirErrores(errores);
            Path archivoErrores = salida.resolve(Load.ARCHIVO_ERRORES);
            revisar("errores: devuelve cuantos escribio", n == 3);
            revisar("errores: una linea por rechazado", contarLineas(archivoErrores) == 3);
            revisar("errores: la pila NO se vacio", errores.tamanio() == 3 && errores.cima() == cimaAntes);
            revisar("errores: primero el primer rechazado",
                    "casos | linea 12 | CONDADO_DESCONOCIDO | condado = Unknown".equals(linea(archivoErrores, 1)));
            revisar("errores: ultimo el ultimo rechazado",
                    "condados | linea 2 | NO_ES_CONDADO | fips = 1000".equals(linea(archivoErrores, 3)));

            n = load.escribirErrores(new Pila<>());
            revisar("pila vacia: archivo vacio, sin excepcion", n == 0 && contarLineas(archivoErrores) == 0);

            // ---------- reporte ----------
            ResumenETL resumen = new ResumenETL();
            resumen.getCasos().setLeidos(42);
            resumen.getCasos().setValidos(35);
            resumen.getCasos().setAdvertencias(2);
            resumen.getCasos().setRechazados(7);
            resumen.getCasos().setRechazadosPorTipo(TipoError.DUPLICADO, 1);
            resumen.getCondados().setLeidos(9);
            resumen.getCondados().setValidos(8);
            resumen.getCondados().setReparados(2);
            resumen.getCondados().setRechazados(1);
            resumen.getCondados().setRechazadosPorTipo(TipoError.NO_ES_CONDADO, 1);
            resumen.setTiempoMs(321);
            load.escribirReporte(resumen);
            String reporte = contenido(salida.resolve(Load.ARCHIVO_REPORTE));
            revisar("reporte: tiene fecha y hora", reporte.contains("Fecha y hora: 20"));
            revisar("reporte: leidos de casos", reporte.contains("Leidos: 42"));
            revisar("reporte: advertencias", reporte.contains("Advertencias: 2"));
            revisar("reporte: motivo de rechazo", reporte.contains("DUPLICADO: 1")
                    && reporte.contains("NO_ES_CONDADO: 1"));
            revisar("reporte: reparados de condados", reporte.contains("Reparados: 2"));
            revisar("reporte: dice que cuadra", reporte.contains("Cuadre: SI"));
            revisar("reporte: tiempo", reporte.contains("Tiempo del proceso: 321 ms"));

            resumen.getCasos().setValidos(30);
            load.escribirReporte(resumen);
            revisar("reporte: avisa si NO cuadra",
                    contenido(salida.resolve(Load.ARCHIVO_REPORTE)).contains("Cuadre: NO"));

            // ---------- argumentos nulos ----------
            boolean lanzo = false;
            try {
                load.escribirCasos(null);
            } catch (NullPointerException e) {
                lanzo = e.getMessage() != null;
            }
            revisar("null lanza excepcion con mensaje", lanzo);
        } finally {
            borrar(base);
        }

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
        if (fallas > 0) System.exit(1);
    }

    private static void borrar(Path carpeta) throws IOException {
        java.io.File[] hijos = carpeta.toFile().listFiles();
        if (hijos != null) {
            for (java.io.File hijo : hijos) borrar(hijo.toPath());
        }
        Files.deleteIfExists(carpeta);
    }
}
