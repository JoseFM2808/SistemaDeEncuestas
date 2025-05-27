/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SteveJobs.encuestas.modelo;

import javax.swing.JOptionPane;

/**
 *
 * @author Pablo
 */
public class PlantillaPreguntaCat {
    int id;
    String categoria;
    String descripcion;
    private static int contador = 1;
    

    public PlantillaPreguntaCat(String categoria, String descripcion) {
        this.id = contador;
        this.categoria = categoria;
        this.descripcion = descripcion;        
        contador++;
    }

    public int getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "cat{" + "id=" + id + ", categoria=" + categoria + ", descripcion=" + descripcion + '}';
    }

     public void mostrarIdCat() {
        JOptionPane.showMessageDialog(null, "ID: "+id);
    }
}
