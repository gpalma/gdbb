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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 *
 * @author josegregorio
 */
public class DensestSubgraph {
    
    private ArrayList<Integer> inDegrees;
    private ArrayList<Integer> outDegrees;
    private ArrayList<String> indexToString;
    private HashMap<String, Integer> stringToIndex;
    private Boolean validIn[];
    private Boolean validOut[];
    private ArrayList<ArrayList<Integer>> predecessors;
    private Integer addedVertexs;
    
    public DensestSubgraph(){
    }
    
    private void initializePredecessors(Graph g) {
        Iterator<Edge> it = g.getEdges();
        Edge curEdge;
        this.addedVertexs = 0;
        while (it.hasNext()) {
            curEdge = it.next();
            addNode(curEdge.getSrc());
            addNode(curEdge.getDst());
            predecessors.get(stringToIndex.get(curEdge.getDst()))
                    .add(stringToIndex.get(curEdge.getSrc()));
        }
    }
    
    private void addNode(String nodeId) {
        if (!stringToIndex.containsKey(nodeId)) {
            stringToIndex.put(nodeId, addedVertexs);
            indexToString.add(addedVertexs, nodeId);
            predecessors.add(addedVertexs, new ArrayList<Integer>());
            addedVertexs++;
        }
    }
    
    /* Funcion que elimina todos los arcos en el grafo que llegan al vertice
     * node.
     */
    protected void deleteIncoming(int nodeIndex) {
        ArrayList<Integer> pred = predecessors.get(nodeIndex);
        int size = pred.size(), predIndx;
        for (int i = 0; i < size; i++) {
            predIndx = pred.get(i);
            if (validOut[predIndx]) {
                outDegrees.set(predIndx, outDegrees.get(predIndx) - 1);
            }
        }
        validIn[nodeIndex] = false;
    }
    
    /* Funcion que elimina todos los arcos en el grafo que salen del vertice
     * node.
     */
    protected void deleteOutgoing(int nodeIndex, Graph g) {
        Iterator<String> it = g.adj(indexToString.get(nodeIndex));
        int succIndx;
        while (it.hasNext()) {
            succIndx = stringToIndex.get(it.next());
            if (validIn[succIndx]) {
                inDegrees.set(succIndx, inDegrees.get(succIndx) - 1);
            }
        }
        validOut[nodeIndex] = false;
    }
    
    private int getMinimumInDegree() {
        int size = inDegrees.size();
        int minimum = -1;
        for (int i = 0; i < size; i++) {
            if (validIn[i]) {
                if (minimum != -1) {
                    if (inDegrees.get(i) < inDegrees.get(minimum)) {
                        minimum = i;
                    }
                } else {
                    minimum = i;
                }
            }
        }
        return minimum;
    }
    
    private int getMinimumOutDegree() {
        int size = outDegrees.size();
        int minimum = -1;
        for (int i = 0; i < size; i++) {
            if (validOut[i]) {
                if (minimum != -1) {
                    if (outDegrees.get(i) < outDegrees.get(minimum)) {
                        minimum = i;
                    }
                } else {
                    minimum = i;
                }
            }
        }
        return minimum;
    }
    
    private double calculateDensity(Graph g) {
      int EdgesST = 0;
    	ArrayList<Integer> S = new ArrayList<Integer>();
    	ArrayList<Integer> T = new ArrayList<Integer>();
    	int sizeIn = inDegrees.size();
    	int sizeOut = outDegrees.size();
        Adjacencies adj = new Adjacencies();
        
    	for ( int i = 0; i < sizeIn; i++ ) {
            if (validIn[i]) {
    		if (inDegrees.get(i) > 0) {
    		    T.add(i);
    		}
            }
    	}
    	
    	for ( int j = 0; j < sizeOut; j++ ) {
            if (validOut[j]) {
                if (outDegrees.get(j) > 0) {
                    S.add(j);
                }
            }
    	}

	int sizeS = S.size();
	int sizeT = T.size();
	for ( int x = 0; x < sizeS; x++ ) {
            for ( int y = 0; y < sizeT; y++ ) {
                if ( adj.adjNodesRestricted(g, 
                    indexToString.get(S.get(x)), 
                    indexToString.get(T.get(y)) )) {
                    EdgesST++;
                }
            }		
    	}
        
    	return sizeS*sizeT != 0 ? (EdgesST/Math.sqrt(sizeS*sizeT)) : 0.0;
    }
    
