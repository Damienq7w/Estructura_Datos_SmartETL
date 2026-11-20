import etl.Transform;

public class PruebaTransform {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    public static void main(String[] args) {
        // FIPS
        revisar("1001 -> 01001", "01001".equals(Transform.arreglarFips("1001")));
        revisar("01001 queda igual", "01001".equals(Transform.arreglarFips("01001")));
        revisar("FIPS vacio es invalido", Transform.arreglarFips("") == null);
        revisar("FIPS null es invalido", Transform.arreglarFips(null) == null);
        revisar("FIPS con letras es invalido", Transform.arreglarFips("12a4") == null);

        // Texto
        revisar("' Texas ' -> 'Texas'", Transform.limpiarTexto(" Texas ").equals("Texas"));
        revisar("null -> ''", Transform.limpiarTexto(null).equals(""));

        // Numeros
        revisar("abc no es numero", Transform.leerNumero("abc") == null);
        revisar("vacio no es numero (no da 0)", Transform.leerNumero("") == null);
        revisar("'25' es 25", Transform.leerNumero("25") == 25);
        revisar("'-3' es -3 (el negativo se rechaza aparte)", Transform.leerNumero("-3") == -3);

        // Fechas
        revisar("2020-03-15 valida", Transform.fechaValida("2020-03-15"));
        revisar("2021-13-45 invalida", !Transform.fechaValida("2021-13-45"));
        revisar("21-1-1 invalida", !Transform.fechaValida("21-1-1"));

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
    }
}
