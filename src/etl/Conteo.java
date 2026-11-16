package etl;

import model.TipoError;

/**
 * Contadores de un archivo (casos o condados) que Sara usa en el reporte.
 * Se cumple siempre: leidos = validos + rechazados.
 */
public class Conteo {

    private int leidos;
    private int validos;
    private int reparados;
    private int advertencias;
    private int rechazados;
    // Una casilla por cada tipo de error; el indice es tipo.ordinal()
    private final int[] rechazadosPorTipo = new int[TipoError.values().length];

    void sumarLeido()      { leidos++; }
    void sumarValido()     { validos++; }
    void sumarReparado()   { reparados++; }
    void sumarAdvertencia(){ advertencias++; }

    void sumarRechazado(TipoError tipo) {
        rechazados++;
        rechazadosPorTipo[tipo.ordinal()]++;
    }

    public int getLeidos()       { return leidos; }
    public int getValidos()      { return validos; }
    public int getReparados()    { return reparados; }
    public int getAdvertencias() { return advertencias; }
    public int getRechazados()   { return rechazados; }

    public int getRechazados(TipoError tipo) {
        return rechazadosPorTipo[tipo.ordinal()];
    }
}
