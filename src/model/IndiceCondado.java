package model;

import algoritmos.Ordenamiento;
import estructuras.ArbolBST;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;

// un nodo del arbol de condados: el condado y la lista de sus casos.
// el arbol guarda un nodo por condado (unos 3.200), no el millon de registros
public class IndiceCondado implements Comparable<IndiceCondado> {

    private final String fips;
    private final String nombre;
    private final String estado;

    // casos de este condado, en orden de fecha (asi llegan del archivo)
    private final ListaEnlazada<CasoDiario> historial = new ListaEnlazada<>();

    public IndiceCondado(String fips, String nombre, String estado) {
        this.fips = fips;
        this.nombre = nombre;
        this.estado = estado;
    }

    public IndiceCondado(Condado condado) {
        this(condado.getFips(), condado.getNombre(), condado.getEstado());
    }

    // solo el FIPS decide el orden; es texto de 5 digitos, nunca numero
    @Override
    public int compareTo(IndiceCondado otro) {
        return fips.compareTo(otro.fips);
    }

    // arma el arbol balanceado con los condados limpios: se ordenan por
    // FIPS y se insertan por medios
    public static ArbolBST<IndiceCondado> construirArbol(ListaEnlazada<Condado> condados) {
        ListaSecuencial<IndiceCondado> ordenados = new ListaSecuencial<>();
        for (Condado condado : condados) {
            ordenados.insertar(new IndiceCondado(condado));
        }
        // el catalogo ya viene casi ordenado: insercion lo recorre casi una sola vez
        new Ordenamiento().insercion(ordenados, (a, b) -> a.compareTo(b));

        ArbolBST<IndiceCondado> arbol = new ArbolBST<>();
        arbol.construirPorMedios(ordenados);
        return arbol;
    }

    // busca un condado por su FIPS de 5 digitos; null si no esta.
    // las comparaciones quedan en arbol.getComparaciones()
    public static IndiceCondado buscar(ArbolBST<IndiceCondado> arbol, String fips) {
        if (fips == null) {
            return null;
        }
        return arbol.buscar(new IndiceCondado(fips, "", ""));
    }

    public String getFips() { return fips; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
    public ListaEnlazada<CasoDiario> getHistorial() { return historial; }

    @Override
    public String toString() {
        return fips + " | " + nombre + " | " + estado + " | " + historial.tamanio() + " casos";
    }
}
