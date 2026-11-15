package menu;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import algoritmos.Busqueda;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import etl.Extract;
import model.CasoDiario;
import model.Esquema;

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
}
