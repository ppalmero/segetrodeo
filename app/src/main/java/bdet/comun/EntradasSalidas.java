/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdet.comun;

import java.util.ArrayList;

/**
 *
 * @author Pablo
 */
public class EntradasSalidas {
    private ArrayList<Integer> entradas;
    private ArrayList<Integer> salidas;

    public EntradasSalidas() {
        entradas = new ArrayList<>();
        salidas = new ArrayList<>();
    }
    
    public void addEntrada(int e){
        entradas.add(e);
    }
    
    public void addSalida(int s){
        salidas.add(s);
    }

    public ArrayList<Integer> getEntradas() {
        return entradas;
    }

    public ArrayList<Integer> getSalidas() {
        return salidas;
    }
    
    
    
}
