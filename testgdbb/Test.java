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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import ve.usb.gdbb.*;

public abstract class Test {

	// Test Graph
	protected Graph graphTest;

	// Path where the graphs files are located
	private String path = "";//"../graphs/files/";
	static protected String[]
		TestFiles = {
			"/experiment1/user4/benchmark/graphs/files/miniprueba",
			"/experiment1/user4/benchmark/graphs/files/DSJC1000.1.col",
			"/experiment1/user4/benchmark/graphs/files/DSJC1000.5.col",
			"/experiment1/user4/benchmark/graphs/files/DSJC1000.9.col",
			"/experiment1/user4/benchmark/graphs/files/USA-road-d.FLA.gr",
			"/experiment1/user4/benchmark/graphs/files/USA-road-d.NY.gr",
			"/experiment1/user4/benchmark/graphs/files/SSCA2-17",
			"/experiment1/user4/benchmark/graphs/files/RANDOM-1M",
			"/experiment1/user4/benchmark/graphs/files/R-MAT-1M",
			"/experiment1/user4/benchmark/graphs/files/Random-0.3-1",
			"/experiment1/user4/benchmark/graphs/files/Random-0.3-2",
			"/experiment1/user4/benchmark/graphs/files/Random-0.3-3",
			"/experiment1/user4/benchmark/graphs/files/fixed-number-arcs-0.1",
			"/experiment1/user4/benchmark/graphs/files/fixed-number-arcs-0.5",
			"/experiment1/user4/benchmark/graphs/files/fixed-number-arcs-0.9",
			"/experiment1/user4/benchmark/graphs/files/Berlin10M",
			"/experiment1/user4/benchmark/graphs/files/Berlin5M",
			"/experiment2/user4/BerlinGenerator/Berlin50M"
		},
		GraphNames = {
			"miniprueba",
			"DSJC1000.1",
			"DSJC1000.5",
			"DSJC1000.9",
			"USA-road-d.FLA",
			"USA-road-d.NY",
			"SSCA2-17",
			"RANDOM-1M",
			"R-MAT-1M",
			"Random-0.3-1",
			"Random-0.3-5",
			"Random-0.3-9",
			"fixed-number-arcs-0.1",
			"fixed-number-arcs-0.5",
			"fixed-number-arcs-0.9",
			"Berlin10M",
			"Berlin5M",
			"Berlin50M"
		},
		GDBM = {
			"DiGraphAdjList",
			"HyperGraphDB",
			"DexDB",
			"Neo4j",
			"Neo4j (using Cypher)"
		},
		format = {
			"sif",
			"nt"
		};
	static protected String[][]
		PMParam = {
			{"2", "a", "3"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"6", "pr", "8"},
			{"<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor117/Offer231837>",
				"bsbm:vendor",
				"<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor117/Vendor117>"},
			{"<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor117/Offer231837>",
				"bsbm:vendor",
				"<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor117/Vendor117>"},
			{"<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor117/Offer231837>",
				"bsbm:vendor",
				"<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor117/Vendor117>"}
		};
	static protected String TestName = "Unnamed";
	// Characteristics of each graph {#nodes, #edges}
	static protected int[][]
		GraphData = {
			{5,			6},
			{1000,		99258},
			{1000,		499652},
			{1000,		898898},
			{1070376,	2687902},
			{264346,	730100},
			{131065,	3907909},
			{1000000,	9999952},
			{524049,	9994044},
			{2048,		1257678},
			{4096,		5031936},
			{8192,		20130200},
			{10000,		10000000},
			{4472,		10000000},
			{3333,		10000000},
			{2743234,	10010277},
			{1408609,	5010066},
			{1,	1}
		};
	public int selectedFile = 0;
	// Random number generator
	protected Random r;
	protected int graphPosition;
	long testTime, testMemo;

	/* Return the direction of the graph file
	 * that's currently being tested, in the
	 * specified format
	 */
	String getCurrentFile(int f) {
		return path+TestFiles[graphPosition]+"."+format[f];
	}

	/* An auxiliary function to be used in some
	 * tests that needs a special treatment
	 */
	protected void setgraph(Graph g){
		graphTest = g;
	}


	/* Creates the graph given a graph index and
	 * an index for the GDBM in which it will
	 * be created
	 */
	protected boolean createGraph(int option, int posGraph){
		if(0 > posGraph || posGraph > TestFiles.length){
			System.err.print("Invalid position of TestFile array\n");
			return false;
		}
		graphPosition = posGraph;
		String fileDir = getCurrentFile(0);
		switch ( option ) {
		case 0:
			graphTest = new DiGraphAdjList();
			break;
		case 1:
			graphTest = new HyperGraphDB(fileDir, posGraph);
			break;
		case 2:
			graphTest = new DexDB(fileDir, posGraph);
			break;
		case 3:
			graphTest = new Neo4j(fileDir, posGraph);
			break;
		case 4:
			graphTest = new Neo4jCypher(fileDir, posGraph);
			break;
		default:
			System.err.print("Dont exist a Graph for this option\n");
			return false;
		}
		r = new Random(posGraph);
		return true;
	}

	/*
	 * Funcion que carga un grafo en memoria
	 * Devuelve true si logro crear el grafo
	 */
	protected boolean getGraph(int option, int posGraph){
		if(0 > posGraph || posGraph > TestFiles.length){
			System.err.print("Invalid position of TestFile array\n");
			return false;
		}
		graphPosition = posGraph;
		String fileDir = getCurrentFile(0);
		switch ( option ) {
		case 0:
			setgraph(new DiGraphAdjList(fileDir));
			break;
		case 1:
			setgraph(new HyperGraphDB(posGraph));
			break;
		case 2:
			setgraph(new DexDB(posGraph));
			break;
		case 3:
			setgraph(new Neo4j(fileDir, posGraph));
			break;
		case 4:
			setgraph(new Neo4jCypher(fileDir, posGraph));
			break;
		default:
			System.err.print("Dont exist a Graph for this option\n");
			return false;
		}
		r = new Random(posGraph);
		return true;
	}

