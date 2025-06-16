package SteveJobs.encuestas.modelo;

/**
 * Clase para transportar los datos necesarios para el menu principal.
 */
public class DatosMenuPrincipal {
    private String mensajeBienvenida;
    private int numeroEncuestasPendientes;
    private boolean esAdministrador;

    public DatosMenuPrincipal(String mensajeBienvenida, int numeroEncuestasPendientes, boolean esAdministrador) {
        this.mensajeBienvenida = mensajeBienvenida;
        this.numeroEncuestasPendientes = numeroEncuestasPendientes;
        this.esAdministrador = esAdministrador;
    }

    public String getMensajeBienvenida() {
        return mensajeBienvenida;
    }

    public void setMensajeBienvenida(String mensajeBienvenida) {
        this.mensajeBienvenida = mensajeBienvenida;
    }

    public int getNumeroEncuestasPendientes() {
        return numeroEncuestasPendientes;
    }

    public void setNumeroEncuestasPendientes(int numeroEncuestasPendientes) {
        this.numeroEncuestasPendientes = numeroEncuestasPendientes;
    }

    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }
}
