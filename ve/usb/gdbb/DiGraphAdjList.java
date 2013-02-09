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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class DiGraphAdjList implements Graph {

    private int V;
    private int E;
    private ArrayList<ArrayList<Integer>> adj;
    private ArrayList<Edge> edges;
    private HashMap<String, Integer> StoI;
    private ArrayList<String> ItoS;

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
            adj = (ArrayList<ArrayList<Integer>>) new ArrayList<ArrayList<Integer>>();
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            int pos, cur = 0;
            String edgeName = "", curName = "";
            while (scanner.hasNextLine()) {
                pos = 0;
                String[] line = scanner.nextLine().split("\t");
                for (String i : line) {
                    if (pos == 0) {
                        if (!StoI.containsKey(i)) {
                            addNode(i);
                        }
                        cur = StoI.get(i);
                        curName = i;
                        pos = 1;
                    } else if (pos == 1) {
                        edgeName = i;
                        pos = 2;
                    } else {
                        if (!StoI.containsKey(i)) {
                            addNode(i);
                        }
                        adj.get(cur).add(StoI.get(i));
                        edges.add(new Edge(edgeName, curName, i));
                        E++;
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
        adj.get(StoI.get(e.getSrc())).add(StoI.get(e.getDst()));
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

}
