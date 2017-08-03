import java.util.*;

 public class Ant{
     
	public class position{
		public int vm;
		public int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	} 
	public double[][] delta;
	public int Q = 100;
	public List<position> tour;
	public double tourLength;
	public long[] TL_task;
	public List<Integer> tabu;
	private int VMs;
	private int tasks;
	private List<Task> cloudletList;
	private List<Resource> vmList;
	
        
        public void RandomSelectVM(List<Task> list1, List<Resource> list2){
		cloudletList = list1;
		vmList = list2;
		VMs = vmList.size();
		tasks = cloudletList.size();
		delta = new double[VMs][tasks];
		TL_task = new long[VMs];
		for(int i=0; i<VMs; i++)TL_task[i] = 0;
		tabu = new ArrayList<Integer>();
		tour=new ArrayList<position>();
		
		
		int firstVM = (int)(VMs*Math.random());
		int firstExecute = (int)(tasks*Math.random());
		tour.add(new position(firstVM, firstExecute));
		tabu.add(new Integer(firstExecute));
		TL_task[firstVM] += cloudletList.get(firstExecute).getMi();
	}
	
        
	public double Dij(int vm, int task){
		double d;
	    d = TL_task[vm]/vmList.get(vm).getMips() + cloudletList.get(task).getMi()/vmList.get(vm).getBw();
		return d;
	}
	
        
	  public void SelectNextVM(double[][] pheromone){
		  double[][] p;
		  p = new double[VMs][tasks];
		  double alpha = 1.0;
		  double beta = 1.0;
		  double sum = 0;
		  
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  if(tabu.contains(new Integer(j))) continue;
				  sum += Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta);
			  }
		  }
		  
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  p[i][j] = Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta)/sum;
				  if(tabu.contains(new Integer(j)))p[i][j] = 0;
			  }
		  }
		double selectp = Math.random();
        
        double sumselect = 0;
        int selectVM = -1;
        int selectTask = -1;
        boolean flag=true;
        for(int i=0; i<VMs&&flag==true; i++){
        	for(int j=0; j<tasks; j++){
        		sumselect += p[i][j];
        		if(sumselect>=selectp){
        			selectVM = i;
        			selectTask = j;
        			flag=false;
        			break;
        		}
        	}
        }
        if (selectVM==-1 | selectTask == -1)  
          
    		tabu.add(new Integer(selectTask));
		tour.add(new position(selectVM, selectTask));
		TL_task[selectVM] += cloudletList.get(selectTask).getMi();  		
	  }
	  
	  
	  
	public void CalTourLength(){
		
		double[] max;
		max = new double[VMs];
		for(int i=0; i<tour.size(); i++){
			max[tour.get(i).vm] += cloudletList.get(tour.get(i).task).getMi()/vmList.get(tour.get(i).vm).getMips(); 
		}		
		tourLength = max[0];
		for(int i=0; i<VMs; i++){
			if(max[i]>tourLength)tourLength = max[i];
			
		}
		
	}
	
    public void CalDelta(){
    	for(int i=0; i<VMs; i++){
    		for(int j=0; j<tasks; j++){
    			if(i==tour.get(j).vm&&tour.get(j).task==j)delta[i][j] = Q/tourLength;
    			else delta[i][j] = 0;
    		}
    	}
    }
 }
