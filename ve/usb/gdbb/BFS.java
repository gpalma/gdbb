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

import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;

public class BFS {

	private Graph graph;
	private HashSet<String> visitedNodes;

	/*
	 * Class used for the implementation of BFS's queue.
	 */
	private static class Pair<T> {
    	public T first;
		public int second;
		Pair(T newFirst, int newSecond) {
			first = newFirst;
			second = newSecond;
		}
	}

	public BFS(Graph newGraph) {
		graph = newGraph;
	}

	/*
	 * This function returns 'true' if dst is reachable from
	 * scr (this happends if and only if, dst is reachable from
	 * src in |E| or less arcs between them - given the graph
	 * is G = (V,E) and E is the set of arcs). Returns 'false'
	 * if not.
	 * Using Breadth First Search algoritm (BFS).
	 */
	public boolean existsPath (String src, String dest) {
		Pair<Boolean> res = runBFS(src, dest, graph.E(), false);
		return res.first;
	}

	/*
	 * This function returns 'true' if dst is reachable from
	 * scr in k or less arcs between them. Returns 'false' if
	 * the opposite.
	 * Using Breadth First Search algoritm (BFS).
	 */
	public boolean existsPath (String src, String dest, int k) {
		Pair<Boolean> res = runBFS(src, dest, k, false);
		return (res.first && (res.second <= k));
	}

	/*
	 * This function returns 'true' if dst is reachable from
	 * scr with exactly k arcs between them. Returns 'false' if
	 * the opposite.
	 * Using Breadth First Search algoritm (BFS).
	 */
	public boolean kHops (String src, String dest, int k) {
		Pair<Boolean> res = runBFS(src, dest, k, true);
		return (res.first && (res.second == k));
	}

	/*
	 * This function returns 'true' if node dst is
	 * reachable from node src with k or less arcs
	 * between them in the graph stored. Returns
	 * 'false' if the opposite.
	 * Using Breadth First Search algoritm (BFS).
	 */
	private Pair<Boolean> runBFS (String src, String dest, int k, boolean exactly) {

		Queue<Pair<String> > queue = new LinkedList<Pair<String> >();
		visitedNodes = new HashSet<String>();
		Iterator<String> adj;
		String next;
		Pair<String> aux = new Pair<String>(src, 0);
		queue.add(aux);

		while(!queue.isEmpty()) {
			aux = queue.poll();
			visitedNodes.add(aux.first);
			if (aux.first.equals(dest) && (!exactly || aux.second==k)) {
				return new Pair<Boolean>(true, aux.second);
			}
			if (aux.second < k) {
				adj = this.graph.adj(aux.first);
				while(adj.hasNext()) {
					next = adj.next();
					if (!visitedNodes.contains(next)) {
						queue.add(new Pair<String>(next, aux.second+1));
					}
				}
			}
		}
		return new Pair<Boolean>(false, 0);
	}

	/* Returns an iterator over the list of
	 * nodes that belongs to the k-Hop set
	 * of node 'src'.
	 */
	public Iterator<String> kHopsNeighborhood (String src, int k) {

		HashSet[] sets = new HashSet[2];
		sets[0] = new HashSet<String>();
		sets[1] = new HashSet<String>();
		sets[0].add(src);
		Iterator<String> hopN, adj;
		int i = 0, j = 1, ind = 0;

		while (ind < k) {

			hopN = sets[i].iterator();
			while(hopN.hasNext()) {
				adj = this.graph.adj(hopN.next());
				while (adj.hasNext())
					sets[j].add(adj.next());
			}

			sets[i].clear();

			i = (i==0 ? 1 : 0);
			j = (j==0 ? 1 : 0);
			ind++;
		}

		return sets[i].iterator();
	}
}
