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
package ve.usb.gdbb;

/*
 * Correction Class used for the Graph
 * Summarization algorithm: Graph Summ
 * returns a set of corrections
 */

public class Correction{

	// This represents two nodes
	public String u, v;
	/* True if you have to add this,
	 * False if the opposite
	 */
	public boolean pos;

	public Correction(){}

	public Correction(String a, String b, boolean bol){
		this.u = a;
		this.v = b;
		this.pos = bol;
	}
}
