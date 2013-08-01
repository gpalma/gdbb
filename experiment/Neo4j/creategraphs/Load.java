package experiment.Neo4j.creategraphs;
import testgdbb.*;

public class Load{
	public static void main(String args[]) {
		TestLoad t = new TestLoad();
		t.test(3, Integer.parseInt(args[0]));
	}
}
