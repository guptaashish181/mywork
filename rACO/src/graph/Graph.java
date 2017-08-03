
package graph;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Graph {

    protected Vertex exit;
    protected String label;
    protected boolean directed;
    protected int numberOfVertices;
    protected HashMap<String,Vertex> verticesMap= new HashMap<String,Vertex>();
    protected LinkedList<Vertex> vertices=new LinkedList<Vertex>();
    protected Set<Edge> edges=new HashSet<Edge>();
    protected double [][] adjMatrix;

    public Graph(boolean directed,String label, double [] computationCost, double[][] communicationCost) {

        this.label=label;
        this.adjMatrix = communicationCost;
        this.numberOfVertices=computationCost.length;
        this.directed=directed;

        for(int row=0;row<adjMatrix.length;row++) {
           this.addVertex(label+(row+1),computationCost[row]);
        }

        for(int row=0;row<adjMatrix.length;row++) {

            for(int col=0;col<adjMatrix.length;col++) {

                if(adjMatrix[row][col]!=0) {

                    this.addEdge(label+(row+1), label+(col+1), adjMatrix[row][col]);

                }
            }
        }
   
    }

    public void addVertex(String label, double weight) {
        Vertex vertex=new Vertex(label,weight);
        vertices.add(vertex);
        verticesMap.put(label, vertex);
    }

    public void addEdge(String from,String to, double weight) {

        Vertex source=verticesMap.get(from);
        Vertex destination=verticesMap.get(to);

        Edge edge = new Edge(source,destination,weight);
        edges.add(edge);

        source.addOutgoingEdge(edge);
        destination.addIncomingEdge(edge);
        
        if(!directed) {
            destination.addOutgoingEdge(edge);
            source.addIncomingEdge(edge);
        }

    }


    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }

    public LinkedList<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(LinkedList<Vertex> vertices) {
        this.vertices = vertices;
    }

    public String toString() {
        return "[ Graph :vertices  " +  numberOfVertices  + ", edges: " + getEdges().size() +" ]";
          
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public void setNumberOfVertices(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
    }

    public Vertex getExit() {

        Vertex vt=null;

        if(exit!=null) {
            vt=exit;
        } else {

        for(Vertex v: vertices) {
            if(v.isExit()) {
                exit=v;
                break;
            }
        }

        vt=exit;

        }


        return vt;
    }

    public void printGraph() {

        System.out.println(this);
        for(Vertex v: this.vertices) {

            System.out.println(v);

         //   System.out.println("\tIncoming Communication cost: ");
            for(Edge e: v.getIncomingEdges()) {
         //      System.out.println("\t" + e);
            }

         //   System.out.println("\tOutgoing communication cost: ");
            for(Edge e: v.getOutgoingEdges()) {
        //        System.out.println("\t" + e);
            }

            System.out.println();

        }
    }




 public void printGraph(PrintStream out) {

        out.println(this);
        for(Vertex v: this.vertices) {

            out.println(v);

           // out.println("\tIncoming communication cost: ");
            for(Edge e: v.getIncomingEdges()) {
             //   out.println("\t" + e);
            }

           // out.println("\tOutgoing communication cost: ");
            for(Edge e: v.getOutgoingEdges()) {
           //     out.println("\t" + e);
            }

            out.println();

        }
    }


}
