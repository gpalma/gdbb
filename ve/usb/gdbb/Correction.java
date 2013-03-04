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
