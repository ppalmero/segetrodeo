package bdet.rtree;

import java.util.HashMap;

import bdet.comun.Constantes;
import bdet.comun.Rectangulo;


public class Nodo extends Entrada {
	private Estadistica estadistica; // Estad�sticas de lectura
	protected boolean esHoja; // Indica si es un nodo hoja
	private Nodo padre; // Puntero al padre del nodo
	protected Entrada[] hijos; // Arreglo de hijos del nodo
	protected int numeroHijos; // N�mero de entradas en el nodo
	
	// Crea un nuevo Nodo
	public Nodo(boolean esHoja, Estadistica estadistica){
	    this.estadistica = estadistica;
	    this.esHoja = esHoja;
	    hilbert = 0;
	    limites = new Rectangulo();
	
	    if (esHoja)
	        hijos = new Dato[Constantes.TAMANO_NODO_HOJA];
	    else
	        hijos = new Nodo[Constantes.TAMANO_NODO];
	
	    numeroHijos = 0;
	}
	
	// Inserta una nueva entrada en el nodo
	public Nodo insertar(Entrada e){
	    Entrada insert;
	
	    if (esHoja)
	        insert = e;
	    else {
	        int n;
	        if (e.hilbert > hilbert)
	            n = numeroHijos - 1;
	        else {
	            for (n = 0; n < numeroHijos; n++)
	                // Selecciona el hijo en que mejor se acomoda la entrada
	                if (e.hilbert <= hijos[n].hilbert)
	                    break;
	        }
	        insert = ((Nodo) hijos[n]).insertar(e); // Inserta la entrada en un hijo
	    }
	
	    limites = limites.agregar(e.limites); // Actualiza el MBR
	
	    if (e.hilbert > hilbert)
	        hilbert = e.hilbert; // Actualiza el LHV
	
	    if (insert != null) { // Es un nodo hoja u ocurrio una division de nodos
	        if (!estaLleno()) {
	            agregarEntrada(insert);
	            return (padre == null ? this : null);
	        }
	        else {
	            if (padre == null) { // Es el nodo raiz
	                Nodo vecino = dividir(insert); // Divide este nodo
	                Nodo raiz = new Nodo(false, estadistica); // Crea una nueva raiz
	                raiz.agregarEntrada(vecino);
	                raiz.agregarEntrada(this);
	                return (raiz);
	            }
	            else {
	                Nodo vecino = padre.getVecino(this);
	
	                if (!vecino.estaLleno()) { // El vecino no esta lleno
	                    compartirEntradas(vecino, insert); // Distribuye los hijos
	                    return (null);
	                }
	                else
	                    return (dividir(vecino, insert));
	            }
	        }
	    }
	
	    return (padre == null ? this : null);
	}
	
	// Retorna en resultado los objetos que se intersecta con areaConsultada
	public void consultar(HashMap resultado,
	        Rectangulo areaConsulta)
	{
	    if (esHoja)
	        estadistica.paginas_hoja_leidas++;
	    else
	        estadistica.paginas_no_hoja_leidas++;
	
	    Entrada e;
	
	    for (int i = 0; i < numeroHijos; i++) {
	        e = hijos[i];
	        Rectangulo limite = e.limites;
	        if (areaConsulta.intersecta(limite)) {
	            if (esHoja) {
	                resultado.put(
	                        new Integer(((Dato) e).getOid()),
	                        (Dato) e);
	                estadistica.mbr_encontrados++;
	            }
	            else
	                ((Nodo) e).consultar(resultado,
	                        areaConsulta);
	        }
	    }
	}
	
	// Retorna si el nodo esta lleno o no
	private boolean estaLleno(){
	    return (numeroHijos >= hijos.length);
	}
	
	// Agrega una nueva entrada en la posicion adecuada
	private void agregarEntrada(Entrada e){
	    int n;
	
	    for (n = numeroHijos; n > 0; n--) {
	        if (hijos[n - 1].hilbert < e.hilbert)
	            break;
	
	        hijos[n] = hijos[n - 1];
	    }
	
	    hijos[n] = e;
	
	    if (!esHoja)
	        ((Nodo) e).padre = this;
	
	    limites = limites.agregar(e.limites); // Actualiza el MBR
	
	    if (n == numeroHijos)
	        hilbert = e.hilbert; // Actualiza el LHV
	
	    numeroHijos++;
	}
	
	// Divide el nodo
	private Nodo dividir(Entrada e){
	    Nodo vecino = new Nodo(esHoja, estadistica);
	    compartirEntradas(vecino, e);
	    return (vecino);
	}
	
