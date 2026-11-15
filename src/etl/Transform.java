package etl;

/**
 * Filtro del ETL: arregla lo que se puede arreglar y rechaza lo que no.
 *
 * Parte 2 (sabado): metodos pequenos de arreglo. No usan ni la cola
 * ni la pila; reciben un texto y devuelven el texto arreglado o una
 * senal (null / false) de que no sirve.
 */
public class Transform {

    /** Largo fijo de un codigo FIPS de condado. */
    public static final int LARGO_FIPS = 5;

    /** Quita espacios al inicio y al final. Si llega null devuelve "". */
    public static String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim();
    }

    /**
     * Deja el FIPS con 5 digitos, poniendo ceros a la izquierda.
     * "1001" -> "01001". Devuelve null si esta vacio, tiene algo que
     * no es digito o tiene mas de 5 digitos.
     * El FIPS es String: nunca se convierte a int (perderia el cero).
     */
    public static String arreglarFips(String texto) {
        String fips = limpiarTexto(texto);
        if (fips.isEmpty() || fips.length() > LARGO_FIPS || !soloDigitos(fips)) {
            return null;
        }
        while (fips.length() < LARGO_FIPS) {
            fips = "0" + fips;
        }
        return fips;
    }

    /**
     * Convierte un texto a entero. Si no es un numero devuelve null
     * (NO devuelve 0), para que el registro se pueda rechazar.
     */
    public static Integer leerNumero(String texto) {
        String limpio = limpiarTexto(texto);
        if (limpio.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * true si la fecha tiene la forma AAAA-MM-DD: 10 caracteres,
     * guiones en las posiciones 4 y 7, digitos en el resto,
     * mes entre 1 y 12 y dia entre 1 y 31.
     */
    public static boolean fechaValida(String texto) {
        String fecha = limpiarTexto(texto);
        if (fecha.length() != 10 || fecha.charAt(4) != '-' || fecha.charAt(7) != '-') {
            return false;
        }
        String anio = fecha.substring(0, 4);
        String mes = fecha.substring(5, 7);
        String dia = fecha.substring(8, 10);
        if (!soloDigitos(anio) || !soloDigitos(mes) || !soloDigitos(dia)) {
            return false;
        }
        int m = Integer.parseInt(mes);
        int d = Integer.parseInt(dia);
        return m >= 1 && m <= 12 && d >= 1 && d <= 31;
    }

    /** true si el texto no esta vacio y todos sus caracteres son digitos. */
    private static boolean soloDigitos(String texto) {
        if (texto.isEmpty()) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isDigit(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
