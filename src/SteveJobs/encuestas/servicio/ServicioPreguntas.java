package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.dao.PreguntaBancoDAO;
import java.util.List;
import java.util.ArrayList; // Aunque no se usa directamente si el DAO devuelve lista poblada
import java.sql.Timestamp; // Para setear fechas si es necesario

public class ServicioPreguntas {
    
    private PreguntaBancoDAO preguntaBancoDAO;

    public ServicioPreguntas() {
        this.preguntaBancoDAO = new PreguntaBancoDAO();
    }

    /**
     * Registra una nueva pregunta en el banco de preguntas.
     * Realiza validaciones básicas antes de pasarla al DAO.
     *
     * @param pregunta El objeto {@link PreguntaBanco} a registrar.
     * @return {@code true} si la pregunta se registró exitosamente, {@code false} en caso contrario.
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
        if (pregunta.getIdTipoPregunta() <= 0) { // Asumiendo que los IDs son positivos
            System.err.println("ServicioPreguntas: El ID del tipo de pregunta no es válido.");
            return false;
        }
        if (pregunta.getEstado() == null || pregunta.getEstado().trim().isEmpty()) {
             System.err.println("ServicioPreguntas: El estado de la pregunta no puede estar vacío.");
            return false;
        }
         if (pregunta.getIdUsuarioCreador() == null || pregunta.getIdUsuarioCreador() <=0 ) {
             System.err.println("ServicioPreguntas: El ID del usuario creador no es válido.");
            return false;
        }

        // Establecer fechas si no están presentes (DAO también tiene lógica similar)
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        if (pregunta.getFechaCreacion() == null) {
            pregunta.setFechaCreacion(ahora);
        }
        if (pregunta.getFechaModificacion() == null) {
            pregunta.setFechaModificacion(ahora);
        }

        // Podrían añadirse más validaciones, e.g., existencia de idTipoPregunta, idClasificacion, idUsuarioCreador en BD.

        boolean registrado = preguntaBancoDAO.registrarPregunta(pregunta);
        if (registrado) {
            System.out.println("ServicioPreguntas: Pregunta registrada en el banco exitosamente (ID: " + pregunta.getIdPreguntaBanco() + ").");
        } else {
            System.err.println("ServicioPreguntas: Falló el registro de la pregunta en el banco.");
        }
        return registrado;
    }

    /**
     * Obtiene todas las preguntas almacenadas en el banco de preguntas.
     *
     * @return Una lista de objetos {@link PreguntaBanco}. Puede ser una lista vacía si no hay preguntas.
     */
    public List<PreguntaBanco> obtenerTodasLasPreguntasDelBanco() {
        List<PreguntaBanco> preguntas = preguntaBancoDAO.listarPreguntas();
        if (preguntas == null) { // El DAO actual devuelve lista vacía en error de conexión, no null.
            System.err.println("ServicioPreguntas: Error al obtener la lista de preguntas del banco (DAO devolvió null).");
            return new ArrayList<>(); // Devolver lista vacía para consistencia.
        }
        System.out.println("ServicioPreguntas: Se obtuvieron " + preguntas.size() + " preguntas del banco.");
        return preguntas;
    }

    // --- Métodos existentes (placeholders o implementaciones parciales) ---
    public List<PreguntaBanco> listarPreguntasDelBancoConFiltro(String filtroTexto, String filtroTipo) {
        // TODO: Implementar correctamente si es necesario para otras funcionalidades.
        //       Actualmente no es parte del REQ de este subtask.
        System.out.println("ServicioPreguntas.listarPreguntasDelBancoConFiltro no implementado, devolviendo lista vacía.");
        // Aquí se llamaría a preguntaBancoDAO.listarPreguntasDelBancoConFiltro(filtroTexto, filtroTipo);
        return new ArrayList<>();
    }
    
    // ... otros métodos que se necesiten para los requerimientos REQMS-007 a REQMS-011
    // Por ejemplo:
    // public PreguntaBanco obtenerPreguntaDelBancoPorId(int idPregunta) { ... }
    // public boolean actualizarPreguntaDelBanco(PreguntaBanco pregunta) { ... }
    // public boolean cambiarEstadoPreguntaDelBanco(int idPregunta, String nuevoEstado) { ... }
    // public boolean eliminarPreguntaDelBanco(int idPregunta) { ... }
}
