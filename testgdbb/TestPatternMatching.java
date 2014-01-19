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

package testgdbb;

import ve.usb.gdbb.*;
import java.util.*;
import java.lang.*;

public class TestPatternMatching extends Test{
	
	protected GraphDB graphTest;
	protected BFS bfsTest;
	protected String[] params;
	protected int graphPos;

	public TestPatternMatching() {
		TestName = "Pattern Matching";
	}

	protected void adjacentX() {
		int N = 0;
		GraphIterator<String> it = graphTest.adj(params[0]);
		while (it.hasNext()) {
			N++;
			it.next();
		}
		it.close();
		System.out.println("adjacentX\n" + N
			+ " adjacent nodes\nto node '" + params[0] + "'.");
	}

	protected void adjacentXP() {
		int N = 0;
		GraphIterator<String> it = graphTest.adj(params[0],params[1]);
		while (it.hasNext()) {
			N++;
			it.next();
		}
		it.close();
		System.out.println("adjacentXP\n" + N + " adjacent nodes\nto node '"
			+ params[0] + "' using\n'" + params[1] + "' relationship.");
	}

	protected void edgeBetween() {
		int N = 0;
		GraphIterator<String> it = graphTest.edgeBetween(params[0],params[2]);
		while (it.hasNext()) {
			N++;
			it.next();
		}
		it.close();
		System.out.println("edgeBetween\n" + N + " different \nrelationships\nbetween nodes '"
			+ params[0] + "'\nand '" + params[2] + "'.");
	}

	protected void ExternalHop(int k) {
		int N = 0;
		bfsTest = new BFS(this.graphTest);
		GraphIterator<String> it = bfsTest.kHopsNeighborhood(params[0],k);
		while (it.hasNext()) {
			N++;
			it.next();
		}
		it.close();
		System.out.println("External " + k + "-Hop\n" + N + " nodes belong\nto the "
			+ k + "-hop set\nof '" + params[0] + "'.");
	}

	protected void InternalHop(int k) {
		int N = 0;
		GraphIterator<String> it = this.graphTest.kHops(params[0],k);
		while (it.hasNext()) {
			N++;
			it.next();
		}
		System.out.println("Internal " + k + "-Hop\n" + N + " nodes belong\nto the " +
			k + "-hop set\nof '" + params[0] + "'.");
	}
  
	protected boolean testGraph(){
		return true;
	}
	protected void setgraph(Graph g){
		this.graphTest = (GraphDB)g;
	}

	public boolean testPM(int graph, int option, int testOpt) {
		boolean testOk = true;
		graphPos = graph;

		if (testOpt < 0 || 8 < testOpt) {
			System.out.println(
					"The selected option is not valid. Use instead:\n"
					+ "  [0] adjacentX test\n"
					+ "  [1] adjacentXP test\n"
					+ "  [2] edgeBetween test\n"
					+ "  [3] External 2-Hop test\n"
					+ "  [4] External 3-Hop test\n"
					+ "  [5] External 4-Hop test\n"
					+ "  [6] Internal 2-Hop test\n"
					+ "  [7] Internal 3-Hop test\n"
					+ "  [8] Internal 4-Hop test");
			return false;
		}

		if (!printHeader(option, graph))
			return false;
		
		params = PMParam[graphPos];

		// Loading the graph and running the test
		if(!this.getGraph(option, graph)) {
			System.out.println("Status  LOADING ERROR");
			testOk = false;
		} else {

			switch (testOpt) {
			case 0:
				adjacentX();
				break;
			case 1:
				adjacentXP();
				break;
			case 2:
				edgeBetween();
				break;
			case 3:
				ExternalHop(2);
				break;
			case 4:
				ExternalHop(3);
				break;
			case 5:
				ExternalHop(4);
				break;
			case 6:
				InternalHop(2);
				break;
			case 7:
				InternalHop(3);
				break;
			case 8:
				InternalHop(4);
				break;
			default: break;
			}

			System.out.println("---------------------");
			printOK();
			this.graphTest.close();
		}

		printBottom ();

		return testOk;
	}
}
