/*
 * Módulo Responsable: Interacción y Resultados
 * Autores: José Flores
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase POJO para representar la entidad 'Respuestas'.
 */
package SteveJobs.encuestas.modelo;

import java.sql.Timestamp;

public class Respuesta {
    private int id_respuesta;
    private int id_encuesta_pregunta;
    private int id_usuario;
    private String valor_respuesta; // Puede ser texto libre, un número, una opción seleccionada, etc.
    private Timestamp fecha_respuesta;

    // Constructores
    public Respuesta() {
    }

    public Respuesta(int id_respuesta, int id_encuesta_pregunta, int id_usuario, String valor_respuesta, Timestamp fecha_respuesta) {
        this.id_respuesta = id_respuesta;
        this.id_encuesta_pregunta = id_encuesta_pregunta;
        this.id_usuario = id_usuario;
        this.valor_respuesta = valor_respuesta;
        this.fecha_respuesta = fecha_respuesta;
    }

    // Getters y Setters
    public int getId_respuesta() {
        return id_respuesta;
    }

    public void setId_respuesta(int id_respuesta) {
        this.id_respuesta = id_respuesta;
    }

    public int getId_encuesta_pregunta() {
        return id_encuesta_pregunta;
    }

    public void setId_encuesta_pregunta(int id_encuesta_pregunta) {
        this.id_encuesta_pregunta = id_encuesta_pregunta;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getValor_respuesta() {
        return valor_respuesta;
    }

    public void setValor_respuesta(String valor_respuesta) {
        this.valor_respuesta = valor_respuesta;
    }

    public Timestamp getFecha_respuesta() {
        return fecha_respuesta;
    }

    public void setFecha_respuesta(Timestamp fecha_respuesta) {
        this.fecha_respuesta = fecha_respuesta;
    }

    @Override
    public String toString() {
        return "Respuesta{" +
                "id_respuesta=" + id_respuesta +
                ", id_encuesta_pregunta=" + id_encuesta_pregunta +
                ", id_usuario=" + id_usuario +
                ", valor_respuesta='" + valor_respuesta + ''' +
                ", fecha_respuesta=" + fecha_respuesta +
                '}';
    }
}
