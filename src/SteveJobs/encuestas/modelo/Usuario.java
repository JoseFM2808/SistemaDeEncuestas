package SteveJobs.encuestas.modelo;

import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private String documentoIdentidad;
    private String nombresApellidos;
    private String email;
    private String password;
    private String tipoNivel;
    private Timestamp fechaRegistroSistema;
    private String estadoCuenta;

    public Usuario() {
    }

    public Usuario(String documentoIdentidad, String nombresApellidos, String email, String password, String tipoNivel) {
        this.documentoIdentidad = documentoIdentidad;
        this.nombresApellidos = nombresApellidos;
        this.email = email;
        this.password = password;
        this.tipoNivel = tipoNivel;
        this.estadoCuenta = "Activo";
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getNombresApellidos() {
        return nombresApellidos;
    }

    public void setNombresApellidos(String nombresApellidos) {
        this.nombresApellidos = nombresApellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoNivel() {
        return tipoNivel;
    }

    public void setTipoNivel(String tipoNivel) {
        this.tipoNivel = tipoNivel;
    }

    public Timestamp getFechaRegistroSistema() {
        return fechaRegistroSistema;
    }

    public void setFechaRegistroSistema(Timestamp fechaRegistroSistema) {
        this.fechaRegistroSistema = fechaRegistroSistema;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    @Override
    public String toString() {
        return "Usuario{" +
               "idUsuario=" + idUsuario +
               ", documentoIdentidad='" + documentoIdentidad + '\'' +
               ", nombresApellidos='" + nombresApellidos + '\'' +
               ", email='" + email + '\'' +
               // No incluir password en toString por seguridad
               ", tipoNivel='" + tipoNivel + '\'' +
               ", fechaRegistroSistema=" + fechaRegistroSistema +
               ", estadoCuenta='" + estadoCuenta + '\'' +
               '}';
    }
}