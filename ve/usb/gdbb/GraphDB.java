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
	public abstract GraphIterator<String> adj(String nodeId);
	public abstract GraphIterator<String> adj(String nodeId, String relId);
	public abstract GraphIterator<String> edgeBetween(String srcId, String dstId);
	public abstract GraphIterator<Edge> getEdges();
	public abstract GraphIterator<String> getNodes();
	public abstract Integer getInDegree(String nodeId);
	public abstract Integer getOutDegree(String nodeId);
	public abstract boolean bfs(String src, String dst);
	public abstract boolean dfs(String src, String dst);
	public abstract GraphIterator<String> kHops(String src, int k);
	public abstract void close();

	/* This function creates a file in 'sif' format
	 * with the representation of the graph loaded
	 * in this object.
	 */
	public void print(String File) {
		try{
			FileWriter fstream = new FileWriter(File);
			BufferedWriter out = new BufferedWriter(fstream);
			GraphIterator<Edge> archs = this.getEdges();
			Edge curr;
			while ( archs.hasNext() ) {
				curr = archs.next();
				out.write(curr.getSrc()+"\t"+curr.getId()+"\t"+curr.getDst()+"\n");
			}
			out.close();
			archs.close();
		}catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
