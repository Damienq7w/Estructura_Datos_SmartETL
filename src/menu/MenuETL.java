package menu;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import algoritmos.Busqueda;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.Cola;
import etl.EjecutorETL;
import etl.Extract;
import model.CasoDiario;
import model.Esquema;
import model.RegistroCrudo;
import model.ResumenETL;
import model.TipoError;

public class MenuETL {

    private final EstadoETL estado;

    public MenuETL(EstadoETL estado) {
        this.estado = estado;
    }

    public boolean ejecutar(int opcion, Scanner teclado) {
        switch (opcion) {
            case 1:
                cargarCasos();
                return true;
            case 2:
                mostrarRegistros();
                return true;
            case 3:
                buscarPorFipsYFecha(teclado);
                return true;
            case 4:
                buscarPorNombre(teclado);
                return true;
            case 5:
                mostrarIncidencias();
                return true;
            case 6:
                mostrarEstadisticas();
                return true;
            case 7:
                verCondadosYVecindad();
                return true;
            case 8:
                compararListas();
                return true;
            case 9:
                mostrarEsquema();
                return true;
            case 10:
                ejecutarEtlCompleto(teclado);
                return true;
            case 11:
                etlExtraer(teclado);
                return true;
            case 12:
                etlMostrarCola();
                return true;
            case 13:
                etlTransformar();
                return true;
            case 14:
                etlMostrarValidos();
                return true;
            case 15:
                etlMostrarPilaErrores();
                return true;
            case 16:
                etlCargar();
                return true;
            default:
                return false;
        }
    }

    private boolean sinCargar() {
        if (!estado.hayCasos()) {
            System.out.println("Primero ejecute la opcion 1");
            return true;
        }
        return false;
    }

    private void cargarCasos() {
        if (estado.hayCasos()) {
            System.out.println("Los casos ya estaban cargados, no se vuelven a leer");
            return;
        }

        Extract extract = estado.getExtract();
        long inicio = System.currentTimeMillis();
        try {
            estado.setCasos(extract.cargarCasos(EstadoETL.RUTA_CASOS));
        } catch (IOException e) {
            System.out.println("Error leyendo " + EstadoETL.RUTA_CASOS + ": " + e.getMessage());
            return;
        }
        long tiempoMs = System.currentTimeMillis() - inicio;

        System.out.println("Filas leidas: " + extract.getCasosLeidas());
        System.out.println("Filas cargadas: " + extract.getCasosCargadas());
        System.out.println("Filas descartadas: " + extract.getCasosDescartadas());
        System.out.println("Tiempo de carga: " + tiempoMs + " ms");
        System.out.println("Sin FIPS: " + extract.getCasosSinFips());
        System.out.println("Condado Unknown: " + extract.getCasosCondadoUnknown());
        System.out.println("Muertes vacias: " + extract.getCasosMuertesVacias());
        System.out.println("Estos casos se procesaran en el Transform (Hito 2)");
    }

    private void mostrarRegistros() {
        if (sinCargar()) {
            return;
        }
        estado.getCasos().mostrar(10);
    }

    private void buscarPorFipsYFecha(Scanner teclado) {
        if (sinCargar()) {
            return;
        }
        System.out.print("FIPS: ");
        String fips = teclado.nextLine().trim();
        System.out.print("Fecha (aaaa-mm-dd): ");
        String fecha = teclado.nextLine().trim();

        Busqueda busqueda = new Busqueda();
        CasoDiario encontrado = busqueda.porFipsYFecha(estado.getCasos(), fips, fecha);

        if (encontrado == null) {
            System.out.println("No se encontro ese registro");
        } else {
            System.out.println(encontrado.mostrar());
        }
        System.out.println("Comparaciones realizadas: " + busqueda.getComparaciones());
    }

    private void buscarPorNombre(Scanner teclado) {
        if (sinCargar()) {
            return;
        }
        System.out.print("Nombre del condado: ");
        String nombre = teclado.nextLine().trim();

        Busqueda busqueda = new Busqueda();
        ListaEnlazada<CasoDiario> encontrados = busqueda.porCondado(estado.getCasos(), nombre);

        System.out.println("Encontrados: " + encontrados.tamanio());
        encontrados.mostrar(10);
        System.out.println("Comparaciones realizadas: " + busqueda.getComparaciones());
    }

    private void mostrarIncidencias() {
        if (sinCargar()) {
            return;
        }
        ListaEnlazada<String> incidencias = estado.getExtract().getIncidencias();
        System.out.println("Total de incidencias registradas: " + incidencias.tamanio());
        incidencias.mostrar(10);
    }

