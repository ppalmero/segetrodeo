/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package geometria;

import java.util.Comparator;

/**
 *
 * @author Pablazo
 */
public class Nodo1 extends PuntoCH {
    public double angu;
    public int idposPunto;
    //public final Comparator<Punto> POLAR_ORDER = new PolarOrder();

    Nodo1 (double posX, double posY, double angulo, int id){
        x = posX;
        y = posY;
        angu = angulo;
        idposPunto = id;
    }

    Nodo1 (PuntoCH p, double angulo, int id){
        x = p.getX();//elimine el cast a int por problemas en convexhull, archivo 40-2diam.txt
        y = -p.getY();
        posicion = p.getPosicion();
        angu = angulo;
        idposPunto = id;
    }

    Nodo1 (PuntoCH p){
        super (p);
    }

    Nodo1 (double x, double y){
        super (x, y);
    }
    
    Nodo1 (int x, int y){
        super (x, y);
    }

    Nodo1 (double x, int y){
        angu = x;
        idposPunto = y;
    }

    public void setAngulo(double a){
        angu = a;
    }

    public double getAngulo(){
        return angu;
    }

    public void setID(int a){
        idposPunto = a;
    }

    public int getID(){
        return idposPunto;
    }
    
    /*public static int ccw(Punto a, Punto b, Punto c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (area2 < 0) {
            return -1;
        } else if (area2 > 0) {
            return +1;
        } else {
            return 0;
        }
    }

   public class PolarOrder implements Comparator<Punto> {

        public int compare(Punto q1, Punto q2) {
            double dx1 = q1.x - q2.x;
            double dy1 = q1.y - q2.y;
            double dx2 = q2.x - q2.x;
            double dy2 = q2.y - q2.y;

            if (dy1 >= 0 && dy2 < 0) {
                return -1;    // q1 above; q2 below
            } else if (dy2 >= 0 && dy1 < 0) {
                return +1;    // q1 below; q2 above
            } else if (dy1 == 0 && dy2 == 0) {            // 3-collinear and horizontal
                if (dx1 >= 0 && dx2 < 0) {
                    return -1;
                } else if (dx2 >= 0 && dx1 < 0) {
                    return +1;
                } else {
                    return 0;
                }
            } else {
                return -ccw(Nodo1.this, q1, q2);     // both above or below
            }
            // Note: ccw() recomputes dx1, dy1, dx2, and dy2
        }
    }*/
}
