
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task {
    
    private int id;
    private int mi;
    private int inputFileSize;
    private int outputFileSize;
    
    private Double deadline;
    private Double credit;
    private Double maxUserPay;

    public Double getDeadline() {
        return deadline;
    }

    public void setDeadline(Double deadline) {
        this.deadline = deadline;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getMaxUserPay() {
        return maxUserPay;
    }

    public void setMaxUserPay(Double maxUserPay) {
        this.maxUserPay = maxUserPay;
    }

    

    public Task() {
    }

    
    public Task(int id, int mi) {
        this.id = id;
        this.mi = mi;
    }

    public Task(int id, int mi, int inputFileSize, int outputFileSize) {
        this.id = id;
        this.mi = mi;
        this.inputFileSize = inputFileSize;
        this.outputFileSize = outputFileSize;
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMi() {
        return mi;
    }

    public void setMi(int mi) {
        this.mi = mi;
    }

    public int getInputFileSize() {
        return inputFileSize;
    }

    public void setInputFileSize(int inputFileSize) {
        this.inputFileSize = inputFileSize;
    }

    public int getOutputFileSize() {
        return outputFileSize;
    }

    public void setOutputFileSize(int outputFileSize) {
        this.outputFileSize = outputFileSize;
    }
    
    public String toString() {
        
        //return "[T"+id + " " + mi +" " + inputFileSize+  " " + outputFileSize +"]";
        return "T"+id + " ";
        
    }
    
    public static void print(List<Task> list) {
        
        System.out.println("\n\nTasks: ");
        for(Task t: list ) {
            System.out.println(t);
        }
        
        
    }
    
    
    
    public static List<Task> random(int n) {
        
        //Scanner sc= new Scanner(System.in);
        
        //System.out.println("enter no of tasks: ");
        //int n= sc.nextInt();
        
        
        List<Task> list=new ArrayList<Task>();
        
        int minMI=1000;
        int maxMI=5000;
        int avgMI=(int) ((double)(minMI+maxMI)/2.0);
        
        int minFileSize=100;
        int maxFileSize=300;
        
        
        Random rnd=new Random();
        
        
        for(int i=0;i<n;i++) {
            
            //int mi=(minMI+rnd.nextInt((maxMI-minMI)));
            
            int mi=(1+rnd.nextInt(9))*1000;
            int fs=(1+rnd.nextInt(4))*100;
            //int fs=(minFileSize+rnd.nextInt(maxFileSize-minFileSize));
            
            Task t=new Task((i+1),mi,fs,fs);
            
            list.add(t);
            
        }
        
        
        //System.out.println("Total Tasks: " + list.size());
               
        
        return list;
        
    }
    
    
    public static List<Task> randomScenario(int n,double p) {
        
                
        List<Task> list=new ArrayList<Task>();
        
        int minMI=1000;
        int maxMI=5000;
        int avgMI=(int) ((double)(minMI+maxMI)/2.0);
        
        int minFileSize=100;
        int maxFileSize=300;
        
        
        Random rnd=new Random();
        
        //double p = 0.8;
        
        int counter=0;
        
        for(int i=0;i<n;i++) {
            
            //int fs=(1+rnd.nextInt(4))*100;
            int fs=100;
            int mi=0;
            //int mi=(minMI+rnd.nextInt((maxMI-minMI)));
            if(rnd.nextDouble() <= p) {
                
                counter++;
                mi=(5+rnd.nextInt(9))*1000;
            
            //int fs=(minFileSize+rnd.nextInt(maxFileSize-minFileSize));
            } else {
                
                mi=(1+rnd.nextInt(2))*1000;
                
            }
           // System.out.println("i:"+i + "mi:" +mi);
            Task t=new Task((i+1),mi,fs,fs);
            
            list.add(t);
            
        }
        
        System.out.println("Length Counter: " + counter);
        //System.out.println("Total Tasks: " + list.size());
               
        
        return list;
        
    }
    
    
    
}
