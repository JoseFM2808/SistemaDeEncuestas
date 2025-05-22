/*
Autor: José Flores

*/
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.RespuestaUsuario;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class RespuestaUsuarioDAO {

    public boolean guardarListaRespuestas(List<RespuestaUsuario> listaRespuestas) {
        if (listaRespuestas == null || listaRespuestas.isEmpty()) {
            return true;
        }
        String sql = "INSERT INTO respuestas_usuarios (id_encuesta_detalle_pregunta, id_usuario, valor_respuesta, fecha_hora_respuesta, ts_inicio_participacion, ts_fin_participacion, retroalimentacion_usr) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exitoTotal = true;

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                con.setAutoCommit(false);
                ps = con.prepareStatement(sql);

                for (RespuestaUsuario respuesta : listaRespuestas) {
                    ps.setInt(1, respuesta.getIdEncuestaDetallePregunta());
                    ps.setInt(2, respuesta.getIdUsuario());
                    ps.setString(3, respuesta.getValorRespuesta());
                    ps.setTimestamp(4, respuesta.getFechaHoraRespuesta() != null ? respuesta.getFechaHoraRespuesta() : new Timestamp(System.currentTimeMillis()));
                    ps.setTimestamp(5, respuesta.getTsInicioParticipacion());
                    ps.setTimestamp(6, respuesta.getTsFinParticipacion());
                    ps.setString(7, respuesta.getRetroalimentacionUsuario());
                    ps.addBatch();
                }
                int[] resultados = ps.executeBatch();
                con.commit();

                for (int resultado : resultados) {
                    if (resultado == PreparedStatement.EXECUTE_FAILED) {
                        exitoTotal = false;
                        System.err.println("DAO: Falló una inserción en el lote de respuestas.");
                        break;
                    }
                }
                if(exitoTotal) System.out.println("DAO: Lote de respuestas guardado exitosamente.");

            } else {
                exitoTotal = false;
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al guardar lista de respuestas: " + e.getMessage());
            e.printStackTrace();
            exitoTotal = false;
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.err.println("DAO Error al hacer rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                }
            }
            ConexionDB.cerrar(null, ps, con);
        }
        return exitoTotal;
    }

}
