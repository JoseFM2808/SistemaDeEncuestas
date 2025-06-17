/*
 * Autores del Módulo:
 * - Pablo Alegre
 *
 * Responsabilidad Principal:
 * - Lógica de negocio para el banco de preguntas.
 * - Versión corregida por Asistente de AED para alinear con el modelo y DAO finales.
 */
package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.dao.PreguntaBancoDAO;
import java.util.List;
import java.util.ArrayList;

public class ServicioPreguntas {
    
    private PreguntaBancoDAO preguntaBancoDAO;

    public ServicioPreguntas() {
        this.preguntaBancoDAO = new PreguntaBancoDAO();
    }

    /**
     * Registra una nueva pregunta en el banco. Realiza validaciones básicas.
     * @param pregunta El objeto PreguntaBanco a registrar.
     * @return true si se registró exitosamente, false en caso contrario.
     */
    public boolean registrarPreguntaEnBanco(PreguntaBanco pregunta) {
        if (pregunta == null) {
            System.err.println("ServicioPreguntas: La pregunta no puede ser nula.");
            return false;
        }
        if (pregunta.getTextoPregunta() == null || pregunta.getTextoPregunta().trim().isEmpty()) {
            System.err.println("ServicioPreguntas: El texto de la pregunta no puede estar vacío.");
            return false;
        }
        if (pregunta.getIdTipoPregunta() <= 0) { // Asumiendo que los IDs de la BD son positivos
            System.err.println("ServicioPreguntas: El ID del tipo de pregunta no es válido.");
            return false;
        }
        
        // Se eliminaron las validaciones de campos que ya no existen en el modelo:
        // estado, idUsuarioCreador, fechaCreacion, fechaModificacion.

        // Se corrigió la llamada al método del DAO y se adaptó la lógica de retorno.
        int nuevoId = preguntaBancoDAO.crearPreguntaBanco(pregunta);
        
        if (nuevoId > 0) {
            pregunta.setIdPreguntaBanco(nuevoId); // Actualizamos el ID en el objeto
            System.out.println("ServicioPreguntas: Pregunta registrada en el banco exitosamente (ID: " + nuevoId + ").");
            return true;
        } else {
            System.err.println("ServicioPreguntas: Falló el registro de la pregunta en el banco.");
            return false;
        }
    }

    /**
     * Obtiene todas las preguntas almacenadas en el banco de preguntas.
     * @return Una lista de objetos PreguntaBanco.
     */
    public List<PreguntaBanco> obtenerTodasLasPreguntasDelBanco() {
        // Se corrigió la llamada al método correcto del DAO
        List<PreguntaBanco> preguntas = preguntaBancoDAO.obtenerTodasLasPreguntas();
        
        if (preguntas == null) { // Por si acaso la conexión falla y el DAO devuelve null
            System.err.println("ServicioPreguntas: Error al obtener la lista de preguntas (el DAO devolvió null).");
            return new ArrayList<>(); 
        }
        
        System.out.println("ServicioPreguntas: Se obtuvieron " + preguntas.size() + " preguntas del banco.");
        return preguntas;
    }
}
