
package bdet.estructuraB;

/**
 * @author Gisela y Guillermo
 */
public class EntradaB {
	private final float tiempo; // Tiempo de la entrada
	private int numBitacora; //indice de Estruc A
	private int numBloque; //bloque de la bitacora
	private final int fila; // Fila en la bit�cora de la entrada

//	 Crea una nueva EntradaB
	public EntradaB(float tiempo, int bitacora,int bloque, int fila){
	    this.tiempo = tiempo;
	    this.numBitacora = bitacora;
	    this.numBloque = bloque;
	    this.fila = fila;
	}

//	 Retorna la bit�cora de la entrada
	public int getBitacora(){
	    return numBitacora;
	}
//	 Retorna la fila en la bit�cora de la entrada
	public int getBloque(){
	    return numBloque;
	}
//	 Retorna la fila en la bit�cora de la entrada
	public int getFila(){
	    return fila;
	}

//	  Retorna el tiempo de la entrada
	public float getTiempo(){
	    return tiempo;
	}
}
