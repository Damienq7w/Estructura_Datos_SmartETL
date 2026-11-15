import ia.Bitacora;
import ia.GeneradorFlujograma;
import java.io.IOException;
import java.nio.file.Path;

public class PruebaGeneradorFlujograma {
    public static void main(String[] args) throws IOException {
        Bitacora bitacora = new Bitacora(20);
        bitacora.activar();

        String[] acciones = {
            "DESENCOLAR", "VALIDAR", "AGREGAR_A_LISTA",
            "DESENCOLAR", "VALIDAR", "APILAR_ERROR",
            "DESENCOLAR", "VALIDAR", "AGREGAR_A_LISTA"
        };

        for (String accion : acciones) {
            bitacora.registrar("ETL", accion, "");
        }

        GeneradorFlujograma generador = new GeneradorFlujograma();
        String mermaid = generador.generar(bitacora.tablaTransiciones());

        String esperado = """
                flowchart TD
                    N1["DESENCOLAR"]
                    N2{"VALIDAR"}
                    N3["AGREGAR_A_LISTA"]
                    N4["APILAR_ERROR"]
                    N1 -->|3 veces| N2
                    N2 -->|2 veces| N3
                    N3 -->|1 vez| N1
                    N2 -->|1 vez| N4
                    N4 -->|1 vez| N1
                """;

        if (!esperado.equals(mermaid)) {
            throw new AssertionError("El flujograma generado fue:\n" + mermaid);
        }

        System.out.println("OK - Cuatro nodos, un rombo y cinco flechas");

        Path archivo = generador.guardarMarkdown(
                "ejemplo-bitacora.md",
                "Flujograma de ejemplo de la bitácora",
                "Generador propio",
                mermaid,
                "La prueba registra nueve eventos de ejemplo. VALIDAR tiene dos "
                        + "salidas: agregar el registro a la lista o apilar el error.");

        System.out.println("Ejemplo generado en " + archivo);
    }
}