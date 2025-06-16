package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.modelo.Usuario;
import SteveJobs.encuestas.modelo.DatosMenuPrincipal;
import SteveJobs.encuestas.modelo.Encuesta; // Necesario para List<Encuesta>
import java.util.List; // Necesario para List

/**
 * Servicio para la interfaz de usuario.
 */
public class ServicioUI {

    private ServicioUsuarios servicioUsuarios;
    private ServicioEncuestas servicioEncuestas;
    // private ServicioAutenticacion servicioAutenticacion; // Si es necesario para roles adicionales

    public ServicioUI(ServicioUsuarios servicioUsuarios, ServicioEncuestas servicioEncuestas) {
        this.servicioUsuarios = servicioUsuarios;
        this.servicioEncuestas = servicioEncuestas;
        // this.servicioAutenticacion = servicioAutenticacion;
    }

    /**
     * Obtiene los datos necesarios para el menú principal.
     * @param usuario El usuario actualmente logueado.
     * @return Datos para el menú principal.
     */
    public DatosMenuPrincipal obtenerDatosMenuPrincipal(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) { // Verificar también el rol
            // Manejar el caso de usuario no logueado, sesión inválida, o rol no definido
            return new DatosMenuPrincipal("Bienvenido Invitado. Por favor, inicie sesión.", 0, false);
        }

        String mensajeBienvenida = "Bienvenido " + usuario.getNombres(); // Usar getNombres()

        // Determinar si es administrador usando el campo 'rol'
        boolean esAdmin = "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol());

        int encuestasPendientes = 0;
        if (!esAdmin) {
            // Si no es admin, obtener encuestas pendientes para este usuario
            if (servicioEncuestas != null) {
                List<Encuesta> encuestasActivasUsuario = servicioEncuestas.obtenerEncuestasActivasParaUsuario(usuario);
                if (encuestasActivasUsuario != null) {
                    encuestasPendientes = encuestasActivasUsuario.size();
                }
            }
        } else {
            // Si es admin, mostrar número total de encuestas activas
            if (servicioEncuestas != null) {
                encuestasPendientes = servicioEncuestas.contarEncuestasActivas();
            }
        }

        return new DatosMenuPrincipal(mensajeBienvenida, encuestasPendientes, esAdmin);
    }

    // Otros metodos del servicio UI podrían ir aquí, por ejemplo:
    // - obtenerDatosParaDashboardAdmin()
    // - obtenerDetallesEncuestaParaVisualizacion(int encuestaId, Usuario usuario)
    // - prepararDatosFormularioRegistro()

}
