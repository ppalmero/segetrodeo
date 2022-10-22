/*
 * Created on 18/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.bitacora;

import bdet.comun.Accesos;
import bdet.comun.Constantes;
import bdet.comun.Punto;

/**
 * @author Gisela y Guillermo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Bitacora {
	private BloqueBitacora[] bloques;
	private int indiceActual; //me dice el bloque
	private int cantBloq;
	private Accesos estadisticaBit; // Estad�sticas de lectura
	private int indiceA; //para consulta Trayectoria
	
	public Bitacora(int cantBloques, int indiceA){
		this.bloques=new BloqueBitacora[cantBloques];
		this.indiceActual=0;
		this.bloques[indiceActual] = new BloqueBitacora(indiceA, indiceActual);
		this.estadisticaBit=new Accesos();
		this.estadisticaBit.bloques_leidos=0;
		this.cantBloq=cantBloques;
		this.indiceA=indiceA;
	}
	
	public int getEstadisticaBit(){
		return (estadisticaBit.bloques_leidos);
	}
	public void incrementarAcceso(){
		estadisticaBit.bloques_leidos++;
	}
	public boolean estaLlenaBit(float cantMov){
                
		return (cantMov > (Constantes.TAMANO_BITACORA*Constantes.TAMANO_BLOQUE_BITACORA)-
				    ((indiceActual*Constantes.TAMANO_BLOQUE_BITACORA)+bloques[indiceActual].getIndiceBloque()));
                
		/*if ((indiceActual==cantBloq-1)&&(bloques[indiceActual].estaLleno())){
			return true;
		}else{
			return false;
		}*/
	}
	public int getIndiceActual(){
		return this.indiceActual;
	}
	
	public BloqueBitacora[] getBloques(){
		return this.bloques;
	}
	public BloqueBitacora getBloque(int pos, Accesos accesos){
		accesos.bloques_leidos++;
		return this.bloques[pos];
	}
	public int getIndiceBloque(int pos){
		return this.bloques[pos].getIndiceBloque();
	}
	public void insertarBitacora(float tiempo, Punto posicion, int oid, DatoBitacora refAnt){
		if (bloques[indiceActual].estaLleno()){
			this.indiceActual++;
			this.bloques[indiceActual] = new BloqueBitacora(this.indiceA, this.indiceActual);
		}
		this.bloques[indiceActual].insertarDatoB(oid, tiempo, posicion, refAnt);
	}
}