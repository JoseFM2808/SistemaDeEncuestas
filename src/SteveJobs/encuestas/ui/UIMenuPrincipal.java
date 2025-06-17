package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.modelo.DatosMenuPrincipal; // Asumiendo que esta clase existe y tiene getMensajeBienvenida(), isEsAdministrador(), getNumeroEncuestasPendientes()
import SteveJobs.encuestas.servicio.ServicioEncuestas;
import SteveJobs.encuestas.servicio.ServicioUI; // Asumiendo que ServicioUI existe y tiene obtenerDatosMenuPrincipal()
import SteveJobs.encuestas.servicio.ServicioUsuarios;
import SteveJobs.encuestas.servicio.ServicioAutenticacion; // Para el flujo de login

import javax.swing.JOptionPane;

public class UIMenuPrincipal {

    private static ServicioUsuarios servicioUsuarios = new ServicioUsuarios();
    private static ServicioEncuestas servicioEncuestas = new ServicioEncuestas();
    private static ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private static ServicioUI servicioUI = new ServicioUI(servicioUsuarios, servicioEncuestas); // Asumiendo constructor de ServicioUI

    private Usuario usuarioActual;

    public UIMenuPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            if (usuarioActual == null) { // No hay usuario logueado
                String[] opcionesInvitado = {"Iniciar Sesión", "Registrarse", "Salir del Sistema"};
                int seleccion = JOptionPane.showOptionDialog(null, "Bienvenido al Sistema de Encuestas SteveJobs.\nPor favor, inicie sesión o regístrese.",
                        "Menú Principal", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opcionesInvitado, opcionesInvitado[0]);

                switch (seleccion) {
                    case 0: // Iniciar Sesión
                        Usuario u = UIAutenticacion.mostrarFormularioAutenticacion();
                        if (u != null) {
                            this.usuarioActual = u;
                            // Cargar el menú específico del rol después del login exitoso
                            if ("Administrador".equalsIgnoreCase(usuarioActual.getRol())) {
                                UIMenuAdministrador.mostrarMenu(usuarioActual);
                            } else if ("Encuestado".equalsIgnoreCase(usuarioActual.getRol())) {
                                UIMenuEncuestado.mostrarMenu(usuarioActual);
                            } else {
                                JOptionPane.showMessageDialog(null, "Rol de usuario no reconocido: " + usuarioActual.getRol(), "Error de Rol", JOptionPane.ERROR_MESSAGE);
                                this.usuarioActual = null; // Desloguear si el rol no es válido
                            }
                        }
                        break;
                    case 1: // Registrarse
                        UIRegistroUsuario.mostrarFormularioRegistro();
                        // Después del registro, el usuario debería poder iniciar sesión.
                        break;
                    case 2: // Salir del Sistema
                    default: // También cubre el cierre del diálogo
                        salir = true;
                        break;
                }
            } else { // Usuario ya logueado
                 // Si ya hay un usuario logueado (quizás de una sesión anterior o directo)
                 // Redirigir directamente a su menú específico.
                 // Esta lógica es un poco redundante si el login siempre redirige.
                 // Pero es útil si UIMenuPrincipal se instancia con un usuario ya logueado.
                if ("Administrador".equalsIgnoreCase(usuarioActual.getRol())) {
                    UIMenuAdministrador.mostrarMenu(usuarioActual);
                } else if ("Encuestado".equalsIgnoreCase(usuarioActual.getRol())) {
                    UIMenuEncuestado.mostrarMenu(usuarioActual);
                } else {
                     JOptionPane.showMessageDialog(null, "Rol de usuario no reconocido: " + usuarioActual.getRol() + ". Saliendo.", "Error de Rol", JOptionPane.ERROR_MESSAGE);
                }
                // Después de volver de los menús específicos, el usuario se considera "deslogueado" de ese submenú.
                // Para un verdadero logout, this.usuarioActual debería ser null.
                // Por ahora, simplemente salimos del bucle principal.
                String[] opcionesLogout = {"Cerrar Sesión", "Salir del Sistema"};
                int seleccionLogout = JOptionPane.showOptionDialog(null, "¿Desea cerrar sesión o salir del sistema?",
                        "Confirmar Salida", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcionesLogout, opcionesLogout[0]);
                if(seleccionLogout == 0) { // Cerrar Sesión
                    this.usuarioActual = null; // Efectivamente cierra la sesión
                } else { // Salir del Sistema o cerró dialogo
                    salir = true;
                }
            }
        }
        System.out.println("Saliendo del sistema de encuestas SteveJobs.");
    }


    public static void main(String[] args) {
        // Este main es para demostración y podría ser diferente al SistemaEncuestasApp.java
        // Para probar directamente, se podría simular un login o pasar un usuario.

        // Caso 1: Iniciar sin usuario (flujo normal)
        UIMenuPrincipal menu = new UIMenuPrincipal(null);
        menu.mostrar();

        // Caso 2: (Para prueba directa de roles, si se quisiera saltar el login UI)
        // Usuario adminTest = new Usuario();
        // adminTest.setId_usuario(1);
        // adminTest.setNombres("AdminTest");
        // adminTest.setRol("Administrador");
        // UIMenuPrincipal menuAdmin = new UIMenuPrincipal(adminTest);
        // menuAdmin.mostrar();

        // Usuario encuestadoTest = new Usuario();
        // encuestadoTest.setId_usuario(2);
        // encuestadoTest.setNombres("EncuestadoTest");
        // encuestadoTest.setRol("Encuestado");
        // UIMenuPrincipal menuEnc = new UIMenuPrincipal(encuestadoTest);
        // menuEnc.mostrar();
    }
}
