/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geometria;

import bdet.bitacora.DatoBitacora;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Pablo
 */
public class ConvexHull {

    Nodo1[] cierre;
    int pos;

    public ConvexHull(Conjunto puntos) {
        int no_of_nodes = puntos.size();
        cierre = new Nodo1[no_of_nodes + 1];
        int i;
        for (i = 0; i < no_of_nodes; i++) {
            cierre[i] = new Nodo1(puntos.get(i), 0.0D, i);
        }

        int canp = no_of_nodes;
        int j;
        for (i = 0; i < canp; i++) {
            for (j = i + 1; j < canp; j++) {
                if (cierre[j].getX() < cierre[i].getX()) {
                    Nodo1 temp = cierre[i];
                    cierre[i] = cierre[j];
                    cierre[j] = temp;
                    continue;
                }
                if (cierre[j].getX() == cierre[i].getX() && -cierre[j].getY() < -cierre[i].getY()) {
                    Nodo1 temp = cierre[i];
                    cierre[i] = cierre[j];
                    cierre[j] = temp;
                }
            }
        }


        Nodo1 p1 = new Nodo1(cierre[0]);
        Nodo1 p2 = new Nodo1( cierre[0].getX(), (-cierre[0].getY()) + 2);
        for (i = 0; i < canp; i++) {
            cierre[i].setAngulo(angulo(p1, p2, p1, cierre[i]));
        }

        for (i = 0; i < canp; i++) {
            for (j = i + 1; j < canp; j++) {
                if (cierre[j].angu > cierre[i].angu) {
                    Nodo1 temp = cierre[i];
                    cierre[i] = cierre[j];
                    cierre[j] = temp;
                }
            }
        }


        boolean maspasos = true;
        i = 0;
        while (maspasos) {
            double as = areasignadaCC(cierre[i % canp], cierre[(i + 1) % canp], cierre[(i + 2) % canp]);
            if (as <= 0.0D) {
                for (int k = i + 1; k <= canp - 1; k++) {
                    cierre[k] = cierre[k + 1];
                }

                canp--;
                if (i == 0) {
                    i++;
                } else {
                    i--;
                }
            } else {
                i++;
            }
            if (i < canp - 2) {
                maspasos = true;
            } else {
                maspasos = false;
            }
        }
        pos = 0;
        Nodo1 menorY = cierre[0];
        for (i = 1; i <= canp - 1; i++) {
            if (cierre[i].y < menorY.y) {
                pos = i;
                menorY = cierre[i];
                continue;
            }
            if (cierre[i].y == menorY.y && cierre[i].x < menorY.x) {
                pos = i;
                menorY = cierre[i];
            }
        }
    }

    double angulo(Nodo1 p1, Nodo1 p2, Nodo1 p3, Nodo1 p4) {
        double u[] = new double[2];
        double v[] = new double[2];
        u[0] = p2.x - p1.x;
        u[1] = p2.y - p1.y;
        v[0] = p4.x - p3.x;
        v[1] = p4.y - p3.y;
        double m1 = Math.sqrt(u[0] * u[0] + u[1] * u[1]);
        double m2 = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        return Math.acos((u[0] * v[0] + u[1] * v[1]) / (m1 * m2));
    }

    double areasignadaCC(Nodo1 pu1, Nodo1 pu2, Nodo1 pu3) {
        double salida = (pu1.x * pu2.y + pu2.x * pu3.y + pu3.x * pu1.y) - (pu1.x * pu3.y + pu2.x * pu1.y + pu3.x * pu2.y);
        return 0.5D * salida;
    }

    public ArrayList<Integer> getCierre() {
        ArrayList<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < cierre.length - 1; i++) {
            if (cierre[(i + pos) % (cierre.length - 1)] != null) {
                //posiciones.add(cierre[(i + pos) % (cierre.length - 1)].idposPunto);
                posiciones.add(cierre[(i + pos) % (cierre.length - 1)].getPosicion());
            }
        }

        return posiciones;
    }
}
