/*
Autor: Gian Fri

*/
package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.dao.UsuarioDAO;

public class ServicioAutenticacion {

    private UsuarioDAO usuarioDAO;


    public ServicioAutenticacion() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Autentica a un usuario basado en su email y contraseña.
     *
     * Este método verifica que el email y la contraseña no estén vacíos.
     * Luego, llama al método {@code validarUsuario} del {@link UsuarioDAO}
     * para comprobar las credenciales contra la base de datos.
     *
     * @param email El correo electrónico del usuario que intenta iniciar sesión.
     *              No debe ser {@code null} ni estar vacío.
     * @param password La contraseña proporcionada por el usuario.
     *                 No debe ser {@code null} ni estar vacía.
     * @return El objeto {@link Usuario} si la autenticación es exitosa (email y contraseña coinciden),
     *         o {@code null} si la autenticación falla (email no encontrado, contraseña incorrecta,
     *         o parámetros de entrada inválidos).
     */
    public Usuario autenticar(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            System.err.println("Error de autenticación: El email no puede estar vacío.");
            return null;
        }
        if (password == null || password.isEmpty()) {

            System.err.println("Error de autenticación: El password no puede estar vacío.");
            return null;
        }


        System.out.println("Intentando autenticar al usuario: " + email);
        Usuario usuarioAutenticado = usuarioDAO.validarUsuario(email, password);

        if (usuarioAutenticado != null) {
            System.out.println("Autenticación exitosa para: " + email);
        } else {
            System.out.println("Autenticación fallida para: " + email);
        }
        return usuarioAutenticado;
    }
}