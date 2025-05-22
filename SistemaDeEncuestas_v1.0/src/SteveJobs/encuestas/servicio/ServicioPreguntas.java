/*
Pendiente..
 */
package SteveJobs.encuestas.servicio;
import SteveJobs.encuestas.modelo.PreguntaBanco;
import SteveJobs.encuestas.dao.PreguntaBancoDAO; // Asumiendo que Pablo lo crea
import java.util.List;
import java.util.ArrayList;

public class ServicioPreguntas {
    
    private PreguntaBancoDAO preguntaBancoDAO;

    public ServicioPreguntas() {
        this.preguntaBancoDAO = new PreguntaBancoDAO(); // Asumiendo que Pablo crea esta clase DAO
    }

    public List<PreguntaBanco> listarPreguntasDelBancoConFiltro(String filtroTexto, String filtroTipo) {
        System.out.println("ServicioPreguntas.listarPreguntasDelBancoConFiltro no implementado, devolviendo lista vacía.");
        // Aquí Pablo llamaría a su DAO
        return new ArrayList<>();
    }
    // ... otros métodos que Pablo necesite para sus requerimientos REQMS-007 a REQMS-011
    
}
