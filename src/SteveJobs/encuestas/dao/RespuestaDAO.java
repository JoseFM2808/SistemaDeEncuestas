/*
 * Módulo Responsable: Interacción y Resultados
 * Autores: José Flores
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase DAO para realizar operaciones CRUD en la tabla 'Respuestas'.
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.Respuesta;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RespuestaDAO {

    /**
     * Guarda una nueva respuesta en la base de datos.
     * @param respuesta La respuesta a guardar.
     * @return true si la respuesta fue guardada exitosamente, false en caso contrario.
     */
    public boolean guardarRespuesta(Respuesta respuesta) {
        String sql = "INSERT INTO Respuestas (id_encuesta_pregunta, id_usuario, valor_respuesta, fecha_respuesta) VALUES (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean guardada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, respuesta.getId_encuesta_pregunta());
            ps.setInt(2, respuesta.getId_usuario());
            ps.setString(3, respuesta.getValor_respuesta());
            if (respuesta.getFecha_respuesta() != null) {
                ps.setTimestamp(4, respuesta.getFecha_respuesta());
            } else {
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Default to now
            }

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        respuesta.setId_respuesta(generatedKeys.getInt(1));
                        guardada = true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("RespuestaDAO: Error SQL al guardar respuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return guardada;
    }

    /**
     * Obtiene una respuesta por su ID.
     * @param idRespuesta El ID de la respuesta.
     * @return La respuesta si se encuentra, null en caso contrario.
     */
    public Respuesta obtenerRespuestaPorId(int idRespuesta) {
        String sql = "SELECT id_respuesta, id_encuesta_pregunta, id_usuario, valor_respuesta, fecha_respuesta FROM Respuestas WHERE id_respuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Respuesta respuesta = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idRespuesta);
            rs = ps.executeQuery();

            if (rs.next()) {
                respuesta = mapearResultSetARespuesta(rs);
            }
        } catch (SQLException e) {
            System.err.println("RespuestaDAO: Error SQL al obtener respuesta por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return respuesta;
    }

    /**
     * Obtiene todas las respuestas de un usuario para una encuesta específica (a través de EncuestaPregunta).
     * @param idUsuario El ID del usuario.
     * @param idEncuesta El ID de la encuesta.
     * @return Una lista de respuestas.
     */
    public List<Respuesta> obtenerRespuestasPorUsuarioYEncuesta(int idUsuario, int idEncuesta) {
        // Esta consulta requiere un JOIN con Encuesta_Preguntas
        String sql = "SELECT r.id_respuesta, r.id_encuesta_pregunta, r.id_usuario, r.valor_respuesta, r.fecha_respuesta " +
                     "FROM Respuestas r " +
                     "JOIN Encuesta_Preguntas ep ON r.id_encuesta_pregunta = ep.id_encuesta_pregunta " +
                     "WHERE r.id_usuario = ? AND ep.id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Respuesta> respuestas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return respuestas;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEncuesta);
            rs = ps.executeQuery();

            while (rs.next()) {
                respuestas.add(mapearResultSetARespuesta(rs));
            }
        } catch (SQLException e) {
            System.err.println("RespuestaDAO: Error SQL al obtener respuestas por usuario y encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return respuestas;
    }

    /**
     * Obtiene todas las respuestas para una pregunta específica de una encuesta (id_encuesta_pregunta).
     * @param idEncuestaPregunta El ID de la encuesta_pregunta.
     * @return Una lista de respuestas.
     */
    public List<Respuesta> obtenerRespuestasPorEncuestaPregunta(int idEncuestaPregunta) {
        String sql = "SELECT id_respuesta, id_encuesta_pregunta, id_usuario, valor_respuesta, fecha_respuesta FROM Respuestas WHERE id_encuesta_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Respuesta> respuestas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return respuestas;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuestaPregunta);
            rs = ps.executeQuery();

            while (rs.next()) {
                respuestas.add(mapearResultSetARespuesta(rs));
            }
        } catch (SQLException e) {
            System.err.println("RespuestaDAO: Error SQL al obtener respuestas por id_encuesta_pregunta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return respuestas;
    }


    /**
     * Elimina todas las respuestas asociadas a una encuesta_pregunta específica.
     * Útil si se elimina una pregunta de una encuesta.
     * @param idEncuestaPregunta El ID de la encuesta_pregunta.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean eliminarRespuestasPorEncuestaPregunta(int idEncuestaPregunta) {
        String sql = "DELETE FROM Respuestas WHERE id_encuesta_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminadas = false;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuestaPregunta);
            ps.executeUpdate(); // No importa cuántas filas, si no hay error, es "exitoso"
            eliminadas = true;
        } catch (SQLException e) {
            System.err.println("RespuestaDAO: Error SQL al eliminar respuestas por encuesta_pregunta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return eliminadas;
    }

    /**
     * Elimina todas las respuestas de un usuario específico para una encuesta dada.
     * @param idUsuario El ID del usuario.
     * @param idEncuesta El ID de la encuesta.
     * @return El número de respuestas eliminadas, o -1 si hay un error.
     */
    public int eliminarRespuestasDeUsuarioParaEncuesta(int idUsuario, int idEncuesta) {
        String sql = "DELETE r FROM Respuestas r " +
                     "JOIN Encuesta_Preguntas ep ON r.id_encuesta_pregunta = ep.id_encuesta_pregunta " +
                     "WHERE r.id_usuario = ? AND ep.id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        int filasAfectadas = -1;
        try {
            con = ConexionDB.conectar();
            if (con == null) return -1;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEncuesta);
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("RespuestaDAO: Error SQL al eliminar respuestas de usuario para encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return filasAfectadas;
    }


    private Respuesta mapearResultSetARespuesta(ResultSet rs) throws SQLException {
        Respuesta respuesta = new Respuesta();
        respuesta.setId_respuesta(rs.getInt("id_respuesta"));
        respuesta.setId_encuesta_pregunta(rs.getInt("id_encuesta_pregunta"));
        respuesta.setId_usuario(rs.getInt("id_usuario"));
        respuesta.setValor_respuesta(rs.getString("valor_respuesta"));
        respuesta.setFecha_respuesta(rs.getTimestamp("fecha_respuesta"));
        return respuesta;
    }
}
