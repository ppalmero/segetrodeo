package bdet.rtree;

import bdet.comun.Rectangulo;


public abstract class Entrada {
	protected Rectangulo limites; // MBR
	protected float hilbert; // Valor de Hilbert
	
	// Retorna el MBR
	public Rectangulo getLimites(){
	    return limites;
	}
	
	// Retorna el valor de Hilbert
	public float getHilbert(){
	    return hilbert;
	}
}