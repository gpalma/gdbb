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
public class TestDFSexternal extends Test {

  	String[] newV;
  	int N;
	DFS dfsTest;

	public TestDFSexternal() {
		TestName = "External DFS";
	}

	protected boolean checkDFS() {
		boolean res = true;
		for (int i=0 ; i<N-2 && res ; i++) {
			if (dfsTest.existsPath(newV[i], newV[i+1])
				&& dfsTest.existsPath(newV[i+1], newV[i+2]))
				res = dfsTest.existsPath(newV[i], newV[i+2]);
		}
		return res;
	}

	protected boolean testGraph() {
		if (this.graphTest.V() < 10) {
			System.out.println("The size of the graph must be greater.");
			return false;
		}

		N = 10;
		newV = this.nextRandomNode(N);
		dfsTest = new DFS(this.graphTest);

		return this.checkDFS();
	}
}
