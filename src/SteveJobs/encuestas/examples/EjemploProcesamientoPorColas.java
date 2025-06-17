package SteveJobs.encuestas.examples;

import java.util.NoSuchElementException;

/**
 * Ejemplo de implementación de dos colas utilizando un único array.
 * Esta estructura puede ser útil para gestionar diferentes tipos de tareas o datos
 * que necesitan ser procesados de forma independiente pero comparten un recurso de almacenamiento limitado.
 *
 * <p><b>Aplicación en el Sistema de Encuestas:</b>
 * Este sistema de dos colas podría utilizarse para gestionar tareas asíncronas dentro de la aplicación de encuestas:
 * <ul>
 *   <li><b>Cola 1 ("Nuevas Encuestas a Validar"):</b> Cuando un administrador crea o modifica significativamente
 *       una encuesta, esta podría ser encolada aquí. Un proceso en segundo plano (worker thread) podría
 *       desencolar estas encuestas para realizar validaciones complejas (e.g., consistencia de preguntas,
 *       adecuación del perfil) antes de que puedan ser activadas.</li>
 *   <li><b>Cola 2 ("Reportes de Resultados a Generar"):</b> Cuando una encuesta se cierra o un administrador
 *       solicita un reporte complejo, la tarea de generación de dicho reporte (que podría ser intensiva
 *       en recursos) se encola aquí. Otro proceso en segundo plano procesaría esta cola, generando los
 *       reportes y notificando al administrador cuando estén listos, sin bloquear la interfaz de usuario.</li>
 * </ul>
 * Esto ayuda a desacoplar tareas y mejorar la responsividad del sistema principal.
 * </p>
 */
public class EjemploProcesamientoPorColas {

    private Object[] array;
    private int capacidad;

    // Atributos para la Cola 1 (crece desde el inicio del array hacia la derecha)
    private int frente1;    // Índice del primer elemento de la Cola 1
    private int final1;     // Índice del último elemento de la Cola 1
    private int tamano1;    // Número actual de elementos en la Cola 1
    private int limiteCola1; // Último índice disponible para la Cola 1

    // Atributos para la Cola 2 (crece desde el final del array hacia la izquierda)
    // Corrección: Para simplificar la lógica circular, la Cola 2 también crecerá "hacia la derecha"
    // dentro de su partición. Los índices frente2 y final2 serán relativos al inicio de su partición.
    private int baseCola2;   // Índice de inicio de la partición de Cola 2
    private int frente2;   // Índice del primer elemento de la Cola 2 (absoluto en el array)
    private int final2;    // Índice del último elemento de la Cola 2 (absoluto en el array)
    private int tamano2;   // Número actual de elementos en la Cola 2
    private int capacidadCola2;

    /**
     * Constructor para inicializar las dos colas dentro de un único array.
     *
     * @param capacidadTotal La capacidad total del array que será compartida por las dos colas.
     *                       Debe ser al menos 2 para que cada cola tenga espacio.
     * @throws IllegalArgumentException si la capacidadTotal es menor que 2.
     */
    public EjemploProcesamientoPorColas(int capacidadTotal) {
        if (capacidadTotal < 2) {
            throw new IllegalArgumentException("La capacidad total debe ser al menos 2.");
        }
        capacidad = capacidadTotal;
        array = new Object[capacidad];

        // Cola 1 ocupa la primera mitad (aproximadamente)
        limiteCola1 = (capacidad / 2) - 1; // último índice de la partición de cola1
        frente1 = 0;
        final1 = -1; // Indica que la cola está vacía
        tamano1 = 0;

        // Cola 2 ocupa la segunda mitad
        baseCola2 = limiteCola1 + 1; // primer índice de la partición de cola2
        capacidadCola2 = capacidad - baseCola2;
        frente2 = baseCola2;
        final2 = baseCola2 -1; // Indica que la cola está vacía
        tamano2 = 0;
    }

    // --- Métodos para la Cola 1 ---

    /**
     * Agrega un elemento al final de la Cola 1.
     *
     * @param item El elemento a agregar.
     * @return {@code true} si el elemento fue agregado exitosamente.
     * @throws IllegalStateException si la Cola 1 está llena.
     */
    public boolean enqueueCola1(Object item) {
        if (isCola1Full()) {
            throw new IllegalStateException("Cola 1 está llena. No se puede agregar el elemento: " + item);
        }
        final1 = (final1 + 1) % (limiteCola1 + 1); // Manejo circular dentro de la partición de Cola 1
        array[final1] = item;
        tamano1++;
        return true;
    }

