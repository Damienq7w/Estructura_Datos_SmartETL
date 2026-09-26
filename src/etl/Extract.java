package etl;

import estructuras.ListaEnlazada;
import model.CasoDiario;
import model.Condado;
import model.Esquema;
import model.Vecindad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Extract {

    private int casosLeidas, casosCargadas, casosDescartadas;
    private int casosSinFips, casosCondadoUnknown, casosMuertesVacias;

    private int condadosLeidas, condadosCargadas, condadosDescartadas;
    private int condadosNoSonCondado, condadosFipsSinCero;

    private int vecindadLeidas, vecindadCargadas, vecindadDescartadas;
    private int vecindadVecinoDeSiMismo, vecindadRepetida;

    private final ListaEnlazada<String> incidencias = new ListaEnlazada<>();

    // lee casos_diarios.csv y arma la lista de CasoDiario
    public ListaEnlazada<CasoDiario> cargarCasos(String ruta) throws IOException {
        ListaEnlazada<CasoDiario> lista = new ListaEnlazada<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine();
            if (linea == null || !Esquema.cabeceraValida(separarCsv(linea), Esquema.CABECERA_CASOS_DIARIOS)) {
                incidencias.insertar("casos_diarios.csv rechazado: la cabecera no coincide con el esquema");
                return lista;
            }

            int numeroLinea = 1;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                casosLeidas++;
                String[] campos = separarCsv(linea);

                if (campos.length < Esquema.CABECERA_CASOS_DIARIOS.length) {
                    casosDescartadas++;
                    incidencias.insertar("casos linea " + numeroLinea + ": columnas incompletas");
                    continue;
                }

                String fecha = limpiar(campos[Esquema.CASOS_COL_FECHA]);
                String condado = limpiar(campos[Esquema.CASOS_COL_CONDADO]);
                String estado = limpiar(campos[Esquema.CASOS_COL_ESTADO]);
                String fips = limpiar(campos[Esquema.CASOS_COL_FIPS]);
                String casosTexto = limpiar(campos[Esquema.CASOS_COL_CASOS]);
                String muertesTexto = limpiar(campos[Esquema.CASOS_COL_MUERTES]);

                // solo se cuentan los problemas, no se corrigen aqui
                if (fips.isEmpty()) {
                    casosSinFips++;
                    incidencias.insertar("casos linea " + numeroLinea + ": fips vacio");
                }
                if (condado.equalsIgnoreCase("Unknown")) {
                    casosCondadoUnknown++;
                    incidencias.insertar("casos linea " + numeroLinea + ": condado Unknown");
                }

                int casos = parsearEntero(casosTexto, 0);
                int muertes;
                if (muertesTexto.isEmpty()) {
                    muertes = -1;
                    casosMuertesVacias++;
                    incidencias.insertar("casos linea " + numeroLinea + ": muertes vacias");
                } else {
                    muertes = parsearEntero(muertesTexto, -1);
                }

                lista.insertar(new CasoDiario(fecha, condado, estado, fips, casos, muertes));
                casosCargadas++;
            }
        }
        return lista;
    }

    // lee condados.csv y arma la lista de Condado
    public ListaEnlazada<Condado> cargarCondados(String ruta) throws IOException {
        ListaEnlazada<Condado> lista = new ListaEnlazada<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine();
            if (linea == null || !Esquema.cabeceraValida(separarCsv(linea), Esquema.CABECERA_CONDADOS)) {
                incidencias.insertar("condados.csv rechazado: la cabecera no coincide con el esquema");
                return lista;
            }

            int numeroLinea = 1;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                condadosLeidas++;
                String[] campos = separarCsv(linea);

                if (campos.length < Esquema.CABECERA_CONDADOS.length) {
                    condadosDescartadas++;
                    incidencias.insertar("condados linea " + numeroLinea + ": columnas incompletas");
                    continue;
                }

                String fips = limpiar(campos[Esquema.CONDADOS_COL_FIPS]);
                String nombre = limpiar(campos[Esquema.CONDADOS_COL_NOMBRE]);
                String estado = limpiar(campos[Esquema.CONDADOS_COL_ESTADO]);

                if (fips.isEmpty() || fips.length() <= 2) {
                    condadosNoSonCondado++;
                    incidencias.insertar("condados linea " + numeroLinea + ": no parece ser un condado (fips=" + fips + ")");
                }
                if (fips.length() == 4) {
                    condadosFipsSinCero++;
                    incidencias.insertar("condados linea " + numeroLinea + ": fips de 4 digitos, sin cero (" + fips + ")");
                }

                lista.insertar(new Condado(fips, nombre, estado));
                condadosCargadas++;
            }
        }
        return lista;
    }

    // lee vecindad.tsv (separado por tabulacion) y arma la lista de Vecindad
    public ListaEnlazada<Vecindad> cargarVecindad(String ruta) throws IOException {
        ListaEnlazada<Vecindad> lista = new ListaEnlazada<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine();
            if (linea == null || !Esquema.cabeceraValida(separarTsv(linea), Esquema.CABECERA_VECINDAD)) {
                incidencias.insertar("vecindad.tsv rechazado: la cabecera no coincide con el esquema");
                return lista;
            }

            int numeroLinea = 1;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                vecindadLeidas++;
                String[] campos = separarTsv(linea);

                if (campos.length < Esquema.CABECERA_VECINDAD.length) {
                    vecindadDescartadas++;
                    incidencias.insertar("vecindad linea " + numeroLinea + ": columnas incompletas");
                    continue;
                }

                String nombreOrigen = limpiar(campos[Esquema.VECINDAD_COL_NOMBRE_ORIGEN]);
                String fipsOrigen = limpiar(campos[Esquema.VECINDAD_COL_FIPS_ORIGEN]);
                String nombreDestino = limpiar(campos[Esquema.VECINDAD_COL_NOMBRE_DESTINO]);
                String fipsDestino = limpiar(campos[Esquema.VECINDAD_COL_FIPS_DESTINO]);

                if (fipsOrigen.equals(fipsDestino)) {
                    vecindadVecinoDeSiMismo++;
                    incidencias.insertar("vecindad linea " + numeroLinea + ": condado vecino de si mismo (" + fipsOrigen + ")");
                }

                if (esParYaCargado(lista, fipsOrigen, fipsDestino)) {
                    vecindadRepetida++;
                    incidencias.insertar("vecindad linea " + numeroLinea + ": par repetido (" + fipsOrigen + "-" + fipsDestino + ")");
                }

                lista.insertar(new Vecindad(nombreOrigen, fipsOrigen, nombreDestino, fipsDestino));
                vecindadCargadas++;
            }
        }
        return lista;
    }

    // revisa si el par (origen-destino) ya esta, en cualquier orden
    private boolean esParYaCargado(ListaEnlazada<Vecindad> yaCargadas, String fipsOrigen, String fipsDestino) {
        for (Vecindad v : yaCargadas) {
            boolean mismoPar = v.getFipsOrigen().equals(fipsOrigen) && v.getFipsDestino().equals(fipsDestino);
            boolean parInvertido = v.getFipsOrigen().equals(fipsDestino) && v.getFipsDestino().equals(fipsOrigen);
            if (mismoPar || parInvertido) {
                return true;
            }
        }
        return false;
    }

    private String[] separarCsv(String linea) {
        return separar(linea, ',');
    }

    private String[] separarTsv(String linea) {
        return separar(linea, '\t');
    }

    // separa por el caracter dado, ignorando el separador si esta entre comillas
    private String[] separar(String linea, char separador) {
        String[] temporal = new String[128];
        int cantidad = 0;
        StringBuilder actual = new StringBuilder();
        boolean dentroDeComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                dentroDeComillas = !dentroDeComillas;
            } else if (c == separador && !dentroDeComillas) {
                temporal[cantidad++] = actual.toString();
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        temporal[cantidad++] = actual.toString();

        String[] resultado = new String[cantidad];
        System.arraycopy(temporal, 0, resultado, 0, cantidad);
        return resultado;
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private int parsearEntero(String texto, int porDefecto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    public ListaEnlazada<String> getIncidencias() { return incidencias; }

    public int getCasosLeidas() { return casosLeidas; }
    public int getCasosCargadas() { return casosCargadas; }
    public int getCasosDescartadas() { return casosDescartadas; }
    public int getCasosSinFips() { return casosSinFips; }
    public int getCasosCondadoUnknown() { return casosCondadoUnknown; }
    public int getCasosMuertesVacias() { return casosMuertesVacias; }

    public int getCondadosLeidas() { return condadosLeidas; }
    public int getCondadosCargadas() { return condadosCargadas; }
    public int getCondadosDescartadas() { return condadosDescartadas; }
    public int getCondadosNoSonCondado() { return condadosNoSonCondado; }
    public int getCondadosFipsSinCero() { return condadosFipsSinCero; }

    public int getVecindadLeidas() { return vecindadLeidas; }
    public int getVecindadCargadas() { return vecindadCargadas; }
    public int getVecindadDescartadas() { return vecindadDescartadas; }
    public int getVecindadVecinoDeSiMismo() { return vecindadVecinoDeSiMismo; }
    public int getVecindadRepetida() { return vecindadRepetida; }
}
