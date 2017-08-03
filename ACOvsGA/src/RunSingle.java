
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RunSingle {

    public static void main(String[] args) {
        
        int numTasks=5;              // 100 - 300
        int numVMs=3;
        
        Global.numTasks=numTasks;
        Global.numResources=numVMs;
        
        int scenario=3; // 1-more Lengthy , 2-less Lengthy, 3-mixed 50/50
       
        int steps=1;                   //iteration
        
    
        List<OutputMetric> aco=new ArrayList<OutputMetric>();
        
        for(int i=1;i<=steps;i++) {
        
        
        Problem prob = new Problem(numTasks,numVMs,scenario);
        
        Map<String,OutputMetric> results = new LinkedHashMap<String,OutputMetric>();
                 
        FitnessFunction fit = new FitnessFunction(prob);
        
        
      
        Simulator sim2 = new Simulator();       
        Map<Integer,Integer>  sch2 = AntColonySystem.getSCH(prob,fit);
        sim2.run(prob,sch2);
        results.put("ACO sch", sim2.metric);
        aco.add(sim2.metric);
     
            System.out.println("ACO sch: " + sch2.size() + "::" + sch2);
        /*
        System.out.println("\nStep: " + i);
        System.out.println("Algorithm\tMS\tAvgRU\tLBL");
        System.out.println("=====================================================================");
        
        
        for(String algo : results.keySet()) {
            
            OutputMetric m = results.get(algo);
            
            System.out.printf("%10s%8.2f%8.2f%8.2f\n",
                    algo,m.getMakespan(),
                    m.getAvgResourceUtilization(),
                    m.getLoadBalanceLevel());
                    
            
        }
        
        System.out.println("=====================================================================");
                
        
        System.out.println("\nRU:");
        for(String algo : results.keySet()) {
            
            OutputMetric m = results.get(algo);
            System.out.println(algo + ":"+m.useCounter + " , " + m.useTime);
        
        }
        
        }
        
        
        
        System.out.println("\nFinal Result: \n");
        
        
        double [][] out= new double[1][3];
               
        for(int step=1;step<=steps;step++) {
        
       
           
            out[1][0] = out[1][0] + aco.get(step-1).getMakespan();
            out[1][1] = out[1][1] + aco.get(step-1).getAvgResourceUtilization()*100.0;
            out[1][2] = out[1][2] + aco.get(step-1).getLoadBalanceLevel();
            
        }
        
        
            
        
        for(int i=0;i<out.length;i++) {
            for(int j=0;j<out[i].length;j++) {
                out[i][j]=out[i][j]/steps;
            }
        }
        
        OutputUtil.print("Avg", out);
        */
        
    }
    
    }
    
}
