package SteveJobs.encuestas.util;

import java.util.EmptyStackException;

/**
 * Una implementación de una Pila (Stack) genérica utilizando una lista enlazada.
 * Esta pila se utilizará para gestionar la navegación entre diferentes menús de la UI.
 * Se proporciona una instancia estática pública para un acceso global sencillo.
 *
 * @param <T> El tipo de datos que almacenará la pila.
 */
public class PilaNavegacion<T> {

    /**
     * Instancia estática única (Singleton) para acceso global a la pila de navegación.
     * Se usa String para identificar los menús o estados de la UI.
     */
    public static final PilaNavegacion<String> instance = new PilaNavegacion<>();

    /**
     * Nodo interno para la lista enlazada que representa la pila.
     * @param <T> El tipo de dato almacenado en el nodo.
     */
    private static class Nodo<T> {
        T data;
        Nodo<T> siguiente;

        Nodo(T data) {
            this.data = data;
            this.siguiente = null;
        }
    }

    private Nodo<T> cima;     // El nodo en la cima de la pila
    private int contador; // Número de elementos en la pila

    /**
     * Constructor para crear una PilaNavegacion vacía.
     */
    public PilaNavegacion() {
        cima = null;
        contador = 0;
    }

    /**
     * Agrega un elemento a la cima de la pila.
     *
     * @param data El elemento a agregar.
     */
    public void push(T data) {
        Nodo<T> nuevoNodo = new Nodo<>(data);
        nuevoNodo.siguiente = cima;
        cima = nuevoNodo;
        contador++;
        System.out.println("PilaNavegacion: Pushed '" + data + "'. Tamaño actual: " + contador); // Log de depuración
    }

    /**
     * Elimina y devuelve el elemento en la cima de la pila.
     *
     * @return El elemento en la cima de la pila.
     * @throws EmptyStackException si la pila está vacía.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = cima.data;
        cima = cima.siguiente;
        contador--;
        System.out.println("PilaNavegacion: Popped '" + data + "'. Tamaño actual: " + contador); // Log de depuración
        return data;
    }

    /**
     * Devuelve el elemento en la cima de la pila sin eliminarlo.
     *
     * @return El elemento en la cima de la pila.
     * @throws EmptyStackException si la pila está vacía.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return cima.data;
    }

    /**
     * Verifica si la pila está vacía.
     *
     * @return {@code true} si la pila está vacía, {@code false} en caso contrario.
     */
    public boolean isEmpty() {
        return contador == 0; // O cima == null
    }

    /**
     * Devuelve el número de elementos en la pila.
     *
     * @return El tamaño de la pila.
     */
    public int size() {
        return contador;
    }
}
