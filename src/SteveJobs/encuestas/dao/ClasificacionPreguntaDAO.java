
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.ClasificacionPregunta;
import java.util.List;
import java.util.ArrayList;

public class ClasificacionPreguntaDAO {
    
    public ClasificacionPregunta obtenerClasificacionPorNombre(String nombre) {
        System.out.println("DEBUG: ClasificacionPreguntaDAO.obtenerClasificacionPorNombre NO IMPLEMENTADO, devolviendo null para " + nombre);
        return null; /* Pablo implementará esto */
    }
     public ClasificacionPregunta obtenerClasificacionPorId(int id) {
        System.out.println("DEBUG: ClasificacionPreguntaDAO.obtenerClasificacionPorId NO IMPLEMENTADO, devolviendo null para ID " + id);
        return null; /* Pablo implementará esto */
    }
    public ClasificacionPregunta obtenerClasificacionPorId(Integer id) { // Sobrecarga
        if (id == null) return null;
        return obtenerClasificacionPorId(id.intValue());
    }
    
}
