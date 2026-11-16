import estructuras.Cola;
import etl.Conteo;
import model.RegistroCrudo;
import etl.Transform;
import model.CasoDiario;
import model.ErrorETL;
import model.TipoError;

/** Cola armada a mano: cada fila debe terminar donde corresponde. */
public class PruebaTransformCasos {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    private static RegistroCrudo fila(int linea, String... campos) {
        return new RegistroCrudo(linea, campos);
    }

    public static void main(String[] args) {
        Cola<RegistroCrudo> cola = new Cola<>();
        //                  date          county          state       fips     cases  deaths
        cola.encolar(fila(2, "2020-03-15", "Autauga",      "Alabama",  "1001",  "5",   "0"));  // buena (fips reparado)
        cola.encolar(fila(3, "2020-03-15", "Unknown",      "Alabama",  "",      "2",   "0"));  // CONDADO_DESCONOCIDO
        cola.encolar(fila(4, "2020-03-15", "New York City","New York", "",      "9",   "1"));  // AREA_SIN_FIPS
        cola.encolar(fila(5, "2020-03-15", "Baldwin",      "Alabama",  "01003", "abc", "0"));  // VALOR_NO_NUMERICO
        cola.encolar(fila(6, "2020-03-15", "Adjuntas",     "Puerto Rico", "72001", "3", ""));  // muertes vacias: valida
        cola.encolar(fila(7, "2020-03-15", "Barbour",      "Alabama",  "01005", "-4",  "0"));  // VALOR_NEGATIVO
        cola.encolar(fila(8, "2020-13-45", "Bibb",         "Alabama",  "01007", "1",   "0"));  // FECHA_INVALIDA
        cola.encolar(fila(9, "2020-03-15", "Blount",       "Alabama"));                        // COLUMNAS_INCOMPLETAS

        Transform t = new Transform();
        t.transformarCasos(cola);
        Conteo k = t.getConteoCasos();

        revisar("la cola queda vacia", cola.estaVacia());
        revisar("leidos = 8", k.getLeidos() == 8);
        revisar("validos = 2", k.getValidos() == 2);
        revisar("rechazados = 6", k.getRechazados() == 6);
        revisar("leidos = validos + rechazados", k.getLeidos() == k.getValidos() + k.getRechazados());
        revisar("1 reparado (1001 -> 01001)", k.getReparados() == 1);
        revisar("1 advertencia (muertes vacias)", k.getAdvertencias() == 1);

        revisar("1 CONDADO_DESCONOCIDO", k.getRechazados(TipoError.CONDADO_DESCONOCIDO) == 1);
        revisar("1 AREA_SIN_FIPS", k.getRechazados(TipoError.AREA_SIN_FIPS) == 1);
        revisar("1 VALOR_NO_NUMERICO", k.getRechazados(TipoError.VALOR_NO_NUMERICO) == 1);
        revisar("1 VALOR_NEGATIVO", k.getRechazados(TipoError.VALOR_NEGATIVO) == 1);
        revisar("1 FECHA_INVALIDA", k.getRechazados(TipoError.FECHA_INVALIDA) == 1);
        revisar("1 COLUMNAS_INCOMPLETAS", k.getRechazados(TipoError.COLUMNAS_INCOMPLETAS) == 1);

        CasoDiario primero = t.getCasosValidos().obtener(0);
        revisar("el fips valido quedo 01001", primero.getFips().equals("01001"));
        CasoDiario sinMuertes = t.getCasosValidos().obtener(1);
        revisar("muertes vacias se guardan como -1", sinMuertes.getMuertes() == Transform.SIN_DATO);

        // El ultimo apilado es el de la linea 9
        ErrorETL tope = t.getErrores().desapilar();
        revisar("el tope de la pila es la linea 9", tope.getLinea() == 9);
        revisar("el error trae el archivo 'casos'", tope.getArchivo().equals("casos"));

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
    }
}
