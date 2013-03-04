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
import java.util.ArrayList;
import java.util.Scanner;

public class TestDensestSubgraph extends Test {
    
    public TestDensestSubgraph() {
    }
       
    protected void testGraph() {
        DensestSubgraph d = new DensestSubgraph();
        d.DenseSubgraph(graphTest);
    }
    
    public static void main(String[] args) {
        Test testing = new TestDensestSubgraph();
        Integer opt;
        Scanner console = new Scanner(System.in);
        System.out.println("Testing Dense Graph");
        System.out.println("Specify the graph type for tests:");
        System.out.println("0- DiGraphAdjList");
        opt = console.nextInt();
        testing.test(opt);
    }
}
