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
import java.util.ArrayList;
import ve.usb.gdbb.*;

/*
 * Clase que permite probar el algoritmo ShortPath
 */
public class TestShortPath extends Test {

	static protected int numTest = 10;

	public TestShortPath() {
		TestName = "Shortest Path";
	}

	protected boolean testGraph(){
		String[] nodes;
		ShortPath s = new ShortPath();
		nodes = this.nextRandomNode(numTest * 2);
		if(nodes == null) return false;
		for(int i = 0; i < numTest ; i++){
			s.getDistance(this.graphTest, nodes[i*2], nodes[i*2+1]);
		}
		return true;
	}
}
