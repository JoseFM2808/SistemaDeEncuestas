/*
Autor: Alfredo Swidin
 */
package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.modelo.EncuestaDetallePregunta;
import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.servicio.ServicioEncuestas;
import SteveJobs.encuestas.servicio.ServicioPreguntas;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.List;
import java.util.ArrayList;

public class UIConfigurarPreguntasEncuesta {

    private static ServicioEncuestas servicioEncuestas = new ServicioEncuestas();
    private static ServicioPreguntas servicioPreguntas = new ServicioPreguntas();
    private static int idEncuestaActual;

    public static void mostrarMenuConfiguracion(int idEncuesta) {
        if (idEncuesta == -1) {
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar una encuesta válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        idEncuestaActual = idEncuesta;
        Encuesta encuesta = servicioEncuestas.obtenerDetallesCompletosEncuesta(idEncuestaActual);
        if (encuesta == null) {
             JOptionPane.showMessageDialog(null, "No se pudo cargar la encuesta con ID: " + idEncuesta, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean salir = false;
        while (!salir) {
            String tituloMenu = "Configurar Preguntas para Encuesta: " + encuesta.getNombreEncuesta() + " (ID: " + idEncuesta + ")";
            String[] opciones = {
                    "Asociar Pregunta del Banco",
                    "Agregar Pregunta Nueva (Única para esta encuesta)",
                    "Ver Preguntas Asociadas",
                    "Marcar/Desmarcar Pregunta como Descarte",
                    "Eliminar Pregunta de la Encuesta",
                    "Volver a Gestión de Encuestas"
            };
            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    tituloMenu,
                    "Configuración de Preguntas",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null || seleccion.equals(opciones[5])) {
                salir = true;
                continue;
            }

            switch (seleccion) {
                case "Asociar Pregunta del Banco":
                    asociarPreguntaBancoUI();
                    break;
                case "Agregar Pregunta Nueva (Única para esta encuesta)":
                    agregarPreguntaNuevaUI();
                    break;
                case "Ver Preguntas Asociadas":
                    verPreguntasAsociadasUI();
                    break;
                case "Marcar/Desmarcar Pregunta como Descarte":
                    marcarDesmarcarDescarteUI();
                    break;
                case "Eliminar Pregunta de la Encuesta":
                    eliminarPreguntaDeEncuestaUI();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
            encuesta = servicioEncuestas.obtenerDetallesCompletosEncuesta(idEncuestaActual);
            if (encuesta == null) {
                JOptionPane.showMessageDialog(null, "La encuesta ya no está disponible.", "Error", JOptionPane.ERROR_MESSAGE);
                salir = true;
            }
        }
    }

    private static void asociarPreguntaBancoUI() {
        List<PreguntaBanco> preguntasBanco = servicioPreguntas.listarPreguntasDelBancoConFiltro(null, null);
        if (preguntasBanco == null || preguntasBanco.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay preguntas en el banco para asociar.", "Banco Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<String> opcionesPreguntas = new ArrayList<>();
        for (PreguntaBanco pb : preguntasBanco) {
            opcionesPreguntas.add(pb.getIdPreguntaBanco() + ": " + pb.getTextoPregunta().substring(0, Math.min(pb.getTextoPregunta().length(), 50)) + "...");
        }

        String seleccionPreguntaStr = (String) JOptionPane.showInputDialog(null, "Seleccione la pregunta del banco a asociar:",
                "Asociar Pregunta del Banco", JOptionPane.QUESTION_MESSAGE, null, opcionesPreguntas.toArray(),
                opcionesPreguntas.isEmpty() ? null : opcionesPreguntas.get(0));

        if (seleccionPreguntaStr == null) return;

        int idPreguntaBancoSeleccionada = Integer.parseInt(seleccionPreguntaStr.split(":")[0]);

        String ordenStr = JOptionPane.showInputDialog(null, "Orden de la pregunta en la encuesta (1-12):", "Orden de Pregunta", JOptionPane.PLAIN_MESSAGE);
        if (ordenStr == null || ordenStr.trim().isEmpty()) return;
        int orden;
        try {
            orden = Integer.parseInt(ordenStr.trim());
            if (orden < 1 || orden > 12) {
                JOptionPane.showMessageDialog(null, "El orden debe ser un número entre 1 y 12.", "Error de Orden", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Orden debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int esDescarteOption = JOptionPane.showConfirmDialog(null, "¿Marcar esta pregunta como de descarte?", "Pregunta de Descarte", JOptionPane.YES_NO_OPTION);
        boolean esDescarte = (esDescarteOption == JOptionPane.YES_OPTION);
        String criterioDescarte = null;
        if (esDescarte) {
            criterioDescarte = JOptionPane.showInputDialog(null, "Ingrese el criterio de descarte (valor de respuesta que descarta):", "Criterio de Descarte", JOptionPane.PLAIN_MESSAGE);
            if (criterioDescarte == null) {
                criterioDescarte = "";
            }
        }

        if (servicioEncuestas.asociarPreguntaDelBancoAEncuesta(idEncuestaActual, idPreguntaBancoSeleccionada, orden, esDescarte, criterioDescarte)) {
            JOptionPane.showMessageDialog(null, "Pregunta del banco asociada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al asociar la pregunta. Verifique si la encuesta ya tiene 12 preguntas, si la pregunta ya está asociada o si el ID de la pregunta del banco es válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void agregarPreguntaNuevaUI() {
        String texto = JOptionPane.showInputDialog(null, "Texto de la nueva pregunta (única para esta encuesta):", "Agregar Pregunta Nueva", JOptionPane.PLAIN_MESSAGE);
        if (texto == null || texto.trim().isEmpty()) return;

        String nombreTipo = JOptionPane.showInputDialog(null, "Nombre del Tipo de Pregunta (ej. TEXTO_CORTO):", "Tipo de Pregunta", JOptionPane.PLAIN_MESSAGE);
        if (nombreTipo == null || nombreTipo.trim().isEmpty()) return;

        String nombreClasif = JOptionPane.showInputDialog(null, "Nombre de la Clasificación (opcional):", "Clasificación", JOptionPane.PLAIN_MESSAGE);

        String ordenStr = JOptionPane.showInputDialog(null, "Orden de la pregunta en la encuesta (1-12):", "Orden de Pregunta", JOptionPane.PLAIN_MESSAGE);
        if (ordenStr == null || ordenStr.trim().isEmpty()) return;
        int orden;
        try {
            orden = Integer.parseInt(ordenStr.trim());
            if (orden < 1 || orden > 12) {
                JOptionPane.showMessageDialog(null, "El orden debe ser un número entre 1 y 12.", "Error de Orden", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Orden debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int esDescarteOption = JOptionPane.showConfirmDialog(null, "¿Marcar esta pregunta como de descarte?", "Pregunta de Descarte", JOptionPane.YES_NO_OPTION);
        boolean esDescarte = (esDescarteOption == JOptionPane.YES_OPTION);
        String criterioDescarte = null;
        if (esDescarte) {
            criterioDescarte = JOptionPane.showInputDialog(null, "Ingrese el criterio de descarte (valor de respuesta que descarta):", "Criterio de Descarte", JOptionPane.PLAIN_MESSAGE);
            if (criterioDescarte == null) criterioDescarte = "";
        }

        if (servicioEncuestas.agregarPreguntaNuevaAEncuesta(idEncuestaActual, texto, nombreTipo, (nombreClasif != null && !nombreClasif.trim().isEmpty() ? nombreClasif.trim() : null), orden, esDescarte, criterioDescarte)) {
             JOptionPane.showMessageDialog(null, "Pregunta nueva agregada y asociada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al agregar la pregunta nueva. Verifique el tipo/clasificación o si la encuesta ya tiene 12 preguntas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void verPreguntasAsociadasUI() {
        List<EncuestaDetallePregunta> detalles = servicioEncuestas.obtenerPreguntasDeEncuesta(idEncuestaActual);
        if (detalles == null || detalles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay preguntas asociadas a esta encuesta.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("Preguntas de la Encuesta ID: " + idEncuestaActual + "\n\n");
        for (EncuestaDetallePregunta edp : detalles) {
            sb.append("Orden: ").append(edp.getOrdenEnEncuesta()).append("\n");
            sb.append("  ID Detalle: ").append(edp.getIdEncuestaDetalle()).append("\n");
            sb.append("  Texto: ").append(edp.getTextoPreguntaMostrable()).append("\n");
            sb.append("  Es Descarte: ").append(edp.isEsPreguntaDescarte() ? "Sí" : "No").append("\n");
            if (edp.isEsPreguntaDescarte() && edp.getCriterioDescarteValor() != null) {
                sb.append("  Criterio Descarte: ").append(edp.getCriterioDescarteValor()).append("\n");
            }
            sb.append("------------------------------------\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));
        JOptionPane.showMessageDialog(null, scrollPane, "Preguntas Asociadas", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void marcarDesmarcarDescarteUI() {
        List<EncuestaDetallePregunta> detalles = servicioEncuestas.obtenerPreguntasDeEncuesta(idEncuestaActual);
        if (detalles == null || detalles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay preguntas para marcar/desmarcar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> opcionesPreguntas = new ArrayList<>();
        for (EncuestaDetallePregunta edp : detalles) {
            opcionesPreguntas.add(edp.getOrdenEnEncuesta() + ". (ID_Detalle:" + edp.getIdEncuestaDetalle() + ") " + edp.getTextoPreguntaMostrable().substring(0, Math.min(edp.getTextoPreguntaMostrable().length(), 40)) + "..." + (edp.isEsPreguntaDescarte() ? " (DESCARTE)" : ""));
        }

        String seleccionStr = (String) JOptionPane.showInputDialog(null, "Seleccione la pregunta para cambiar estado de descarte:",
                "Marcar/Desmarcar Descarte", JOptionPane.QUESTION_MESSAGE, null, opcionesPreguntas.toArray(), opcionesPreguntas.get(0));

        if (seleccionStr == null) return;

        int idEncuestaDetalleSeleccionada = Integer.parseInt(seleccionStr.split("\\)")[0].split(":")[1]);

        EncuestaDetallePregunta preguntaSeleccionada = null;
        for(EncuestaDetallePregunta edp : detalles){
            if(edp.getIdEncuestaDetalle() == idEncuestaDetalleSeleccionada){
                preguntaSeleccionada = edp;
                break;
            }
        }

        if(preguntaSeleccionada == null){
             JOptionPane.showMessageDialog(null, "Error al seleccionar la pregunta.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        int accion = JOptionPane.showConfirmDialog(null,
                "Pregunta: " + preguntaSeleccionada.getTextoPreguntaMostrable() +
                "\nEstado actual de descarte: " + (preguntaSeleccionada.isEsPreguntaDescarte() ? "SÍ" : "NO") +
                "\n¿Desea cambiar el estado de descarte?",
                "Cambiar Estado de Descarte", JOptionPane.YES_NO_OPTION);

        if (accion == JOptionPane.YES_OPTION) {
            boolean nuevoEstadoDescarte = !preguntaSeleccionada.isEsPreguntaDescarte();
            String criterio = null;
            if (nuevoEstadoDescarte) {
                criterio = JOptionPane.showInputDialog(null, "Ingrese el criterio de descarte (ej. la respuesta que descarta):", "Criterio de Descarte", JOptionPane.PLAIN_MESSAGE);
                if (criterio == null || criterio.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El criterio de descarte no puede estar vacío si se marca como descarte.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            if (nuevoEstadoDescarte) {
                if (servicioEncuestas.marcarPreguntaComoDescarte(idEncuestaDetalleSeleccionada, criterio)) {
                    JOptionPane.showMessageDialog(null, "Pregunta marcada como descarte.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al marcar como descarte.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                 if (servicioEncuestas.desmarcarPreguntaComoDescarte(idEncuestaDetalleSeleccionada)) {
                    JOptionPane.showMessageDialog(null, "Pregunta desmarcada como descarte.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al desmarcar como descarte.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private static void eliminarPreguntaDeEncuestaUI() {
        List<EncuestaDetallePregunta> detalles = servicioEncuestas.obtenerPreguntasDeEncuesta(idEncuestaActual);
        if (detalles == null || detalles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay preguntas para eliminar de esta encuesta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
         List<String> opcionesPreguntas = new ArrayList<>();
        for (EncuestaDetallePregunta edp : detalles) {
            opcionesPreguntas.add(edp.getOrdenEnEncuesta() + ". (ID_Detalle:" + edp.getIdEncuestaDetalle() + ") " + edp.getTextoPreguntaMostrable().substring(0, Math.min(edp.getTextoPreguntaMostrable().length(), 40)) + "...");
        }

        String seleccionStr = (String) JOptionPane.showInputDialog(null, "Seleccione la pregunta a eliminar de la encuesta:",
                "Eliminar Pregunta de Encuesta", JOptionPane.QUESTION_MESSAGE, null, opcionesPreguntas.toArray(), opcionesPreguntas.get(0));

        if (seleccionStr == null) return;
        int idEncuestaDetalleSeleccionada = Integer.parseInt(seleccionStr.split("\\)")[0].split(":")[1]);

        int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar esta pregunta de la encuesta?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (servicioEncuestas.eliminarPreguntaDeEncuestaServicio(idEncuestaDetalleSeleccionada)) {
                JOptionPane.showMessageDialog(null, "Pregunta eliminada de la encuesta exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar la pregunta de la encuesta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}