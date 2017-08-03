//@Ashish gupta  National Institute of Technology, Kurukshetra


public class Solution {

	static int counter=0;
	
	int id;

	int ant;

	int [] taskMachine;

        double Loadbalancing;

	int  freeTime;

        double makespan;
		
	public Solution() {
		id=++counter;
	}
	
	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		Solution.counter = counter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAnt() {
		return ant;
	}

	public void setAnt(int ant) {
		this.ant = ant;
	}

	public int[] getTaskMachine() {
		return taskMachine;
	}

	public void setTaskMachine(int[] taskMachine) {
		this.taskMachine = taskMachine;
	}

    public double getMakespan() {
        return makespan;
    }

    public void setMakespan(double makespan) {
        this.makespan = makespan;
    }

    public double getReliability() {
        return Loadbalancing;
    }

    public void setReliability(double reliability) {
        this.Loadbalancing = reliability;
    }

	

	public int getFreeTime() {
		return freeTime;
	}

	public void setFreeTime(int freeTime) {
		this.freeTime = freeTime;
	}
	
	
		
	

	public String toString() {
		String t= "id: " + id + " ant: " + (ant+1) + " makespan: " + makespan + " LBL.: "+ Loadbalancing;
		/*
		int k=0;
		for(int i: taskMachine) {
			t= t + " " +  "[T" + (k+1) + " - M" + (i+1) + "]"; 
			k++;
		}*/
		
		return t;
	}
	
	
}
