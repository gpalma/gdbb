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

package testgdbb;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;
import ve.usb.gdbb.*;

abstract class TestNP extends Test{
    /*
   * Class used for the implementation of BFS's queue.
	 */
	private class Pair {
		public String node;
		public int cost;
		Pair(String n, int c) {
			node = n;
			cost = c;
		}
	}
    protected int K = 4; // Longitud de arborescencia
    protected int N = 3; // Numero de nodos a probar
    protected DiGraphAdjList getGraph(String inicial){
        DiGraphAdjList g = new DiGraphAdjList();
        Edge e;
        Queue<Pair> cola = new LinkedList<Pair>();
        Iterator<String> adj;
	    String next;
	    int numnodos = 0;
	    Pair aux = new Pair(inicial, 0);
	    cola.add(aux);
	    g.addNode(inicial);
        while(!cola.isEmpty() && numnodos++ < 10) {
		    aux = cola.poll();
		    if (aux.cost == K) {
		    	break;
		    }
		    adj = this.graphTest.adj(aux.node);
	        while(adj.hasNext()) {
    			next = adj.next();
	    		g.addNode(next);
	    		e = new Edge(aux.node+next, aux.node, next);
	    		g.addEdge(e);
	    		cola.add(new Pair(next, aux.cost+1));
	    	}
	    }
        return g;
    }
  
}
