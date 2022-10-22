/*
 * Created on 18/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.estructuraC;

/**
 * @author Gisela y Guillermo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DatoC {
	private PosActual datoC;
	private DatoC siguiente;

	public DatoC(PosActual datoC){
		this.datoC=new PosActual(datoC.getPuntoReferencia(), datoC.getPosBitacora(), datoC.getPosBloque(), datoC.getTiempo());
		this.siguiente=null;
	}
	
	public int getPuntoReferencia(){
		return this.datoC.getPuntoReferencia();
	}
	
	public int getPosBitacora(){
		return this.datoC.getPosBitacora();
	}
	public int getPosBloque(){
		return this.datoC.getPosBloque();
	}
	
	public DatoC getSiguiente(){
		return siguiente;
	}
	public float getTiempo(){
		return this.datoC.getTiempo();
	}
	public void setSiguiente(DatoC siguiente){
		this.siguiente=siguiente;
	}
}