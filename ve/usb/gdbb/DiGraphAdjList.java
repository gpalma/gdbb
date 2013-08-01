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
   * Constructor that creates an empty directed graph
	 * using Adjacency Lists.
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
   * Constructor that creates a directed graph,
   * reading it from the file with name 'fileName' using sif format.
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
	 * Function that returns the number of nodes in the graph.
	 */
	public int V() {  
		return V;
	}

	/*
	 * Function that returns the number of edges in the graph.
	 */
	public int E() {
		return E;
	}

	/*
   * Function that adds a node to the graph.
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
	 * Returns TRUE if all nodes in the new edge,
   * and add the new edge to the graph.
   * FALSE otherwise.
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
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId'.
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
	 * Function that returns an iterator over all edges in the graph.
	 */
	public Iterator<Edge> getEdges () {
		return edges.iterator();
	}

	/*
	 * Function that returns an iterator over all nodes in the graph.
	 */
	public Iterator<String> getNodes () {
		return ItoS.iterator();
	}

	/*
   * Function that returns the in degree of the given node, if the
   * given node doesn't exist then return null.
	 */
	public Integer getInDegree(String nodeId) {
		if (!StoI.containsKey(nodeId)) {
			return null;
		}
		return inDegree.get(StoI.get(nodeId));
	}

	/*
   * Function that returns the out degree of the given node, if the
   * given node doesn't exist then return null.
	 */
	public Integer getOutDegree(String nodeId) {
		if (!StoI.containsKey(nodeId)) {
			return null;
		}
		return outDegree.get(StoI.get(nodeId));
	}

  /*
   * Return TRUE if node 'dst' can be reached from
   * using Depth First Search. FALSE otherwise.
   */
	public boolean dfs(String src, String dst) {
		DFS dfsRun = new DFS(this);
		return dfsRun.existsPath(src, dst);
	}

  /*
   * Return TRUE if node 'dst' can be reached from
   * using Breadth First Search. FALSE otherwise.
   */
	public boolean bfs(String src, String dst) {
		BFS bfsRun = new BFS(this);
		return bfsRun.existsPath(src, dst);
	}

  /*
   * Returns an Iterator over all nodes that belongs
   * to the 'k' hops of 'src' node.
   */
	public Iterator<String> kHops(String src, int k) {
		BFS bfsRun = new BFS(this);
		return bfsRun.kHopsNeighborhood(src,k);
	}

	public void close() {}

	/* This function creates a file in 'sif' format
	 * with the representation of the graph loaded
	 * in this object.
	 */
	public void print(String File) {
		try{
			FileWriter fstream = new FileWriter(File);
			BufferedWriter out = new BufferedWriter(fstream);
			Iterator<Edge> archs = this.getEdges();
			Edge curr;
			while ( archs.hasNext() ) {
				curr = archs.next();
				out.write(curr.getSrc()+"\t"+curr.getId()+"\t"+curr.getDst()+"\n");
			}
			out.close();
		}catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

}
