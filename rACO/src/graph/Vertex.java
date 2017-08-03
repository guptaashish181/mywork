

package graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Vertex {

    protected String label;
    protected double weight;
    protected Set<Edge> incomingEdges= new HashSet<Edge>();
    protected Set<Edge> outgoingEdges=new HashSet<Edge>();

    public Vertex() {
    }

    public Vertex(String label, double weight) {
        this.label = label;
        this.weight = weight;
    }
   
    public int getInDegree() {
        return incomingEdges.size();
    }

    public int getOutDegree() {
        return outgoingEdges.size();
    }

    public Set<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    public void setIncomingEdges(Set<Edge> incomingEdges) {
        this.incomingEdges = incomingEdges;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

      public void addIncomingEdge(Edge edge) {
        incomingEdges.add(edge);
    }

    public void addOutgoingEdge(Edge edge) {
        outgoingEdges.add(edge);
    }

    public void removeIncomingEdge(Edge edge) {
        incomingEdges.remove(edge);
    }

    public void removeOutgoingEdge(Edge edge) {
        outgoingEdges.remove(edge);
    }


    public Set<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public LinkedList<Vertex> getSuccessors() {
        LinkedList<Vertex> list = new LinkedList<Vertex>();
        for(Edge e: this.outgoingEdges) {
            list.add(e.getDestination());
        }
        return list;
    }

    public LinkedList<Vertex> getPredecessors() {
        LinkedList<Vertex> list = new LinkedList<Vertex>();
        for(Edge e: this.incomingEdges) {
            list.add(e.getSource());
        }
        return list;
    }
   
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isExit() {
        return (this.outgoingEdges.size()==0);
    }

   public String toString()  {
       return "[" + label + "- CompCost :" + (int)weight + "]";
   }



}
