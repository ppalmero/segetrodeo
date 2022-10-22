package bdet;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import bdet.bitacora.BloqueBitacora;
import bdet.bitacora.DatoBitacora;
import bdet.comun.Constantes;
import bdet.comun.Punto;
import bdet.comun.Rectangulo;
import bdet.dsrtree.DSRTree;
import bdet.rtree.Dato;

public class InitDSTree {
    private void cargarPosicionInicial(JSONObject reader) {
        //si se trabaja con d*rtree en el celular
    }

    private void obtenerDatos() {

    }

    private void inicializarEstructura() {
        String por;
        String bloq = "" + Constantes.TAMANO_BITACORA;
        String Cantidad = "300";
        //salida = new PrintWriter(new BufferedWriter(new FileWriter("C:/Datos/Documentos Gise/Trabajo/Trabajo Tesis/EspacioTemporales/Informe-actividades/informe final/pruebaslala/DSRTREE/evento/evento_5/" + Cantidad + "/DS" + bloq)));
        por = "7";
        String str[] = {"datos/prueba_" + Cantidad + "_" + por + "", "consultas/evento_5", Cantidad, por, "pruebas/eventoLala/" + bloq + "bloques/sal_evento_5_" + Cantidad + "_" + por + "_40"};
        if (str.length != 5) {
            System.out.println("Error. Parámetros incorrectos");
            System.exit(1);
        }
        try{
            // Archivo con datos de prueba
            BufferedReader datosEntrada = new BufferedReader(new FileReader(str[0]));

            // Archivo con consultas
            //BufferedReader consultas = new BufferedReader(new FileReader(str[1]));

            // Número de objetos del archivo de prueba
            int numeroObjetos = Integer.parseInt(str[2]);

            // Arreglo con todos los objetos para instante de tiempo dado
            Dato[] datosRtree = new Dato[numeroObjetos];

            // Porcentaje de mov
            float porcMov = (float) (Integer.parseInt(str[3]) / 100.0);

            // Crea un nuevo DRSTree
            DSRTree dsr = new DSRTree(numeroObjetos);

            // Archivo de salida
            /*PrintWriter salida1 = new PrintWriter(
                    new BufferedWriter(new FileWriter(str[4])));
             */
            String lineaActual;

            int oid; // OID del objeto
            float tiempo = 0; // tiempo en que se produce el movimiento
            float x1, y1 = 0; // "MBR" del objeto
            int i = 0; //para iterar
            int porcentajeMov;
            while ((i < numeroObjetos) && (lineaActual = datosEntrada.readLine()) != null) {

                StringTokenizer s = new StringTokenizer(lineaActual);

                // Se leen los datos de la línea actual
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
            ArrayList<Float> momentos = new ArrayList<>();
            while ((lineaActual = datosEntrada.readLine()) != null) {

                StringTokenizer s = new StringTokenizer(lineaActual);
                int indA = dsr.getA().getIndice();

                // Se leen los datos de la línea actual
                oid = Integer.parseInt(s.nextToken()) - 1;
                tiempo = Float.parseFloat(s.nextToken()) * 100;
                x1 = Float.parseFloat(s.nextToken());
                y1 = Float.parseFloat(s.nextToken());
                /*if (tiempo == 104.0){
                    break;
                }*/
                if (tiempo != tiempoViejo) {
                    try{
                        if (dsr.getA().entradasA[indA].getBitacora().estaLlenaBit(porcMov * numeroObjetos)) {
                            System.out.println("OBJETOS INSERTADOS -------- " + i);
                            dsr.actualizar(tiempo, indA, datosRtree);
                        }
                    }catch (ArrayIndexOutOfBoundsException ex){
                        System.out.println(ex.getMessage());
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
                momentos.add(tiempoViejo);
                tiempoViejo = tiempo;

                Punto pos = new Punto(x1, y1);
                DatoBitacora dato = new DatoBitacora(oid, tiempo, pos, null, dsr.getA().indiceA, dsr.getA().getEntradaA(dsr.getA().indiceA).getBitacora().getIndiceActual());
                try {
                    dsr.cargaMovimiento(dato);
                } catch (ArrayIndexOutOfBoundsException ex){
                    System.out.println(i);
                }
                i++;
            }
        /* LECTURA DE DATOS RELACIONALES

        try {
            NetworkServerControl server = new NetworkServerControl();
            server.start (null);
            String driver = "org.apache.derby.jdbc.ClientDriver";
            Class.forName(driver).newInstance();
            String dbName = "SegETRodeo";
            String connectionURL = "jdbc:derby://localhost:1527/" + dbName + "";

            conn = DriverManager.getConnection(connectionURL, "root", "GCyBD2017");
        } catch (SQLException ex) {
            Logger.getLogger(FramePruebas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
