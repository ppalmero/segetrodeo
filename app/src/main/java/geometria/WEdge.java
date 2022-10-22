package geometria;

//import grafos.*;
//import java.awt.Graphics2D;

public class WEdge extends Object {
  public PuntoCH p1;
  public PuntoCH p2;

  public WEdge(PuntoCH ap1, PuntoCH ap2) {
    p1 = ap1;
    p2 = ap2;
  }
/*
  public WEdge(PuntoCH ap1, PuntoCH ap2, PuntoCH pO, PuntoCH pD) {
    p1 = ap1;
    p2 = ap2;
    this.pO = pO;
    this.pD = pD;
  }
*/
  public boolean equals(Object r) {
    WEdge q = (WEdge) r;
    return ( (r instanceof WEdge) &&
            //( (p1 == q.p1) && (p2 == q.p2) || (p1 == q.p2) && (p2 == q.p1)));
            ( ((p1.x == q.p1.x) && (p1.y == q.p1.y) && (p2.x == q.p2.x) && (p2.y == q.p2.y)) ||
              ((p1.x == q.p2.x) && (p1.y == q.p2.y) && (p2.x == q.p1.x) && (p2.y == q.p1.y)) )
            );
  }

  public int hashCode() {
    return p1.hashCode() + p2.hashCode();
  }

  public Arista getLinea(WEdge w) {
    Arista l = new Arista(p1, p2);
    return l;
  }

  public void setInf(WEdge w) {
    if (p2 == null) {
      double dx = - (w.p2.y - w.p1.y);
      double dy = (w.p2.x - w.p1.x);
      double v = 1000 / Math.sqrt(dx * dx + dy * dy);
      dx *= v;
      dy *= v;
      p2 = new PuntoCH((int)(p1.x + dx), (int)(p1.y + dy));
    }
  }

  /*public void paint(Graphics2D g, WEdge w) {
    g.drawLine( (int) (p1.x), (int) (p1.y), (int) (p2.x), (int) (p2.y));
  }*/
}
