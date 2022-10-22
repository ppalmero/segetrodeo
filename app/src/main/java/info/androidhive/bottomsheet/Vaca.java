package info.androidhive.bottomsheet;

public class Vaca {
    int x;
    int y;
    int oId;
    String nombre;

    public Vaca(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vaca(int oId, String nombre) {
        this.oId = oId;
        this.nombre = nombre;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getoId() {
        return oId;
    }

    public void setoId(int oId) {
        this.oId = oId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
