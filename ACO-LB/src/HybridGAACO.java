

import java.util.*;

public class HybridGAACO {
  
    private int dist[][];
    
    
    Problem prob;
    FitnessFunction fit;
    
    public long[] TL_task;
    
    private double pheromone[][];
    private double choiceInfo[][];
    private SingleAnt ants[];
    
    private int noAnts;
    private int noNodes;
    private int solLen;
    
    private double alfa;
    private double beta;
    
    private double ro;
    
    private double xi;
    
    private double tau0;
    
    private double q0;
   
    private int iterations;
  
    private int[] bestSoFarTour;


    public HybridGAACO(int solLen,int noNodes, int noAnts,Problem prob,FitnessFunction fit){
        
        this.prob=prob;
        this.fit=fit;
        this.solLen=solLen;
        this.noNodes = noNodes;
        this.noAnts = noAnts;
        this.alfa = 1.0;
        this.beta = 3.0;
        this.ro = 0.1;
        this.xi = 0.1;
        this.iterations = 0;
        this.tau0 = 0;
        this.q0 = 0.9;

        TL_task = new long[noNodes];
        
        dist = new int[noNodes][noNodes];
        
        pheromone = new double[noNodes][noNodes];
        choiceInfo = new double[noNodes][noNodes];

        ants = new SingleAnt[noAnts];
        for(int i = 0; i < noAnts; i++){
            ants[i] = new SingleAnt(solLen);
        }
       
        bestSoFarTour = new int[solLen+1];

    }
    public HybridGAACO(int solLen,int noNodes, int noAnts, double alfa,
                    double beta, double ro, double xi, double q0, boolean doOpt2, boolean doOpt3){
        this.solLen=solLen;
        this.noNodes = noNodes;
        this.noAnts = noAnts;
        this.alfa = alfa;
        this.beta = beta;
        this.ro = ro;
        this.xi = xi;
        this.iterations = 0;
        this.tau0 = 0;
        this.q0 = q0;

        dist = new int[noNodes][noNodes];
        
        pheromone = new double[noNodes][noNodes];
        choiceInfo = new double[noNodes][noNodes];

        ants = new SingleAnt[noAnts];
        for(int i = 0; i < noAnts; i++){
            ants[i] = new SingleAnt(solLen);
        }
       
        bestSoFarTour = new int[solLen+1];
    }

    public SingleAnt getAnt(int k){
        return ants[k];
    }

    public double getPheromone(int i, int j){
        return pheromone[i][j];
    }
    public double getHeuristic(int i, int j){
        return choiceInfo[i][j];
    }
    public int getDistance(int i, int j){
        return dist[i][j];
    }
    public void setPheromone(int i, int j, double ph){
        pheromone[i][j] = ph;
    }
    public void setHeuristic(int i, int j, double h){
        choiceInfo[i][j] = h;
    }
    public void setDistance(int i, int j, int d){
        dist[i][j] = d;
    }
    public void setAlfa(double a){
        alfa = a;
    }
    public void setBeta(double b){
        beta = b;
    }
    public void setTau0(double tau){
        tau0 = tau;
    }
    public void setRo(double r){
        ro = r;
    }
    public void setXi(double x){
        xi = x;
    }
    public void setQ0(double q){
        q0 = q;
    }
    public double getAlfa(){
        return alfa;
    }
    public double getBeta(){
        return beta;
    }
    public double getRo(){
        return ro;
    }
    public double getXi(){
        return xi;
    }
    public double getTau0(){
        return tau0;
    }
    public double getQ0(){
        return q0;
    }
    public int[] getBestTour(){
        int[] bestTour = new int[solLen+1];
        double bestTourLength = Double.MAX_VALUE;
        int bestIdx = -1;
        for(int i = 0; i < noAnts; i++){
            if(ants[i].getTourLength() < bestTourLength){
                bestTourLength = ants[i].getTourLength();
                bestIdx = i;
            }
        }
        for(int j = 0; j <=solLen; j++)
            bestTour[j] = ants[bestIdx].getTour(j);
        return bestTour;
    }
    public int[] getBestSoFarTour(){
        return bestSoFarTour;
    }
    public void updateBestSoFarTour(){
        if(getBestTourLength() < computeTourLength(bestSoFarTour)){
            bestSoFarTour = getBestTour();
        }
    }
    public double getBestTourLength(){
        return computeTourLength(getBestTour());
    }
    public void setIteration(int iter){
        iterations = iter;
    }
    public int getIteration(){
        return iterations;
    }
    public int getNoAnts(){
        return noAnts;
    }
    public void setNoAnts(int ants){
        noAnts = ants;
    }
    public int getNoNodes(){
        return noNodes;
    }
    public void setNoNodes(int nodes){
        noNodes = nodes;
    }
    public void initData(){
        int i,j;
        for(i = 0; i < noNodes; i++)
            for(j = 0; j < noNodes; j++){
                dist[i][j] = 0;
                pheromone[i][j] = 0.0;
                choiceInfo[i][j] = 0.0;
            }
    }
    public void initPheromones(){
        int i,j;
        tau0 = computePheromone0();

        for(i = 0; i < noNodes; i++)
            for(j = 0; j < noNodes; j++)
                pheromone[i][j] = tau0;

        for(i = 0; i < noNodes; i++)
            pheromone[i][i] = 0;

        //opt = new OptimizationTSP(dist);
    }
    public void computeHeuristic(){
        double niu;
        int i,j;

        for(i = 0; i < noNodes; i++)
            for(j = 0; j < noNodes; j++){
                if(dist[i][j] > 0)
                    niu = 1.0/dist[i][j];
                else
                    niu = 1.0/0.0001;
            choiceInfo[i][j] = Math.pow(pheromone[i][j],alfa)*Math.pow(niu,beta);
        }
    }
    public void initAnts(){
        int i,j;
        for(i = 0; i < noAnts; i++){
            ants[i].setTourLength(0);

            for(j = 0; j < solLen; j++)
                ants[i].setTour(j, 0);
        }
    }
    
 
    
