/*
 * Autores del Módulo:
 * - Pablo Alegre
 *
 * Responsabilidad Principal:
 * - Lógica de negocio para usuarios
 */
package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.modelo.Usuario;
// import SteveJobs.encuestas.modelo.Usuario; // Duplicate import removed
import SteveJobs.encuestas.dao.UsuarioDAO;
import java.time.LocalDate;
// Timestamp no es necesario aquí si se maneja en DAO/DB para fecha_registro
// import java.sql.Timestamp;

public class ServicioUsuarios {

    private UsuarioDAO usuarioDAO;

    // private static final Pattern EMAIL_PATTERN = ...; // Eliminado según REQ

    public ServicioUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param dni El Documento Nacional de Identidad del usuario. Debe ser único.
     * @param nombres Los nombres completos del usuario.
     * @param apellidos Los apellidos completos del usuario.
     * @param email El correo electrónico del usuario. Debe ser único.
     * @param clave La contraseña para el acceso del usuario.
     * @param fecha_nacimiento La fecha de nacimiento del usuario. Puede ser {@code null}.
     * @param genero El género del usuario (e.g., "Masculino", "Femenino", "Otro"). Puede ser {@code null}.
     * @param distrito_residencia El distrito donde reside el usuario. Puede ser {@code null}.
     * @param rol El rol asignado al nuevo usuario (e.g., "Encuestado", "Administrador"). No debe ser {@code null}.
     * @return {@code true} si el usuario se registró exitosamente, {@code false} en caso contrario (p.ej., email o DNI ya existen, error de base de datos).
     */
    public boolean registrarNuevoUsuario(String dni, String nombres, String apellidos, String email, String clave,
                                         LocalDate fecha_nacimiento, String genero, String distrito_residencia, String rol) {

        // Validaciones de campos obligatorios
        if (dni == null || dni.trim().isEmpty()) {
            System.err.println("Error de registro: El DNI no puede estar vacío.");
            return false;
        }
        if (nombres == null || nombres.trim().isEmpty()) {
            System.err.println("Error de registro: Los nombres no pueden estar vacíos.");
            return false;
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            System.err.println("Error de registro: Los apellidos no pueden estar vacíos.");
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Error de registro: El email no puede estar vacío.");
            return false;
        }
        // Email pattern validation removed as per REQ
        if (clave == null || clave.isEmpty()) {
            System.err.println("Error de registro: La contraseña no puede estar vacía.");
            return false;
        }
        if (rol == null || rol.trim().isEmpty()) {
            System.err.println("Error de registro: El rol no puede estar vacío.");
            return false;
        }

        // Aquí se podría añadir una verificación de existencia de email/DNI
        // llamando a usuarioDAO.obtenerUsuarioPorEmail() o similar si es necesario
        // antes de intentar la creación, aunque el DAO también debería manejarlo.

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(dni.trim());
        nuevoUsuario.setNombres(nombres.trim());
        nuevoUsuario.setApellidos(apellidos.trim());
        nuevoUsuario.setEmail(email.trim());
        nuevoUsuario.setClave(clave); // Se asume que el hashing, si es necesario, se maneja en otro lugar o no se usa.

        nuevoUsuario.setFecha_nacimiento(fecha_nacimiento); // Puede ser null
        nuevoUsuario.setGenero(genero != null ? genero.trim() : null); // Puede ser null
        nuevoUsuario.setDistrito_residencia(distrito_residencia != null ? distrito_residencia.trim() : null); // Puede ser null
        nuevoUsuario.setRol(rol.trim());

        // fecha_registro es usualmente manejada por la base de datos (e.g., DEFAULT CURRENT_TIMESTAMP)
        // o se establece en la capa DAO al momento de la inserción.

        boolean registrado = usuarioDAO.crearUsuario(nuevoUsuario); // Cambio de registrarUsuario a crearUsuario

        if (registrado) {
            System.out.println("Servicio: Usuario '" + email.trim() + "' registrado exitosamente.");
        } else {
            System.err.println("Servicio: Falló el registro del usuario '" + email.trim() + "'. Verifique logs del DAO (p.ej., email/DNI duplicado o error de BD).");
        }
        return registrado;
    }

    public Usuario obtenerUsuarioPorId(int idUsuario) {
        // System.out.println("Servicio: obtenerUsuarioPorId(" + idUsuario + ")"); // Log opcional
        return usuarioDAO.obtenerUsuarioPorId(idUsuario); // Asume que el método existe en UsuarioDAO
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Servicio: Email inválido para búsqueda.");
            return null;
        }
        // System.out.println("Servicio: Buscando usuario por email: " + email); // Log opcional
        return usuarioDAO.obtenerUsuarioPorEmail(email.trim());
    }

    public boolean actualizarPerfilUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId_usuario() <= 0) { // Corregido a getId_usuario()
            System.err.println("Servicio: Datos de usuario inválidos o ID de usuario faltante para actualizar.");
            return false;
        }
        // System.out.println("Servicio: Intentando actualizar perfil para usuario ID: " + usuario.getId_usuario()); // Log opcional
        return usuarioDAO.actualizarUsuario(usuario); // Asume que el método existe y se llama actualizarUsuario
    }

    public boolean cambiarEstadoUsuario(int idUsuario, String nuevoEstado) {
        // Esta funcionalidad requiere una definición más clara de cómo se maneja el "estado" en el modelo Usuario
        // y qué operaciones soporta el DAO. La implementación original tenía 'cambiarEstadoCuenta'.
        // Si Usuario tiene un campo 'estado', se debería obtener el usuario, setear el estado y actualizar.
        System.err.println("Servicio: cambiarEstadoUsuario no está completamente implementado o requiere clarificación de la estructura de 'estado' y DAO.");
        // Ejemplo conceptual si Usuario tiene setEstado() y DAO tiene actualizarUsuario():
        // Usuario u = usuarioDAO.obtenerUsuarioPorId(idUsuario);
        // if (u != null && nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
        //     u.setEstado(nuevoEstado.trim()); // Suponiendo que existe u.setEstado()
        //     return usuarioDAO.actualizarUsuario(u);
        // }
        return false; // Placeholder
    }
}