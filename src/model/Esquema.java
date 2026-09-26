package model;

/**
 * Guarda la cabecera exacta esperada de cada archivo de entrada y
 * la posición de cada columna, tal como vienen en la fuente
 * original (sin transformar).
 */
public final class Esquema {

    private Esquema() {
        // Clase de solo constantes: no se instancia.
    }

    // ---------- casos_diarios.csv ----------
    public static final String[] CABECERA_CASOS_DIARIOS = {
        "date", "county", "state", "fips", "cases", "deaths"
    };
    public static final int CASOS_COL_FECHA   = 0;
    public static final int CASOS_COL_CONDADO = 1;
    public static final int CASOS_COL_ESTADO  = 2;
    public static final int CASOS_COL_FIPS    = 3;
    public static final int CASOS_COL_CASOS   = 4;
    public static final int CASOS_COL_MUERTES = 5;

    // ---------- condados.csv ----------
    public static final String[] CABECERA_CONDADOS = {
        "fips", "name", "state"
    };
    public static final int CONDADOS_COL_FIPS   = 0;
    public static final int CONDADOS_COL_NOMBRE = 1;
    public static final int CONDADOS_COL_ESTADO = 2;

    // ---------- vecindad.tsv (separado por tabulación) ----------
    public static final String[] CABECERA_VECINDAD = {
        "county_name", "county_id", "adjacent_county_name", "adjacent_county_id"
    };
    public static final int VECINDAD_COL_NOMBRE_ORIGEN  = 0;
    public static final int VECINDAD_COL_FIPS_ORIGEN    = 1;
    public static final int VECINDAD_COL_NOMBRE_DESTINO = 2;
    public static final int VECINDAD_COL_FIPS_DESTINO   = 3;

    /**
     * Compara una cabecera leida del archivo contra la esperada,
     * ignorando espacios sobrantes al inicio/fin de cada columna.
     *
     * Uso tipico:
     *   Esquema.cabeceraValida(linea.split(","), Esquema.CABECERA_CASOS_DIARIOS)
     *   Esquema.cabeceraValida(linea.split("\t"), Esquema.CABECERA_VECINDAD)
     */
    public static boolean cabeceraValida(String[] leida, String[] esperada) {
        if (leida == null || esperada == null || leida.length != esperada.length) {
            return false;
        }
        for (int i = 0; i < esperada.length; i++) {
            String a = leida[i] == null ? "" : leida[i].trim();
            String b = esperada[i].trim();
            if (!a.equalsIgnoreCase(b)) {
                return false;
            }
        }
        return true;
    }
}