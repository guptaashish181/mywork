

package graph;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;


public class TaskOrderGenerator {

    private Graph taskGraph;
    private Graph processorGraph;

    private int numberOfTasks;
    private int numberOfProcessors;

    private LinkedHashMap<String,Double> rankMap=new LinkedHashMap<String,Double>();
    private double [] processorFailureRate;

    Scanner sc =new Scanner(System.in);

    public TaskOrderGenerator() {

    }

    public TaskOrderGenerator(Graph taskGraph, Graph processorGraph,double [] processorFailureRate) {
        this.taskGraph = taskGraph;
        this.processorGraph = processorGraph;
        this.numberOfProcessors=processorGraph.getNumberOfVertices();
        this.numberOfTasks= taskGraph.getNumberOfVertices();
        this.processorFailureRate=processorFailureRate;
   
    }

    /*
    private void assignRank(Vertex vertex) {

        System.out.println("1. Vertex: " + vertex.getLabel());

        if(vertex!=null) {
        
        if(vertex.isExit()) {
            double rank=calculateRank(vertex);
            rankMap.put(vertex.getLabel(), rank);
        }


            for(Vertex v: vertex.getPredecessors()) {

                System.out.println("1. Pre: " + v.getLabel());
                if(rankMap.get(v.getLabel())==null) {
                 double rank=calculateRank(v);
                 rankMap.put(v.getLabel(), rank);
                    System.out.println("1. " + v.getLabel() + " " + rank);
                }
                
             }
        

             for(Vertex vv: vertex.getPredecessors()) {

                  System.out.println("2. Vertex: " + vv.getLabel());
                    //assignRank(v);

                    for(Vertex v: vv.getPredecessors()) {

                System.out.println("2. Pre: " + v.getLabel());
                if(!rankMap.containsKey(v.getLabel())) {
                    boolean r=true;
                    for(Vertex vvv:v.getSuccessors()) {
                           if( !rankMap.containsKey(vvv.getLabel()) ) {
                               r=false;
                               break;
                           }

                    }
                    if(r) {
                 double rank=calculateRank(v);
                 rankMap.put(v.getLabel(), rank);
                 System.out.println("2. " + v.getLabel() + " " + rank);

                    }
                }

             }
        }
        }


    }*/

    private void assignRank(Vertex vertex) {

        //System.out.println("1. Vertex: " + vertex.getLabel());

        if(vertex!=null) {


            if(!rankMap.containsKey(vertex.getLabel())) {
                    boolean r=true;
                    for(Vertex vvv:vertex.getSuccessors()) {
                           if( !rankMap.containsKey(vvv.getLabel()) ) {
                               r=false;
                               break;
                           }

                    }
                    if(r) {
                 double rank=calculateRank(vertex);
                 rankMap.put(vertex.getLabel(), rank);
                 //System.out.println("2. " + vertex.getLabel() + " " + rank);

                    }
                }


            for(Vertex v: vertex.getPredecessors()) {

                //System.out.println("1. Pre: " + v.getLabel());
                assignRank(v);

             }

              

        }


    }


    private double calculateRank(Vertex v) {

        double rank=0;
        double temp=0;
        double temprc=0;
        double commCost=0;
        for(int index=0;index<this.numberOfProcessors;index++) {
            
            Vertex p= processorGraph.getVertices().get(index);
            temp+=(v.getWeight()/p.getWeight());
            temprc+=(processorFailureRate[index]/p.getWeight());

            for(Edge e: p.getOutgoingEdges()) {
                commCost+=e.getWeight();
            }
            
        }

        double averageTaskComputationCost=(temp/this.numberOfProcessors);
        double rc=((1-Math.exp(-v.getWeight()*temprc))*averageTaskComputationCost);
        double averageProcessorCommCost=(commCost/(this.numberOfProcessors*2));


        if(v.isExit()) {
            rank= averageTaskComputationCost+rc;

        } else {

           //System.out.println("Next: ");
            //printRankMap();
            
            double max=0;
            for(Edge e: v.getOutgoingEdges()) {
                //System.out.println("Dest Label: " + e.getDestination().getLabel());
                double srank=rankMap.get(e.getDestination().getLabel());
                double t=(e.getWeight()/averageProcessorCommCost)+srank;
                if(t>max) {
                    max=t;
                }
            }

            rank= averageTaskComputationCost+ max +rc;
            }
        


        return rank;
    }


    public void generate() {

        Vertex exit=taskGraph.getExit();
        assignRank(exit);

        printRankMap();

    }

    public int [] getTaskOrder() {

       int [] to = new int[this.numberOfTasks];
       double [] r = new double[this.numberOfTasks];
        
       int index=0;
       for(String s: rankMap.keySet()) {
           
           r[index]=rankMap.get(s);
           index++;
       }


       Arrays.sort(r);

       int k=to.length-1;
       for(double t: r) {
           to[k]=getTaskIndex(t,r);
           k--;
       }

       return to;
    }

    private int getTaskIndex(double v,double [] r) {


       String task=null;
       for(String s: rankMap.keySet()) {

           if(rankMap.get(s)==v) {
               task=s;
               break;
           }
       }

       int i=Integer.parseInt(task.substring(1))-1;

       return i;



    }

    public void printRankMap() {
        for(String s: rankMap.keySet()) {
            System.out.println(s + " - " + rankMap.get(s));
        }
    }

}
