
import java.util.Random;

public class Individual {

    
    
    static int defaultGeneLength = Problem.numTasks;
    private int [] genes = new int[defaultGeneLength];
    private double fitness = 0;

    
      public static int getRandomBetween() {
        int result=0; 
        int index=0;
        Random r=new Random();
        //int start=GA.ranges[index][0];
        //int end=GA.ranges[index][1];
        
        int start=0;
        int end=Problem.numVMs;
        
         result=(r.nextInt((end-start))+start);
         return result;
            
    }
    public void generateIndividual() {
        for (int i = 0; i < size(); i++) {
            int gene = this.getRandomBetween();
            genes[i] = gene;
        }
    }

    public void setIndividual(int [] x) {
        for (int i = 0; i < size(); i++) {
            
            genes[i] = x[i];
        }
    }
    public static void setDefaultGeneLength(int length) {
        defaultGeneLength = length;
    }
    
    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int value) {
        genes[index] = value;
        fitness = 0;
    }

    
    public int size() {
        return genes.length;
    }

    public int[] getGenes() {
        return genes;
    }

    
    
    public double getFitness() {
        if (fitness == 0) {
            //fitness = FitnessCalc.getFitness(this);
            fitness = GA.fit.calc(genes);
        }
        return fitness;
    }

    @Override
    public String toString() {
               
        String geneString = "[";
        for (int i = 0; i < size(); i++) {
            geneString += String.format("%d",getGene(i));
            if(i<size()-1) {
                geneString+=",";
            }
        }
        geneString+="]";
        return geneString;
    }
}


