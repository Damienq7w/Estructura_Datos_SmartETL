package model;

import java.util.Comparator;
import java.util.Objects;

/**
 * Representa una fila de condados.csv: fips,name,state
 *
 * El fips se guarda tal como viene (puede tener solo 4 digitos);
 * completarlo a 5 digitos con un cero es trabajo del Transform
 * no de este molde.
 */
public class Condado implements Registro, Comparable<Condado> {

    private final String fips;
    private final String nombre;
    private final String estado;

    public Condado(String fips, String nombre, String estado) {
        this.fips = fips;
        this.nombre = nombre;
        this.estado = estado;
    }

    public String getFips() { return fips; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }

    @Override
    public String getClave() {
        return fips;
    }

    @Override
    public String mostrar() {
        return String.format("fips=%-6s | %-25s | %s", fips, nombre, estado);
    }

    @Override
    public String toString() {
        return mostrar();
    }

    /**
     * Compara dos condados por su fips. Esta comparación sera la
     * base de la clave del árbol.
     */
    @Override
    public int compareTo(Condado otro) {
        return Comparator.comparing(Condado::getFips).compare(this, otro);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Condado)) return false;
        Condado that = (Condado) o;
        return Objects.equals(fips, that.fips);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fips);
    }
}