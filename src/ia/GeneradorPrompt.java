package ia;

import estructuras.ListaEnlazada;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import model.ResumenETL;
import model.TipoError;

public class GeneradorPrompt {
    private static final Path PLANTILLA = Path.of("prompts", "reporte_etl.txt");

    public String generarReporteETL(ResumenETL resumen,
                                    ListaEnlazada<Transicion> transiciones)
            throws IOException {
        StringBuilder datos = new StringBuilder();
        agregarContadores(datos, "Casos diarios", resumen.getCasos());
        agregarContadores(datos, "Condados", resumen.getCondados());
        agregarContadores(datos, "Vecindad", resumen.getVecindad());
        return generarReporteETL(datos.toString(), transiciones);
    }

    public String generarReporteETL(String resumen,
                                    ListaEnlazada<Transicion> transiciones)
            throws IOException {
        String plantilla = Files.readString(PLANTILLA, StandardCharsets.UTF_8);
        StringBuilder prompt = new StringBuilder(plantilla.stripTrailing())
                .append("\n\nResumen del ETL por archivo\n")
                .append(resumen.strip())
                .append("\n\nTabla de transiciones\n")
                .append("Origen | Destino | Veces\n");

        for (Transicion transicion : transiciones) {
            prompt.append(transicion.getOrigen()).append(" | ")
                    .append(transicion.getDestino()).append(" | ")
                    .append(transicion.getVeces()).append('\n');
        }

        return prompt.toString();
    }

    private void agregarContadores(StringBuilder datos, String archivo,
                                  ResumenETL.Contadores contadores) {
        datos.append(archivo).append('\n')
                .append("Leídos: ").append(contadores.getLeidos()).append('\n')
                .append("Válidos: ").append(contadores.getValidos()).append('\n')
                .append("Reparados: ").append(contadores.getReparados()).append('\n')
                .append("Con advertencias: ").append(contadores.getAdvertencias()).append('\n')
                .append("Rechazados: ").append(contadores.getRechazados()).append('\n');

        for (TipoError motivo : TipoError.values()) {
            int cantidad = contadores.getRechazadosPorTipo(motivo);
            if (cantidad > 0) {
                datos.append("Rechazos por ").append(motivo)
                        .append(": ").append(cantidad).append('\n');
            }
        }
        datos.append('\n');
    }
}