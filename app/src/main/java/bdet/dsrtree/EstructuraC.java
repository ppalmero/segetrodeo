/*
 * Created on 18/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.dsrtree;

import bdet.estructuraC.DatoC;
import bdet.estructuraC.EntradaC;
import bdet.estructuraC.PosActual;

/**
 * @author Gisela
 *
 */
public class EstructuraC {
	private EntradaC[] entradasC;
	private PosActual[] posicionesActuales; //arreglo con posiciones actuales	
	
	public EstructuraC(int cantObjetos){
		entradasC = new EntradaC[cantObjetos]; // cantidad de objetos
		posicionesActuales = new PosActual[cantObjetos];
		for (int i=0; i<cantObjetos; i++){
			posicionesActuales[i]= new PosActual(-1,-1,-1, 0);
			entradasC[i]= new EntradaC();
		}
	}
	public EntradaC[] getEntradasC(){
		return this.entradasC;
	}
	public EntradaC getEntradaC(int pos){
		return this.entradasC[pos];
	}
	public PosActual getPosicionesActuales(int oid){
		return this.posicionesActuales[oid];
	}
	
	//se utiliza para ir guardando los �ltimos movimientos de los objetos
	public void setPosicionesActuales(int oid, int puntoReferencia, int posBitacora, int posBloque, float tiempo){
		this.posicionesActuales[oid].setPuntoReferencia(puntoReferencia);
		this.posicionesActuales[oid].setPosBitacora(posBitacora);
		this.posicionesActuales[oid].setPosBloque(posBloque);
		this.posicionesActuales[oid].setTiempo(tiempo);
	}
	//se ejecuta cuando se crea una nueva bit�cora
	public void insertarDatoC(DatoC datoC, int oid){
		datoC.setSiguiente(entradasC[oid].getEntradaLista());
		entradasC[oid].setEntradaLista(datoC);
		entradasC[oid].setIndice();
	}
}
