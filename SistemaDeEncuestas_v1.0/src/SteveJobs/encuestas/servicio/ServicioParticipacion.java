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