package SteveJobs.encuestas.modelo;

import java.sql.Timestamp;

public class PreguntaBanco {

    private int idPreguntaBanco;
    private String textoPregunta;
    private int idTipoPregunta;       // Corresponds to id_tipo_pregunta in DB
    private Integer idClasificacion;  // Corresponds to id_clasificacion_pregunta in DB (can be null)
    private String estado;
    private Timestamp fechaCreacion;
    private Timestamp fechaModificacion;
    private Integer idUsuarioCreador;   // Can be null if not always tracked or system-generated

    // Transient fields, not directly mapped to DB columns but useful for display/logic
    private String nombreTipoPregunta;
    private String nombreClasificacion;

    public PreguntaBanco() {
    }

    // Constructor con todos los campos podría ser útil
    public PreguntaBanco(int idPreguntaBanco, String textoPregunta, int idTipoPregunta, Integer idClasificacion,
                         String estado, Timestamp fechaCreacion, Timestamp fechaModificacion, Integer idUsuarioCreador) {
        this.idPreguntaBanco = idPreguntaBanco;
        this.textoPregunta = textoPregunta;
        this.idTipoPregunta = idTipoPregunta;
        this.idClasificacion = idClasificacion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.idUsuarioCreador = idUsuarioCreador;
    }


    public int getIdPreguntaBanco() {
        return idPreguntaBanco;
    }

    public void setIdPreguntaBanco(int idPreguntaBanco) {
        this.idPreguntaBanco = idPreguntaBanco;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

    public void setTextoPregunta(String textoPregunta) {
        this.textoPregunta = textoPregunta;
    }

    public int getIdTipoPregunta() {
        return idTipoPregunta;
    }

    public void setIdTipoPregunta(int idTipoPregunta) {
        this.idTipoPregunta = idTipoPregunta;
    }

    public Integer getIdClasificacion() {
        return idClasificacion;
    }

    public void setIdClasificacion(Integer idClasificacion) {
        this.idClasificacion = idClasificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Timestamp getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Timestamp fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getIdUsuarioCreador() {
        return idUsuarioCreador;
    }

    public void setIdUsuarioCreador(Integer idUsuarioCreador) {
        this.idUsuarioCreador = idUsuarioCreador;
    }

    // Getters y setters para campos transient (no mapeados directamente a la BD)
    public String getNombreTipoPregunta() {
        return nombreTipoPregunta;
    }

    public void setNombreTipoPregunta(String nombreTipoPregunta) {
        this.nombreTipoPregunta = nombreTipoPregunta;
    }

    public String getNombreClasificacion() {
        return nombreClasificacion;
    }

    public void setNombreClasificacion(String nombreClasificacion) {
        this.nombreClasificacion = nombreClasificacion;
    }

    @Override
    public String toString() {
        return "PreguntaBanco{" +
                "idPreguntaBanco=" + idPreguntaBanco +
                ", textoPregunta='" + textoPregunta + ''' +
                ", idTipoPregunta=" + idTipoPregunta +
                ", idClasificacion=" + idClasificacion +
                ", estado='" + estado + ''' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaModificacion=" + fechaModificacion +
                ", idUsuarioCreador=" + idUsuarioCreador +
                ", nombreTipoPregunta='" + nombreTipoPregunta + ''' +
                ", nombreClasificacion='" + nombreClasificacion + ''' +
                '}';
    }
}
