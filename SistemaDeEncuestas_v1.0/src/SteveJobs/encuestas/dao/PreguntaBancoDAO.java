/*
Pendiente..
 */
package SteveJobs.encuestas.dao;

import SteveJobs.encuestas.modelo.PreguntaBanco;
import java.util.List;
import java.util.ArrayList;


public class PreguntaBancoDAO {
    
    public PreguntaBanco obtenerPreguntaPorId(int id) {
         System.out.println("DEBUG: PreguntaBancoDAO.obtenerPreguntaPorId(int) NO IMPLEMENTADO, devolviendo null para ID " + id);
        return null;
    }
     public PreguntaBanco obtenerPreguntaPorId(Integer id) {
        return obtenerPreguntaPorId(id.intValue());
    }
    public List<PreguntaBanco> listarPreguntasDelBancoConFiltro(String filtroTexto, String filtroTipo) {
        System.out.println("DEBUG: PreguntaBancoDAO.listarPreguntasDelBancoConFiltro NO IMPLEMENTADO, devolviendo lista vacía.");
        return new ArrayList<>();
    }
    
}
