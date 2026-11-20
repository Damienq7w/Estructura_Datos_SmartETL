import ia.Bitacora;
import ia.GeneradorFlujograma;
import java.io.IOException;
import java.nio.file.Path;
import estructuras.ListaEnlazada;

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
                ListaEnlazada<String> vecinosEjemplo = new ListaEnlazada<>();
        vecinosEjemplo.insertar("Elmore (01051)");
        vecinosEjemplo.insertar("Lowndes (01085)");

        String vecindad = generador.generarVecindad("Autauga (01001)", vecinosEjemplo);
        String esperadoVecindad = "flowchart TD\n"
                + "    C[\"Autauga (01001)\"]\n"
                + "    C --- V1[\"Elmore (01051)\"]\n"
                + "    C --- V2[\"Lowndes (01085)\"]\n";

        if (!esperadoVecindad.equals(vecindad)) {
            throw new AssertionError("El diagrama de vecindad generado fue:\n" + vecindad);
        }
        System.out.println("OK - Diagrama de un condado y dos vecinos");
        System.out.println("Ejemplo generado en " + archivo);
    }
}