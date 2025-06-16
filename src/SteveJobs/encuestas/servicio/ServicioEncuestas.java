package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.dao.EncuestaDAO;
import SteveJobs.encuestas.dao.EncuestaDetallePreguntaDAO;
import SteveJobs.encuestas.dao.PreguntaBancoDAO;
import SteveJobs.encuestas.dao.TipoPreguntaDAO;
import SteveJobs.encuestas.dao.ClasificacionPreguntaDAO;
import SteveJobs.encuestas.dao.RespuestaUsuarioDAO; // Importar RespuestaUsuarioDAO
import SteveJobs.encuestas.modelo.Encuesta;
import SteveJobs.encuestas.modelo.EncuestaDetallePregunta;
import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.modelo.TipoPregunta;
import SteveJobs.encuestas.modelo.ClasificacionPregunta;
import SteveJobs.encuestas.modelo.Usuario;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;


public class ServicioEncuestas {
    private EncuestaDAO encuestaDAO;
    private EncuestaDetallePreguntaDAO encuestaDetalleDAO;
    private PreguntaBancoDAO preguntaBancoDAO;
    private TipoPreguntaDAO tipoPreguntaDAO;
    private ClasificacionPreguntaDAO clasificacionPreguntaDAO;
    private RespuestaUsuarioDAO respuestaUsuarioDAO; // Añadir RespuestaUsuarioDAO

    public ServicioEncuestas() {
        this.encuestaDAO = new EncuestaDAO();
        this.encuestaDetalleDAO = new EncuestaDetallePreguntaDAO();
        this.preguntaBancoDAO = new PreguntaBancoDAO();
        this.tipoPreguntaDAO = new TipoPreguntaDAO();
        this.clasificacionPreguntaDAO = new ClasificacionPreguntaDAO();
        this.respuestaUsuarioDAO = new RespuestaUsuarioDAO(); // Inicializar RespuestaUsuarioDAO
    }

