/*
 * Created on 09/04/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bdet.dsrtree;

import bdet.bitacora.Bitacora;
import bdet.bitacora.BloqueBitacora;
import bdet.bitacora.DatoBitacora;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import bdet.comun.Constantes;
import bdet.comun.ElemTray;
import bdet.comun.Accesos;
import bdet.comun.EntradasSalidas;
import bdet.comun.ParEnteros;
import bdet.comun.Rectangulo;
import bdet.estructuraA.EntradaA;
import bdet.estructuraC.DatoC;
import bdet.estructuraC.PosActual;
import java.util.ArrayList;
import java.util.TreeMap;
import bdet.rtree.Dato;
import bdet.rtree.RTree;
import bdet.comun.Punto;
import bdet.estructuraB.EntradaB;
import bdet.estructuraC.EntradaC;
import android.graphics.Point;

/**
 * @author Gisela
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DSRTree {

    private EstructuraA A;
    private EstructuraB B;
    private EstructuraC C;
    private int cantObjetos;

    public DSRTree(int cantObj) {
        A = new EstructuraA();
        B = new EstructuraB(1300);//50 PABLO
        C = new EstructuraC(cantObj);
        cantObjetos = cantObj;
    }

    public EstructuraA getA() {
        return this.A;
    }

    public EstructuraB getB() {
        return this.B;
    }

    public EstructuraC getC() {
        return this.C;
    }

    //Carga inicial / inserta en el Rtree
    public void cargaInicial(Dato[] datos) {
        RTree tree = new RTree();
        tree.insertar(datos);
        A.insertarA(0, tree, new Bitacora(Constantes.TAMANO_BITACORA, 0));
        A.setIndice(0);
    }

    private DatoBitacora getDatoBitacoraC(PosActual pos) {
        DatoBitacora p;
        Accesos accesos = new Accesos();
        if (pos.getPuntoReferencia() == -1) {
            return null;
        } else {
            int i = pos.getPuntoReferencia();
            int j = pos.getPosBloque();
            int k = pos.getPosBitacora();
            Bitacora bit = A.entradasA[i].getBitacora();
            BloqueBitacora bloq = bit.getBloque(k, accesos);
            p = bloq.getDatoB(j);
            return p;
        }
    }

    public int tamanoEstructura(int cantObj, PrintWriter salida1) {
        int totalRTree = 0, totalBit = 0, referencias = this.A.getIndice(), totalC = 0;
        System.out.println("Espacio Utilizado por el D*R-Tree:");
        System.out.println("----------------------------------");
        salida1.println("Espacio Utilizado por el D*R-Tree:");
        salida1.println("----------------------------------");
        EntradaA entrada;
        for (int i = 0; i <= referencias; i++) {
            entrada = this.A.entradasA[i];
            System.out.println("Tiempo: " + entrada.getPuntoReferencia());
            salida1.println("Tiempo: " + entrada.getPuntoReferencia());
            System.out.println("	R-Tree	 Nº BLOQUES = " + entrada.getRTree().getNumeroBloques());
            salida1.println("R-Tree	 Nº BLOQUES = " + entrada.getRTree().getNumeroBloques());
            System.out.println("	Bitacora Nº BLOQUES = " + (entrada.getBitacora().getIndiceActual() + 1));
            salida1.println("Bitacora Nº BLOQUES = " + (entrada.getBitacora().getIndiceActual() + 1));
            System.out.println("----------------------------------");
            salida1.println("----------------------------------");
            totalRTree += entrada.getRTree().getNumeroBloques();
            totalBit += (entrada.getBitacora().getIndiceActual()) + 1;
        }
        for (int i = 0; i < cantObj; i++) {
            totalC += C.getEntradaC(i).getIndice();
        }
        totalC = (totalC * 16 + 4 * cantObj) / 1024;
        System.out.println();
        salida1.println();
        System.out.println("R-Trees	     Nº BLOQUES = " + totalRTree);
        salida1.println("R-Trees	     Nº BLOQUES = " + totalRTree);
        System.out.println("Bitacora     Nº BLOQUES = " + totalBit);
        salida1.println("Bitacora     Nº BLOQUES = " + totalBit);
        System.out.println("Estructura C Nº BLOQUES = " + totalC);
        salida1.println("Estructura C Nº BLOQUES = " + totalC);
        System.out.println("TOTAL	     Nº BLOQUES = " + (totalBit + totalRTree + totalC));
        salida1.println("TOTAL	     Nº BLOQUES = " + (totalBit + totalRTree + totalC));
        System.out.println();
        salida1.println();
        System.out.println("----------------------------------");
        salida1.println("----------------------------------");
        return (totalBit + totalRTree + totalC);

    }

    // carga en la bitacora 
    public void cargaMovimiento(DatoBitacora dato) {
        int indiceA = A.getIndice();
        Accesos accesos = new Accesos();
        int i = 0;
        dato.setRefAnt(getDatoBitacoraC(C.getPosicionesActuales(dato.getOid())));
        A.entradasA[indiceA].getBitacora().insertarBitacora(dato.getTiempo(), dato.getPosicionActual(),
                dato.getOid(), dato.getRefAnt());
        Bitacora tempBit = A.entradasA[indiceA].getBitacora();
        int posBitacora = tempBit.getIndiceActual();
        C.setPosicionesActuales(dato.getOid(), indiceA, posBitacora, tempBit.getBloque(posBitacora, accesos).getIndiceBloque() - 1, dato.getTiempo());
    }

    // Actualiza DSRTree con los correspondientes datos para un tiempo dado
    public void actualizar(float tiempo, int puntoReferencia, Dato[] datosRtreeAnt) {
        int i = 0;
        float x1;
        float y1;
        Dato[] datosRtree = new Dato[cantObjetos];
        while (i < cantObjetos) {
            if (getDatoBitacoraC(C.getPosicionesActuales(i)) != null) {
                x1 = getDatoBitacoraC(C.getPosicionesActuales(i)).getPosicionActual().x;
                y1 = getDatoBitacoraC(C.getPosicionesActuales(i)).getPosicionActual().y;
            } else {
                x1 = datosRtreeAnt[i].getLimites().getCentro().x;
                y1 = datosRtreeAnt[i].getLimites().getCentro().y;
            }
            datosRtree[i] = new Dato(new Rectangulo(x1, y1, x1, y1), i);
            PosActual tempPos = C.getPosicionesActuales(i);
            if (tempPos.getPuntoReferencia() == puntoReferencia) {
                C.insertarDatoC(new DatoC(tempPos), i);
            }
            i++;
        }
        RTree tree = new RTree();
        tree.insertar(datosRtree);
        A.insertarA(tiempo, tree, new Bitacora(Constantes.TAMANO_BITACORA, A.indiceA));
        System.out.println("Actualizar C");

    }

    public void mostrarSalida(String salida, DSRTree dsr) throws IOException {

        PrintWriter archOut = new PrintWriter(new BufferedWriter(new FileWriter(salida)));
        int a = 0;
        Accesos accesos = new Accesos();
        while (dsr.getA().entradasA[a] != null) {
            EntradaA prefActual = dsr.getA().getEntradaA(a);
            archOut.println("Punto de Referencia: " + prefActual.getPuntoReferencia());
            archOut.println("Bitacora: " + a);
            Bitacora bit = prefActual.getBitacora();
            for (int i = 0; i < 5; i++) {
                archOut.println("Bloque: " + i);
                for (int j = 0; j < 20; j++) {

                    DatoBitacora db = bit.getBloque(i, accesos).getDatoB(j);
                    archOut.println("Entrada: " + db.getOid() + " - " + db.getTiempo() + " - " + db.getPosicionActual().x + ", " + db.getPosicionActual().y);
                }
            }

            archOut.println();
            a++;
        }
        archOut.println("Estructura C: ");
        for (int c = 0; c < 100; c++) {
            archOut.println("Entrada C: oid " + c);
            DatoC dc = C.getEntradaC(c).getEntradaLista();
            while (dc != null) {
                archOut.print("Bit: " + dc.getPuntoReferencia() + " - Bl " + dc.getPosBitacora() + " - pos " + dc.getPosBloque() + " ->");
                dc = dc.getSiguiente();
            }
            archOut.println();
        }
        archOut.close();
    }

//	 Encuentra todos los objetos que entraron en areaConsulta o la abandonaron
//	 en el instante de tiempo indicado
    public void eventQuery(EntradasSalidas resultado, Rectangulo areaConsulta, float tiempo, Accesos accesos) {

        int indiceB = this.B.getIndice(tiempo);
        DatoBitacora auxDatoBit;
        Bitacora bit = A.getEntradaA(this.B.getBitacora(indiceB)).getBitacora();
        BloqueBitacora bloque = A.getEntradaA(this.B.getBitacora(indiceB)).getBitacora().getBloque(this.B.getBloque(indiceB), accesos);
        DatoBitacora fila = bloque.getDatoB(this.B.getFila(indiceB));
        int indiceFila = this.B.getFila(indiceB);
        int indiceBloque = this.B.getBloque(indiceB);
        int entraron = 0, salieron = 0;

        // pregunto si el tiempo es igual al tiempo m�s bajo (0) porque si es asi tiene que buscar en  arbol R0
        if (tiempo == this.A.entradasA[0].getPuntoReferencia()) {
            HashMap resultadoHash = new HashMap();
            this.A.entradasA[0].getRTree().consultar(resultadoHash, areaConsulta, accesos);
            Collection c;
            int y = 0;
            c = resultadoHash.values();
            while (y < c.size()) {
                entraron++;
                y++;
            }
            resultado.addEntrada(entraron);
            /**
             * * VER **
             */
        } else {
            while ((fila != null) && (fila.getTiempo() == tiempo)) {
                // si esta dentro del area
                if (areaConsulta.intersecta(fila.getPosicionActual())) {
                    auxDatoBit = fila.getRefAnt();
                    if (auxDatoBit == null) { //me fijo en la posicion inicial
                        //no tiene que estar antes dentro del area
                        if (!areaConsulta.intersecta(C.getEntradaC(fila.getOid()).getPosInicial())) {
                            entraron++;
                            System.out.println("oid " + fila.getOid() + " entró");
                            resultado.addEntrada(fila.getOid());
                        }
                    } else if (!areaConsulta.intersecta(auxDatoBit.getPosicionActual())) {
                        //no tiene que estar antes dentro del area
                        entraron++;
                        System.out.println("oid " + fila.getOid() + " entró");
                        resultado.addEntrada(fila.getOid());
                    }
                } //si no esta dentro del area
                else {
                    auxDatoBit = fila.getRefAnt();
                    if (auxDatoBit == null) {
                        if (areaConsulta.intersecta(C.getEntradaC(fila.getOid()).getPosInicial())) {
                            //tiene que estar antes dentro del area
                            salieron++;
                            System.out.println("oid " + fila.getOid() + " salió");
                            resultado.addSalida(fila.getOid());
                        }
                    } else if (areaConsulta.intersecta(auxDatoBit.getPosicionActual())) {
                        //tiene que estar antes dentro del area
                        salieron++;
                        System.out.println("oid " + fila.getOid() + " salió");
                        resultado.addSalida(fila.getOid());
                    }
                }
                indiceFila++;
                if (bloque.getIndiceBloque() > indiceFila) {
                    fila = bloque.getDatoB(indiceFila);
                } else {
                    indiceFila = 0;
                    indiceBloque++;
                    if (indiceBloque < bit.getIndiceActual()) {
                        bloque = A.getEntradaA(this.B.getBitacora(indiceB)).getBitacora().getBloque(indiceBloque, accesos);
                        fila = bloque.getDatoB(indiceFila);
                    } else {
                        fila = null;
                    }
                }
            }
            /*resultado.setA(entraron);
            resultado.setB(salieron);*/
        }
    }

    public void trayectoriaQuery(Vector result, int oidCon, float tiempo, float tiempo2, Accesos accesos) {
        DatoC datoC = null;
        DatoBitacora datoBit = null;
        float ultimoTiempo = tiempo2;
        int j = 0;
        //ElemTray elemento = new ElemTray(0,0,0);
        if (C.getEntradaC(oidCon).getEntradaLista() != null) {
            datoC = this.C.getEntradaC(oidCon).getEntradaLista();
            accesos.bloques_leidos++; // por acceder a la estructura C
            //recupero la posici�n a donde esta el elemento en la estruct A

            while (datoC.getSiguiente() != null && tiempo2 < datoC.getTiempo()) {
                datoC = datoC.getSiguiente();
                //accesos.bloques_leidos++;
            }
            if (tiempo <= datoC.getTiempo()) {
                datoBit = this.A.entradasA[datoC.getPuntoReferencia()].getBitacora().getBloque(datoC.getPosBitacora(), accesos).getDatoB(datoC.getPosBloque());
                //busco el 1� tiempo igual al tiempo maximo que estoy buscando
                while (datoBit != null && datoBit.getTiempo() > tiempo2) {
                    if ((datoBit.getRefAnt() != null) && ((datoBit.getIndiceBl() != datoBit.getRefAnt().getIndiceBl()) || (datoBit.getIndiceA() != datoBit.getRefAnt().getIndiceA()))) {
                        accesos.bloques_leidos++;
                    }
                    datoBit = datoBit.getRefAnt();
                }
                while (datoBit != null && datoBit.getTiempo() <= tiempo2 && datoBit.getTiempo() >= tiempo) {
                    ElemTray elemento = new ElemTray(0, 0, 0);
                    elemento.x = datoBit.getPosicionActual().x;
                    elemento.y = datoBit.getPosicionActual().y;
                    elemento.tiempo = datoBit.getTiempo();
                    ultimoTiempo = elemento.tiempo;

                    System.out.println("oid " + oidCon + ": posX " + elemento.x + " posY " + elemento.y + " tiempo " + elemento.tiempo);
                    result.add(j++, elemento);
                    if ((datoBit.getRefAnt() != null) && ((datoBit.getIndiceBl() != datoBit.getRefAnt().getIndiceBl()) || (datoBit.getIndiceA() != datoBit.getRefAnt().getIndiceA()))) {
                        accesos.bloques_leidos++;
                    }
                    datoBit = datoBit.getRefAnt();
                }
            } else {
                // se movi� pero no en el intervalo que se pide y por ende se mantuvo en la misma posici�n
                // del ultimo movimiento (antes del limite inferior del intervalo)
                datoBit = this.A.entradasA[datoC.getPuntoReferencia()].getBitacora().getBloque(datoC.getPosBitacora(), accesos)
                        .getDatoB(datoC.getPosBloque());
                System.out.println("punto de ref menor al tiempo (limite inferior)");
                while (ultimoTiempo >= tiempo) {
                    ElemTray elemento = new ElemTray(0, 0, 0);
                    elemento.x = datoBit.getPosicionActual().x;
                    elemento.y = datoBit.getPosicionActual().y;
                    elemento.tiempo = ultimoTiempo;
                    ultimoTiempo--;
                    result.add(j++, elemento);
                    System.out.println("oid " + oidCon + " posX " + elemento.x + " posY " + elemento.y + " tiempo " + elemento.tiempo);
                }
            }
        }
        //CUANDO refAnt ES IGUAL A null BUSCAR EN estructura C (posicion inicial)
        if (datoC != null) {
            ultimoTiempo--;
        }
        if (datoBit == null) {
            accesos.bloques_leidos++; // por acceder a la estructura C
            System.out.println("cuando datoBit es null");
            // cuando no se movio nunca, posici�n inicial
            while (ultimoTiempo >= tiempo) {
                ElemTray elemento = new ElemTray(0, 0, 0);
                elemento.x = C.getEntradaC(oidCon).getPosInicial().x;
                elemento.y = C.getEntradaC(oidCon).getPosInicial().y;
                elemento.tiempo = ultimoTiempo;
                ultimoTiempo--;
                result.add(j++, elemento);
                System.out.println("oid " + oidCon + " posX " + elemento.x + " posY " + elemento.y + " tiempo " + elemento.tiempo);
            }
        } else {
            // para completar que paso desde el limite inferior del intervalo al primer mov dentro del intervalo
            while (ultimoTiempo >= tiempo) {
                ElemTray elemento = new ElemTray(0, 0, 0);
                elemento.x = datoBit.getPosicionActual().x;
                elemento.y = datoBit.getPosicionActual().y;
                elemento.tiempo = ultimoTiempo;
                ultimoTiempo--;
                result.add(j++, elemento);
                System.out.println("oid " + oidCon + " posX " + elemento.x + " posY " + elemento.y + " tiempo " + elemento.tiempo);
            }
        }
    }

    public void trayectoriaOpQuery(Vector result, int oidCon, float tiempo, float tiempo2, Accesos accesos) {
        int puntoRef = buscarEnA(tiempo2);
        int indiceBit, k = 0;
        BloqueBitacora bloque = null;
        Bitacora bitacora = null;
        DatoBitacora datoBit = null;

        for (int x = puntoRef; x >= 0; x--) {
            bitacora = A.getEntradaA(x).getBitacora();
            indiceBit = bitacora.getIndiceActual();
            for (int i = indiceBit; i >= 0; i--) {
                bloque = bitacora.getBloque(i, accesos);
                for (int j = bitacora.getIndiceBloque(i) - 1; j >= 0; j--) {
                    if ((bloque.getDatoB(j).getOid() == oidCon) && (bloque.getDatoB(j).getTiempo() >= tiempo)) {
                        ElemTray elemento = new ElemTray(0, 0, 0);
                        elemento.x = bloque.getDatoB(j).getPosicionActual().x;
                        elemento.y = bloque.getDatoB(j).getPosicionActual().y;
                        elemento.tiempo = bloque.getDatoB(j).getTiempo();
                        result.add(k++, elemento);
                        System.out.println("oid " + oidCon + " posX " + elemento.x + " posY " + elemento.y + " tiempo " + elemento.tiempo);
                    }
                    if (bloque.getDatoB(j).getTiempo() < tiempo) {
                        j = i = x = 0;
                    }
                }
            }
        }
        System.out.println("Accesos: " + accesos.bloques_leidos);
    }

    public void timeSliceQuery(HashMap resultado, Rectangulo espacio, float tiempo, Accesos accesos) {

        int puntoRef = buscarEnA(tiempo);

        int i, j;
        EntradaA entrada = A.getEntradaA(puntoRef);
        if (entrada.getPuntoReferencia() == tiempo) {
            entrada.getRTree().consultar(resultado, espacio, accesos);
        } else {
            entrada.getRTree().consultar(resultado, espacio, accesos);
            Bitacora bitacora = entrada.getBitacora();
            for (i = 0; i <= (bitacora.getIndiceActual()); i++) {
                BloqueBitacora bloque = bitacora.getBloque(i, accesos);
                for (j = 0; ((j < bloque.getIndiceBloque()) && (bloque.getDatoB(j).getTiempo() <= tiempo)); j++) {
                    if (resultado.containsKey(bloque.getDatoB(j).getOid2())) {
                        resultado.remove(bloque.getDatoB(j).getOid2());
                    }
                    if (espacio.intersecta(bloque.getDatoB(j).getPosicionActual())) {
                        resultado.put(bloque.getDatoB(j).getOid2(),
                                new Dato(new Rectangulo(bloque.getDatoB(j).getPosicionActual()),
                                        bloque.getDatoB(j).getOid2().intValue()));
                    }
                }
                if (j < bloque.getIndiceBloque() && bloque.getDatoB(j).getTiempo() > tiempo) {
                    i = bitacora.getIndiceActual() + 1;
                }
            }
        }
    }

    public void intervalQuery(HashMap resultado, Rectangulo espacio, float tiempo1, float tiempo2, Accesos accesos) {
        int puntoRef = buscarEnA(tiempo1);
        int i, j;
        EntradaA entrada = A.getEntradaA(puntoRef);
        BloqueBitacora bloqueTemp = null;
        Bitacora bitacora = null;
        entrada.getRTree().consultar(resultado, espacio, accesos);
        bitacora = entrada.getBitacora();
        int inith = puntoRef;
        for (int h = inith; (h <= A.indiceA && entrada.getPuntoReferencia() <= tiempo2); h++) {
            for (i = 0; i <= (bitacora.getIndiceActual()); i++) {
                BloqueBitacora bloque = bitacora.getBloque(i, accesos);
                for (j = 0; (j < bloque.getIndiceBloque() && (bloque.getDatoB(j).getTiempo() <= tiempo2)); j++) {
                    /*PABLO agrego condición de tiempo para que devuelva los 
                    objetos que estvieron dentro del área en el intervalo de 
                    tiempo y salieron de la misma antes de finalizar el 
                    intervalo*/
                    if (resultado.containsKey(bloque.getDatoB(j).getOid2()) && bloque.getDatoB(j).getTiempo() < tiempo1) {
                        resultado.remove(bloque.getDatoB(j).getOid2());
                    }
                    if (espacio.intersecta(bloque.getDatoB(j).getPosicionActual())) {
                        resultado.put(bloque.getDatoB(j).getOid2(),
                                new Dato(new Rectangulo(bloque.getDatoB(j).getPosicionActual()),
                                        bloque.getDatoB(j).getOid2().intValue()));
                    }
                }
                if (j < bloque.getIndiceBloque() && bloque.getDatoB(j).getTiempo() > tiempo2) {
                    h = A.indiceA + 1;
                    i = Constantes.TAMANO_BITACORA;
                }

            }
            if (h < A.indiceA) {
                entrada = A.getEntradaA(h + 1);
                bitacora = entrada.getBitacora();
            }

        }
    }

    public int buscarEnA(float tiempo) {
        int central1 = 0;
        int bajo = 0;
        int alto = this.A.getIndice();
        int central = (bajo + alto) / 2;
        central1 = central;
        float temp = this.A.entradasA[central].getPuntoReferencia();
        while (bajo < alto && tiempo != temp) {
            if (tiempo > temp) {
                bajo = central + 1;
            } else {
                alto = central - 1;
            }
            central1 = central;
            central = (bajo + alto) / 2;
            temp = this.A.entradasA[central].getPuntoReferencia();
        }
        System.out.println(tiempo + " encontrado en punto de referencia " + central);
        if (tiempo == temp) {
            System.out.println(tiempo + " encontrado en punto de referencia " + central + " igual al punto de ref");
            //return central;
        } else {
            System.out.println(tiempo + " no existe en A");
            //return central1;
        }

        if (tiempo < temp) {//PABLO modificaciones por no encontrar a la vaca lilian en el día 2 a la hora 1
            return central - 1;
        }

        return central;
    }

    public TreeMap getMovimientos(float tiempo) {
        TreeMap mov = new TreeMap();
        float tiempoActual = 0.0f;
        ArrayList<DatoBitacora> movimientos = new ArrayList<>();
        for (int i = 0; i < this.getC().getEntradasC().length; i++) {
            movimientos.add(new DatoBitacora(i, 0.0f, this.getC().getEntradaC(i).getPosInicial(), null, -1, -1));
        }
        for (int i = 0; i <= this.getA().getIndice(); i++) {
            if (this.getA().getEntradaA(i).getPuntoReferencia() <= tiempo) {
                BloqueBitacora[] bloques = this.getA().getEntradaA(i).getBitacora().getBloques();
                for (int j = 0; j < bloques.length; j++) {
                    if (bloques[j] == null) {
                        break;
                    }
                    DatoBitacora[] datos = bloques[j].getDatosB();
                    for (int k = 0; k < datos.length; k++) {
                        if (datos[k] == null) {
                            break;
                        }
                        if (tiempoActual != datos[k].getTiempo()) {
                            mov.put(tiempoActual, movimientos);
                            movimientos = new ArrayList<>();
                            tiempoActual = datos[k].getTiempo();
                        }
                        movimientos.add(datos[k]);
                    }

                }
            }
        }
        return mov;
    }

    public ArrayList<Point> getPosicionEnTiempo(float f) {
        ArrayList<Point> resultado = new ArrayList<>();
        if (f == 0) {
            for (EntradaC entradasC : C.getEntradasC()) {
                resultado.add(new Point(entradasC.getPosInicial().getPoint()));
            }
        } else {
            for (EntradaC entradasC : C.getEntradasC()) {
                if (entradasC.getEntradaLista() != null /*&& entradasC.getEntradaLista().getTiempo() <= f*/) {
                    DatoC entrada = entradasC.getEntradaLista();
                    DatoC entradaPadre = entradasC.getEntradaLista();

                    while (entrada.getSiguiente() != null && entrada.getTiempo() >= f) {
                        entradaPadre = entrada;
                        entrada = entrada.getSiguiente();
                    }

                    if (entrada.getTiempo() > f) {
                        /**
                         * *SEGUIR DENTRO DE LA BITACORA **
                         */
                        DatoBitacora db = A.entradasA[entrada.getPuntoReferencia()].getBitacora().getBloque(entrada.getPosBitacora(), new Accesos()).getDatoB(entrada.getPosBloque()).getRefAnt();
                        while (db != null && db.getTiempo() > f) {
                            db = db.getRefAnt();
                        }
                        if (db == null){
                            resultado.add(new Point(entradasC.getPosInicial().getPoint()));
                        } else {
                            resultado.add(db.getPosicionActual().getPoint());
                        }
                    } else if (entrada.getTiempo() < f) {
                        /**
                         * * IR A LA BITÁCORA DE LA POSICIÓN ANTERIOR **
                         */
                        DatoBitacora db = A.entradasA[entradaPadre.getPuntoReferencia()].getBitacora().getBloque(entradaPadre.getPosBitacora(), new Accesos()).getDatoB(entradaPadre.getPosBloque()).getRefAnt();
                        while (db != null && db.getTiempo() > f) {
                            db = db.getRefAnt();
                        }
                        if (db == null){
                            resultado.add(new Point(entradasC.getPosInicial().getPoint()));
                        } else {
                            resultado.add(db.getPosicionActual().getPoint());
                        }
                    } else {
                        Accesos accesos = new Accesos();
                        resultado.add(A.entradasA[entrada.getPuntoReferencia()].getBitacora().getBloque(entrada.getPosBitacora(), accesos).getDatoB(entrada.getPosBloque()).getPosicionActual().getPoint());
                    }
                } else {
                    resultado.add(new Point(entradasC.getPosInicial().getPoint()));
                }
            }
        }
        return resultado;
    }

    public ArrayList<Float> getTiempos() {
        ArrayList<Float> resultado = new ArrayList<>();
        for (EntradaB entrada : B.getEntradas()) {
            if (entrada != null) {
                resultado.add(entrada.getTiempo());
            }
        }
        return resultado;
    }
}
