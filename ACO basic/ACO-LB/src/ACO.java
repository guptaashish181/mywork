//@Ashish Gupta, guptaashish181@gmail.com, NIT Kurukshetra
import java.util.*;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
/*
@author Ashish
*/
public class ACO {

	public class position{
		int vm;
		int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	}

	private List<Ant> ants;
	private int antcount;
	private int Q = 100;
	private double[][] pheromone;
	private double[][] Delta;
	private int VMs;
	private int tasks;
	public position[] bestTour;//The best solution
	private double bestLength;//The length of the optimal solution (the size of the time）
	private List<? extends Cloudlet> cloudletList;
	private List<? extends Vm> vmList;

        public void init(int antNum, List<? extends Cloudlet> list1, List<? extends Vm> list2){
		//cloudletList = new ArrayList<? extends Cloudlet>;
		cloudletList = list1;
		vmList = list2;
		antcount = antNum;
		ants = new ArrayList<Ant>();
		VMs = vmList.size();
		tasks = cloudletList.size();
		pheromone = new double[VMs][tasks];
		Delta = new double[VMs][tasks];
		bestLength = 1000000;
		//Initialize the pheromone matrix
		for(int i=0; i<VMs; i++){
			for(int j=0; j<tasks; j++){
				pheromone[i][j] = 0.1;
			}
		}
		bestTour = new position[tasks];
		for(int i=0; i<tasks; i++){
			bestTour[i] = new position(-1, -1);
		}
		//Randomly placed ants
        for(int i=0; i<antcount; i++){
            ants.add(new Ant());
            ants.get(i).RandomSelectVM(cloudletList, vmList);
        }
	}
	/**
	 * ACO
	 * @param maxgen ACO The maximum number of iterations
	 */
	public void run(int maxgen){
		for(int runTime=0; runTime<maxgen; runTime++){
			System.out.println("iter:: "+runTime);
			//Each ant moves the process
			for(int i=0; i<antcount; i++){
				for(int j=1; j<tasks; j++){
					ants.get(i).SelectNextVM(pheromone);
//                                 System.out.println("ant movement : " + ants);       
				}
                             System.out.println("Ant : " + i + "::" + ants.get(i).tour + " :: " + ants.get(i).tourLength  );                             
			}
                        
       
                        
                        
                        
                        
			for(int i=0; i<antcount; i++){
				//System.out.println("First "+i+" Only ants");
				ants.get(i).CalTourLength();
				//System.out.println("First "+i+" Only ants away："+ants.get(i).tourLength);
				ants.get(i).CalDelta();
	if(ants.get(i).tourLength<bestLength){
					//Keep the optimal path
	                bestLength = ants.get(i).tourLength;
	                //System.out.println("First "+runTime+"generation"+"First "+i+"Only ants find new solutions："+bestLength);
	                for(int j=0;j<tasks;j++){
	                	bestTour[j].vm = ants.get(i).tour.get(j).vm;
	                    bestTour[j].task = ants.get(i).tour.get(j).task;
	                System.out.println("best: " + bestTour[j].task + "-" + bestTour[j].vm );
                        }
                        
                        
                                    
                        
	                //Update the pheromone on the way to find the optimal solution
	                for(int k=0; k<VMs; k++){
	                	for(int j=0; j<tasks; j++){
	                		pheromone[k][j] = pheromone[k][j] + Q/bestLength;
	                	}
	                }
				}
			}
			UpdatePheromone();//Update pheromones for each road

			//Re-set the ant again
			for(int i=0;i<antcount;i++){
				ants.get(i).RandomSelectVM(cloudletList, vmList);
		    }
		}
	}
	/**
     * Update pheromone matrix
     */
	public void UpdatePheromone(){
		double rou=0.5;
        for(int k=0; k<antcount; k++){
        	for(int i=0; i<VMs; i++){
        		for(int j=0; j<tasks; j++){
        			Delta[i][j] += ants.get(k).delta[i][j];
        		}
        	}
        }

        for(int i=0; i<VMs; i++){
        	for(int j=0; j<tasks; j++){
        		pheromone[i][j] = (1-rou)*pheromone[i][j] + Delta[i][j];
        	}
        }
	}
	/**
     * Output the program to run the results
     */
    public void ReportResult(){
        System.out.println("The optimal path length is "+bestLength);
        for(int j=0; j<tasks; j++)
        {
        	System.out.println(bestTour[j].task+"Assigned to "+bestTour[j].vm);
        }
    }
}
