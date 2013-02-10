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

/*
 * Reachability test functions implemented
 * with Depth First Search algoritm (DFS).
 *
 * Implemented by
 * Alejandro Flores Velazco
 * Jonathan Queipo Andrade
 */

package ve.usb.gdbb;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Stack;

public class DFS {

	private Graph graph;
	private HashSet<String> visitedNodes;
	
	public DFS(Graph newGraph) {
		graph = newGraph;
	}
	
	/*
	 * This function returns 'true' if dst is reachable from
	 * scr (this happends if and only if, dst is reachable from
	 * src in |E| or less arcs between them - given the graph
	 * is G = (V,E) and E is the set of arcs). Returns 'false'
	 * if not.
	 * Using Depth First Search algoritm (DFS).
	 */
	public boolean existsPath(String src, String dest) {
		visitedNodes = new HashSet<String>();
		return runDFS(src, dest, graph.E());
	}

	/*
	 * This function returns 'true' if dst is reachable from
	 * scr in k or less arcs between them. Returns 'false' if
	 * the opposite.
	 * Using Depth First Search algoritm (DFS).
	 */
	public boolean existsPath(String src, String dest, int k) {
		visitedNodes = new HashSet<String>();
		return runDFS(src, dest, k);
	}

	/*
	 * This function returns 'true' if node dst is
	 * reachable from node src with k or less arcs
	 * between them in the graph stored. Returns
	 * 'false' if the opposite.
	 * Using Depth First Search algoritm (DFS).
	 */
	private boolean runDFS(String src, String dest, int k) {
		if (k < 0)
			return false;
		visitedNodes.add(src);
		if (src.equals(dest))
			return true;
		Iterator<String> adj = graph.adj(src);
		boolean exists = false;
		String next;
		while (!exists && adj.hasNext()) {
			next = adj.next();
			if (!visitedNodes.contains(next)) {
				exists = runDFS(next, dest, k-1);
			}
		}
		return exists;
	}
}
