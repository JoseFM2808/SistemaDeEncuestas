/*
Autor: José Flores

*/
package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.dao.RespuestaUsuarioDAO;
import SteveJobs.encuestas.modelo.RespuestaUsuario;
import java.util.List;
import java.sql.Timestamp; // Para las marcas de tiempo

public class ServicioParticipacion {
    private RespuestaUsuarioDAO respuestaDAO;

    public ServicioParticipacion() {
        this.respuestaDAO = new RespuestaUsuarioDAO();
    }

    /**
     * Registra una lista de respuestas de un usuario para una encuesta.
     *
     * Este método itera sobre la lista de respuestas proporcionada. Si alguna respuesta
     * no tiene establecida {@code fechaHoraRespuesta}, se le asigna la marca de tiempo actual.
     * Luego, todas las respuestas se pasan al DAO para ser guardadas en un lote.
     *
     * @param respuestas Una lista de objetos {@link RespuestaUsuario} que representan
     *                   las respuestas del usuario a las preguntas de una encuesta.
     *                   No debe ser {@code null} ni vacía.
     * @return {@code true} si todas las respuestas se guardaron exitosamente mediante
     *         {@link RespuestaUsuarioDAO#guardarListaRespuestas(List)},
     *         {@code false} si la lista de respuestas es nula o vacía, o si el DAO
     *         reporta un fallo al guardar.
     */
    public boolean registrarRespuestasCompletas(List<RespuestaUsuario> respuestas) {
        if (respuestas == null || respuestas.isEmpty()) {
            System.err.println("ServicioParticipacion: No hay respuestas para registrar.");
            return false;
        }

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        for(RespuestaUsuario r : respuestas){
            if(r.getFechaHoraRespuesta() == null){
                r.setFechaHoraRespuesta(ahora);
            }
        }

        return respuestaDAO.guardarListaRespuestas(respuestas);
    }

}