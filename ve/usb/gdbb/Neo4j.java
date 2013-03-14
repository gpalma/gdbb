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

	private boolean hasNode(String nodeId) {
		Node newNode = nodesIdIndex.get("id", nodeId).getSingle();
		boolean exists = (newNode!=null);
		return exists;
	}

	private boolean hasEdge(Edge edge) {
		if (!this.hasNode(edge.getSrc()) || !this.hasNode(edge.getDst()))
			return false;
		
		Node src = nodesIdIndex.get("id", edge.getSrc()).getSingle();
		Node dst = nodesIdIndex.get("id", edge.getDst()).getSingle();
		
		for (Relationship rel : src.getRelationships(this.relType)) {
			if (rel.getOtherNode(src).equals(dst)) return true;
		}
		return false;
	}

	public Neo4j(String fileName, String pathDB) {
		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(pathDB);
		indexManager = graphDB.index();
		nodesIdIndex = indexManager.forNodes("ids");
		relType =  DynamicRelationshipType.withName("isRelated");
		
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
	public Neo4j(String fileName) {
		this(fileName, "some/path");
	}
	public Neo4j() {
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase("some/path");
		nodesIdIndex = graphDB.index().forNodes("ids");
		relType =  DynamicRelationshipType.withName("isRelated");
	}

    public void addNode(String nodeId){
		
		if (!hasNode(nodeId)) {
			Transaction tx = graphDB.beginTx();
			try
			{
				Node newNode = graphDB.createNode();
				newNode.setProperty("id", nodeId);
				nodesIdIndex.add(newNode, "id", nodeId);
				this.V++;
				tx.success();
			}
			finally
			{
				tx.finish();
			}
		}
	}

    public boolean addEdge(Edge e){
		if (!hasEdge(e)) {
			Node src = nodesIdIndex.get("id", e.getSrc()).getSingle();
			Node dst = nodesIdIndex.get("id", e.getDst()).getSingle();
			
			Transaction tx = graphDB.beginTx();
			try {
				if (src==null) {
					src = graphDB.createNode();
					src.setProperty("id", e.getSrc());						
					nodesIdIndex.add(src, "id", e.getSrc());				
					this.V++;
				}
				if (dst==null) { 
					dst = graphDB.createNode();
					dst.setProperty("id", e.getDst());						
					nodesIdIndex.add(dst, "id", e.getDst());
					this.V++;
				}
				
				relType = DynamicRelationshipType.withName("isRelated");
				Relationship rel = src.createRelationshipTo(dst, relType);			
				rel.setProperty("relationship-type", "isRelated");
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
	
    public Iterator<String> adj(String nodeId) {
    	return null;
    }
    public Iterator<Edge> getEdges() {
    	return null;
    }
    public Graph subGraph(int n) {
		return null;
	}
	
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
