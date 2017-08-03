//@Ashish gupta  National Institute of Technology, Kurukshetra
//Meta-heuristic scheduling for independent task

import graph.Graph;
import graph.TaskOrderGenerator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.*;
/*
@author Ashish
*/
public class ACO {

    public static Matrix getExecutionTimeMatrix(double [] taskCompCost,double [] processorCompCost) {
        
                		
		Matrix mat  = new Matrix(taskCompCost.length,processorCompCost.length);
		
                
                for(int r=0;r<taskCompCost.length;r++) {
                    for(int c=0;c<processorCompCost.length;c++) {
                        
                        mat.setValue(r, c, ((double)taskCompCost[r]/processorCompCost[c]));
                        
                    }
                }                
                return mat;        
    }
    
    public static void main(String[] args) {
       
        Scanner sc= new Scanner(System.in);
        
         int noOfTasks ;
         System.out.println("\nEnter the NO. of task :\n");
         noOfTasks=sc.nextInt();
         int noOfProcessors ;
        System.out.println("\nEnter the NO. of Processor :\n");
             noOfProcessors=sc.nextInt();
        
         double[] computationCost = new double[noOfProcessors];
          System.out.println("\nEnter the Computation Cost of Processors :\n");
         //double[] computationCost =new double [noOfProcessors];
         for(int i=0;i<noOfProcessors;i++)
         {
         computationCost[i]=sc.nextDouble();
         }         
         
          System.out.println("\nEnter the Number of iteration :\n");
          int noOfIterations=sc.nextInt();
          
         double[][] communicationCost = new double[noOfProcessors][noOfProcessors];
        
        Random rd=new Random();
        for(int i=0;i<noOfProcessors;i++)
        {
            double mips=rd.nextInt(5)*10+1;
            computationCost[i]=mips;
        }
        
        Graph processorGraph = new Graph(true, "P", computationCost, communicationCost);
        processorGraph.printGraph();
       
        // For no. of Task
        RandomTaskGraph rtg=new RandomTaskGraph(noOfTasks,20,5,1.0); // nooftasks,avgcompcost,margin,ccr
        
        double [] taskComputationCost= rtg.getTaskComputationCost();    // with random task graph
        //double [] taskComputationCost ={20, 19, 15, 23, 15, 23, 19, 23};   
        double[][] taskCommunicationCost =rtg.getTaskCommunicationCost();
        //double[] taskComputationCost = rtg.getTaskComputationCost();
       
       //double[] taskComputationCost = new double[noOfTasks];
       //  double[][] taskCommunicationCost = new double[noOfTasks][noOfTasks];
      System.out.println("\nRandomly Assigned Computation Cost of Tasks :\n");
        Random rds=new Random();
        for(int i=0;i<noOfTasks;i++)
        {
            double mips=(rds.nextInt(5))*14+17;
            taskComputationCost[i]=mips;
            System.out.println("Task "+ (i+1) +":"+taskComputationCost[i]);
        }
       


        Matrix etm = getExecutionTimeMatrix(taskComputationCost,computationCost);

       // etm.printMatrix("ETM", true);


        //Graph taskGraph = new Graph(true, "V", taskComputationCost, taskCommunicationCost);
       // taskGraph.printGraph();

        
        // input
        int noOfMachines = noOfProcessors; //////////

        int noOfAnts = 10;
       // int noOfIterations = 1000; // 200, 500, 1000

        double evaporationRate = 0.4;
        double delta = 0;


        //double w = 0.2;
        double alpha = 1, beta = 5;   //2 to 5

        //local

        ArrayList<Solution> localSolutions = new ArrayList<Solution>();


        //global
        ArrayList<Solution> globalSolutions = new ArrayList<Solution>();

        Matrix executionTimeMatrix=etm;

        executionTimeMatrix.printMatrix("Execution Time: ", true);
       
        Matrix pheromoneMatrix = new Matrix(noOfTasks, noOfMachines, 0.5);       
       
        Matrix completionTimeMatrix;//=Matrix.getFreeTimeMatrix();
     
        int completionTime, cost;
        double pheromone, neta, sum;
        double[] temp;

        /// assign initial solution to all ant
   
        //completionTimeMatrix = new Matrix();
         
        for (int iter = 1; iter <= noOfIterations; iter++) {
  
            completionTimeMatrix = new Matrix(noOfMachines,noOfAnts);
            
            //// 
            
           // completionTimeMatrix.printMatrix("completionTimeMatrix: " , true);

            // loop for ant
            for (int ant = 0; ant < noOfAnts; ant++) {
                completionTimeMatrix.printMatrix("completionTimeMatrix: " , true);
                Solution s = new Solution();

                System.out.print("\nTask Order For Ant " + (ant+1) + ": ");


                int[] taskMachine = new int[noOfTasks];

                // Neta Calc
                neta=0;
                
                for (int task=0;task<noOfTasks;task++) {
                    
                    sum = 0;
                    temp = new double[noOfMachines];
                    for (int machine = 0; machine < noOfMachines; machine++) {

                        System.out.print("\nAnt: " + (ant+1) + " Task: T" + (task+1) + " Machine: M" + (machine+1) + ": ");
                    
                        completionTime = (int) completionTimeMatrix.getValue(machine, ant);   //machine=3, ant=10
                       
                        pheromone = pheromoneMatrix.getValue(task, machine);        //task=8 , machine= 3
                        System.out.print(" Pheromone: " + pheromone);

                        if(completionTime==0)
                        {
                         neta=1000;                             
                         System.out.print(" Neta: Infinity");
                        }
                        else{
                        neta = (1.0 / completionTime);  // updated
                        System.out.print(" Neta: " + neta);
                        }
                        

                       sum = sum + (Math.pow(neta, beta) * Math.pow(pheromone, alpha)); // updated denominatore
   
                        temp[machine] = (Math.pow(pheromone, alpha) * Math.pow(neta, beta)); // numerator
                     
                    }

                    System.out.println("\nProbability calc: ");

                    double maxProbability = 0;
                    int selMachine = -1;
                    for (int machine = 0; machine < noOfMachines; machine++) {
                        double prob = (temp[machine] / sum);
                        System.out.print(" Prob: " + prob);
                    
                        if (prob > maxProbability) {
                            maxProbability = prob;
                            selMachine = machine;
                        }

                    }

                    System.out.println("\nAnt " + (ant+1) + " Task: T" + (task+1) + " select machine M" + (selMachine+1));
                    taskMachine[task] = selMachine;

                    int et = (int) executionTimeMatrix.getValue(task, selMachine);
                    
                    int ft = (int) completionTimeMatrix.getValue(selMachine, ant);
                    completionTimeMatrix.setValue(selMachine, ant, (ft + et));
                    completionTimeMatrix.printMatrix("Completion Time: " , true);

                }	// task



                // calc updated pheromone value


                int maxCompletionTime = (int) completionTimeMatrix.getMaxColumnValue(ant);

                System.out.println("Total Completion Time (max): " + maxCompletionTime);


                s.setAnt(ant);
                s.setFreeTime(maxCompletionTime);
                s.setTaskMachine(taskMachine);

                ////////////////// update delta
                //-----delta=((1-evaporationrate)/(maxfreetime+maxreliability))
                
                delta = (1 / (maxCompletionTime ));
                //double w=0.5, y=0.5;
                //delta =((1/(maxCompletionTime)) );
                System.out.println("Delta: " + delta);
                //System.out.printf(" Delta: %.5f", delta);
                


                //pheromone updation

                for (int task=0;task<noOfTasks;task++) {

                    int m = 0;
                    m = s.getTaskMachine()[task];
                    double updatedValue = ((1-evaporationRate) * pheromoneMatrix.getValue(task, m)) + evaporationRate*delta;
                    pheromoneMatrix.setValue(task, m, updatedValue);
                    System.out.println("--pheromnone update  T" + task + " m " + m + " delta "+ delta + " update Value " + updatedValue);
                }

                pheromoneMatrix.printMatrix("Update Pheromone matrix", false);

                //calculate resource utilization 
                    double sru=0;
                   for(int k=0;k<noOfMachines;k++)
                   {
                  // sru+=completionTimeMatrix.getValue(ant,k)/maxFreeTime;
                    sru+=completionTimeMatrix.getValue(k,ant);
                   } 
                   
                    double avgRU = (sru/noOfMachines);
                    
                    sru = 0.0;
                    for(int i=0; i < noOfMachines; i++) {
                    double ru=(completionTimeMatrix.getValue(i,ant));
                        sru += Math.pow((avgRU-ru),2);
                    }        
                    double d = Math.sqrt(sru/noOfMachines);        
                    double LBL = (1-(d/avgRU))*100.0;          //load balancing level
                      
                
                // makespan
                s.setMakespan(completionTimeMatrix.getMaxColValue(ant));

                s.setReliability(LBL);


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

                System.out.println("\nGlobal solutions before NDS: ");
                for (Solution s : globalSolutions) {

                    //System.out.println(s);

                    System.out.println("ant" + s.ant + " MS " +  s.makespan + " LBL " + s.Loadbalancing);


                }

      //    NonDominantSolutionSolver solver = new NonDominantSolutionSolver(noOfAnts, globalSolutions);
             NDS solver = new NDS(noOfAnts, globalSolutions);
            globalSolutions = solver.run();



                System.out.println("\nGlobal solutions after NDS(filtering): ");
                for (Solution s : globalSolutions) {

                    //System.out.println(s);

                    System.out.println("ant" + s.ant + " MS " +  s.makespan + " LBL " + s.Loadbalancing);


                }
            
            localSolutions.clear();

            if (iter == noOfIterations) {
                System.out.println("\nOptimal Global solutions after NDS: ");
                for (Solution s : globalSolutions) {

                    //System.out.println(s);

                    System.out.println("ant" + s.ant + " MS " +  s.makespan + " LBL " + s.Loadbalancing);


                }

            }
            ////////////////// global pheromone update

            double globalDelta = 1;
            double globalEvaporationrate =0.1;
            //System.out.println("global Delta: " + globalDelta);


            //global pheromone updation

            Hashtable<String, Double> ht = new Hashtable<String, Double>();

            for (Solution s : globalSolutions) {

                int[] taskMachine = s.getTaskMachine();

                for (int task = 0; task < taskMachine.length; task++) {

                    int machine = taskMachine[task];

                    double updatedValue = ((1-globalEvaporationrate)* pheromoneMatrix.getValue(task, machine)) + globalEvaporationrate*globalDelta;


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
              // System.out.println("Task : T" + s[0] + " machine: M" + s[1]);
                pheromoneMatrix.setValue(Integer.parseInt(s[0]), Integer.parseInt(s[1]), ht.get(key));

            }
        
        }

        pheromoneMatrix.printMatrix("Update Pheromone matrix after global update", false);

       

    } //iter end

    

    public static void printArray(String title, int[] array) {

        System.out.println("\n" + title + "\n");
        for (int k : array) {
            System.out.print(k + " ");
        }

    }
}