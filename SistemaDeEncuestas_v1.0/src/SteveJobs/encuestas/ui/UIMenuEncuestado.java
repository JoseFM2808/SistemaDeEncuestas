/*
Pendiente..
 */
package SteveJobs.encuestas.ui;
import SteveJobs.encuestas.modelo.Usuario;
import javax.swing.JOptionPane;


public class UIMenuEncuestado {

    public static void mostrarMenu(Usuario encuestado) {
        JOptionPane.showMessageDialog(null, "Bienvenido al Portal del Encuestado, " + encuestado.getNombresApellidos() + "!\n(Funcionalidades pendientes de implementación)", "Portal Encuestado", JOptionPane.INFORMATION_MESSAGE);
    }
    
}
