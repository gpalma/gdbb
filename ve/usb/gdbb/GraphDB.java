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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public abstract class GraphDB implements Graph {
	protected int V;
	protected int E;
	
	public int V() {
		return V;
	}
	public int E() {
		return E;
	}

	public abstract void addNode(String nodeId);
	public abstract boolean addEdge(Edge e);
	public abstract Iterator<String> adj(String nodeId);
	public abstract Iterator<Edge> getEdges();
	public abstract Iterator<String> getNodes();
	public abstract Integer getInDegree(String nodeId);
	public abstract Integer getOutDegree(String nodeId);
        
        /*
     * Funcion que retorna un subgrafo del grafo actual.
     */
        public Graph subGraph(int n) {
            /* Choosing a random node, to generate
                * a subgraph from it's BFS run
                */
            Iterator<Edge> iter = this.getEdges();
            // This is not random FIX
            Edge randomEdge = (Edge) iter.next();
            String randomNode = randomEdge.getSrc();
            int totalNodes = 1;
            /* BFS run */
            Graph subGraph = new DiGraphAdjList();
            Queue<String> queue = new LinkedList<String>();
            HashSet<String> visitedNodes = new HashSet<String>();
            Iterator<String> adjActual;
            String next;
            String aux = randomNode;
            queue.add(aux);

            subGraph.addNode(aux);
            visitedNodes.add(aux);

            while(!queue.isEmpty() && totalNodes<n) {
                    aux = queue.poll();
                    adjActual = this.adj(aux);
                    while(adjActual.hasNext()) {
                            next = adjActual.next();
                            if (!visitedNodes.contains(next) && totalNodes<n) {
                                    queue.add(next);
                                    subGraph.addNode(next);
                                    visitedNodes.add(next);
                                    subGraph.addEdge(new Edge(totalNodes+"", aux, next));
                                    totalNodes++;
                            }
                    }
            }

        return subGraph;
	}
	
	/*Funcion que,dado el String File, imprime en un archivo, cuyo nombre
	 *sera File, el grafo en formato SIF*/
	public void print(String File) {
            try{
                FileWriter fstream = new FileWriter(File);
                BufferedWriter out = new BufferedWriter(fstream);
                Iterator<Edge> archs = this.getEdges();
                Edge curr;
                while ( archs.hasNext() ) {
                        curr = archs.next();
                        out.write(curr.getSrc()+"\tpr\t"+curr.getDst()+"\n");
                }
                out.close();
            }catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
        }
	
	public abstract boolean patternMatching(Graph subGraph);
}
