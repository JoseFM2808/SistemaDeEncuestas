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
import java.util.Comparator;


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

    public int registrarNuevaEncuesta(String nombre, String descripcion, Timestamp fechaInicio, Timestamp fechaFin, int publicoObjetivo, String perfilRequerido, int idAdmin) {
        // Validaciones de negocio
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("Servicio: El nombre de la encuesta es obligatorio.");
            return -1;
        }
        if (fechaFin.before(fechaInicio)) {
            System.err.println("Servicio: La fecha de fin no puede ser anterior a la de inicio.");
            return -1;
        }

        Encuesta nuevaEncuesta = new Encuesta();
        nuevaEncuesta.setNombre(nombre.trim());
        nuevaEncuesta.setDescripcion(descripcion);
        nuevaEncuesta.setFechaInicio(fechaInicio);
        nuevaEncuesta.setFechaFin(fechaFin);
        nuevaEncuesta.setPublicoObjetivo(publicoObjetivo);
        nuevaEncuesta.setPerfilRequerido(perfilRequerido);
        nuevaEncuesta.setIdAdminCreador(idAdmin);
        nuevaEncuesta.setEstado("Borrador");
        nuevaEncuesta.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        
        return encuestaDAO.crearEncuesta(nuevaEncuesta);
    }
    
    public List<Encuesta> obtenerTodasLasEncuestasOrdenadasPorNombre() {
        List<Encuesta> encuestas = encuestaDAO.obtenerTodasLasEncuestas();
        if (encuestas == null || encuestas.size() <= 1) {
            return encuestas;
        }

        // Algoritmo de Ordenamiento por Inserción
        for (int i = 1; i < encuestas.size(); i++) {
            Encuesta encuestaActual = encuestas.get(i);
            String nombreActual = encuestaActual.getNombre() != null ? encuestaActual.getNombre() : "";
            int j = i - 1;
            while (j >= 0 && (encuestas.get(j).getNombre() != null ? encuestas.get(j).getNombre() : "").compareToIgnoreCase(nombreActual) > 0) {
                encuestas.set(j + 1, encuestas.get(j));
                j = j - 1;
            }
            encuestas.set(j + 1, encuestaActual);
        }
        return encuestas;
    }
    
    public Encuesta buscarEncuestaEnListaPorId(List<Encuesta> listaEncuestas, int idBuscado) {
        if (listaEncuestas == null || listaEncuestas.isEmpty()) return null;
        
        // Algoritmo de Búsqueda Binaria (requiere lista ordenada por el criterio de búsqueda)
        // Creamos una copia y la ordenamos por ID para la búsqueda
        List<Encuesta> copiaOrdenadaPorId = new ArrayList<>(listaEncuestas);
        copiaOrdenadaPorId.sort(Comparator.comparingInt(Encuesta::getIdEncuesta));

        int izq = 0, der = copiaOrdenadaPorId.size() - 1;
        while (izq <= der) {
            int med = izq + (der - izq) / 2;
            if (copiaOrdenadaPorId.get(med).getIdEncuesta() == idBuscado) {
                return copiaOrdenadaPorId.get(med);
            }
            if (copiaOrdenadaPorId.get(med).getIdEncuesta() < idBuscado) {
                izq = med + 1;
            } else {
                der = med - 1;
            }
        }
        return null; // No encontrado
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
    public boolean modificarMetadatosEncuesta(int idEncuesta, String nuevoNombre, String nuevaDescripcion, Timestamp nuevaFechaInicio, Timestamp nuevaFechaFin, int nuevoPublicoObj, String nuevoPerfilDef) {
        Encuesta encuesta = encuestaDAO.obtenerEncuestaPorId(idEncuesta);
        if (encuesta == null) {
            return false;
        }
        encuesta.setNombre(nuevoNombre);
        encuesta.setDescripcion(nuevaDescripcion);
        encuesta.setFechaInicio(nuevaFechaInicio);
        encuesta.setFechaFin(nuevaFechaFin);
        encuesta.setPublicoObjetivo(nuevoPublicoObj);
        encuesta.setPerfilRequerido(nuevoPerfilDef);

        return encuestaDAO.actualizarEncuesta(encuesta);
    }

    public boolean cambiarEstadoEncuesta(int idEncuesta, String nuevoEstado) {
        Encuesta encuesta = encuestaDAO.obtenerEncuestaPorId(idEncuesta);
        if (encuesta == null) {
            return false;
        }
        // Regla de negocio: para activar, debe tener 12 preguntas.
        if ("Activa".equalsIgnoreCase(nuevoEstado)) {
            if (encuestaDetalleDAO.contarPreguntasEnEncuesta(idEncuesta) != 12) {
                System.err.println("Servicio: No se puede activar. La encuesta debe tener exactamente 12 preguntas.");
                return false;
            }
        }
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
        Encuesta original = encuestaDAO.obtenerEncuestaPorId(idEncuestaOriginal);
        if (original == null) return null;

        Encuesta copia = new Encuesta();
        // Usando los getters y setters corregidos para crear la copia
        copia.setNombre("Copia de " + original.getNombre());
        copia.setDescripcion(original.getDescripcion());
        copia.setFechaInicio(original.getFechaInicio());
        copia.setFechaFin(original.getFechaFin());
        copia.setPublicoObjetivo(original.getPublicoObjetivo());
        copia.setPerfilRequerido(original.getPerfilRequerido());
        copia.setIdAdminCreador(idAdminCopia);
        copia.setEstado("Borrador");
        copia.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        
        int idNuevaEncuesta = encuestaDAO.crearEncuesta(copia);
        if (idNuevaEncuesta != -1) {
            copia.setIdEncuesta(idNuevaEncuesta);
            // Lógica para copiar preguntas asociadas (asume que el DAO funciona)
            List<EncuestaDetallePregunta> detallesOriginales = encuestaDetalleDAO.obtenerPreguntasPorEncuesta(idEncuestaOriginal);
            for(EncuestaDetallePregunta detalle : detallesOriginales) {
                detalle.setIdEncuesta(idNuevaEncuesta); // Apuntar al nuevo ID de encuesta
                detalle.setIdEncuestaDetalle(0); // Resetear el ID del detalle para que se genere uno nuevo
                encuestaDetalleDAO.agregarPreguntaAEncuesta(detalle);
            }
            return copia;
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