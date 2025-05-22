/*
Autor: Gian Fri

*/

package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.servicio.ServicioAutenticacion;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class UIAutenticacion {

    public static Usuario mostrarLogin() {
        String email = null;
        String password = null;

        email = JOptionPane.showInputDialog(null, "Email:", "Inicio de Sesión", JOptionPane.PLAIN_MESSAGE);

        if (email == null) {
            return null;
        }

        JPasswordField pf = new JPasswordField();
        Object[] Lpan = {"Contraseña:" ,pf};
        int option = JOptionPane.showConfirmDialog(null, Lpan, "Inicio de Sesión", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            password = new String(pf.getPassword());
        } else {
            return null;
        }

        if (email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo Email no puede estar vacío.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo Contraseña no puede estar vacío.", "Error de Entrada", JOptionPane.ERROR_MESSAGE); 
            return null;
        }

        ServicioAutenticacion servicioAuth = new ServicioAutenticacion();

        Usuario usuarioAutenticado = servicioAuth.autenticar(email.trim(), password);

        if (usuarioAutenticado != null) {
            JOptionPane.showMessageDialog(null,
                    "Login exitoso. Bienvenido " + usuarioAutenticado.getNombresApellidos() + "!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            return usuarioAutenticado;
        } else {

            JOptionPane.showMessageDialog(null,
                    "Email o contraseña incorrectos, o la cuenta podría estar inactiva.",
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(UIAutenticacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        System.out.println("Mostrando diálogo de login...");
        Usuario usuario = mostrarLogin();

        if (usuario != null) {
            System.out.println("Usuario autenticado: " + usuario.getEmail());
            System.out.println("ID: " + usuario.getIdUsuario());
            System.out.println("Tipo Nivel: " + usuario.getTipoNivel());
        } else {
            System.out.println("El login fue cancelado o falló.");
        }

        System.exit(0);
    }
}