package experiment.Neo4jCypher.algorithms;
import testgdbb.*;

public class shortpath{
	public static void main(String args[]){
		Test t = new TestShortPath();
		t.testAlgorithm(Integer.parseInt( args[0]), 4);
	}

}
