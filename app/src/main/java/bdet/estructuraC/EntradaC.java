/*
 * Created on 19/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.estructuraC;

import bdet.comun.Punto;


/**
 * @author Gisela y Guillermo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EntradaC {
	private DatoC entradaLista;
	private Punto posInicial;
	private int indice; // cantidad de nodos por cada lista 
	
	public EntradaC(){
		this.entradaLista=null;
		this.indice=0;
	}
	
	public void setPosInicial(float x, float y){
		this.posInicial=new Punto(x,y);
	}
	public void setIndice(){
		this.indice++;
	}
	public int getIndice(){
		return this.indice;
	}
	public Punto getPosInicial(){
		return this.posInicial;
	}
	
	public DatoC getEntradaLista(){
		return this.entradaLista;
	}
	public void setEntradaLista(DatoC entrada){
		this.entradaLista=entrada;
	}
}
