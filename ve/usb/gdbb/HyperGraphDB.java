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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import org.hypergraphdb.*;
import org.hypergraphdb.query.Not;
import org.hypergraphdb.handle.SequentialUUIDHandleFactory;
import org.hypergraphdb.algorithms.*;
import org.hypergraphdb.util.Pair;
import org.hypergraphdb.HGIndexManager;
import org.hypergraphdb.indexing.*;

import org.hypergraphdb.storage.bje.BJEConfig;
import org.hypergraphdb.storage.bdb.BDBConfig;
import java.util.List;
public class HyperGraphDB extends GraphDB{
	public static HyperGraph graph = null; 
	protected ArrayList<String> it;
	protected ArrayList<Edge> ite;
	protected String path = "graphs/DB/hypergraphdb";
	protected int inccacheSize =  100000000;
	protected int cacheSize =  1024  * 1024 * 1024;

	/*
	 * Graph Constructor
	 */
	public HyperGraphDB(){
		this.V = 0;
		this.E = 0;

		HGConfiguration config = new HGConfiguration();
		SequentialUUIDHandleFactory handleFactory = new SequentialUUIDHandleFactory(System.currentTimeMillis(), 0);
		config.setHandleFactory(handleFactory);
		config.setMaxCachedIncidenceSetSize(inccacheSize);
		config.setSkipMaintenance(true);
		config.setTransactional(false);
		BJEConfig storeConfig = (BJEConfig) config.getStoreImplementation().getConfiguration();
		storeConfig.getEnvironmentConfig().setCacheSize(cacheSize);

		this.graph = HGEnvironment.get(path+"/prueba", config);        

		this.removeAll();
	}

	/*
	 * Graph Constructor. It receives the name of a file in sif format 
	 * Removes the actual graph and create a new one
	 */
	public HyperGraphDB(String fileName) {

		this.V = 0;
		this.E = 0;

		HGConfiguration config = new HGConfiguration();
		SequentialUUIDHandleFactory handleFactory = new SequentialUUIDHandleFactory(System.currentTimeMillis(), 0);
		config.setHandleFactory(handleFactory);
		config.setMaxCachedIncidenceSetSize(inccacheSize);
		config.setSkipMaintenance(true);
		config.setTransactional(false);
		BJEConfig storeConfig = (BJEConfig) config.getStoreImplementation().getConfiguration();
		storeConfig.getEnvironmentConfig().setCacheSize(cacheSize);

		this.graph = HGEnvironment.get(path+"/"+fileName, config);
		this.removeAll();

		readFile(fileName);
	}

	/*
	 * Graph Constructor. Loads graph k in memory
	 */
	public HyperGraphDB(int k){
		HGConfiguration config = new HGConfiguration();
		SequentialUUIDHandleFactory handleFactory = new SequentialUUIDHandleFactory(System.currentTimeMillis(), 0);
		config.setHandleFactory(handleFactory);
		config.setMaxCachedIncidenceSetSize(inccacheSize);
		config.setSkipMaintenance(true);
		config.setTransactional(false);
		BJEConfig storeConfig = (BJEConfig) config.getStoreImplementation().getConfiguration();
		storeConfig.getEnvironmentConfig().setCacheSize(cacheSize);

		this.graph = HGEnvironment.get(path+"/"+k, config);
		init();
		this.V = (int) V();
		this.E = (int) E();

	}

	/*
	 * Graph Constructor. It receive the name of a file in sif format 
	 * It removes all nodes and creates the graph
	 */
	public HyperGraphDB(String fileName, int k) {

		this.V = 0;
		this.E = 0;

		HGConfiguration config = new HGConfiguration();
		SequentialUUIDHandleFactory handleFactory = new SequentialUUIDHandleFactory(System.currentTimeMillis(), 0);
		config.setHandleFactory(handleFactory);
		config.setMaxCachedIncidenceSetSize(inccacheSize);
		config.setSkipMaintenance(true);
		config.setTransactional(false);
		BJEConfig storeConfig = (BJEConfig) config.getStoreImplementation().getConfiguration();
		storeConfig.getEnvironmentConfig().setCacheSize(cacheSize);

		this.graph = HGEnvironment.get(path+"/"+k, config);

		this.removeAll();
		readFile(fileName);
		graph.getIndexManager().register(new ByTargetIndexer(graph.getTypeSystem().getTypeHandle(HGValueLink.class), 0));
		graph.runMaintenance();
	}

