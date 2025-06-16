package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.modelo.EncuestaDetallePregunta;
import SteveJobs.encuestas.modelo.RespuestaUsuario;
import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.servicio.ServicioEncuestas;
import SteveJobs.encuestas.servicio.ServicioParticipacion;

import javax.swing.JOptionPane;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

public class UIMenuEncuestado {

    private static ServicioEncuestas servicioEncuestas = new ServicioEncuestas();
    private static ServicioParticipacion servicioParticipacion = new ServicioParticipacion();

    /**
     * Muestra el menú principal para el encuestado.
     * Permite al usuario ver las encuestas disponibles y seleccionar una para responder.
     *
     * @param encuestado El objeto {@link Usuario} del encuestado que ha iniciado sesión.
     */
    public static void mostrarMenu(Usuario encuestado) {
        if (encuestado == null) {
            JOptionPane.showMessageDialog(null, "Error: No se ha proporcionado un usuario encuestado.", "Error de Usuario", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean salirMenuEncuestado = false;
        String nombreEncuestado = encuestado.getNombres() != null ? encuestado.getNombres() : "Encuestado";

        while (!salirMenuEncuestado) {
            List<Encuesta> encuestasDisponibles = servicioEncuestas.obtenerEncuestasActivasParaUsuario(encuestado);

            if (encuestasDisponibles == null || encuestasDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay encuestas disponibles para ti en este momento, " + nombreEncuestado + ".", "Sin Encuestas", JOptionPane.INFORMATION_MESSAGE);
                salirMenuEncuestado = true;
                continue;
            }

            List<String> opcionesDialogo = new ArrayList<>();
            for (Encuesta e : encuestasDisponibles) {
                opcionesDialogo.add(e.getIdEncuesta() + ": " + e.getNombreEncuesta() + " (Descripción: " + (e.getDescripcion() != null ? e.getDescripcion().substring(0, Math.min(e.getDescripcion().length(), 30))+"..." : "N/A") + ")");
            }
            String opcionVolver = "Volver al Menú Principal";
            opcionesDialogo.add(opcionVolver);

            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Bienvenido, " + nombreEncuestado + ".\nSelecciona una encuesta para responder o vuelve al menú principal:",
                    "Portal Encuestado - Encuestas Disponibles",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcionesDialogo.toArray(new String[0]),
                    opcionesDialogo.get(0)
            );

            if (seleccion == null || seleccion.equals(opcionVolver)) {
                salirMenuEncuestado = true;
            } else {
                Encuesta encuestaSeleccionada = null;
                try {
                    int idEncuestaSeleccionada = Integer.parseInt(seleccion.split(":")[0].trim());
                    for (Encuesta e : encuestasDisponibles) {
                        if (e.getIdEncuesta() == idEncuestaSeleccionada) {
                            encuestaSeleccionada = e;
                            break;
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Error al procesar la selección.", "Error Interno", JOptionPane.ERROR_MESSAGE);
                }

                if (encuestaSeleccionada != null) {
                    responderEncuestaUI(encuestado, encuestaSeleccionada);
                    // Al volver de responderEncuestaUI, el bucle continuará,
                    // y obtenerEncuestasActivasParaUsuario se llamará de nuevo,
                    // refrescando la lista (la encuesta respondida ya no debería aparecer).
                } else if (!seleccion.equals(opcionVolver)) {
                     JOptionPane.showMessageDialog(null, "La encuesta seleccionada no es válida. Inténtalo de nuevo.", "Error de Selección", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    /**
     * Guía al usuario a través del proceso de respuesta a una encuesta seleccionada.
     *
     * @param encuestado El {@link Usuario} que está respondiendo.
     * @param encuestaSeleccionada La {@link Encuesta} que se va a responder.
     */
    private static void responderEncuestaUI(Usuario encuestado, Encuesta encuestaSeleccionada) {
        Encuesta encuestaConPreguntas = servicioEncuestas.obtenerDetallesCompletosEncuesta(encuestaSeleccionada.getIdEncuesta());

        if (encuestaConPreguntas == null || encuestaConPreguntas.getPreguntasAsociadas() == null || encuestaConPreguntas.getPreguntasAsociadas().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se pudieron cargar las preguntas para esta encuesta (ID: " + encuestaSeleccionada.getIdEncuesta() + ").\nPuede que no tenga preguntas configuradas.", "Error de Encuesta", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<RespuestaUsuario> respuestasRecogidas = new ArrayList<>();
        Timestamp tsInicioParticipacion = new Timestamp(System.currentTimeMillis());

        JOptionPane.showMessageDialog(null, "Vas a comenzar la encuesta: '" + encuestaConPreguntas.getNombreEncuesta() + "'.\n" + encuestaConPreguntas.getDescripcion(), "Inicio de Encuesta", JOptionPane.INFORMATION_MESSAGE);

        for (EncuestaDetallePregunta edp : encuestaConPreguntas.getPreguntasAsociadas()) {
            String textoPregunta = edp.getTextoPreguntaMostrable();
            if (textoPregunta == null || textoPregunta.trim().isEmpty()) {
                textoPregunta = "(Texto de pregunta no disponible)";
            }

            String tituloPregunta = "Pregunta " + edp.getOrdenEnEncuesta() + "/" + encuestaConPreguntas.getPreguntasAsociadas().size();
            String respuestaValor = JOptionPane.showInputDialog(null, textoPregunta, tituloPregunta, JOptionPane.QUESTION_MESSAGE);

            if (respuestaValor == null) { // Usuario presionó Cancelar
                int confirmCancel = JOptionPane.showConfirmDialog(null,
                    "¿Estás seguro de que deseas cancelar tu participación en esta encuesta?\nTus respuestas no serán guardadas.",
                    "Cancelar Participación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (confirmCancel == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Participación cancelada. Tus respuestas no han sido guardadas.", "Participación Cancelada", JOptionPane.INFORMATION_MESSAGE);
                    return; // Sale del método responderEncuestaUI
                } else {
                    // Forzar una respuesta vacía si el usuario no quiere cancelar pero no ingresó nada.
                    // O se podría volver a preguntar. Por simplicidad, se toma como vacía.
                    respuestaValor = "";
                }
            }

            RespuestaUsuario respuesta = new RespuestaUsuario();
            respuesta.setIdEncuestaDetallePregunta(edp.getIdEncuestaDetalle()); // Corregido para usar el ID correcto
            respuesta.setIdUsuario(encuestado.getId_usuario());
            respuesta.setValorRespuesta(respuestaValor);
            respuesta.setFechaHoraRespuesta(new Timestamp(System.currentTimeMillis()));
            respuesta.setTsInicioParticipacion(tsInicioParticipacion);
            // tsFinParticipacion y retroalimentacion_usr se setearán al final.
            respuestasRecogidas.add(respuesta);
        }

        // Todas las preguntas respondidas (o cancelado)
        Timestamp tsFinParticipacion = new Timestamp(System.currentTimeMillis());
        String retroalimentacion = JOptionPane.showInputDialog(null,
            "Has completado todas las preguntas. ¿Algún comentario o retroalimentación adicional sobre la encuesta?",
            "Retroalimentación (Opcional)",
            JOptionPane.PLAIN_MESSAGE);

        for (RespuestaUsuario r : respuestasRecogidas) {
            r.setTsFinParticipacion(tsFinParticipacion);
            if (retroalimentacion != null && !retroalimentacion.trim().isEmpty()) {
                r.setRetroalimentacionUsuario(retroalimentacion.trim());
            }
        }

        boolean exitoRegistro = servicioParticipacion.registrarRespuestasCompletas(respuestasRecogidas);

        if (exitoRegistro) {
            JOptionPane.showMessageDialog(null, "¡Gracias! Tus respuestas han sido guardadas exitosamente.", "Participación Completada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Hubo un error al guardar tus respuestas. Por favor, inténtalo más tarde.", "Error al Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
