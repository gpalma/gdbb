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
import java.lang.Math;

/*
 * Test class for reachability implementation with DFS and BFS.
 */
public class TestReachability extends Test {

  	String[] newV;
	BFS bfsTest;
	DFS dfsTest;

	protected boolean checkBFS(int b, int e) {
		if (Math.abs(b-e)<2)
			return true;
		int middle = (b+e)/2;
		boolean totalReach = bfsTest.existsPath(newV[b], newV[e]),
				part1Reach = bfsTest.existsPath(newV[b], newV[middle]),
				part2Reach = bfsTest.existsPath(newV[middle], newV[e]);
		if (totalReach != part1Reach && part2Reach)
			return false;
		return checkBFS(b,middle) && checkBFS(middle,e);
	}

	protected boolean checkDFS(int b, int e) {
		if (Math.abs(b-e)<2)
			return true;
		int middle = (b+e)/2;
		boolean totalReach = dfsTest.existsPath(newV[b], newV[e]),
				part1Reach = dfsTest.existsPath(newV[b], newV[middle]),
				part2Reach = dfsTest.existsPath(newV[middle], newV[e]);
		if (totalReach != part1Reach && part2Reach)
			return false;
		return checkDFS(b,middle) && checkDFS(middle,e);
	}

	protected boolean testGraph() {
		int N = (int)Math.ceil(3.0 * (double)this.graphTest.V() / 4.0);
		newV = this.nextRandomNode(N);
		bfsTest = new BFS(this.graphTest);
		dfsTest = new DFS(this.graphTest);

		return this.checkBFS(0,N-1) && this.checkDFS(0,N-1);
	}
}