	/*
	 * Load the graph from filename in sif format
	 */
	protected void readFile(String fileName){
		try{
			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			int cur = 0, curSuc;
			String edgeName = "", curName = "";
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("\t");
				String[] suc = new String[line.length-2];
				curName = line[0];
				edgeName = line[1];
				addNode(curName);
				for(int i = 2; i < line.length; i++){
					addNode(line[i]);
					suc[i-2] = line[i];
				}
				addHyperEdge(edgeName, curName, suc);
			}


			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Integer a = new Integer(this.E);
		graph.add(a);


	}


	/*
	 * Initializes V and E
	 */
	protected boolean init(){
		HGesp e = graph.getOne(HGQuery.hg.type(HGesp.class));
		this.V = (int)HGQuery.hg.count(graph, HGQuery.hg.and(HGQuery.hg.arity(0), HGQuery.hg.type(String.class)));
		this.E = graph.getOne(HGQuery.hg.type(Integer.class));
		System.out.println("Nodes: "+this.V +" Edges: " +  this.E);
		return true;
	}


	/*
	 * Return the number of nodes in graph
	 */
	public int V() {
		if(V < 0){
			init();
		}
		return V;
	}

	/*
	 * Return the number of edges in graph
	 */
	public int E() {
		if(E < 0){
			init();
		}
		return E;
	}

	/*
	 * Close graph
	 */
	public void close(){
		if(graph != null){
			graph.close();
			graph = null;
		}
	}

	/*
	 * Destructor of class
	 */
	public void finalize(){
		if(graph != null){
			graph.close();
			graph = null;
		}
	}


	/*
	 * Add an Hyperedge
	 */
	public boolean addHyperEdge(String edgename, String head, String[]suc){   
		HGHandle[] arc = new HGHandle[suc.length+1];

		arc[0] = getHandle(head);
		for(int i = 0; i < suc.length; i++){
			arc[i+1] = getHandle(suc[i]);
		}

		graph.add(new HGValueLink(new String("E"+edgename), arc));
		this.E = this.E + suc.length;

		return true;
	}

	/*
	 * Adds a node to the graph
	 */
	public void addNode(String nodeId){
		Object o = HGQuery.hg.addUnique(HyperGraphDB.graph, nodeId, HGQuery.hg.eq(nodeId));
		if(o.getClass().getName().equals("org.hypergraphdb.handle.WeakHandle")){
			this.V++;
		}
	}

	/*
	 * Adds an edge
	 */
	public boolean addEdge(Edge e){
		HGHandle h1 = getHandle(e.getSrc());
		HGHandle h2 = getHandle(e.getDst());
		graph.add(new HGValueLink(e.getId(), h1, h2));
		this.E++;
		return true;
	}

