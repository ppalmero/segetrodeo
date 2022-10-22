package bdet.comun;

import java.io.PrintWriter;

/*
 * Consulta.java
 * 
 * Clase que permite llevar las estad�sticas de consulta
 *  
 */

public class Accesos {
	public int bloques_leidos; // N�mero de bloques de disco le�dos
	
	
	public Accesos(){
	    limpiar();
	}
	
	public void limpiar(){
		bloques_leidos = 0;
	}
	
	public void imprimir(){
	    System.out.println("Bloques Le�dos = " + bloques_leidos);
	    System.out.println();
	}

	/**
	 * @param salida1
	 */
	public void imprimir(PrintWriter salida1) {
		salida1.println("Bloques Le�dos = " + bloques_leidos);
	    salida1.println();
	}
}