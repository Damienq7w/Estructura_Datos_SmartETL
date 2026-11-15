package ia;

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
}