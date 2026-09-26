package model;

import java.util.Objects;

/**
 * Representa una fila de vecindad.tsv:
 * county_name, county_id, adjacent_county_name, adjacent_county_id
 *
 * Indica que dos condados son vecinos. Algunas filas tienen el
 * mismo condado como origen y destino ("vecino de si mismo"); se
 * guardan tal cual.
 */
public class Vecindad implements Registro {

    private final String nombreOrigen;
    private final String fipsOrigen;
    private final String nombreDestino;
    private final String fipsDestino;

    public Vecindad(String nombreOrigen, String fipsOrigen,
                     String nombreDestino, String fipsDestino) {
        this.nombreOrigen = nombreOrigen;
        this.fipsOrigen = fipsOrigen;
        this.nombreDestino = nombreDestino;
        this.fipsDestino = fipsDestino;
    }

    public String getNombreOrigen() { return nombreOrigen; }
    public String getFipsOrigen() { return fipsOrigen; }
    public String getNombreDestino() { return nombreDestino; }
    public String getFipsDestino() { return fipsDestino; }

    /** true si esta fila dice que un condado es vecino de sí mismo. */
    public boolean esAutoVecindad() {
        return Objects.equals(fipsOrigen, fipsDestino);
    }

    @Override
    public String getClave() {
        return fipsOrigen + "-" + fipsDestino;
    }

    @Override
    public String mostrar() {
        return String.format("%-25s (fips=%-6s) --- vecino de --- %-25s (fips=%-6s)",
                nombreOrigen, fipsOrigen, nombreDestino, fipsDestino);
    }

    @Override
    public String toString() {
        return mostrar();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vecindad)) return false;
        Vecindad that = (Vecindad) o;
        return Objects.equals(fipsOrigen, that.fipsOrigen)
                && Objects.equals(fipsDestino, that.fipsDestino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fipsOrigen, fipsDestino);
    }
}