package geometria;

// CLASE ARISTA
import android.graphics.Color;

public class Arista extends Object {

    private PuntoCH dest = null;	//v�rtice destino de la Arista
    private PuntoCH orig = null;	//v�rtice origen de la Arista
    private Arista sig = null;	//apunta a la Arista que comienza en el v�rtice dest
    private Arista twin = null;	//apunta a la Arista gemela (S�LO PARA T.DELAUNAY)
    private Arista prev = null;     //apunta a la Arista anterior (Para triang != Delaunay)
    private int color = Color.BLUE;
    private float pendiente;
    private int posPo = -1;
    private int posPd = -1;
    // no se puede utilizar twin para prev por el funcionamiento de setTwin

    public int getPosPd() {
        return posPd;
    }

    public void setPosPd(int posPd) {
        this.posPd = posPd;
    }

    public int getPosPo() {
        return posPo;
    }

    public void setPosPo(int posPo) {
        this.posPo = posPo;
    }

    public float getPendiente() {
        return pendiente;
    }

    public void setPendiente(float pendiente) {
        this.pendiente = pendiente;
    }

    public void setPendiente() {
        this.pendiente = (float)(((dest.y - orig.y) * 1.0f) / (dest.x - orig.x));//Cambio Double
    }

    public Arista(int po, int pd){
        this.posPo = po;
        this.posPd = pd;
    }

    public Arista(PuntoCH v, Arista n, Arista t) {
        this.dest = v;
        this.sig = n;
        this.twin = t;
    }

    public Arista(Arista e) {
        this.dest = e.dest;
        this.sig = e.sig;
        this.twin = e.twin;
        this.orig = e.orig;
        this.prev = e.prev;
    }

    public void fijarDest(PuntoCH v) {
        this.dest = v;
    }

    public void fijarSig(Arista e) {
        this.sig = e;
    }

    public void fijarTwin(Arista e) {
        this.twin = e;
        e.twin = this;
    }

    public Arista obtenerSig() {
        return this.sig;
    }

    public Arista obtenerTwin() {
        return this.twin;
    }

    public PuntoCH obtenerDest() {
        return this.dest;
    }

    public String toString() {
        return "<" + obtenerOrig() + "," + obtenerDest() + ">";
    }

// ------------------------------ A�ADIDOS --------------------------------
    public Arista(PuntoCH v, PuntoCH w) {
        this.orig = v;
        this.dest = w;
        /*orig.agregarVecino(w);
        dest.agregarVecino(v);*/
    }

    /*public Arista(PuntoCH v, PuntoCH w) {
        this.orig = new PuntoCH(v.x, v.y);
        this.dest = new PuntoCH(w.x, w.y);
    }*/

    public void fijarOrig(PuntoCH v) {
        this.orig = v;
    }

    public PuntoCH obtenerOrig() {
        return this.orig;
    }

    public void fijarPrev(Arista e) {
        this.prev = e;
    }

    public Arista obtenerPrev() {
        return this.prev;
    }

    public boolean igualQue(Arista a) {
        return (this.obtenerOrig().igualQue(a.obtenerOrig())
                && this.obtenerDest().igualQue(a.obtenerDest()));
    }

    public void setColor(int c) {
        this.color = c;
    }

    public int getColor() {
        return this.color;
    }

    public double longitud() {
        return orig.distance(dest);//FIJARSE SI FUNCIONA PARA DELAUNAY
    }

    void agregarVecino() {
        orig.agregarVecino(dest);
        dest.agregarVecino(orig);
    }

    void agregarVecinoDelone() {
        dest.agregarVecino(obtenerTwin().dest);
        obtenerTwin().dest.agregarVecino(dest);
    }
    
    public boolean equals(Object o){
        Arista a = (Arista) o;
        if ((posPo == a.getPosPo()) && (posPd == a.getPosPd())){
            return true;
        } else {
            return false;
        }
    }
}