    public void decisionRule(int k, int step){
        

       /* int c = ants[k].getTour(step-1); // orasul anterior al furnicii curente
        double sumProb = 0.0;

        double selectionProbability[] = new double[noNodes];

        int j;
        for(j = 0; j < noNodes; j++){
            if( (j == c))
                selectionProbability[j] = 0.0;
            else{
                selectionProbability[j] = choiceInfo[c][j];
                sumProb+=selectionProbability[j];
            }

        }
        double prob = Math.random()*sumProb;
        j = 0;
        double p = selectionProbability[j];
        while(p < prob){
            j++;
            p += selectionProbability[j];
        }
        
        int randomDecision = j;

       
        double maxHeuristic = -1;
        int maxHeuristicIdx = -1;
        for(j = 0; j < noNodes; j++){
            if(maxHeuristic < choiceInfo[c][j]){
                maxHeuristic = choiceInfo[c][j];
                maxHeuristicIdx = j;
            }
        }

        if(Math.random() < q0){
            ants[k].setTour(step, maxHeuristicIdx);
            
        }
        else{
            ants[k].setTour(step, randomDecision);
            
        }*/

         Random rand = new Random();
        ants[k].setTour(step,Math.abs(rand.nextInt())%noNodes);

    }
        
    public void constructSolutions(){
      
        initAnts();

        int step = 0;
        int k;
        int r;

        Random rand = new Random();

        /* Initial city assignment */
        for(k = 0; k < noAnts; k++){
            r = Math.abs(rand.nextInt())%noNodes;

            ants[k].setTour(step,r);
           
            TL_task[r]= prob.getTasks().get(r).getMi();
        }
   
        while(step < solLen-1){
            step++;
            for(k = 0; k < noAnts; k++){
                decisionRule(k,step);
                localPheromoneUpdate(k,step);
            }
        }
     
        for(k = 0; k < noAnts; k++){
            
            ants[k].setTour(solLen,ants[k].getTour(0));
            localPheromoneUpdate(k,solLen);
            ants[k].setTourLength(computeTourLength(ants[k].getTour()));
        }
        updateBestSoFarTour();
    }


