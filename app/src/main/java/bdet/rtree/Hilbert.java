package bdet.rtree;

import bdet.comun.Constantes;
import bdet.comun.Punto;

/*
 * Hilbert.java
 * 
 * Contiene una funci�n est�tica para calcular el valor de Hilbert de un punto
 * 
 * Basado en c�digo de Jefferson R. de O. e Silva
 *  
 */

public class Hilbert {
	private static final int[] estados = { 1, 0, 2, 0, 0, 3, 1,
	        1, 2, 2, 0, 3, 3, 1, 3, 2 };
	private static final int[] t1 = { 0, 0, 1, 1, 0, 1, 0, 1,
	        1, 0, 1, 0, 1, 1, 0, 0 };
	private static final int[] t2 = { 0, 1, 1, 0, 0, 1, 1, 0,
	        0, 1, 1, 0, 0, 1, 1, 0 };
	
	// Obtiene el valor hilbert para un punto p
	public static float getValorHilbert(Punto p)
	{
	    int[] x = new int[Constantes.PRECISION_HILBERT];
	    int[] y = new int[Constantes.PRECISION_HILBERT];
	    float vx = p.x;
	    float vy = p.y;
	    // Transforma las coordenadas en binario
	    for (int i = 0; i < Constantes.PRECISION_HILBERT; i++) {
	        vx = vx * 2;
	
	        if (vx >= 1) {
	            x[i] = 1;
	            vx -= 1;
	        }
	        else
	            x[i] = 0;
	
	        vy = vy * 2;
	
	        if (vy >= 1) {
	            y[i] = 1;
	            vy -= 1;
	        }
	        else
	            y[i] = 0;
	    }
	    // Calcula el valor Hilbert
	    float h = 0;
	    int s = 0;
	    int ind;
	    for (int j = 1; j <= Constantes.PRECISION_HILBERT; j++) {
	        ind = 4 * s + 2 * x[j - 1] + y[j - 1];
	        h += (2 * t1[ind] + t2[ind])
	                * (Math.pow(2, (-2) * (j)));
	        s = estados[ind];
	    }
	    return h;
	}
}