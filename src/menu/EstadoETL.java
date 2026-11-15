package menu;

import estructuras.ListaEnlazada;
import etl.Extract;
import model.CasoDiario;
import model.Condado;
import model.Vecindad;

public class EstadoETL {

    public static final String RUTA_CASOS = "data/casos_diarios.csv";
    public static final String RUTA_CONDADOS = "data/condados.csv";
    public static final String RUTA_VECINDAD = "data/vecindad.tsv";

    private final Extract extract = new Extract();

    private ListaEnlazada<CasoDiario> casos;
    private ListaEnlazada<Condado> condados;
    private ListaEnlazada<Vecindad> vecindad;

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
