/*
 * Módulo Responsable: Gestión de Entidades Base (Usuarios y Preguntas)
 * Autores: Pablo Alegre
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase POJO para representar la entidad 'Categorias_Preguntas'.
 */
package SteveJobs.encuestas.modelo;

public class CategoriaPregunta {
    private int id_categoria;
    private String nombre_categoria;
    private String descripcion;
    private String estado;

    // Constructores
    public CategoriaPregunta() {
    }

    public CategoriaPregunta(int id_categoria, String nombre_categoria, String descripcion, String estado) {
        this.id_categoria = id_categoria;
        this.nombre_categoria = nombre_categoria;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre_categoria() {
        return nombre_categoria;
    }

    public void setNombre_categoria(String nombre_categoria) {
        this.nombre_categoria = nombre_categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "CategoriaPregunta{" +
                "id_categoria=" + id_categoria +
                ", nombre_categoria='" + nombre_categoria + ''' +
                ", descripcion='" + descripcion + ''' +
                ", estado='" + estado + ''' +
                '}';
    }
}
