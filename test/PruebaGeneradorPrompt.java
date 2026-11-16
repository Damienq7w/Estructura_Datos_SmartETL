import estructuras.ListaEnlazada;
import etl.EjecutorETL;
import ia.Bitacora;
import ia.GeneradorPrompt;
import java.io.IOException;

public class PruebaGeneradorPrompt {
    public static void main(String[] args) throws IOException {
        Bitacora bitacora = new Bitacora(10);
        bitacora.activar();
        bitacora.registrar("ETL", "LEER", "");
        bitacora.registrar("ETL", "VALIDAR", "");
        bitacora.registrar("ETL", "LEER", "");
        bitacora.registrar("ETL", "VALIDAR", "");

        String resumen = """
                casos_diarios.csv
                Leídos: 10
                Válidos: 8
                Reparados: 2
                Con advertencias: 1
                Rechazados: 2
                Rechazos por FIPS faltante: 2
                """;

        String prompt = new GeneradorPrompt()
                .generarReporteETL(resumen, bitacora.tablaTransiciones());

        comprobar(prompt.contains("Redacta en español")
                        && prompt.contains("flowchart TD"),
                "El prompt incluye las instrucciones de la plantilla");
        comprobar(prompt.contains(resumen.strip()),
                "El prompt conserva las cifras del resumen de ejemplo");
        comprobar(prompt.contains("LEER | VALIDAR | 2")
                        && prompt.contains("VALIDAR | LEER | 1"),
                "El prompt incluye las transiciones y sus conteos");

        probarConResumenETL();
    }

    private static void probarConResumenETL() throws IOException {
        EjecutorETL etl = new EjecutorETL(
                "data/pruebas/casos_prueba.csv",
                "data/pruebas/condados_prueba.csv",
                "data/pruebas/vecindad_prueba.tsv",
                "output");
        etl.extraer();
        etl.transformar();

        String prompt = new GeneradorPrompt()
                .generarReporteETL(etl.getResumen(), new ListaEnlazada<>());

        comprobar(prompt.contains("Casos diarios\nLeídos: 42\n")
                        && prompt.contains("Con advertencias: 2\n")
                        && prompt.contains("Rechazos por AREA_SIN_FIPS: 1\n"),
                "El prompt incluye las cifras reales de casos");
        comprobar(prompt.contains("Condados\nLeídos: 9\nVálidos: 8\nReparados: 2\n")
                        && prompt.contains("Rechazos por NO_ES_CONDADO: 1\n"),
                "El prompt incluye las cifras reales de condados");
        comprobar(prompt.contains("Vecindad\nLeídos: 8\nVálidos: 6\n")
                        && prompt.contains("Rechazos por AUTO_VECINDAD: 1\n"),
                "El prompt incluye las cifras reales de vecindad");
        comprobar(prompt.endsWith("Origen | Destino | Veces\n"),
                "La tabla vacía no añade transiciones");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        System.out.println("OK - " + mensaje);
    }
}