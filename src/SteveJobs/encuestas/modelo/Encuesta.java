/*
 * Módulo Responsable: Admin. de Encuestas
 * Autores: Alfredo Swidin
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase POJO para representar la entidad 'Encuestas'.
 */
package SteveJobs.encuestas.modelo;

import java.sql.Timestamp;

public class Encuesta {
    private int id_encuesta;
    private String nombre;
    private String descripcion;
    private Timestamp fecha_inicio;
    private Timestamp fecha_fin;
    private String perfil_requerido; // JSON como String
    private String estado;

    // Constructores
    public Encuesta() {
    }

    public Encuesta(int id_encuesta, String nombre, String descripcion, Timestamp fecha_inicio, Timestamp fecha_fin, String perfil_requerido, String estado) {
        this.id_encuesta = id_encuesta;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.perfil_requerido = perfil_requerido;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId_encuesta() {
        return id_encuesta;
    }

    public void setId_encuesta(int id_encuesta) {
        this.id_encuesta = id_encuesta;
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

    public Timestamp getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Timestamp fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Timestamp getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Timestamp fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getPerfil_requerido() {
        return perfil_requerido;
    }

    public void setPerfil_requerido(String perfil_requerido) {
        this.perfil_requerido = perfil_requerido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Encuesta{" +
                "id_encuesta=" + id_encuesta +
                ", nombre='" + nombre + ''' +
                ", descripcion='" + descripcion + ''' +
                ", fecha_inicio=" + fecha_inicio +
                ", fecha_fin=" + fecha_fin +
                ", perfil_requerido='" + perfil_requerido + ''' +
                ", estado='" + estado + ''' +
                '}';
    }
}
