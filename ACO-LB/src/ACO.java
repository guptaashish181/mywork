
import java.util.*;
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
	public position[] bestTour;
	private double bestLength;
        
	private List<Task> cloudletList;
	private List<Resource> vmList;

        public void init(int antNum, Problem prob){
		cloudletList = prob.getTasks();
		vmList = prob.getResources();
		antcount = antNum;
		ants = new ArrayList<Ant>(); 
		VMs = vmList.size();
		tasks = cloudletList.size();
		pheromone = new double[VMs][tasks];
		Delta = new double[VMs][tasks];
		bestLength = 1000000;
		
		for(int i=0; i<VMs; i++){
			for(int j=0; j<tasks; j++){
				pheromone[i][j] = 0.1;
			}
		}
		bestTour = new position[tasks];
		for(int i=0; i<tasks; i++){
			bestTour[i] = new position(-1, -1);
		}
		
        for(int i=0; i<antcount; i++){  
            ants.add(new Ant());  
            ants.get(i).RandomSelectVM(cloudletList, vmList);
        }  			
	}
	
        public void run(int maxgen){
            
		for(int runTime=0; runTime<maxgen; runTime++){
                        
                    System.out.println("ACO::iter:"+runTime);
			
                        for(int i=0; i<antcount; i++){
				//for(int j=1; j<tasks; j++){	
					ants.get(i).SelectNextVM(pheromone);
				//}
			}
                        
			for(int i=0; i<antcount; i++){
	
				ants.get(i).CalTourLength();
	
				ants.get(i).CalDelta();
				if(ants.get(i).tourLength<bestLength){  
	
	                bestLength = ants.get(i).tourLength;  
	
	                for(int j=0;j<tasks;j++){  
	                	bestTour[j].vm = ants.get(i).tour.get(j).vm;
	                        bestTour[j].task = ants.get(i).tour.get(j).task;
	                } 
	
	                for(int k=0; k<VMs; k++){
	                	for(int j=0; j<tasks; j++){
	                		pheromone[k][j] = pheromone[k][j] + Q/bestLength;
	                	}
	                }  
				}
			}
			UpdatePheromone();
				
			
			for(int i=0;i<antcount;i++){  
				ants.get(i).RandomSelectVM(cloudletList, vmList);  
		    }  	
		}
	}
	 
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
	 
        
     public static Map<Integer,Integer> getSCH(Problem prob,FitnessFunction fit) {

               
        Map<Integer,Integer> sch = new TreeMap<Integer,Integer>();
        
        int noAnts=Global.numAnts;
        int solLen=prob.getNumTasks();
        int noNodes = prob.getNumVMs();
        int maxiter = solLen;
        
        ACO ac = new ACO();
        ac.init(noAnts, prob);
        ac.run(maxiter);
        
        
        position[] bestt = ac.bestTour;
         System.out.println("bestt: " + bestt.length);
             
        
        for (int i=0;i<bestt.length;i++) {
        
            position p = bestt[i];
            
            System.out.println(p.task + " - " + p.vm);
            
            int tid = prob.getTasks().get(p.task).getId();
            int rid = prob.getResources().get(p.vm).getId();
            
            sch.put(tid, rid);
            
        }
     

        return sch;
        
     }
}
