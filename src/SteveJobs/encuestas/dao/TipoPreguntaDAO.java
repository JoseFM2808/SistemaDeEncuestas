package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.TipoPregunta;
import java.util.List;
import java.util.ArrayList;

public class TipoPreguntaDAO {
    
    public TipoPregunta obtenerTipoPreguntaPorNombre(String nombre) {
        System.out.println("DEBUG: TipoPreguntaDAO.obtenerTipoPreguntaPorNombre NO IMPLEMENTADO, devolviendo null para " + nombre);
        return null; /* Pablo implementará esto */
    }
    public TipoPregunta obtenerTipoPreguntaPorId(int id) {
         System.out.println("DEBUG: TipoPreguntaDAO.obtenerTipoPreguntaPorId NO IMPLEMENTADO, devolviendo null para ID " + id);
        return null; /* Pablo implementará esto */
    }
     public TipoPregunta obtenerTipoPreguntaPorId(Integer id) { // Sobrecarga por si se pasa Integer
        if (id == null) return null;
        return obtenerTipoPreguntaPorId(id.intValue());
    }
    
}
