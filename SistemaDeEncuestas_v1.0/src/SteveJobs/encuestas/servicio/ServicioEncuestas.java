/*
Autor: Alfredo Swidin
 */
package SteveJobs.encuestas.servicio;

import SteveJobs.encuestas.dao.EncuestaDAO;
import SteveJobs.encuestas.dao.EncuestaDetallePreguntaDAO;
import SteveJobs.encuestas.dao.PreguntaBancoDAO;
import SteveJobs.encuestas.dao.TipoPreguntaDAO;
import SteveJobs.encuestas.dao.ClasificacionPreguntaDAO;
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
}