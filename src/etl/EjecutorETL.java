package etl;

import java.io.IOException;

import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import model.CasoDiario;
import model.Condado;
import model.ErrorETL;
import model.RegistroCrudo;
import model.ResumenETL;
import model.TipoError;

/**
 * Corre el proceso ETL completo, o paso por paso:
 *
 *   extraer()     -> lee los archivos y mete cada fila cruda en una Cola (Extract, de Joel)
 *   transformar() -> saca de la cola: arregla o rechaza (Transform, de Luis)
 *   cargar()      -> escribe los archivos y el reporte en output/ (Load)
 *
 * El menu solo llama a esta clase y muestra lo que devuelve: aqui no se
 * imprime nada. Cada paso avisa con IllegalStateException si falta el anterior.
 * Los numeros del ResumenETL salen de los contadores (Conteo) de Transform.
 */
public class EjecutorETL {

    private final String rutaCasos;
    private final String rutaCondados;
    private final String rutaVecindad;
    private final Load load;

    private Extract extract;
    private Transform transform;
    private Cola<RegistroCrudo> colaCasos;
    private Cola<RegistroCrudo> colaCondados;
    private Cola<RegistroCrudo> colaVecindad;

    private long tiempoMs;
    private boolean extraido;
    private boolean transformado;
    private ResumenETL resumen;

    public EjecutorETL(String rutaCasos, String rutaCondados, String rutaVecindad, String carpetaSalida) {
        this.rutaCasos = rutaCasos;
        this.rutaCondados = rutaCondados;
        this.rutaVecindad = rutaVecindad;
        this.load = new Load(carpetaSalida);
    }

    // ---------------------------------------------------------------
    // Los tres pasos
    // ---------------------------------------------------------------

    /** Extract: lee los tres archivos y encola cada fila sin revisarla. */
    public void extraer() throws IOException {
        long inicio = System.currentTimeMillis();

        extract = new Extract();
        transform = new Transform();
        colaCondados = new Cola<>();
        colaCasos = new Cola<>();
        colaVecindad = new Cola<>();
        resumen = null;
        extraido = false;
        transformado = false;
        tiempoMs = 0;

        extract.encolarCondados(rutaCondados, colaCondados);
        extract.encolarCasos(rutaCasos, colaCasos);
        extract.encolarVecindad(rutaVecindad, colaVecindad);

        exigirFilas(colaCondados, rutaCondados);
        exigirFilas(colaCasos, rutaCasos);
        exigirFilas(colaVecindad, rutaVecindad);

        extraido = true;
        tiempoMs += System.currentTimeMillis() - inicio;
    }

    private void exigirFilas(Cola<RegistroCrudo> cola, String ruta) throws IOException {
        if (cola.estaVacia()) {
            throw new IOException("No se encolo ninguna fila de " + ruta
                    + " (archivo vacio o cabecera distinta a la esperada)");
        }
    }

    /** Transform: vacia las colas; cada fila va a la lista de validos o a la pila de errores. */
    public void transformar() {
        if (!extraido) {
            throw new IllegalStateException("Primero extraiga los datos (paso Extract)");
        }
        if (transformado) {
            throw new IllegalStateException("Los datos ya fueron transformados; vuelva a extraer para repetir");
        }
        long inicio = System.currentTimeMillis();

        transform.transformarCondados(colaCondados);
        transform.transformarCasos(colaCasos);
        transform.transformarVecindad(colaVecindad);

        tiempoMs += System.currentTimeMillis() - inicio;
        transformado = true;
        resumen = armarResumen();
    }

    /** Load: escribe los archivos limpios, los errores y el reporte. Devuelve el resumen. */
    public ResumenETL cargar() throws IOException {
        verTransformado();
        load.escribirCasos(transform.getCasosValidos());
        load.escribirCondados(transform.getCondadosValidos());
        load.escribirVecindad(transform.getGrafo());
        load.escribirErrores(transform.getErrores());
        load.escribirReporte(resumen);
        return resumen;
    }

    /** Hace los tres pasos seguidos. */
    public ResumenETL ejecutarTodo() throws IOException {
        extraer();
        transformar();
        return cargar();
    }

    // ---------------------------------------------------------------
    // El resumen: solo se copian los numeros de Conteo
    // ---------------------------------------------------------------

    private ResumenETL armarResumen() {
        ResumenETL r = new ResumenETL();
        copiar(transform.getConteoCasos(), r.getCasos());
        copiar(transform.getConteoCondados(), r.getCondados());
        copiar(transform.getConteoVecindad(), r.getVecindad());
        r.setTiempoMs(tiempoMs);
        return r;
    }

    private void copiar(Conteo origen, ResumenETL.Contadores destino) {
        destino.setLeidos(origen.getLeidos());
        destino.setValidos(origen.getValidos());
        destino.setReparados(origen.getReparados());
        destino.setAdvertencias(origen.getAdvertencias());
        destino.setRechazados(origen.getRechazados());
        for (TipoError tipo : TipoError.values()) {
            destino.setRechazadosPorTipo(tipo, origen.getRechazados(tipo));
        }
    }

    // ---------------------------------------------------------------
    // Lo que el menu necesita para mostrar
    // ---------------------------------------------------------------

    public boolean estaExtraido() { return extraido; }
    public boolean estaTransformado() { return transformado; }

    public Cola<RegistroCrudo> getColaCasos() { return colaCasos; }
    public Cola<RegistroCrudo> getColaCondados() { return colaCondados; }
    public Cola<RegistroCrudo> getColaVecindad() { return colaVecindad; }

    public ListaEnlazada<CasoDiario> getCasosValidos() {
        verTransformado();
        return transform.getCasosValidos();
    }

    public ListaEnlazada<Condado> getCondadosValidos() {
        verTransformado();
        return transform.getCondadosValidos();
    }

    public Grafo<Condado> getGrafo() {
        verTransformado();
        return transform.getGrafo();
    }

    public Pila<ErrorETL> getErrores() {
        verTransformado();
        return transform.getErrores();
    }

    public ResumenETL getResumen() {
        verTransformado();
        return resumen;
    }

    public Load getLoad() { return load; }

    private void verTransformado() {
        if (!transformado) {
            throw new IllegalStateException("Primero transforme los datos (paso Transform)");
        }
    }
}