	/*
	 * Returns an array with 'amount' random nodes from the graph
	 */
	protected String[] nextRandomNode(int amount){
		int[] randomNodesPos = new int[amount];
		String[] randomNodes = new String[amount];
		ArrayList<String> ItoS = new ArrayList<String>();
		HashMap<String, Integer> StoI = new HashMap<String, Integer>();
		for (int i = 0; i < amount; i++) {
			randomNodesPos[i] = nextRandom(graphTest.V());
		}
		Arrays.sort(randomNodesPos);
		try {
			File file = new File(getCurrentFile(0));
			Scanner scanner = new Scanner(file);
			int pos, cur = 0, readed = 0;
			while (scanner.hasNextLine() && cur < amount) {
				pos = 0;
				String[] line = scanner.nextLine().split("\t");
				for (String i : line) {
					if (pos == 0) {
						if (!StoI.containsKey(i)) {
							StoI.put(i, readed);
							ItoS.add(readed, i);
							readed++;
						}
						pos = 1;
					} else if (pos == 1) {
						pos = 2;
					} else {
						if (!StoI.containsKey(i)) {
							StoI.put(i, readed);
							ItoS.add(readed, i);
							readed++;
						}
					}
				}
				while (cur < amount && readed > randomNodesPos[cur]) {
					randomNodes[cur] = ItoS.get(randomNodesPos[cur]);
					cur++;
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return randomNodes;
	}

	/* Void method that it's used to execute some final
	 * actions to be able to finalize the tests
	 */
	protected void postTest(){
		this.graphTest.close();
	}

	/* Returns a pseudorandom number in the range [0..n]
	 */
	protected int nextRandom(int n) {
		return r.nextInt(n);
	}

	/* Returns the name of the current file
	 */
	public String getFileName() {
		if(0 <= selectedFile && selectedFile < TestFiles.length)
			return TestFiles[selectedFile];
		else
			return null;
	}

	/* Returns the amount of memory used (Not free)
	 */
	long getMemory() {
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		return runtime.totalMemory() - runtime.freeMemory();
	}

	/* The objective of this function is to be implemented by
	 * test extensions to prove some characteristic of the graph
	 */
	protected abstract boolean testGraph();

	/* Prints the header of the test 
	 * (Name & Characteristics of the graph selected)
	 */
	public boolean printHeader (int option, int k) {

		// Checking the internal configuration of the benchmark
		int GLen = TestFiles.length;
		if (GLen != GraphNames.length ||
			GLen != GraphData.length  ||
			GLen != PMParam.length) {
			System.out.println(
				  "There's something wrong with the internal\n"
				+ "configuration of the benchmark.\n"
				+ "Check if the lengths of \'TestFiles\', \'GraphNames\'\n"
				+ "and \'GraphData\' arrays are the same."
			);
			return false;
		}

		// Check correctness of 'option'
		if (0 > option || option >= GDBM.length) {
			System.out.println("The GDBM parameter is not valid. Use instead:");
			for (int i=0 ; i<GDBM.length ; i++)
				System.out.println("  ["+i+"] "+GDBM[i]);
			return false;
		}

		// Check correctness of 'k'
		if (0 > k || k >= GLen) {
			System.out.println("The graph parameter is not valid. Use instead:");
			for (int i=0 ; i<GLen ; i++)
				System.out.println("  ["+i+"] "+GraphNames[i]);
			return false;
		}

		// Showing some test data, before it begins
		System.out.println(TestName + " Test\n" + GDBM[option]);
		System.out.println("---------------------");
		System.out.format("Graph%16s\n", GraphNames[k]);
		System.out.format("Nodes%16d\n", GraphData[k][0]);
		System.out.format("Edges%16d\n", GraphData[k][1]);
		System.out.println("---------------------");

		// Inic time and memory counters
		testMemo = getMemory()/1024;
		testTime = System.currentTimeMillis();

		return true;
	}

	// Prints a FAILED status
	public void printFailed () {
		System.out.println("Status         FAILED");
	}

	// Prints a OK status
	public void printOK () {
		System.out.println("Status             OK");
	}

	// Prints the time and memory used during the test
	public void printBottom () {
		testMemo = getMemory()/1024 - testMemo;
		testTime = System.currentTimeMillis() - testTime;

		System.out.println("---------------------");
		System.out.format("Time(ms)%13d\n", testTime);
		System.out.format("Memory(kb)%11d\n", testMemo);
	}

	/* Test Algorithm (The graph must be in disk)
	 * Just run the algorithm
	 */
	public boolean testAlgorithm(int k, int option){
		boolean testOk = true;
		if (!printHeader(option, k))
			return false;

		// Loading the graph and running the test
		if(!this.getGraph(option, k)) {
			System.out.println("Status  LOADING ERROR");
			testOk = false;
		} else if(!this.testGraph()) {
			printFailed();
			this.postTest();
			testOk = false;
		} else {
			printOK();
			printBottom ();
			this.postTest();
		}
		return testOk;
	}

	// Test Algorithm (The graph must be in disk)
	public boolean testAlgorithm(int option){
		for(int k = 0 ; k < TestFiles.length ; k++){
			if(!testAlgorithm(k, option)) return false;
		}
		return true;
	}
}
