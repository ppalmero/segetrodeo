/*
 * Created on 31/03/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.estructuraA;

import bdet.comun.Constantes;
import bdet.bitacora.Bitacora;
import bdet.rtree.RTree;

/**
 * @author Gisela y Guillermo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EntradaA {
	private RTree rtree;
	private Bitacora bitacora;
	private float puntoReferencia;
	
	public EntradaA(RTree rtreeA, Bitacora bitacora, float puntoReferencia, int indiceA){
		this.rtree=new RTree();
		this.rtree=rtreeA;
		this.bitacora=new Bitacora(Constantes.TAMANO_BITACORA, indiceA);
		this.puntoReferencia=puntoReferencia;
	}
	
	public RTree getRTree(){
		return this.rtree;
	}
	
	public float getPuntoReferencia(){
		return this.puntoReferencia;
	}
	public Bitacora getBitacora(){
		return this.bitacora;
	}
}
