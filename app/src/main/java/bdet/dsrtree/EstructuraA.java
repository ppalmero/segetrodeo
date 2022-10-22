/*
 * Created on 31/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.dsrtree;

import bdet.rtree.RTree;
import bdet.bitacora.Bitacora;
import bdet.estructuraA.EntradaA;

/**
 * @author Gisela
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EstructuraA {
	public EntradaA[] entradasA;
	public int indiceA;
	public boolean primeraVez;

	public EstructuraA(){
		this.entradasA=new EntradaA[100]; // VER TAMA�O TOTAL ESTRUC A!!!!!!
		this.indiceA=0;
		this.primeraVez = true;
	}
	public EntradaA getEntradaA(int pos){
		return this.entradasA[pos];
	}
	public int getIndice(){
		return this.indiceA;
	}
	public void setIndice(int indice){
		this.indiceA=indice;
	}
	// Actualizar estructura C !!!!! 
	public void insertarA(float tiempo, RTree rtree, Bitacora bitacora){
		if (!primeraVez)
			this.entradasA[++indiceA]=new EntradaA(rtree, bitacora,tiempo,indiceA);
		else
			{this.entradasA[0]=new EntradaA(rtree, bitacora,tiempo,indiceA);
			this.primeraVez = false;
			}
		System.out.println("Instante--------------------------------" + indiceA);	
	}
}
