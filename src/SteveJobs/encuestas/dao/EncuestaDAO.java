/*
 * Autores del Módulo:
 * - Alfredo Swidin
 * - Asistente de AED (Refactorización)
 *
 * Responsabilidad Principal:
 * - Acceso a datos para la entidad Encuesta, alineado con el modelo y diccionario finales.
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EncuestaDAO {

    public int crearEncuesta(Encuesta encuesta) {
        String sql = "INSERT INTO encuestas (nombre, descripcion, fecha_inicio, fecha_fin, publico_objetivo, perfil_requerido, estado, fecha_creacion, id_admin_creador) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        int idGenerado = -1;

        try {
            con = ConexionDB.conectar();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, encuesta.getNombre());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, encuesta.getFechaInicio());
            ps.setTimestamp(4, encuesta.getFechaFin());
            ps.setInt(5, encuesta.getPublicoObjetivo());
            ps.setString(6, encuesta.getPerfilRequerido());
            ps.setString(7, encuesta.getEstado());
            ps.setTimestamp(8, encuesta.getFechaCreacion());
            ps.setInt(9, encuesta.getIdAdminCreador());

            if (ps.executeUpdate() > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idGenerado = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al crear encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(generatedKeys, ps, con);
        }
        return idGenerado;
    }
    
    public Encuesta obtenerEncuestaPorId(int idEncuesta) {
        String sql = "SELECT * FROM encuestas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Encuesta encuesta = null;
        try {
            con = ConexionDB.conectar();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);
            rs = ps.executeQuery();
            if (rs.next()) {
                encuesta = new Encuesta();
                encuesta.setIdEncuesta(rs.getInt("id_encuesta"));
                encuesta.setNombre(rs.getString("nombre"));
                encuesta.setDescripcion(rs.getString("descripcion"));
                encuesta.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                encuesta.setFechaFin(rs.getTimestamp("fecha_fin"));
                encuesta.setPublicoObjetivo(rs.getInt("publico_objetivo"));
                encuesta.setPerfilRequerido(rs.getString("perfil_requerido"));
                encuesta.setEstado(rs.getString("estado"));
                encuesta.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                encuesta.setIdAdminCreador(rs.getInt("id_admin_creador"));
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al obtener encuesta por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return encuesta;
    }

    public List<Encuesta> obtenerTodasLasEncuestas() {
        List<Encuesta> lista = new ArrayList<>();
        String sql = "SELECT * FROM encuestas ORDER BY fecha_creacion DESC";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Encuesta encuesta = new Encuesta();
                encuesta.setIdEncuesta(rs.getInt("id_encuesta"));
                encuesta.setNombre(rs.getString("nombre"));
                encuesta.setDescripcion(rs.getString("descripcion"));
                encuesta.setEstado(rs.getString("estado"));
                encuesta.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                encuesta.setFechaFin(rs.getTimestamp("fecha_fin"));
                lista.add(encuesta);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al obtener todas las encuestas: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean actualizarEncuesta(Encuesta encuesta) {
        String sql = "UPDATE encuestas SET nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, publico_objetivo = ?, perfil_requerido = ?, estado = ? WHERE id_encuesta = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, encuesta.getNombre());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, encuesta.getFechaInicio());
            ps.setTimestamp(4, encuesta.getFechaFin());
            ps.setInt(5, encuesta.getPublicoObjetivo());
            ps.setString(6, encuesta.getPerfilRequerido());
            ps.setString(7, encuesta.getEstado());
            ps.setInt(8, encuesta.getIdEncuesta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al actualizar encuesta: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarEstadoEncuesta(int idEncuesta, String nuevoEstado) {
         String sql = "UPDATE encuestas SET estado = ? WHERE id_encuesta = ?";
         try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEncuesta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al actualizar estado de encuesta: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarEncuesta(int idEncuesta) {
        String sql = "DELETE FROM encuestas WHERE id_encuesta = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEncuesta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al eliminar encuesta: " + e.getMessage());
            return false;
        }
    }
}