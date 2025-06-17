/*
 * Autores del Módulo:
 * - Alfredo Swidin
 * - Asistente de AED (Refactorización)
 *
 * Responsabilidad Principal:
 * - Representa la entidad Encuesta, alineada con el diccionario de datos final.
 */
package SteveJobs.encuestas.modelo;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

public class Encuesta {

    // Atributos corregidos para coincidir con el Diccionario de Datos
    private int idEncuesta;
    private String nombre;
    private String descripcion;
    private Timestamp fechaInicio;
    private Timestamp fechaFin;
    private int publicoObjetivo;
    private String perfilRequerido;
    private String estado;
    private Timestamp fechaCreacion;
    private int idAdminCreador;
    private List<EncuestaDetallePregunta> preguntasAsociadas;

    public Encuesta() {
        this.preguntasAsociadas = new ArrayList<>();
    }
    
    // Getters y Setters corregidos
    public int getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Timestamp fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Timestamp getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Timestamp fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getPublicoObjetivo() {
        return publicoObjetivo;
    }

    public void setPublicoObjetivo(int publicoObjetivo) {
        this.publicoObjetivo = publicoObjetivo;
    }

    public String getPerfilRequerido() {
        return perfilRequerido;
    }

    public void setPerfilRequerido(String perfilRequerido) {
        this.perfilRequerido = perfilRequerido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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
}