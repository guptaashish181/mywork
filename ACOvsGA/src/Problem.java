
import java.util.List;

public class Problem {

    static int numVMs;
    static int numTasks;
    List<Task> tasks;
    List<Resource> resources;
    
    double[][] et;
    public static double avET=0;
    
    double avgTaskLen = 0;
    double maxTaskLen = 0;
    

    public int getNumVMs() {
        return numVMs;
    }

    public void setNumVMs(int numVMs) {
        this.numVMs = numVMs;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(int numTasks) {
        this.numTasks = numTasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public double[][] getEt() {
        return et;
    }
    
    public double[][] getClonedEt() {
        
        int rows=et.length;
        int cols=et[0].length;
        
        double [][] cet = new double[rows][cols];
        
        for(int i=0;i<rows;i++) {
            for(int j=0;j<cols;j++) {
                
                cet[i][j]=et[i][j];
            }
        }
        
        
        
        
        return cet;
    }

    public void setEt(double[][] et) {
        this.et = et;
    }

    public Problem(int numTasks1, int numVMs1,int sn) {

        numVMs = numVMs1;
        numTasks = numTasks1;
        double p=0.5;
        if(sn==1) {
            p=0.7;
            tasks = Task.randomScenario(numTasks,p);
        } if(sn==2) {
            p=0.2;
            tasks = Task.randomScenario(numTasks,p);
        } else if(sn==3) {
            //tasks = Task.random(numTasks);
            tasks = Task.randomScenario(numTasks,p);
        }
        
        resources = Resource.random(numVMs);
        
        // Expected exe time. every task on every machine
        avET=0;
        et = new double[numTasks][numVMs];

        for (int i = 0; i < numTasks; i++) {
            for (int j = 0; j < numVMs; j++) {

                et[i][j] = (tasks.get(i).getMi() / (double) resources.get(j).getMips());

                avET+=et[i][j];
            }
        }

        
        avET = avET/((double)numTasks*numVMs);
        
        //OutputUtil.print("et", et);

    }

    public double getAvgTaskLength() {

        if (avgTaskLen == 0) {

            double sum = 0;
            double max = 0;
            for (Task t : tasks) {
                sum += t.getMi();
                if (t.getMi() > max) {
                    max = t.getMi();
                }
            }

            avgTaskLen = (sum / numTasks);
            maxTaskLen = max;

        }

        return avgTaskLen;
    }

    
    public double getMaxTaskLen() {

        return maxTaskLen;

    }

    
        
    public double  getAvgET(int taskIndex) {
    
        double av=0;
        
        double s=0;
            for (int j = 0; j < numVMs; j++) {

                s+= (tasks.get(taskIndex).getMi() / (double) resources.get(j).getMips());

            }
        

            av = (s/numVMs);
        //OutputUtil.print("re et", et);
        
        return av;
    }

    
}
