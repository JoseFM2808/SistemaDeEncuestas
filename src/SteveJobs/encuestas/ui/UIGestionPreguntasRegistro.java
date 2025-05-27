/*
Autor: José Flores

*/

package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.PreguntaRegistro;
import SteveJobs.encuestas.servicio.ServicioConfiguracionAdmin;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.Scanner;

public class UIGestionPreguntasRegistro {

    private static ServicioConfiguracionAdmin servicioConfig = new ServicioConfiguracionAdmin();
    private static Scanner lectorConsola = new Scanner(System.in); // Para pausas

    public static void mostrarMenu() {
        boolean salir = false;
        while (!salir) {
            String[] opciones = {
                "Agregar Nueva Pregunta de Registro",
                "Listar Preguntas de Registro (Consola)",
                "Modificar Pregunta de Registro",
                "Eliminar Pregunta de Registro",
                "Volver al Menú Administrador"
            };
            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Gestión de Preguntas de Registro para Perfil de Usuario",
                    "Admin: Preguntas de Registro",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) {
                salir = true;
                continue;
            }

            switch (seleccion) {
                case "Agregar Nueva Pregunta de Registro":
                    agregarPreguntaUI();
                    break;
                case "Listar Preguntas de Registro (Consola)":
                    listarPreguntasUIConsole();
                    break;
                case "Modificar Pregunta de Registro":
                    modificarPreguntaUI();
                    break;
                case "Eliminar Pregunta de Registro":
                    eliminarPreguntaUI();
                    break;
                case "Volver al Menú Administrador":
                    salir = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }

    private static void agregarPreguntaUI() {
        String texto = JOptionPane.showInputDialog(null, "Texto de la pregunta:", "Agregar Pregunta", JOptionPane.PLAIN_MESSAGE);
        if (texto == null || texto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El texto no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] tiposRespuesta = {"TEXTO_CORTO", "NUMERO", "FECHA", "SELECCION_UNICA_RADIO", "SELECCION_UNICA_COMBO"};
        String tipoRespuesta = (String) JOptionPane.showInputDialog(null, "Tipo de respuesta:", "Agregar Pregunta",
                JOptionPane.QUESTION_MESSAGE, null, tiposRespuesta, tiposRespuesta[0]);
        if (tipoRespuesta == null) return;

        String opcionesPosibles = null;
        if (tipoRespuesta.startsWith("SELECCION_UNICA")) {
            opcionesPosibles = JOptionPane.showInputDialog(null, "Opciones posibles (separadas por ';'):", "Agregar Pregunta", JOptionPane.PLAIN_MESSAGE);
            if (opcionesPosibles == null) opcionesPosibles = "";
        }

        int obligatoriaOption = JOptionPane.showConfirmDialog(null, "¿Es obligatoria?", "Agregar Pregunta", JOptionPane.YES_NO_OPTION);
        boolean esObligatoria = (obligatoriaOption == JOptionPane.YES_OPTION);

        String ordenStr = JOptionPane.showInputDialog(null, "Orden de visualización (número):", "Agregar Pregunta", JOptionPane.PLAIN_MESSAGE);
        int ordenVisualizacion = 0;
        try {
            if (ordenStr != null && !ordenStr.trim().isEmpty()) {
                ordenVisualizacion = Integer.parseInt(ordenStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Orden de visualización debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (servicioConfig.crearNuevaPreguntaRegistro(texto.trim(), tipoRespuesta, opcionesPosibles, esObligatoria, ordenVisualizacion)) {
            JOptionPane.showMessageDialog(null, "Pregunta de registro agregada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al agregar la pregunta de registro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void listarPreguntasUIConsole() {
        List<PreguntaRegistro> preguntas = servicioConfig.listarPreguntasRegistro();
        if (preguntas.isEmpty()) {
            System.out.println("\n------------------------------------");
            System.out.println("No hay preguntas de registro definidas.");
            System.out.println("------------------------------------");
            JOptionPane.showMessageDialog(null, "No hay preguntas de registro definidas (ver consola para detalles).", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        System.out.println("\n--- Preguntas de Registro Existentes ---");
        for (PreguntaRegistro pr : preguntas) {
            System.out.println("ID: " + pr.getIdPreguntaRegistro());
            System.out.println("  Texto: " + pr.getTextoPregunta());
            System.out.println("  Tipo: " + pr.getTipoRespuesta());
            if (pr.getOpcionesPosibles() != null && !pr.getOpcionesPosibles().isEmpty()) {
                System.out.println("  Opciones: " + pr.getOpcionesPosibles());
            }
            System.out.println("  Obligatoria: " + (pr.isEsObligatoria() ? "Sí" : "No"));
            System.out.println("  Orden: " + pr.getOrdenVisualizacion());
            System.out.println("  Estado: " + pr.getEstado());
            System.out.println("------------------------------------");
        }
        System.out.println("--- Fin del listado ---");
        JOptionPane.showMessageDialog(null, "Listado de preguntas mostrado en la consola.", "Listado de Preguntas", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Presione Enter para continuar...");
        lectorConsola.nextLine();
    }


    private static void modificarPreguntaUI() {
        String idStr = JOptionPane.showInputDialog(null, "Ingrese el ID de la pregunta a modificar:", "Modificar Pregunta", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null || idStr.trim().isEmpty()) return;

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String texto = JOptionPane.showInputDialog(null, "Nuevo texto de la pregunta (ID: " + id + "):", "Modificar Pregunta", JOptionPane.PLAIN_MESSAGE);
        if (texto == null || texto.trim().isEmpty()) {
             JOptionPane.showMessageDialog(null, "El texto no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] tiposRespuesta = {"TEXTO_CORTO", "NUMERO", "FECHA", "SELECCION_UNICA_RADIO", "SELECCION_UNICA_COMBO"};
        String tipoRespuesta = (String) JOptionPane.showInputDialog(null, "Nuevo tipo de respuesta:", "Modificar Pregunta",
                JOptionPane.QUESTION_MESSAGE, null, tiposRespuesta, tiposRespuesta[0]);
        if (tipoRespuesta == null) return;

        String opcionesPosibles = null;
        if (tipoRespuesta.startsWith("SELECCION_UNICA")) {
            opcionesPosibles = JOptionPane.showInputDialog(null, "Nuevas opciones posibles (separadas por ';'):", "Modificar Pregunta", JOptionPane.PLAIN_MESSAGE);
             if (opcionesPosibles == null) opcionesPosibles = "";
        }

        int obligatoriaOption = JOptionPane.showConfirmDialog(null, "¿Es obligatoria?", "Modificar Pregunta", JOptionPane.YES_NO_OPTION);
        boolean esObligatoria = (obligatoriaOption == JOptionPane.YES_OPTION);

        String ordenStr = JOptionPane.showInputDialog(null, "Nuevo orden de visualización (número):", "Modificar Pregunta", JOptionPane.PLAIN_MESSAGE);
        int ordenVisualizacion = 0;
        try {
            if (ordenStr != null && !ordenStr.trim().isEmpty()) {
                ordenVisualizacion = Integer.parseInt(ordenStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Orden de visualización debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] estados = {"ACTIVA", "INACTIVA"};
        String estado = (String) JOptionPane.showInputDialog(null, "Nuevo estado:", "Modificar Pregunta",
                JOptionPane.QUESTION_MESSAGE, null, estados, estados[0]);
        if (estado == null) return;


        if (servicioConfig.modificarPreguntaRegistro(id, texto.trim(), tipoRespuesta, opcionesPosibles, esObligatoria, ordenVisualizacion, estado)) {
            JOptionPane.showMessageDialog(null, "Pregunta de registro modificada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al modificar la pregunta de registro. Verifique el ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void eliminarPreguntaUI() {
        String idStr = JOptionPane.showInputDialog(null, "Ingrese el ID de la pregunta a eliminar:", "Eliminar Pregunta", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null || idStr.trim().isEmpty()) return;

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar la pregunta con ID " + id + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (servicioConfig.eliminarPreguntaRegistro(id)) {
                JOptionPane.showMessageDialog(null, "Pregunta de registro eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar la pregunta de registro. Verifique el ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}