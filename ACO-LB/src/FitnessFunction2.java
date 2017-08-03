
public class FitnessFunction2 {

    Problem prob;

    public FitnessFunction2(Problem prob) {
        this.prob = prob;
    }

    public double calc(int[] individual) {

        double fitness = 0;

        int n = prob.numTasks;
        
        int m = prob.numVMs;

        double[][] et = prob.et;

        double[] delay = new double[m];

        double[] est = new double[n];

        double[] eft = new double[n];

              
        for (int i = 0; i < n; i++) {

            int vm = individual[i];

            est[i] = delay[vm];

            eft[i] = est[i] + et[i][vm];

            delay[vm] = delay[vm] + eft[i];

            if(eft[i]>et[i][vm]) {
                fitness += (1- (et[i][vm]/eft[i])); 
            } else {
                fitness+=1;
            }
            


        }


        
        fitness=(1.0/fitness);
        
        System.out.println("fitness:"+fitness);
                
        return fitness;

    }
}