    public void constructSolutionsForHybrid(){
      
        initAnts();

        System.out.println("hybrid constuction...");        
        

        int step = 0;
        int k;
        int r;

        
        for(k = 0; k < noAnts; k++){
            
            
            Map<Integer,Integer>  sch1 = GA.getSCH(prob,fit);
            
            for(Integer tid : sch1.keySet()) {
            
                ants[k].setTour(tid-1,sch1.get(tid)-1);
                
            } 
           
            //ants[k].print();
        }
   
        
        for(k = 0; k < noAnts; k++){
            
            ants[k].setTour(solLen,ants[k].getTour(0));
            localPheromoneUpdate(k,solLen);
            ants[k].setTourLength(computeTourLength(ants[k].getTour()));
        }
        
        updateBestSoFarTour();
    }
    


    
    public void globalEvaporation(){
       
        for(int i = 0; i <solLen; i++){
            int idx1 = bestSoFarTour[i];
            int idx2 = bestSoFarTour[i+1];
            //System.out.println("Pheromone before global evaporation depozit: "+pheromone[idx1][idx2]);
            pheromone[idx1][idx2]*=(1-ro);
            pheromone[idx2][idx1]*=(1-ro);
            //System.out.println("Pheromone after global evaporation depozit: "+pheromone[idx1][idx2]);
        }
        
    }
    public void depositPheromone(int k){
        /* Storage is only on the best lap so far, so do not use this method */
        
    }
    public void globalPheromoneDeposit(){
        double delta = 1.0/((double) getBestTourLength());
        for(int i = 0; i <solLen; i++){
            int idx1 = bestSoFarTour[i];
            int idx2 = bestSoFarTour[i+1];
            //System.out.println("Pheromone before global pheromone depozit: "+pheromone[idx1][idx2]);
            pheromone[idx1][idx2]+=ro*delta;
            pheromone[idx2][idx1]+=ro*delta;
            //System.out.println("Pheromone after global pheromone depozit: "+pheromone[idx1][idx2]);
        }
    }
    public void updatePheromones(){
        globalEvaporation();
        globalPheromoneDeposit();
        computeHeuristic();
    }
    public void localPheromoneUpdate(int ant, int step){
        int idx1 = ants[ant].getTour(step);
        int idx2 = ants[ant].getTour(step-1);
        //System.out.println("Pheromone before local evaporation:"+pheromone[idx1][idx2]);
        double currentValue = pheromone[idx1][idx2];
        pheromone[idx1][idx2] = (1-xi)*currentValue+xi*tau0;
        pheromone[idx2][idx1] = pheromone[idx1][idx2];
        //Heuristic value update
        double niu = 0;
        if(dist[idx1][idx2] > 0)
            niu = 1.0/dist[idx1][idx2];
        else
            niu = 1.0/0.0001;
        //System.out.println("Pheromone after local evaporation:"+pheromone[idx1][idx2]);
        choiceInfo[idx1][idx2] = Math.pow(pheromone[idx1][idx2],alfa)*Math.pow(niu,beta);
        choiceInfo[idx1][idx2] = choiceInfo[idx2][idx1];
    }   
    private double greedyTour(){
        
        int tour[] = new int[solLen+1];
        double length;
        int min, node;
        int i,j;

        tour[0] = 0;
        bestSoFarTour[0] = 0;
       

        for(i = 1; i < solLen; i++){
            min = Integer.MAX_VALUE;
            node = -1;
            for(j = 0; j < solLen; j++){
                if((j!=tour[i-1])){
                    if(min > dist[tour[i-1]][j]){
                        min = dist[tour[i-1]][j];
                        node = j;
                    }
                }
            }
            tour[i] = node;
            bestSoFarTour[i] = node;
           
        }
        tour[noNodes] = tour[0];
        bestSoFarTour[noNodes] = bestSoFarTour[0];
        return computeTourLength(tour);

    }
    public double computeTourLength(int tour[]){
        
        //System.out.println("tour: " +  Arrays.toString(tour));
        
        int [] ind =new int[solLen];
        System.arraycopy(tour, 0, ind, 0, solLen);
        
        double len= fit.calc(tour);
        //System.out.println("len: "+ len);
        
        /*int len = 0;
        for(int i = 0; i < noNodes; i++){
            len+=dist[tour[i]][tour[i+1]];
        }*/
        
        
        return len;
    }
        
    private double computePheromone0(){
        return 1.0/(((double)greedyTour())*((double)noAnts));
    }
   
    public void localSearch(){
    /* Local search procedures*/
        
    }
    public double[][] getPheromoneMatrix(){
        return pheromone;
    }


     public static Map<Integer,Integer> getSCH(Problem prob,FitnessFunction fit) {

       // System.out.println("Tasks: " + prob.getTasks());
        
        Map<Integer,Integer> sch = new TreeMap<Integer,Integer>();
        
        int noAnts=100;
        int solLen=prob.getNumTasks();
        int noNodes = prob.getNumVMs();
                
        HybridGAACO ac= new HybridGAACO(solLen,noNodes, noAnts , prob, fit);
        
       
         ac.constructSolutionsForHybrid();
        
        int [] best = ac.getBestSoFarTour();
      
        //List<Integer> sch = new ArrayList<Integer>();
        for (int i=0;i<best.length-1;i++) {
            //sch.add(i);
            int tid = prob.getTasks().get(i).getId();
            int rid = prob.getResources().get(best[i]).getId();
            
            sch.put(tid, rid);
            
        }


        return sch;
        
     }
    
}
