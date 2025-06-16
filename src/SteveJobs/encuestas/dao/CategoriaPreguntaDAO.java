/*
 * Módulo Responsable: Gestión de Entidades Base (Usuarios y Preguntas)
 * Autores: Pablo Alegre
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase DAO para realizar operaciones CRUD en la tabla 'Categorias_Preguntas'.
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.CategoriaPregunta;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaPreguntaDAO {

    /**
     * Crea una nueva categoría de pregunta en la base de datos.
     * @param categoria La categoría a crear.
     * @return true si la creación fue exitosa, false en caso contrario.
     */
    public boolean crearCategoriaPregunta(CategoriaPregunta categoria) {
        String sql = "INSERT INTO Categorias_Preguntas (nombre_categoria, descripcion, estado) VALUES (?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean creada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, categoria.getNombre_categoria());
            ps.setString(2, categoria.getDescripcion());
            ps.setString(3, categoria.getEstado());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId_categoria(generatedKeys.getInt(1));
                        creada = true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("CategoriaPreguntaDAO: Error SQL al crear categoría: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return creada;
    }

    /**
     * Obtiene una categoría de pregunta por su ID.
     * @param idCategoria El ID de la categoría.
     * @return La categoría si se encuentra, null en caso contrario.
     */
    public CategoriaPregunta obtenerCategoriaPreguntaPorId(int idCategoria) {
        String sql = "SELECT id_categoria, nombre_categoria, descripcion, estado FROM Categorias_Preguntas WHERE id_categoria = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CategoriaPregunta categoria = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCategoria);
            rs = ps.executeQuery();

            if (rs.next()) {
                categoria = mapearResultSetACategoria(rs);
            }
        } catch (SQLException e) {
            System.err.println("CategoriaPreguntaDAO: Error SQL al obtener categoría por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return categoria;
    }

    /**
     * Actualiza una categoría de pregunta existente.
     * @param categoria La categoría con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarCategoriaPregunta(CategoriaPregunta categoria) {
        String sql = "UPDATE Categorias_Preguntas SET nombre_categoria = ?, descripcion = ?, estado = ? WHERE id_categoria = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setString(1, categoria.getNombre_categoria());
            ps.setString(2, categoria.getDescripcion());
            ps.setString(3, categoria.getEstado());
            ps.setInt(4, categoria.getId_categoria());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                actualizada = true;
            }
        } catch (SQLException e) {
            System.err.println("CategoriaPreguntaDAO: Error SQL al actualizar categoría: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return actualizada;
    }

    /**
     * Elimina una categoría de pregunta (se recomienda borrado lógico cambiando estado).
     * Esta implementación realiza un borrado físico.
     * @param idCategoria El ID de la categoría a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarCategoriaPregunta(int idCategoria) {
        String sql = "DELETE FROM Categorias_Preguntas WHERE id_categoria = ?";
        // Considerar si existen preguntas en Banco_Preguntas asociadas a esta categoría antes de eliminar.
        // Se podría requerir lógica adicional para manejar esas dependencias (ej. no permitir eliminar si hay preguntas asociadas).
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminada = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCategoria);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminada = true;
            }
        } catch (SQLException e) {
            System.err.println("CategoriaPreguntaDAO: Error SQL al eliminar categoría: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return eliminada;
    }

    /**
     * Obtiene todas las categorías de preguntas.
     * @return Una lista de todas las categorías.
     */
    public List<CategoriaPregunta> obtenerTodasLasCategorias() {
        String sql = "SELECT id_categoria, nombre_categoria, descripcion, estado FROM Categorias_Preguntas";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CategoriaPregunta> categorias = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return categorias;
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                categorias.add(mapearResultSetACategoria(rs));
            }
        } catch (SQLException e) {
            System.err.println("CategoriaPreguntaDAO: Error SQL al obtener todas las categorías: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return categorias;
    }

    /**
     * Obtiene todas las categorías de preguntas por estado.
     * @param estado El estado de la categoría a filtrar.
     * @return Una lista de todas las categorías.
     */
    public List<CategoriaPregunta> obtenerCategoriasPorEstado(String estado) {
        String sql = "SELECT id_categoria, nombre_categoria, descripcion, estado FROM Categorias_Preguntas WHERE estado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CategoriaPregunta> categorias = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return categorias;
            ps = con.prepareStatement(sql);
            ps.setString(1, estado);
            rs = ps.executeQuery();

            while (rs.next()) {
                categorias.add(mapearResultSetACategoria(rs));
            }
        } catch (SQLException e) {
            System.err.println("CategoriaPreguntaDAO: Error SQL al obtener categorías por estado: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return categorias;
    }


    private CategoriaPregunta mapearResultSetACategoria(ResultSet rs) throws SQLException {
        CategoriaPregunta categoria = new CategoriaPregunta();
        categoria.setId_categoria(rs.getInt("id_categoria"));
        categoria.setNombre_categoria(rs.getString("nombre_categoria"));
        categoria.setDescripcion(rs.getString("descripcion"));
        categoria.setEstado(rs.getString("estado"));
        return categoria;
    }
}
