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

import java.util.Iterator;

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
	public abstract Iterator<Integer> getInDegree();
	public abstract Iterator<Integer> getOutDegree();
	
	public abstract boolean patternMatching(Graph subGraph);
}
