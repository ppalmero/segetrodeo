package bdet.dsrtree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import bdet.bitacora.BloqueBitacora;
import bdet.bitacora.DatoBitacora;
import bdet.comun.Accesos;
import bdet.comun.Constantes;
import bdet.comun.ElemTray;
import bdet.comun.ParEnteros;
import bdet.comun.Punto;
import bdet.comun.Rectangulo;
import bdet.rtree.Dato;

/**
 * @author Gisela y Guillermo
 *
 */
public class Principal {

    public static void main(String[] args, PrintWriter salida2) throws IOException {
        //Generador.generarObjetos(100,10,20);
        if (args.length != 5) {
            System.out.println("Error. Par�metros incorrectos");
            System.exit(1);
        }
        // Archivo con datos de prueba
        BufferedReader datosEntrada = new BufferedReader(new FileReader(args[0]));

        // Archivo con consultas
        BufferedReader consultas = new BufferedReader(new FileReader(args[1]));

        // N�mero de objetos del archivo de prueba
        int numeroObjetos = Integer.parseInt(args[2]);

        // Arreglo con todos los objetos para instante de tiempo dado
        Dato[] datosRtree = new Dato[numeroObjetos];

        // Porcentaje de mov
        float porcMov = (float) (Integer.parseInt(args[3]) / 100.0);

        // Crea un nuevo DRSTree
        DSRTree dsr = new DSRTree(numeroObjetos);

        // Archivo de salida
        PrintWriter salida1 = new PrintWriter(
                new BufferedWriter(new FileWriter(args[4])));

        String lineaActual;

        int oid; // OID del objeto
        float tiempo = 0; // tiempo en que se produce el movimiento
        float x1, y1 = 0; // "MBR" del objeto
        int i = 0; //para iterar	   
        int porcentajeMov;
        while ((i < numeroObjetos) && (lineaActual = datosEntrada.readLine()) != null) {

            StringTokenizer s = new StringTokenizer(lineaActual);

            // Se leen los datos de la l�nea actual
            oid = Integer.parseInt(s.nextToken()) - 1;
            tiempo = Float.parseFloat(s.nextToken()) * 100;
            x1 = Float.parseFloat(s.nextToken());
            y1 = Float.parseFloat(s.nextToken());
//	        x1 = Float.parseFloat(s.nextToken());
//	        y1 = Float.parseFloat(s.nextToken());
            //tiempo = Float.parseFloat(s.nextToken());

            //int porcenta=Integer.parseInt(s.nextToken());
            //System.out.println(oid +" "+ x1 +" "+ y1 +" "+ tiempo);
            datosRtree[i++] = new Dato(new Rectangulo(x1, y1, x1, y1), oid);
            dsr.getC().getEntradaC(oid).setPosInicial(x1, y1);
        }
        dsr.cargaInicial(datosRtree);
        i = 1;
        float tiempoViejo = 0;
        while ((lineaActual = datosEntrada.readLine()) != null) {

            StringTokenizer s = new StringTokenizer(lineaActual);
            int indA = dsr.getA().getIndice();

            // Se leen los datos de la l�nea actual
            // Se leen los datos de la l�nea actual
            oid = Integer.parseInt(s.nextToken()) - 1;
            tiempo = Float.parseFloat(s.nextToken()) * 100;
            x1 = Float.parseFloat(s.nextToken());
            y1 = Float.parseFloat(s.nextToken());
//	        x1 = Float.parseFloat(s.nextToken());
//	        y1 = Float.parseFloat(s.nextToken());
            //tiempo = Float.parseFloat(s.nextToken());
//	        porcentajeMov=Integer.parseInt(s.nextToken());
            if (tiempo != tiempoViejo) {

                if (dsr.getA().entradasA[indA].getBitacora().estaLlenaBit(porcMov * numeroObjetos)) {
                    System.out.println("OBJETOS INSERTADOS -------- " + i);
                    dsr.actualizar(tiempo, indA, datosRtree);
                }
                BloqueBitacora bloque[] = dsr.getA().getEntradaA(dsr.getA().getIndice()).getBitacora().getBloques();
                int indiceBloque = bloque[dsr.getA().getEntradaA(dsr.getA().getIndice()).getBitacora().getIndiceActual()].getIndiceBloque();
                if (indiceBloque == Constantes.TAMANO_BLOQUE_BITACORA) {
                    indiceBloque = 0;
                }
                dsr.getB().agregarEntrada(tiempo,
                        dsr.getA().getIndice(),
                        dsr.getA().getEntradaA(dsr.getA().getIndice()).getBitacora().getIndiceActual(),
                        indiceBloque);
            }
            tiempoViejo = tiempo;

            Punto pos = new Punto(x1, y1);
            DatoBitacora dato = new DatoBitacora(oid, tiempo, pos, null, dsr.getA().indiceA, dsr.getA().getEntradaA(dsr.getA().indiceA).getBitacora().getIndiceActual());
            dsr.cargaMovimiento(dato);
            i++;
        }

        float x2 = 0, y2 = 0;
        float tiempo2;
        int nConsultas = 0;
        int oidCon = 0;

        // Resultado de consultas eventos (dice cuantos 
        //objetos entraron y cuantos salieron)
        ParEnteros p = new ParEnteros();
        int tamanoTotal;

        tamanoTotal = dsr.tamanoEstructura(numeroObjetos, salida1);
        Accesos totalAccesos = new Accesos();
        Accesos accesos = new Accesos();

        dsr.getMovimientos(100.0f);
        //CONSULTAS
        while ((lineaActual = consultas.readLine()) != null) {
            StringTokenizer s = new StringTokenizer(lineaActual);

            //s.nextToken();
            // Se leen los datos de la l�nea actual
            x1 = Float.parseFloat(s.nextToken());
            if (x1 == -2) {
                oidCon = Integer.parseInt(s.nextToken()) - 1;
                tiempo = Float.parseFloat(s.nextToken()) * 100;
                tiempo2 = Float.parseFloat(s.nextToken()) * 100 + 30;
                if (tiempo > 60) {
                    tiempo = 60;
                    tiempo2 = 100;
                }
//	        	tiempo=(float) 0.0;
//	        	tiempo2=(float) 100.0;
            } else {
                y1 = Float.parseFloat(s.nextToken());
                x2 = Float.parseFloat(s.nextToken());
                y2 = Float.parseFloat(s.nextToken());
                tiempo = Float.parseFloat(s.nextToken()) * 100;

                tiempo2 = s.hasMoreTokens() ? Float.parseFloat(s.nextToken()) * 100 : -1;
                //s.nextToken();
            }

            System.out.println("------------------------------------------------------");

            if (tiempo2 == -1) {
                System.out.println("Consulta TimeSlice - Tiempo: " + tiempo);
                salida1.println("Consulta TimeSlice - Tiempo: " + tiempo);
                System.out.println();
                HashMap res = new HashMap();
                dsr.timeSliceQuery(res, new Rectangulo(x1, y1, x2, y2), tiempo, accesos);

                Collection c = res.values();
                System.out.println("Objetos encontrados: " + c.size());
                salida1.println("Objetos encontrados: " + c.size());
                Iterator iter = c.iterator();
                while (iter.hasNext()) {
                    Dato da = (Dato) iter.next();
                    salida1.println(da.getLimites() + " " + da.getOid());
                }
                System.out.print("Total: ");
                salida1.print("Total: ");
                accesos.imprimir(salida1);
                totalAccesos.bloques_leidos += accesos.bloques_leidos;
                accesos.limpiar();
            } /**
             * ****************************************
             */
            else if (tiempo2 == -300) {
                System.out.println("Consulta Evento - Tiempo: " + tiempo);
                salida1.println("Consulta Evento - Tiempo: " + tiempo);
                System.out.println();
                p.setA(0);
                p.setB(0);
                //dsr.eventQuery(p, new Rectangulo(x1, y1, x2, y2), tiempo, accesos);
                //dsr.eventQuery(p, new Rectangulo(0.0f, 0.0f, 1.0f, 1.0f), 2.0f, accesos);
                System.out.println("Objetos encontrados: " + (p.getA() + p.getB()));
                System.out.println("Entraron " + p.getA() + " y salieron " + p.getB() + " objetos");
                System.out.print("Total: ");
                salida1.println("Objetos encontrados: " + (p.getA() + p.getB()));
                salida1.println("Entraron " + p.getA() + " y salieron " + p.getB() + " objetos");
                salida1.print("Total: ");
                accesos.imprimir(salida1);
                totalAccesos.bloques_leidos += accesos.bloques_leidos;
                accesos.limpiar();
            } /**
             * **************************************
             */
            else if (x1 == -2) {
                Vector result = new Vector();
                System.out.println("Consulta Trayectoria - Objeto: " + oidCon + " - Tiempo: [" + tiempo + "," + tiempo2 + "]");
                salida1.println("Consulta Trayectoria - Objeto: " + oidCon + " - Tiempo: [" + tiempo + "," + tiempo2 + "]");
                System.out.println();

                dsr.trayectoriaQuery(result, oidCon, tiempo, tiempo2, accesos);
                Iterator a = result.iterator();
                System.out.println("Trayectoria:");
                salida1.println("Trayectoria:");
                while (a.hasNext()) {
                    ElemTray ele = (ElemTray) a.next();
                    System.out.println("Tiempo: " + ele.tiempo + " - Posicion:" + ele.x + ", " + ele.y);
                    salida1.println("Tiempo: " + ele.tiempo + " - Posicion:" + ele.x + ", " + ele.y);
                }

                System.out.print("Total: ");
                salida1.print("Total: ");
                accesos.imprimir(salida1);
                totalAccesos.bloques_leidos += accesos.bloques_leidos;
                accesos.limpiar();
//	        	Vector result = new Vector();
//	            System.out.println("Consulta Trayectoria DRTree - Objeto: " + oidCon + " - Tiempo: ["+ tiempo + "," + tiempo2 + "]");
//	            salida1.println("Consulta Trayectoria DRTree - Objeto: " + oidCon + " - Tiempo: ["+ tiempo + "," + tiempo2 + "]");
//	            System.out.println();
//	                       
//	            dsr.trayectoriaOpQuery(result, oidCon, tiempo, tiempo2, accesos);
//	            Iterator a = result.iterator();
//	            System.out.println("TrayectoriaOp:");
//	            salida1.println("TrayectoriaOp:");
//	            while (a.hasNext()) {
//	            	ElemTray ele=(ElemTray)a.next();
//	            	System.out.println("Tiempo: "+ ele.tiempo + " - Posicion:" + ele.x + ", " + ele.y);
//	            	salida1.println("Tiempo: "+ ele.tiempo + " - Posicion:" + ele.x + ", " + ele.y);
//	                }
//	           
//	            System.out.print("Total: ");
//	            salida1.print("Total: ");
//	            accesos.imprimir(salida1);
//	            totalAccesos.bloques_leidos += accesos.bloques_leidos;
//	            accesos.limpiar();
            } else {
                System.out.println("Consulta Intervalo - Tiempo: [" + tiempo + "," + tiempo2 + "]");
                salida1.println("Consulta Intervalo - Tiempo: [" + tiempo + "," + tiempo2 + "]");
                System.out.println();
                salida1.println();
                HashMap resultado = new HashMap();
                dsr.intervalQuery(resultado, new Rectangulo(x1, y1, x2, y2), tiempo, tiempo2, accesos);

                Collection c = resultado.values();
                System.out.println("Objetos encontrados: " + c.size());
                salida1.println("Objetos encontrados: " + c.size());
                Iterator j = c.iterator();
                while (j.hasNext()) {
                    Dato da = (Dato) j.next();
                    salida1.println(da.getLimites() + " " + da.getOid());
                }
                System.out.print("Total: ");
                salida1.print("Total: ");
                accesos.imprimir(salida1);
                totalAccesos.bloques_leidos += accesos.bloques_leidos;
                accesos.limpiar();
            }
            nConsultas++;
        }
        System.out.println("TAMAÑO D*R-TREE BLOQUES: " + tamanoTotal + " BLOQUES LEIDOS PROMEDIO: " + totalAccesos.bloques_leidos / (float) nConsultas);
        salida1.println("TAMAÑO D*R-TREE BLOQUES: " + tamanoTotal + " BLOQUES LEIDOS PROMEDIO: " + totalAccesos.bloques_leidos / (float) nConsultas);

        salida2.println(porcMov * 100 + " " + totalAccesos.bloques_leidos / (float) nConsultas);

        datosEntrada.close();
        salida1.close();
    }
}
