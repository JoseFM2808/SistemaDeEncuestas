package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement; // Para RETURN_GENERATED_KEYS
import java.util.ArrayList;
import java.util.List;

public class PreguntaBancoDAO {

    /**
     * Registra una nueva pregunta en el banco de preguntas.
     *
     * @param pregunta El objeto {@link PreguntaBanco} a registrar.
     *                 Se espera que {@code texto_pregunta}, {@code id_tipo_pregunta},
     *                 {@code estado}, y {@code id_usuario_creador} no sean nulos.
     *                 {@code fecha_creacion} se establecerá si es nula.
     *                 {@code id_clasificacion_pregunta} puede ser nulo.
     * @return {@code true} si la pregunta se registró exitosamente (y se pudo obtener el ID generado),
     *         {@code false} en caso contrario.
     */
    public boolean registrarPregunta(PreguntaBanco pregunta) {
        String sql = "INSERT INTO preguntas_banco (texto_pregunta, id_tipo_pregunta, id_clasificacion_pregunta, estado, fecha_creacion, id_usuario_creador, fecha_modificacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean exito = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                System.err.println("PreguntaBancoDAO: No se pudo conectar a la BD.");
                return false;
            }

            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pregunta.getTextoPregunta());
            ps.setInt(2, pregunta.getIdTipoPregunta());

            if (pregunta.getIdClasificacion() != null) {
                ps.setInt(3, pregunta.getIdClasificacion());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setString(4, pregunta.getEstado()); // e.g., "Activa", "Inactiva"

            if (pregunta.getFechaCreacion() != null) {
                ps.setTimestamp(5, pregunta.getFechaCreacion());
            } else {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }

            if (pregunta.getIdUsuarioCreador() != null) {
                 ps.setInt(6, pregunta.getIdUsuarioCreador());
            } else {
                // Depende de la definición de la tabla, podría ser NULL o un ID de sistema
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            // fecha_modificacion al crear puede ser igual a fecha_creacion o nula
             if (pregunta.getFechaModificacion() != null) {
                ps.setTimestamp(7, pregunta.getFechaModificacion());
            } else {
                 ps.setTimestamp(7, new Timestamp(System.currentTimeMillis())); // O igual a fechaCreacion
            }


            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pregunta.setIdPreguntaBanco(generatedKeys.getInt(1));
                        exito = true;
                        System.out.println("PreguntaBancoDAO: Pregunta registrada con ID: " + pregunta.getIdPreguntaBanco());
                    } else {
                        System.err.println("PreguntaBancoDAO: No se pudo obtener el ID generado para la pregunta.");
                    }
                }
            } else {
                 System.err.println("PreguntaBancoDAO: No se pudo registrar la pregunta (0 filas afectadas).");
            }
        } catch (SQLException e) {
            System.err.println("PreguntaBancoDAO: Error SQL al registrar pregunta: " + e.getMessage());
            e.printStackTrace(); // Considerar un logging más robusto
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return exito;
    }

    /**
     * Lista todas las preguntas existentes en el banco de preguntas.
     *
     * @return Una lista de objetos {@link PreguntaBanco}.
     *         La lista puede estar vacía si no hay preguntas o si ocurre un error.
     */
    public List<PreguntaBanco> listarPreguntas() {
        String sql = "SELECT id_pregunta_banco, texto_pregunta, id_tipo_pregunta, id_clasificacion_pregunta, estado, fecha_creacion, fecha_modificacion, id_usuario_creador FROM preguntas_banco";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PreguntaBanco> preguntas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                System.err.println("PreguntaBancoDAO: No se pudo conectar a la BD.");
                return preguntas; // Devuelve lista vacía
            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                PreguntaBanco pregunta = new PreguntaBanco();
                pregunta.setIdPreguntaBanco(rs.getInt("id_pregunta_banco"));
                pregunta.setTextoPregunta(rs.getString("texto_pregunta"));
                pregunta.setIdTipoPregunta(rs.getInt("id_tipo_pregunta"));

                // id_clasificacion_pregunta puede ser NULL en la BD
                int idClasif = rs.getInt("id_clasificacion_pregunta");
                if (rs.wasNull()) {
                    pregunta.setIdClasificacion(null);
                } else {
                    pregunta.setIdClasificacion(idClasif);
                }

                pregunta.setEstado(rs.getString("estado"));
                pregunta.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                pregunta.setFechaModificacion(rs.getTimestamp("fecha_modificacion"));

                int idUsuario = rs.getInt("id_usuario_creador");
                if (rs.wasNull()){
                    pregunta.setIdUsuarioCreador(null);
                } else {
                    pregunta.setIdUsuarioCreador(idUsuario);
                }
                preguntas.add(pregunta);
            }
        } catch (SQLException e) {
            System.err.println("PreguntaBancoDAO: Error SQL al listar preguntas: " + e.getMessage());
            e.printStackTrace(); // Considerar un logging más robusto
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return preguntas;
    }
    
    // --- Métodos existentes (placeholders o implementaciones parciales) ---
    public PreguntaBanco obtenerPreguntaPorId(int id) {
        // TODO: Implementar correctamente si es necesario para otras funcionalidades.
        //       Actualmente no es parte del REQ de este subtask.
        String sql = "SELECT id_pregunta_banco, texto_pregunta, id_tipo_pregunta, id_clasificacion_pregunta, estado, fecha_creacion, fecha_modificacion, id_usuario_creador FROM preguntas_banco WHERE id_pregunta_banco = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreguntaBanco pregunta = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                System.err.println("PreguntaBancoDAO: No se pudo conectar a la BD.");
                return null;
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                pregunta = new PreguntaBanco();
                pregunta.setIdPreguntaBanco(rs.getInt("id_pregunta_banco"));
                pregunta.setTextoPregunta(rs.getString("texto_pregunta"));
                pregunta.setIdTipoPregunta(rs.getInt("id_tipo_pregunta"));
                int idClasif = rs.getInt("id_clasificacion_pregunta");
                if (rs.wasNull()) pregunta.setIdClasificacion(null); else pregunta.setIdClasificacion(idClasif);
                pregunta.setEstado(rs.getString("estado"));
                pregunta.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                pregunta.setFechaModificacion(rs.getTimestamp("fecha_modificacion"));
                int idUsuario = rs.getInt("id_usuario_creador");
                if (rs.wasNull()) pregunta.setIdUsuarioCreador(null); else pregunta.setIdUsuarioCreador(idUsuario);
            }
        } catch (SQLException e) {
            System.err.println("PreguntaBancoDAO: Error SQL al obtener pregunta por ID " + id + ": " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        // System.out.println("DEBUG: PreguntaBancoDAO.obtenerPreguntaPorId(int) parcialmente implementado.");
        return pregunta;
    }

    public PreguntaBanco obtenerPreguntaPorId(Integer id) {
        if (id == null) return null;
        return obtenerPreguntaPorId(id.intValue());
    }

    public List<PreguntaBanco> listarPreguntasDelBancoConFiltro(String filtroTexto, String filtroTipo) {
        // TODO: Implementar correctamente si es necesario para otras funcionalidades.
        //       Actualmente no es parte del REQ de este subtask.
        System.out.println("DEBUG: PreguntaBancoDAO.listarPreguntasDelBancoConFiltro NO IMPLEMENTADO, devolviendo lista vacía.");
        return new ArrayList<>();
    }
}
