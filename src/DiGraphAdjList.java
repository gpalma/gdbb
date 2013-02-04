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

package gdbb;

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

    public DiGraphAdjList() {
        this.V = 0;
        this.E = 0;
        this.StoI = new HashMap<String, Integer>();
        this.ItoS = new ArrayList<String>();
        this.edges = new ArrayList<Edge>();
        adj = (ArrayList<ArrayList<Integer>>) new ArrayList<ArrayList<Integer>>();
    }

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
                            StoI.put(i, V);
                            ItoS.add(V, i);
                            adj.add(V, new ArrayList<Integer>());
                            V++;
                        }
                        cur = StoI.get(i);
                        curName = i;
                        pos = 1;
                    } else if (pos == 1) {
                        edgeName = i;
                        pos = 2;
                    } else {
                        if (!StoI.containsKey(i)) {
                            StoI.put(i, V);
                            ItoS.add(V, i);
                            adj.add(V, new ArrayList<Integer>());
                            V++;
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

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(Edge e) {
        if (!StoI.containsKey(e.getSrc())) {
            StoI.put(e.getSrc(), V);
            ItoS.add(V, e.getSrc());
            adj.add(V, new ArrayList<Integer>());
            V++;
        }
        if (!StoI.containsKey(e.getDst())) {
            StoI.put(e.getDst(), V);
            ItoS.add(V, e.getDst());
            adj.add(V, new ArrayList<Integer>());
            V++;
        }
        adj.get(StoI.get(e.getSrc())).add(StoI.get(e.getDst()));
        E++;
    }

    public Iterator<String> adj(String v) {
        ArrayList<String> adjlist = new ArrayList<String>();
        if (StoI.containsKey(v)) {
            for (Integer i : adj.get(StoI.get(v)))
                adjlist.add(ItoS.get(i));
        }
        return adjlist.iterator();
    }

    public Iterator<Edge> getEdges () {
        return edges.iterator();
    }

}
