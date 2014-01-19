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

public class TestLoad extends Test{

	public TestLoad() {
		TestName = "Graph Loading";
	}

	protected boolean testGraph(){
		return false;
	}
	/*
	 * Given an option create a graph
	 */
	public boolean test(int option, int posgraph) {

		boolean testOk = true;
		if (!printHeader(option, posgraph))
			return false;

		if(!this.getGraph(option, posgraph)) {
			System.out.println("Status  LOADING ERROR");
			testOk = false;
		} else if (graphTest.V() != GraphData[posgraph][0]
					|| graphTest.E() != GraphData[posgraph][1]) {
			printFailed();
			System.out.println("---------------------");
			System.out.println(graphTest.V()+" nodes loaded\nfrom "+GraphData[posgraph][0]);
			System.out.println(graphTest.E()+" edges loaded\nfrom "+GraphData[posgraph][1]);
			postTest();
			testOk = false;
		} else {
			printOK();
			postTest();
		}

		printBottom ();

		return testOk;
	}


}
