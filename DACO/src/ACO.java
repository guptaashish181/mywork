
import graph.Graph;
import graph.TaskOrderGenerator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class ACO {

    public static Matrix getExecutionTimeMatrix(double [] taskCompCost,double [] processorCompCost) {
        
                		
		Matrix mat  = new Matrix(taskCompCost.length,processorCompCost.length);
		
                
                for(int r=0;r<taskCompCost.length;r++) {
                    for(int c=0;c<processorCompCost.length;c++) {
                        
                        mat.setValue(r, c, (taskCompCost[r]/processorCompCost[c]));
                        
                    }
                }
                
                return mat;
        
    }
    
    public static void main(String[] args) {

        double[] processorFailureRate = {0.00015, 0.00060, 0.00045, 0.00010, 0.00075};
        double[] computationCost = {5, 2, 4, 3, 4};
        double[][] communicationCost = {
            {0, 2, 3, 0, 0},
            {2, 0, 0, 2, 4},
            {3, 0, 0, 1, 0},
            {0, 2, 1, 0, 3},
            {0, 4, 0, 3, 0}
        };

        Graph processorGraph = new Graph(true, "P", computationCost, communicationCost);
        processorGraph.printGraph();

        /*
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter no of tasks: ");
        int n=sc.nextInt();
        
        System.out.println("Enter avg. computation cost of tasks: ");
        int avg=sc.nextInt();
        
        System.out.println("Enter avg. computation cost margin of tasks: ");
        int margin=sc.nextInt();
        
        System.out.println("Enter CCR: ");
        double ccr=sc.nextDouble();
        
        RandomTaskGraph rtg=new RandomTaskGraph(n,avg,margin,ccr); // nooftasks,avgcompcost,margin,ccr
            */   
        
        RandomTaskGraph rtg=new RandomTaskGraph(5,20,5,1.0); // nooftasks,avgcompcost,margin,ccr
        

        double [] taskComputationCost= rtg.getTaskComputationCost();
        double[][] taskCommunicationCost =rtg.getTaskCommunicationCost();


        int noOfTasks = taskCommunicationCost[0].length;
        int noOfProcessors = computationCost.length;


        Matrix etm = getExecutionTimeMatrix(taskComputationCost,computationCost);

        etm.printMatrix("ETM", true);


        Graph taskGraph = new Graph(true, "V", taskComputationCost, taskCommunicationCost);
        taskGraph.printGraph();

        // generate task order.
        TaskOrderGenerator tog = new TaskOrderGenerator(taskGraph, processorGraph, processorFailureRate);
        tog.generate();
        int[] taskOrderList = tog.getTaskOrder();
        System.out.println("\nTask order: \n");
        for (int t : taskOrderList) {
            System.out.print("V" + (t+1) + " ");
        }
//------------------

        // input
        int noOfMachines = noOfProcessors; //////////

        int noOfAnts = 5;
        int noOfIterations = 5;

        double evaporationRate = 0.8;
        double delta = 0;


        double w = 0.2;
        double alpha = 1, beta = 5;

        //local

        ArrayList<Solution> localSolutions = new ArrayList<Solution>();


        //global
        ArrayList<Solution> globalSolutions = new ArrayList<Solution>();

        // generated input data
        //Matrix executionTimeMatrix= new Matrix(noOfTasks,noOfMachines);
        //Matrix executionTimeMatrix= Matrix.getExecutionTimeMatrix();
        Matrix executionTimeMatrix = MatrixHelper.getExecutionTimeMatrix(noOfTasks, noOfMachines);

        executionTimeMatrix.printMatrix("Execution Time: ", true);

        //Matrix costMatrix= new Matrix(noOfTasks,noOfMachines);
        //Matrix costMatrix= Matrix.getCostMatrix();
        //Matrix costMatrix = MatrixHelper.getCostMatrix(executionTimeMatrix, noOfTasks, noOfMachines);

        //costMatrix.printMatrix("Cost Matrix: ", true);


        Matrix pheromoneMatrix = new Matrix(noOfTasks, noOfMachines, 0.5);
        //Matrix pheromoneMatrix=Matrix.getPheromoneMatrix();
        //pheromoneMatrix.printMatrix("Pheromone Matrix: ", false);

        //Matrix freeTimeMatrix= new Matrix(noOfMachines,noOfAnts,1);
        Matrix freeTimeMatrix;//=Matrix.getFreeTimeMatrix();
     /*   Matrix reliability=new Matrix(noOfMachines, noOfAnts, 0);
        for(int k=0;k<noOfMachines;k++) {
            for(int c=0;c<noOfAnts;c++) {
            reliability.setValue(k,c , processorFailureRate[k]);
        }
        }
        reliability.printMatrix("------------rel: " , true);
       */

        int freeTime, cost;
        double pheromone, neta, sum;
        double[] temp;

        /// assign initial solution to all ant

        

        for (int iter = 1; iter <= noOfIterations; iter++) {

            //freeTimeMatrix=Matrix.getFreeTimeMatrix(); /////////////////////
            freeTimeMatrix = new Matrix(noOfMachines, noOfAnts, 1);

            //freeTimeMatrix.printMatrix("Free Time: " , true);

            // loop for ant
            for (int ant = 0; ant < noOfAnts; ant++) {



                Solution s = new Solution();

                // Task Order Generation
                //taskOrderList= TaskOrderGenerator.orderList(noOfTasks);
                //taskOrderList= TaskOrderGenerator.specifiedOrderList(ant);
                //System.out.print("\nTask Order For Ant " + (ant+1) + ": ");


                int[] taskMachine = new int[noOfTasks];

                // Neta Calc
                neta=0;
                for (int task : taskOrderList) {
                    sum = 0;
                    temp = new double[noOfMachines];
                    for (int machine = 0; machine < noOfMachines; machine++) {

                        //System.out.print("\nAnt: " + (ant+1) + " Task: T" + (task+1) + " Machine: M" + (machine+1) + ": ");
                          //------reliability
                        freeTime = (int) freeTimeMatrix.getValue(machine, ant);
                        //double rl =  reliability.getValue(machine, ant);
                        //System.out.print(" Free Time: " + freeTime);

                       
                       

                        pheromone = pheromoneMatrix.getValue(task, machine);
                        //System.out.print(" Pheromone: " + pheromone);

                        neta = (1.0 / freeTime); // + ( 1.0 / rl); // updated
                        //System.out.print(" Neta: " + neta);

                        sum = sum + (Math.pow(neta, beta) * Math.pow(pheromone, alpha)); // updated

                        temp[machine] = (Math.pow(pheromone, alpha) * Math.pow(neta, beta)); //



                    }

                    //System.out.println("\nProbability calc: ");

                    double maxProbability = 0;
                    int selMachine = -1;
                    for (int machine = 0; machine < noOfMachines; machine++) {
                        //System.out.println("\nMachine: M" + (machine+1) + " " );
                        //System.out.print(" temp: " + temp[machine]);
                        //System.out.print(" sum: " + sum);
                        double prob = (temp[machine] / sum);
                        //System.out.print(" Prob: " + prob);
                        ////
                        if (prob > maxProbability) {
                            maxProbability = prob;
                            selMachine = machine;
                        }

                    }

                    //System.out.println("\nAnt " + (ant+1) + " Task: T" + (task+1) + " select machine M" + (selMachine+1));


                    // free time matrix update
                    //System.out.println("Task: " + task + " selMachine: " + selMachine);
                    int et = (int) executionTimeMatrix.getValue(task, selMachine);
                    //System.out.println("\nTask: T" + (task+1) + " Sel Machine: M" + (selMachine+1) +  " Exe. Time: " + et);
                    int ft = (int) freeTimeMatrix.getValue(selMachine, ant);
                    freeTimeMatrix.setValue(selMachine, ant, (ft + et));
//----selection of machine wrt free tme and reliability
//                    reliability.setValue(selMachine, ant, processorFailureRate[selMachine]*taskComputationCost[ant]);
                    //freeTimeMatrix.printMatrix("Free Time: " , true);


                 
                    taskMachine[task] = selMachine;



                }	// task



                // calc updated pheromone value


                int maxFreeTime = (int) freeTimeMatrix.getMaxColumnValue(ant);

                //System.out.println("Total Free Time (max): " + maxFreeTime);


                s.setAnt(ant);
                s.setFreeTime(maxFreeTime);
                s.setTaskMachine(taskMachine);


             

                ////////////////// update delta
                ///-----delta=((1-evaporationrate)/(maxfreetime+maxreliability))
                delta = ((1 - evaporationRate) / (maxFreeTime ));
                //System.out.println("Delta: " + delta);
                //System.out.printf(" Delta: %.5f", delta);



                //pheromone updation

                for (int task : taskOrderList) {

                    int m = 0;
                    m = s.getTaskMachine()[task];
                    double updatedValue = (evaporationRate * pheromoneMatrix.getValue(task, m)) + delta;
                    pheromoneMatrix.setValue(task, m, updatedValue);
                    //System.out.println("-- " + task + " " + m + " "+ delta + " " + updatedValue);
                }

                //pheromoneMatrix.printMatrix("Update Pheromone matrix", false);


                // calculate reliability.....
                double rel=0;
                for(int k=0;k<noOfMachines;k++) {

                    rel+=processorFailureRate[k]*freeTimeMatrix.getMaxColValue(ant);

                }

                // makespan
                s.setMakespan(freeTimeMatrix.getMaxColValue(ant));

                s.setReliability(rel);


                //  add to local solution
                localSolutions.add(s);





            } // ant end

            
            System.out.println("\nLocal solutions: ");
            for(Solution s: localSolutions) {

            System.out.println(s);

            }





            // copy local to global solutions
            globalSolutions.addAll(localSolutions);

            //System.out.println("Global Sol size: " + globalSolutions.size());

            //////////////



            NonDominantSolutionSolver solver = new NonDominantSolutionSolver(noOfAnts, globalSolutions);
            globalSolutions = solver.run();
            localSolutions.clear();

            if (iter == noOfIterations) {
                System.out.println("\nGlobal solutions after nd: ");
                for (Solution s : globalSolutions) {

                    //System.out.println(s);

                    System.out.println("ant" + s.ant + " " +  s.makespan + " " + s.reliability);


                }

            }
            ////////////////// global pheromone update

            double globalDelta = 1;
            //System.out.println("global Delta: " + globalDelta);


            //global pheromone updation

            Hashtable<String, Double> ht = new Hashtable<String, Double>();

            for (Solution s : globalSolutions) {

                int[] taskMachine = s.getTaskMachine();

                for (int task = 0; task < taskMachine.length; task++) {

                    int machine = taskMachine[task];

                    double updatedValue = (w * pheromoneMatrix.getValue(task, machine)) + globalDelta;


                    if (ht.get(task + "-" + machine) == null) {
                        ht.put(task + "-" + machine, updatedValue);
                    }

                }

            }


            for (int r = 0; r < pheromoneMatrix.getNoOfRows(); r++) {
                for (int c = 0; c < pheromoneMatrix.getNoOfCols(); c++) {
                    double updatedValue = (evaporationRate * pheromoneMatrix.getValue(r, c));
                    pheromoneMatrix.setValue(r, c, updatedValue);
                }
            }


            for (String key : ht.keySet()) {

                String[] s = key.split("-");
                //System.out.println("Task : T" + s[0] + " machine: M" + s[1]);
                pheromoneMatrix.setValue(Integer.parseInt(s[0]), Integer.parseInt(s[1]), ht.get(key));

            }
        
        }

        //pheromoneMatrix.printMatrix("Update Pheromone matrix after global update", false);

        ////////

    } //iter end

    

    public static void printArray(String title, int[] array) {

        System.out.println("\n" + title + "\n");
        for (int k : array) {
            System.out.print(k + " ");
        }

    }
}

