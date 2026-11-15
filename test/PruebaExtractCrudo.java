import estructuras.Cola;
import etl.Extract;
import model.RegistroCrudo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class PruebaExtractCrudo {
    private static int ok;

    private static void comprobar(String nombre, boolean condicion) {
        if (!condicion) throw new AssertionError(nombre);
        ok++;
        System.out.println(nombre + ": OK");
    }

    public static void main(String[] args) throws Exception {
        Path archivo = Files.createTempFile("extract-crudo-", ".txt");
        try {
            Extract extract = new Extract();
            Cola<RegistroCrudo> cola = new Cola<>();
            Files.writeString(archivo, "date,county,state,fips,cases,deaths\n"
                    + "2020-01-01,\"Nombre, compuesto\",TX,,invalido,\n"
                    + "incompleta\n\n", StandardCharsets.UTF_8);
            extract.encolarCasos(archivo.toString(), cola);
            comprobar("Todas las filas, incluso vacias", cola.tamanio() == 3 && extract.getCasosEncolados() == 3);
            RegistroCrudo primero = cola.desencolar();
            comprobar("Linea y campos sin convertir", primero.getNumeroLinea() == 2
                    && primero.getCampos()[1].equals("Nombre, compuesto")
                    && primero.getCampos()[4].equals("invalido") && primero.getCampos()[5].isEmpty());
            comprobar("Fila incompleta intacta", cola.desencolar().getCampos().length == 1);
            comprobar("Linea vacia numerada", cola.desencolar().getNumeroLinea() == 4);

            Files.writeString(archivo, "fips,name,state\n01001, Autauga ,AL\n0,Pais,NA\n");
            extract.encolarCondados(archivo.toString(), cola);
            comprobar("Condados sin filtrar", cola.tamanio() == 2 && extract.getCondadosEncolados() == 2);
            comprobar("Conserva espacios y ceros", cola.frente().getCampos()[1].equals(" Autauga ")
                    && cola.frente().getCampos()[0].equals("01001"));
            while (!cola.estaVacia()) cola.desencolar();

            Files.writeString(archivo, "county_name\tcounty_id\tadjacent_county_name\tadjacent_county_id\n"
                    + "A\t01\tA\t01\nA\t01\tA\t01\n");
            extract.encolarVecindad(archivo.toString(), cola);
            comprobar("Vecindad conserva repetidos y autoenlaces", cola.tamanio() == 2
                    && extract.getVecindadEncolada() == 2 && cola.frente().getCampos().length == 4);
            Files.writeString(archivo, "cabecera incorrecta\nfila\n");
            extract.encolarCasos(archivo.toString(), cola);
            comprobar("Cabecera invalida no altera cola ni contador", cola.tamanio() == 2 && extract.getCasosEncolados() == 3);
            Files.writeString(archivo, "");
            extract.encolarCondados(archivo.toString(), cola);
            comprobar("Archivo vacio rechazado", cola.tamanio() == 2 && extract.getIncidencias().tamanio() == 2);
            String[] campos = {"original"};
            RegistroCrudo registro = new RegistroCrudo(2, campos);
            campos[0] = "cambio";
            registro.getCampos()[0] = "otro cambio";
            comprobar("Registro conserva sus campos", registro.mostrar().equals("original"));
            System.out.println("Pruebas OK: " + ok + "; pruebas con FALLA: 0");
        } finally { Files.deleteIfExists(archivo); }
    }
}
