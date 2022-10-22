/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geometria;

//import java.util.ArrayList;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
//import java.util.Vector;

/**
 *
 * @author Pablazo
 */
public class Voronoi {

    //ArrayList puntos;        //PuntoCHs Dibujados
    //ArrayList tvoro;    //Aristas del camino Voronoi
    Hashtable edgeDB;   //Aristas del grafo de Voronoi
    private Conjunto puntos;

    //Vector trigs;
    public Voronoi() {
        //puntos = new ArrayList();
        //tvoro = new ArrayList();
        edgeDB = new Hashtable();
        //trigs = new Vector ();
    }

    public void calculaVoronoi(Conjunto puntos) {
        this.puntos = puntos;
        int n = puntos.size();
        //trigs.clear();
        edgeDB.clear();
        for (int i = 0; i < n; i++) {
            PuntoCH pi = (PuntoCH) puntos.get(i);
            double pix = pi.x;
            double piy = pi.y;
            double piz = pix * pix + piy * piy;
            for (int j = i + 1; j < n; j++) {
                PuntoCH pj = (PuntoCH) puntos.get(j);
                double pjx = pj.x;
                double pjy = pj.y;
                double pjz = pjx * pjx + pjy * pjy;
                for (int k = i + 1; k < n; k++) {
                    PuntoCH pk = (PuntoCH) puntos.get(k);
                    double pkx = pk.x;
                    double pky = pk.y;
                    double pkz = pkx * pkx + pky * pky;
                    double zn = (pjx - pix) * (pky - piy) - (pkx - pix) * (pjy - piy);
                    if (j == k || zn > 0) {
                        continue;
                    }
                    int m = 0;
                    double xn = (pjy - piy) * (pkz - piz) - (pky - piy) * (pjz - piz);
                    double yn = (pkx - pix) * (pjz - piz) - (pjx - pix) * (pkz - piz);
                    for (m = 0; m < n; m++) {
                        PuntoCH pm = (PuntoCH) puntos.get(m);
                        double pmz = pm.x * pm.x + pm.y * pm.y;
                        if (m != i && m != j && m != k
                                && ((pm.x - pix) * xn + (pm.y - piy) * yn + (pmz - piz) * zn) > 0) {
                            break;
                        }
                    }
                    if (m == n) {
                        WTrig newTrig = new WTrig(pi, pj, pk);
                        //trigs.addElement(newTrig);
                        for (int p = 0; p < 3; p++) {
                            WEdge work = new WEdge(pi, pj);
                            WEdge corr = (WEdge) edgeDB.get(work);
                            if (corr == null) {
                                edgeDB.put(work, new WEdge(newTrig.pC, null));
                            } else {
                                corr.p2 = newTrig.pC;
                            }
                            PuntoCH tmp = pi;
                            pi = pj;
                            pj = pk;
                            pk = tmp;
                        }
                    }
                }
            }
        }
        for (Enumeration e = edgeDB.keys(); e.hasMoreElements();) {
            WEdge v = (WEdge) e.nextElement();
            WEdge w = (WEdge) edgeDB.get(v);
            w.setInf(v);
        }
    }

    public Hashtable getEdgeDB() {
        return edgeDB;
    }

    public Conjunto getPuntos() {
        return puntos;
    }

    public void setPuntos(Conjunto puntos) {
        this.puntos = puntos;
    }
}
