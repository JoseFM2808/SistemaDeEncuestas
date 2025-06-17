/*
 * Autores del Módulo:
 * - José Flores
 *
 * Responsabilidad Principal:
 * - Lógica de configuración general de administrador
 */
package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.dao.PreguntaRegistroDAO;
import SteveJobs.encuestas.modelo.PreguntaRegistro;
import java.util.List;

public class ServicioConfiguracionAdmin {
    private PreguntaRegistroDAO preguntaRegistroDAO;

    public ServicioConfiguracionAdmin() {
        this.preguntaRegistroDAO = new PreguntaRegistroDAO();
    }

    public boolean crearNuevaPreguntaRegistro(String texto, String tipoRespuesta, String opcionesPosibles, boolean esObligatoria, int ordenVisualizacion) {
        if (texto == null || texto.trim().isEmpty()) {
            System.err.println("Servicio: El texto de la pregunta no puede estar vacío.");
            return false;
        }
        if (tipoRespuesta == null || tipoRespuesta.trim().isEmpty()) {
            System.err.println("Servicio: El tipo de respuesta no puede estar vacío.");
            return false;
        }

        PreguntaRegistro nuevaPregunta = new PreguntaRegistro(texto, tipoRespuesta, opcionesPosibles, esObligatoria, ordenVisualizacion, "ACTIVA");
        return preguntaRegistroDAO.agregarPreguntaRegistro(nuevaPregunta);
    }

    public List<PreguntaRegistro> listarPreguntasRegistro() {
        return preguntaRegistroDAO.obtenerTodasLasPreguntasRegistro();
    }

    public boolean modificarPreguntaRegistro(int id, String texto, String tipoRespuesta, String opcionesPosibles, boolean esObligatoria, int ordenVisualizacion, String estado) {
        if (texto == null || texto.trim().isEmpty() || tipoRespuesta == null || tipoRespuesta.trim().isEmpty()) {
            System.err.println("Servicio: Texto y tipo de respuesta son obligatorios para modificar.");
            return false;
        }
        PreguntaRegistro pregunta = new PreguntaRegistro(texto, tipoRespuesta, opcionesPosibles, esObligatoria, ordenVisualizacion, estado);
        pregunta.setIdPreguntaRegistro(id);
        return preguntaRegistroDAO.actualizarPreguntaRegistro(pregunta);
    }

    public boolean eliminarPreguntaRegistro(int id) {
        return preguntaRegistroDAO.eliminarPreguntaRegistro(id);
    }
}