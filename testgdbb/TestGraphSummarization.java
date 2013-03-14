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
import java.util.Queue;
import ve.usb.gdbb.*;

public class TestGraphSummarization extends TestNP{
    protected boolean testGraph(){
        String[] nodos;
        GraphSummarization s;
        DiGraphAdjList g;
        nodos = this.nextRandomNode(N);
        if(nodos == null) return false;
        for(int i = 0; i < N; i++){
            g = getGraph(nodos[i]);
            System.out.println("\tPrueba "+ i);
            s = new GraphSummarization();
            System.out.println("\tComienzo Graphsumm "+ g.V());
            s.Summarize(g);
            System.out.println("\tFin graphsumm ");
            g = (DiGraphAdjList)s.returnGSummarize();
            g.print(this.getFileName()+i+".out");
        }
        return true;
    }
    public static void main(String [] args){
        Test t;
        t = new TestGraphSummarization();
        t.test(0);
    }
}
