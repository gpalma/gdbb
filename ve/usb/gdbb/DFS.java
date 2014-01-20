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

import java.util.*;

public class DensestSubgraph {

	private ArrayList<Integer> inDegrees;
	private ArrayList<Integer> outDegrees;
	private ArrayList<String> indexToString;
	private HashMap<String, Integer> stringToIndex;
	private Boolean validIn[];
	private Boolean validOut[];
	private ArrayList<ArrayList<Integer>> predecessors;
	private Integer addedVertexs;

	/* Comparator used to sort nodes by in degree by increasing order
	 */
	private class CustomComparatorInDegree implements Comparator<Integer> {
		public int compare(Integer o1, Integer o2) {
			if (!validIn[o1].booleanValue() && validIn[o2].booleanValue()) {
				return 1;
			} else if (validIn[o1].booleanValue() && !validIn[o2].booleanValue()) {
				return -1;
			} else if (!validIn[o1].booleanValue() && !validIn[o2].booleanValue()) {
				return 0;
			}
			return inDegrees.get(o1).compareTo(inDegrees.get(o2));
		}
	}

	/* Comparator used to sort nodes by out degree by increasing order
	 */
	private class CustomComparatorOutDegree implements Comparator<Integer> {
		public int compare(Integer o1, Integer o2) {
			if (!validOut[o1].booleanValue() && validOut[o2].booleanValue()) {
				return 1;
			} else if (validOut[o1].booleanValue() && !validOut[o2].booleanValue()) {
				return -1;
			} else if (!validOut[o1].booleanValue() && !validOut[o2].booleanValue()) {
				return 0;
			}
			return outDegrees.get(o1).compareTo(outDegrees.get(o2));
		}
	}

	public DensestSubgraph(){
	}

	/* Procedure that updates the predecessors of all nodes
	 * in the graph.
	 */
	private void initializePredecessors(Graph g) {
		GraphIterator<String> itNodes = g.getNodes();
		this.addedVertexs = 0;
		while (itNodes.hasNext()) {
			addNode(itNodes.next());
		}
		itNodes.close();
		GraphIterator<Edge> it = g.getEdges();
		Edge curEdge;
		while (it.hasNext()) {
			curEdge = it.next();
			predecessors.get(stringToIndex.get(curEdge.getDst()))
				.add(stringToIndex.get(curEdge.getSrc()));
		}
		it.close();
	}

	private void addNode(String nodeId) {
		if (!stringToIndex.containsKey(nodeId)) {
			stringToIndex.put(nodeId, addedVertexs);
			indexToString.add(addedVertexs, nodeId);
			predecessors.add(addedVertexs, new ArrayList<Integer>());
			addedVertexs++;
		}
	}

	/* Procedure that deletes all incoming edges from the node
	 * with "nodeIndex" as index.
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

	/* Procedure that deletes all outgoing edges from the node
	 * with "nodeIndex" as index.
	 */
	protected void deleteOutgoing(int nodeIndex, Graph g) {
		GraphIterator<String> it = g.adj(indexToString.get(nodeIndex));
		int succIndx;
		while (it.hasNext()) {
			succIndx = stringToIndex.get(it.next());
			if (validIn[succIndx]) {
				inDegrees.set(succIndx, inDegrees.get(succIndx) - 1);
			}
		}
		it.close();
		validOut[nodeIndex] = false;
	}

	/* Function used to calculate the current density of the graph
	 */
	private double calculateDensity(Graph g) {
		int EdgesST = 0;
		ArrayList<Integer> S = new ArrayList<Integer>();
		ArrayList<Integer> T = new ArrayList<Integer>();
		GraphIterator<String> it;
		int sizeIn = inDegrees.size();
		int sizeOut = outDegrees.size();

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

		int sizeS = S.size(), curr, indexCurr;

		for (int i = 0; i < sizeS; i++) {
			curr = S.get(i);
			it = g.adj(indexToString.get(curr));
			while (it.hasNext()) {
				indexCurr = stringToIndex.get(it.next());
				if(validIn[indexCurr]){
					EdgesST++;
				}
			}
			it.close();
		}

		int sizeT = T.size();

		return sizeS*sizeT != 0 ? (EdgesST/Math.sqrt(sizeS*sizeT)) : 0.0;
	}

	public Graph DenseSubgraph(Graph g) {
		ArrayList<Integer> indexIn = new ArrayList<Integer>();
		ArrayList<Integer> indexOut = new ArrayList<Integer>();
		GraphIterator<String> it = g.getNodes();
		int i, j, vertexs, vertexsAll = g.V();
		double density, tmp;
		String cur;
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

		i = 0;
		while (it.hasNext()) {
			cur = it.next();
			inDegrees.add(g.getInDegree(cur));
			outDegrees.add(g.getOutDegree(cur));
			indexIn.add(i);
			indexOut.add(i);
			i++;
		}
		it.close();

		for (int k = 0; k < vertexsAll; k++) {
			validIn[k] = true;
			validOut[k] = true;
			densestValidIn[k] = true;
			densestValidOut[k] = true;
		}

		Collections.sort(indexIn, new CustomComparatorInDegree());
		Collections.sort(indexOut, new CustomComparatorOutDegree());

		density = calculateDensity(g);

		i = 0;
		j = 0;
		vertexs = 2*vertexsAll;
		while ( vertexs > 0 ) {	
			if (i < vertexsAll && j < vertexsAll) {
				if (inDegrees.get(indexIn.get(0)) <= outDegrees.get(indexOut.get(0)) ) {
					deleteIncoming(indexIn.get(0));
					i++;
				} else {
					deleteOutgoing(indexOut.get(0), g);
					j++;
				}
			} else if (i < vertexsAll && j == vertexsAll) {
				deleteIncoming(indexIn.get(0));
				i++;
			} else if (i == vertexsAll && j < vertexsAll) {
				deleteOutgoing(indexOut.get(0), g);
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
			Collections.sort(indexIn, new CustomComparatorInDegree());
			Collections.sort(indexOut, new CustomComparatorOutDegree());
			vertexs--;
		}

		Graph densestGraph = new DiGraphAdjList();
		GraphIterator<Edge> itEdges = g.getEdges();
		Edge curEdge;
		int srcIndx, dstIndx;

		while (itEdges.hasNext()) {
			curEdge = itEdges.next();
			srcIndx = stringToIndex.get(curEdge.getSrc());
			dstIndx = stringToIndex.get(curEdge.getDst());
			if (densestValidOut[srcIndx] && densestValidIn[dstIndx]) {
				densestGraph.addNode(curEdge.getSrc());
				densestGraph.addNode(curEdge.getDst());
				densestGraph.addEdge(curEdge);
			}
		}
		itEdges.close();

		return densestGraph;
	}
}
