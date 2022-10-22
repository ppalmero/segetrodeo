package bdet.rtree;

import bdet.comun.Rectangulo;


public class Dato extends Entrada {
	protected int oid; // Identificador del objeto
	
	// Crea un nuevo Dato dado un rectangulo y un identificador de objeto
	public Dato(Rectangulo r, int oid){
	    this.oid = oid;
	    limites = r;
	    hilbert = Hilbert.getValorHilbert(r.getCentro());
	}
	
	// Crea un nuevo Dato igual al dado como par�metro
	public Dato(Dato dato){
	    this.oid = dato.oid;
	    limites = new Rectangulo(dato.limites.xmin, dato.limites.ymin, dato.limites.xmax, dato.limites.ymax);
	    hilbert = Hilbert.getValorHilbert(limites.getCentro());
	}
	
	// Retorna el oid de este Dato
	public int getOid(){
	    return oid;
	}
	
	// Determina si este Dato es igual a o
	public boolean equals(Object o){
	    if (o instanceof Dato) {
	        if (((Dato) o).oid == oid
	                && ((Dato) o).limites.equals(limites))
	            return true;
	        else
	            return false;
	    }
	    return false;
	}
}