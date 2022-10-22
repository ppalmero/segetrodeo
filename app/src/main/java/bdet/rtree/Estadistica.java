package bdet.rtree;


public class Estadistica {
	public int mbr_encontrados; // N�mero de objetos encontrados
	public int paginas_hoja_leidas; // N�mero de nodos hoja le�dos
	public int paginas_no_hoja_leidas; // N�mero de nodos no hoja le�dos
	
	// Crea una nueva Estad�stica
	public Estadistica(){
	    limpiar();
	}
	
	// Imprime la estad�stica
	public void imprimirEstadisticasConsulta(){
	    System.out.println("CONSULTANDO R-TREE");
	    System.out.println("P�ginas Leidas	: " + (paginas_hoja_leidas + paginas_no_hoja_leidas));
	    System.out.println("		Hoja	: " + paginas_hoja_leidas);
	    System.out.println("		No Hoja	: " + paginas_no_hoja_leidas);
	    System.out.println("MBRs Encontrados: " + mbr_encontrados);
	    System.out.println();
	}
	
	// Reinicializa la estad�stica
	public void limpiar(){
	    mbr_encontrados = 0;
	    paginas_hoja_leidas = 0;
	    paginas_no_hoja_leidas = 0;
	}
}