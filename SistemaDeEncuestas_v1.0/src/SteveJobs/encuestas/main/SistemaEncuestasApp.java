/*
Autor: José Flores

*/
package SteveJobs.encuestas.main;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.ui.UIAutenticacion;
import SteveJobs.encuestas.ui.UIMenuAdministrador;
import SteveJobs.encuestas.ui.UIMenuEncuestado;
import SteveJobs.encuestas.ui.UIRegistroUsuario;
import javax.swing.JOptionPane;

public class SistemaEncuestasApp {

    public static void main(String[] args) {


        mostrarMenuPrincipal();
    }

    public static void mostrarMenuPrincipal() {
        boolean salir = false;
        while (!salir) {
            String[] opciones = {"Iniciar Sesión", "Registrarse como Encuestado", "Salir"};
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Bienvenido al Sistema de Encuestas",
                    "Menú Principal",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            switch (seleccion) {
                case 0:
                    Usuario usuarioAutenticado = UIAutenticacion.mostrarLogin();
                    if (usuarioAutenticado != null) {
                        if ("Administrador".equalsIgnoreCase(usuarioAutenticado.getTipoNivel())) {
                            UIMenuAdministrador.mostrarMenu(usuarioAutenticado);
                        } else if ("Encuestado".equalsIgnoreCase(usuarioAutenticado.getTipoNivel())) { 
                            UIMenuEncuestado.mostrarMenu(usuarioAutenticado);
                        } else {
                             JOptionPane.showMessageDialog(null, "Tipo de usuario desconocido: " + usuarioAutenticado.getTipoNivel());
                        }
                    }
                    break;
                case 1:
                    UIRegistroUsuario.mostrarFormularioRegistro();
                    break;
                case 2:
                    salir = true;
                    JOptionPane.showMessageDialog(null, "Gracias por usar el sistema. ¡Hasta pronto!");
                    break;
                default:
                    if (seleccion == JOptionPane.CLOSED_OPTION) {
                        salir = true;
                        System.out.println("Sistema cerrado.");
                    }
                    break;
            }
        }
    }
}