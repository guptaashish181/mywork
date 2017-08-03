
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RunTwo {

    public static void main(String[] args) {
        
        int numTasks=300;
        int numVMs=5;
        
        Global.numTasks=numTasks;
        Global.numResources=numVMs;
        
        int scenario=3; // 1-more Lengthy , 2-less Lengthy, 3-mixed 50/50
       
        int steps=1;
            
        List<OutputMetric> ga=new ArrayList<OutputMetric>();
        List<OutputMetric> aco=new ArrayList<OutputMetric>();
        List<OutputMetric> gaaco=new ArrayList<OutputMetric>();
        
        for(int i=1;i<=steps;i++) {
        
        
        Problem prob = new Problem(numTasks,numVMs,scenario);
        
        Map<String,OutputMetric> results = new LinkedHashMap<String,OutputMetric>();
                 
        FitnessFunction fit = new FitnessFunction(prob);
                
        Simulator sim1 = new Simulator();
        Map<Integer,Integer>  sch1 = GA.getSCH(prob,fit);
        sim1.run(prob,sch1);
        results.put("GA ", sim1.metric);
        ga.add(sim1.metric);
     
        Simulator sim2 = new Simulator();       
        Map<Integer,Integer>  sch2 = AntColonySystem.getSCH(prob,fit);
        //Map<Integer,Integer>  sch2 = ACO.getSCH(prob,fit);
        sim2.run(prob,sch2);
        results.put("ACO ", sim2.metric);
        aco.add(sim2.metric);
     
        System.out.println("ACO: " + sch2.size() + "::" + sch2);
        
        
        Simulator sim3 = new Simulator();       
        Map<Integer,Integer>  sch3 = HybridGAACO.getSCH(prob,fit);
        sim3.run(prob,sch3);
        results.put("H-GA-ACO  ", sim3.metric);
        gaaco.add(sim3.metric);
     
        System.out.println("H-GA-ACO: " + sch3.size() + "::" + sch3);
        
        
        
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
        
        
        double [][] out= new double[3][3];
               
        for(int step=1;step<=steps;step++) {
        
       
            out[0][0] = out[0][0] + ga.get(step-1).getMakespan();
            out[0][1] = out[0][1] + ga.get(step-1).getAvgResourceUtilization()*100.0;
            out[0][2] = out[0][2] + ga.get(step-1).getLoadBalanceLevel();
            
            out[1][0] = out[1][0] + aco.get(step-1).getMakespan();
            out[1][1] = out[1][1] + aco.get(step-1).getAvgResourceUtilization()*100.0;
            out[1][2] = out[1][2] + aco.get(step-1).getLoadBalanceLevel();
            
            out[2][0] = out[2][0] + gaaco.get(step-1).getMakespan();
            out[2][1] = out[2][1] + gaaco.get(step-1).getAvgResourceUtilization()*100.0;
            out[2][2] = out[2][2] + gaaco.get(step-1).getLoadBalanceLevel();
            
            
        }
        
        
        //OutputUtil.print("Agg", out);
               
        for(int i=0;i<out.length;i++) {
            for(int j=0;j<out[i].length;j++) {
                out[i][j]=out[i][j]/steps;
            }
        }
        
        OutputUtil.print("Avg", out);
        
    }
    
    
    
}
