/*
 * Created on 22/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.bitacora;

import bdet.comun.Constantes;
import bdet.comun.Punto;


/**
 * @author Gisela Y Guillermo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BloqueBitacora {
	private DatoBitacora[] DatosB;
	private int indiceBloque;  //me dice el dato en el bloque actual
	private int indiceA; //para consulta trayectoria
	private int indiceBl; //para consulta trayectoria
	
	public BloqueBitacora(int indiceA, int indiceBl){
		this.DatosB=new DatoBitacora[Constantes.TAMANO_BLOQUE_BITACORA];
		this.indiceBloque = 0;
		this.indiceA=indiceA;
		this.indiceBl=indiceBl;
	}
	public DatoBitacora[] getDatosB(){
		return this.DatosB;
	}
	public DatoBitacora getDatoB(int pos){
		return this.DatosB[pos];
	}
	public int getIndiceBloque(){
		return this.indiceBloque;
	}
	public boolean estaLleno(){
		return (indiceBloque==Constantes.TAMANO_BLOQUE_BITACORA);
	}	
	public void insertarDatoB(int oid, float tiempo, Punto posicionActual, DatoBitacora refAnt){
		DatosB[this.indiceBloque++]= new DatoBitacora(oid, tiempo, posicionActual, refAnt, indiceA, indiceBl);
	}
	
}
