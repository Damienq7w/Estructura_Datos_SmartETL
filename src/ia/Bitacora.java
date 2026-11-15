package ia;

import java.util.Objects;

import estructuras.ListaEnlazada;

public class Bitacora {
    private final int maxEventos;
    private ListaEnlazada<Evento> registro = new ListaEnlazada<>();
    private boolean activa = false;

    public Bitacora(int maxEventos) {
        this.maxEventos = maxEventos;
    }

    public void registrar(String proceso, String accion, String detalle) {
        if (!activa || tamanio() >= maxEventos) {
            return;
        }

        int numero = tamanio() + 1;
        registro.insertar(new Evento(numero, proceso, accion, detalle));
    }

    public void activar() {
        activa = true;
    }

    public void desactivar() {
        activa = false;
    }

    public boolean estaActiva() {
        return activa;
    }

    public int tamanio() {
        return registro.tamanio();
    }

    public void limpiar() {
        registro = new ListaEnlazada<>();
    }

    public ListaEnlazada<Evento> eventos() {
        return registro;
    }

        public ListaEnlazada<Transicion> tablaTransiciones() {
        ListaEnlazada<Transicion> tabla = new ListaEnlazada<>();
        Evento anterior = null;

        for (Evento actual : registro) {
            if (anterior != null) {
                contarTransicion(tabla, anterior.getAccion(), actual.getAccion());
            }
            anterior = actual;
        }

        return tabla;
    }

    private void contarTransicion(ListaEnlazada<Transicion> tabla,
                                  String origen, String destino) {
        for (Transicion transicion : tabla) {
            if (Objects.equals(transicion.getOrigen(), origen)
                    && Objects.equals(transicion.getDestino(), destino)) {
                transicion.incrementar();
                return;
            }
        }

        tabla.insertar(new Transicion(origen, destino));
    }
}