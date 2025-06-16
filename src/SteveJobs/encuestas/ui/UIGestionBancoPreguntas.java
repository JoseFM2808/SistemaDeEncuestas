package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.servicio.ServicioPreguntas;
// Importar ServicioUsuarios si se necesita para algo más que el ID.
// import SteveJobs.encuestas.servicio.ServicioUsuarios;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.sql.Timestamp;
import java.util.List;

public class UIGestionBancoPreguntas {

    static ServicioPreguntas servicioPreguntas = new ServicioPreguntas();
    // static ServicioUsuarios servicioUsuarios; // Descomentar si se necesita más del usuario

    /**
     * Muestra el menú principal para la gestión del banco de preguntas.
     * Permite al administrador registrar nuevas preguntas o ver las existentes.
     * @param admin El objeto Usuario del administrador actualmente logueado.
     */
    public static void mostrarMenu(Usuario admin) {
        if (admin == null) {
            JOptionPane.showMessageDialog(null, "Error: Se requiere un usuario administrador para acceder a esta función.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] opciones = {"Registrar Nueva Pregunta", "Ver Banco de Preguntas", "Volver al Menú Principal"};
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

            if (seleccion == null) { // El usuario cerró el diálogo o presionó Cancelar
                return; // Salir del método, volviendo al menú anterior implícitamente.
            }

            switch (seleccion) {
                case "Registrar Nueva Pregunta":
                    registrarPreguntaUI(admin);
                    break;
                case "Ver Banco de Preguntas":
                    verBancoPreguntasUI();
                    break;
                case "Volver al Menú Principal":
                    // No hacer nada, el bucle terminará y volverá al menú anterior
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } while (!"Volver al Menú Principal".equals(seleccion) && seleccion != null);
    }

    /**
     * Interfaz para registrar una nueva pregunta en el banco.
     * Solicita los datos necesarios al administrador.
     * @param admin El administrador que registra la pregunta.
     */
    private static void registrarPreguntaUI(Usuario admin) {
        try {
            String textoPregunta = JOptionPane.showInputDialog(null, "Ingrese el texto de la pregunta:", "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE);
            if (textoPregunta == null || textoPregunta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El texto de la pregunta no puede estar vacío.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idTipoPreguntaStr = JOptionPane.showInputDialog(null, "Ingrese el ID del Tipo de Pregunta (numérico):", "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE);
            if (idTipoPreguntaStr == null) return; // Cancelado
            int idTipoPregunta = Integer.parseInt(idTipoPreguntaStr);

            String idClasificacionPreguntaStr = JOptionPane.showInputDialog(null, "Ingrese el ID de Clasificación (numérico, 0 o vacío si no aplica):", "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE);
            Integer idClasificacionPregunta = null;
            if (idClasificacionPreguntaStr != null && !idClasificacionPreguntaStr.trim().isEmpty() && !idClasificacionPreguntaStr.trim().equals("0")) {
                idClasificacionPregunta = Integer.parseInt(idClasificacionPreguntaStr);
            }

            String[] estadosPosibles = {"Activa", "Inactiva", "Borrador"}; // Ejemplos de estados
            String estado = (String) JOptionPane.showInputDialog(
                    null, "Seleccione el estado de la pregunta:",
                    "Registrar Pregunta", JOptionPane.QUESTION_MESSAGE, null,
                    estadosPosibles, estadosPosibles[0]);
            if (estado == null) return; // Cancelado

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
            e.printStackTrace(); // Para depuración
        }
    }

    /**
     * Interfaz para mostrar todas las preguntas del banco.
     */
    private static void verBancoPreguntasUI() {
        List<PreguntaBanco> preguntas = servicioPreguntas.obtenerTodasLasPreguntasDelBanco();

        if (preguntas == null || preguntas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay preguntas registradas en el banco.", "Banco de Preguntas Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-50s | %-10s | %-15s | %-10s | %-15s | %-15s | %-10s\n",
            "ID", "Texto de la Pregunta", "ID Tipo", "ID Clasif.", "Estado", "Fecha Creación", "Fecha Modif.", "ID Creador"));
        sb.append("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");

        for (PreguntaBanco p : preguntas) {
            sb.append(String.format("%-5d | %-50.50s | %-10d | %-15s | %-10s | %-19s | %-19s | %-10s\n",
                    p.getIdPreguntaBanco(),
                    p.getTextoPregunta(),
                    p.getIdTipoPregunta(),
                    p.getIdClasificacion() == null ? "N/A" : p.getIdClasificacion().toString(),
                    p.getEstado(),
                    p.getFechaCreacion() != null ? p.getFechaCreacion().toString().substring(0,19) : "N/A", // Formato YYYY-MM-DD HH:MM:SS
                    p.getFechaModificacion() != null ? p.getFechaModificacion().toString().substring(0,19) : "N/A",
                    p.getIdUsuarioCreador() == null ? "N/A" : p.getIdUsuarioCreador().toString()
            ));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(false); // Para que el scroll horizontal funcione si las líneas son largas
        textArea.setWrapStyleWord(false);

        // Usar una fuente monoespaciada para mejor alineación de columnas
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));


        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(800, 400)); // Ajustar tamaño según necesidad

        JOptionPane.showMessageDialog(null, scrollPane, "Banco de Preguntas", JOptionPane.INFORMATION_MESSAGE);
    }
}
