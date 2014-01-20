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

import com.sparsity.dex.algorithms.SinglePairShortestPathBFS;
import com.sparsity.dex.algorithms.TraversalDFS;
import com.sparsity.dex.algorithms.TraversalBFS;
import com.sparsity.dex.gdb.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class DexDB extends GraphDB {

	protected DexConfig cfg;
	protected Dex dex;
	protected Database db;
	protected Session sess;
	protected com.sparsity.dex.gdb.Graph g;
	protected int NodeType; //Node's ids
	protected int NodeIdType; //Node Attribute's ids
	protected String path = "graphs/DB/dexdb/";
	// This licence is for research purpuse only (Expires 01/11/2013)
	protected String licenceDEX = "WJ16D-KRC8W-NDPJN-QPN55";
  
	/*
	 * Constructor to create an empty graph.
	 */
	public DexDB() {
		try {
			cfg = new DexConfig();
			cfg.setLicense(licenceDEX);
			dex = new Dex(cfg);
			db = dex.create(path + "/DexBD.dex", "DexBD");
			sess = db.newSession();
			g = sess.getGraph();
			NodeType = g.newNodeType("NODE");
			NodeIdType = g.newAttribute(NodeType, "ID", DataType.String, AttributeKind.Unique);

		} catch (FileNotFoundException e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
  
	/*
	 * Constructor that loads a graph from the directory:
	 * ../graphs/DB/dexdb/N/DexDB.dex with N as a parameter.
	 */
	public DexDB(int N) {
		try {
			cfg = new DexConfig();
			cfg.setLicense(licenceDEX);
			dex = new Dex(cfg);
			db = dex.open(path+ N +"/DexBD.dex", true);
			sess = db.newSession();
			g = sess.getGraph();
			NodeType = g.findType("NODE");
			NodeIdType = g.findAttribute(NodeType, "ID");
			V = (int) g.countNodes();
			E = (int) g.countEdges();

		} catch (FileNotFoundException e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
  
	/*
	 * Public graph constructor
	 * Using a file to load the data.
	 */
	public DexDB(String fileName, int N) {
		try {
			cfg = new DexConfig();
			cfg.setLicense(licenceDEX);
			dex = new Dex(cfg);
			(new File(path + N)).mkdirs();
			db = dex.create(path+ N +"/DexBD.dex", "DexBD");
			sess = db.newSession();
			g = sess.getGraph();
			NodeType = g.newNodeType("NODE");
			NodeIdType = g.newAttribute(NodeType, "ID", DataType.String, AttributeKind.Unique);
			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			int pos, indT = 0;
			String edgeName = "", curName = "";
			sess.begin();
			while (scanner.hasNextLine()) {
				pos = 0;
				String[] line = scanner.nextLine().split("\t");
				for (String i : line) {
					if (pos == 0) {
						curName = i;
						pos = 1;
					} else if (pos == 1) {
						edgeName = i;
						pos = 2;
					} else {
						addEdge(new Edge(edgeName, curName, i));
						indT++;
						if (indT > 100000) {
							sess.commit();
							indT = 0;
							sess.begin();
						}
					}
				}
			}
			scanner.close();
			sess.commit();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Returns True if node 'nodeId'
	 * is stored in the graph.
	 * False otherwise.
	 */
	private boolean hasNode(String nodeId) {
		Value value = new Value();
		return (g.findObject(NodeIdType, value.setString(nodeId)) != Objects.InvalidOID);
	}

	/*
	 * Adds node 'nodeId' to the graph.
	 */
	public void addNode(String nodeId){
		if ((!hasNode(nodeId))) {
			Value value = new Value();
			long mNode = g.newNode(NodeType);
			g.setAttribute(mNode, NodeIdType, value.setString(nodeId));
			V++;
		}
	}

	/*
	 * Adds edge 'e' to the graph.
	 */
	public boolean addEdge(Edge e) {
		long src, dst;
		src = g.findObject(NodeIdType, (new Value()).setString(e.getSrc()));
		if (src==Objects.InvalidOID) {
			src = g.newNode(NodeType);
			g.setAttribute(src, NodeIdType, (new Value()).setString(e.getSrc()));
			V++;
		}
		dst = g.findObject(NodeIdType, (new Value()).setString(e.getDst()));
		if (dst==Objects.InvalidOID) {
			dst = g.newNode(NodeType);
			g.setAttribute(dst, NodeIdType, (new Value()).setString(e.getDst()));
			V++;
		}
		int edge = g.findType(e.getId());
		if (edge == Type.InvalidType) {
			edge = g.newRestrictedEdgeType(e.getId(), NodeType, NodeType, true);
		}
		g.newEdge(edge, src, dst);
		E++;
		return true;
	}

	/*
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId'.
	 */
	public GraphIterator<String> adj(String nodeId) {
		Value value = new Value();
		long node = g.findObject(NodeIdType, value.setString(nodeId));
		TypeList tlist = g.findEdgeTypes();
		TypeListIterator listIt = tlist.iterator();
		Objects adjacents = g.neighbors(node, listIt.nextType(), EdgesDirection.Outgoing);
		Objects adjacents2;
		while (listIt.hasNext()) {
			adjacents2 = g.neighbors(node, listIt.nextType(), EdgesDirection.Outgoing);
			adjacents.union(adjacents2);
			adjacents2.close();
		}
		return new DEXNodeIterator(adjacents);
	}

	/*
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId' with 'relId' as connection.
	 */
	public GraphIterator<String> adj(String nodeId, String relId) {
		Value value = new Value();
		long node = g.findObject(NodeIdType, value.setString(nodeId));
		int edgeTypeId = g.findType(relId);
		Objects adjacents = g.neighbors(node, edgeTypeId, EdgesDirection.Outgoing);
		return new DEXNodeIterator(adjacents);
	}

	/*
	 * Returns an iterator over all
	 * edges that exist between 'srcId' and 'dstId'.
	 */
	public GraphIterator<String> edgeBetween(String srcId, String dstId) {
		Value value = new Value();
		long nodeSrc, nodeDst, edgeId;
		int type;
		ArrayList<String> labels = new ArrayList<String>();
		TypeList tlist = g.findEdgeTypes();
		TypeListIterator listIt = tlist.iterator();
		nodeSrc = g.findObject(NodeIdType, value.setString(srcId));
		nodeDst = g.findObject(NodeIdType, value.setString(dstId));
		while(listIt.hasNext()) {
			type = listIt.nextType();
			edgeId = g.findEdge(type, nodeSrc, nodeDst);
			if (edgeId != Objects.InvalidOID) {
				labels.add(g.getType(type).getName());
			}
		}
		return new SimpleGraphIterator(labels);
	}

	/*
	 * Returns an iterator hover all edges.
	 */
	public GraphIterator<Edge> getEdges() {
		TypeList tlist = g.findEdgeTypes();
		TypeListIterator listIt = tlist.iterator();
		int type = listIt.nextType();
		Objects edges = g.select(type),edges2;
		while (listIt.hasNext()) {
			type = listIt.nextType();
			edges2 = g.select(type);
			edges.union(edges2);
			edges2.close();
		}
		return new DEXEdgeIterator(edges);
	}

	/*
	 * Returns an iterator hover all nodes.
	 */
	public GraphIterator<String> getNodes() {
		return new DEXNodeIterator(g.select(NodeType));
	}

	/*
	 * Returns the InDegree of node
	 * 'nodeId'. NULL otherwise.
	 */
	public Integer getInDegree(String nodeId) {
		if (!hasNode(nodeId)) {
			return null;
		}
		Value value = new Value();
		long nodesId = g.findObject(NodeIdType, value.setString(nodeId));
		TypeList tlist = g.findEdgeTypes();
		TypeListIterator listIt = tlist.iterator();
		long degree = 0;
		while(listIt.hasNext()) {
			degree += g.degree(nodesId, listIt.nextType(), EdgesDirection.Ingoing);
		}
		return (int) degree;
	}

	/*
	 * Returns the OutDegree of node
	 * 'nodeId'. NULL otherwise.
	 */
	public Integer getOutDegree(String nodeId) {
		if (!hasNode(nodeId)) {
			return null;
		}

		Value value = new Value();
		long nodesId = g.findObject(NodeIdType, value.setString(nodeId));
		TypeList tlist = g.findEdgeTypes();
		TypeListIterator listIt = tlist.iterator();
		long degree = 0;
		while(listIt.hasNext()) {
			degree += g.degree(nodesId, listIt.nextType(), EdgesDirection.Outgoing);
		}

		return (int) degree;
	}

	/*
	 * Return TRUE if node 'dst' can be reached from
	 * using Breadth First Search. FALSE otherwise.
	 */
	public boolean bfs(String src, String dst) {
		if(src == dst) return true;
		Value value = new Value();
		long curr, dstO;
		boolean found = false;
		dstO = g.findObject(NodeIdType, value.setString(dst));
		TraversalBFS bfs = new TraversalBFS(sess, g.findObject(NodeIdType, value.setString(src)));
		bfs.addAllEdgeTypes(EdgesDirection.Outgoing);
		bfs.addAllNodeTypes();
		while (bfs.hasNext() && !found) {
			curr = bfs.next();
			if (curr == dstO) found = true;
		}
		bfs.close();

		return found;
	}

	/*
	 * Return TRUE if node 'dst' can be reached from
	 * using Depth First Search. FALSE otherwise.
	 */
	public boolean dfs(String src, String dst) {
		if(src == dst) return true;
		Value value = new Value();
		long curr, dstO;
		boolean found = false;
		dstO = g.findObject(NodeIdType, value.setString(dst));
		TraversalDFS dfs = new TraversalDFS(sess, g.findObject(NodeIdType, value.setString(src)));
		dfs.addAllEdgeTypes(EdgesDirection.Outgoing);
		dfs.addAllNodeTypes();
		while (dfs.hasNext() && !found) {
			curr = dfs.next();
			if (curr == dstO) found = true;
		}
		dfs.close();

		return found;
	}

	/*
	 * Returns an Iterator over all nodes that belongs
	 * to the 'k' hops of 'src' node.
	 */
	public GraphIterator<String> kHops(String src, int k) {
		ArrayList<String> nodeList = new ArrayList<String>();
		if (k <= 0)
			return new SimpleGraphIterator(nodeList);
		Value value = new Value();
		TypeList tlist = g.findEdgeTypes();
		TypeListIterator listIt = tlist.iterator();
		long srcId;
		srcId = g.findObject(NodeIdType, value.setString(src));
		Objects o1 = g.neighbors(srcId, listIt.nextType(), EdgesDirection.Outgoing);
		Objects o2, aux;
		while (listIt.hasNext()) {
			aux = g.neighbors(srcId, listIt.nextType(), EdgesDirection.Outgoing);
			o1.union(aux);
			aux.close();
		}
		for (int i = 1; i < k; i++) {
			o2 = o1.copy();
			o1.close();
			listIt = tlist.iterator();
			o1 = g.neighbors(o2, listIt.nextType(), EdgesDirection.Outgoing);
			while (listIt.hasNext()) {
				aux = g.neighbors(o2, listIt.next(), EdgesDirection.Outgoing);
				o1.union(aux);
				aux.close();
			}
			o2.close();
		}

		ObjectsIterator it = o1.iterator();
		while (it.hasNext()) {
			long nodesId = it.next();
			g.getAttribute(nodesId, NodeIdType, value);
			nodeList.add(value.getString());
		}

		o1.close();
		it.close();

		return new SimpleGraphIterator(nodeList);
	}

	public void close(){
		sess.close();
		db.close();
		dex.close();
	}

	/* Iterator class for DEX Nodes */
	public class DEXNodeIterator<Object> implements GraphIterator<Object> {
		Objects sel;
		ObjectsIterator it;
		public DEXNodeIterator(Objects sel_) {
			sel = sel_;
			it = sel.iterator();
		}
		public boolean hasNext() {
			return it.hasNext();
		}
		public Object next() {
			long node = it.next();
			Value value = new Value();
			g.getAttribute(node, NodeIdType, value);
			return (Object)value.getString();
		}
		public void close() {
			it.close();
			sel.close();
		}
	}

	/* Iterator class for DEX Nodes */
	public class DEXEdgeIterator<Object> implements GraphIterator<Object> {
		Objects sel;
		ObjectsIterator it;
		public DEXEdgeIterator(Objects sel_) {
			sel = sel_;
			it = sel.iterator();
		}
		public boolean hasNext() {
			return it.hasNext();
		}
		public Object next() {
			long edgeId = it.next();
			Value value = new Value();
			EdgeData data = g.getEdgeData(edgeId);
			g.getAttribute(data.getHead(), NodeIdType, value);
			String dstId = value.getString();
			g.getAttribute(data.getTail(), NodeIdType, value);
			String srcId = value.getString();
			String edgeName = g.getType(g.getObjectType(edgeId)).getName();
			return (Object)new Edge(edgeName, srcId, dstId);
		}
		public void close() {
			it.close();
			sel.close();
		}
	}

}
