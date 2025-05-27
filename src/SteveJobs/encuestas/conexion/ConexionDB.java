/*
Autores:
- Gian Fri
- José Flores
- Pendiente de revisión
*/
package SteveJobs.encuestas.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/bd_sistema_encuestas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "password123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver MySQL no encontrado. Asegúrate de tener el conector JAR en el proyecto.");
            e.printStackTrace();
        }
    }

    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("CONEXIÓN EXITOSA a bd_sistema_encuestas");
        } catch (SQLException e) {
            System.err.println("ERROR DE CONEXION BD: " + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }

    public static void cerrar(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
            }
        }
    }

    public static void cerrar(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar Statement: " + e.getMessage());
            }
        }
    }

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

    public static void cerrar(ResultSet rs, Statement stmt, Connection con) {
        cerrar(rs);
        cerrar(stmt);
        cerrar(con);
    }

    public static void cerrar(Statement stmt, Connection con) {
        cerrar(null, stmt, con);
    }
}