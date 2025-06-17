/*
 * Módulo Responsable: Conexión a Base de Datos
 * Autores: Equipo de Desarrollo (Revisado y Unificado)
 * Versión: 2.0 (Reescritura)
 * Fecha: 15/06/2025
 *
 * Descripción del Archivo:
 * Clase de utilidad para gestionar la conexión con la base de datos MySQL.
 * Asegura el uso del driver correcto para MySQL 8.x y proporciona métodos
 * para obtener y cerrar conexiones y recursos JDBC.
 */
package SteveJobs.encuestas.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    // URL de conexión a la base de datos del sistema de encuestas
    private static final String URL = "jdbc:mysql://localhost:3306/bd_sistema_encuestas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    // Usuario de la base de datos (se recomienda usar variables de entorno o configuración externa en producción)
    private static final String USUARIO = "root";
    // Contraseña del usuario de la base de datos (se recomienda usar variables de entorno o configuración externa en producción)
    private static final String CONTRASENA = "password123"; // Reemplazar con la contraseña real si es necesario para desarrollo

    static {
        try {
            // Carga del driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error Crítico: Driver MySQL (com.mysql.cj.jdbc.Driver) no encontrado. Asegúrese de que el conector JAR de MySQL 8.x esté en el classpath del proyecto.");
            // En una aplicación real, esto podría lanzar una RuntimeException para detener la app si la BD es esencial.
        }
    }

    /**
     * Establece una conexión con la base de datos.
     *
     * @return Un objeto Connection si la conexión es exitosa, o null si falla.
     */
    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            System.err.println("Error de Conexión BD: No se pudo establecer la conexión con '" + URL + "'. Mensaje: " + e.getMessage());
            // Considerar logging más robusto aquí en una aplicación real.
        }
        return con;
    }

    /**
     * Cierra un ResultSet de forma segura.
     *
     * @param rs El ResultSet a cerrar.
     */
    public static void cerrar(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra un Statement de forma segura.
     *
     * @param stmt El Statement a cerrar.
     */
    public static void cerrar(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar Statement: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra un PreparedStatement de forma segura.
     * (Alias de cerrar(Statement stmt) ya que PreparedStatement es una subclase de Statement)
     * @param ps El PreparedStatement a cerrar.
     */
    public static void cerrar(PreparedStatement ps) {
        cerrar((Statement) ps);
    }

    /**
     * Cierra una Connection de forma segura.
     *
     * @param con La Connection a cerrar.
     */
    public static void cerrar(Connection con) {
        if (con != null) {
            try {
                if (!con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra un ResultSet, un Statement y una Connection de forma segura.
     *
     * @param rs  El ResultSet a cerrar (puede ser null).
     * @param stmt El Statement a cerrar (puede ser null).
     * @param con La Connection a cerrar (puede ser null).
     */
    public static void cerrar(ResultSet rs, Statement stmt, Connection con) {
        cerrar(rs);
        cerrar(stmt);
        cerrar(con);
    }

    /**
     * Cierra un Statement y una Connection de forma segura.
     *
     * @param stmt El Statement a cerrar (puede ser null).
     * @param con La Connection a cerrar (puede ser null).
     */
    public static void cerrar(Statement stmt, Connection con) {
        cerrar(stmt); // Llama al método cerrar(Statement) individual
        cerrar(con);  // Llama al método cerrar(Connection) individual
    }
}
