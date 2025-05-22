package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; 

public class UsuarioDAO {

    public boolean registrarUsuario(Usuario usuario) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO usuarios (documento_identidad, nombres_apellidos, email, password, tipo_nivel) VALUES (?, ?, ?, ?, ?)";
        boolean registrado = false;

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, usuario.getDocumentoIdentidad());
                ps.setString(2, usuario.getNombresApellidos());
                ps.setString(3, usuario.getEmail());
                ps.setString(4, usuario.getPassword());
                ps.setString(5, usuario.getTipoNivel());

                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    registrado = true;
                    System.out.println("Usuario registrado exitosamente.");
                } else {
                    System.out.println("No se pudo registrar el usuario.");
                }
            } else {
                System.err.println("No se pudo establecer la conexión con la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(null, ps, con);
        }
        return registrado;
    }

    public Usuario validarUsuario(String email, String password) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario usuario = null;
        String sql = "SELECT id_usuario, documento_identidad, nombres_apellidos, email, password, tipo_nivel, fecha_registro_sistema, estado_cuenta FROM usuarios WHERE email = ? AND password = ?";

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, email);
                ps.setString(2, password);

                rs = ps.executeQuery();

                if (rs.next()) {
                    String estadoCuenta = rs.getString("estado_cuenta");
                    if ("Activo".equalsIgnoreCase(estadoCuenta)) {
                        usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setDocumentoIdentidad(rs.getString("documento_identidad"));
                        usuario.setNombresApellidos(rs.getString("nombres_apellidos"));
                        usuario.setEmail(rs.getString("email"));

                        usuario.setTipoNivel(rs.getString("tipo_nivel"));
                        usuario.setFechaRegistroSistema(rs.getTimestamp("fecha_registro_sistema"));
                        usuario.setEstadoCuenta(estadoCuenta);
                        System.out.println("Usuario validado exitosamente: " + usuario.getEmail());
                    } else {
                        System.out.println("Intento de login para usuario inactivo: " + email);
                    }
                } else {
                    System.out.println("Credenciales incorrectas o usuario no encontrado para: " + email);
                }
            } else {
                System.err.println("No se pudo establecer la conexión con la base de datos para validación.");
            }
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return usuario;
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        System.out.println("Método obtenerUsuarioPorEmail no implementado aún.");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario usuario = null;
        String sql = "SELECT id_usuario, documento_identidad, nombres_apellidos, email, password, tipo_nivel, fecha_registro_sistema, estado_cuenta FROM usuarios WHERE email = ?";

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, email);
                rs = ps.executeQuery();

                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setDocumentoIdentidad(rs.getString("documento_identidad"));
                    usuario.setNombresApellidos(rs.getString("nombres_apellidos"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoNivel(rs.getString("tipo_nivel"));
                    usuario.setFechaRegistroSistema(rs.getTimestamp("fecha_registro_sistema"));
                    usuario.setEstadoCuenta(rs.getString("estado_cuenta"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por email: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(rs, ps, con);
        }
        return usuario;
    }
    
    public boolean actualizarPerfilUsuario(Usuario usuario) {
        System.out.println("Método actualizarPerfilUsuario no implementado aún.");
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "UPDATE usuarios SET documento_identidad = ?, nombres_apellidos = ?, email = ?, password = ?, tipo_nivel = ? WHERE id_usuario = ?";
        boolean actualizado = false;

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, usuario.getDocumentoIdentidad());
                ps.setString(2, usuario.getNombresApellidos());
                ps.setString(3, usuario.getEmail());
                ps.setString(4, usuario.getPassword());
                ps.setString(5, usuario.getTipoNivel());
                ps.setInt(6, usuario.getIdUsuario());

                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    actualizado = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar perfil de usuario: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(null, ps, con);
        }
        return actualizado;
    }

    public boolean cambiarEstadoCuenta(int idUsuario, String nuevoEstado) {
        System.out.println("Método cambiarEstadoCuenta no implementado aún.");
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "UPDATE usuarios SET estado_cuenta = ? WHERE id_usuario = ?";
        boolean actualizado = false;

        try {
            con = ConexionDB.conectar();
            if (con != null) {
                ps = con.prepareStatement(sql);
                ps.setString(1, nuevoEstado);
                ps.setInt(2, idUsuario);

                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    actualizado = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado de cuenta: " + e.getMessage());
        } finally {
            ConexionDB.cerrar(null, ps, con);
        }
        return actualizado;
    }
}