    /**
     * Remueve y retorna el elemento al frente de la Cola 1.
     *
     * @return El elemento al frente de la Cola 1.
     * @throws NoSuchElementException si la Cola 1 está vacía.
     */
    public Object dequeueCola1() {
        if (isCola1Empty()) {
            throw new NoSuchElementException("Cola 1 está vacía. No se puede remover ningún elemento.");
        }
        Object item = array[frente1];
        array[frente1] = null; // Opcional: limpiar la referencia para el GC
        frente1 = (frente1 + 1) % (limiteCola1 + 1); // Manejo circular
        tamano1--;
        return item;
    }

    /**
     * Retorna el elemento al frente de la Cola 1 sin removerlo.
     *
     * @return El elemento al frente de la Cola 1.
     * @throws NoSuchElementException si la Cola 1 está vacía.
     */
    public Object peekCola1() {
        if (isCola1Empty()) {
            throw new NoSuchElementException("Cola 1 está vacía.");
        }
        return array[frente1];
    }

    /**
     * Verifica si la Cola 1 está vacía.
     * @return {@code true} si la Cola 1 está vacía, {@code false} en caso contrario.
     */
    public boolean isCola1Empty() {
        return tamano1 == 0;
    }

    /**
     * Verifica si la Cola 1 está llena.
     * @return {@code true} si la Cola 1 está llena, {@code false} en caso contrario.
     */
    public boolean isCola1Full() {
        return tamano1 == (limiteCola1 + 1);
    }

    /**
     * Retorna el tamaño actual de la Cola 1.
     * @return El número de elementos en la Cola 1.
     */
    public int getTamanoCola1() {
        return tamano1;
    }

    // --- Métodos para la Cola 2 ---

    /**
     * Agrega un elemento al final de la Cola 2.
     *
     * @param item El elemento a agregar.
     * @return {@code true} si el elemento fue agregado exitosamente.
     * @throws IllegalStateException si la Cola 2 está llena.
     */
    public boolean enqueueCola2(Object item) {
        if (isCola2Full()) {
            throw new IllegalStateException("Cola 2 está llena. No se puede agregar el elemento: " + item);
        }
        // El cálculo de final2 debe ser relativo al inicio de su partición y luego convertido a absoluto
        if (isCola2Empty()) { // Si está vacía, frente y final apuntan al inicio de la partición
            final2 = baseCola2;
        } else {
            final2 = baseCola2 + ((final2 - baseCola2 + 1) % capacidadCola2);
        }
        array[final2] = item;
        tamano2++;
        return true;
    }

    /**
     * Remueve y retorna el elemento al frente de la Cola 2.
     *
     * @return El elemento al frente de la Cola 2.
     * @throws NoSuchElementException si la Cola 2 está vacía.
     */
    public Object dequeueCola2() {
        if (isCola2Empty()) {
            throw new NoSuchElementException("Cola 2 está vacía. No se puede remover ningún elemento.");
        }
        Object item = array[frente2];
        array[frente2] = null; // Opcional: limpiar la referencia para el GC
        // El cálculo de frente2 debe ser relativo al inicio de su partición y luego convertido a absoluto
        frente2 = baseCola2 + ((frente2 - baseCola2 + 1) % capacidadCola2);
        tamano2--;
        // Si la cola se vacía, resetear frente y final para la próxima inserción
        if (isCola2Empty()) {
            frente2 = baseCola2;
            final2 = baseCola2 - 1;
        }
        return item;
    }

    /**
     * Retorna el elemento al frente de la Cola 2 sin removerlo.
     *
     * @return El elemento al frente de la Cola 2.
     * @throws NoSuchElementException si la Cola 2 está vacía.
     */
    public Object peekCola2() {
        if (isCola2Empty()) {
            throw new NoSuchElementException("Cola 2 está vacía.");
        }
        return array[frente2];
    }

    /**
     * Verifica si la Cola 2 está vacía.
     * @return {@code true} si la Cola 2 está vacía, {@code false} en caso contrario.
     */
    public boolean isCola2Empty() {
        return tamano2 == 0;
    }

    /**
     * Verifica si la Cola 2 está llena.
     * @return {@code true} si la Cola 2 está llena, {@code false} en caso contrario.
     */
    public boolean isCola2Full() {
        return tamano2 == capacidadCola2;
    }

    /**
     * Retorna el tamaño actual de la Cola 2.
     * @return El número de elementos en la Cola 2.
     */
    public int getTamanoCola2() {
        return tamano2;
    }

