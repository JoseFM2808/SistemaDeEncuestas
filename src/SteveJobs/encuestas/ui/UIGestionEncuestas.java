/*
 * Autores del Módulo:
 * - Alfredo Swidin
 *
 * Responsabilidad Principal:
 * - UI para gestión de encuestas
 */
package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.servicio.ServicioEncuestas;
import SteveJobs.encuestas.util.PilaNavegacion; // Importar PilaNavegacion

import javax.swing.JOptionPane;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UIGestionEncuestas {

    private static ServicioEncuestas servicioEncuestas = new ServicioEncuestas();
    private static Usuario adminLogueado;

    public static void mostrarMenu(Usuario admin) {
        adminLogueado = admin;
        boolean salir = false;
        while (!salir) {
            String[] opciones = {
                "Crear Nueva Encuesta",
                "Listar Todas las Encuestas",
                "Ver/Modificar Detalles de Encuesta",
                "Configurar Preguntas de Encuesta",
                "Cambiar Estado de Encuesta",
                "Copiar Encuesta",
                "Eliminar Encuesta",
                "Buscar Encuesta por ID",
                "Volver al Menú Principal"
            };
            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Gestión de Encuestas",
                    "Admin: Módulo de Encuestas",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null || seleccion.equals(opciones[8])) {
                if (PilaNavegacion.instance != null && !PilaNavegacion.instance.isEmpty()) {
                    PilaNavegacion.instance.pop();
                }
                salir = true;
                continue;
            }

            try {
                switch (seleccion) {
                    case "Crear Nueva Encuesta":
                        crearEncuestaUI();
                        break;
                    case "Listar Todas las Encuestas":
                        listarEncuestasUI();
                        break;
                    case "Ver/Modificar Detalles de Encuesta":
                        verOModificarDetallesEncuestaUI();
                        break;
                    case "Configurar Preguntas de Encuesta":
                        configurarPreguntasDeUnaEncuestaUI();
                        break;
                    case "Cambiar Estado de Encuesta":
                        cambiarEstadoEncuestaUI();
                        break;
                    case "Copiar Encuesta":
                        copiarEncuestaUI();
                        break;
                    case "Eliminar Encuesta":
                        eliminarEncuestaUI();
                        break;
                    case "Buscar Encuesta por ID": // Nuevo case
                        buscarEncuestaPorIdUI();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error: " + e.getMessage(), "Error Inesperado", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private static Timestamp obtenerFechaDesdeJOptionPane(String mensajeDialogo, String tituloDialogo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        String fechaStr = JOptionPane.showInputDialog(null, mensajeDialogo + " (Formato: YYYY-MM-DD HH:MM:SS)", tituloDialogo, JOptionPane.PLAIN_MESSAGE);
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }
        try {
            java.util.Date parsedDate = sdf.parse(fechaStr.trim());
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Formato de fecha y hora inválido. Use YYYY-MM-DD HH:MM:SS", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static void crearEncuestaUI() {
        String nombre = JOptionPane.showInputDialog(null, "Nombre de la nueva encuesta:", "Crear Encuesta", JOptionPane.PLAIN_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) return;

        String descripcion = JOptionPane.showInputDialog(null, "Descripción:", "Crear Encuesta", JOptionPane.PLAIN_MESSAGE);
        Timestamp fechaInicio = obtenerFechaDesdeJOptionPane("Fecha de Inicio (YYYY-MM-DD HH:MM:SS):", "Crear Encuesta");
        if (fechaInicio == null) return;

        Timestamp fechaFin = obtenerFechaDesdeJOptionPane("Fecha de Fin (YYYY-MM-DD HH:MM:SS):", "Crear Encuesta");
        if (fechaFin == null) return;

        if (fechaFin.before(fechaInicio)) {
            JOptionPane.showMessageDialog(null, "La fecha de fin no puede ser anterior a la de inicio.", "Error de Fechas", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String publicoStr = JOptionPane.showInputDialog(null, "Cantidad de público objetivo (ej. 100, 0 si no aplica):", "Crear Encuesta", JOptionPane.PLAIN_MESSAGE);
        int publicoObjetivo = 0;
        if (publicoStr != null && !publicoStr.trim().isEmpty()) {
            try {
                publicoObjetivo = Integer.parseInt(publicoStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Cantidad de público debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String definicionPerfil = JOptionPane.showInputDialog(null, "Definición del perfil del encuestado (ej. Criterios en texto o JSON):", "Crear Encuesta", JOptionPane.PLAIN_MESSAGE);


        int idAdmin = (adminLogueado != null) ? adminLogueado.getId_usuario() : 0;
        
        int idNuevaEncuesta = servicioEncuestas.registrarNuevaEncuesta(nombre, descripcion, fechaInicio, fechaFin, 0, "", idAdmin);

        if (idNuevaEncuesta != -1) {
            JOptionPane.showMessageDialog(null, "Encuesta '" + nombre + "' creada con ID: " + idNuevaEncuesta, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al crear la encuesta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void listarEncuestasUI() {
        // Se llama al método que devuelve la lista ya ordenada por nombre
        List<Encuesta> encuestas = servicioEncuestas.obtenerTodasLasEncuestasOrdenadasPorNombre();
        if (encuestas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay encuestas registradas.", "Listar Encuestas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("Encuestas Registradas (Orden Alfabético):\n\n");
        for (Encuesta e : encuestas) {
            // --- CORRECCIÓN ---
            // Usando los getters correctos del modelo Encuesta
            sb.append("ID: ").append(e.getIdEncuesta())
              .append(" - Nombre: ").append(e.getNombre())
              .append(" - Estado: ").append(e.getEstado())
              .append("\n  Vigencia: ").append(e.getFechaInicio()).append(" a ").append(e.getFechaFin())
              .append("\n------------------------------------\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));
        JOptionPane.showMessageDialog(null, scrollPane, "Listado de Encuestas", JOptionPane.INFORMATION_MESSAGE);
    }

    private static Encuesta seleccionarEncuestaParaAccion(String accion) {
        // Se obtiene la lista ordenada para que el usuario vea lo mismo que en "Listar"
        List<Encuesta> encuestas = servicioEncuestas.obtenerTodasLasEncuestasOrdenadasPorNombre();
        if (encuestas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay encuestas para " + accion + ".", "Error", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        // --- CORRECCIÓN ---
        // Se usa el getter correcto getNombre()
        String[] opcionesEncuestas = encuestas.stream()
                .map(e -> e.getIdEncuesta() + ": " + e.getNombre() + " (" + e.getEstado() + ")")
                .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(null, "Seleccione la encuesta para " + accion + ":",
                "Seleccionar Encuesta", JOptionPane.QUESTION_MESSAGE, null, opcionesEncuestas, opcionesEncuestas[0]);

        if (seleccion != null) {
            int idEncuestaSeleccionada = Integer.parseInt(seleccion.split(":")[0]);
            // El servicio ya devuelve el objeto Encuesta completo
            return servicioEncuestas.obtenerDetallesCompletosEncuesta(idEncuestaSeleccionada);
        }
        return null;
    }

    private static void verOModificarDetallesEncuestaUI() {
        Encuesta encuesta = seleccionarEncuestaParaAccion("ver/modificar detalles");
        if (encuesta == null) return;

        // --- CORRECCIÓN ---
        // Usando los getters correctos para mostrar los detalles
        StringBuilder detalles = new StringBuilder();
        detalles.append("ID: ").append(encuesta.getIdEncuesta()).append("\n");
        detalles.append("Nombre: ").append(encuesta.getNombre()).append("\n");
        detalles.append("Descripción: ").append(encuesta.getDescripcion()).append("\n");
        detalles.append("Inicio Vigencia: ").append(encuesta.getFechaInicio()).append("\n");
        detalles.append("Fin Vigencia: ").append(encuesta.getFechaFin()).append("\n");
        detalles.append("Público Objetivo: ").append(encuesta.getPublicoObjetivo()).append("\n");
        detalles.append("Perfil Requerido: ").append(encuesta.getPerfilRequerido()).append("\n");
        detalles.append("Estado: ").append(encuesta.getEstado()).append("\n");

        JTextArea textArea = new JTextArea(detalles.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));
        
        int opcion = JOptionPane.showConfirmDialog(null, new JScrollPane(textArea), "Detalles Encuesta ID: " + encuesta.getIdEncuesta() + " - ¿Modificar?", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
             // Usando los getters correctos para pre-rellenar los diálogos de modificación
            String nuevoNombre = JOptionPane.showInputDialog(null, "Nuevo nombre:", encuesta.getNombre());
            String nuevaDesc = JOptionPane.showInputDialog(null, "Nueva descripción:", encuesta.getDescripcion());
            
            // Llamada al servicio (ya era correcta, pero ahora la consistencia está asegurada)
            if (servicioEncuestas.modificarMetadatosEncuesta(encuesta.getIdEncuesta(), nuevoNombre, nuevaDesc,
                encuesta.getFechaInicio(), encuesta.getFechaFin(),
                encuesta.getPublicoObjetivo(), encuesta.getPerfilRequerido())) {
                JOptionPane.showMessageDialog(null, "Encuesta actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar la encuesta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void configurarPreguntasDeUnaEncuestaUI() {
        Encuesta encuesta = seleccionarEncuestaParaAccion("configurar preguntas");
        if (encuesta == null) return;

        if ("Activa".equalsIgnoreCase(encuesta.getEstado()) || "Cerrada".equalsIgnoreCase(encuesta.getEstado())) {
            JOptionPane.showMessageDialog(null, "No se pueden configurar preguntas de una encuesta que está Activa o Cerrada.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UIConfigurarPreguntasEncuesta.mostrarMenuConfiguracion(encuesta.getIdEncuesta());
    }

    private static void cambiarEstadoEncuestaUI() {
        Encuesta encuesta = seleccionarEncuestaParaAccion("cambiar estado");
        if (encuesta == null) return;

        String[] estadosPosibles = {"Borrador", "Activa", "Cerrada", "Cancelada", "Archivada"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(null, "Encuesta: " + encuesta.getNombre() + "\nEstado actual: " + encuesta.getEstado() + "\nSeleccione el nuevo estado:",
                "Cambiar Estado de Encuesta", JOptionPane.QUESTION_MESSAGE, null, estadosPosibles, encuesta.getEstado());

        if (nuevoEstado != null && !nuevoEstado.equals(encuesta.getEstado())) {
            if (servicioEncuestas.cambiarEstadoEncuesta(encuesta.getIdEncuesta(), nuevoEstado)) {
                JOptionPane.showMessageDialog(null, "Estado de la encuesta actualizado a " + nuevoEstado + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al cambiar el estado. Verifique si cumple condiciones (ej. 12 preguntas para activar).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void copiarEncuestaUI() {
        Encuesta encuestaOriginal = seleccionarEncuestaParaAccion("copiar");
        if (encuestaOriginal == null) return;

        int idAdmin = (adminLogueado != null) ? adminLogueado.getId_usuario() : 0;
        Encuesta encuestaCopiada = servicioEncuestas.copiarEncuesta(encuestaOriginal.getIdEncuesta(), idAdmin);

        if (encuestaCopiada != null) {
            JOptionPane.showMessageDialog(null, "Encuesta copiada exitosamente con ID: " + encuestaCopiada.getIdEncuesta() + "\nNuevo nombre: " + encuestaCopiada.getNombre(), "Copia Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al copiar la encuesta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void eliminarEncuestaUI() {
        Encuesta encuesta = seleccionarEncuestaParaAccion("eliminar");
        if (encuesta == null) return;

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea eliminar la encuesta '" + encuesta.getNombre() + "' (ID: " + encuesta.getIdEncuesta() + ")?\n¡Esto eliminará sus preguntas configuradas y todas las respuestas de usuarios!",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (servicioEncuestas.eliminarEncuesta(encuesta.getIdEncuesta())) {
                JOptionPane.showMessageDialog(null, "Encuesta eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar la encuesta. Podría tener datos asociados que impiden su borrado directo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void buscarEncuestaPorIdUI() {
        String idStr = JOptionPane.showInputDialog(null, "Ingrese el ID de la encuesta a buscar:", "Buscar Encuesta por ID", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null || idStr.trim().isEmpty()) {
            return; // Usuario canceló o no ingresó nada
        }

        try {
            int idBuscado = Integer.parseInt(JOptionPane.showInputDialog("ID a buscar:"));
            List<Encuesta> todasLasEncuestas = servicioEncuestas.obtenerTodasLasEncuestasOrdenadasPorNombre();
            Encuesta encuestaEncontrada = servicioEncuestas.buscarEncuestaEnListaPorId(todasLasEncuestas, idBuscado);

            if (encuestaEncontrada != null) {
                StringBuilder detalles = new StringBuilder();
                detalles.append("Encuesta Encontrada:\n\n");
                detalles.append("ID: ").append(encuestaEncontrada.getIdEncuesta()).append("\n");
                detalles.append("Nombre: ").append(encuestaEncontrada.getNombre()).append("\n");
                detalles.append("Descripción: ").append(encuestaEncontrada.getDescripcion()).append("\n");
                detalles.append("Estado: ").append(encuestaEncontrada.getEstado()).append("\n");
                detalles.append("Fecha Inicio: ").append(encuestaEncontrada.getFechaInicio()).append("\n");
                detalles.append("Fecha Fin: ").append(encuestaEncontrada.getFechaFin()).append("\n");
                detalles.append("Público Objetivo: ").append(encuestaEncontrada.getPublicoObjetivo()).append("\n");
                detalles.append("Perfil Requerido: ").append(encuestaEncontrada.getPerfilRequerido()).append("\n");

                JTextArea textArea = new JTextArea(detalles.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(400, 200));
                JOptionPane.showMessageDialog(null, scrollPane, "Detalles de Encuesta ID: " + idBuscado, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Encuesta con ID " + idBuscado + " no encontrada.", "Búsqueda sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID ingresado no es un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado durante la búsqueda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
