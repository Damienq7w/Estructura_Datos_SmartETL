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
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        System.out.println("OK - " + mensaje);
    }
}