import estructuras.ListaEnlazada;
import etl.EjecutorETL;
import ia.Bitacora;
import ia.GeneradorFlujograma;
import ia.GeneradorPrompt;
import ia.Transicion;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import model.ResumenETL;

public class GenerarEvidenciaETL {
    public static void main(String[] args) throws IOException {
        Path datos = Path.of("data", "pruebas");
        Path salida = Path.of("output", "etl-prueba");
        EjecutorETL etl = new EjecutorETL(
                datos.resolve("casos_prueba.csv").toString(),
                datos.resolve("condados_prueba.csv").toString(),
                datos.resolve("vecindad_prueba.tsv").toString(),
                salida.toString());

        Bitacora bitacora = new Bitacora(10);
        bitacora.activar();

        etl.extraer();
        bitacora.registrar("ETL", "Leer archivos y encolar filas", "");

        etl.transformar();
        bitacora.registrar("ETL", "Validar filas y separar válidos y errores", "");

        ResumenETL resumen = etl.cargar();
        bitacora.registrar("ETL", "Escribir archivos limpios, errores y reporte", "");
        bitacora.desactivar();

        ListaEnlazada<Transicion> transiciones = bitacora.tablaTransiciones();
        GeneradorFlujograma generador = new GeneradorFlujograma();
        Path diagrama = generador.guardarMarkdown(
                "etl-por-etapas.md",
                "Flujograma ETL por etapas",
                "Generador propio a partir de una corrida con data/pruebas/",
                generador.generar(transiciones),
                "La bitácora registra las etapas completadas del ETL.");

        Path prompt = salida.resolve("prompt-etl-por-etapas.txt");
        Files.writeString(prompt, new GeneradorPrompt().generarReporteETL(resumen, transiciones));

        System.out.println("Diagrama: " + diagrama);
        System.out.println("Prompt: " + prompt);
    }
}