package experiment.Neo4jCypher.creategraphs;
import testgdbb.*;

public class Create{
	public static void main(String args[]){
		TestCreate t = new TestCreate();
		t.test(4, Integer.parseInt(args[0]));
	}
}
