package bdet.dsrtree;

import bdet.estructuraB.EntradaB;

/**
 * @author Gisela
 */
public class EstructuraB {

    private EntradaB[] entradas; // Entradas de B
    private int numeroEntradas; // N�mero de entradas actuales

//	 Crea una nueva EstructuraB que almacena tamano entradas
    public EstructuraB(int x) {
        entradas = new EntradaB[x];
        numeroEntradas = 0;
    }

//	 Agrega un nueva entrada
    public void agregarEntrada(float t, int bitacora, int bloque, int fila) {
        entradas[numeroEntradas++] = new EntradaB(t, bitacora, bloque, fila);
    }

//	  Obtiene un �ndice dado un tiempo
    public int getIndice(float t) {
        int i;
        for (i = 0; i < numeroEntradas && entradas[i].getTiempo() < t; i++);

        if (i == numeroEntradas || entradas[i].getTiempo() > t) {
            i--;
        }

        return i;
    }

//	 Retorna la bit�cora de la entrada i
    public int getBitacora(int i) {
        return entradas[i].getBitacora();
    }

    public int getBloque(int i) {
        return entradas[i].getBloque();
    }
//	 Retorna la fila en la bit�cora de la entrada i

    public int getFila(int i) {
        return entradas[i].getFila();
    }

    public EntradaB[] getEntradas() {
        return entradas;
    }
}
