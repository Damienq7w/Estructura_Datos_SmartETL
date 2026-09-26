package model;

/**
 * Interfaz que define lo que tienen en común todos los registros
 * del sistema: CasoDiario, Condado y Vecindad.
 *
 * Es lo que permite el flujo CSV -> EXTRACT -> LISTA&lt;Registro&gt;:
 * el Extract y el menú pueden tratar a los tres tipos de la misma
 * forma (polimorfismo) sin conocer su tipo concreto.
 */
public interface Registro {

    /**
     * Devuelve la clave que identifica a este registro
     * (por ejemplo, el FIPS, o el FIPS + fecha).
     */
    String getClave();

    /**
     * Devuelve una representación del registro en una sola línea,
     * lista para mostrarse en consola o en un reporte.
     */
    String mostrar();
}