    private void mostrarEstadisticas() {
        if (sinCargar()) {
            return;
        }
        System.out.println("Total de registros cargados: " + estado.getCasos().tamanio());
    }

    private void verCondadosYVecindad() {
        Extract extract = estado.getExtract();

        if (estado.getCondados() == null || estado.getVecindad() == null) {
            try {
                estado.setCondados(extract.cargarCondados(EstadoETL.RUTA_CONDADOS));
                estado.setVecindad(extract.cargarVecindad(EstadoETL.RUTA_VECINDAD));
            } catch (IOException e) {
                System.out.println("Error leyendo condados/vecindad: " + e.getMessage());
                return;
            }

            System.out.println("Filas leidas de condados.csv: " + extract.getCondadosLeidas());
            System.out.println("Filas cargadas de condados.csv: " + extract.getCondadosCargadas());
            System.out.println("Filas que no son condados: " + extract.getCondadosNoSonCondado());
            System.out.println("FIPS sin cero: " + extract.getCondadosFipsSinCero());
            System.out.println("Filas leidas de vecindad.tsv: " + extract.getVecindadLeidas());
            System.out.println("Filas cargadas de vecindad.tsv: " + extract.getVecindadCargadas());
            System.out.println("Condado vecino de si mismo: " + extract.getVecindadVecinoDeSiMismo());
            System.out.println("Vecindades repetidas: " + extract.getVecindadRepetida());
        }

        System.out.println();
        System.out.println("Condados:");
        estado.getCondados().mostrar(10);
        System.out.println("Vecindad:");
        estado.getVecindad().mostrar(10);
    }

    private void compararListas() {
        if (sinCargar()) {
            return;
        }

        ListaSecuencial<CasoDiario> secuencial = new ListaSecuencial<>();
        long inicio = System.currentTimeMillis();
        for (CasoDiario caso : estado.getCasos()) {
            secuencial.insertar(caso);
        }
        long tiempoMs = System.currentTimeMillis() - inicio;

        System.out.println("ListaEnlazada -> registros: " + estado.getCasos().tamanio());
        System.out.println("ListaSecuencial -> registros: " + secuencial.tamanio() + " (capacidad: "
                + secuencial.capacidad() + ")");
        System.out.println("Tiempo de copiar a ListaSecuencial: " + tiempoMs + " ms");
        System.out.println("La ListaEnlazada no necesita conocer el tamano de antemano;");
        System.out.println("la ListaSecuencial accede por posicion en O(1) pero copia el arreglo al llenarse.");
    }

    private void mostrarEsquema() {
        System.out.println("casos_diarios.csv: " + Arrays.toString(Esquema.CABECERA_CASOS_DIARIOS));
        System.out.println("condados.csv: " + Arrays.toString(Esquema.CABECERA_CONDADOS));
        System.out.println("vecindad.tsv: " + Arrays.toString(Esquema.CABECERA_VECINDAD));
    }

    // ---------------------------------------------------------------
    // Proceso ETL: Extract -> Cola -> Transform -> Load
    // El menu pide, llama a EjecutorETL y muestra. No calcula nada.
    // ---------------------------------------------------------------

    private static final int MOSTRAR = 10;

    private boolean preguntarDatosDePrueba(Scanner teclado) {
        System.out.println("Datos a usar: 1 = base real (data/), 2 = datos de prueba (data/pruebas/)");
        System.out.print("Elija (1 o 2): ");
        return teclado.nextLine().trim().equals("2");
    }

    private void ejecutarEtlCompleto(Scanner teclado) {
        EjecutorETL etl = estado.nuevoEtl(preguntarDatosDePrueba(teclado));
        try {
            System.out.println("Extract, Transform y Load en curso...");
            ResumenETL resumen = etl.ejecutarTodo();
            mostrarResumen(resumen);
            System.out.println("Archivos escritos en: " + etl.getLoad().ruta(""));
        } catch (java.io.IOException | IllegalStateException e) {
            System.out.println("No se pudo completar el ETL: " + e.getMessage());
        }
    }

    private void etlExtraer(Scanner teclado) {
        EjecutorETL etl = estado.nuevoEtl(preguntarDatosDePrueba(teclado));
        try {
            etl.extraer();
            System.out.println("Filas de condados en la cola: " + etl.getColaCondados().tamanio());
            System.out.println("Filas de casos en la cola: " + etl.getColaCasos().tamanio());
            System.out.println("Filas de vecindad en la cola: " + etl.getColaVecindad().tamanio());
            System.out.println("Siguiente paso: opcion 12 (ver la cola) o 13 (transformar)");
        } catch (java.io.IOException e) {
            System.out.println("No se pudo extraer: " + e.getMessage());
        }
    }

