package experiment.Neo4jCypher.creategraphs;
import testgdbb.*;

public class Load{
	public static void main(String args[]) {
		TestLoad t = new TestLoad();
		t.test(4, Integer.parseInt(args[0]));
	}
}
