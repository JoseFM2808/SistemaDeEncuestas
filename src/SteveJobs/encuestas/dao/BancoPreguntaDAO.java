/*
 * Módulo Responsable: Gestión de Entidades Base (Usuarios y Preguntas)
 * Autores: Pablo Alegre
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase DAO para realizar operaciones CRUD en la tabla 'Banco_Preguntas'.
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.BancoPregunta;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BancoPreguntaDAO {

    /**
     * Crea una nueva pregunta en el banco de preguntas.
     * @param pregunta La pregunta a crear.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    public boolean crearPregunta(BancoPregunta pregunta) {
        String sql = "INSERT INTO Banco_Preguntas (id_categoria, texto_pregunta, tipo_pregunta, opciones) VALUES (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean creada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pregunta.getId_categoria());
            ps.setString(2, pregunta.getTexto_pregunta());
            ps.setString(3, pregunta.getTipo_pregunta());
            ps.setString(4, pregunta.getOpciones()); // Opciones puede ser JSON o texto delimitado

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pregunta.setId_pregunta(generatedKeys.getInt(1));
                        creada = true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("BancoPreguntaDAO: Error SQL al crear pregunta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return creada;
    }

    /**
     * Obtiene una pregunta del banco por su ID.
     * @param idPregunta El ID de la pregunta.
     * @return La pregunta si se encuentra, null en caso contrario.
     */
    public BancoPregunta obtenerPreguntaPorId(int idPregunta) {
        String sql = "SELECT id_pregunta, id_categoria, texto_pregunta, tipo_pregunta, opciones FROM Banco_Preguntas WHERE id_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BancoPregunta pregunta = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idPregunta);
            rs = ps.executeQuery();

            if (rs.next()) {
                pregunta = mapearResultSetAPregunta(rs);
            }
        } catch (SQLException e) {
            System.err.println("BancoPreguntaDAO: Error SQL al obtener pregunta por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return pregunta;
    }

    /**
     * Actualiza una pregunta existente en el banco.
     * @param pregunta La pregunta con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarPregunta(BancoPregunta pregunta) {
        String sql = "UPDATE Banco_Preguntas SET id_categoria = ?, texto_pregunta = ?, tipo_pregunta = ?, opciones = ? WHERE id_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, pregunta.getId_categoria());
            ps.setString(2, pregunta.getTexto_pregunta());
            ps.setString(3, pregunta.getTipo_pregunta());
            ps.setString(4, pregunta.getOpciones());
            ps.setInt(5, pregunta.getId_pregunta());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                actualizada = true;
            }
        } catch (SQLException e) {
            System.err.println("BancoPreguntaDAO: Error SQL al actualizar pregunta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return actualizada;
    }

    /**
     * Elimina una pregunta del banco.
     * @param idPregunta El ID de la pregunta a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarPregunta(int idPregunta) {
        // Considerar si esta pregunta está siendo usada en Encuesta_Preguntas.
        // Podría necesitarse lógica para impedir eliminación o eliminar referencias.
        String sql = "DELETE FROM Banco_Preguntas WHERE id_pregunta = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idPregunta);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminada = true;
            }
        } catch (SQLException e) {
            System.err.println("BancoPreguntaDAO: Error SQL al eliminar pregunta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return eliminada;
    }

    /**
     * Obtiene todas las preguntas del banco.
     * @return Una lista de todas las preguntas.
     */
    public List<BancoPregunta> obtenerTodasLasPreguntas() {
        String sql = "SELECT id_pregunta, id_categoria, texto_pregunta, tipo_pregunta, opciones FROM Banco_Preguntas";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<BancoPregunta> preguntas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return preguntas;
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                preguntas.add(mapearResultSetAPregunta(rs));
            }
        } catch (SQLException e) {
            System.err.println("BancoPreguntaDAO: Error SQL al obtener todas las preguntas: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return preguntas;
    }

    /**
     * Obtiene todas las preguntas de una categoría específica.
     * @param idCategoria El ID de la categoría.
     * @return Una lista de preguntas de esa categoría.
     */
    public List<BancoPregunta> obtenerPreguntasPorCategoria(int idCategoria) {
        String sql = "SELECT id_pregunta, id_categoria, texto_pregunta, tipo_pregunta, opciones FROM Banco_Preguntas WHERE id_categoria = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<BancoPregunta> preguntas = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return preguntas;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCategoria);
            rs = ps.executeQuery();

            while (rs.next()) {
                preguntas.add(mapearResultSetAPregunta(rs));
            }
        } catch (SQLException e) {
            System.err.println("BancoPreguntaDAO: Error SQL al obtener preguntas por categoría: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return preguntas;
    }

    private BancoPregunta mapearResultSetAPregunta(ResultSet rs) throws SQLException {
        BancoPregunta pregunta = new BancoPregunta();
        pregunta.setId_pregunta(rs.getInt("id_pregunta"));
        pregunta.setId_categoria(rs.getInt("id_categoria"));
        pregunta.setTexto_pregunta(rs.getString("texto_pregunta"));
        pregunta.setTipo_pregunta(rs.getString("tipo_pregunta"));
        pregunta.setOpciones(rs.getString("opciones"));
        return pregunta;
    }
}
