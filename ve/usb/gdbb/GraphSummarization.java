/*
 *  Copyright (C) 2013, Universidad Simon Bolivar
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ve.usb.gdbb;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.LinkedList;
public class GraphSummarization {
  // Clase elemento de lista
    protected class Elem{
        int u, v;
        double cost;
    }
    //Conjuntos Disjuntos
    protected int[] A;   
    protected int[] tam;
    
    //Matriz de incidencias
    protected int[][] edges;
    protected int N;
    
    //Transformadores de nodos a enteros y viceversa
    protected TreeMap<String, Integer> StoI;
    protected ArrayList<String> ItoS;
    
    
    // Matriz de incidencias de supernodos indica cuantos lados existen
    // de un super nodo a otro
    private int[][] Auv;
    //Indica el costo de un nodo
    private int[] C;
    //Indica el costo de unir dos nodos u y v en w
    private int[][] Cw;
    
    // Resultado del graph summarization
    protected Graph gs;
    protected ArrayList<Correction> Cgs;
    
    /*
     * Constructor de la clase
     */
    public GraphSummarization(){
    }
    
    /*
     * Luego de aplicar Summarize se instancia gs el grafo resultante y se 
     * retorna por medio de esta funcion
     */
    public Graph returnGSummarize(){
    	return gs;
    }
    
    /*
     * Luego de aplicar Summarize se instancian el conjunto de correcciones
     * y se retorna por medio de esta funcion
     */
    public ArrayList<Correction> returnCorrections(){
    	return Cgs;
    }
    
    
    /*
     * Funcion que devuelve el representante de un conjunto
     */
    private int find(int i){
        int f = i;
        while(A[f] != f) f = A[f];
        A[i] = f;
        return f;
    }
    /*
     * Funcion que dados dos elementos de dos conjuntos diferentes los une
     */
    private void union(int i, int j){
        int a, b;
        a = find(i);
        b = find(j);
        if(a < b){
            A[b] = a;
            tam[a] = tam[b] + tam[a];
        }else{
            A[a] = b;
            tam[b] = tam[b] + tam[a];
        }
    }
    /*
     * Funcion que devuelve el minimo de dos numeros
     */
    private int min(int i, int j){ 
        return (i < j ? i : j);
    }
    
    /*
     * Aplica el algorithmo greedy de graph summarization al grafo g
     */
    public void Summarize(Graph g){
        Elem E, Emax, Enew;
        int maxindex, k, w;
        double maxvalue, aux;
        Iterator<Elem> it;
        ArrayList<Elem> cola = new ArrayList<Elem>();
        LinkedList<Elem> H = new LinkedList<Elem>();
        int fu, fv, fi, fj;
        Edge lado;
        Correction cor;
        
        gs = new DiGraphAdjList();
        Cgs = new ArrayList<Correction>();
        
        /* Initialization phase */
        this.constructGraph(g);
        this.calculateCosts();
        for(int i = 0; i < N; i++){
            for(int j1 = 0; j1 < N; j1++){
                if(Auv[i][j1] == 0) continue;
                for(int j2 = j1+1; j2 < N; j2++){
                    if(Auv[i][j2] == 0 || Auv[j1][j2] > 0) continue; //
                    if(Cost(j1, j2) > 0){
                        E = new Elem();
                        E.u = j1;
                        E.v = j2;
                        E.cost = Cost(j1, j2);
                        H.add(E);
                    }
                }
            }
        }
        
        /* Iterative merging phase */
        while(H.size() != 0){
            /* Get the elem with the largest cost */
            maxvalue = 0;
            maxindex = -1;
            Emax = null;
            it = H.iterator(); 
            k = 0;
            while (it.hasNext()){
                E = it.next();
                if(E.cost > maxvalue){
                    maxvalue = E.cost;
                    maxindex = k;
                    Emax = E;
                }
                k++;
            }
            //System.out.println(ItoS.get(Emax.u)+ " "+ ItoS.get(Emax.v));
            //Nodos mezclados en la iteracion
            H.remove(maxindex);
            
            /* Merge and recalculate costs */
            union(Emax.u, Emax.v);
            w = find(Emax.u);
            this.calculateCosts();
            
            /* Delete those elements that are two hops of u or v */
            it = H.iterator(); 
            while (it.hasNext()){
                E = it.next();
                if(E.u == Emax.u || E.u == Emax.v || E.v == Emax.u || E.v == Emax.v){
                    it.remove();
                }
            }
            /* Insert those that are within two hops of w */
            for(int i = 0; i < N; i++){
                int j1 = w;
                if(Auv[i][j1] == 0) continue;
                for(int j2 = 0; j2 < N; j2++){
                    if(Auv[i][j2] == 0 || j2 == j1 || Auv[j1][j2] > 0) continue; // 
                    if(Cost(j1, j2) > 0){
                        E = new Elem();
                        E.u = j1;
                        E.v = j2;
                        E.cost = Cost(j1, j2);
                        H.add(E);
                    }
                }
            }
            
            /* Delete those elements such that some node is in Nw */
            it = H.iterator(); 
            while (it.hasNext()){
                E = it.next();
                if(E.u == w || E.v == w) continue;
                if(Auv[E.u][w] > 0 || Auv[E.v][w] > 0){
                    if(Cost(E.u, E.v) > 0){
                        Enew = new Elem();
                        Enew.u = E.u;
                        Enew.v = E.v;
                        Enew.cost = Cost(E.u, E.v);
                        cola.add(Enew);
                    }
                    it.remove();
                }
            }
            /* Insert the elements with cost > 0 */
            for(int i = 0; i < cola.size(); i++){
                H.add(cola.get(i));
            }
            cola.clear();
        }
        
        /* Output phase */
        for(int u = 0; u < N; u++){
            fu = find(u);
            if(fu != u) continue;
            gs.addNode(ItoS.get(u));
        }
        for(int u = 0; u < N; u++){
            fu = find(u);
            for(int v = 0; v < N; v++){
                fv = find(v);
                if(u == v) continue;
                if(Auv[fu][fv] > (tam[fu]*tam[fv] +1.0)/2.0){
                	if(u == fu && v == fv){
		            	lado = new Edge(ItoS.get(u)+ItoS.get(v), ItoS.get(u), ItoS.get(v));
		            	gs.addEdge(lado);
                	}
                	if(edges[u][v] <= 0){
                		cor = new Correction(ItoS.get(u), ItoS.get(v), false);
                		Cgs.add(cor);
                	}
                }else{
                	if(edges[u][v] > 0){
                		cor = new Correction(ItoS.get(u), ItoS.get(v), true);
                		Cgs.add(cor);
                	}
                }
            }
        }
        
        
    }
    
    /*
     * Funcion auxiliar que construye el grafo en memoria
     * e inicializa algunas variables
     */
    protected void constructGraph(Graph g){
        
        N = g.V();
        Iterator<String> it, it2;
        String s, s2;
        edges = new int[g.V()][g.V()];
        Auv = new int[g.V()][g.V()];
        C = new int[g.V()];
        Cw = new int[g.V()][g.V()];
        StoI = new TreeMap<String, Integer>();
		ItoS = new ArrayList<String>();
        
        Integer k = 0;
        it = g.getNodes();
        
        // Nodos
        while(it.hasNext()){
            s = it.next();
            ItoS.add(s);
            StoI.put(s, k);
            k++;
        }
        
        // Inicializar matriz de incidencias
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                edges[i][j] = 0;
            }
        }
        it = g.getNodes();
        Integer u, v;
        while(it.hasNext()){
            s = it.next();
            u = StoI.get(s);
            it2 = g.adj(s);
            while(it2.hasNext()){
                s2 = it2.next();
                v = StoI.get(s2);
                edges[u.intValue()][v.intValue()] += 1;
                edges[v.intValue()][u.intValue()] += 1;
            }
        }
        
        
        // Inicializar conjuntos disjuntos
        A = new int[g.V()];
        tam = new int[g.V()];
        for(int i = 0; i < g.V(); i++){
            A[i] = i;
            tam[i] = 1;
        }
        
    }
    
    
    /*
     * Funcion que inicializa las matrices de costos de nodos
     */
    private void calculateCosts(){
        int fi, fj, fu, fv;
        // Inicializacion
        for(int i = 0; i < N; i++){
            C[i] = 0;
            for(int j = 0; j < N; j++){
                this.Auv[i][j] = 0;
                this.Cw[i][j] = 0;
            }
        }
        // Super arcos
        for(int i = 0; i < N; i++){
            fi = find(i);
            for(int j = 0; j < N; j++){
                fj = find(j);
                if(fi == fj) continue;
                if(edges[i][j] > 0){
                    Auv[fi][fj] += 1;
                }
            }
        }
        // Costos de un nodo
        for(int i = 0; i < N; i++){
            fi = find(i);
            if(i != fi) continue;
            for(int j = 0; j < N; j++){
                fj = find(j);
                if(j != fj) continue;
                C[i] += min(Auv[i][j], tam[i]*tam[j] - Auv[i][j] + 1);
            }
        }
        // Costos si se unieran dos nodos u y v
        for(int u = 0; u < N; u++){
            fu = find(u);
            if(fu != u) continue;
            for(int v = 0; v < N; v++){
                fv = find(v);
                if(u == v || fv != v) continue;
                for(int i = 0; i < N; i++){
                    fi = find(i);
                    if(i == u || i == v || fi != i) continue;
                    this.Cw[u][v] += min(Auv[u][i]+ Auv[v][i], 
                                         (tam[u]+tam[v])*tam[i] - (Auv[u][i]+ Auv[v][i]) + 1);
                }
            }
        }
        
    }
    
    /*
     * Funcion que devuelve la ventaja de unir dos nodos
     */
    protected double Cost(int u, int v){
        double t = C[u] + C[v];
        return (t - Cw[u][v])/(t);
    }
    
}
