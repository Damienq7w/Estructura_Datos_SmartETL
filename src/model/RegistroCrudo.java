package model;

/** Campos sin validar ni convertir, con su linea fisica en el archivo. */
public class RegistroCrudo {
    private final int numeroLinea;
    private final String[] campos;

    public RegistroCrudo(int numeroLinea, String[] campos) {
        this.numeroLinea = numeroLinea;
        this.campos = campos.clone();
    }

    public int getNumeroLinea() { return numeroLinea; }
    public String[] getCampos() { return campos.clone(); }
    public String mostrar() { return String.join(" | ", campos); }
}
