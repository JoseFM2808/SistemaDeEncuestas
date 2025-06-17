/*
 * Autores del Módulo:
 * - José Flores
 * - Asistente de AED (Implementación y Refactorización)
 *
 * Responsabilidad Principal:
 * - UI para la interacción del encuestado con el sistema.
 */
package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.modelo.EncuestaDetallePregunta;
import SteveJobs.encuestas.modelo.RespuestaUsuario;
import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.servicio.ServicioEncuestas;
import SteveJobs.encuestas.servicio.ServicioParticipacion;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UIMenuEncuestado {

    private static ServicioEncuestas servicioEncuestas = new ServicioEncuestas();
    private static ServicioParticipacion servicioParticipacion = new ServicioParticipacion();

    public static void mostrarMenu(Usuario encuestado) {
        boolean salir = false;
        while (!salir) {
            String[] opciones = {"Ver Encuestas Disponibles", "Salir"};
            int seleccion = JOptionPane.showOptionDialog(null,
                    "Bienvenido al Portal del Encuestado, " + encuestado.getNombres() + "!",
                    "Portal Encuestado",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

            switch (seleccion) {
                case 0:
                    mostrarEncuestasDisponibles(encuestado);
                    break;
                case 1:
                case JOptionPane.CLOSED_OPTION:
                    salir = true;
                    break;
            }
        }
    }

    private static void mostrarEncuestasDisponibles(Usuario encuestado) {
        List<Encuesta> encuestas = servicioEncuestas.obtenerEncuestasActivasParaUsuario(encuestado);

        if (encuestas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Actualmente no hay encuestas disponibles para tu perfil.", "Sin Encuestas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] nombresEncuestas = encuestas.stream()
                .map(e -> e.getIdEncuesta() + ": " + e.getNombre())
                .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(null, "Selecciona una encuesta para responder:",
                "Encuestas Disponibles", JOptionPane.QUESTION_MESSAGE, null, nombresEncuestas, nombresEncuestas[0]);

        if (seleccion != null) {
            int idEncuestaSeleccionada = Integer.parseInt(seleccion.split(":")[0]);
            Encuesta encuestaSeleccionada = servicioEncuestas.obtenerDetallesCompletosEncuesta(idEncuestaSeleccionada);
            if (encuestaSeleccionada != null) {
                responderEncuestaUI(encuestado, encuestaSeleccionada);
            }
        }
    }

    private static void responderEncuestaUI(Usuario encuestado, Encuesta encuesta) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Vas a comenzar la encuesta: '" + encuesta.getNombre() + "'\n" +
                "Descripción: " + encuesta.getDescripcion() + "\n\n¿Deseas continuar?",
                "Confirmar Participación", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<RespuestaUsuario> respuestasRecopiladas = new ArrayList<>();
        List<EncuestaDetallePregunta> preguntas = encuesta.getPreguntasAsociadas();

        // Ordenar preguntas por el campo 'orden' si no vienen ordenadas del DAO
        preguntas.sort(Comparator.comparingInt(EncuestaDetallePregunta::getOrdenEnEncuesta));

        boolean descartado = false;
        for (EncuestaDetallePregunta pregunta : preguntas) {
            String respuesta = JOptionPane.showInputDialog(null,
                    "Pregunta " + pregunta.getOrdenEnEncuesta() + ":\n" + pregunta.getTextoPreguntaMostrable(),
                    "Respondiendo Encuesta...", JOptionPane.QUESTION_MESSAGE);

            if (respuesta == null) { // Usuario canceló
                JOptionPane.showMessageDialog(null, "Encuesta cancelada.", "Cancelado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            RespuestaUsuario r = new RespuestaUsuario(pregunta.getIdEncuestaDetalle(), encuestado.getId_usuario(), respuesta);
            respuestasRecopiladas.add(r);

            // Lógica de descarte
            if (pregunta.isEsPreguntaDescarte() && respuesta.equalsIgnoreCase(pregunta.getCriterioDescarteValor())) {
                JOptionPane.showMessageDialog(null, "Gracias por tu participación. Según tus respuestas, no cumples con el perfil completo para esta encuesta.", "Participación Finalizada", JOptionPane.INFORMATION_MESSAGE);
                descartado = true;
                break; // Termina el bucle de preguntas
            }
        }

        if (servicioParticipacion.registrarRespuestasCompletas(respuestasRecopiladas)) {
             if (!descartado) {
                 JOptionPane.showMessageDialog(null, "¡Gracias! Has completado la encuesta exitosamente.", "Encuesta Finalizada", JOptionPane.INFORMATION_MESSAGE);
             }
        } else {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al guardar tus respuestas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
