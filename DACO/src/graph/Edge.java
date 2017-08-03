
package graph;


public class Edge {

    protected Vertex source;
    protected Vertex destination;
    protected double weight;

    public Edge() {
    }

    public Edge(Vertex source, Vertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

   public String toString()  {
       return "[" +  source.getLabel() + "-" + destination.getLabel()  + "-" + (int)weight + "]";
   }

   
}
