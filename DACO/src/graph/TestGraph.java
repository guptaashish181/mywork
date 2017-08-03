
package graph;

public class TestGraph {

    public static void main(String[] args) {

        double [] processorFailureRate= {0.00015,0.00060,0.00045,0.00010,0.00075};
        double [] computationCost= {5,2,4,3,4};
        double [][] communicationCost= {

            {0,2,3,0,0},
            {2,0,0,2,4},
            {3,0,0,1,0},
            {0,2,1,0,3},
            {0,4,0,3,0}
        };

        Graph processorGraph= new Graph(true,"P",computationCost,communicationCost);
        processorGraph.printGraph();


        double [] taskComputationCost= {30,30,40,20,10,20,60,10,20,20,30};
        double [][] taskCommunicationCost= {

            {0,4,5,10,20,0,0,0,0,0,0},
            {0,0,0,0,0,10,0,0,0,0,0},
            {0,0,0,0,0,4,0,0,10,0,0},
            {0,0,0,0,0,0,15,10,0,4,0},
            {0,0,0,0,0,0,0,0,0,30,0},
            {0,0,0,0,0,0,0,0,20,0,0},
            {0,0,0,0,0,0,0,0,0,0,10},
            {0,0,0,0,0,0,0,0,0,0,9},
            {0,0,0,0,0,0,0,0,0,0,30},
            {0,0,0,0,0,0,0,0,0,0,10},
            {0,0,0,0,0,0,0,0,0,0,0}
        };

        Graph taskGraph= new Graph(true,"V",taskComputationCost,taskCommunicationCost);
        taskGraph.printGraph();


        TaskOrderGenerator tog=new TaskOrderGenerator(taskGraph, processorGraph, processorFailureRate);
        tog.generate();

        int[] taskOrderList = tog.getTaskOrder();
        for (int t : taskOrderList) {
            System.out.print("V" + (t+1) + " ");
        }

    }

}
