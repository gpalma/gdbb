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
import org.hypergraphdb.HGQuery.hg.*;
public class HyperGraphDB extends GraphDB{
    
    
    protected static HyperGraph graph = null; 
    protected org.hypergraphdb.HGQuery.hg hg;
    protected ArrayList<String> it;
    protected ArrayList<Edge> ite;
    
    /*
     * Graph Constructor
     */
    public HyperGraphDB(){
        this.V = 0;
        this.E = 0;
        if(graph == null){
            this.graph = new HyperGraph("hgraph");
        }
        this.hg = new org.hypergraphdb.HGQuery.hg();
        this.removeAll();
    }
    
    /*
     * Graph Constructor. It receive the name of a file in format sif
     */
    public HyperGraphDB(String fileName) {
        try {
            HGHandle h1 = null, h2;
            this.V = 0;
            this.E = 0;
            if(graph == null){
                this.graph = new HyperGraph("hgraph");
            }
            this.hg = new org.hypergraphdb.HGQuery.hg();
            this.removeAll();
            
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            int pos, cur = 0, curSuc;
            String edgeName = "", curName = "";
            while (scanner.hasNextLine()) {
                pos = 0;
                String[] line = scanner.nextLine().split("\t");
                for (String i : line) {
                    if (pos == 0) {
                        curName = i;
                        h1 = addNode2(curName);
                        pos = 1;
                    } else if (pos == 1) {
                        edgeName = i;
                        pos = 2;
                    } else {
                        h2 = addNode2(i);
                        addEdge(edgeName, h1, h2);
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Add a node to the graph
     */
    public void addNode(String nodeId){
        graph.add(nodeId);
        this.V++;
    }
    
    /*
     * Add an node to the graph, but returns the HGHandle
     */
    public HGHandle addNode2(String nodeId){
        this.V++;
        return graph.add(nodeId);
    }
    
    /*
     * Add an edge with HGhandle h1 and h2 called id
     */
    public boolean addEdge(String id, HGHandle h1, HGHandle h2){
        Link li = new Link(id);
        graph.add(new HGValueLink(li, h1, h2));
        this.E++;
        return true;
    }
    
    /*
     * Add an edge
     */
    public boolean addEdge(Edge e){
        Link li = new Link(e.getId());
        HGHandle h1 = graph.getHandle(e.getSrc());
        HGHandle h2 = graph.getHandle(e.getDst());
        graph.add(new HGValueLink(li, h1, h2));
        this.E++;
        return true;
    }
    
    /*
     * Returns an iterator with the nodes adjacents to nodeId
     */
    public Iterator<String> adj(String nodeId){
        it = new ArrayList();
        HGSearchResult<HGHandle> rs;
        HGHandle current = graph.getHandle(nodeId);
        HGValueLink a;
        for (Object s : hg.getAll(graph, hg.orderedLink(current))){
            a = (HGValueLink)s;
            it.add((String)graph.get(a.getTargetAt(1)));
        }
        return it.iterator();
    }
    
    /*
     * Returns an iterator with all the edges of the graph
     */
    public Iterator<Edge> getEdges(){
        ite = new ArrayList();
        HGHandle h1, h2;
        Edge e;
        HGValueLink a;
        Link li;
        for (Object s : hg.getAll(graph, hg.type(Link.class))){
            a = (HGValueLink) s;
            h1 = a.getTargetAt(0);
            h2 = a.getTargetAt(1);
            li = (Link)a.getValue();
            
            ite.add(new Edge(li.s, (String)graph.get(h1), (String)graph.get(h2)));
        }
        return ite.iterator();
    }
    
    /*
     * Returns an iterator with all the nodes of the graph
     */
    public Iterator<String> getNodes (){
        it = new ArrayList();
        for (Object s : hg.getAll(graph, hg.type(String.class))){
            it.add((String)s);
        }
        return it.iterator();
    }
    
    /*
     * Pattern Matching (Commented by Alejandro and Johnathan)
     */
    public boolean patternMatching(Graph subGraph){
        return false;
    }
    
    /*
     * Removes all the nodes of the graph
     */
    protected void removeAll(){
        for (Object s : hg.getAll(graph, hg.type(String.class))){
            graph.remove(graph.getHandle(s));
        }
    }
    
    /*
     * Destructor of the class clase
     */
    protected void finalize(){
        if(graph != null){
            graph.close();
        }
    }
    
    /*
     * Returns the in degree of nodeId
     */
    public Integer getInDegree(String nodeId){
        HGSearchResult<HGHandle> rs;
        HGHandle current;
        HGValueLink a;
        Integer res = new Integer(0);
        HGHandle h = graph.getHandle(nodeId);
        rs = graph.find(hg.incident(h));
        
        while (rs.hasNext()){
            current = rs.next();
            a = graph.get(current);
            if(graph.get(a.getTargetAt(1)).equals(nodeId)){
                res++;
            }
        }
        return res;
    }
    
    /*
     * Returns the out degree of nodeId
     */
    public Integer getOutDegree(String nodeId){
        HGSearchResult<HGHandle> rs;
        HGHandle current;
        HGValueLink a;
        Integer res = new Integer(0);
        HGHandle h = graph.getHandle(nodeId);
        rs = graph.find(hg.incident(h));
        
        while (rs.hasNext()){
            current = rs.next();
            a = graph.get(current);
            if(graph.get(a.getTargetAt(0)).equals(nodeId)){
                res++;
            }
        }
        return res;
    }
    
    /*
     * Testing
     */
    public static void main(String args[]){
        HyperGraphDB graph = new HyperGraphDB();
        
        HGHandle h1, h2 , h3;
        h1 = graph.addNode2("n1");
        h2 = graph.addNode2("n2");
        h3 = graph.addNode2("n3");
        graph.addEdge(new Edge("lado1", "n1", "n2"));
        graph.addEdge("lado2",h1, h3);
        
        Iterator<String> it = graph.adj("n1");
        System.out.print("Sucesores de n1:");
        while(it.hasNext()){
            System.out.print(" "+it.next());
        }System.out.print("\n");
        
        System.out.println("Grado de entrada de n1:"+graph.getInDegree("n1"));
        System.out.println("Grado de salida de n1:"+graph.getOutDegree("n1"));
        
        it = graph.getNodes();
        System.out.print("Todos los nodos:");
        while(it.hasNext()){
            System.out.print(" "+it.next());
        }System.out.print("\n");
        
        Iterator<Edge> ite = graph.getEdges();
        System.out.print("Todos los Arcos:");
        while(ite.hasNext()){
            System.out.print(" "+ite.next());
        }System.out.print("\n");
        
        
        
    }
    
}



