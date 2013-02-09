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
import java.util.Random;

/*
 * Clase abstracta Test
 */
public abstract class Test{
    protected String[] TestFiles = {
        "graphs/DSJC1000.1.col.sif",
        "graphs/DSJC1000.5.col.sif",
        "graphs/DSJC1000.9.col.sif",
        "graphs/USA-road-d.NY.gr.sif",
        "graphs/USA-road-d.FLA.gr.sif",
        "graphs/SSCA2-17.sif",
        "graphs/R-MAT-1M.sif",
        "graphs/RANDOM-1M.sif"
    };
    protected Graph graphTest; // Grafo de prueba
    protected Random r; // Use random for get nodes or edges

    /*
     * Funcion que crea un grafo a partir de una opcion de grafo
     * y el indice del arreglo de nombres de archivos
     * Devuelve true si logro crear el grafo
     */
    protected boolean createGraph(int option, int posGraph){
        if(0 > posGraph || posgraph > TestFiles.length){
            System.err.print("Invalid position of TestFile array\n");
            return false;
        }
        if(option == 0){
            graphTest = null;
            graphTest = new DiGraphAdjList(TestFiles[posGraph]);
            r = null;
            r = new Random(posGraph);
            return true;
        }else{
            System.err.print("Dont exist a Graph for this option\n");
        }
        return false;
    }

    /*
     * Devuelve el proximo numero pseudoaleatorio dentro del rango [0..n]
     */
    protected int nextRandom(int n){
        return r.nextInt(n);
    }

    /*
     * Funcion que devuelve la cantidad de archivos de prueba
     */
    public int getFilesLenght(){
        return TestFiles.length;
    }

    /*
     * Funcion que dado un grafo genera un caso de prueba para el grafo
     * Se debe utilizar "r" para generar los nodos o arcos de manera aleatoria
     * pues se usa una semilla estandar para todos.
     */
    protected abstract void testGraph();

    /*
     * Funcion que ejecuta el test dada una opcion de grafo
     */
    public boolean test(int option){
        for(int i = 0; i < getFilesLenght(); i++){
            if(!this.createGraph(option, i)) return false;
            this.testGraph();
        }
        return true;
    }
}
