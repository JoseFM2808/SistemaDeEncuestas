/*
Autor: Alfredo Swidin
 */
package SteveJobs.encuestas.modelo;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

public class Encuesta {

    private int idEncuesta;
    private String nombreEncuesta;
    private String descripcion;
    private Timestamp fechaInicioVigencia;
    private Timestamp fechaFinVigencia;
    private int publicoObjetivoCantidad;
    private String definicionPerfil;
    private String estado;
    private Timestamp fechaCreacionEncuesta;
    private int idAdminCreador;
    private List<EncuestaDetallePregunta> preguntasAsociadas;

    public Encuesta() {
        this.preguntasAsociadas = new ArrayList<>();
    }

    public Encuesta(String nombreEncuesta, String descripcion, Timestamp fechaInicioVigencia, Timestamp fechaFinVigencia, int publicoObjetivoCantidad, String definicionPerfil, int idAdminCreador) {
        this.nombreEncuesta = nombreEncuesta;
        this.descripcion = descripcion;
        this.fechaInicioVigencia = fechaInicioVigencia;
        this.fechaFinVigencia = fechaFinVigencia;
        this.publicoObjetivoCantidad = publicoObjetivoCantidad;
        this.definicionPerfil = definicionPerfil;
        this.idAdminCreador = idAdminCreador;
        this.estado = "Borrador";
        this.preguntasAsociadas = new ArrayList<>();
    }
    
    public int getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public String getNombreEncuesta() {
        return nombreEncuesta;
    }

    public void setNombreEncuesta(String nombreEncuesta) {
        this.nombreEncuesta = nombreEncuesta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(Timestamp fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public Timestamp getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(Timestamp fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public int getPublicoObjetivoCantidad() {
        return publicoObjetivoCantidad;
    }

    public void setPublicoObjetivoCantidad(int publicoObjetivoCantidad) {
        this.publicoObjetivoCantidad = publicoObjetivoCantidad;
    }

    public String getDefinicionPerfil() {
        return definicionPerfil;
    }

    public void setDefinicionPerfil(String definicionPerfil) {
        this.definicionPerfil = definicionPerfil;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacionEncuesta() {
        return fechaCreacionEncuesta;
    }

    public void setFechaCreacionEncuesta(Timestamp fechaCreacionEncuesta) {
        this.fechaCreacionEncuesta = fechaCreacionEncuesta;
    }

    public int getIdAdminCreador() {
        return idAdminCreador;
    }

    public void setIdAdminCreador(int idAdminCreador) {
        this.idAdminCreador = idAdminCreador;
    }

    public List<EncuestaDetallePregunta> getPreguntasAsociadas() {
        return preguntasAsociadas;
    }

    public void setPreguntasAsociadas(List<EncuestaDetallePregunta> preguntasAsociadas) {
        this.preguntasAsociadas = preguntasAsociadas;
    }
    
    @Override
    public String toString() {
        return "Encuesta{" +
                "idEncuesta=" + idEncuesta +
                ", nombreEncuesta='" + nombreEncuesta + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaInicioVigencia=" + fechaInicioVigencia +
                ", fechaFinVigencia=" + fechaFinVigencia +
                ", publicoObjetivoCantidad=" + publicoObjetivoCantidad +
                ", definicionPerfil='" + definicionPerfil + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaCreacionEncuesta=" + fechaCreacionEncuesta +
                ", idAdminCreador=" + idAdminCreador +
                ", preguntasAsociadas=" + (preguntasAsociadas != null ? preguntasAsociadas.size() : "0") + " preguntas" +
                '}';
    }
}