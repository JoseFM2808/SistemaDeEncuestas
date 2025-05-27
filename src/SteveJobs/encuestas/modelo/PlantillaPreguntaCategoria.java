/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SteveJobs.encuestas.modelo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Pablo
 */
public class PlantillaPreguntaCategoria {
    public static void insertarOrdenado(List<PlantillaPreguntaCat> lista, PlantillaPreguntaCat nuevoCat) {
    int i = 0;
    while (i < lista.size() && lista.get(i).getId() < nuevoCat.getId()) {
        i++;
    }
    lista.add(i, nuevoCat);
}

    public static void main(String[] args) {
        ArrayList<PlantillaPreguntaCat> listaCat = new ArrayList<>();
        
        int Id = 0;
        
         int cantidad = Integer.parseInt(JOptionPane.showInputDialog("¿Cuántas personas deseas registrar?"));
         
         
        for (int i = 0; i < cantidad; i++) {
            String categoriaC = JOptionPane.showInputDialog("Ingrese el nombre de la categoria #" + (i + 1));
            String DescripcionC = JOptionPane.showInputDialog("Ingrese la descripcion de la categoria #" + (i + 1));
            PlantillaPreguntaCat Cat = new PlantillaPreguntaCat(categoriaC, DescripcionC);
            listaCat.add(Cat);
        }
        
        
        StringBuilder mensaje = new StringBuilder("Categorias registradas:\n");
        for (int i = 0; i < listaCat.size(); i++) {
            mensaje.append((i + 1)).append(". ").append(listaCat.get(i).getCategoria()).append("\n");
        }     
        
        
        JOptionPane.showMessageDialog(null, mensaje.toString());
        
        int idBuscar = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID a buscar:"));
PlantillaPreguntaCat resultado = PlantillaPregunta.buscarEnOrdenPorId(listaCat, idBuscar);

if (resultado != null) {
    JOptionPane.showMessageDialog(null, "Categoría encontrada:\n" + resultado.toString());
} else {
    JOptionPane.showMessageDialog(null, "ID no encontrado.");
}

         
    }
}
