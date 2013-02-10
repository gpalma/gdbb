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

import ve.usb.gdbb.*;
import java.util.Scanner;

public class TestAdjacencies extends Test {
    
    public TestAdjacencies() {
    }
    
    protected boolean testGraph() {
        String[] randomNodes = nextRandomNode(2);
        Adjacencies tests = new Adjacencies();
        boolean areAdj;
        areAdj = tests.adjNodes(graphTest, randomNodes[0], randomNodes[1]);
        System.out.println("Nodes " + randomNodes[0] + " and " + 
                randomNodes[1] + " are " + (areAdj? "": "not ") + "adjacents.");
        randomNodes = nextRandomNode(4);
        areAdj = tests.adjEdges(graphTest, new Edge("", randomNodes[0], randomNodes[1]),
                new Edge("", randomNodes[2], randomNodes[3]));
        System.out.println("Edges (" + randomNodes[0] + ", " + randomNodes[1] + ") and (" + 
                randomNodes[2] + ", " + randomNodes[3] + ") are " + (areAdj? "": "not ") + "adjacents.");
        return true;
    }
    
    public static void main(String[] args) {
        Test testing = new TestAdjacencies();
        Integer opt;
        Scanner console = new Scanner(System.in);
        System.out.println("Specify the graph type for tests:");
        System.out.println("0- DiGraphAdjList");
        opt = console.nextInt();
        testing.test(opt);
    }
}
