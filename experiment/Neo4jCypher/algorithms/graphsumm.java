package experiment.Neo4jCypher.algorithms;
import testgdbb.*;

public class graphsumm{
	public static void main(String args[]){
		Test t = new TestGraphSummarization();
		t.testAlgorithm(Integer.parseInt( args[0]), 4);
	}
}
