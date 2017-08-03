
public class SingleAnt{
    
    SingleAnt(int n){
        
        tour = new int[n+1];
        
        tourLength = 0;
        
    }
    
    //private int noNodes;
    private double tourLength;
    private int tour[];
    
    public double getTourLength() {
        return tourLength;
    }

    public void setTourLength(double tourLength) {
        this.tourLength = tourLength;
    }

   
   
    public void setTourLength(int len){
        tourLength = len;
    }
   
    public void setNoNodes(int len){
        tourLength = len;
    }
  
    public int getTour(int idx){
        return tour[idx];
    }
    public void setTour(int idx, int val){
        tour[idx] = val;

    }
    public int[] getTour(){
        return tour;
    }
}
