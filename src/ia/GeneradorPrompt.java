package ia;

import estructuras.ListaEnlazada;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GeneradorPrompt {
    private static final Path PLANTILLA = Path.of("prompts", "reporte_etl.txt");

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
}