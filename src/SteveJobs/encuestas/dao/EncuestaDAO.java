/*
Autor: AlfredoSwidin
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.conexion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EncuestaDAO {

    public int crearEncuesta(Encuesta encuesta) {
        String sql = "INSERT INTO Encuestas (nombre_encuesta, descripcion, fecha_inicio_vigencia, fecha_fin_vigencia, publico_objetivo_cantidad, definicion_perfil, estado, id_admin_creador) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        int idEncuestaGenerada = -1;

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                System.err.println("DAO Error: No se pudo conectar a la BD para crear encuesta.");
                return -1;
            }
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, encuesta.getNombreEncuesta());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, encuesta.getFechaInicioVigencia());
            ps.setTimestamp(4, encuesta.getFechaFinVigencia());
            if (encuesta.getPublicoObjetivoCantidad() > 0) {
                ps.setInt(5, encuesta.getPublicoObjetivoCantidad());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setString(6, encuesta.getDefinicionPerfil());
            ps.setString(7, encuesta.getEstado() != null ? encuesta.getEstado() : "Borrador");
             if (encuesta.getIdAdminCreador() > 0) {
                ps.setInt(8, encuesta.getIdAdminCreador());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }


            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idEncuestaGenerada = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al crear encuesta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(generatedKeys);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return idEncuestaGenerada;
    }

    public boolean actualizarEncuesta(Encuesta encuesta) {
        String sql = "UPDATE Encuestas SET nombre_encuesta = ?, descripcion = ?, fecha_inicio_vigencia = ?, fecha_fin_vigencia = ?, publico_objetivo_cantidad = ?, definicion_perfil = ?, estado = ? WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setString(1, encuesta.getNombreEncuesta());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, encuesta.getFechaInicioVigencia());
            ps.setTimestamp(4, encuesta.getFechaFinVigencia());
            ps.setInt(5, encuesta.getPublicoObjetivoCantidad());
            ps.setString(6, encuesta.getDefinicionPerfil());
            ps.setString(7, encuesta.getEstado());
            ps.setInt(8, encuesta.getIdEncuesta());
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al actualizar encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return exito;
    }

    public boolean eliminarEncuesta(int idEncuesta) {
        String sql = "DELETE FROM Encuestas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al eliminar encuesta (puede ser por FK constraints): " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return exito;
    }

    public Encuesta obtenerEncuestaPorId(int idEncuesta) {
        String sql = "SELECT * FROM Encuestas WHERE id_encuesta = ?";
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
                encuesta = new Encuesta();
                encuesta.setIdEncuesta(rs.getInt("id_encuesta"));
                encuesta.setNombreEncuesta(rs.getString("nombre_encuesta"));
                encuesta.setDescripcion(rs.getString("descripcion"));
                encuesta.setFechaInicioVigencia(rs.getTimestamp("fecha_inicio_vigencia"));
                encuesta.setFechaFinVigencia(rs.getTimestamp("fecha_fin_vigencia"));
                encuesta.setPublicoObjetivoCantidad(rs.getInt("publico_objetivo_cantidad"));
                encuesta.setDefinicionPerfil(rs.getString("definicion_perfil"));
                encuesta.setEstado(rs.getString("estado"));
                encuesta.setFechaCreacionEncuesta(rs.getTimestamp("fecha_creacion_encuesta"));
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
        String sql = "SELECT id_encuesta, nombre_encuesta, descripcion, estado, fecha_inicio_vigencia, fecha_fin_vigencia FROM Encuestas ORDER BY fecha_creacion_encuesta DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ConexionDB.conectar();
            if (con == null) return lista;
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Encuesta encuesta = new Encuesta();
                encuesta.setIdEncuesta(rs.getInt("id_encuesta"));
                encuesta.setNombreEncuesta(rs.getString("nombre_encuesta"));
                encuesta.setDescripcion(rs.getString("descripcion"));
                encuesta.setEstado(rs.getString("estado"));
                encuesta.setFechaInicioVigencia(rs.getTimestamp("fecha_inicio_vigencia"));
                encuesta.setFechaFinVigencia(rs.getTimestamp("fecha_fin_vigencia"));
                lista.add(encuesta);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al obtener todas las encuestas: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return lista;
    }
    
    public boolean actualizarEstadoEncuesta(int idEncuesta, String nuevoEstado) {
        String sql = "UPDATE Encuestas SET estado = ? WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEncuesta);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al actualizar estado de encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps, con);
        }
        return exito;
    }
}