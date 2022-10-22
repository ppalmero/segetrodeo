package bdet.comun;

import android.graphics.Point;

public class Punto {
	
	public float x, y; 
	
	public Punto(float x, float y){
	    this.x = x;
	    this.y = y;
	}
	
	public Punto(Rectangulo limites){
	    this.x = limites.xmin;
	    this.y = limites.ymin;
	}
	
	public String toString(){
	    return ("( " + x + ", " + y + " )");
	}
	
	public boolean equals(Object o){
	    if (o instanceof Punto)
	        return ((x == ((Punto) o).x) && (y == ((Punto) o).y));
	    return false;
	}

    public Point getPoint() {
        return new Point((int)(x * 1000), (int)(y * 1000));
    }
}