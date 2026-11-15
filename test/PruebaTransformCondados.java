import estructuras.Cola;
import etl.Conteo;
import model.RegistroCrudo;
import etl.Transform;
import model.Condado;
import model.TipoError;

/** Cola de condados armada a mano. */
public class PruebaTransformCondados {

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
        //                  fips     name              state
        cola.encolar(fila(2, "0",     "United States",  "NA"));  // pais
        cola.encolar(fila(3, "1000",  "Alabama",        "AL"));  // estado (termina en 000)
        cola.encolar(fila(4, "1001",  "Autauga County", "AL"));  // valido, reparado
        cola.encolar(fila(5, "06037", "Los Angeles County", "CA")); // valido tal cual
        cola.encolar(fila(6, "",      "Algo",           "TX"));  // fips vacio
        cola.encolar(fila(7, "48001"));                          // COLUMNAS_INCOMPLETAS

        Transform t = new Transform();
        t.transformarCondados(cola);
        Conteo k = t.getConteoCondados();

        revisar("leidos = 6", k.getLeidos() == 6);
        revisar("validos = 2", k.getValidos() == 2);
        revisar("3 NO_ES_CONDADO", k.getRechazados(TipoError.NO_ES_CONDADO) == 3);
        revisar("1 COLUMNAS_INCOMPLETAS", k.getRechazados(TipoError.COLUMNAS_INCOMPLETAS) == 1);
        revisar("leidos = validos + rechazados", k.getLeidos() == k.getValidos() + k.getRechazados());
        revisar("1 reparado", k.getReparados() == 1);

        Condado autauga = t.getCondadosValidos().obtener(0);
        revisar("1001 quedo 01001", autauga.getFips().equals("01001"));
        revisar("los conteos de casos no se tocan", t.getConteoCasos().getLeidos() == 0);

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
    }
}
