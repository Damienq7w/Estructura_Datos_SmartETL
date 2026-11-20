import estructuras.ListaEnlazada;
import estructuras.OyenteEventos;
import ia.Bitacora;
import ia.Evento;
import ia.Transicion;

public class PruebaBitacora {
    public static void main(String[] args) {
        Bitacora bitacora = new Bitacora(5);
        OyenteEventos oyente = bitacora;

        comprobar(!bitacora.estaActiva() && bitacora.tamanio() == 0,
                "Bitácora nueva y vacía");

        bitacora.activar();
        oyente.evento("ETL", "ENCOLAR", "FIPS 01001, línea 25");
        comprobar(bitacora.estaActiva() && bitacora.tamanio() == 1,
                "Registro mediante el oyente con la bitácora activa");

        Evento primero = bitacora.eventos().obtener(0);
        comprobar(primero.getNumero() == 1
                        && "ETL".equals(primero.getProceso())
                        && "ENCOLAR".equals(primero.getAccion())
                        && "FIPS 01001, línea 25".equals(primero.getDetalle()),
                "Datos del evento recibido mediante el oyente");

        bitacora.registrar("ETL", "DESENCOLAR", "Línea 25");
        bitacora.registrar("ETL", "VALIDAR", "Línea 25");
        comprobar(bitacora.eventos().obtener(0).getNumero() == 1
                        && bitacora.eventos().obtener(1).getNumero() == 2
                        && bitacora.eventos().obtener(2).getNumero() == 3,
                "Numeración en orden");

        bitacora.desactivar();
        oyente.evento("ETL", "REPARAR", "Línea 25");
        comprobar(!bitacora.estaActiva() && bitacora.tamanio() == 3,
                "El oyente respeta el registro apagado");

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

        probarTablaTransiciones();
        System.out.println("Todas las comprobaciones de Bitacora pasaron.");
    }

    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) {
            throw new AssertionError(nombre);
        }
        System.out.println("OK - " + nombre);
    }

    private static void probarTablaTransiciones() {
        Bitacora bitacora = new Bitacora(20);
        bitacora.activar();

        comprobar(bitacora.tablaTransiciones().tamanio() == 0,
                "Tabla con cero eventos");

        bitacora.registrar("ETL", "DESENCOLAR", "");
        comprobar(bitacora.tablaTransiciones().tamanio() == 0,
                "Tabla con un evento");

        String[] accionesRestantes = {
            "VALIDAR", "AGREGAR_A_LISTA",
            "DESENCOLAR", "VALIDAR", "APILAR_ERROR",
            "DESENCOLAR", "VALIDAR", "AGREGAR_A_LISTA"
        };

        for (String accion : accionesRestantes) {
            bitacora.registrar("ETL", accion, "");
        }

        ListaEnlazada<Transicion> tabla = bitacora.tablaTransiciones();
        comprobar(tabla.tamanio() == 5, "Cinco transiciones distintas");

        comprobarTransicion(tabla.obtener(0), "DESENCOLAR", "VALIDAR", 3);
        comprobarTransicion(tabla.obtener(1), "VALIDAR", "AGREGAR_A_LISTA", 2);
        comprobarTransicion(tabla.obtener(2), "AGREGAR_A_LISTA", "DESENCOLAR", 1);
        comprobarTransicion(tabla.obtener(3), "VALIDAR", "APILAR_ERROR", 1);
        comprobarTransicion(tabla.obtener(4), "APILAR_ERROR", "DESENCOLAR", 1);
    }

    private static void comprobarTransicion(Transicion transicion,
                                            String origen, String destino,
                                            int veces) {
        comprobar(origen.equals(transicion.getOrigen())
                        && destino.equals(transicion.getDestino())
                        && transicion.getVeces() == veces,
                origen + " -> " + destino + " (" + veces + ")");
    }
}