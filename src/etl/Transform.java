package etl;

import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import model.CasoDiario;
import model.ErrorETL;
import model.RegistroCrudo;
import model.Esquema;
import model.TipoError;

/**
 * Filtro del ETL: arregla lo que se puede arreglar y rechaza lo que no.
 *
 * Recibe las colas de registros crudos que deja Extract y separa:
 *  - los validos, en una ListaEnlazada (ya arreglados);
 *  - los rechazados, en la Pila de errores, cada uno con su motivo.
 * Ademas lleva los contadores que Sara usa en el reporte.
 */
public class Transform {

    /** Largo fijo de un codigo FIPS de condado. */
    public static final int LARGO_FIPS = 5;

    /** Valor que se guarda en "muertes" cuando el campo viene vacio. */
    public static final int SIN_DATO = -1;

    private final ListaEnlazada<CasoDiario> casosValidos = new ListaEnlazada<>();
    private final Pila<ErrorETL> errores = new Pila<>();

    private final Conteo conteoCasos = new Conteo();

    // =====================================================================
    // Parte 3: casos diarios
    // =====================================================================

    /**
     * Vacia la cola de casos: cada registro termina en la lista de
     * validos o en la pila de errores.
     */
    public void transformarCasos(Cola<RegistroCrudo> cola) {
        while (!cola.estaVacia()) {
            RegistroCrudo registro = cola.desencolar();
            conteoCasos.sumarLeido();

            String[] c = registro.getCampos();
            int linea = registro.getNumeroLinea();

            // Las reglas en orden: la primera que falla decide el rechazo
            ErrorETL error = reglaColumnas("casos", linea, c, Esquema.CABECERA_CASOS_DIARIOS.length);
            if (error == null) error = reglaFipsCaso(linea, c);
            if (error == null) error = reglaFecha(linea, c);
            if (error == null) error = reglaCasos(linea, c);
            if (error == null) error = reglaMuertes(linea, c);

            if (error != null) {
                rechazar(error, conteoCasos);
            } else {
                casosValidos.insertar(crearCaso(c));
                conteoCasos.sumarValido();
            }
        }
    }

    /** Regla 1 (comun a todos los archivos): la fila trae todas las columnas. */
    private ErrorETL reglaColumnas(String archivo, int linea, String[] c, int esperadas) {
        int recibidas = (c == null) ? 0 : c.length;
        if (recibidas < esperadas) {
            return new ErrorETL(archivo, linea, TipoError.COLUMNAS_INCOMPLETAS,
                    "columnas = " + recibidas + ", se esperaban " + esperadas);
        }
        return null;
    }

    /** Regla 2: sin FIPS no se sabe de que condado es el registro. */
    private ErrorETL reglaFipsCaso(int linea, String[] c) {
        if (arreglarFips(c[Esquema.CASOS_COL_FIPS]) != null) {
            return null;
        }
        String condado = limpiarTexto(c[Esquema.CASOS_COL_CONDADO]);
        TipoError tipo = condado.equalsIgnoreCase("Unknown")
                ? TipoError.CONDADO_DESCONOCIDO
                : TipoError.AREA_SIN_FIPS;
        return new ErrorETL("casos", linea, tipo,
                "fips vacio, condado = " + condado);
    }

    /** Regla 3: la fecha tiene la forma AAAA-MM-DD. */
    private ErrorETL reglaFecha(int linea, String[] c) {
        String fecha = c[Esquema.CASOS_COL_FECHA];
        if (!fechaValida(fecha)) {
            return new ErrorETL("casos", linea, TipoError.FECHA_INVALIDA,
                    "fecha = " + limpiarTexto(fecha));
        }
        return null;
    }

    /** Regla 4: los casos son un numero y no son negativos. */
    private ErrorETL reglaCasos(int linea, String[] c) {
        String texto = c[Esquema.CASOS_COL_CASOS];
        Integer casos = leerNumero(texto);
        if (casos == null) {
            return new ErrorETL("casos", linea, TipoError.VALOR_NO_NUMERICO,
                    "casos = " + limpiarTexto(texto));
        }
        if (casos < 0) {
            return new ErrorETL("casos", linea, TipoError.VALOR_NEGATIVO,
                    "casos = " + casos);
        }
        return null;
    }

    /**
     * Regla 5: las muertes. Vacio es valido (advertencia, se guarda
     * SIN_DATO); texto que no es numero o negativo se rechaza.
     */
    private ErrorETL reglaMuertes(int linea, String[] c) {
        String texto = limpiarTexto(c[Esquema.CASOS_COL_MUERTES]);
        if (texto.isEmpty()) {
            return null;
        }
        Integer muertes = leerNumero(texto);
        if (muertes == null) {
            return new ErrorETL("casos", linea, TipoError.VALOR_NO_NUMERICO,
                    "muertes = " + texto);
        }
        if (muertes < 0) {
            return new ErrorETL("casos", linea, TipoError.VALOR_NEGATIVO,
                    "muertes = " + muertes);
        }
        return null;
    }

    /**
     * Crea el CasoDiario ya arreglado. Solo se llama cuando el registro
     * paso todas las reglas. No se cambia el registro: se crea uno nuevo.
     */
    private CasoDiario crearCaso(String[] c) {
        String fipsOriginal = limpiarTexto(c[Esquema.CASOS_COL_FIPS]);
        String fips = arreglarFips(fipsOriginal);
        if (!fips.equals(fipsOriginal)) {
            conteoCasos.sumarReparado();
        }

        int muertes;
        String textoMuertes = limpiarTexto(c[Esquema.CASOS_COL_MUERTES]);
        if (textoMuertes.isEmpty()) {
            muertes = SIN_DATO;
            conteoCasos.sumarAdvertencia();
        } else {
            muertes = leerNumero(textoMuertes);
        }

        return new CasoDiario(
                limpiarTexto(c[Esquema.CASOS_COL_FECHA]),
                limpiarTexto(c[Esquema.CASOS_COL_CONDADO]),
                limpiarTexto(c[Esquema.CASOS_COL_ESTADO]),
                fips,
                leerNumero(c[Esquema.CASOS_COL_CASOS]),
                muertes);
    }

    // =====================================================================
    // Comun
    // =====================================================================

    /** Apila el error y lo cuenta en el conteo del archivo. */
    private void rechazar(ErrorETL error, Conteo conteo) {
        errores.apilar(error);
        conteo.sumarRechazado(error.getTipo());
    }

    public ListaEnlazada<CasoDiario> getCasosValidos() { return casosValidos; }
    public Pila<ErrorETL> getErrores() { return errores; }
    public Conteo getConteoCasos() { return conteoCasos; }

    // =====================================================================
    // Parte 2: metodos de arreglo (sabado)
    // =====================================================================

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
