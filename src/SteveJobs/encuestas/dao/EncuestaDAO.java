/*
 * Módulo Responsable: Admin. de Encuestas
 * Autores: Alfredo Swidin
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase DAO para realizar operaciones CRUD en la tabla 'Encuestas'.
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EncuestaDAO {

    /**
     * Crea una nueva encuesta en la base de datos.
     * @param encuesta La encuesta a crear.
     * @return El ID de la encuesta creada, o -1 si falla.
     */
    public int crearEncuesta(Encuesta encuesta) {
        String sql = "INSERT INTO Encuestas (nombre, descripcion, fecha_inicio, fecha_fin, perfil_requerido, estado) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        int generatedId = -1;

        try {
            con = ConexionDB.conectar();
            if (con == null) return -1;
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, encuesta.getNombre());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, encuesta.getFecha_inicio());
            ps.setTimestamp(4, encuesta.getFecha_fin());
            ps.setString(5, encuesta.getPerfil_requerido()); // JSON como String
            ps.setString(6, encuesta.getEstado());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        encuesta.setId_encuesta(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("EncuestaDAO: Error SQL al crear encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return generatedId;
    }

    /**
     * Obtiene una encuesta por su ID.
     * @param idEncuesta El ID de la encuesta.
     * @return La encuesta si se encuentra, null en caso contrario.
     */
    public Encuesta obtenerEncuestaPorId(int idEncuesta) {
        String sql = "SELECT id_encuesta, nombre, descripcion, fecha_inicio, fecha_fin, perfil_requerido, estado FROM Encuestas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Encuesta encuesta = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);
            rs = ps.executeQuery();

            if (rs.next()) {
                encuesta = mapearResultSetAEncuesta(rs);
            }
        } catch (SQLException e) {
            System.err.println("EncuestaDAO: Error SQL al obtener encuesta por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return encuesta;
    }

    /**
     * Actualiza una encuesta existente.
     * @param encuesta La encuesta con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarEncuesta(Encuesta encuesta) {
        String sql = "UPDATE Encuestas SET nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, perfil_requerido = ?, estado = ? WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setString(1, encuesta.getNombre());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, encuesta.getFecha_inicio());
            ps.setTimestamp(4, encuesta.getFecha_fin());
            ps.setString(5, encuesta.getPerfil_requerido());
            ps.setString(6, encuesta.getEstado());
            ps.setInt(7, encuesta.getId_encuesta());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                actualizada = true;
            }
        } catch (SQLException e) {
            System.err.println("EncuestaDAO: Error SQL al actualizar encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return actualizada;
    }

    /**
     * Elimina una encuesta.
     * @param idEncuesta El ID de la encuesta a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarEncuesta(int idEncuesta) {
        // Antes de eliminar, se deberían eliminar las referencias en Encuesta_Preguntas y Respuestas.
        // Esto puede hacerse en la capa de servicio o con cascadas en BD.
        // Aquí solo se elimina la encuesta principal.
        String sql = "DELETE FROM Encuestas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminada = true;
            }
        } catch (SQLException e) {
            System.err.println("EncuestaDAO: Error SQL al eliminar encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return eliminada;
    }

    /**
     * Obtiene todas las encuestas.
     * @return Una lista de todas las encuestas.
     */
    public List<Encuesta> obtenerTodasLasEncuestas() {
        String sql = "SELECT id_encuesta, nombre, descripcion, fecha_inicio, fecha_fin, perfil_requerido, estado FROM Encuestas";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Encuesta> encuestas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return encuestas;
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                encuestas.add(mapearResultSetAEncuesta(rs));
            }
        } catch (SQLException e) {
            System.err.println("EncuestaDAO: Error SQL al obtener todas las encuestas: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return encuestas;
    }
    
    /**
     * Obtiene todas las encuestas por un estado específico.
     * @param estado El estado de las encuestas a filtrar.
     * @return Una lista de encuestas que coinciden con el estado.
     */
    public List<Encuesta> obtenerEncuestasPorEstado(String estado) {
        String sql = "SELECT id_encuesta, nombre, descripcion, fecha_inicio, fecha_fin, perfil_requerido, estado FROM Encuestas WHERE estado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Encuesta> encuestas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return encuestas;
            ps = con.prepareStatement(sql);
            ps.setString(1, estado);
            rs = ps.executeQuery();

            while (rs.next()) {
                encuestas.add(mapearResultSetAEncuesta(rs));
            }
        } catch (SQLException e) {
            System.err.println("EncuestaDAO: Error SQL al obtener encuestas por estado '" + estado + "': " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return encuestas;
    }


    private Encuesta mapearResultSetAEncuesta(ResultSet rs) throws SQLException {
        Encuesta encuesta = new Encuesta();
        encuesta.setId_encuesta(rs.getInt("id_encuesta"));
        encuesta.setNombre(rs.getString("nombre"));
        encuesta.setDescripcion(rs.getString("descripcion"));
        encuesta.setFecha_inicio(rs.getTimestamp("fecha_inicio"));
        encuesta.setFecha_fin(rs.getTimestamp("fecha_fin"));
        encuesta.setPerfil_requerido(rs.getString("perfil_requerido"));
        encuesta.setEstado(rs.getString("estado"));
        return encuesta;
    }
}