	// Division 2-a-3
	private Nodo dividir(Nodo vecino, Entrada e){
	    Nodo izquierdo, derecho;
	    Nodo vecino2 = new Nodo(esHoja, estadistica);
	
	    if (vecino.hilbert < hilbert) {
	        izquierdo = vecino;
	        derecho = this;
	    }
	    else {
	        izquierdo = this;
	        derecho = vecino;
	    }
	
	    Nodo insert = null;
	    int num = derecho.numeroHijos / 3;
	
	    if (e.hilbert > derecho.hijos[num].hilbert) {
	        insert = derecho;
	        num++;
	    }
	
	    vecino2.adoptar(derecho, num);
	    // Moves 1/3 hijos from the right node to the new node
	    num = izquierdo.numeroHijos / 3;
	
	    if (e.hilbert <= izquierdo.hijos[izquierdo.numeroHijos
	            - num - 1].hilbert) {
	        insert = izquierdo;
	        num++;
	    }
	    vecino2.adoptar(izquierdo, num);
	    // Moves 1/3 hijos from the left node to the new node
	
	    if (insert == null)
	        insert = vecino2;
	
	    insert.agregarEntrada(e);
	
	    return (vecino2); // Returns a new node
	}
	
	// Comparte entradas con otro nodo
	private void compartirEntradas(Nodo vecino, Entrada e){
	    Nodo insert;
	    int num = (vecino.hijos.length - vecino.numeroHijos) / 2; // Numero de entradas a mover
	
	    if (hilbert <= vecino.hilbert) {
	        if (e.hilbert <= hijos[numeroHijos - num - 1].hilbert) {
	            insert = this;
	            num++; // Mueve una entrada mas
	        }
	        else
	            insert = vecino;
	    }
	    else {
	        if (e.hilbert <= hijos[num].hilbert)
	            insert = vecino;
	        else {
	            insert = this;
	            num++; // Mueve una entrada mas
	        }
	    }
	    vecino.adoptar(this, num); // Mueve la mitad de los hijos
	    insert.agregarEntrada(e);
	}
	
	// Adopta num hijos
	private void adoptar(Nodo vecino, int num){
	    if (num == 0)
	        return;
	    int i, j;
	    if (hilbert <= vecino.hilbert) {
	
	        for (i = 0; i < num; i++)
	            agregarEntrada(vecino.hijos[i]);
	
	        int inicio = num;
	        int fin = vecino.numeroHijos;
	
	        for (i = inicio; i < fin; i++) { // Rota los hijos
	            vecino.hijos[i - num] = vecino.hijos[i];
	            vecino.hijos[i] = null;
	        }
	    }
	    else {
	        for (i = numeroHijos - 1; i >= 0; i--)
	            // Rota los hijos
	            hijos[i + num] = hijos[i];
	
	        for (i = 0; i < num; i++) {
	            j = vecino.numeroHijos - num + i;
	            hijos[i] = vecino.hijos[j]; // Mueve el hijo
	            if (!esHoja)
	                ((Nodo) hijos[i]).padre = this;
	            limites = limites.agregar(hijos[i].limites); // Ajusta el MBR
	            vecino.hijos[j] = null;
	        }
	        numeroHijos += num;
	    }
	    vecino.numeroHijos -= num;
	    vecino.actualizar();
	}
	
	// Recalcula MBR y LHV
	private void actualizar(){
	    limites = new Rectangulo();
	
	    for (int i = 0; i < numeroHijos; i++)
	        limites = limites.agregar(hijos[i].limites);
	
	    hilbert = hijos[numeroHijos - 1].hilbert;
	}
	
	// Retorna un vecino
	private Nodo getVecino(Entrada e)
	{
	    int idx;
	    for (idx = 0; idx < numeroHijos; idx++)
	        if (hijos[idx] == e)
	            break;
	
	    if (idx == 0)
	        return ((Nodo) hijos[1]); // Hijo de mas a la izquierda
	
	    if (idx == numeroHijos - 1)
	        return ((Nodo) hijos[idx - 1]); // Hijo de mas a la derecha
	
	    Nodo izquierdo = (Nodo) hijos[idx - 1];
	    Nodo derecho = (Nodo) hijos[idx + 1];
	    // Retorna el vecino con menos hijos
	    return (izquierdo.numeroHijos < derecho.numeroHijos ? izquierdo
	            : derecho);
	}
	
	// Retorna el numero de nodos utilizados
	public int getNumeroNodos(){
	    if (esHoja)
	        return 1;
	    else {
	        int m = 0;
	        for (int i = 0; i < numeroHijos; i++)
	            m += ((Nodo) hijos[i]).getNumeroNodos();
	        return 1 + m;
	    }
	}
}