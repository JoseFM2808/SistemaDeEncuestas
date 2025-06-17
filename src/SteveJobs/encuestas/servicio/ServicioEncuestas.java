/*
 * Autores del Módulo:
 * - Alfredo Swidin
 *
 * Responsabilidad Principal:
 * - Lógica de negocio para encuestas
 */
package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.dao.EncuestaDAO;
import SteveJobs.encuestas.dao.EncuestaDetallePreguntaDAO;
import SteveJobs.encuestas.dao.PreguntaBancoDAO;
import SteveJobs.encuestas.dao.TipoPreguntaDAO;
import SteveJobs.encuestas.dao.ClasificacionPreguntaDAO;
import SteveJobs.encuestas.dao.RespuestaUsuarioDAO; // Necesario para obtenerEncuestasActivasParaUsuario
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

    public ServicioEncuestas() {
        this.encuestaDAO = new EncuestaDAO();
        this.encuestaDetalleDAO = new EncuestaDetallePreguntaDAO();
        this.preguntaBancoDAO = new PreguntaBancoDAO();
        this.tipoPreguntaDAO = new TipoPreguntaDAO();
        this.clasificacionPreguntaDAO = new ClasificacionPreguntaDAO();
    }

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
    
    /**
     * Obtiene todas las encuestas de la base de datos y las ordena alfabéticamente por nombre
     * utilizando el algoritmo de Insertion Sort.
     *
     * @return Una lista de {@link Encuesta} ordenadas por nombre.
     */
    public List<Encuesta> obtenerTodasLasEncuestas() {
        List<Encuesta> encuestas = encuestaDAO.obtenerTodasLasEncuestas();
        if (encuestas == null || encuestas.size() <= 1) {
            return encuestas; // No necesita ordenación o ya está "ordenada"
        }

        // Implementación de Insertion Sort
        for (int i = 1; i < encuestas.size(); i++) {
            Encuesta encuestaActual = encuestas.get(i);
            String nombreActual = encuestaActual.getNombreEncuesta() != null ? encuestaActual.getNombreEncuesta() : "";
            int j = i - 1;

            // Mover elementos de encuestas[0..i-1] que son mayores que nombreActual
            // a una posición adelante de su posición actual
            while (j >= 0) {
                String nombreJ = encuestas.get(j).getNombreEncuesta() != null ? encuestas.get(j).getNombreEncuesta() : "";
                if (nombreJ.compareToIgnoreCase(nombreActual) > 0) {
                    encuestas.set(j + 1, encuestas.get(j));
                    j = j - 1;
                } else {
                    break; // El elemento actual ya está en su posición correcta relativa a los anteriores
                }
            }
            encuestas.set(j + 1, encuestaActual);
        }
        return encuestas;
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

    public List<Encuesta> obtenerEncuestasActivasParaUsuario(Usuario usuario) {

        System.out.println("Servicio: obtenerEncuestasActivasParaUsuario - Lógica de filtrado por perfil PENDIENTE.");
        List<Encuesta> todasActivas = encuestaDAO.obtenerTodasLasEncuestas();
        List<Encuesta> activasFiltradas = new ArrayList<>();
        for(Encuesta e : todasActivas){
            if("Activa".equalsIgnoreCase(e.getEstado())){
                activasFiltradas.add(e);
            }
        }
        return activasFiltradas;
    }


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

    /**
     * Busca una encuesta específica por su ID dentro de una lista de encuestas dada,
     * utilizando el algoritmo de Binary Search.
     * La lista proporcionada se copia y se ordena por ID antes de la búsqueda para
     * asegurar el pre-requisito de Binary Search sin modificar la lista original.
     *
     * @param listaEncuestas La lista de encuestas en la que buscar.
     * @param idEncuestaBuscada El ID de la encuesta a encontrar.
     * @return La {@link Encuesta} encontrada, o {@code null} si no se encuentra en la lista.
     */
    public Encuesta buscarEncuestaEnListaPorId(List<Encuesta> listaEncuestas, int idEncuestaBuscada) {
        if (listaEncuestas == null || listaEncuestas.isEmpty()) {
            return null;
        }

        // Crear una copia para no modificar la lista original que podría estar ordenada por nombre
        List<Encuesta> copiaLista = new ArrayList<>(listaEncuestas);

        // Ordenar la copia por ID para Binary Search
        copiaLista.sort((e1, e2) -> Integer.compare(e1.getIdEncuesta(), e2.getIdEncuesta()));

        int izquierda = 0;
        int derecha = copiaLista.size() - 1;

        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            Encuesta encuestaMedio = copiaLista.get(medio);

            if (encuestaMedio.getIdEncuesta() == idEncuestaBuscada) {
                return encuestaMedio; // Encuesta encontrada
            }

            if (encuestaMedio.getIdEncuesta() < idEncuestaBuscada) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return null; // Encuesta no encontrada
    }

    /**
     * Busca una pregunta específica dentro de una encuesta por su número de orden.
     * Utiliza una búsqueda secuencial en la lista de preguntas de la encuesta,
     * la cual se asume ordenada por {@code ordenEnEncuesta} gracias al DAO.
     *
     * @param idEncuesta El ID de la encuesta en la que buscar.
     * @param ordenBuscado El número de orden de la pregunta deseada.
     * @return El objeto {@link EncuestaDetallePregunta} si se encuentra una pregunta con el orden especificado,
     *         o {@code null} si la encuesta no tiene preguntas, no se encuentra la encuesta, o no existe
     *         una pregunta con dicho orden.
     */
    public EncuestaDetallePregunta buscarPreguntaPorOrden(int idEncuesta, int ordenBuscado) {
        List<EncuestaDetallePregunta> preguntas = obtenerPreguntasDeEncuesta(idEncuesta);

        if (preguntas == null || preguntas.isEmpty()) {
            System.out.println("ServicioEncuestas: No hay preguntas para la encuesta ID " + idEncuesta + " o la encuesta no existe.");
            return null;
        }

        // Búsqueda Secuencial
        for (EncuestaDetallePregunta edp : preguntas) {
            if (edp.getOrdenEnEncuesta() == ordenBuscado) {
                return edp; // Pregunta encontrada
            }
        }

        System.out.println("ServicioEncuestas: No se encontró pregunta con orden " + ordenBuscado + " en la encuesta ID " + idEncuesta + ".");
        return null; // Pregunta no encontrada con ese orden
    }
}