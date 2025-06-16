package SteveJobs.encuestas.ui;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.modelo.DatosMenuPrincipal;
import SteveJobs.encuestas.servicio.ServicioEncuestas;
import SteveJobs.encuestas.servicio.ServicioUI;
import SteveJobs.encuestas.servicio.ServicioUsuarios;

public class UIMenuPrincipal {

    private ServicioUI servicioUI;
    private Usuario usuarioActual; // Este usuario vendría de la autenticación

    public UIMenuPrincipal(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        // En una aplicación real, ServicioUsuarios y ServicioEncuestas podrían ser inyectados
        // o recuperados de un contexto de aplicación.
        ServicioUsuarios servicioUsuarios = new ServicioUsuarios();
        ServicioEncuestas servicioEncuestas = new ServicioEncuestas();
        this.servicioUI = new ServicioUI(servicioUsuarios, servicioEncuestas);
    }

    public void cargarMenu() {
        DatosMenuPrincipal datosMenu;
        if (usuarioActual == null) {
            System.out.println("No hay usuario logueado. Mostrando menú para invitados.");
            datosMenu = servicioUI.obtenerDatosMenuPrincipal(null);
        } else {
            System.out.println("Cargando menú principal para el usuario: " + usuarioActual.getEmail());
            datosMenu = servicioUI.obtenerDatosMenuPrincipal(usuarioActual);
        }

        System.out.println("-----------------------------------------");
        System.out.println(datosMenu.getMensajeBienvenida());
        System.out.println("-----------------------------------------");

        if (datosMenu.isEsAdministrador()) {
            System.out.println("Opciones de Administrador:");
            System.out.println("1. Gestionar Usuarios");
            System.out.println("2. Gestionar Encuestas");
            System.out.println("3. Ver Resultados Globales");
            // ... más opciones de admin
        } else {
            // Si es invitado (usuarioActual == null), también caería aquí pero el mensaje de bienvenida es diferente.
            if (usuarioActual != null) { // Solo mostrar opciones de encuestado si no es invitado
                System.out.println("Opciones de Encuestado:");
                System.out.println("1. Ver Encuestas Disponibles (" + datosMenu.getNumeroEncuestasPendientes() + " pendientes)");
                System.out.println("2. Ver Mis Resultados");
                // ... más opciones de encuestado
            } else {
                System.out.println("Opciones de Invitado:");
                System.out.println("1. Iniciar Sesión");
                System.out.println("2. Registrarse");
            }
        }
        System.out.println("0. Salir");
        System.out.println("-----------------------------------------");
        // Aquí iría la lógica para leer la opción del usuario y navegar a otras UIs
    }

    // Método principal o de demostración (temporal)
    public static void main(String[] args) {
        // Simulación: Crear un usuario administrador de prueba
        Usuario adminUser = new Usuario();
        adminUser.setId_usuario(1); // Corregido para usar el nombre de setter correcto si fuera necesario (setId_usuario)
        adminUser.setNombres("Admin User"); // Corregido para usar getNombres() en ServicioUI
        adminUser.setEmail("admin@example.com");
        adminUser.setRol("ADMINISTRADOR"); // Establecer el rol

        // Simulación: Crear un usuario encuestado de prueba
        Usuario regularUser = new Usuario();
        regularUser.setId_usuario(2);
        regularUser.setNombres("Regular User"); // Corregido para usar getNombres() en ServicioUI
        regularUser.setEmail("user@example.com");
        regularUser.setRol("ENCUESTADO"); // Establecer el rol

        System.out.println("DEMOSTRACIÓN DEL MENÚ PRINCIPAL (ADMIN)");
        UIMenuPrincipal menuAdmin = new UIMenuPrincipal(adminUser);
        menuAdmin.cargarMenu();

        System.out.println("\nDEMOSTRACIÓN DEL MENÚ PRINCIPAL (ENCUESTADO)");
        UIMenuPrincipal menuEncuestado = new UIMenuPrincipal(regularUser);
        menuEncuestado.cargarMenu();

        System.out.println("\nDEMOSTRACIÓN DEL MENÚ PRINCIPAL (INVITADO)");
        UIMenuPrincipal menuInvitado = new UIMenuPrincipal(null);
        menuInvitado.cargarMenu();
    }
}
