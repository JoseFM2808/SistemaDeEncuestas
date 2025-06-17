/*
 * Autores del Módulo:
 * - Pablo Alegre
 *
 * Responsabilidad Principal:
 * - Acceso a datos de usuarios
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // --- INICIO DE MÉTODOS CRUD ---

    /**
     * Registra un nuevo usuario en la base de datos.
     * @param usuario El objeto Usuario a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean crearUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (dni, nombres, apellidos, email, clave, fecha_nacimiento, genero, distrito_residencia, fecha_registro, rol) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean creado = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                System.err.println("UsuarioDAO: No se pudo establecer la conexión con la base de datos.");
                return false;
            }
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, usuario.getDni());
            ps.setString(2, usuario.getNombres());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getClave());
            if (usuario.getFecha_nacimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(usuario.getFecha_nacimiento()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, usuario.getGenero());
            ps.setString(8, usuario.getDistrito_residencia());
            if (usuario.getFecha_registro() != null) {
                ps.setTimestamp(9, usuario.getFecha_registro());
            } else {
                ps.setTimestamp(9, new Timestamp(System.currentTimeMillis())); // Default to now
            }
            ps.setString(10, usuario.getRol()); // Añadir rol

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId_usuario(generatedKeys.getInt(1));
                        creado = true;
                        System.out.println("UsuarioDAO: Usuario registrado con ID: " + usuario.getId_usuario());
                    }
                }
            } else {
                System.err.println("UsuarioDAO: No se pudo registrar el usuario (0 filas afectadas).");
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(ps); // Solo cerrar PreparedStatement aquí
            ConexionDB.cerrar(con); // Cerrar Connection aquí
        }
        return creado;
    }

    /**
     * Obtiene un usuario por su ID.
     * @param idUsuario El ID del usuario a buscar.
     * @return El objeto Usuario si se encuentra, null en caso contrario.
     */
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql = "SELECT id_usuario, dni, nombres, apellidos, email, clave, fecha_nacimiento, genero, distrito_residencia, fecha_registro, rol FROM Usuarios WHERE id_usuario = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario usuario = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearResultSetAUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al obtener usuario por ID: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return usuario;
    }

    /**
     * Obtiene un usuario por su DNI.
     * @param dni El DNI del usuario a buscar.
     * @return El objeto Usuario si se encuentra, null en caso contrario.
     */
    public Usuario obtenerUsuarioPorDNI(String dni) {
        String sql = "SELECT id_usuario, dni, nombres, apellidos, email, clave, fecha_nacimiento, genero, distrito_residencia, fecha_registro, rol FROM Usuarios WHERE dni = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario usuario = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearResultSetAUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al obtener usuario por DNI: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return usuario;
    }

    /**
     * Obtiene un usuario por su email.
     * @param email El email del usuario a buscar.
     * @return El objeto Usuario si se encuentra, null en caso contrario.
     */
    public Usuario obtenerUsuarioPorEmail(String email) {
        String sql = "SELECT id_usuario, dni, nombres, apellidos, email, clave, fecha_nacimiento, genero, distrito_residencia, fecha_registro, rol FROM Usuarios WHERE email = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario usuario = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) return null;
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = mapearResultSetAUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al obtener usuario por email: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return usuario;
    }
    
    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     * La clave no se actualiza con este método por seguridad.
     * @param usuario El objeto Usuario con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarUsuario(Usuario usuario) {
        // El rol se actualiza aquí también, si es necesario. Si no, se puede quitar de la sentencia SQL.
        String sql = "UPDATE Usuarios SET dni = ?, nombres = ?, apellidos = ?, email = ?, fecha_nacimiento = ?, genero = ?, distrito_residencia = ?, rol = ? WHERE id_usuario = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setString(1, usuario.getDni());
            ps.setString(2, usuario.getNombres());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getEmail());
            if (usuario.getFecha_nacimiento() != null) {
                ps.setDate(5, java.sql.Date.valueOf(usuario.getFecha_nacimiento()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, usuario.getGenero());
            ps.setString(7, usuario.getDistrito_residencia());
            ps.setString(8, usuario.getRol()); // Actualizar rol
            ps.setInt(9, usuario.getId_usuario());


            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                actualizado = true;
                System.out.println("UsuarioDAO: Usuario ID " + usuario.getId_usuario() + " actualizado.");
            } else {
                System.err.println("UsuarioDAO: No se encontró usuario con ID " + usuario.getId_usuario() + " para actualizar o no hubo cambios.");
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al actualizar usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return actualizado;
    }

    /**
     * Actualiza la clave de un usuario.
     * @param idUsuario El ID del usuario.
     * @param nuevaClave La nueva clave (ya hasheada si es necesario).
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarClaveUsuario(int idUsuario, String nuevaClave) {
        String sql = "UPDATE Usuarios SET clave = ? WHERE id_usuario = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean actualizado = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevaClave);
            ps.setInt(2, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                actualizado = true;
                System.out.println("UsuarioDAO: Clave del usuario ID " + idUsuario + " actualizada.");
            } else {
                 System.err.println("UsuarioDAO: No se encontró usuario con ID " + idUsuario + " para actualizar clave.");
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al actualizar clave de usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return actualizado;
    }


    /**
     * Elimina un usuario de la base de datos.
     * @param idUsuario El ID del usuario a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarUsuario(int idUsuario) {
        // Considerar si es mejor un borrado lógico (cambiar estado) en lugar de físico.
        // Por ahora, se implementa borrado físico según CRUD estándar.
        String sql = "DELETE FROM Usuarios WHERE id_usuario = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean eliminado = false;

        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminado = true;
                System.out.println("UsuarioDAO: Usuario ID " + idUsuario + " eliminado.");
            } else {
                System.err.println("UsuarioDAO: No se encontró usuario con ID " + idUsuario + " para eliminar.");
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al eliminar usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return eliminado;
    }

    /**
     * Obtiene todos los usuarios de la base de datos.
     * @return Una lista de objetos Usuario.
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        String sql = "SELECT id_usuario, dni, nombres, apellidos, email, clave, fecha_nacimiento, genero, distrito_residencia, fecha_registro, rol FROM Usuarios";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Usuario> usuarios = new ArrayList<>();

        try {
            con = ConexionDB.conectar();
            if (con == null) return usuarios; // Devuelve lista vacía si no hay conexión
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearResultSetAUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al obtener todos los usuarios: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return usuarios;
    }

    // --- FIN DE MÉTODOS CRUD ---

    // --- INICIO DE MÉTODOS AUXILIARES ---
    /**
     * Mapea un ResultSet a un objeto Usuario.
     * @param rs El ResultSet con los datos del usuario.
     * @return Un objeto Usuario.
     * @throws SQLException Si ocurre un error al acceder a los datos del ResultSet.
     */
    private Usuario mapearResultSetAUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(rs.getInt("id_usuario"));
        usuario.setDni(rs.getString("dni"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setEmail(rs.getString("email"));
        usuario.setClave(rs.getString("clave")); // Se recupera, pero no se usa directamente en lógica de negocio sensible sin hashear.

        java.sql.Date fechaNacimientoSql = rs.getDate("fecha_nacimiento");
        if (fechaNacimientoSql != null) {
            usuario.setFecha_nacimiento(fechaNacimientoSql.toLocalDate());
        } else {
            usuario.setFecha_nacimiento(null);
        }

        usuario.setGenero(rs.getString("genero"));
        usuario.setDistrito_residencia(rs.getString("distrito_residencia"));
        usuario.setFecha_registro(rs.getTimestamp("fecha_registro"));
        usuario.setRol(rs.getString("rol")); // Mapear rol
        return usuario;
    }
    // --- FIN DE MÉTODOS AUXILIARES ---
    
    public Usuario validarUsuario(String email, String clave) {
        // Usamos la misma consulta que obtenerUsuarioPorEmail pero añadiendo la clave
        String sql = "SELECT id_usuario, dni, nombres, apellidos, email, clave, fecha_nacimiento, genero, distrito_residencia, fecha_registro, rol FROM Usuarios WHERE email = ? AND clave = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario usuario = null;

        try {
            con = ConexionDB.conectar();
            if (con == null) {
                 System.err.println("UsuarioDAO: No se pudo conectar a la BD para validar.");
                 return null;
            }
            
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, clave);

            rs = ps.executeQuery();

            // Si se encuentra una fila, las credenciales son correctas
            if (rs.next()) {
                // Reutilizamos el método que ya tienes para no repetir código
                usuario = mapearResultSetAUsuario(rs); 
                System.out.println("UsuarioDAO: Credenciales validadas para el usuario: " + email);
            } else {
                System.out.println("UsuarioDAO: Credenciales incorrectas para el usuario: " + email);
            }
        } catch (SQLException e) {
            System.err.println("UsuarioDAO: Error SQL al validar usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.cerrar(rs);
            ConexionDB.cerrar(ps);
            ConexionDB.cerrar(con);
        }
        return usuario;
    }
}