    private boolean etlSinExtraer() {
        if (estado.getEtl() == null || !estado.getEtl().estaExtraido()) {
            System.out.println("Primero ejecute la opcion 11 (extraer a la cola)");
            return true;
        }
        return false;
    }

    private boolean etlSinTransformar() {
        if (estado.getEtl() == null || !estado.getEtl().estaTransformado()) {
            System.out.println("Primero ejecute la opcion 13 (transformar)");
            return true;
        }
        return false;
    }

    private void etlMostrarCola() {
        if (etlSinExtraer()) {
            return;
        }
        EjecutorETL etl = estado.getEtl();
        mostrarFrenteDeCola("condados", etl.getColaCondados());
        mostrarFrenteDeCola("casos", etl.getColaCasos());
        mostrarFrenteDeCola("vecindad", etl.getColaVecindad());
    }

    // la cola solo deja ver su frente sin sacar nada
    private void mostrarFrenteDeCola(String nombre, Cola<RegistroCrudo> cola) {
        System.out.println("Cola de " + nombre + ": " + cola.tamanio() + " filas");
        if (cola.estaVacia()) {
            System.out.println("  (vacia: ya se transformo)");
        } else {
            RegistroCrudo frente = cola.frente();
            System.out.println("  Frente -> linea " + frente.getNumeroLinea() + ": " + frente.mostrar());
        }
    }

    private void etlTransformar() {
        if (etlSinExtraer()) {
            return;
        }
        EjecutorETL etl = estado.getEtl();
        try {
            etl.transformar();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }
        mostrarResumen(etl.getResumen());
        System.out.println("Siguiente paso: opcion 14, 15 o 16");
    }

    private void etlMostrarValidos() {
        if (etlSinTransformar()) {
            return;
        }
        EjecutorETL etl = estado.getEtl();
        System.out.println("Casos validos: " + etl.getCasosValidos().tamanio() + " (primeros " + MOSTRAR + ")");
        etl.getCasosValidos().mostrar(MOSTRAR);
        System.out.println("Condados validos: " + etl.getCondadosValidos().tamanio() + " (primeros " + MOSTRAR + ")");
        etl.getCondadosValidos().mostrar(MOSTRAR);
        System.out.println("Grafo de vecindad: " + etl.getGrafo().numeroVertices() + " vertices y "
                + etl.getGrafo().numeroAristas() + " aristas");
    }

    private void etlMostrarPilaErrores() {
        if (etlSinTransformar()) {
            return;
        }
        System.out.println("Errores en la pila: " + estado.getEtl().getErrores().tamanio()
                + " (desde la cima, primeros " + MOSTRAR + ")");
        estado.getEtl().getErrores().mostrar(MOSTRAR);
    }

    private void etlCargar() {
        if (etlSinTransformar()) {
            return;
        }
        EjecutorETL etl = estado.getEtl();
        try {
            etl.cargar();
            System.out.println("Archivos escritos en: " + etl.getLoad().ruta(""));
            System.out.println("  casos_limpios.csv, condados_limpios.csv, errores_etl.txt, reporte_etl.txt");
        } catch (java.io.IOException e) {
            System.out.println("No se pudo escribir: " + e.getMessage());
        }
    }

    private void mostrarResumen(ResumenETL resumen) {
        System.out.println();
        mostrarContadores("Casos", resumen.getCasos());
        mostrarContadores("Condados", resumen.getCondados());
        mostrarContadores("Vecindad", resumen.getVecindad());
        System.out.println("Cuadra (leidos = validos + rechazados): " + (resumen.cuadra() ? "SI" : "NO"));
        System.out.println("Tiempo: " + resumen.getTiempoMs() + " ms");
    }

    private void mostrarContadores(String titulo, ResumenETL.Contadores c) {
        System.out.println(titulo + ": leidos " + c.getLeidos() + ", validos " + c.getValidos()
                + ", reparados " + c.getReparados() + ", advertencias " + c.getAdvertencias()
                + ", rechazados " + c.getRechazados());
        for (TipoError tipo : TipoError.values()) {
            if (c.getRechazadosPorTipo(tipo) > 0) {
                System.out.println("    " + tipo + ": " + c.getRechazadosPorTipo(tipo));
            }
        }
    }
}
