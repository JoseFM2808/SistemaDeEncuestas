/*
 * Módulo Responsable: Admin. de Encuestas
 * Autores: Alfredo Swidin
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase DAO para gestionar la asociación entre encuestas y preguntas (tabla 'Encuesta_Preguntas').
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.EncuestaPregunta;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EncuestaPreguntaDAO {

    /**
     * Asocia una pregunta a una encuesta.
     * @param encuestaPregunta El objeto EncuestaPregunta a guardar.
     * @return true si la asociación fue exitosa, false en caso contrario.
     */
    public boolean asociarPreguntaAEncuesta(EncuestaPregunta encuestaPregunta) {
        String sql = "INSERT INTO Encuesta_Preguntas (id_encuesta, id_pregunta, orden, es_descarte, criterio_descarte) VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean asociada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, encuestaPregunta.getId_encuesta());
            ps.setInt(2, encuestaPregunta.getId_pregunta());
            ps.setInt(3, encuestaPregunta.getOrden());
            ps.setBoolean(4, encuestaPregunta.isEs_descarte());
            ps.setString(5, encuestaPregunta.getCriterio_descarte());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        encuestaPregunta.setId_encuesta_pregunta(generatedKeys.getInt(1));
                        asociada = true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("EncuestaPreguntaDAO: Error SQL al asociar pregunta a encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return asociada;
    }

    /**
     * Obtiene una asociación Encuesta-Pregunta por su ID.
     * @param idEncuestaPregunta El ID de la asociación.
     * @return El objeto EncuestaPregunta si se encuentra, null en caso contrario.
     */
    public EncuestaPregunta obtenerAsociacionPorId(int idEncuestaPregunta) {
        String sql = "SELECT id_encuesta_pregunta, id_encuesta, id_pregunta, orden, es_descarte, criterio_descarte FROM Encuesta_Preguntas WHERE id_encuesta_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        EncuestaPregunta ep = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuestaPregunta);
            rs = ps.executeQuery();

            if (rs.next()) {
                ep = mapearResultSetAEncuestaPregunta(rs);
            }
        } catch (SQLException e) {
            System.err.println("EncuestaPreguntaDAO: Error SQL al obtener asociación por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return ep;
    }

    /**
     * Obtiene todas las preguntas asociadas a una encuesta específica.
     * @param idEncuesta El ID de la encuesta.
     * @return Una lista de objetos EncuestaPregunta.
     */
    public List<EncuestaPregunta> obtenerPreguntasPorEncuesta(int idEncuesta) {
        String sql = "SELECT id_encuesta_pregunta, id_encuesta, id_pregunta, orden, es_descarte, criterio_descarte FROM Encuesta_Preguntas WHERE id_encuesta = ? ORDER BY orden ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EncuestaPregunta> preguntas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return preguntas;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);
            rs = ps.executeQuery();

            while (rs.next()) {
                preguntas.add(mapearResultSetAEncuestaPregunta(rs));
            }
        } catch (SQLException e) {
            System.err.println("EncuestaPreguntaDAO: Error SQL al obtener preguntas por encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return preguntas;
    }

    /**
     * Actualiza los detalles de una asociación pregunta-encuesta (ej. orden, descarte).
     * @param encuestaPregunta El objeto EncuestaPregunta con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarAsociacion(EncuestaPregunta encuestaPregunta) {
        String sql = "UPDATE Encuesta_Preguntas SET id_encuesta = ?, id_pregunta = ?, orden = ?, es_descarte = ?, criterio_descarte = ? WHERE id_encuesta_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, encuestaPregunta.getId_encuesta());
            ps.setInt(2, encuestaPregunta.getId_pregunta());
            ps.setInt(3, encuestaPregunta.getOrden());
            ps.setBoolean(4, encuestaPregunta.isEs_descarte());
            ps.setString(5, encuestaPregunta.getCriterio_descarte());
            ps.setInt(6, encuestaPregunta.getId_encuesta_pregunta());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                actualizada = true;
            }
        } catch (SQLException e) {
            System.err.println("EncuestaPreguntaDAO: Error SQL al actualizar asociación: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return actualizada;
    }

    /**
     * Elimina una asociación específica pregunta-encuesta por su ID.
     * @param idEncuestaPregunta El ID de la asociación a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarAsociacion(int idEncuestaPregunta) {
        String sql = "DELETE FROM Encuesta_Preguntas WHERE id_encuesta_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuestaPregunta);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminada = true;
            }
        } catch (SQLException e) {
            System.err.println("EncuestaPreguntaDAO: Error SQL al eliminar asociación: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return eliminada;
    }

    /**
     * Elimina todas las asociaciones de preguntas para una encuesta específica.
     * Útil cuando se elimina una encuesta.
     * @param idEncuesta El ID de la encuesta cuyas preguntas asociadas se eliminarán.
     * @return true si la eliminación fue exitosa (o no había nada que eliminar), false si hubo un error.
     */
    public boolean eliminarTodasLasPreguntasDeEncuesta(int idEncuesta) {
        String sql = "DELETE FROM Encuesta_Preguntas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminadas = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);

            // executeUpdate devuelve el número de filas afectadas.
            // Si no hay filas, no es un error, simplemente no había nada que eliminar.
            ps.executeUpdate();
            eliminadas = true;
        } catch (SQLException e) {
            System.err.println("EncuestaPreguntaDAO: Error SQL al eliminar todas las preguntas de la encuesta ID " + idEncuesta + ": " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return eliminadas;
    }

    /**
     * Cuenta el número de preguntas asociadas a una encuesta.
     * @param idEncuesta El ID de la encuesta.
     * @return El número de preguntas, o -1 si hay un error.
     */
    public int contarPreguntasEnEncuesta(int idEncuesta) {
        String sql = "SELECT COUNT(*) FROM Encuesta_Preguntas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = -1;

        try {
            con = ConexionDB.conectar();
            if (con == null) return -1;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("EncuestaPreguntaDAO: Error SQL al contar preguntas en encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return count;
    }

    private EncuestaPregunta mapearResultSetAEncuestaPregunta(ResultSet rs) throws SQLException {
        EncuestaPregunta ep = new EncuestaPregunta();
        ep.setId_encuesta_pregunta(rs.getInt("id_encuesta_pregunta"));
        ep.setId_encuesta(rs.getInt("id_encuesta"));
        ep.setId_pregunta(rs.getInt("id_pregunta"));
        ep.setOrden(rs.getInt("orden"));
        ep.setEs_descarte(rs.getBoolean("es_descarte"));
        ep.setCriterio_descarte(rs.getString("criterio_descarte"));
        return ep;
    }
}