	/*
	 * Returns an iterator with the nodes adjacents to nodeId
	 */
	public Iterator<String> adj(String nodeId){
		it = new ArrayList<String>();
		HGHandle current = getHandle(nodeId);
		HGValueLink a;
		int tam;
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.incidentAt(current, 0))){
			a = (HGValueLink)s;
			tam = a.getArity();
			for(int i = 1; i < tam; i++){
				it.add((String)graph.get(a.getTargetAt(i)));
			}
		}
		return it.iterator();
	}

	/*
	 * Returns an iterator over the nodes adjacents to nodeId with the edgeName relId
	 */
	public Iterator<String> adj(String nodeId, String relId){
		it = new ArrayList<String>();
		HGHandle current = getHandle(nodeId);
		HGValueLink a;
		relId = "E"+relId;
		int tam;
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.and(HGQuery.hg.incidentAt(current, 0), HGQuery.hg.eq(relId)))){
			a = (HGValueLink)s;
			tam = a.getArity();
			for(int i = 1; i < tam; i++){
				it.add((String)graph.get(a.getTargetAt(i)));
			}
		}
		return it.iterator();
	}

	/*
	 * It's the same that adjEdges
	 */
	public Iterator<String> edgeBetween(String src, String dst){
		return adjEdges(src, dst);
	}

	/*
	 * Given a node returns the handle of the node
	 */
	public HGHandle getHandle(String nodeId){
		String s1 = graph.getOne(HGQuery.hg.and(HGQuery.hg.arity(0), HGQuery.hg.eq(nodeId), HGQuery.hg.type(String.class)));
		return graph.getHandle(s1);
	}

	/*
	 * Given a Handle returns the object
	 */
	public Object get(HGHandle h1){
		return graph.get(h1);
	}

	/*
	 * Returns true if exist an edge from n1 to n2 (Revisar)
	 */
	public boolean adjacents(String n1, String n2){
		HGHandle h1, h2;
		h1 = getHandle(n1);
		h2 = getHandle(n2);
		HGValueLink li = graph.getOne(HGQuery.hg.and(HGQuery.hg.incidentAt(h1, 0), HGQuery.hg.incident(h2)));
		return li != null;
	}

	/*
	 * Adjacent edges: set of labels of edges from x to y. 
	 */
	public Iterator<String> adjEdges(String n1, String n2){
		HGHandle h1, h2;
		HGValueLink a;
		it = new ArrayList<String>();
		h1 = getHandle(n1);
		h2 = getHandle(n2);
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.and(HGQuery.hg.incidentAt(h1, 0), HGQuery.hg.incident(h2)))){
			a = (HGValueLink) s;
			it.add((String)a.getValue());
		}
		return it.iterator();
	}

	/*
	 * Returns an iterator over all the edges of the graph
	 */
	public Iterator<Edge> getEdges(){
		ite = new ArrayList<Edge>();
		HGHandle h1, h2;
		Edge e;
		HGValueLink a;
		Link li;
		int tam;
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.and( HGQuery.hg.not(HGQuery.hg.arity(0)), HGQuery.hg.type(String.class) ))){
			a = (HGValueLink) s;
			h1 = a.getTargetAt(0);
			tam = a.getArity();
			String edgename = (String)a.getValue();
			String head = (String)graph.get(h1);
			for(int i = 1; i < tam; i++){
				h2 = a.getTargetAt(i);
				ite.add(new Edge(edgename, head, (String)graph.get(h2)));
			}
		}
		return ite.iterator();
	}

	/*
	 * Returns an iterator over all the nodes of the graph
	 */
	public Iterator<String> getNodes (){
		it = new ArrayList<String>();
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.and(HGQuery.hg.arity(0), HGQuery.hg.type(String.class) ))){
			it.add((String)s);
		}
		return it.iterator();
	}

	/*
	 * Removes all the nodes of the graph
	 */
	protected void removeAll(){
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.and(HGQuery.hg.arity(0), HGQuery.hg.type(String.class) ))){
			graph.remove(graph.getHandle(s));
		}
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.type(HGesp.class) )){
			graph.remove(graph.getHandle(s));
		}
	}

	/*
	 * Returns the in degree of nodeId
	 */
	public Integer getInDegree(String nodeId){
		Integer res = new Integer(0);
		HGHandle h = getHandle(nodeId);
		res = (int)HGQuery.hg.count(graph, HGQuery.hg.and(
					HGQuery.hg.not(HGQuery.hg.incidentAt(h, 0)), 
					HGQuery.hg.incident(h)) 
				);
		return res;
	}

	/*
	 * Returns the out degree of nodeId
	 */
	public Integer getOutDegree(String nodeId){
		Integer res = new Integer(0);
		HGHandle current = getHandle(nodeId);
		HGValueLink a;
		int tam;
		for (Object s : HGQuery.hg.getAll(graph, HGQuery.hg.incidentAt(current, 0))){
			a = (HGValueLink)s;
			tam = a.getArity();
			res = res + (tam-1);
		}
		return res;
	}

	/*
	 * Algorithm Depth first search
	 * Returns true if dst is reachable from src
	 */
	public boolean dfs(String src, String dst){
		if(src == dst) return true;
		DefaultALGenerator algen = new DefaultALGenerator(graph);
		algen.setReturnPreceeding(false);
		HGTraversal traversal = new HGDepthFirstTraversal(getHandle(src), algen);
		String v;
		while (traversal.hasNext()){
			Pair<HGHandle, HGHandle> next = traversal.next();
			v = graph.get(next.getSecond());
			if(dst.equals(v)) return true;
		}
		return false;
	}

	/*
	 * Algorithm Breadth first search
	 * Returns true if dst is reachable from src
	 */
	public boolean bfs(String src, String dst){
		if(src == dst) return true;
		DefaultALGenerator algen = new DefaultALGenerator(graph);
		algen.setReturnPreceeding(false);
		HGTraversal traversal = new HGBreadthFirstTraversal(getHandle(src), algen);
		String v;
		while (traversal.hasNext()){
			Pair<HGHandle, HGHandle> next = traversal.next();
			v = graph.get(next.getSecond());
			if(dst.equals(v)) return true;

		}
		return false;
	}

  /*
   * Returns an Iterator over all nodes that belongs
   * to the 'k' hops of 'src' node.
   */
	public Iterator<String> kHops(String src, int k) {
		ArrayList<String> nodeList = new ArrayList<String>();
		return nodeList.iterator();
	}
}



