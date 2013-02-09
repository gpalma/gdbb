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
import java.util.Iterator;

public class kneighborhood {
    /*
     * Constructor de la clase
     */
    public kneighborhood(){
    }

    /*
     * Funcion que devuelve los k vecinos de un nodo v1.
     */
    public ArrayList<String> getNeighborhood(Graph g, String v1) {
        ArrayList<String> res = new ArrayList<String>();
        Iterator<String> siguientes = g.adj(v1);
        while(siguientes.hasNext()){
            res.add(siguientes.next());
        }
        return res;
    }

}
