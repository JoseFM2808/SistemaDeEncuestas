/*
 * Módulo Responsable: Admin. de Encuestas
 * Autores: Alfredo Swidin
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase POJO para representar la entidad 'Encuesta_Preguntas'.
 */
package SteveJobs.encuestas.modelo;

public class EncuestaPregunta {
    private int id_encuesta_pregunta;
    private int id_encuesta;
    private int id_pregunta; // Refiere a Banco_Preguntas.id_pregunta
    private int orden;
    private boolean es_descarte;
    private String criterio_descarte; // Podría ser un valor específico o una condición simple

    // Constructores
    public EncuestaPregunta() {
    }

    public EncuestaPregunta(int id_encuesta_pregunta, int id_encuesta, int id_pregunta, int orden, boolean es_descarte, String criterio_descarte) {
        this.id_encuesta_pregunta = id_encuesta_pregunta;
        this.id_encuesta = id_encuesta;
        this.id_pregunta = id_pregunta;
        this.orden = orden;
        this.es_descarte = es_descarte;
        this.criterio_descarte = criterio_descarte;
    }

    // Getters y Setters
    public int getId_encuesta_pregunta() {
        return id_encuesta_pregunta;
    }

    public void setId_encuesta_pregunta(int id_encuesta_pregunta) {
        this.id_encuesta_pregunta = id_encuesta_pregunta;
    }

    public int getId_encuesta() {
        return id_encuesta;
    }

    public void setId_encuesta(int id_encuesta) {
        this.id_encuesta = id_encuesta;
    }

    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public boolean isEs_descarte() {
        return es_descarte;
    }

    public void setEs_descarte(boolean es_descarte) {
        this.es_descarte = es_descarte;
    }

    public String getCriterio_descarte() {
        return criterio_descarte;
    }

    public void setCriterio_descarte(String criterio_descarte) {
        this.criterio_descarte = criterio_descarte;
    }

    @Override
    public String toString() {
        return "EncuestaPregunta{" +
                "id_encuesta_pregunta=" + id_encuesta_pregunta +
                ", id_encuesta=" + id_encuesta +
                ", id_pregunta=" + id_pregunta +
                ", orden=" + orden +
                ", es_descarte=" + es_descarte +
                ", criterio_descarte='" + criterio_descarte + ''' +
                '}';
    }
}
