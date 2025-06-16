/*
 * Módulo Responsable: Gestión de Entidades Base (Usuarios y Preguntas)
 * Autores: Pablo Alegre
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase POJO para representar la entidad 'Banco_Preguntas'.
 */
package SteveJobs.encuestas.modelo;

public class BancoPregunta {
    private int id_pregunta;
    private int id_categoria;
    private String texto_pregunta;
    private String tipo_pregunta; // Ej: "Abierta", "Opción Múltiple", "Escala"
    private String opciones; // Podría ser un JSON o un formato delimitado para opciones múltiples/escala

    // Constructores
    public BancoPregunta() {
    }

    public BancoPregunta(int id_pregunta, int id_categoria, String texto_pregunta, String tipo_pregunta, String opciones) {
        this.id_pregunta = id_pregunta;
        this.id_categoria = id_categoria;
        this.texto_pregunta = texto_pregunta;
        this.tipo_pregunta = tipo_pregunta;
        this.opciones = opciones;
    }

    // Getters y Setters
    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getTexto_pregunta() {
        return texto_pregunta;
    }

    public void setTexto_pregunta(String texto_pregunta) {
        this.texto_pregunta = texto_pregunta;
    }

    public String getTipo_pregunta() {
        return tipo_pregunta;
    }

    public void setTipo_pregunta(String tipo_pregunta) {
        this.tipo_pregunta = tipo_pregunta;
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }

    @Override
    public String toString() {
        return "BancoPregunta{" +
                "id_pregunta=" + id_pregunta +
                ", id_categoria=" + id_categoria +
                ", texto_pregunta='" + texto_pregunta + ''' +
                ", tipo_pregunta='" + tipo_pregunta + ''' +
                ", opciones='" + opciones + ''' +
                '}';
    }
}
