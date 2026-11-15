import ia.Bitacora;
import ia.Evento;

public class PruebaBitacora {
    public static void main(String[] args) {
        Bitacora bitacora = new Bitacora(5);

        comprobar(!bitacora.estaActiva() && bitacora.tamanio() == 0,
                "Bitácora nueva y vacía");

        bitacora.activar();
        bitacora.registrar("ETL", "ENCOLAR", "FIPS 01001, línea 25");
        comprobar(bitacora.estaActiva() && bitacora.tamanio() == 1,
                "Registro con la bitácora activa");

        Evento primero = bitacora.eventos().obtener(0);
        comprobar(primero.getNumero() == 1
                        && "ETL".equals(primero.getProceso())
                        && "ENCOLAR".equals(primero.getAccion())
                        && "FIPS 01001, línea 25".equals(primero.getDetalle()),
                "Datos del primer evento");

        bitacora.registrar("ETL", "DESENCOLAR", "Línea 25");
        bitacora.registrar("ETL", "VALIDAR", "Línea 25");
        comprobar(bitacora.eventos().obtener(0).getNumero() == 1
                        && bitacora.eventos().obtener(1).getNumero() == 2
                        && bitacora.eventos().obtener(2).getNumero() == 3,
                "Numeración en orden");

        bitacora.desactivar();
        bitacora.registrar("ETL", "REPARAR", "Línea 25");
        comprobar(!bitacora.estaActiva() && bitacora.tamanio() == 3,
                "Registro apagado");

        Bitacora limitada = new Bitacora(5);
        limitada.activar();
        for (int i = 1; i <= 8; i++) {
            limitada.registrar("ETL", "VALIDAR", "Línea " + i);
        }
        comprobar(limitada.tamanio() == 5, "Tope de cinco eventos");

        limitada.limpiar();
        comprobar(limitada.tamanio() == 0, "Limpieza de eventos");
        limitada.registrar("ETL", "ENCOLAR", "Nueva corrida");
        comprobar(limitada.tamanio() == 1
                        && limitada.eventos().obtener(0).getNumero() == 1,
                "Numeración reiniciada");

        System.out.println("Todas las comprobaciones de Bitacora pasaron.");
    }

    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new AssertionError(nombre);
        }
        System.out.println("OK - " + nombre);
    }
}