package bdet.comun;

import android.graphics.Point;


public class Rectangulo {
	public final float xmin, ymin, xmax, ymax; // L�mites del rect�ngulo
	
	public Rectangulo(){
	    xmin = ymin = Float.POSITIVE_INFINITY;
	    xmax = ymax = Float.NEGATIVE_INFINITY;
	}
	
	public Rectangulo(float xmin, float ymin, float xmax, float ymax){
	    this.xmin = xmin;
	    this.ymin = ymin;
	    this.xmax = xmax;
	    this.ymax = ymax;
	}
	
	public Rectangulo(Punto punto){
	    this.xmin = this.xmax = punto.x;
	    this.ymin = this.ymax = punto.y;
	}
        
        public Rectangulo(Point punto){
	    this.xmin = this.xmax = punto.x;
	    this.ymin = this.ymax = punto.y;
	}
	
	public Punto getCentro(){
	    return (new Punto((xmin + xmax) / 2.0f,(ymin + ymax) / 2.0f));
	}
	
	public Rectangulo agregar(Rectangulo r){
	    return (new Rectangulo(r.xmin < xmin ? r.xmin : xmin, r.ymin < ymin ? r.ymin : ymin, r.xmax > xmax ? r.xmax : xmax, r.ymax > ymax ? r.ymax : ymax));
	}
	
	public boolean intersecta(Rectangulo r){
	    return (r.xmin <= xmax && r.xmax >= xmin && r.ymin <= ymax && r.ymax >= ymin);
	}
	
	public boolean intersecta(Punto p){
	    return intersecta(new Rectangulo(p));
	}
	
	public String toString(){
	    return ((new Punto(xmin, ymin)).toString() + " - " + (new Punto(xmax, ymax)).toString());
	}
	
	public boolean equals(Object o){
	    if (o instanceof Rectangulo) {
	        if (((Rectangulo) o).xmin == xmin
	                && ((Rectangulo) o).ymin == ymin
	                && ((Rectangulo) o).xmax == xmax
	                && ((Rectangulo) o).ymax == ymax)
	            return true;
	        else
	            return false;
	    }
	    return false;
	}
}