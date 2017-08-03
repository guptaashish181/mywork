//@author Ashish gupta  National Institute of Technology, Kurukshetra
//Min Min Scheduling without Cloudsim
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ashish
 */

public class MyMinMin {
    
     public static void main(String[] args) {
    
        int [] mi = {

}; 
        
        int [] mips = {2,
3,
5,
2,
1,
5,
6,
7,
6,
5,
4,
6,
8,
9,
6,
4,
3,
5,
6,
7,
5,
4,
3,
5,
7,
4,
7,
9,
8,
7,
8,
4};
            
        int n=mi.length;
        int m=mips.length;
        System.out.println("\nTask Length:\t"+mi.length);
        System.out.println("\nMachine Length:\t"+mips.length);
        
        System.out.println("\nMIN-MIN Scheduling \n");
       
        System.out.println("========== OUTPUT ==========");
        System.out.println("Task No"+"\t"+ "Start Time" + "\t"+ "Finish Time");
         
        
        
        int [] sch = new int[n];
        double [] start = new double[n];
        double [] end = new double[n];        
         
        boolean[] isRemoved=new boolean[n];

        double [] mat=new double[m];
        
               
        double [][] c= new double[n][m];
        
        double [][] et= new double[n][m];
        
            for(int i=0;i<n;i++){
                for(int j=0;j<m;j++) {
                    c[i][j]=(double)mi[i]/mips[j];
                    et[i][j]=c[i][j];
                }
            }
    /*    for(int i=0;i<n;i++){
            for(int j=0;j<m;j++) {
        System.out.println("ET ",et[i][j] +" ");
        
        System.out.println("start CT ",c[i][j] + " ");
            }
        }
      */  
        int i=0;

        int tasksRemoved=0;
        do{
            double minTime=Double.MAX_VALUE;
            int machine=-1;
            int taskNo=-1;
            /*Find the task in the meta set with the earliest completion time and the machine that obtains it.*/
            for(i=0;i<n;i++){
            
                if(isRemoved[i])continue;
                for(int j=0;j<m;j++){
                    if(c[i][j]<minTime){
                        minTime=c[i][j];
                        machine=j;
                        taskNo=i;
                    }
                }
            }           
            
            start[taskNo]=mat[machine];
            end[taskNo]=start[taskNo]+et[taskNo][machine];
           
            System.out.println(taskNo  + "\t" + start[taskNo]  + "\t"+ end[taskNo]);

            
            mat[machine]=mat[machine]+minTime;
            
            sch[taskNo]=machine;
            
            
            tasksRemoved++;
            isRemoved[taskNo]=true;
     
            /*Update c[][] Matrix for other tasks in the meta-set*/
            for(i=0;i<n;i++){
             
                if(isRemoved[i])continue;
                
                else{   
                    c[i][machine]=mat[machine]+et[i][machine];
                }
            }            

        }  while(tasksRemoved!=n);
        

         System.out.println("SCH: " + Arrays.toString(sch));
         
         double max=0;
         for(int ii=0;ii<n;ii++){
             
             if(end[ii]>max) {
                 max=end[ii];
                 
             }
             
         }
         
         double makespan=max;
              
         //OutputUtil.print("out: CT",c);
                  
         System.out.println("makespan:"+makespan);

         
         
    }
}