    public static void main(String[] args) {
        System.out.println("Ejemplo de Procesamiento por Colas (Dos Colas en un Array)");
        EjemploProcesamientoPorColas colas = new EjemploProcesamientoPorColas(10); // Capacidad: C1=5, C2=5

        System.out.println("\n--- Encolando en Cola 1 (Nuevas Encuestas a Validar) ---");
        try {
            colas.enqueueCola1("Encuesta Marketing Q3"); System.out.println("Encolado en Cola1: Encuesta Marketing Q3");
            colas.enqueueCola1("Encuesta Satisfacción Cliente"); System.out.println("Encolado en Cola1: Encuesta Satisfacción Cliente");
            colas.enqueueCola1("Encuesta Clima Laboral"); System.out.println("Encolado en Cola1: Encuesta Clima Laboral");
        } catch (IllegalStateException e) { System.err.println(e.getMessage()); }

        System.out.println("\n--- Encolando en Cola 2 (Reportes de Resultados a Generar) ---");
        try {
            colas.enqueueCola2("Reporte Encuesta XYZ (Mayo)"); System.out.println("Encolado en Cola2: Reporte Encuesta XYZ (Mayo)");
            colas.enqueueCola2("Reporte General Anual"); System.out.println("Encolado en Cola2: Reporte General Anual");
        } catch (IllegalStateException e) { System.err.println(e.getMessage()); }

        System.out.println("\nCola 1 llena? " + colas.isCola1Full() + " (Tamaño: " + colas.getTamanoCola1() + ")");
        System.out.println("Cola 2 llena? " + colas.isCola2Full() + " (Tamaño: " + colas.getTamanoCola2() + ")");

        System.out.println("\n--- Procesando Cola 1 ---");
        try {
            System.out.println("Peek Cola 1: " + colas.peekCola1());
            System.out.println("Dequeue Cola 1: " + colas.dequeueCola1());
            System.out.println("Dequeue Cola 1: " + colas.dequeueCola1());
        } catch (NoSuchElementException e) { System.err.println(e.getMessage()); }

        System.out.println("\n--- Procesando Cola 2 ---");
        try {
            System.out.println("Peek Cola 2: " + colas.peekCola2());
            System.out.println("Dequeue Cola 2: " + colas.dequeueCola2());
        } catch (NoSuchElementException e) { System.err.println(e.getMessage()); }

        System.out.println("\nCola 1 vacía? " + colas.isCola1Empty() + " (Tamaño: " + colas.getTamanoCola1() + ")");
        System.out.println("Cola 2 vacía? " + colas.isCola2Empty() + " (Tamaño: " + colas.getTamanoCola2() + ")");

        System.out.println("\n--- Intentando llenar Cola 1 ---");
        try {
            colas.enqueueCola1("Encuesta Producto A"); System.out.println("Encolado en Cola1: Encuesta Producto A");
            colas.enqueueCola1("Encuesta Producto B"); System.out.println("Encolado en Cola1: Encuesta Producto B");
            colas.enqueueCola1("Encuesta Producto C"); System.out.println("Encolado en Cola1: Encuesta Producto C"); // Debería llenar Cola1 si capacidad es 5
            colas.enqueueCola1("Encuesta Producto D (Extra)"); System.out.println("Encolado en Cola1: Encuesta Producto D (Extra)"); // Debería fallar
        } catch (IllegalStateException e) {
            System.err.println("Error al encolar en Cola1: " + e.getMessage());
        }
        System.out.println("Cola 1 llena? " + colas.isCola1Full() + " (Tamaño: " + colas.getTamanoCola1() + ")");

        System.out.println("\n--- Vaciando Cola 1 ---");
        while(!colas.isCola1Empty()){
            System.out.println("Dequeue Cola 1: " + colas.dequeueCola1());
        }
        System.out.println("Cola 1 vacía? " + colas.isCola1Empty());
        try {
            colas.dequeueCola1(); // Intentar desencolar de cola vacía
        } catch (NoSuchElementException e) {
            System.err.println("Error al desencolar de Cola1: " + e.getMessage());
        }

        System.out.println("\n--- Demostración de circularidad en Cola 2 (si capacidad lo permite) ---");
        // Para ver circularidad, llenamos y vaciamos parcialmente Cola2
        EjemploProcesamientoPorColas colasCirculares = new EjemploProcesamientoPorColas(6); // C1=3, C2=3
        System.out.println("Encolando en C2: R1, R2, R3");
        colasCirculares.enqueueCola2("R1");
        colasCirculares.enqueueCola2("R2");
        colasCirculares.enqueueCola2("R3");
        System.out.println("C2 llena: " + colasCirculares.isCola2Full());
        System.out.println("Desencolando de C2: " + colasCirculares.dequeueCola2()); // Sale R1, frente2 avanza
        System.out.println("Encolando en C2: R4 (debería ir al inicio de la partición de C2 si es circular)");
        colasCirculares.enqueueCola2("R4"); // Debería ocupar el espacio de R1
        System.out.println("Contenido de C2 después de circularidad (esperado R2, R3, R4):");
        while(!colasCirculares.isCola2Empty()){
            System.out.println("Dequeue Cola 2: " + colasCirculares.dequeueCola2());
        }
    }
}
