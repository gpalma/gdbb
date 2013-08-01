package experiment.Neo4jCypher.algorithms;
import testgdbb.*;

public class reachability{
	public static void main(String args[]){
		Test t;
		int testNum = Integer.parseInt(args[1]);

		if (testNum == 0) {
			// Test of the external implementation of BFS with Neo4j
			t = new TestBFSexternal();
			t.testAlgorithm(Integer.parseInt( args[0]), 4);
		} else if (testNum == 1) {
			// Test of the internal implementation of BFS with Neo4j
			t = new TestBFSinternal();
			t.testAlgorithm(Integer.parseInt( args[0]), 4);
		} else if (testNum == 2) {
			// Test of the external implementation of DFS with Neo4j
			t = new TestDFSexternal();
			t.testAlgorithm(Integer.parseInt( args[0]), 4);
		} else if (testNum == 3) {
			// Test of the internal implementation of DFS with Neo4j
			t = new TestDFSinternal();
			t.testAlgorithm(Integer.parseInt( args[0]), 4);
		} else {
			System.out.println("The first (and only) argument must be between 0 and 3.");
			System.out.println("\t[0] To test the external implementation of BFS with Neo4j");
			System.out.println("\t[1] To test the internal implementation of BFS with Neo4j");
			System.out.println("\t[2] To test the external implementation of DFS with Neo4j");
			System.out.println("\t[3] To test the internal implementation of DFS with Neo4j");
		}
	}

}
