package experiment.HyperGraphDB.algorithms;
import testgdbb.*;

public class densestgraph{
  public static void main(String args[]){
    Test t = new TestDensestSubgraph();
    t.testAlgorithm(Integer.parseInt( args[0]), 1);
  }

}
