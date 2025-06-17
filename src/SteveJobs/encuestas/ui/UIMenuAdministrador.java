/*
 * Autores del Módulo:
 * - José Flores
 *
 * Responsabilidad Principal:
 * - Menú principal para administradores
 */
package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.util.PilaNavegacion; // Importar PilaNavegacion
import javax.swing.JOptionPane;

public class UIMenuAdministrador {

    // No se necesita una instancia local de PilaNavegacion, se usará PilaNavegacion.instance

    public static void mostrarMenu(Usuario admin) {
        boolean salirMenuAdmin = false;
        // Asumiendo que UIMenuAdministrador es un punto de entrada al módulo admin,
        // no se pushea nada aquí inicialmente, o se pushea un estado base si este menú puede ser "regresado" desde otro lugar.
        // Para este ejercicio, los submenús se encargarán del push/pop.

        // Corrección: El UIMenuAdministrador SÍ debe pushear su estado ANTES de llamar a un submenú.
        // Y el submenú al VOLVER, popeará ESE estado.
        // El "Salir (Volver al Menú Principal)" de UIMenuAdministrador NO debe popear, sino simplemente salir de su bucle.

        while (!salirMenuAdmin) {
            // El título podría mostrar el tamaño de la pila para depuración:
            // String tituloPanel = "Panel de Administración (Pila: " + PilaNavegacion.instance.size() + ")";
            String bienvenido = admin.getNombres() != null && !admin.getNombres().isEmpty() ? admin.getNombres() : (admin.getApellidos() != null ? admin.getApellidos() : admin.getEmail());


            String[] opcionesAdmin = {
                "Gestionar Preguntas de Registro",      // Llama a UIGestionPreguntasRegistro
                "Gestionar Usuarios",                   // Pendiente
                "Gestionar Tipos de Pregunta",          // Pendiente
                "Gestionar Clasificaciones de Pregunta",// Pendiente
                "Gestionar Banco de Preguntas",         // Llama a UIGestionBancoPreguntas
                "Gestionar Encuestas",                  // Llama a UIGestionEncuestas
                "Ver Resultados de Encuestas",          // Pendiente
                "Salir (Volver al Menú Principal)"
            };

            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Menú Administrador - Bienvenido " + bienvenido, // Usar el nombre/email del admin
                    "Panel de Administración", // tituloPanel para debug
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcionesAdmin,
                    opcionesAdmin[0]
            );

            // El índice para "Salir" es opcionesAdmin.length - 1
            if (seleccion == null || seleccion.equals(opcionesAdmin[opcionesAdmin.length - 1])) {
                salirMenuAdmin = true; // Simplemente sale de este menú, no popea.
                                      // El pop lo maneja UIMenuPrincipal o el submenú que retorna aquí.
                continue;
            }

            switch (seleccion) {
                case "Gestionar Preguntas de Registro":
                    PilaNavegacion.instance.push("UIMenuAdministrador -> UIGestionPreguntasRegistro");
                    UIGestionPreguntasRegistro.mostrarMenu(); // Asumiendo que UIGestionPreguntasRegistro.mostrarMenu() fue actualizado para pop
                    break;
                case "Gestionar Usuarios":
                    // PilaNavegacion.instance.push("UIMenuAdministrador -> UIGestionUsuarios"); // Si se implementa
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Gestionar Usuarios' pendiente.");
                    break;
                case "Gestionar Tipos de Pregunta":
                    // PilaNavegacion.instance.push("UIMenuAdministrador -> UIGestionTiposPregunta");
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Gestionar Tipos de Pregunta' pendiente.");
                    break;
                case "Gestionar Clasificaciones de Pregunta":
                    // PilaNavegacion.instance.push("UIMenuAdministrador -> UIGestionClasificaciones");
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Gestionar Clasificaciones de Pregunta' pendiente.");
                    break;
                case "Gestionar Banco de Preguntas":
                    PilaNavegacion.instance.push("UIMenuAdministrador -> UIGestionBancoPreguntas");
                    UIGestionBancoPreguntas.mostrarMenu(admin); // Asumiendo que UIGestionBancoPreguntas.mostrarMenu() fue actualizado para pop
                    break;
                case "Gestionar Encuestas":
                    PilaNavegacion.instance.push("UIMenuAdministrador -> UIGestionEncuestas");
                    UIGestionEncuestas.mostrarMenu(admin); // Asumiendo que UIGestionEncuestas.mostrarMenu() fue actualizado para pop
                    break;
                case "Ver Resultados de Encuestas":
                    // PilaNavegacion.instance.push("UIMenuAdministrador -> UIResultadosEncuestas");
                    JOptionPane.showMessageDialog(null, "Funcionalidad 'Ver Resultados de Encuestas' pendiente.");
                    // Si UIResultadosEncuestas.mostrarMenu(admin) existiera, se llamaría aquí.
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
}
