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

import com.sparsity.dex.gdb.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class DexDB extends GraphDB {
    
    DexConfig cfg;
    Dex dex;
    Database db;
    Session sess;
    com.sparsity.dex.gdb.Graph g;
    int NodeType; //Node's ids
    int NodeIdType; //Node Attribute's ids
    int DirectsType; //Edge's ids
    int DirectsIdType; //Edge Attribute's ids

    public DexDB() {
        try {
            cfg = new DexConfig();
            dex = new Dex(cfg);
            db = dex.create("DexBD.dex", "DexBD");
            sess = db.newSession();
            g = sess.getGraph();
            NodeType = g.newNodeType("NODE");
            NodeIdType = g.newAttribute(NodeType, "ID", DataType.String, AttributeKind.Unique);
            DirectsType = g.newRestrictedEdgeType("EDGE", NodeType, NodeType, false);
            DirectsIdType = g.newAttribute(DirectsType, "ID", DataType.String, AttributeKind.Indexed);
            
        } catch (FileNotFoundException e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public DexDB(String fileName) {
        try {
            this.V = 0;
            this.E = 0;
            cfg = new DexConfig();
            dex = new Dex(cfg);
            db = dex.create("DexBD.dex", "DexBD");
            sess = db.newSession();
            g = sess.getGraph();
            NodeType = g.newNodeType("NODE");
            NodeIdType = g.newAttribute(NodeType, "ID", DataType.String, AttributeKind.Unique);
            DirectsType = g.newRestrictedEdgeType("EDGE", NodeType, NodeType, false);
            DirectsIdType = g.newAttribute(DirectsType, "ID", DataType.String, AttributeKind.Indexed);
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            int pos;
            String edgeName = "", curName = "";
            while (scanner.hasNextLine()) {
                pos = 0;
                String[] line = scanner.nextLine().split("\t");
                for (String i : line) {
                    if (pos == 0) {
                        addNode(i);
                        curName = i;
                        pos = 1;
                    } else if (pos == 1) {
                        edgeName = i;
                        pos = 2;
                    } else {
                        addNode(i);
                        addEdge(new Edge(edgeName, curName, i));
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private boolean hasNode(String nodeId) {
        Value value = new Value();
        return (g.findObject(NodeIdType, value.setString(nodeId)) != 0);
    }
    
    public void addNode(String nodeId){
        if ((!hasNode(nodeId))) {
            Value value = new Value();
            long mNode = g.newNode(NodeType);
            g.setAttribute(mNode, NodeIdType, value.setString(nodeId));
            V++;
        }
    }
    
    public boolean addEdge(Edge e) {
        if (!hasNode(e.getSrc()) || !hasNode(e.getDst())) {
            return false;
        }
        Value value = new Value();
        long anEdge;
        anEdge = g.newEdge(DirectsType,
                g.findObject(NodeIdType, value.setString(e.getSrc())),
                g.findObject(NodeIdType, value.setString(e.getDst())));
        g.setAttribute(anEdge, DirectsIdType, value.setString(e.getId()));
        E++;
        return true;
    }
    
    public Iterator<String> adj(String nodeId) {
        Value value = new Value();
        ArrayList<String> neighbors = new ArrayList<String>();
        Objects adjacents = g.neighbors(g.findObject(NodeIdType, value.setString(nodeId)), DirectsType, EdgesDirection.Outgoing);
        
        ObjectsIterator it = adjacents.iterator();
        while (it.hasNext()) {
            long nodesId = it.next();
            g.getAttribute(nodesId, NodeIdType, value);
            neighbors.add(value.getString());
        }
        
        adjacents.close();
        it.close();
        
        return neighbors.iterator();
    }
    public Iterator<Edge> getEdges() {
        Value value = new Value();
        ArrayList<Edge> edges = new ArrayList<Edge>();
        Objects ed = g.select(DirectsType);
        String edgeId, srcId, dstId;
        
        ObjectsIterator it = ed.iterator();
        while (it.hasNext()) {
            long edgesId = it.next();
            g.getAttribute(edgesId, DirectsIdType, value);
            edgeId = value.getString();
            EdgeData data = g.getEdgeData(edgesId);
            g.getAttribute(data.getTail(), NodeIdType, value);
            srcId = value.getString();
            g.getAttribute(data.getHead(), NodeIdType, value);
            dstId = value.getString();
            edges.add(new Edge(edgeId, srcId, dstId));
        }
        
        ed.close();
        it.close();
        
        return edges.iterator();
    }
    public Iterator<String> getNodes() {
        Value value = new Value();
        ArrayList<String> nodeList = new ArrayList<String>();
        Objects nodesAll = g.select(NodeType);
        
        ObjectsIterator it = nodesAll.iterator();
        while (it.hasNext()) {
            long nodesId = it.next();
            g.getAttribute(nodesId, NodeIdType, value);
            nodeList.add(value.getString());
        }
        
        nodesAll.close();
        it.close();
        
        return nodeList.iterator();
    }
    public Integer getInDegree(String nodeId) {
        Value value = new Value();
        long nodesId = g.findObject(NodeIdType, value.setString(nodeId));

        return (int) g.degree(nodesId, DirectsType, EdgesDirection.Ingoing);
    }
    public Integer getOutDegree(String nodeId) {
        Value value = new Value();
        long nodesId = g.findObject(NodeIdType, value.setString(nodeId));

        return (int) g.degree(nodesId, DirectsType, EdgesDirection.Outgoing);
    }

    public boolean patternMatching(Graph subGraph) {
        return false;
    }
    
    public Graph subGraph(int n) {
        return null;
    }
    
}
