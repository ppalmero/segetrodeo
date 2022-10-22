package bdet.dsrtree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import bdet.comun.Constantes;

/*
 * Created on 03/01/2006
 *
 */
public class Pruebas {
	public static void main(String[] args) throws IOException{
		String por; 
		String bloq=""+Constantes.TAMANO_BITACORA;
		String Cantidad="1000";
		PrintWriter salida = new PrintWriter(new BufferedWriter(new FileWriter("C:/Datos/Documentos Gise/Trabajo/Trabajo Tesis/EspacioTemporales/Informe-actividades/informe final/pruebaslala/DSRTREE/evento/evento_5/"+ Cantidad +"/DS"+bloq)));
		
		por="1";
		String str[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_5_"+Cantidad+"_"+por+"_40"};
		Principal.main(str, salida);
		por="3";
		String str1[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_5_"+Cantidad+"_"+por+"_40"};
		Principal.main(str1, salida);
		por="5";
		String str2[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_5_"+Cantidad+"_"+por+"_40"};
		Principal.main(str2, salida);
		por="7";
		String str3[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_"+Cantidad+"_"+por+"_5"};
		Principal.main(str3, salida);
		por="9";
		String str4[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_"+Cantidad+"_"+por+"_5"};
		Principal.main(str4, salida);
		por="11";
		String str5[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_"+Cantidad+"_"+por+"_5"};
		Principal.main(str5, salida);
		por="13";
		String str6[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_"+Cantidad+"_"+por+"_5"};
		Principal.main(str6, salida);
		por="15";
		String str7[]={"datos/prueba_"+Cantidad+"_"+por,"consultas/evento_5",Cantidad, por, "pruebas/eventoLala/"+bloq+"bloques/sal_evento_"+Cantidad+"_"+por+"_5"};
		Principal.main(str7, salida);
		
		salida.close();
		

		

	}
}
