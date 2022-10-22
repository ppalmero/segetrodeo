/*
 * Created on 19/03/2005
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
public class PosActual {
	private int puntoReferencia; //indice de Estruc A
	private int posBitacora; //bloque de la bitacora
	private int posBloque;	//dato dentro del bloque
	private float tiempo;   //tiempo del la entrada de la bitacora

	public PosActual(int puntoReferencia, int posBitacora, int posBloque, float t){
		this.puntoReferencia=puntoReferencia; // n�mero de bit�cora
		this.posBitacora=posBitacora;
		this.posBloque=posBloque;
		this.tiempo=t;
	}
	
	public int getPuntoReferencia(){
		return puntoReferencia;
	}
	
	public int getPosBitacora(){
		return posBitacora;
	}
	public int getPosBloque(){
		return posBloque;
	}
	public void setPuntoReferencia(int punto){
		this.puntoReferencia=punto;
	}
	public void setPosBitacora(int pos){
		this.posBitacora=pos;
	}
	public void setPosBloque(int pos){
		this.posBloque=pos;
	}
	public float getTiempo(){
		return tiempo;
	}
	public void setTiempo(float t){
		this.tiempo=t;
	}
	
}
