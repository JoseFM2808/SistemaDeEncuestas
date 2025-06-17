package SteveJobs.encuestas.algoritmos;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Calcula estadísticas básicas como promedios y medianas para una lista de números.
 * Las entradas no numéricas o nulas en la lista de datos se ignorarán o podrían causar errores
 * si no se manejan adecuadamente antes de llamar a los métodos de cálculo.
 */
public class CalculadoraEstadisticasBasicas {

    /**
     * Calcula el promedio de una lista de números.
     * Los valores nulos en la lista se ignoran en el cálculo.
     * Si la lista está vacía o solo contiene nulos, devuelve 0.0.
     *
     * @param datos Lista de números (Doubles) para calcular el promedio.
     * @return El promedio de los números en la lista.
     */
    public double calcularPromedio(List<Double> datos) {
        if (datos == null || datos.isEmpty()) {
            return 0.0;
        }
        // Filtrar nulos y luego calcular
        List<Double> datosValidos = datos.stream()
                                       .filter(Objects::nonNull)
                                       .collect(Collectors.toList());

        if (datosValidos.isEmpty()) {
            return 0.0;
        }

        double suma = 0;
        for (Double dato : datosValidos) {
            suma += dato;
        }
        return suma / datosValidos.size();
    }

    /**
     * Calcula la mediana de una lista de números.
     * Los valores nulos en la lista se ignoran. La lista se ordena para el cálculo.
     * Si la lista está vacía o solo contiene nulos después de filtrar, devuelve 0.0.
     *
     * @param datos Lista de números (Doubles) para calcular la mediana.
     * @return La mediana de los números en la lista.
     */
    public double calcularMediana(List<Double> datos) {
        if (datos == null || datos.isEmpty()) {
            return 0.0;
        }

        List<Double> datosOrdenados = datos.stream()
                                           .filter(Objects::nonNull)
                                           .sorted()
                                           .collect(Collectors.toList());

        if (datosOrdenados.isEmpty()) {
            return 0.0;
        }

        int n = datosOrdenados.size();
        if (n % 2 == 1) {
            // Número impar de elementos
            return datosOrdenados.get(n / 2);
        } else {
            // Número par de elementos
            double medio1 = datosOrdenados.get(n / 2 - 1);
            double medio2 = datosOrdenados.get(n / 2);
            return (medio1 + medio2) / 2.0;
        }
    }

    // Se podrían agregar más métodos estadísticos aquí (moda, desviación estándar, etc.)
}
