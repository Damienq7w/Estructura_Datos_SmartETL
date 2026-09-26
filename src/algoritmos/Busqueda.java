package algoritmos;

import estructuras.ListaEnlazada;
import model.CasoDiario;

public class Busqueda {

    // Registros revisados en la ultima busqueda
    private int comparaciones;

    /**
     * Busca el registro de un condado en una fecha concreta.
     * Se detiene en la primera coincidencia, porque la pareja
     * (fips, fecha) identifica un único registro.
     *
     * @param lista lista de casos donde buscar
     * @param fips  código del condado, como texto (ej. "48201")
     * @param fecha fecha en formato AAAA-MM-DD (ej. "2021-12-31")
     * @return el registro encontrado, o null si no existe
     */
    public CasoDiario porFipsYFecha(ListaEnlazada<CasoDiario> lista, String fips, String fecha) {
        comparaciones = 0;
        if (lista == null || fips == null || fecha == null) {
            return null;
        }

        // Se recorre con el iterador for-each, avanza de nodo en nodo.
        // Usar lista.obtener(i) dentro de un for volvería a caminar desde la
        // cabeza en cada vuelta y la búsqueda pasaría de O(n) a O(n²).
        for (CasoDiario caso : lista) {
            comparaciones++;
            // El texto buscado va a la izquierda del equals, si el registro
            // tiene el fips vacio o nulo, no se produce un NullPointerException.
            if (fips.equals(caso.getFips()) && fecha.equals(caso.getFecha())) {
                return caso;
            }
        }
        return null;
    }

    /**
     * Busca todos los registros de un condado por su nombre.
     * No se detiene en la primera coincidencia: un condado tiene un registro
     * por cada día, así que devuelve todos los que encuentre.
     * No distingue mayúsculas de minúsculas ("autauga" = "Autauga").
     *
     * @param lista  lista de casos donde buscar
     * @param nombre nombre del condado (ej. "Autauga")
     * @return lista con todas las coincidencias (vacía si no hay ninguna)
     */
    public ListaEnlazada<CasoDiario> porCondado(ListaEnlazada<CasoDiario> lista, String nombre) {
        comparaciones = 0;
        ListaEnlazada<CasoDiario> resultado = new ListaEnlazada<>();
        if (lista == null || nombre == null) {
            return resultado;
        }

        String buscado = nombre.trim();
        for (CasoDiario caso : lista) {
            comparaciones++;
            if (buscado.equalsIgnoreCase(caso.getCondado())) {
                resultado.insertar(caso);
            }
        }
        return resultado;
    }

    /**
     * @return cuántos registros se revisaron en la última búsqueda
     */
    public int getComparaciones() {
        return comparaciones;
    }
}