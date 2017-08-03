
import java.util.Map;

public class OutputMetric {

   
   private double makespan;
  
  Map<Integer,Integer> useCounter;
  Map<Integer,Double> useTime;
   
  
  private double avgResourceUtilization = 0;
  private double loadBalanceLevel = 0;

    public double getLoadBalanceLevel() {
        return loadBalanceLevel;
    }

    public void setLoadBalanceLevel(double loadBalanceLevel) {
        this.loadBalanceLevel = loadBalanceLevel;
    }


  
  public Map<Integer, Integer> getUseCounter() {
        return useCounter;
    }

    public void setUseCounter(Map<Integer, Integer> useCounter) {
        this.useCounter = useCounter;
    }

    public Map<Integer, Double> getUseTime() {
        return useTime;
    }

    public void setUseTime(Map<Integer, Double> useTime) {
        this.useTime = useTime;
    }

    public double getAvgResourceUtilization() {
        return avgResourceUtilization;
    }

    public void setAvgResourceUtilization(double avgResourceUtilization) {
        this.avgResourceUtilization = avgResourceUtilization;
    }
  
  
  
  

    public double getMakespan() {
        return makespan;
    }

    public void setMakespan(double makespan) {
        this.makespan = makespan;
    }

  
  
    
  
  
   
}
