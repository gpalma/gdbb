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
import org.neo4j.tooling.*;
import org.neo4j.graphdb.index.*;
import org.neo4j.graphdb.factory.*;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.graphdb.Expander;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

public class Neo4jCypher extends Neo4j {

	/*
	 * Public graph constructor
	 * Using a file to load the data.
	 */
	public Neo4jCypher (String fileName, int posGraph) {
		super(fileName, posGraph);
	}

	/*
	 * Public graph constructor
	 * (Empty Graph)
	 */
	public Neo4jCypher () {
		super();
	}

	/*
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId'.
	 */
	public Iterator<String> adj(String nodeId) {
		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		String QueryPM =
			"START n=node(" + 
			node.getId() + ") " +
			"MATCH n-[]->m RETURN m.idNode";

		ExecutionResult result = query(QueryPM);

		ArrayList<String> adjNodeList = new ArrayList<String>();

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				adjNodeList.add(column.getValue().toString());
			}
		}
		return adjNodeList.iterator();
	}

	/*
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId'.
	 */
	public Iterator<String> adj(String nodeId, String relId) {
		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		String QueryPM =
			"START n=node(" + node.getId() + ") " +
			"MATCH n-[:" + relId + "]->m RETURN m.idNode";

		ExecutionResult result = query(QueryPM);

		ArrayList<String> adjNodeList = new ArrayList<String>();

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				adjNodeList.add(column.getValue().toString());
			}
		}
		return adjNodeList.iterator();
	}

	/*
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId'.
	 */
	public Iterator<String> edgeBetween(String srcId, String dstId) {
		Node nodeS = nodesIdIndex.get(idNode, srcId).getSingle(),
			 nodeD = nodesIdIndex.get(idNode, dstId).getSingle();
		String QueryPM =
			"START n=node(" + nodeS.getId() + ")" +
			", m=node(" + nodeD.getId() + ") " +
			"MATCH n-[r]->m RETURN type(r)";

		ExecutionResult result = query(QueryPM);

		ArrayList<String> edgeList = new ArrayList<String>();

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				edgeList.add(column.getValue().toString());
			}
		}
		return edgeList.iterator();
	}

	/*
	 * Returns the InDegree of node
	 * 'nodeId'. NULL otherwise.
	 */
	public Integer getInDegree(String nodeId) {

		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		if (node == null)
			return null;

		String QueryPM =
			"START n=node(" + node.getId() + ") " +
			"MATCH n<-[]-m RETURN count(m)";

		ExecutionResult result = query(QueryPM);

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

		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		if (node == null)
			return null;

		String QueryPM =
			"START n=node(" + node.getId() + ") " +
			"MATCH n-[]->m RETURN count(m)";

		ExecutionResult result = query(QueryPM);

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				return (Integer.parseInt(column.getValue().toString()));
			}
		}
		return null;
	}
	
	public boolean dfs(String src, String dst) {
		Node nodeS = nodesIdIndex.get(idNode, src).getSingle(),
			 nodeD = nodesIdIndex.get(idNode, dst).getSingle();
		if (nodeS == null || nodeD == null)
			return false;
		
		String QueryPM =
			"START n=node(" + nodeS.getId() + ") " +
			", m=node(" + nodeD.getId() + ") " +
			"MATCH p=n-[*]->m RETURN p LIMIT 1";

		ExecutionResult result = query(QueryPM);

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				return true;
			}
		}

		return false;
	}
	
	public boolean bfs(String src, String dst) {
		return dfs(src,dst);
	}
	
	public Iterator<String> kHops(String src, int k) {

		ArrayList<String> nodeList = new ArrayList<String>();
		Node nodeS = nodesIdIndex.get(idNode, src).getSingle();
		if (nodeS == null)
			return nodeList.iterator();
		
		String QueryPM =
			"START n=node(" + nodeS.getId() + ") " +
			"MATCH n-[*" + k + "]->m RETURN DISTINCT m.idNode";

		ExecutionResult result = query(QueryPM);

		for ( Map<String, Object> row : result ){
			for ( Map.Entry<String, Object> column : row.entrySet() ){
				nodeList.add(column.getValue().toString());
			}
		}

		return nodeList.iterator();
	}
}
