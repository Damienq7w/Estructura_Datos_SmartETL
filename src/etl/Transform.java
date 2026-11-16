package etl;

import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import model.CasoDiario;
import model.Condado;
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
    private final ListaEnlazada<Condado> condadosValidos = new ListaEnlazada<>();
    private final Pila<ErrorETL> errores = new Pila<>();

    private final Conteo conteoCasos = new Conteo();
    private final Conteo conteoCondados = new Conteo();
    private final Conteo conteoVecindad = new Conteo();

    // Grafo de condados vecinos (no dirigido): cada condado es un vertice
    private final Grafo<Condado> grafo = new Grafo<>();

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
    // Parte 4: condados
    // =====================================================================

    /** Vacia la cola de condados con el mismo recorrido que los casos. */
    public void transformarCondados(Cola<RegistroCrudo> cola) {
        while (!cola.estaVacia()) {
            RegistroCrudo registro = cola.desencolar();
            conteoCondados.sumarLeido();

            String[] c = registro.getCampos();
            int linea = registro.getNumeroLinea();

            ErrorETL error = reglaColumnas("condados", linea, c, Esquema.CABECERA_CONDADOS.length);
            if (error == null) error = reglaEsCondado(linea, c);

            if (error != null) {
                rechazar(error, conteoCondados);
            } else {
                condadosValidos.insertar(crearCondado(c));
                conteoCondados.sumarValido();
            }
        }
    }

    /**
     * Regla 2 de condados: la fila es un pais o un estado, no un condado.
     * Lo es si el FIPS esta vacio, es 0, termina en 000 o el estado es NA.
     */
    private ErrorETL reglaEsCondado(int linea, String[] c) {
        String fips = limpiarTexto(c[Esquema.CONDADOS_COL_FIPS]);
        String estado = limpiarTexto(c[Esquema.CONDADOS_COL_ESTADO]);
        boolean noEsCondado = fips.isEmpty()
                || fips.equals("0")
                || fips.endsWith("000")
                || estado.equals("NA")
                || arreglarFips(fips) == null;   // letras o mas de 5 digitos
        if (noEsCondado) {
            return new ErrorETL("condados", linea, TipoError.NO_ES_CONDADO,
                    "fips = " + fips + ", estado = " + estado);
        }
        return null;
    }

    /** Regla 3 de condados: el FIPS corto se arregla y cuenta como reparado. */
    private Condado crearCondado(String[] c) {
        String fipsOriginal = limpiarTexto(c[Esquema.CONDADOS_COL_FIPS]);
        String fips = arreglarFips(fipsOriginal);
        if (!fips.equals(fipsOriginal)) {
            conteoCondados.sumarReparado();
        }
        return new Condado(fips,
                limpiarTexto(c[Esquema.CONDADOS_COL_NOMBRE]),
                limpiarTexto(c[Esquema.CONDADOS_COL_ESTADO]));
    }

    // =====================================================================
    // Parte 7: vecindad y construccion del grafo
    // =====================================================================

    /**
     * Vacia la cola de vecindad y construye el grafo de condados vecinos.
     *
     * Cada fila dice "origen colinda con destino". El archivo trae cada
     * par dos veces (A-B y B-A) y cada condado como vecino de si mismo,
     * asi que de 22.200 filas solo 9.483 son aristas nuevas.
     */
    public void transformarVecindad(Cola<RegistroCrudo> cola) {
        while (!cola.estaVacia()) {
            RegistroCrudo registro = cola.desencolar();
            conteoVecindad.sumarLeido();

            String[] c = registro.getCampos();
            int linea = registro.getNumeroLinea();

            ErrorETL error = reglaColumnas("vecindad", linea, c, Esquema.CABECERA_VECINDAD.length);
            if (error == null) error = reglaFipsVecindad(linea, c);
            if (error != null) {
                rechazar(error, conteoVecindad);
                continue;
            }

            // 1. Arreglar los dos FIPS a 5 digitos
            Condado origen = crearCondadoVecindad(c, Esquema.VECINDAD_COL_FIPS_ORIGEN,
                    Esquema.VECINDAD_COL_NOMBRE_ORIGEN);
            Condado destino = crearCondadoVecindad(c, Esquema.VECINDAD_COL_FIPS_DESTINO,
                    Esquema.VECINDAD_COL_NOMBRE_DESTINO);

            // Los vertices se agregan siempre (si no existen), aun en la fila
            // "vecino de si mismo": asi un condado sin vecinos (una isla)
            // tambien queda en el grafo.
            grafo.agregarVertice(origen);
            grafo.agregarVertice(destino);

            // 2. Un condado vecino de si mismo
            if (origen.equals(destino)) {
                rechazar(new ErrorETL("vecindad", linea, TipoError.AUTO_VECINDAD,
                        "fips = " + origen.getFips()), conteoVecindad);
                continue;
            }

            // 3. Si la arista ya existia (llego antes como B-A): duplicado.
            //    agregarArista solo mira la lista de vecinos del origen.
            if (grafo.agregarArista(origen, destino)) {
                conteoVecindad.sumarValido();
            } else {
                rechazar(new ErrorETL("vecindad", linea, TipoError.DUPLICADO,
                        origen.getFips() + " - " + destino.getFips()), conteoVecindad);
            }
        }
    }

    /** Los dos FIPS tienen que poder arreglarse a 5 digitos. */
    private ErrorETL reglaFipsVecindad(int linea, String[] c) {
        String fipsOrigen = c[Esquema.VECINDAD_COL_FIPS_ORIGEN];
        String fipsDestino = c[Esquema.VECINDAD_COL_FIPS_DESTINO];
        if (arreglarFips(fipsOrigen) == null || arreglarFips(fipsDestino) == null) {
            return new ErrorETL("vecindad", linea, TipoError.NO_ES_CONDADO,
                    "fips = " + limpiarTexto(fipsOrigen) + " / " + limpiarTexto(fipsDestino));
        }
        return null;
    }

    /**
     * Crea el Condado de una columna de la vecindad, con el FIPS ya
     * arreglado. El nombre viene como "Autauga County, AL": lo que va
     * despues de la ultima coma es el estado.
     */
    private Condado crearCondadoVecindad(String[] c, int colFips, int colNombre) {
        String fipsOriginal = limpiarTexto(c[colFips]);
        String fips = arreglarFips(fipsOriginal);
        if (!fips.equals(fipsOriginal)) {
            conteoVecindad.sumarReparado();
        }
        String nombreCompleto = limpiarTexto(c[colNombre]);
        String nombre = nombreCompleto;
        String estado = "";
        int coma = nombreCompleto.lastIndexOf(',');
        if (coma >= 0) {
            nombre = nombreCompleto.substring(0, coma).trim();
            estado = nombreCompleto.substring(coma + 1).trim();
        }
        return new Condado(fips, nombre, estado);
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
    public ListaEnlazada<Condado> getCondadosValidos() { return condadosValidos; }
    public Pila<ErrorETL> getErrores() { return errores; }
    public Conteo getConteoCasos() { return conteoCasos; }
    public Conteo getConteoCondados() { return conteoCondados; }
    public Conteo getConteoVecindad() { return conteoVecindad; }

    /** El grafo de condados vecinos, para Joel (BFS) y Kenneth (DFS). */
    public Grafo<Condado> getGrafo() { return grafo; }

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
