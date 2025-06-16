/*
Autor: José Flores

*/
package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Usuario;
import javax.swing.JOptionPane;

public class UIMenuAdministrador {

    public static void mostrarMenu(Usuario admin) {
        boolean salirMenuAdmin = false;
        while (!salirMenuAdmin) {
            String[] opcionesAdmin = {
                "Gestionar Preguntas de Registro",
                "Gestionar Usuarios",
                "Gestionar Tipos de Pregunta",
                "Gestionar Clasificaciones de Pregunta",
                "Gestionar Banco de Preguntas",
                "Gestionar Encuestas",
                "Ver Resultados de Encuestas",
                "Salir (Volver al Menú Principal)"
            };

            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Menú Administrador - Bienvenido " + admin.getNombresApellidos(),
                    "Panel de Administración",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcionesAdmin,
                    opcionesAdmin[0]
            );

            if (seleccion == null || seleccion.equals(opcionesAdmin[7])) {
                salirMenuAdmin = true;
                continue;
            }

            switch (seleccion) {
                case "Gestionar Preguntas de Registro":
                    UIGestionPreguntasRegistro.mostrarMenu();
                    break;
                case "Gestionar Usuarios":
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Gestionar Usuarios' pendiente.");
                    break;
                case "Gestionar Tipos de Pregunta":
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Gestionar Tipos de Pregunta' pendiente.");
                    break;
                case "Gestionar Clasificaciones de Pregunta":
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Gestionar Clasificaciones de Pregunta' pendiente.");
                    break;
                case "Gestionar Banco de Preguntas":
                    UIGestionBancoPreguntas.mostrarMenu(admin); // Llamada a la nueva UI
                    break;
                case "Gestionar Encuestas":
                    UIGestionEncuestas.mostrarMenu(admin); // Llamada a la UI de Gestión de Encuestas
                    break;
                case "Ver Resultados de Encuestas":
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Ver Resultados de Encuestas' pendiente.");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
}
