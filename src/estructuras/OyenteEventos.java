package estructuras;

@FunctionalInterface
public interface OyenteEventos {
    void evento(String proceso, String accion, String detalle);
}