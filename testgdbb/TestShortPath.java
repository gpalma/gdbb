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

public class TestShortPath extends Test{
    static protected int numPruebas = 10;
    protected boolean testGraph(){
        String[] nodos;
        ShortPath s = new ShortPath();
        nodos = this.nextRandomNode(numPruebas*2);
        if(nodos == null) return false;
        for(int i = 0; i < numPruebas; i++){
            s.getDistance(this.graphTest, nodos[i*2], nodos[i*2+1]);
        }
        return true;
    }
    
    
    public static void main(String [ ] args){
        Test t;
        t = new TestShortPath();
        t.test(0);
    }
}
