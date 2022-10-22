/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package geometria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author pc
 */
public class Conjunto implements Set{
    ArrayList<PuntoCH> l = new ArrayList<>();
    boolean punto2 = false;
    int puntoant = -1;

    public int size() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return (l.size());
    }

    public boolean isEmpty() {
        return l.isEmpty();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(Object o) {
        int c=0;
        PuntoCH elem = (PuntoCH) o;

        while ( (c < (l.size())) && (((elem.x < (l.get(c).x - 4)) || (elem.x > (l.get(c).x + 4)) || (elem.y < (l.get(c).y - 4)) || (elem.y > (l.get(c).y + 4)))) ){
            c++;
        }
        if (c != l.size())
            punto2 = !punto2;

        return ( c<(l.size()) );
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public int contienePos (Object o) {
        int c=0;
        PuntoCH elem = (PuntoCH) o;

        while ( (c < (l.size())) && (((elem.x < (l.get(c).x - 4)) || (elem.x > (l.get(c).x + 4)) || (elem.y < (l.get(c).y - 4)) || (elem.y > (l.get(c).y + 4)))) ){
            c++;
        }
        if (c < l.size())
            if (puntoant == -1)
                puntoant = c;
        /*if (c < l.size()){
            if (!punto2){
                puntoant = c;
            }
            else
                puntoant = -1;
            
            punto2 = !punto2;
        }*/

        return ( c );
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public int consultaP2 (){
       return puntoant;
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean add(Object e) {
        if (!l.contains((PuntoCH)e)){
            l.add((PuntoCH)e);
            return true;
       }
       return false;
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    public PuntoCH get(int e){
        return (l.get(e));
    }

    public boolean remove(Object o) {
        if (l.contains((PuntoCH) o)){
            l.remove((PuntoCH)o);
            return true;
        }
        return false;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean remove (int i){
        if (l.remove(i) != null)
            return true;
        else
            return false;
    }

    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        while (l.size()>0){
            l.remove(l.size()-1);
        }
    }

    public int getPos(PuntoCH p){
        for (int i = 0; i < l.size(); i++){
            if (l.get(i).igualQue(p)){
                return i;
            }
        }
        return -1;
    }
}