    public void DenseSubgraph(Graph g) {
        Iterator<Integer> inDegreeIT = g.getInDegree();
        Iterator<Integer> outDegreeIT = g.getOutDegree();
        Iterator<String> it = g.getNodes();
        int i, j, vertexs, Vi, Vo, vertexsAll = g.V();
        double density, tmp;
        Boolean densestValidIn[] = new Boolean[vertexsAll];
        Boolean densestValidOut[] = new Boolean[vertexsAll];
        
        this.indexToString = new ArrayList<String>();
        this.stringToIndex = new HashMap<String, Integer>();
        this.inDegrees = new ArrayList<Integer>();
        this.outDegrees = new ArrayList<Integer>();
        this.validIn = new Boolean[vertexsAll];
        this.validOut = new Boolean[vertexsAll];
        this.predecessors = new ArrayList<ArrayList<Integer>>();
        initializePredecessors(g);
        
        while (it.hasNext() && inDegreeIT.hasNext() && outDegreeIT.hasNext()) {
            indexToString.add(it.next());
            inDegrees.add(inDegreeIT.next());
            outDegrees.add(outDegreeIT.next());
        }
        
        for (int k = 0; k < vertexsAll; k++) {
            validIn[k] = true;
            validOut[k] = true;
            densestValidIn[k] = true;
            densestValidOut[k] = true;
        }
        
        density = calculateDensity(g);
        
        i = 0;
        j = 0;
        vertexs = 2*vertexsAll;
	while ( vertexs > 0 ) {	
            if (i < vertexsAll && j < vertexsAll) {
                Vi = getMinimumInDegree();
                Vo = getMinimumOutDegree();
                if (inDegrees.get(Vi) <= outDegrees.get(Vo) ) {
                    deleteIncoming(Vi);
                    i++;
                } else {
                    deleteOutgoing(Vo, g);
                    j++;
                }
            } else if (i < vertexsAll && j == vertexsAll) {
                Vi = getMinimumInDegree();
                deleteIncoming(Vi);
                i++;
            } else if (i == vertexsAll && j < vertexsAll) {
                Vo = getMinimumOutDegree();
                deleteOutgoing(Vo, g);
                j++;
            }

            tmp = calculateDensity(g);

            if (density < tmp) {
                density = tmp;
                for (int k = 0; k < vertexsAll; k++) {
                    densestValidIn[k] = this.validIn[k];
                    densestValidOut[k] = this.validOut[k];
                }
            }

            vertexs--;
	}
	
	System.out.println("The best density of a subgraph of the tested graph is " + density);
        print("BestDensitySubgraph", g , densestValidIn, densestValidOut);
    }
    
    public void print(String File, Graph g, Boolean[] validIn, Boolean[] validOut) {
    	try{
            FileWriter fstream = new FileWriter(File);
            BufferedWriter out = new BufferedWriter(fstream);
            Iterator<Edge> archs = g.getEdges();
            Edge curr;
            int srcIndx, dstIndx;
            while ( archs.hasNext() ) {
                curr = archs.next();
                srcIndx = stringToIndex.get(curr.getSrc());
                dstIndx = stringToIndex.get(curr.getDst());
                if (validOut[srcIndx] && validIn[dstIndx]) {
                    out.write(curr.getSrc()+"\t" + curr.getId() +"\t"+curr.getDst() + "\n");
                }
            }
            out.close();
  	}catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
  	}
    }
}
