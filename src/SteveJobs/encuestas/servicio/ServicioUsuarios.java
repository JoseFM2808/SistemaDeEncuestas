/*
Autor: Gian Fri

*/

package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.dao.UsuarioDAO;

import java.util.regex.Pattern;

public class ServicioUsuarios {

    private UsuarioDAO usuarioDAO;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    public ServicioUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean registrarNuevoUsuario(String docId, String nombres, String email, String password, String tipoNivel) {
        if (docId == null || docId.trim().isEmpty()) {
            System.err.println("Error de registro: El documento de identidad no puede estar vacío.");
            return false;
        }
        if (nombres == null || nombres.trim().isEmpty()) {
            System.err.println("Error de registro: Los nombres y apellidos no pueden estar vacíos.");
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Error de registro: El email no puede estar vacío.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            System.err.println("Error de registro: El formato del email es inválido.");
            return false;
        }
        if (password == null || password.isEmpty()) {
            System.err.println("Error de registro: La contraseña no puede estar vacía.");
            return false;
        }

        if (tipoNivel == null || tipoNivel.trim().isEmpty()) {
            System.err.println("Error de registro: El tipo de nivel no puede estar vacío.");
            return false;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDocumentoIdentidad(docId.trim());
        nuevoUsuario.setNombresApellidos(nombres.trim());
        nuevoUsuario.setEmail(email.trim());
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setTipoNivel(tipoNivel.trim());

        boolean registrado = usuarioDAO.registrarUsuario(nuevoUsuario);

        if (registrado) {
            System.out.println("Servicio: Usuario '" + email + "' registrado exitosamente.");
        } else {
            System.err.println("Servicio: Falló el registro del usuario '" + email + "'. Verifique logs del DAO.");

        }
        return registrado;
    }

    public Usuario obtenerUsuarioPorId(int idUsuario) {
        System.out.println("Servicio: obtenerUsuarioPorId(" + idUsuario + ") no implementado completamente.");

        return null;
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Servicio: Email inválido para búsqueda.");
            return null;
        }
        System.out.println("Servicio: Buscando usuario por email: " + email);
        return usuarioDAO.obtenerUsuarioPorEmail(email.trim());
    }

    public boolean actualizarPerfilUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0) {
            System.err.println("Servicio: Datos de usuario inválidos o ID de usuario faltante para actualizar.");
            return false;
        }

        System.out.println("Servicio: Intentando actualizar perfil para usuario ID: " + usuario.getIdUsuario());
        return usuarioDAO.actualizarPerfilUsuario(usuario);
    }

    public boolean cambiarEstadoUsuario(int idUsuario, String nuevoEstado) {
        if (idUsuario <= 0 || nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            System.err.println("Servicio: ID de usuario o nuevo estado inválido.");
            return false;
        }
        System.out.println("Servicio: Intentando cambiar estado para usuario ID: " + idUsuario + " a " + nuevoEstado);
        return usuarioDAO.cambiarEstadoCuenta(idUsuario, nuevoEstado.trim());
    }

}