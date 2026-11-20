package ia;

public class Transicion {
    private final String origen;
    private final String destino;
    private int veces;

    public Transicion(String origen, String destino) {
        this.origen = origen;
        this.destino = destino;
        this.veces = 1;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getVeces() {
        return veces;
    }

    void incrementar() {
        veces++;
    }
}