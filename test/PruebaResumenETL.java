import model.ResumenETL;
import model.TipoError;

public class PruebaResumenETL {

    private static int fallas = 0;

    private static void revisar(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallas++;
    }

    public static void main(String[] args) {
        ResumenETL r = new ResumenETL();

        // vacio: todo en cero y cuadra
        revisar("resumen nuevo en cero", r.getCasos().getLeidos() == 0 && r.getTiempoMs() == 0);
        revisar("resumen nuevo cuadra", r.cuadra());

        // guardar y leer valores (numeros del LEEME de casos_prueba)
        ResumenETL.Contadores c = r.getCasos();
        c.setLeidos(42);
        c.setValidos(35);
        c.setReparados(0);
        c.setAdvertencias(2);
        c.setRechazados(7);
        c.setRechazadosPorTipo(TipoError.DUPLICADO, 1);
        c.setRechazadosPorTipo(TipoError.AREA_SIN_FIPS, 1);
        revisar("guarda y devuelve leidos", c.getLeidos() == 42);
        revisar("guarda y devuelve validos", c.getValidos() == 35);
        revisar("guarda y devuelve advertencias", c.getAdvertencias() == 2);
        revisar("guarda y devuelve rechazados", c.getRechazados() == 7);
        revisar("cuenta por tipo", c.getRechazadosPorTipo(TipoError.DUPLICADO) == 1
                && c.getRechazadosPorTipo(TipoError.AREA_SIN_FIPS) == 1);
        revisar("tipo sin datos queda en 0", c.getRechazadosPorTipo(TipoError.FECHA_INVALIDA) == 0);

        // cuadre
        revisar("42 = 35 + 7 cuadra", c.cuadra());
        revisar("el resumen completo cuadra", r.cuadra());
        c.setValidos(34);
        revisar("falta un registro: no cuadra", !c.cuadra());
        revisar("el resumen completo tampoco cuadra", !r.cuadra());
        c.setValidos(35);

        // cuadre por tipo
        revisar("por tipo: 2 de 7 no cuadra", !c.cuadraPorTipo());
        c.setRechazados(2);
        c.setValidos(40);
        revisar("por tipo: 2 de 2 cuadra", c.cuadraPorTipo());

        // condados y vecindad son independientes
        r.getCondados().setLeidos(9);
        r.getCondados().setValidos(8);
        r.getCondados().setRechazados(1);
        r.getCondados().setReparados(2);
        revisar("condados independientes de casos", r.getCondados().getReparados() == 2
                && r.getCasos().getReparados() == 0);
        revisar("condados cuadra", r.getCondados().cuadra());

        // tiempo, fechas y extras
        r.setTiempoMs(1250);
        revisar("guarda el tiempo", r.getTiempoMs() == 1250);
        r.setRangoFechas("2021-01-01", "2021-12-31");
        revisar("guarda el rango de fechas", r.getFechaMinima().equals("2021-01-01")
                && r.getFechaMaxima().equals("2021-12-31"));
        r.setCondadosConCasos(3143);
        revisar("guarda condados con casos", r.getCondadosConCasos() == 3143);
        r.getCondadosConMasCasos().insertar("Los Angeles, CA");
        revisar("guarda el ranking", r.getCondadosConMasCasos().tamanio() == 1);

        System.out.println(fallas == 0 ? "\nTodas las pruebas pasaron" : "\nFallaron " + fallas);
        if (fallas > 0) System.exit(1);
    }
}
