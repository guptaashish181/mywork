
public class FitnessFunction {

    Problem prob;

    public FitnessFunction(Problem prob) {
        this.prob = prob;
    }

    public double calc(int[] individual) {

        double fitness = 0;

        int n = prob.numTasks;
        
        //System.out.println("n:"+n);

        int m = prob.numVMs;

        double[][] et = prob.et;

        double[] delay = new double[m];

        double[] est = new double[n];

        double[] eft = new double[n];

        double makespan = 0;

        int taskIndex = -1;

        for (int i = 0; i < n; i++) {

            int vm = individual[i];

            est[i] = delay[vm];

            eft[i] = est[i] + et[i][vm];

            
            delay[vm] = delay[vm] + eft[i];

            if (eft[i] > makespan) {

                makespan = eft[i];

                taskIndex = i;

            }

        }


    
        
        fitness=makespan;
                
        return fitness;

    }
}
