package experiment.HyperGraphDB.algorithms;
import testgdbb.*;

public class patternmatching {
	public static void main(String args[]) {
		TestPatternMatching t = new TestPatternMatching();
		t.testPM(Integer.parseInt(args[0]), 1, Integer.parseInt(args[1]));
	}
}
