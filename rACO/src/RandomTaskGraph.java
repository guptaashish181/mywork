//@Ashish gupta  National Institute of Technology, Kurukshetra
import java.util.Random;


public class RandomTaskGraph {
    
    int noOfTasks;
    int avgComputationCost;
    int margin;
    double ccr;
    double [] taskComputationCost;
    double [][] taskCommunicationCost;
    
    public RandomTaskGraph(int noOfTasks,int avgComputationCost,int margin,double ccr) {
        
        this.noOfTasks=noOfTasks;
        this.avgComputationCost=avgComputationCost;
        this.ccr=ccr;
        this.margin=margin;
                
        
        taskCommunicationCost=new double[noOfTasks][noOfTasks];
        taskComputationCost=new double[noOfTasks];
                
        int min=(avgComputationCost-margin);
        int max=(avgComputationCost+margin);
               
        Random rnd=new Random();
        
        for(int k=0;k<noOfTasks;k++) {
            taskComputationCost[k]= (int) (rnd.nextInt(max-min)+min);
        }
        
        
       for(int i=0;i<noOfTasks;i++) {
            for(int k=0;k<noOfTasks;k++) {
                if(k>i) {
                    taskCommunicationCost[i][k]=(int)(ccr*taskComputationCost[k]);

                } else {
                    taskCommunicationCost[i][k]=0;
        
                }
            }
            
                    
        }
        
        
       
    }

    public double[] getTaskComputationCost() {
        return taskComputationCost;
    }

    public double[][] getTaskCommunicationCost() {
        return taskCommunicationCost;
    }

    
    
    
    
}
