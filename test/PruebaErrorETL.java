import model.ErrorETL;
import model.TipoError;

public class PruebaErrorETL {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    public static void main(String[] args) {
        ErrorETL e = new ErrorETL("casos", 15, TipoError.VALOR_NO_NUMERICO, "casos = abc");

        revisar("getArchivo", e.getArchivo().equals("casos"));
        revisar("getLinea", e.getLinea() == 15);
        revisar("getTipo", e.getTipo() == TipoError.VALOR_NO_NUMERICO);
        revisar("getDetalle", e.getDetalle().equals("casos = abc"));
        revisar("mostrar", e.mostrar().equals("casos | linea 15 | VALOR_NO_NUMERICO | casos = abc"));
        revisar("hay 9 tipos de error", TipoError.values().length == 9);

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
    }
}
