import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import etl.EjecutorETL;
import etl.Load;
import model.ResumenETL;
import model.TipoError;

/**
 * Corre el ETL completo con data/pruebas/ y comprueba que los numeros
 * coinciden con data/pruebas/LEEME.md. Se ejecuta desde la carpeta principal.
 */
public class PruebaETLCompleto {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    private static int contarLineas(Path archivo) throws IOException {
        int n = 0;
        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            while (br.readLine() != null) n++;
        }
        return n;
    }

    private static boolean lanza(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalStateException e) {
            return e.getMessage() != null && !e.getMessage().isEmpty();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Path salida = Files.createTempDirectory("prueba-etl-");
        Path salida2 = Files.createTempDirectory("prueba-etl-2-");
        try {
            String casos = "data/pruebas/casos_prueba.csv";
            String condados = "data/pruebas/condados_prueba.csv";
            String vecindad = "data/pruebas/vecindad_prueba.tsv";

            // ---------- cada paso avisa si falta el anterior ----------
            EjecutorETL pasos = new EjecutorETL(casos, condados, vecindad, salida.toString());
            revisar("transformar sin extraer avisa", lanza(pasos::transformar));
            revisar("cargar sin transformar avisa", lanza(() -> {
                try { pasos.cargar(); } catch (IOException e) { throw new RuntimeException(e); }
            }));
            pasos.extraer();
            revisar("tras extraer: 42 casos en la cola", pasos.getColaCasos().tamanio() == 42);
            revisar("tras extraer: 9 condados en la cola", pasos.getColaCondados().tamanio() == 9);
            revisar("tras extraer: 8 filas de vecindad en la cola", pasos.getColaVecindad().tamanio() == 8);
            revisar("ver validos antes de transformar avisa", lanza(pasos::getCasosValidos));
            pasos.transformar();
            revisar("tras transformar: las colas quedan vacias",
                    pasos.getColaCasos().estaVacia() && pasos.getColaCondados().estaVacia()
                            && pasos.getColaVecindad().estaVacia());
            revisar("transformar dos veces avisa", lanza(pasos::transformar));

            // ---------- numeros contra el LEEME ----------
            ResumenETL r = pasos.getResumen();
            ResumenETL.Contadores c = r.getCasos();
            // Los duplicados son la Parte 5 de Luis. Si todavia no los detecta,
            // el LEEME cambia en una fila: 36 validos y 6 rechazados (sin DUPLICADO).
            boolean hayDuplicados = c.getRechazadosPorTipo(TipoError.DUPLICADO) == 1;
            if (!hayDuplicados) {
                System.out.println("AVISO Transform aun no detecta DUPLICADO (Parte 5 de Luis): se espera 36 validos y 6 rechazados");
            }
            int validos = hayDuplicados ? 35 : 36;
            int rechazados = hayDuplicados ? 7 : 6;
            revisar("casos: leidos 42", c.getLeidos() == 42);
            revisar("casos: validos " + validos, c.getValidos() == validos);
            revisar("casos: reparados 0", c.getReparados() == 0);
            revisar("casos: advertencias 2", c.getAdvertencias() == 2);
            revisar("casos: rechazados " + rechazados, c.getRechazados() == rechazados);
            revisar("casos: CONDADO_DESCONOCIDO 1", c.getRechazadosPorTipo(TipoError.CONDADO_DESCONOCIDO) == 1);
            revisar("casos: AREA_SIN_FIPS 1", c.getRechazadosPorTipo(TipoError.AREA_SIN_FIPS) == 1);
            revisar("casos: VALOR_NO_NUMERICO 1", c.getRechazadosPorTipo(TipoError.VALOR_NO_NUMERICO) == 1);
            revisar("casos: VALOR_NEGATIVO 1", c.getRechazadosPorTipo(TipoError.VALOR_NEGATIVO) == 1);
            revisar("casos: FECHA_INVALIDA 1", c.getRechazadosPorTipo(TipoError.FECHA_INVALIDA) == 1);
            revisar("casos: COLUMNAS_INCOMPLETAS 1", c.getRechazadosPorTipo(TipoError.COLUMNAS_INCOMPLETAS) == 1);
            revisar("casos: cuadra por total y por tipo", c.cuadra() && c.cuadraPorTipo());

            ResumenETL.Contadores k = r.getCondados();
            revisar("condados: leidos 9", k.getLeidos() == 9);
            revisar("condados: validos 8", k.getValidos() == 8);
            revisar("condados: reparados 2", k.getReparados() == 2);
            revisar("condados: rechazados 1 (NO_ES_CONDADO)",
                    k.getRechazados() == 1 && k.getRechazadosPorTipo(TipoError.NO_ES_CONDADO) == 1);
            revisar("condados: cuadra", k.cuadra() && k.cuadraPorTipo());
            ResumenETL.Contadores v = r.getVecindad();
            revisar("vecindad: leidos 8, validas 6, rechazados 2",
                    v.getLeidos() == 8 && v.getValidos() == 6 && v.getRechazados() == 2);
            revisar("vecindad: 1 AUTO_VECINDAD y 1 DUPLICADO",
                    v.getRechazadosPorTipo(TipoError.AUTO_VECINDAD) == 1
                            && v.getRechazadosPorTipo(TipoError.DUPLICADO) == 1);
            revisar("vecindad: cuadra", v.cuadra() && v.cuadraPorTipo());
            revisar("grafo: 8 vertices y 6 aristas",
                    pasos.getGrafo().numeroVertices() == 8 && pasos.getGrafo().numeroAristas() == 6);
            revisar("el resumen completo cuadra", r.cuadra());
            int enPila = rechazados + 1 + 2; // casos + condados + vecindad
            revisar("la pila de errores tiene " + enPila, pasos.getErrores().tamanio() == enPila);

            // ---------- Load ----------
            pasos.cargar();
            revisar("pila intacta despues de cargar", pasos.getErrores().tamanio() == enPila);
            revisar("casos_limpios.csv: cabecera + " + validos,
                    contarLineas(salida.resolve(Load.ARCHIVO_CASOS)) == validos + 1);
            revisar("condados_limpios.csv: cabecera + 8",
                    contarLineas(salida.resolve(Load.ARCHIVO_CONDADOS)) == 9);
            revisar("vecindad_limpia.csv: cabecera + 6 aristas",
                    contarLineas(salida.resolve(Load.ARCHIVO_VECINDAD)) == 7);
            revisar("errores_etl.txt: " + enPila + " lineas", contarLineas(salida.resolve(Load.ARCHIVO_ERRORES)) == enPila);
            revisar("reporte_etl.txt existe", Files.size(salida.resolve(Load.ARCHIVO_REPORTE)) > 0);

            // ---------- la mejor prueba: volver a leer lo limpio => 0 rechazados ----------
            EjecutorETL segunda = new EjecutorETL(
                    salida.resolve(Load.ARCHIVO_CASOS).toString(),
                    salida.resolve(Load.ARCHIVO_CONDADOS).toString(),
                    vecindad, salida2.toString());
            ResumenETL r2 = segunda.ejecutarTodo();
            revisar("releer lo limpio: " + validos + " casos leidos y " + validos + " validos",
                    r2.getCasos().getLeidos() == validos && r2.getCasos().getValidos() == validos);
            revisar("releer lo limpio: 0 casos rechazados", r2.getCasos().getRechazados() == 0);
            revisar("releer lo limpio: 8 condados validos y 0 rechazados",
                    r2.getCondados().getValidos() == 8 && r2.getCondados().getRechazados() == 0);
            revisar("releer lo limpio: 0 condados reparados (ya vienen con 5 digitos)",
                    r2.getCondados().getReparados() == 0);
            // la vecindad original se vuelve a leer tal cual: sus 2 rechazos son los unicos que quedan
            revisar("releer lo limpio: solo quedan los 2 rechazos de la vecindad", segunda.getErrores().tamanio() == 2);

            // ---------- archivo con cabecera distinta ----------
            Path malo = salida.resolve("malo.csv");
            Files.writeString(malo, "otra,cabecera\n1,2\n");
            EjecutorETL conError = new EjecutorETL(malo.toString(), condados, vecindad, salida2.toString());
            boolean lanzo = false;
            try {
                conError.extraer();
            } catch (IOException e) {
                lanzo = e.getMessage().contains("cabecera");
            }
            revisar("cabecera invalida: extraer avisa con mensaje", lanzo);
        } finally {
            borrar(salida.toFile());
            borrar(salida2.toFile());
        }

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
        if (fallas > 0) System.exit(1);
    }

    private static void borrar(File f) {
        File[] hijos = f.listFiles();
        if (hijos != null) for (File h : hijos) borrar(h);
        f.delete();
    }
}
