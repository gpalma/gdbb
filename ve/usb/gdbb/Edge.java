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

public class Edge {

	/* This class represents a relationship/edge
	 * from node 'src' to node 'dst' with 
	 * relationship id/type equal to 'id'.
	 */
	private String id;
	private String src;
	private String dst;

	// Constructor
	public Edge(String id, String src, String dst) {
		this.id = id;
		this.src = src;
		this.dst = dst;
	}

	// 'src' setter
	public void setSrc(String src) {
		this.src = src;
	}

	// 'src' getter
	public String getSrc() {
		return src;
	}

	// 'id' setter
	public void setId(String id) {
		this.id = id;
	}

	// 'id' getter
	public String getId() {
		return id;
	}

	// 'dst' setter
	public void setDst(String dst) {
		this.dst = dst;
	}

	// 'dst' getter
	public String getDst() {
		return dst;
	}

	//Returns the string representation of the edge
	public String toString(){
		return "([" + this.id + "] " + this.src + " " + this.dst+")";
	}

}
