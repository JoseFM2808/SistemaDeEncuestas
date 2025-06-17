/*
 * Autores del Módulo:
 * - José Flores
 * - Asistente de AED (Refactorización)
 *
 * Responsabilidad Principal:
 * - Menú principal para el rol Administrador.
 */
package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.util.PilaNavegacion;
import javax.swing.JOptionPane;

public class UIMenuAdministrador {

    public static void mostrarMenu(Usuario admin) {
        boolean salirMenuAdmin = false;
        while (!salirMenuAdmin) {
            String[] opcionesAdmin = {
                "Gestionar Preguntas de Registro",
                "Gestionar Encuestas",
                "Gestionar Banco de Preguntas",
                "Ver Resultados de Encuestas",
                "Gestionar Usuarios", // Funcionalidad pendiente
                "Gestionar Tipos de Pregunta", // Funcionalidad pendiente
                "Gestionar Clasificaciones de Pregunta", // Funcionalidad pendiente
                "Salir (Cerrar Sesión)"
            };

            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Menú Administrador - Bienvenido " + admin.getNombres(),
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

            // Usamos la Pila de Navegación antes de entrar a un submenú
            if (PilaNavegacion.instance != null) {
                PilaNavegacion.instance.push("MenuAdmin");
            }

            switch (seleccion) {
                case "Gestionar Preguntas de Registro":
                    UIGestionPreguntasRegistro.mostrarMenu();
                    break;
                case "Gestionar Encuestas":
                    // Asume que UIGestionEncuestas tiene un método estático mostrarMenu(admin)
                    UIGestionEncuestas.mostrarMenu(admin);
                    break;
                case "Gestionar Banco de Preguntas":
                    // Asume que existe UIGestionBancoPreguntas con este método
                    // UIGestionBancoPreguntas.mostrarMenu(); // Descomentar cuando la clase exista y esté lista
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Gestionar Banco de Preguntas' pendiente.");
                    break;
                case "Ver Resultados de Encuestas":
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Ver Resultados de Encuestas' pendiente.");
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
                default:
                    // Si la opción no es válida, no debería haber un push en la pila, así que lo sacamos.
                     if (PilaNavegacion.instance != null && !PilaNavegacion.instance.isEmpty()) {
                        PilaNavegacion.instance.pop();
                    }
                    JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
}
