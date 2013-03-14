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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import ve.usb.gdbb.*;

/*
 * Clase abstracta Test
 */
public abstract class Test{
    static protected String[] TestFiles = {
        "testgdbb/graphs/DSJC1000.1.col.sif",
        "testgdbb/graphs/DSJC1000.5.col.sif",
        "testgdbb/graphs/DSJC1000.9.col.sif",
        "testgdbb/graphs/USA-road-d.NY.gr.sif",
        "testgdbb/graphs/USA-road-d.FLA.gr.sif",
        "testgdbb/graphs/SSCA2-17.sif",
        "testgdbb/graphs/R-MAT-1M.sif",
        "testgdbb/graphs/RANDOM-1M.sif"
    };
    public int selectedFile = 0;
    protected Graph graphTest; // Grafo de prueba
    protected Random r; // 
    protected int graphPosition; //
    
    /*
     * Funcion que crea un grafo a partir de una opcion de grafo
     * y el indice del arreglo de nombres de archivos
     * Devuelve true si logro crear el grafo
     */
    protected boolean createGraph(int option, int posGraph){
        if(0 > posGraph || posGraph > TestFiles.length){
            System.err.print("Invalid position of TestFile array\n");
            return false;
        }
        graphPosition = posGraph;
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
     * Funcion que devuelve un arreglo de strings con tantos nodos como amount
     * escogidos de forma aleatoria.
     */
    protected String[] nextRandomNode(int amount){
        int[] randomNodesPos = new int[amount];
        String[] randomNodes = new String[amount];
        ArrayList<String> ItoS = new ArrayList<String>();
        HashMap<String, Integer> StoI = new HashMap<String, Integer>();
        for (int i = 0; i < amount; i++) {
            randomNodesPos[i] = nextRandom(graphTest.V());
        }
        Arrays.sort(randomNodesPos);
        try {
            File file = new File(TestFiles[graphPosition]);
            Scanner scanner = new Scanner(file);
            int pos, cur = 0, readed = 0;
            while (scanner.hasNextLine() && cur < amount) {
                pos = 0;
                String[] line = scanner.nextLine().split("\t");
                for (String i : line) {
                    if (pos == 0) {
                        if (!StoI.containsKey(i)) {
                            StoI.put(i, readed); //OJO
                            ItoS.add(readed, i); //OJO
                            readed++;
                        }
                        pos = 1;
                    } else if (pos == 1) {
                        pos = 2;
                    } else {
                        if (!StoI.containsKey(i)) {
                            StoI.put(i, readed); //OJO
                            ItoS.add(readed, i); //OJO
                            readed++;
                        }
                    }
                }
                while (cur < amount && readed > randomNodesPos[cur]) {
                    randomNodes[cur] = ItoS.get(randomNodesPos[cur]);
                    cur++;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return randomNodes;
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
     * Funcion que devuelve el nombre del archivo actual
     */
    public String getFileName(){
        if(0 <= selectedFile && selectedFile < getFilesLenght()){
            return TestFiles[selectedFile];
        }else{
            return null;
        }
    }
    
    
    /*
     * Funcion que dado un grafo genera un caso de prueba para el grafo
     * Se debe utilizar "r" para generar los nodos o arcos de manera aleatoria
     * pues se usa una semilla estandar para todos.
     */
    protected abstract boolean testGraph();

    /*
     * Funcion que ejecuta el test dada una opcion de grafo
     */
    public boolean test(int option){
        for(int i = 0; i < getFilesLenght(); i++){
            System.out.println("Se esta creando el grafo "+ i+".");
            this.selectedFile = i;
            if(!this.createGraph(option, i)) return false;
            System.out.println("Se empieza el algoritmo.");
            if(!this.testGraph()) return false;
            System.out.println("Termina el algoritmo."+ i+".");
        }
        return true;
    }
}
