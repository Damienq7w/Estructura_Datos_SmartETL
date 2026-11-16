package model;

/**
 * Representa un registro rechazado por el Transform.
 * Es inmutable: todos sus campos son final.
 */
public class ErrorETL {

    private final String archivo;   // "casos", "condados" o "vecindad"
    private final int linea;        // numero de linea en el archivo original
    private final TipoError tipo;   // motivo del rechazo
    private final String detalle;   // explicacion, por ejemplo "casos = abc"

    public ErrorETL(String archivo, int linea, TipoError tipo, String detalle) {
        this.archivo = archivo;
        this.linea = linea;
        this.tipo = tipo;
        this.detalle = detalle;
    }

    public String getArchivo() { return archivo; }
    public int getLinea() { return linea; }
    public TipoError getTipo() { return tipo; }
    public String getDetalle() { return detalle; }

    /** Todo el error en una sola linea; Sara lo usa para el archivo de errores. */
    public String mostrar() {
        return archivo + " | linea " + linea + " | " + tipo + " | " + detalle;
    }

    @Override
    public String toString() {
        return mostrar();
    }
}
