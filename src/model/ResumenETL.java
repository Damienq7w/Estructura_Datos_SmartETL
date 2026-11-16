package model;

import estructuras.ListaEnlazada;

/**
 * Guarda los NUMEROS del proceso ETL. No calcula nada: solo los almacena
 * y los devuelve. Es lo unico que se le envia a la IA (Gemini nunca
 * recibe los registros), asi que si un numero no esta aqui, la IA no lo conoce.
 *
 * Lleva un bloque de contadores por archivo (casos, condados, vecindad),
 * el tiempo total y, mas adelante, los datos del resultado limpio.
 */
public class ResumenETL {

    /** Contadores de un archivo. */
    public static class Contadores {
        private int leidos;
        private int validos;
        private int reparados;
        private int advertencias;
        private int rechazados;
        // una casilla por tipo de error, usando ordinal() (sin HashMap)
        private final int[] rechazadosPorTipo = new int[TipoError.values().length];

        public int getLeidos() { return leidos; }
        public void setLeidos(int leidos) { this.leidos = leidos; }

        public int getValidos() { return validos; }
        public void setValidos(int validos) { this.validos = validos; }

        public int getReparados() { return reparados; }
        public void setReparados(int reparados) { this.reparados = reparados; }

        public int getAdvertencias() { return advertencias; }
        public void setAdvertencias(int advertencias) { this.advertencias = advertencias; }

        public int getRechazados() { return rechazados; }
        public void setRechazados(int rechazados) { this.rechazados = rechazados; }

        public int getRechazadosPorTipo(TipoError tipo) {
            return rechazadosPorTipo[tipo.ordinal()];
        }

        public void setRechazadosPorTipo(TipoError tipo, int cantidad) {
            rechazadosPorTipo[tipo.ordinal()] = cantidad;
        }

        /** Cuadra si leidos = validos + rechazados. */
        public boolean cuadra() {
            return leidos == validos + rechazados;
        }

        /** Cuadra por tipo si la suma de los motivos es igual al total de rechazados. */
        public boolean cuadraPorTipo() {
            int suma = 0;
            for (int cantidad : rechazadosPorTipo) {
                suma += cantidad;
            }
            return suma == rechazados;
        }
    }

    private final Contadores casos = new Contadores();
    private final Contadores condados = new Contadores();
    private final Contadores vecindad = new Contadores();

    private long tiempoMs;

    // datos del resultado limpio (los llena Damian mas adelante)
    private String fechaMinima = "";
    private String fechaMaxima = "";
    private int condadosConCasos;
    private final ListaEnlazada<String> condadosConMasCasos = new ListaEnlazada<>();

    public Contadores getCasos() { return casos; }
    public Contadores getCondados() { return condados; }
    public Contadores getVecindad() { return vecindad; }

    public long getTiempoMs() { return tiempoMs; }
    public void setTiempoMs(long tiempoMs) { this.tiempoMs = tiempoMs; }

    public String getFechaMinima() { return fechaMinima; }
    public String getFechaMaxima() { return fechaMaxima; }

    public void setRangoFechas(String minima, String maxima) {
        this.fechaMinima = minima == null ? "" : minima;
        this.fechaMaxima = maxima == null ? "" : maxima;
    }

    public int getCondadosConCasos() { return condadosConCasos; }
    public void setCondadosConCasos(int condadosConCasos) { this.condadosConCasos = condadosConCasos; }

    /** Lineas de texto ya armadas, por ejemplo "Los Angeles, CA: 1234567 casos". */
    public ListaEnlazada<String> getCondadosConMasCasos() { return condadosConMasCasos; }

    /** true si los tres archivos cuadran: leidos = validos + rechazados. */
    public boolean cuadra() {
        return casos.cuadra() && condados.cuadra() && vecindad.cuadra();
    }
}
