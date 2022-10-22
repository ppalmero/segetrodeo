package bdet.rtree;

import java.util.HashMap;

import bdet.comun.Accesos;
import bdet.comun.Rectangulo;

/*
 * RTree.java
 * 
 * Hilbert R-Tree, incluye m�todos de inserci�n y b�squeda
 *  
 */

public class RTree {
	private Nodo raiz; // Ra�z del �rbol
	private Estadistica estadistica; // Estad�sticas de lectura
	
	// Crea un nuevo R-Tree
	public RTree()
	{
	    estadistica = new Estadistica();
	    raiz = new Nodo(true, estadistica);
	}
	
	// Inserta los datos contenidos en un arreglo
	public void insertar(Dato[] objetos)
	{
	    for (int i = 0; i < objetos.length; i++)
	        insertar(new Dato(objetos[i]));
	}
	
	// Retorna en resultado los objetos que intersectan areaConsulta
	public void consultar(HashMap resultado,
	        Rectangulo areaConsulta,
	        Accesos estadisticaLlamada)
	{
	    raiz.consultar(resultado, areaConsulta);
	    estadisticaLlamada.bloques_leidos = estadisticaLlamada.bloques_leidos += (estadistica.paginas_hoja_leidas + estadistica.paginas_no_hoja_leidas);
	    //estadistica.imprimirEstadisticasConsulta();
	    estadistica.limpiar();
	}
	
	// Retorna el n�mero de bloques que utiliza el R-Tree
	public int getNumeroBloques()
	{
	    return raiz.getNumeroNodos();
	}
	
	// Inserta un dato en el R-Tree
	public void insertar(Dato d)
	{
	    raiz = raiz.insertar(d);
	}
}