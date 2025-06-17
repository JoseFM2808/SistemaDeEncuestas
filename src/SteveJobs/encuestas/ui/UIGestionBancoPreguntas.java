package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.servicio.ServicioPreguntas;
import SteveJobs.encuestas.util.PilaNavegacion; // Importar PilaNavegacion

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.sql.Timestamp;
import java.util.List;

/*
 * Autores del Módulo:
 * - Pablo Alegre
 * (Note: Original assignment was Pablo Alegre, if different now, adjust)
 *
 * Responsabilidad Principal:
 * - UI para gestión del banco de preguntas
 */
public class UIGestionBancoPreguntas {

    static ServicioPreguntas servicioPreguntas = new ServicioPreguntas();
    // admin user passed as parameter, so ServicioUsuarios might not be needed here directly

    /**
     * Muestra el menú principal para la gestión del banco de preguntas.
     * Permite al administrador registrar nuevas preguntas o ver las existentes.
     * @param admin El objeto Usuario del administrador actualmente logueado.
     */
    public static void mostrarMenu(Usuario admin) {
        if (admin == null) {
            JOptionPane.showMessageDialog(null, "Error: Se requiere un usuario administrador.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] opciones = {"Registrar Nueva Pregunta", "Ver Banco de Preguntas", "Volver al Menú Administrador"};
        String seleccion;

        do {
            seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Seleccione una opción para el Banco de Preguntas:",
                    "Gestión Banco de Preguntas",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) { // Usuario cerró el diálogo
                // To ensure stack consistency if dialog is closed, treat as "Volver"
                if (!PilaNavegacion.instance.isEmpty()) {
                    PilaNavegacion.instance.pop();
                }
                return;
            }

            switch (seleccion) {
                case "Registrar Nueva Pregunta":
                    // Before calling a sub-action that might have its own "Volver" that expects this menu's state on stack:
                    // PilaNavegacion.instance.push("UIGestionBancoPreguntas -> registrarPreguntaUI");
                    registrarPreguntaUI(admin);
                    // If registrarPreguntaUI had its own complex navigation and popped, the above push would be correct.
                    // For simple JOptionPanes, usually not needed to push before each sub-action within a menu.
                    break;
                case "Ver Banco de Preguntas":
                    verBancoPreguntasUI();
                    break;
                case "Volver al Menú Administrador":
                    if (!PilaNavegacion.instance.isEmpty()) {
                        PilaNavegacion.instance.pop(); // Pop al volver
                    }
                    // salir del bucle, no hacer nada más
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } while (!opciones[2].equals(seleccion) && seleccion != null); // Salir si se selecciona "Volver"
    }

    private static void registrarPreguntaUI(Usuario admin) {
        try {
            String textoPregunta = JOptionPane.showInputDialog(null, "Ingrese el texto de la pregunta:", "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE);
            if (textoPregunta == null || textoPregunta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El texto de la pregunta no puede estar vacío.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idTipoPreguntaStr = JOptionPane.showInputDialog(null, "Ingrese el ID del Tipo de Pregunta (numérico):", "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE);
            if (idTipoPreguntaStr == null) return;
            int idTipoPregunta = Integer.parseInt(idTipoPreguntaStr);

            String idClasificacionPreguntaStr = JOptionPane.showInputDialog(null, "Ingrese el ID de Clasificación (numérico, 0 o vacío si no aplica):", "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE);
            Integer idClasificacionPregunta = null;
            if (idClasificacionPreguntaStr != null && !idClasificacionPreguntaStr.trim().isEmpty() && !idClasificacionPreguntaStr.trim().equals("0")) {
                idClasificacionPregunta = Integer.parseInt(idClasificacionPreguntaStr);
            }

            String[] estadosPosibles = {"Activa", "Inactiva", "Borrador"};
            String estado = (String) JOptionPane.showInputDialog(
                    null, "Seleccione el estado de la pregunta:",
                    "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE, null,
                    estadosPosibles, estadosPosibles[0]);
            if (estado == null) return;

            PreguntaBanco nuevaPregunta = new PreguntaBanco();
            nuevaPregunta.setTextoPregunta(textoPregunta.trim());
            nuevaPregunta.setIdTipoPregunta(idTipoPregunta);
            nuevaPregunta.setIdClasificacion(idClasificacionPregunta);
            nuevaPregunta.setEstado(estado);

            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            nuevaPregunta.setFechaCreacion(ahora);
            nuevaPregunta.setFechaModificacion(ahora);
            nuevaPregunta.setIdUsuarioCreador(admin.getId_usuario());

            boolean exito = servicioPreguntas.registrarPreguntaEnBanco(nuevaPregunta);

            if (exito) {
                JOptionPane.showMessageDialog(null, "Pregunta registrada exitosamente en el banco (ID: " + nuevaPregunta.getIdPreguntaBanco() + ").", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar la pregunta en el banco.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en el formato numérico: " + e.getMessage(), "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private static void verBancoPreguntasUI() {
        List<PreguntaBanco> preguntas = servicioPreguntas.obtenerTodasLasPreguntasDelBanco();

        if (preguntas == null || preguntas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay preguntas registradas en el banco.", "Banco de Preguntas Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-50s | %-10s | %-15s | %-10s\n",
            "ID", "Texto de la Pregunta", "ID Tipo", "ID Clasif.", "Estado"));
        sb.append("-------------------------------------------------------------------------------------------\n");

        for (PreguntaBanco p : preguntas) {
            sb.append(String.format("%-5d | %-50.50s | %-10d | %-15s | %-10s\n",
                    p.getIdPreguntaBanco(),
                    p.getTextoPregunta(),
                    p.getIdTipoPregunta(),
                    p.getIdClasificacion() == null ? "N/A" : p.getIdClasificacion().toString(),
                    p.getEstado()
            ));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(700, 350));

        JOptionPane.showMessageDialog(null, scrollPane, "Banco de Preguntas", JOptionPane.INFORMATION_MESSAGE);
    }
}
