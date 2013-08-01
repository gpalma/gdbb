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
import java.util.*;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.factory.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.unsafe.batchinsert.*;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.cypher.javacompat.*;
import org.neo4j.tooling.*;
import org.neo4j.kernel.*;
import org.neo4j.helpers.collection.*;

public class Neo4j extends GraphDB {

	public GraphDatabaseService graphDB;
	public GlobalGraphOperations globalOP;
	public IndexManager indexManager;
	public Index<Node> nodesIdIndex;
	public ArrayList<String> nodesId;
	public ArrayList<Edge> rels;
	private Transaction tx;
	public String idNode = "idNode";

	/*
	 * Private Constructor
	 * with possibility to change the
	 * store directory of the graph.
	 */
	private Neo4j(String fileName, String pathDB) {

		graphDB = new GraphDatabaseFactory().
				newEmbeddedDatabaseBuilder(pathDB).
				setConfig(GraphDatabaseSettings.node_keys_indexable, idNode).
				setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
				setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true").
				newGraphDatabase();
		globalOP = GlobalGraphOperations.at(graphDB);
		Iterator<Node> nodeIt = globalOP.getAllNodes().iterator();
		nodeIt.next();
		boolean newG = !nodeIt.hasNext();

		this.nodesId = new ArrayList<String>();
		this.rels = new ArrayList<Edge>();

		if (newG) {
			graphDB.shutdown();
			
			BatchInserter inserter = BatchInserters.inserter(pathDB);
			BatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(inserter);
			BatchInserterIndex indexBatch = indexProvider.
				nodeIndex(idNode, MapUtil.stringMap("type","exact"));
			indexBatch.setCacheCapacity(idNode, 5000000);

			V = 0;
			E = 0;

			try {
				File file = new File(fileName);
				Scanner scanner = new Scanner(file);
				int pos;
				long src = 0, dst;
				String edgeType = "", srcNode = "";
				Map<String, Object> properties;
				DynamicRelationshipType relType;
				IndexHits<Long> hitSearch;

				while (scanner.hasNextLine()) {
					pos = 0;
					String[] line = scanner.nextLine().split("\t");
					for (String curNode : line) {
						if (pos == 0) {
							srcNode = curNode;
							pos = 1;

							hitSearch = indexBatch.get(idNode,srcNode);
							if (hitSearch.size() == 0) {
								properties = MapUtil.map(idNode,srcNode);
								src = inserter.createNode(properties);
								indexBatch.add(src, properties);
								nodesId.add(srcNode);
								V++;
							} else {
								src = hitSearch.getSingle();
							}

						} else if (pos == 1) {
							edgeType = curNode;
							pos = 2;
						} else {

							hitSearch = indexBatch.get(idNode,curNode);
							if (hitSearch.size() == 0) {
								properties = MapUtil.map(idNode,curNode);
								dst = inserter.createNode(properties);
								indexBatch.add(dst, properties);
								nodesId.add(curNode);
								V++;
							} else {
								dst = hitSearch.getSingle();
							}

							relType = DynamicRelationshipType.withName(edgeType);
							inserter.createRelationship(src, dst, relType, null);
							rels.add(new Edge(edgeType, srcNode, curNode));
							E++;
						}
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				indexBatch.flush();
				indexProvider.shutdown();
				inserter.shutdown();
			}

			// Load graph again
			graphDB = new GraphDatabaseFactory().
				newEmbeddedDatabaseBuilder(pathDB).
				setConfig(GraphDatabaseSettings.node_keys_indexable, idNode).
				setConfig(GraphDatabaseSettings.node_auto_indexing, "true").
				setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true").
				newGraphDatabase();
			globalOP = GlobalGraphOperations.at(graphDB);
			indexManager = graphDB.index();
			nodesIdIndex = indexManager.forNodes(idNode);

		} else {

			indexManager = graphDB.index();
			nodesIdIndex = indexManager.forNodes(idNode);

			Relationship newRel;
			String node;
			V = 0;
			E = 0;

			while(nodeIt.hasNext()) {
				node = (String) nodeIt.next().getProperty(idNode);
				nodesId.add(node);
				V++;
			}
			
			Iterator<Relationship> relIt = globalOP.getAllRelationships().iterator();
			while(relIt.hasNext()) {
				newRel = relIt.next();
				rels.add(new Edge(
					newRel.getType().name(),
					(String) newRel.getStartNode().getProperty(idNode),
					(String) newRel.getEndNode().getProperty(idNode)
				));
				E++;
			}
		}
	}

	/*
	 * Public graph constructor
	 * Using a file to load the data.
	 */
	public Neo4j(String fileName, int posGraph) {
		this(fileName, "graphs/DB/Neo4j/" + posGraph);
	}

	/*
	 * Public graph constructor
	 * (Empty Graph)
	 */
	public Neo4j() {
		graphDB = new GraphDatabaseFactory().
			newEmbeddedDatabase("graphs/DB/Neo4j/Default");
		nodesIdIndex = graphDB.index().forNodes(idNode);
	}

	/*
	 * Returns True if node 'nodeId'
	 * is stored in the graph.
	 * False otherwise.
	 */
	private boolean hasNode(String nodeId) {
		Node newNode = nodesIdIndex.get(idNode, nodeId).getSingle();
		return (newNode!=null);
	}

	/*
	 * Returns True if edge 'edge'
	 * is stored in the graph.
	 * False otherwise.
	 */
	private boolean hasEdge(Edge edge) {
		if (!this.hasNode(edge.getSrc()) || !this.hasNode(edge.getDst()))
			return false;

		Node src = nodesIdIndex.get(idNode, edge.getSrc()).getSingle();
		Node dst = nodesIdIndex.get(idNode, edge.getDst()).getSingle();

		for (Relationship rel : src.getRelationships(Direction.OUTGOING)) {
			if (rel.getOtherNode(src).equals(dst) &&
				rel.getType().name().equals(edge.getId()))
				return true;
		}
		return false;
	}

	/*
	 * Adds node 'nodeId' to the graph.
	 */
	public void addNode(String nodeId){

		if (!hasNode(nodeId)) {
			tx = graphDB.beginTx();
			try {
				Node newNode = graphDB.createNode();
				newNode.setProperty(idNode, nodeId);
				nodesIdIndex.add(newNode, idNode, nodeId);
				this.nodesId.add(nodeId);
				this.V++;
				tx.success();
			} finally {
				tx.finish();
			}
		}
	}

	/*
	 * Adds edge 'e' to the graph.
	 */
	public boolean addEdge(Edge e){
		if (!hasEdge(e)) {
			Node src = nodesIdIndex.get(idNode, e.getSrc()).getSingle();
			Node dst = nodesIdIndex.get(idNode, e.getDst()).getSingle();

			tx = graphDB.beginTx();
			try {
				if (src==null) {
					src = graphDB.createNode();
					src.setProperty(idNode, e.getSrc());
					nodesIdIndex.add(src, idNode, e.getSrc());
					this.V++;
				}
				if (dst==null) { 
					dst = graphDB.createNode();
					dst.setProperty(idNode, e.getDst());
					nodesIdIndex.add(dst, idNode, e.getDst());
					this.V++;
				}

				DynamicRelationshipType relType = DynamicRelationshipType.withName(e.getId());
				Relationship rel = src.createRelationshipTo(dst, relType);
				this.rels.add(e);
				this.E++;
				tx.success();
			} finally {
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
		
		ArrayList<String> adjNodeList = new ArrayList<String>();
		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		if (node == null)
			return adjNodeList.iterator();

		Iterator<Relationship> itAdjRel = node.getRelationships(Direction.OUTGOING).iterator();

		while (itAdjRel.hasNext()) {
			adjNodeList.add( (String)itAdjRel.next().getOtherNode(node).getProperty(idNode) );
		} 
		
		return adjNodeList.iterator();
	}

	/*
	 * Returns an iterator over all
	 * nodes adjacents to 'nodeId' with 'relId' as connection.
	 */
	public Iterator<String> adj(String nodeId, String relId) {
		
		ArrayList<String> adjNodeList = new ArrayList<String>();
		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		if (node == null)
			return adjNodeList.iterator();

		Iterator<Relationship> itAdjRel = node.getRelationships(Direction.OUTGOING).iterator();
		Relationship rel;

		while (itAdjRel.hasNext()) {
			rel = itAdjRel.next();
			if (rel.getType().name().equals(relId))
				adjNodeList.add( (String)rel.getOtherNode(node).getProperty(idNode) );
		} 
		
		return adjNodeList.iterator();
	}

	/*
	 * Returns an iterator over all
	 * edges that exist between 'srcId' and 'dstId'.
	 */
	public Iterator<String> edgeBetween(String srcId, String dstId) {
		
		ArrayList<String> edgeList = new ArrayList<String>();
		Node srcNode = nodesIdIndex.get(idNode, srcId).getSingle();
		if (srcNode == null)
			return edgeList.iterator();

		Iterator<Relationship> itAdjRel = srcNode.getRelationships(Direction.OUTGOING).iterator();
		Relationship rel;

		while (itAdjRel.hasNext()) {
			rel = itAdjRel.next();
			if (rel.getOtherNode(srcNode).getProperty(idNode).equals(dstId))
				edgeList.add(rel.getType().name());
		} 
		
		return edgeList.iterator();
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

		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		if (node == null)
			return null;

		Iterator<Relationship> itAdjRel = node.getRelationships(Direction.INCOMING).iterator();
		Integer inDegree = 0;
		while (itAdjRel.hasNext()) {
			inDegree++;
			itAdjRel.next();
		}
		
		return inDegree;
	}

	/*
	 * Returns the OutDegree of node
	 * 'nodeId'. NULL otherwise.
	 */
	public Integer getOutDegree (String nodeId) {

		Node node = nodesIdIndex.get(idNode, nodeId).getSingle();
		if (node == null)
			return null;

		Iterator<Relationship> itAdjRel = node.getRelationships(Direction.OUTGOING).iterator();
		Integer outDegree = 0;
		while (itAdjRel.hasNext()) {
			outDegree++;
			itAdjRel.next();
		}
		
		return outDegree;
	}
	
  /*
   * Return TRUE if node 'dst' can be reached from
   * using Depth First Search. FALSE otherwise.
   */
	public boolean dfs(String src, String dst) {
		Node nodeS = nodesIdIndex.get(idNode, src).getSingle(),
			 nodeD = nodesIdIndex.get(idNode, dst).getSingle();
		if (nodeS == null || nodeD == null)
			return false;
		for (Node currentNode : Traversal
			.description()
			.depthFirst()
			.expand(Traversal.expanderForAllTypes(Direction.OUTGOING))
			.uniqueness(Uniqueness.NODE_GLOBAL)
			.evaluator(
				(Evaluator) Evaluators.endNodeIs(
				Evaluation.INCLUDE_AND_CONTINUE,
				Evaluation.EXCLUDE_AND_CONTINUE,
				nodeD))
			.traverse(nodeS)
			.nodes())
		{
			if (currentNode.getProperty(idNode).equals(dst))
				return true;
		}
		return false;
	}
	
  /*
   * Return TRUE if node 'dst' can be reached from
   * using Breadth First Search. FALSE otherwise.
   */
	public boolean bfs(String src, String dst) {
		Node nodeS = nodesIdIndex.get(idNode, src).getSingle(),
			 nodeD = nodesIdIndex.get(idNode, dst).getSingle();
		if (nodeS == null || nodeD == null)
			return false;
		for (Node currentNode : Traversal
			.description()
			.breadthFirst()
			.expand(Traversal.expanderForAllTypes(Direction.OUTGOING))
			.uniqueness(Uniqueness.NODE_GLOBAL)
			.evaluator(
				(Evaluator) Evaluators.endNodeIs(
				Evaluation.INCLUDE_AND_CONTINUE,
				Evaluation.EXCLUDE_AND_CONTINUE,
				nodeD))
			.traverse(nodeS)
			.nodes())
		{
			if (currentNode.getProperty(idNode).equals(dst))
				return true;
		}
		return false;
	}
	
  /*
   * Returns an Iterator over all nodes that belongs
   * to the 'k' hops of 'src' node.
   */
	public Iterator<String> kHops(String src, int k) {
		HashSet<String> nodeList = new HashSet<String>();
		Node nodeS = nodesIdIndex.get(idNode, src).getSingle();
		if (nodeS == null) { System.out.println("Hola");
			return nodeList.iterator();}
		for (Node currentNode : Traversal
			.description()
			.breadthFirst()
			.expand(Traversal.expanderForAllTypes(Direction.OUTGOING))
			.uniqueness(Uniqueness.NODE_LEVEL)
			.evaluator( Evaluators.includingDepths(k,k) )
			.traverse(nodeS)
			.nodes())
		{
			nodeList.add((String)currentNode.getProperty(idNode));
		}
		return nodeList.iterator();
	}

	public ExecutionResult query (String QueryPM) {
		ExecutionEngine engine = new ExecutionEngine(graphDB);
		return engine.execute(QueryPM);
	}
	
	public void close(){
		graphDB.shutdown();
	}

	protected void finalize(){
		if(graphDB != null){
			graphDB.shutdown();
		}
	}
}
