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

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;

public class ShortPath {

	TreeSet<String> resultado;

	/*
	 * Constructor de la clase
	 */
	public ShortPath() {}

	/*
	 * Funcion que devuelve el camino mas corto desde v1 hasta v2 en 
	 * un arraylist que muestra los nodos intermedios partiendo desde v1 
	 * hasta v2.
	 * Retorna NULL en caso de no encontrar camino.
	 */
	public ArrayList<String> getPath(Graph g, String v1, String v2) {
		ArrayList<String> res = new ArrayList<String>();
		System.out.println(g.V());
		for(int i = 0; i < g.V(); i++){
			if(DFScamino(g, 0, i, v1, v2, res)){
				return res;
			}
		}
		return null;
	}
	/*
	 * Funcion que realiza una busqueda con DFS usando una cota.
	 */
	private boolean DFScamino(Graph g, int i, int cota, String actual, 
			String objetivo, ArrayList<String> res)
	{
		if (actual.equals(objetivo)) {
			res.add(i, actual);
			return true;
		}
		if (i >= cota) return false;
		GraphIterator<String> siguientes = g.adj(actual);
		while (siguientes.hasNext()) {
			if (DFScamino(g, i+1, cota, siguientes.next(), objetivo, res)) {
				res.add(i, actual);
				siguientes.close();
				return true;
			}
		}
		siguientes.close();
		return false;
	}


	/*
	 * Funcion que devuelve la distancia mas corta entre dos nodos v1 y v2.
	 * Si no encuentra camino retorna -1.
	 */
	public int getDistance(Graph g, String v1, String v2) {
		for (int i = 0; i < g.V(); i++) {
			if (DFS(g, 0, i, v1, v2)) return i;
		}
		return -1;
	}
	/*
	 * Funcion que realiza una busqueda con DFS usando una cota.
	 */
	private boolean DFS(Graph g, int i, int cota, String actual, String objetivo){
		if (actual.equals(objetivo)) {
			return true;
		}
		if (i >= cota) return false;
		GraphIterator<String> siguientes = g.adj(actual);
		while (siguientes.hasNext()) {
			if (DFS(g, i+1, cota, siguientes.next(), objetivo)) {
				siguientes.close();
				return true;
			}
		}
		siguientes.close();
		return false;
	}

	/*
	 * Return all nodes khops from src
	 */
	public Iterator<String> khops(Graph g, String src, int k){
		resultado = new TreeSet<String>();
		DFS(g, 0, k, src);
		return resultado.iterator();
	}

	/*
	 * Funcion que realiza una busqueda con DFS usando una cota.
	 */
	private boolean DFS(Graph g, int i, int cota, String actual){
		if(i >= cota){
			resultado.add(actual);
			return true;
		}
		GraphIterator<String> siguientes = g.adj(actual);
		while(siguientes.hasNext()){
			DFS(g, i+1, cota, siguientes.next());
		}
		siguientes.close();
		return true;
	}
}
