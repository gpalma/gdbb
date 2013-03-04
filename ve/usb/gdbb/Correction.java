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
 * Clase correccion, Graph Summarization retorna un conjunto de correcciones
 */
public class Correction{
    /* Constructor de la clase */
    public Correction(){
    }
    /* Constructor de la clase */
    public Correction(String a, String b, boolean bol){
      this.u = a;
      this.v = b;
      this.pos = bol;
    }
    // Representan dos nodos;
     public String u, v;
   	// True si hay que agregarlo false si hay que quitar
   	public boolean pos;
}
