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

import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DiGraphAdjList implements Graph {

    private int V;
    private int E;
    private ArrayList<ArrayList<Integer>> adj;
    private ArrayList<Edge> edges;
    private HashMap<String, Integer> StoI;
    private ArrayList<String> ItoS;
    private ArrayList<Integer> inDegree, outDegree;
    
    /*
     * Constructor que genera un grafo dirigido, implementado con lista
     * de adyacencias, totalmente vacio.
     */
    public DiGraphAdjList() {
        this.V = 0;
        this.E = 0;
        this.StoI = new HashMap<String, Integer>();
        this.ItoS = new ArrayList<String>();
        this.edges = new ArrayList<Edge>();
        this.inDegree = new ArrayList<Integer>();
        this.outDegree = new ArrayList<Integer>();
        adj = (ArrayList<ArrayList<Integer>>) new ArrayList<ArrayList<Integer>>();
    }
    
    /*
     * Constructor que genera un grafo dirigido, implementado con lista de
     * adyacencias, basado en el archivo cuyo nombre es especificado como
     * parametro de entrada (fileName). El grafo representado en el archivo
     * debe estar en formato sif.
     */
    public DiGraphAdjList(String fileName) {
        try {
            this.V = 0;
            this.E = 0;
            this.StoI = new HashMap<String, Integer>();
            this.ItoS = new ArrayList<String>();
            this.edges = new ArrayList<Edge>();
            this.inDegree = new ArrayList<Integer>();
            this.outDegree = new ArrayList<Integer>();
            adj = (ArrayList<ArrayList<Integer>>) new ArrayList<ArrayList<Integer>>();
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            int pos;
            String edgeName = "", curName = "";
            while (scanner.hasNextLine()) {
                pos = 0;
                String[] line = scanner.nextLine().split("\t");
                for (String i : line) {
                    if (pos == 0) {
                        addNode(i);
                        curName = i;
                        pos = 1;
                    } else if (pos == 1) {
                        edgeName = i;
                        pos = 2;
                    } else {
                        addNode(i);
                        addEdge(new Edge(edgeName, curName, i));
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Funcion que retorna la cantidad de nodos que posee el grafo.
     */
    public int V() {  
        return V;
    }
    
    /*
     * Funcion que retorna la cantidad de arcos que posee el grafo.
     */
    public int E() {
        return E;
    }
    
    /*
     * Funcion que agrega un nodo con el id pasado como parametro de
     * entrada (nodeId) en caso de que no se encuentre ya en el grafo.
     */
    public void addNode(String nodeId) {
        if ((!StoI.containsKey(nodeId))) {
            StoI.put(nodeId, V);
            ItoS.add(V, nodeId);
            adj.add(V, new ArrayList<Integer>());
            V++;
            inDegree.add(0);
            outDegree.add(0);
        }
    }
    
    /*
     * Funcion que agrega un arco nuevo al grafo en caso de que ambos nodos
     * especificados en dicho arco existan en el grafo. En caso contrario 
     * retorna false.
     */
    public boolean addEdge(Edge e) {
        if (!StoI.containsKey(e.getSrc()) || !StoI.containsKey(e.getDst())) {
            return false;
        }
        int cur, curSuc;
        adj.get(StoI.get(e.getSrc())).add(StoI.get(e.getDst()));
        edges.add(e);
        cur = StoI.get(e.getSrc());
        curSuc = StoI.get(e.getDst());
        outDegree.set(cur, outDegree.get(cur) + 1);
        inDegree.set(curSuc, inDegree.get(curSuc) + 1);
        E++;
        return true;
    }
    
    /*
     * Funcion que retorna un iterador sobre todos los nodos adyacentes
     * al nodo especificado como parametro de entrada (nodeId). En caso
     * de que el nodo no se encuentre en el grafo o que no tenga otros
     * nodos adyacentes, se devuelve un iterador de una lista vacia.
     */
    public Iterator<String> adj(String nodeId) {
        ArrayList<String> adjlist = new ArrayList<String>();
        if (StoI.containsKey(nodeId)) {
            for (Integer i : adj.get(StoI.get(nodeId)))
                adjlist.add(ItoS.get(i));
        }
        return adjlist.iterator();
    }
    
    /*
     * Funcion que retorna un iterador sobre todos los arcos contenidos
     * en el grafo.
     */
    public Iterator<Edge> getEdges () {
        return edges.iterator();
    }
    
    /*
     * Funcion que retorna un iterador sobre todos los nodos que contiene
     * el grafo.
     */
    public Iterator<String> getNodes () {
        return ItoS.iterator();
    }
    
    /*
     * Funcion que retorna un iterador sobre un arreglo que contiene los
     * grados de entrada de cada nodo en orden de aparicion en el grafo.
     */
    public Iterator<Integer> getInDegree() {
        return inDegree.iterator();
    }
    
    /*
     * Funcion que retorna un iterador sobre un arreglo que contiene los
     * grados de salida de cada nodo en orden de aparicion en el grafo.
     */
    public Iterator<Integer> getOutDegree() {
        return outDegree.iterator();
    }
    
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
}
