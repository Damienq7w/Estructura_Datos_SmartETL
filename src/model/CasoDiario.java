package model;

import java.util.Objects;

/**
 * Representa una fila de casos_diarios.csv:
 * date,county,state,fips,cases,deaths
 */
public class CasoDiario implements Registro {

    private final String fecha;
    private final String condado;
    private final String estado;
    private final String fips;
    private final int casos;
    private final int muertes;

    public CasoDiario(String fecha, String condado, String estado,
                       String fips, int casos, int muertes) {
        this.fecha = fecha;
        this.condado = condado;
        this.estado = estado;
        this.fips = fips;
        this.casos = casos;
        this.muertes = muertes;
    }

    /**
     * Construye un CasoDiario a partir de los textos crudos de una fila,
     * interpretando "muertes" vacío como -1 (dato faltante).
     */
    public static CasoDiario desdeTexto(String fecha, String condado, String estado,
                                         String fips, String casosTexto, String muertesTexto) {
        int casos = (casosTexto == null || casosTexto.isBlank())
                ? -1 : Integer.parseInt(casosTexto.trim());
        int muertes = (muertesTexto == null || muertesTexto.isBlank())
                ? -1 : Integer.parseInt(muertesTexto.trim());
        return new CasoDiario(fecha, condado, estado, fips, casos, muertes);
    }

    public String getFecha() { return fecha; }
    public String getCondado() { return condado; }
    public String getEstado() { return estado; }
    public String getFips() { return fips; }
    public int getCasos() { return casos; }
    public int getMuertes() { return muertes; }

    @Override
    public String getClave() {
        return fips + "-" + fecha;
    }

    @Override
    public String mostrar() {
        return String.format("%s | %-20s | %-3s | fips=%-6s | casos=%-8d | muertes=%d",
                fecha, condado, estado, fips, casos, muertes);
    }

    @Override
    public String toString() {
        return mostrar();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CasoDiario)) return false;
        CasoDiario that = (CasoDiario) o;
        return casos == that.casos && muertes == that.muertes
                && Objects.equals(fecha, that.fecha)
                && Objects.equals(condado, that.condado)
                && Objects.equals(estado, that.estado)
                && Objects.equals(fips, that.fips);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, condado, estado, fips, casos, muertes);
    }
}