package SteveJobs.encuestas.algoritmos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Detecta patrones de frecuencia en una lista de respuestas.
 * Principalmente útil para identificar las respuestas más comunes
 * en preguntas categóricas o de opción múltiple.
 */
public class DetectorPatronesFrecuencia {

    /**
     * Calcula la frecuencia de cada respuesta en una lista de datos.
     * Los valores nulos en la lista de datos se pueden incluir en el conteo
     * si se desea rastrear la frecuencia de "no respuesta" o se pueden filtrar.
     * Por defecto, este método los contará como una categoría más si están presentes.
     *
     * @param <T> El tipo de dato de las respuestas (e.g., String, Integer, Enum).
     * @param respuestas Lista de respuestas a analizar.
     * @return Un {@link Map} donde la clave es la respuesta y el valor es su frecuencia (conteo).
     *         Retorna un mapa vacío si la lista de entrada es nula o vacía.
     */
    public <T> Map<T, Integer> calcularFrecuenciaRespuestas(List<T> respuestas) {
        Map<T, Integer> frecuencias = new HashMap<>();
        if (respuestas == null || respuestas.isEmpty()) {
            return frecuencias;
        }

        for (T respuesta : respuestas) {
            // Si se quisiera ignorar los nulos:
            // if (respuesta == null) continue;

            frecuencias.put(respuesta, frecuencias.getOrDefault(respuesta, 0) + 1);
        }
        return frecuencias;
    }

    /**
     * Encuentra la respuesta (o respuestas) más frecuente(s) en una lista.
     *
     * @param <T> El tipo de dato de las respuestas.
     * @param respuestas Lista de respuestas.
     * @return Un {@link Map} que contiene la(s) respuesta(s) más frecuente(s) y su frecuencia.
     *         Si hay múltiples respuestas con la misma frecuencia máxima, todas se incluyen.
     *         Retorna un mapa vacío si la lista de entrada es nula, vacía, o no se encuentran patrones.
     */
    public <T> Map<T, Integer> obtenerRespuestasMasFrecuentes(List<T> respuestas) {
        Map<T, Integer> frecuencias = calcularFrecuenciaRespuestas(respuestas);
        Map<T, Integer> masFrecuentes = new HashMap<>();

        if (frecuencias.isEmpty()) {
            return masFrecuentes;
        }

        int maxFrecuencia = 0;
        // Primera pasada para encontrar la frecuencia máxima
        for (Integer frecuencia : frecuencias.values()) {
            if (frecuencia > maxFrecuencia) {
                maxFrecuencia = frecuencia;
            }
        }

        // Segunda pasada para añadir todas las respuestas con la frecuencia máxima
        if (maxFrecuencia > 0) { // Asegurarse de que haya al menos una respuesta
            for (Map.Entry<T, Integer> entry : frecuencias.entrySet()) {
                if (entry.getValue() == maxFrecuencia) {
                    masFrecuentes.put(entry.getKey(), maxFrecuencia);
                }
            }
        }
        return masFrecuentes;
    }

    // Se podrían añadir otros métodos para detectar otros tipos de patrones,
    // como secuencias, correlaciones (requeriría múltiples listas o datos estructurados), etc.
}
