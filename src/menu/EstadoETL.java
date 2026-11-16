package menu;

import estructuras.ListaEnlazada;
import etl.EjecutorETL;
import etl.Extract;
import etl.Load;
import model.CasoDiario;
import model.Condado;
import model.Vecindad;

public class EstadoETL {

    public static final String RUTA_CASOS = "data/casos_diarios.csv";
    public static final String RUTA_CONDADOS = "data/condados.csv";
    public static final String RUTA_VECINDAD = "data/vecindad.tsv";

    // datos pequenos con errores a proposito (se cuentan a mano en su LEEME.md)
    public static final String RUTA_PRUEBA_CASOS = "data/pruebas/casos_prueba.csv";
    public static final String RUTA_PRUEBA_CONDADOS = "data/pruebas/condados_prueba.csv";
    public static final String RUTA_PRUEBA_VECINDAD = "data/pruebas/vecindad_prueba.tsv";
    public static final String CARPETA_SALIDA_PRUEBA = "output/pruebas";

    private final Extract extract = new Extract();

    private ListaEnlazada<CasoDiario> casos;
    private ListaEnlazada<Condado> condados;
    private ListaEnlazada<Vecindad> vecindad;

    // proceso ETL con cola, Transform y Load (opciones 10 a 16 del menu)
    private EjecutorETL etl;

    /** Empieza un ETL nuevo con la base real o con los datos de prueba. */
    public EjecutorETL nuevoEtl(boolean datosDePrueba) {
        if (datosDePrueba) {
            etl = new EjecutorETL(RUTA_PRUEBA_CASOS, RUTA_PRUEBA_CONDADOS, RUTA_PRUEBA_VECINDAD, CARPETA_SALIDA_PRUEBA);
        } else {
            etl = new EjecutorETL(RUTA_CASOS, RUTA_CONDADOS, RUTA_VECINDAD, Load.CARPETA_POR_DEFECTO);
        }
        return etl;
    }

    /** El ETL en curso, o null si todavia no se ha empezado ninguno. */
    public EjecutorETL getEtl() {
        return etl;
    }

    public Extract getExtract() {
        return extract;
    }

    public ListaEnlazada<CasoDiario> getCasos() {
        return casos;
    }

    public void setCasos(ListaEnlazada<CasoDiario> casos) {
        this.casos = casos;
    }

    public ListaEnlazada<Condado> getCondados() {
        return condados;
    }

    public void setCondados(ListaEnlazada<Condado> condados) {
        this.condados = condados;
    }

    public ListaEnlazada<Vecindad> getVecindad() {
        return vecindad;
    }

    public void setVecindad(ListaEnlazada<Vecindad> vecindad) {
        this.vecindad = vecindad;
    }

    public boolean hayCasos() {
        return casos != null;
    }
}
