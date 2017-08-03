
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class GA extends Global {

    static int [][] ranges= new int[1][2];
        
   static List<Integer> initial; 
   
    static Problem prob;
    
    static FitnessFunction fit;
   
   static List<Resource> resources;
   static List<Task> tasks;
   
   
   public static Map<Integer,Integer> getSCH(Problem prob,FitnessFunction fit) {

      //  System.out.println("Tasks: " + prob.getTasks());
        
        Map<Integer,Integer> sch = new TreeMap<Integer,Integer>();
        
          GA.prob=prob;
       
       ranges[0][0]=0;
       ranges[0][1]=Problem.numVMs;
       
       List<Resource> resources= prob.getResources();
       List<Task> tasks=prob.getTasks();
       
       GA.fit=fit;
       
       GA.resources=resources;
       GA.tasks=tasks;
       
       
        //resources=Resource.getResourceList();
        //tasks=Task.random();
       
       
        int popSize=numParticles;
       
        //int maxIter=10;
       
        
        Population myPop = new Population(popSize, true);
        
        int generationCount = 0;
        for(int iter=1;iter<=maxIter;iter++) {
            generationCount++;
            //System.out.println("GA::iter:"+generationCount);
            Individual best=myPop.getFittest();
          
            myPop = Algorithm.evolvePopulation(myPop);
            
            
        }
        
        
            Individual best=myPop.getFittest();
            //System.out.println(best);
            
        
        //System.out.println("Solution found!");
                
        //System.out.println("Generation: " + generationCount);
        
        best=myPop.getFittest();
        //System.out.println("Global Best:"+best);

          
        Individual gBest = best;
      
        //List<Integer> sch = new ArrayList<Integer>();
        for (int i=0;i<gBest.size();i++) {
            //sch.add(i);
            int tid = prob.getTasks().get(i).getId();
            int rid = prob.getResources().get(gBest.getGene(i)).getId();
            
            sch.put(tid, rid);
            
        }


        return sch;
        
     }
   
  
}