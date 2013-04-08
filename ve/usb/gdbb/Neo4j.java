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
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import org.neo4j.cypher.javacompat.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.*;
import org.neo4j.graphdb.factory.*;

public class Neo4j extends GraphDB {

	private GraphDatabaseService graphDB;
	private DynamicRelationshipType relType;
	private IndexManager indexManager;
	private Index<Node> nodesIdIndex;
	private ArrayList<String> nodesId;
	private ArrayList<Edge> rels;

	/*
	 * Private Constructor
	 * with possibility to change the
	 * store directory of the graph.
	 */
	private Neo4j(String fileName, String pathDB) {

		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(pathDB);
		indexManager = graphDB.index();
		nodesIdIndex = indexManager.forNodes("ids");
		relType =  DynamicRelationshipType.withName("isRelated");
		this.nodesId = new ArrayList<String>();
		this.rels = new ArrayList<Edge>();

		try {
			this.V = 0;
			this.E = 0;
			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			int pos;
			String edgeName = "", curName = "";
			while (scanner.hasNextLine()) {
				pos = 0;
				String[] line = scanner.nextLine().split("\t");
				for (String i : line) {
					if (pos == 0) {
						if (!hasNode(i)) {
							addNode(i);
						}
						curName = i;
						pos = 1;
					} else if (pos == 1) {
						edgeName = i;
						pos = 2;
					} else {
						if (!hasNode(i)) {
							addNode(i);
						}
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
	 * Public graph constructor
	 * Using a file to load the data.
	 */
	public Neo4j(String fileName) {
		this(fileName, "Graphs/Neo4j/");
	}

	/*
	 * Public graph constructor
	 * (Empty Graph)
	 */
	public Neo4j() {
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase("Graphs/Neo4j/");
		nodesIdIndex = graphDB.index().forNodes("ids");
		relType =  DynamicRelationshipType.withName("isRelated");
	}

	/*
	 * Returns True if node 'nodeId'
	 * is stored in the graph.
	 * False otherwise.
	 */
	private boolean hasNode(String nodeId) {
		Node newNode = nodesIdIndex.get("idNode", nodeId).getSingle();
		boolean exists = (newNode!=null);
		return exists;
	}

	/*
	 * Returns True if edge 'edge'
	 * is stored in the graph.
	 * False otherwise.
	 */
	private boolean hasEdge(Edge edge) {
		if (!this.hasNode(edge.getSrc()) || !this.hasNode(edge.getDst()))
			return false;

		Node src = nodesIdIndex.get("idNode", edge.getSrc()).getSingle();
		Node dst = nodesIdIndex.get("idNode", edge.getDst()).getSingle();

		for (Relationship rel : src.getRelationships(this.relType)) {
			if (rel.getOtherNode(src).equals(dst)) return true;
		}
		return false;
	}

	/*
	 * Adds node 'nodeId' to the graph.
	 */
	public void addNode(String nodeId){

		if (!hasNode(nodeId)) {
			Transaction tx = graphDB.beginTx();
			try
			{
				Node newNode = graphDB.createNode();
				newNode.setProperty("idNode", nodeId);
				nodesIdIndex.add(newNode, "idNode", nodeId);
				this.nodesId.add(nodeId);
				this.V++;
				tx.success();
			}
			finally
			{
				tx.finish();
			}
		}
	}

	/*
	 * Adds edge 'e' to the graph.
	 */
	public boolean addEdge(Edge e){
		if (!hasEdge(e)) {
			Node src = nodesIdIndex.get("idNode", e.getSrc()).getSingle();
			Node dst = nodesIdIndex.get("idNode", e.getDst()).getSingle();

			Transaction tx = graphDB.beginTx();
			try {
				if (src==null) {
					src = graphDB.createNode();
					src.setProperty("idNode", e.getSrc());						
					nodesIdIndex.add(src, "idNode", e.getSrc());				
					this.V++;
				}
				if (dst==null) { 
					dst = graphDB.createNode();
					dst.setProperty("idNode", e.getDst());						
					nodesIdIndex.add(dst, "idNode", e.getDst());
					this.V++;
				}

				relType = DynamicRelationshipType.withName("isRelated");
				Relationship rel = src.createRelationshipTo(dst, relType);			
				rel.setProperty("relationship-type", "isRelated");
				this.rels.add(e);
				this.E++;
				tx.success();
			}
			finally {
				tx.finish();
			}
			return true;
		}
		return false;
	}

	/*
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId'.
	 */
	public Iterator<String> adj(String nodeId) {
		String QueryPM =
			"START n=node:node_auto_index(idNode='" + 
			nodeId + "') " +
			"MATCH n-[]->m RETURN m.idNode";

		ExecutionEngine engine = new ExecutionEngine(graphDB);
		ExecutionResult result = engine.execute(QueryPM);

		ArrayList<String> adjNodeList = new ArrayList<String>();

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				adjNodeList.add(column.getValue().toString());
			}
		}
		return adjNodeList.iterator();
	}

	/*
	 * Returns an iterator hover all edges.
	 */
	public Iterator<Edge> getEdges() {
		return rels.iterator();
	}

	/*
	 * Returns an iterator hover all nodes.
	 */
	public Iterator<String> getNodes () {
		return nodesId.iterator();
	}

	/*
	 * Returns the InDegree of node
	 * 'nodeId'. NULL otherwise.
	 */
	public Integer getInDegree(String nodeId) {

		String QueryPM =
			"START n=node:node_auto_index(idNode='" + 
			nodeId + "') " +
			"MATCH m-[]->n RETURN count(m)";

		ExecutionEngine engine = new ExecutionEngine(graphDB);
		ExecutionResult result = engine.execute(QueryPM);

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				return (Integer.parseInt(column.getValue().toString()));
			}
		}
		return null;
	}

	/*
	 * Returns the OutDegree of node
	 * 'nodeId'. NULL otherwise.
	 */
	public Integer getOutDegree (String nodeId) {

		String QueryPM =
			"START n=node:node_auto_index(idNode='" + 
			nodeId + "') " +
			"MATCH n-[]->m RETURN count(m)";

		ExecutionEngine engine = new ExecutionEngine(graphDB);
		ExecutionResult result = engine.execute(QueryPM);

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				return (Integer.parseInt(column.getValue().toString()));
			}
		}
		return null;
	}

	/*
	 * Receives the graph 'subGraph', and returns
	 * True if the Graph DB finds a match inside
	 * the graph it stores.
	 */
	public boolean patternMatching(Graph subGraph) {
		Iterator<Edge> subGEdges = subGraph.getEdges();
		if (!subGEdges.hasNext())
			return false;
		Edge curE = null;
		String QueryPM = "";
		while (subGEdges.hasNext()) {
			curE = subGEdges.next();
			QueryPM += 	"n_" + (curE.getSrc()) +
				"-[]->n_" + (curE.getDst());
			if (subGEdges.hasNext())
				QueryPM += ",";
		}
		QueryPM = 	"START n_" + curE.getSrc() +
			"=node(*) MATCH " + QueryPM +
			" RETURN count(n_" + curE.getSrc() + ")";
		ExecutionEngine engine = new ExecutionEngine(graphDB);
		ExecutionResult result = engine.execute(QueryPM);

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				if (Integer.parseInt(column.getValue().toString())>0)
					return true;
			}
		}
		return false;
	}
}
