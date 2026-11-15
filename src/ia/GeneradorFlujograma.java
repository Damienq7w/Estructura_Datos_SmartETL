package ia;

import estructuras.ListaEnlazada;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorFlujograma {
    public String generar(ListaEnlazada<Transicion> tabla) {
        ListaEnlazada<Paso> pasos = new ListaEnlazada<>();

        for (Transicion transicion : tabla) {
            Paso origen = obtenerPaso(pasos, transicion.getOrigen());
            obtenerPaso(pasos, transicion.getDestino());
            origen.salidas++;
        }

        StringBuilder mermaid = new StringBuilder("flowchart TD\n");

        for (Paso paso : pasos) {
            boolean esDecision = paso.salidas > 1;
            mermaid.append("    ").append(paso.id)
                    .append(esDecision ? "{\"" : "[\"")
                    .append(escaparEtiqueta(paso.accion))
                    .append(esDecision ? "\"}\n" : "\"]\n");
        }

        for (Transicion transicion : tabla) {
            Paso origen = obtenerPaso(pasos, transicion.getOrigen());
            Paso destino = obtenerPaso(pasos, transicion.getDestino());
            int veces = transicion.getVeces();

            mermaid.append("    ").append(origen.id)
                    .append(" -->|").append(veces)
                    .append(veces == 1 ? " vez| " : " veces| ")
                    .append(destino.id).append('\n');
        }

        return mermaid.toString();
    }

    public Path guardarMarkdown(String nombreArchivo, String titulo,
                                String origen, String mermaid,
                                String explicacion) throws IOException {
        Path carpeta = Path.of("output", "diagramas");
        Files.createDirectories(carpeta);

        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String contenido = "# " + titulo + "\n\n"
                + "**Fecha y hora:** " + fecha + "\n\n"
                + "**Origen:** " + origen + "\n\n"
                + "```mermaid\n" + mermaid.stripTrailing() + "\n```\n\n"
                + explicacion + "\n";

        return Files.writeString(carpeta.resolve(nombreArchivo), contenido,
                StandardCharsets.UTF_8);
    }

    private Paso obtenerPaso(ListaEnlazada<Paso> pasos, String accion) {
        for (Paso paso : pasos) {
            if (paso.accion.equals(accion)) {
                return paso;
            }
        }

        Paso nuevo = new Paso("N" + (pasos.tamanio() + 1), accion);
        pasos.insertar(nuevo);
        return nuevo;
    }

    private String escaparEtiqueta(String accion) {
        return accion.replace("\"", "#quot;")
                .replace('\r', ' ')
                .replace('\n', ' ');
    }

    private static class Paso {
        private final String id;
        private final String accion;
        private int salidas;

        private Paso(String id, String accion) {
            this.id = id;
            this.accion = accion;
        }
    }
}