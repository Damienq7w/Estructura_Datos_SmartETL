import java.io.File;
import java.util.Scanner;

import menu.EstadoETL;
import menu.MenuETL;

public class Main {

    public static void main(String[] args) {
        if (!verificarArchivos()) {
            return;
        }

        EstadoETL estado = new EstadoETL();
        MenuETL menuETL = new MenuETL(estado);

        Scanner teclado = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            opcion = leerOpcion(teclado);

            if (opcion == 0) {
                System.out.println("Hasta luego");
            } else if (!menuETL.ejecutar(opcion, teclado)) {
                System.out.println("Opcion no valida");
            }
        }

        teclado.close();
    }

    private static boolean verificarArchivos() {
        String[] rutas = { EstadoETL.RUTA_CASOS, EstadoETL.RUTA_CONDADOS, EstadoETL.RUTA_VECINDAD };

        for (String ruta : rutas) {
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                System.out.println("Falta el archivo: " + ruta);
                System.out.println("Se busco en: " + archivo.getAbsolutePath());
                System.out.println("Ejecute: python tools/preparar_base.py");
                return false;
            }
        }

        return true;
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println(" 1. Cargar casos diarios (Extract)");
        System.out.println(" 2. Mostrar registros");
        System.out.println(" 3. Buscar por codigo FIPS y fecha");
        System.out.println(" 4. Buscar por nombre de condado");
        System.out.println(" 5. Ver incidencias de lectura");
        System.out.println(" 6. Estadisticas de la base");
        System.out.println(" 7. Ver condados y vecindad");
        System.out.println(" 8. Comparar ListaEnlazada vs ListaSecuencial");
        System.out.println(" 9. Ver esquema fijo de la base");
        System.out.println(" 0. Salir");
        System.out.print("Elija una opcion: ");
    }

    private static int leerOpcion(Scanner teclado) {
        String entrada = teclado.nextLine().trim();
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
