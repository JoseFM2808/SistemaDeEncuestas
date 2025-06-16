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
            // Considerar si devolver true es lo correcto o si debería indicar que no se hizo nada.
            // Para el caso de "no hay respuestas que guardar", true es razonable.
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

    /**
     * Verifica si un usuario específico ya ha respondido a alguna pregunta de una encuesta determinada.
     *
     * Este método comprueba la existencia de al menos una respuesta del usuario para la encuesta,
     * uniéndo las tablas de respuestas de usuario y detalles de preguntas de encuesta.
     *
     * @param idUsuario El ID del usuario a verificar.
     * @param idEncuesta El ID de la encuesta a verificar.
     * @return {@code true} si el usuario ya ha respondido al menos una pregunta de la encuesta,
     *         {@code false} en caso contrario (incluyendo si ocurre un error de base de datos).
     */
    public boolean haRespondidoEncuesta(int idUsuario, int idEncuesta) {
        String sql = "SELECT COUNT(*) " +
                     "FROM respuestas_usuarios ru " +
                     "JOIN encuesta_detalle_preguntas edp ON ru.id_encuesta_detalle_pregunta = edp.id_encuesta_detalle_pregunta " +
                     "WHERE ru.id_usuario = ? AND edp.id_encuesta = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean haRespondido = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                System.err.println("RespuestaUsuarioDAO: No se pudo conectar a la BD para haRespondidoEncuesta.");
                return false; // O lanzar una excepción
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEncuesta);

            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    haRespondido = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("RespuestaUsuarioDAO: Error SQL al verificar si el usuario ha respondido la encuesta: " + e.getMessage());
            e.printStackTrace(); // Considerar logging más robusto
            // En caso de error, podría ser más seguro asumir que ha respondido para evitar dobles respuestas,
            // o devolver false y loguear el error para revisión. Por ahora, se devuelve false.
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return haRespondido;
    }
}
