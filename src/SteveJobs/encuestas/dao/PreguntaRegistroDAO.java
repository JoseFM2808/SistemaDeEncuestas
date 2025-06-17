/*
 * Autores del Módulo:
 * - José Flores
 *
 * Responsabilidad Principal:
 * - Acceso a datos de preguntas de registro
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.PreguntaRegistro;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PreguntaRegistroDAO {

    public boolean agregarPreguntaRegistro(PreguntaRegistro pregunta) {
        String sql = "INSERT INTO preguntas_registro (texto_pregunta, tipo_respuesta, opciones_posibles, es_obligatoria, orden_visualizacion, estado) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, pregunta.getTextoPregunta());
                ps.setString(2, pregunta.getTipoRespuesta());
                ps.setString(3, pregunta.getOpcionesPosibles());
                ps.setBoolean(4, pregunta.isEsObligatoria());
                ps.setInt(5, pregunta.getOrdenVisualizacion());
                ps.setString(6, pregunta.getEstado());

                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    exito = true;
                    System.out.println("DAO: Pregunta de registro guardada exitosamente.");
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al agregar pregunta de registro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(null, ps, con);
        }
        return exito;
    }

    public List<PreguntaRegistro> obtenerTodasLasPreguntasRegistro() {
        List<PreguntaRegistro> preguntas = new ArrayList<>();
        String sql = "SELECT * FROM preguntas_registro ORDER BY orden_visualizacion ASC, id_pregunta_registro ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    PreguntaRegistro pr = new PreguntaRegistro();
                    pr.setIdPreguntaRegistro(rs.getInt("id_pregunta_registro"));
                    pr.setTextoPregunta(rs.getString("texto_pregunta"));
                    pr.setTipoRespuesta(rs.getString("tipo_respuesta"));
                    pr.setOpcionesPosibles(rs.getString("opciones_posibles"));
                    pr.setEsObligatoria(rs.getBoolean("es_obligatoria"));
                    pr.setOrdenVisualizacion(rs.getInt("orden_visualizacion"));
                    pr.setEstado(rs.getString("estado"));
                    preguntas.add(pr);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al obtener preguntas de registro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return preguntas;
    }

    public boolean actualizarPreguntaRegistro(PreguntaRegistro pregunta) {
        String sql = "UPDATE preguntas_registro SET texto_pregunta = ?, tipo_respuesta = ?, opciones_posibles = ?, es_obligatoria = ?, orden_visualizacion = ?, estado = ? WHERE id_pregunta_registro = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, pregunta.getTextoPregunta());
                ps.setString(2, pregunta.getTipoRespuesta());
                ps.setString(3, pregunta.getOpcionesPosibles());
                ps.setBoolean(4, pregunta.isEsObligatoria());
                ps.setInt(5, pregunta.getOrdenVisualizacion());
                ps.setString(6, pregunta.getEstado());
                ps.setInt(7, pregunta.getIdPreguntaRegistro());
                exito = ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al actualizar pregunta de registro: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(null, ps, con);
        }
        return exito;
    }

    public boolean eliminarPreguntaRegistro(int idPreguntaRegistro) {
        String sql = "DELETE FROM preguntas_registro WHERE id_pregunta_registro = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, idPreguntaRegistro);
                exito = ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al eliminar pregunta de registro: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(null, ps, con);
        }
        return exito;
    }
}