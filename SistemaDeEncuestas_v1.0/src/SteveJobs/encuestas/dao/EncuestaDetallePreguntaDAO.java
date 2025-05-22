/*
Autor: Alfredo Swidin
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.EncuestaDetallePregunta;
import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EncuestaDetallePreguntaDAO {

    public boolean agregarPreguntaAEncuesta(EncuestaDetallePregunta detalle) {
        String sql = "INSERT INTO Encuesta_Detalle_Preguntas (id_encuesta, id_pregunta_banco, texto_pregunta_unica, id_tipo_pregunta_unica, id_clasificacion_unica, orden_en_encuesta, es_pregunta_descarte, criterio_descarte_valor) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, detalle.getIdEncuesta());

            if (detalle.getIdPreguntaBanco() != null && detalle.getIdPreguntaBanco() > 0) {
                ps.setInt(2, detalle.getIdPreguntaBanco());
                ps.setNull(3, java.sql.Types.VARCHAR);
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
                ps.setString(3, detalle.getTextoPreguntaUnica());
                if (detalle.getIdTipoPreguntaUnica() != null && detalle.getIdTipoPreguntaUnica() > 0) {
                    ps.setInt(4, detalle.getIdTipoPreguntaUnica());
                } else {
                     ps.setNull(4, java.sql.Types.INTEGER);
                }
                if (detalle.getIdClasificacionUnica() != null && detalle.getIdClasificacionUnica() > 0) {
                    ps.setInt(5, detalle.getIdClasificacionUnica());
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }
            }
            ps.setInt(6, detalle.getOrdenEnEncuesta());
            ps.setBoolean(7, detalle.isEsPreguntaDescarte());
            ps.setString(8, detalle.getCriterioDescarteValor());

            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al agregar pregunta a encuesta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return exito;
    }

    public List<EncuestaDetallePregunta> obtenerPreguntasPorEncuesta(int idEncuesta) {
        List<EncuestaDetallePregunta> detalles = new ArrayList<>();
        String sql = "SELECT edp.*, " +
                     "pb.texto_pregunta AS texto_banco, pb.id_tipo_pregunta AS id_tipo_banco, pb.id_clasificacion AS id_clasif_banco, " +
                     "tpu.nombre_tipo AS nombre_tipo_unica, cpu.nombre_clasificacion AS nombre_clasif_unica " +
                     "FROM Encuesta_Detalle_Preguntas edp " +
                     "LEFT JOIN Preguntas_Banco pb ON edp.id_pregunta_banco = pb.id_pregunta_banco " +
                     "LEFT JOIN TiposPregunta tpu ON edp.id_tipo_pregunta_unica = tpu.id_tipo_pregunta " +
                     "LEFT JOIN ClasificacionesPregunta cpu ON edp.id_clasificacion_unica = cpu.id_clasificacion " +
                     "WHERE edp.id_encuesta = ? ORDER BY edp.orden_en_encuesta ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return detalles;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);
            rs = ps.executeQuery();
            while (rs.next()) {
                EncuestaDetallePregunta detalle = new EncuestaDetallePregunta();
                detalle.setIdEncuestaDetalle(rs.getInt("id_encuesta_detalle"));
                detalle.setIdEncuesta(rs.getInt("id_encuesta"));
                detalle.setOrdenEnEncuesta(rs.getInt("orden_en_encuesta"));
                detalle.setEsPreguntaDescarte(rs.getBoolean("es_pregunta_descarte"));
                detalle.setCriterioDescarteValor(rs.getString("criterio_descarte_valor"));

                Integer idPreguntaBanco = rs.getInt("id_pregunta_banco");
                if (!rs.wasNull()) {
                    detalle.setIdPreguntaBanco(idPreguntaBanco);
                    PreguntaBanco preguntaDelBanco = new PreguntaBanco();
                    preguntaDelBanco.setIdPreguntaBanco(idPreguntaBanco);
                    preguntaDelBanco.setTextoPregunta(rs.getString("texto_banco"));
                    detalle.setPreguntaDelBanco(preguntaDelBanco);
                } else {
                    detalle.setTextoPreguntaUnica(rs.getString("texto_pregunta_unica"));
                    Integer idTipoUnica = rs.getInt("id_tipo_pregunta_unica");
                    detalle.setIdTipoPreguntaUnica(rs.wasNull() ? null : idTipoUnica);
                    Integer idClasifUnica = rs.getInt("id_clasificacion_unica");
                    detalle.setIdClasificacionUnica(rs.wasNull() ? null : idClasifUnica);
                }
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al obtener preguntas de la encuesta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return detalles;
    }

    public EncuestaDetallePregunta obtenerPreguntaDetallePorId(int idEncuestaDetalle) {
        String sql = "SELECT edp.*, pb.texto_pregunta AS texto_banco FROM Encuesta_Detalle_Preguntas edp " +
                     "LEFT JOIN Preguntas_Banco pb ON edp.id_pregunta_banco = pb.id_pregunta_banco " +
                     "WHERE edp.id_encuesta_detalle = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        EncuestaDetallePregunta detalle = null;
        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, idEncuestaDetalle);
                rs = ps.executeQuery();
                if (rs.next()) {
                    detalle = new EncuestaDetallePregunta();
                    detalle.setIdEncuestaDetalle(rs.getInt("id_encuesta_detalle"));
                    detalle.setIdEncuesta(rs.getInt("id_encuesta"));
                    detalle.setOrdenEnEncuesta(rs.getInt("orden_en_encuesta"));
                    detalle.setEsPreguntaDescarte(rs.getBoolean("es_pregunta_descarte"));
                    detalle.setCriterioDescarteValor(rs.getString("criterio_descarte_valor"));

                    Integer idPreguntaBanco = rs.getInt("id_pregunta_banco");
                    if (!rs.wasNull()) {
                        detalle.setIdPreguntaBanco(idPreguntaBanco);
                        PreguntaBanco pb = new PreguntaBanco();
                        pb.setIdPreguntaBanco(idPreguntaBanco);
                        pb.setTextoPregunta(rs.getString("texto_banco"));
                        detalle.setPreguntaDelBanco(pb);
                    } else {
                        detalle.setTextoPreguntaUnica(rs.getString("texto_pregunta_unica"));
                        Integer idTipoUnica = rs.getInt("id_tipo_pregunta_unica");
                        detalle.setIdTipoPreguntaUnica(rs.wasNull() ? null : idTipoUnica);
                        Integer idClasifUnica = rs.getInt("id_clasificacion_unica");
                        detalle.setIdClasificacionUnica(rs.wasNull() ? null : idClasifUnica);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al obtener detalle de pregunta por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return detalle;
    }

    public boolean actualizarDetallePregunta(EncuestaDetallePregunta detalle) {
        String sql = "UPDATE Encuesta_Detalle_Preguntas SET orden_en_encuesta = ?, es_pregunta_descarte = ?, criterio_descarte_valor = ? WHERE id_encuesta_detalle = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, detalle.getOrdenEnEncuesta());
            ps.setBoolean(2, detalle.isEsPreguntaDescarte());
            ps.setString(3, detalle.getCriterioDescarteValor());
            ps.setInt(4, detalle.getIdEncuestaDetalle());
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al actualizar detalle de pregunta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return exito;
    }

    public boolean eliminarPreguntaDeEncuesta(int idEncuestaDetalle) {
        String sql = "DELETE FROM Encuesta_Detalle_Preguntas WHERE id_encuesta_detalle = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuestaDetalle);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DAO Error al eliminar pregunta de encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return exito;
    }

    public int contarPreguntasEnEncuesta(int idEncuesta) {
        String sql = "SELECT COUNT(*) FROM Encuesta_Detalle_Preguntas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            con = ConexionDB.conectar();
            if (con == null) return 0;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEncuesta);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al contar preguntas en encuesta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return count;
    }

    public boolean eliminarTodasPreguntasDeEncuesta(int idEncuesta) {
        String sql = "DELETE FROM Encuesta_Detalle_Preguntas WHERE id_encuesta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;
        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, idEncuesta);
                ps.executeUpdate();
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("DAO Error al eliminar todas las preguntas de la encuesta ID " + idEncuesta + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(ps, con);
        }
        return exito;
    }
}