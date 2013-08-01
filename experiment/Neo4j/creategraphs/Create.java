package experiment.Neo4j.creategraphs;
import testgdbb.*;

public class Create{
	public static void main(String args[]){
		TestCreate t = new TestCreate();
		t.test(3, Integer.parseInt(args[0]));
	}
}
