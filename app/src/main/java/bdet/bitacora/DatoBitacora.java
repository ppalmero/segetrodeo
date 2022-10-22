/*
 * Created on 18/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.bitacora;

import bdet.comun.Punto;

/**
 * @author Gisela y Guillermo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DatoBitacora {
	private int oid;
	private float tiempo;
	private DatoBitacora refAnt; // movimiento anterior del objeto
	private Punto posicionActual; 
	private int indiceA; //para consulta trayectoria
	private int indiceBl; //para consulta trayectoria
	
	
	
	public DatoBitacora(int oid, float tiempo, Punto posicionActual, DatoBitacora refAnt, int indiceA, int indiceBl){
		this.oid=oid;
		this.tiempo=tiempo;
		this.posicionActual=posicionActual;
		this.refAnt=refAnt;
		this.posicionActual=posicionActual;
		this.indiceA=indiceA;
		this.indiceBl=indiceBl;
	}
	
	public int getOid(){
		return this.oid;
	}
	public Integer getOid2(){
		return new Integer (this.oid);
	}
	public float getTiempo(){
		return this.tiempo;
	}
	
	public int getIndiceA(){
		return this.indiceA;
	}
	
	public int getIndiceBl(){
		return this.indiceBl;
	}
	
	public DatoBitacora getRefAnt(){
		return this.refAnt;
	}
	public Punto getPosicionActual(){
		return this.posicionActual;
	}
	public void setRefAnt(DatoBitacora ref){
		this.refAnt=ref;
	}
}