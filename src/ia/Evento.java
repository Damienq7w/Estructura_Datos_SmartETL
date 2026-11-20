package ia;

public class Evento {
    private final int numero;
    private final String proceso;
    private final String accion;
    private final String detalle;

    public Evento(int numero, String proceso, String accion, String detalle) {
        this.numero = numero;
        this.proceso = proceso;
        this.accion = accion;
        this.detalle = detalle;
    }

    public int getNumero() {
        return numero;
    }

    public String getProceso() {
        return proceso;
    }

    public String getAccion() {
        return accion;
    }

    public String getDetalle() {
        return detalle;
    }
}