    /**
     * Registra una nueva encuesta en el sistema.
     *
     * Realiza validaciones sobre los datos de entrada antes de proceder con la creación.
     * La encuesta se crea inicialmente en estado "Borrador" y se le asigna una fecha de creación.
     *
     * @param nombre El nombre de la encuesta. No debe ser nulo ni vacío.
     * @param descripcion Una descripción detallada de la encuesta. Puede ser nula o vacía.
     * @param fechaInicio La fecha y hora de inicio de vigencia de la encuesta. No debe ser nula.
     * @param fechaFin La fecha y hora de fin de vigencia de la encuesta. No debe ser nula y debe ser posterior o igual a {@code fechaInicio}.
     * @param publicoObjetivo La cantidad estimada de participantes para la encuesta. No debe ser un número negativo.
     * @param definicionPerfil Una descripción del perfil del público objetivo de la encuesta. Puede ser nula o vacía.
     * @param idAdmin El ID del usuario administrador que crea la encuesta.
     * @return El ID de la encuesta recién creada si el registro es exitoso. Retorna -1 si ocurre algún error
     *         durante la validación (e.g., nombre vacío, fechas inválidas, público objetivo negativo) o si falla la
     *         creación en la capa DAO.
     */
    public int registrarNuevaEncuesta(String nombre, String descripcion, Timestamp fechaInicio, Timestamp fechaFin, int publicoObjetivo, String definicionPerfil, int idAdmin) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("Servicio: El nombre de la encuesta es obligatorio.");
            return -1;
        }
        if (fechaInicio == null || fechaFin == null) {
            System.err.println("Servicio: Las fechas de inicio y fin son obligatorias.");
            return -1;
        }
        if (fechaFin.before(fechaInicio)) {
            System.err.println("Servicio: La fecha de fin no puede ser anterior a la fecha de inicio.");
            return -1;
        }
        if (publicoObjetivo < 0) {
            System.err.println("Servicio: Público objetivo no puede ser negativo.");
            return -1;
        }

        Encuesta nuevaEncuesta = new Encuesta(nombre.trim(), descripcion, fechaInicio, fechaFin, publicoObjetivo, definicionPerfil, idAdmin);
        nuevaEncuesta.setEstado("Borrador");
        nuevaEncuesta.setFechaCreacionEncuesta(new Timestamp(System.currentTimeMillis()));
        return encuestaDAO.crearEncuesta(nuevaEncuesta);
    }

    private Timestamp convertirStringATimestamp(String fechaStr) {

        if (fechaStr == null || fechaStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat dateFormat;
            if (fechaStr.trim().length() > 10) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            } else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            dateFormat.setLenient(false);
            Date parsedDate = dateFormat.parse(fechaStr.trim());
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            System.err.println("Error al parsear fecha: " + fechaStr + " - " + e.getMessage());
            return null;
        }
    }
    
    public List<Encuesta> obtenerTodasLasEncuestas() {
        return encuestaDAO.obtenerTodasLasEncuestas();
    }


    public boolean modificarMetadatosEncuesta(int idEncuesta, String nuevoNombre, String nuevaDescripcion, Timestamp nuevaFechaInicio, Timestamp nuevaFechaFin, int nuevoPublicoObj, String nuevoPerfilDef) {
        Encuesta encuesta = encuestaDAO.obtenerEncuestaPorId(idEncuesta);
        if (encuesta == null) {
            System.err.println("Servicio: Encuesta con ID " + idEncuesta + " no encontrada para modificar.");
            return false;
        }

        boolean modificado = false;

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            if (!nuevoNombre.trim().equals(encuesta.getNombreEncuesta())) {
                encuesta.setNombreEncuesta(nuevoNombre.trim());
                modificado = true;
            }
        }
        if (nuevaDescripcion != null) {
            if (!nuevaDescripcion.equals(encuesta.getDescripcion())) {
                encuesta.setDescripcion(nuevaDescripcion);
                modificado = true;
            }
        }

        if (nuevaFechaInicio != null) {
            if (!nuevaFechaInicio.equals(encuesta.getFechaInicioVigencia())) {
                encuesta.setFechaInicioVigencia(nuevaFechaInicio);
                modificado = true;
            }
        }
        if (nuevaFechaFin != null) {
            if (!nuevaFechaFin.equals(encuesta.getFechaFinVigencia())) {
                encuesta.setFechaFinVigencia(nuevaFechaFin);
                modificado = true;
            }
        }

        if (encuesta.getFechaInicioVigencia() == null || encuesta.getFechaFinVigencia() == null) {
             System.err.println("Servicio: Las fechas de inicio y fin no pueden ser nulas después de la modificación si se intentó establecer una.");
             return false;
        }
        
        if (encuesta.getFechaFinVigencia().before(encuesta.getFechaInicioVigencia())) {
            System.err.println("Servicio: La fecha de fin no puede ser anterior a la fecha de inicio.");
            return false;
        }

        if (nuevoPublicoObj >= 0) {
            if (nuevoPublicoObj != encuesta.getPublicoObjetivoCantidad()) {
                encuesta.setPublicoObjetivoCantidad(nuevoPublicoObj);
                modificado = true;
            }
        } else {
             System.err.println("Servicio: Público objetivo debe ser un número no negativo.");
             return false;
        }
        
        if (nuevoPerfilDef != null) {
            if(!nuevoPerfilDef.equals(encuesta.getDefinicionPerfil())) {
                encuesta.setDefinicionPerfil(nuevoPerfilDef);
                modificado = true;
            }
        }
        
        if (modificado) {
            return encuestaDAO.actualizarEncuesta(encuesta);
        }
        return true; 
    }

    /**
     * Cambia el estado de una encuesta existente.
     *
     * Antes de cambiar el estado a "Activa", se realizan varias validaciones:
     * 1. La encuesta debe tener exactamente 12 preguntas asociadas.
     * 2. La encuesta debe tener una definición de perfil de público objetivo no vacía.
     * 3. La fecha de fin de vigencia de la encuesta no debe haber pasado.
     *
     * Si alguna de estas condiciones no se cumple al intentar activar, el cambio de estado fallará
     * y se registrará un mensaje de error. Para otros cambios de estado, no se aplican estas validaciones específicas.
     *
     * @param idEncuesta El ID de la encuesta cuyo estado se desea cambiar.
     * @param nuevoEstado El nuevo estado para la encuesta (e.g., "Activa", "Borrador", "Cerrada").
     * @return {@code true} si el estado se actualizó correctamente en la base de datos,
     *         {@code false} si la encuesta no se encontró, si las validaciones para activar no se cumplen,
     *         o si falla la actualización en el DAO.
     */
    public boolean cambiarEstadoEncuesta(int idEncuesta, String nuevoEstado) {
        Encuesta encuesta = encuestaDAO.obtenerEncuestaPorId(idEncuesta);
        if (encuesta == null) {
            System.err.println("Servicio: Encuesta con ID " + idEncuesta + " no encontrada.");
            return false;
        }

        if ("Activa".equalsIgnoreCase(nuevoEstado)) {
            if (encuestaDetalleDAO.contarPreguntasEnEncuesta(idEncuesta) != 12) {
                System.err.println("Servicio: No se puede activar. La encuesta debe tener exactamente 12 preguntas asociadas.");
                return false;
            }
            if (encuesta.getDefinicionPerfil() == null || encuesta.getDefinicionPerfil().trim().isEmpty()){
                 System.err.println("Servicio: No se puede activar. La encuesta debe tener un perfil definido.");
                return false;
            }
            if (encuesta.getFechaFinVigencia().before(new Timestamp(System.currentTimeMillis()))){
                System.err.println("Servicio: No se puede activar. La fecha de fin de la encuesta ya pasó.");
                return false;
            }
        }

        encuesta.setEstado(nuevoEstado);
        return encuestaDAO.actualizarEstadoEncuesta(idEncuesta, nuevoEstado);
    }

    public boolean eliminarEncuesta(int idEncuesta) {

        System.out.println("Servicio: Intentando eliminar preguntas asociadas a encuesta ID " + idEncuesta);
        boolean preguntasEliminadas = encuestaDetalleDAO.eliminarTodasPreguntasDeEncuesta(idEncuesta);

        System.out.println("Servicio: Eliminando encuesta ID " + idEncuesta);
        return encuestaDAO.eliminarEncuesta(idEncuesta);
    }

    /**
     * Obtiene una lista de encuestas activas que un usuario específico aún no ha respondido.
     *
     * El filtrado de encuestas se realiza bajo los siguientes criterios:
     * 1. La encuesta debe estar en estado "Activa".
     * 2. El usuario no debe haber respondido previamente a la encuesta.
     * 3. (Simplificado) No se realiza un filtrado complejo por perfil del usuario contra {@code Encuesta.definicionPerfil}.
     *    Cualquier encuesta activa y no respondida se considera disponible si {@code definicionPerfil} es general.
     *    Una futura mejora podría implicar un matching más detallado si el perfil del usuario y de la encuesta
     *    tuvieran campos estructurados para ello (e.g., roles, etiquetas).
     *
     * @param usuario El {@link Usuario} para el cual se buscan encuestas disponibles.
     * @return Una lista de objetos {@link Encuesta} que están activas y pendientes de respuesta por el usuario.
     *         Puede devolver una lista vacía si no hay encuestas disponibles o si el usuario es nulo.
     */
    public List<Encuesta> obtenerEncuestasActivasParaUsuario(Usuario usuario) {
        if (usuario == null) {
            System.err.println("ServicioEncuestas: Usuario nulo, no se pueden obtener encuestas activas.");
            return new ArrayList<>();
        }

        List<Encuesta> todasLasEncuestas = encuestaDAO.obtenerTodasLasEncuestas();
        List<Encuesta> encuestasDisponibles = new ArrayList<>();

        for (Encuesta encuesta : todasLasEncuestas) {
            if ("Activa".equalsIgnoreCase(encuesta.getEstado())) {
                // Verificar si el usuario ya respondió esta encuesta
                boolean haRespondido = respuestaUsuarioDAO.haRespondidoEncuesta(usuario.getId_usuario(), encuesta.getIdEncuesta());
                if (!haRespondido) {
                    // Lógica de filtrado por perfil (simplificada):
                    // Si definicionPerfil no es restrictiva (ej. vacía o un placeholder general), se añade.
                    // O si hubiera un campo 'rolTarget' en Encuesta y coincidiera con usuario.getRol().
                    // Por ahora, si es activa y no respondida, se considera disponible.
                    // Una implementación más compleja de perfiles necesitaría más estructura en los modelos.
                    // System.out.println("Servicio: Considerando encuesta ID " + encuesta.getIdEncuesta() + " para usuario ID " + usuario.getId_usuario() + ". Perfil: " + encuesta.getDefinicionPerfil());
                    encuestasDisponibles.add(encuesta);
                }
            }
        }
        System.out.println("Servicio: Encontradas " + encuestasDisponibles.size() + " encuestas activas y no respondidas para el usuario ID " + usuario.getId_usuario());
        return encuestasDisponibles;
    }

    public int contarEncuestasActivas() {
        List<Encuesta> todasEncuestas = encuestaDAO.obtenerTodasLasEncuestas();
        int contador = 0;
        if (todasEncuestas != null) {
            for (Encuesta e : todasEncuestas) {
                if ("Activa".equalsIgnoreCase(e.getEstado())) {
                    contador++;
                }
            }
        }
        return contador;
    }

    /**
     * Asocia una pregunta existente del banco de preguntas a una encuesta específica.
     *
     * Verifica que la encuesta no exceda el límite de 12 preguntas y que la pregunta
     * del banco exista antes de crear la asociación.
     *
     * @param idEncuesta El ID de la encuesta a la que se asociará la pregunta.
     * @param idPreguntaBanco El ID de la pregunta en el banco de preguntas.
     * @param orden El orden en que esta pregunta aparecerá en la encuesta.
     * @param esDescarte {@code true} si la pregunta es de descarte, {@code false} en caso contrario.
     * @param criterioDescarte El criterio de descarte si {@code esDescarte} es {@code true}. Puede ser {@code null} o vacío si no es de descarte.
     * @return {@code true} si la pregunta se asoció exitosamente, {@code false} si se alcanzó el límite de preguntas,
     *         la pregunta del banco no existe, o falla la operación en el DAO.
     */
    public boolean asociarPreguntaDelBancoAEncuesta(int idEncuesta, int idPreguntaBanco, int orden, boolean esDescarte, String criterioDescarte) {
        if (encuestaDetalleDAO.contarPreguntasEnEncuesta(idEncuesta) >= 12) {
            System.err.println("Servicio: La encuesta ID " + idEncuesta + " ya tiene 12 preguntas.");
            return false;
        }
        PreguntaBancoDAO pbDao = new PreguntaBancoDAO();
        if (pbDao.obtenerPreguntaPorId(idPreguntaBanco) == null) {
             System.err.println("Servicio: Pregunta del banco con ID " + idPreguntaBanco + " no existe.");
             return false;
        }

        EncuestaDetallePregunta detalle = new EncuestaDetallePregunta(idEncuesta, idPreguntaBanco, orden, esDescarte, criterioDescarte);
        return encuestaDetalleDAO.agregarPreguntaAEncuesta(detalle);
    }

    public boolean agregarPreguntaNuevaAEncuesta(int idEncuesta, String textoPregunta, String nombreTipo, String nombreClasificacion, int orden, boolean esDescarte, String criterioDescarte) {
        if (encuestaDetalleDAO.contarPreguntasEnEncuesta(idEncuesta) >= 12) {
            System.err.println("Servicio: La encuesta ID " + idEncuesta + " ya tiene 12 preguntas.");
            return false;
        }
        TipoPregunta tipo = tipoPreguntaDAO.obtenerTipoPreguntaPorNombre(nombreTipo);
        ClasificacionPregunta clasif = null;
        if(nombreClasificacion != null && !nombreClasificacion.trim().isEmpty()){
             clasif = clasificacionPreguntaDAO.obtenerClasificacionPorNombre(nombreClasificacion);
        }


        if (tipo == null) {
            System.err.println("Servicio: Tipo de pregunta '" + nombreTipo + "' no válido.");
            return false;
        }

        EncuestaDetallePregunta detalle = new EncuestaDetallePregunta(
            idEncuesta,
            textoPregunta,
            tipo.getIdTipoPregunta(),
            (clasif != null ? clasif.getIdClasificacion() : null),
            orden,
            esDescarte,
            criterioDescarte
        );
        return encuestaDetalleDAO.agregarPreguntaAEncuesta(detalle);
    }

    /**
     * Marca una pregunta específica dentro de una encuesta como pregunta de descarte.
     *
     * La pregunta de descarte se identifica por su ID de detalle en la encuesta.
     * Se requiere un criterio de descarte no vacío.
     *
     * @param idEncuestaDetalle El ID del detalle de la pregunta en la encuesta (no el ID de la pregunta en el banco).
     * @param criterioDescarte El valor o criterio que define el descarte. No debe ser nulo ni vacío.
     * @return {@code true} si la pregunta se marcó como descarte exitosamente, {@code false} si la pregunta no se encuentra,
     *         el criterio es inválido, o falla la actualización en el DAO.
     */
    public boolean marcarPreguntaComoDescarte(int idEncuestaDetalle, String criterioDescarte) {
        EncuestaDetallePregunta detalle = encuestaDetalleDAO.obtenerPreguntaDetallePorId(idEncuestaDetalle);
        if (detalle == null) {
            System.err.println("Servicio: Pregunta de encuesta (ID detalle: "+idEncuestaDetalle+") no encontrada para marcar como descarte.");
            return false;
        }
        if (criterioDescarte == null || criterioDescarte.trim().isEmpty()){
            System.err.println("Servicio: El criterio de descarte no puede estar vacío al marcar como descarte.");

        }
        detalle.setEsPreguntaDescarte(true);
        detalle.setCriterioDescarteValor(criterioDescarte);
        return encuestaDetalleDAO.actualizarDetallePregunta(detalle);
    }

    /**
     * Desmarca una pregunta específica dentro de una encuesta como pregunta de descarte.
     *
     * La pregunta se identifica por su ID de detalle en la encuesta.
     * El criterio de descarte se establece como {@code null}.
     *
     * @param idEncuestaDetalle El ID del detalle de la pregunta en la encuesta.
     * @return {@code true} si la pregunta se desmarcó exitosamente, {@code false} si la pregunta no se encuentra
     *         o falla la actualización en el DAO.
     */
    public boolean desmarcarPreguntaComoDescarte(int idEncuestaDetalle) {
        EncuestaDetallePregunta detalle = encuestaDetalleDAO.obtenerPreguntaDetallePorId(idEncuestaDetalle);
        if (detalle == null) {
            System.err.println("Servicio: Pregunta de encuesta (ID detalle: "+idEncuestaDetalle+") no encontrada para desmarcar.");
            return false;
        }
        detalle.setEsPreguntaDescarte(false);
        detalle.setCriterioDescarteValor(null);
        return encuestaDetalleDAO.actualizarDetallePregunta(detalle);
    }

    public boolean eliminarPreguntaDeEncuesta(int idEncuesta, int idEncuestaDetalle){
        return encuestaDetalleDAO.eliminarPreguntaDeEncuesta(idEncuestaDetalle);
    }
    
    public boolean eliminarPreguntaDeEncuestaServicio(int idEncuestaDetalle) {
    return encuestaDetalleDAO.eliminarPreguntaDeEncuesta(idEncuestaDetalle);
    }

    /**
     * Obtiene todas las preguntas asociadas a una encuesta específica.
     *
     * @param idEncuesta El ID de la encuesta cuyas preguntas se desean obtener.
     * @return Una lista de objetos {@link EncuestaDetallePregunta}.
     *         La lista puede estar vacía si la encuesta no tiene preguntas asociadas o si no existe.
     */
    public List<EncuestaDetallePregunta> obtenerPreguntasDeEncuesta(int idEncuesta) {
        return encuestaDetalleDAO.obtenerPreguntasPorEncuesta(idEncuesta);
    }

    public Encuesta copiarEncuesta(int idEncuestaOriginal, int idAdminCopia) {
        Encuesta original = obtenerDetallesCompletosEncuesta(idEncuestaOriginal);
        if (original == null) {
            System.err.println("Servicio: Encuesta original con ID " + idEncuestaOriginal + " no encontrada para copiar.");
            return null;
        }

        Encuesta copia = new Encuesta(
            "Copia de " + original.getNombreEncuesta(),
            original.getDescripcion(),
            original.getFechaInicioVigencia(),
            original.getFechaFinVigencia(),
            original.getPublicoObjetivoCantidad(),
            original.getDefinicionPerfil(),
            idAdminCopia
        );
        copia.setEstado("Borrador");

        int idNuevaEncuesta = encuestaDAO.crearEncuesta(copia);
        if (idNuevaEncuesta != -1) {
            copia.setIdEncuesta(idNuevaEncuesta);
            if (original.getPreguntasAsociadas() != null) {
                for (EncuestaDetallePregunta detalleOriginal : original.getPreguntasAsociadas()) {
                    EncuestaDetallePregunta detalleCopia = new EncuestaDetallePregunta();
                    detalleCopia.setIdEncuesta(idNuevaEncuesta);
                    detalleCopia.setIdPreguntaBanco(detalleOriginal.getIdPreguntaBanco());
                    detalleCopia.setTextoPreguntaUnica(detalleOriginal.getTextoPreguntaUnica());
                    detalleCopia.setIdTipoPreguntaUnica(detalleOriginal.getIdTipoPreguntaUnica());
                    detalleCopia.setIdClasificacionUnica(detalleOriginal.getIdClasificacionUnica());
                    detalleCopia.setOrdenEnEncuesta(detalleOriginal.getOrdenEnEncuesta());
                    detalleCopia.setEsPreguntaDescarte(detalleOriginal.isEsPreguntaDescarte());
                    detalleCopia.setCriterioDescarteValor(detalleOriginal.getCriterioDescarteValor());

                    encuestaDetalleDAO.agregarPreguntaAEncuesta(detalleCopia);
                }
            }
            System.out.println("Servicio: Encuesta ID " + idEncuestaOriginal + " copiada a nueva encuesta ID " + idNuevaEncuesta);
            return copia;
        } else {
            System.err.println("Servicio: Error al crear la entrada principal para la encuesta copiada.");
        }
        return null;
    }

    public Encuesta obtenerDetallesCompletosEncuesta(int idEncuesta) {
        Encuesta encuesta = encuestaDAO.obtenerEncuestaPorId(idEncuesta);
        if (encuesta != null) {
            List<EncuestaDetallePregunta> preguntas = encuestaDetalleDAO.obtenerPreguntasPorEncuesta(idEncuesta);
            PreguntaBancoDAO pbDao = new PreguntaBancoDAO(); 
            TipoPreguntaDAO tpDao = new TipoPreguntaDAO();
            ClasificacionPreguntaDAO cpDao = new ClasificacionPreguntaDAO();

            for(EncuestaDetallePregunta edp : preguntas) {
                if (edp.getIdPreguntaBanco() != null && edp.getPreguntaDelBanco() == null) {
                    PreguntaBanco preguntaBanco = pbDao.obtenerPreguntaPorId(edp.getIdPreguntaBanco());
                    if (preguntaBanco != null) {
                        TipoPregunta tipo = tpDao.obtenerTipoPreguntaPorId(preguntaBanco.getIdTipoPregunta());
                        if(tipo != null) preguntaBanco.setNombreTipoPregunta(tipo.getNombreTipo());

                        if(preguntaBanco.getIdClasificacion() != null && preguntaBanco.getIdClasificacion() > 0){
                            ClasificacionPregunta clasif = cpDao.obtenerClasificacionPorId(preguntaBanco.getIdClasificacion());
                            if(clasif != null) preguntaBanco.setNombreClasificacion(clasif.getNombreClasificacion());
                        }
                    }
                    edp.setPreguntaDelBanco(preguntaBanco);
                } else if (edp.getTextoPreguntaUnica() != null) {
                    if (edp.getIdTipoPreguntaUnica() != null) {
                        TipoPregunta tipoUnica = tpDao.obtenerTipoPreguntaPorId(edp.getIdTipoPreguntaUnica());
                    }
                     if (edp.getIdClasificacionUnica() != null) {
                        ClasificacionPregunta clasifUnica = cpDao.obtenerClasificacionPorId(edp.getIdClasificacionUnica());
                    }
                }
            }
            encuesta.setPreguntasAsociadas(preguntas);
        }
        return encuesta;
    }
}