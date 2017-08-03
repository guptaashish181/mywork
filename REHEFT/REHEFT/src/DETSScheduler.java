
package org.workflowsim.examples.planning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
//import org.cloudbus.cloudsim.BaseCloudletScheduler.Event;

public class DETSScheduler extends BaseCloudletScheduler {
    
    public Map<Vm,List<Event> > newschedules;
    private Map<Cloudlet, Double> rank;
    protected Map<Vm, List<Event>> originalschedules;
    protected Map<Cloudlet, Double> earliestFinishTimes;

    protected Map<Cloudlet, Double> shiftamount;
    private Map<Integer, List<Cloudlet>> clusters;
    private Map<List<Cloudlet>, Vm> minimum_energy_vm;
    private static double arr[][];
    int seq = 0;

    

    protected class CloudletRank implements Comparable<CloudletRank> {

        public Cloudlet cloudlet;
        public Double rank;

        public CloudletRank(Cloudlet cloudlet, Double rank) {
            this.cloudlet = cloudlet;
            this.rank = rank;
        }

        //@Override
        public int compareTo(CloudletRank o) {
            return o.rank.compareTo(rank);
        }
    }

    public DETSScheduler(List<Cloudlet> cloudletlist, List<Vm> vmlist) {
        // TODO Auto-generated constructor stub
        super(cloudletlist, vmlist);
        minimum_energy_vm = new HashMap<List<Cloudlet>, Vm>();
        rank = new HashMap<Cloudlet, Double>();
        earliestFinishTimes = new HashMap<Cloudlet, Double>();
        for (Cloudlet cl : earliestFinishTimes.keySet()) {
            earliestFinishTimes.put(cl, Double.MAX_VALUE);
        }
        originalschedules = new HashMap<Vm, List<Event>>();
        schedules = new HashMap<Vm, List<Event>>();
        clusters = new HashMap<Integer, List<Cloudlet>>();

    }

    public void run() {
        try {

            Log.printLine("DETS scheduler running with " + Cloudletlist.size()
                    + " cloudlets.");

            averageBandwidth = calc_avg_bw();
            allocateVmPowerParameters();
            for (Object vmObject : vmlist) {
                Vm vm = (Vm) vmObject;
                originalschedules.put(vm, new ArrayList<Event>());
                schedules.put(vm, new ArrayList<Event>());
            }

            // Prioritization phase
            calc_ComputationCosts();
            calc_TransferCosts();
           // calculateRanks();

            Clustering ClusterObj = new Clustering(Cloudletlist, vmlist, rank);
            ClusterObj.dfs();
            clusters = ClusterObj.getClusters();
            //Log.printLine(clusters.size());
            allocateClusters(clusters);
            Log.printLine("\tInitial Energy Consumption\t" + calculate_old_energy(originalschedules));
            Log.printLine("\t Energy Consumption After DVFS\t" + calculate_new_energy(originalschedules));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double calculate_old_energy(Map<Vm, List<Event>> originalschedules) {
        double total_old_energy = 0;
        for (Vm vm : originalschedules.keySet()) {
            
            double total_initial_task_exec_time =0;
            double total_initial_idle_time = 0;
            double maxfinish = 0;
            double Exec_time = 0;
            
                
            
            

            total_initial_idle_time = 45;
            
       
            total_old_energy += findEnergyConsumption(vm, total_initial_task_exec_time);
        }
        
        return total_old_energy;
    }
  /*   public double calculate_old_energy(Map<Vm, List<Event>> originalschedules) {
        double total_old_energy = 0;
        for (Vm vm : originalschedules.keySet()) {
            
            double total_initial_task_exec_time =0;
            double total_initial_idle_time = 0;
            double maxfinish = 0;
            double Exec_time = 0;
            List<Event> list_event = originalschedules.get(vm);

            for (Event e : list_event) {
               
                if (e.finish > maxfinish) {
                    maxfinish = e.finish;
                }
                
                Exec_time += e.finish - e.start;
            }
            

            total_initial_idle_time = maxfinish - Exec_time;
            
            total_initial_task_exec_time = Exec_time;
            total_old_energy += findEnergyConsumption(vm, total_initial_task_exec_time);
        }
        
        return total_old_energy;
    }*/
 protected double findEnergyConsumption(Vm vm, double time){
		double executionEnergy = 0.0;
                time=time*vmparams.get(vm).CPI;
                System.out.println("time "+time );
		executionEnergy = vmparams.get(vm).coefficient * Math.pow(1.5, 2)*(time);
                System.out.println(executionEnergy);
		return executionEnergy;
	}
    public double calculate_new_energy(Map<Vm, List<Event>> originalschedules) {
        double total_new_energy = 0;
        // newschedules=originalschedules;
        int size = Cloudletlist.size();
        //double total_new_energy = 0.0;
        for (Vm vm : originalschedules.keySet()) 
        {
            List<Event> list_event = originalschedules.get(vm);
            double finish = 0;
            double start = 0;
            int n = list_event.size();
            Event e1, e2;

            for (int i = 0; i < n - 1; i++) 
            {
                e1 = list_event.get(i);
                e2 = list_event.get(i + 1);
                if (e2.start - e1.finish > 0) 
                {
                    //e1.finish=e2.start;
                    double exec = e1.finish - e1.start;
                    double frequency = e1.cloudlet.getCloudletLength()/ (e2.start - e1.start);
                    double voltage = 0;
                    if (frequency < vmparams.get(vm).maxfreq*0.5)
                    {
                        frequency = vmparams.get(vm).maxfreq*0.5;
                        voltage = 1.0;
                    }
                    else if (frequency < 1.26) 
                    {
                        frequency = 1.26;
                        voltage = 1.1;
                    }
                    else if (frequency < 1.47)
                    {
                        frequency = 1.47;
                        voltage = 1.2;
                    }
                    else if (frequency < 1.68) 
                    {
                        frequency = 1.68;
                        voltage = 1.3;
                    }
                    else if(frequency <1.89)
                    {
                        frequency = 1.89;
                        voltage = 1.4;
                    }   
                    else
                    {
                        frequency = 2.1;
                        voltage = 1.5;
                    }
                    double newexecutiontime = vmparams.get(vm).maxfreq * exec / frequency;
                    e1.finish = e1.start + newexecutiontime;
                    total_new_energy += compute_energy(vm, voltage, newexecutiontime) + compute_idle_energy(vm, e2.start - e1.finish);
                    earliestFinishTimes.put(e1.cloudlet, e1.finish);
                }

                //frequency=e1.cloudlet.getCloudletLength()/(e1.finish-e1.start);
            }
            //total_new_energy=total_new_energy;

        }
        System.out.print(total_new_energy);
        return total_new_energy;

    }

    /*public double calc_optimize_energy(double e1, double e2) {
        return (e1 - e2) / e1;
    }

    public void calcu_min_energy_vm(Map<List<Cloudlet>, Map<Vm, Double>> clus_vm_exec) {
        for (List<Cloudlet> cluster : clus_vm_exec.keySet()) {
            Map<Vm, Double> vms = new HashMap<Vm, Double>();
            vms = clus_vm_exec.get(cluster);
            double min_energy = Double.MAX_VALUE;
            Vm assigned_vm = null;
            for (Vm vm : vms.keySet()) {
                double energy = vms.get(vm);
                if (energy < min_energy) {
                    min_energy = energy;
                    assigned_vm = vm;
                }
            }
            minimum_energy_vm.put(cluster, assigned_vm);
        }
    }
*/
    public void calculateRanks() {
        for (Object cloudletObject : Cloudletlist) {
            
            Cloudlet cloudlet = (Cloudlet) cloudletObject;
            
            
            System.out.println("Rank of cloudlet "+cloudlet.getCloudletId()+" is "+calculateRank(cloudlet));
            
        }
    }

    public double calculateRank(Cloudlet cloudlet) {
        if (rank.containsKey(cloudlet)) {
            return rank.get(cloudlet);
        }

        double avg=0.0;
        for (Double cost : computationCosts.get(cloudlet).values())
        {
            avg += cost;
        }
        avg /= vmlist.size();
        averageComputationCost.put(cloudlet,avg);
        System.out.println("Cloudlet "+cloudlet.getCloudletId()+"averagecomcost="+averageComputationCost.get(cloudlet));
        

        double max = 0.0;
        for (Cloudlet child : Runner.getChildList(cloudlet)) {
                    double rankval = calculateRank(child);
            double childCost = transferCosts.get(cloudlet).get(child) + rankval;
            max = Math.max(max, childCost);
           
        }
        rank.put(cloudlet, averageComputationCost.get(cloudlet) + max);
		//print the rank for the cloudlet
        
        System.out.println("max="+max+" computationcost="+averageComputationCost.get(cloudlet));
        
        System.out.println("Cloudlet: " + cloudlet.getCloudletId() + "rank: " + rank.get(cloudlet));
        return rank.get(cloudlet);
    }

    private void allocateClusters(Map<Integer, List<Cloudlet>> clusters) {

        // Sorting in non-ascending order of rank
        //Collections.sort(cloudletRank);
        for (Integer cluster : clusters.keySet()) {
            Log.printLine("Allocating Cluster nO"+cluster);
           Vm chosenvm=allocateClust(clusters.get(cluster));
           allocateCluster(clusters.get(cluster),chosenvm);
        }
    }
private Vm allocateClust(List<Cloudlet> cluster) {
        
           double maxenergy=0.0;
           Vm selectedVm=null;
           for(Vm vm:vmlist)
           {
               double energy=0.0; 
               for(Cloudlet cloudlet:cluster)
                {
                        energy+=vmparams.get(vm).coefficient*vmparams.get(vm).voltage*vmparams.get(vm).voltage*cloudlet.getCloudletLength();
                }
               if(energy>maxenergy)
               {
                   maxenergy=energy;
                   selectedVm=vm;
               }
               System.out.println(energy);
            }
            return selectedVm;
    }
       
    private void allocateCluster(List<Cloudlet> cluster,Vm chosenvm) {
    	
        double earliestFinishTime = Double.MAX_VALUE;
        //double bestReadyTime = 0.0;
        double finishTime= Double.MAX_VALUE;

        List<Event> Schedules = new ArrayList<Event>(findFinishTime(cluster, chosenvm));
        //Schedules=originalschedules.get(chosenvm);
        //System.out.println("Schedule size is "+Schedules.size());
        //System.out.println("\tvm chosen is "+chosenvm.getId()+" and fininsh time is "+earliestFinishTime);
        //Schedules = findFinishTime(cluster, chosenvm);
        /*System.out.println("\tSchedule is");
        for(Event e:Schedules)
        {
            System.out.println("\t"+e.cloudlet.getCloudletId()+"\t"+e.start+"\t"+e.finish);
        }*/
        originalschedules.put(chosenvm, Schedules);
        //findFinishTime(cloudlet, chosenvm, bestReadyTime, true);
        ///Log.printLine("\tVm is chosen\t");

        for (Event e : Schedules) {
            int id = e.cloudlet.getCloudletId();
            //Log.printLine("\t\t\t\t"+id);
            Cloudlet cloudlet = e.cloudlet;
            if (cluster.contains(cloudlet)) {
                //Log.printLine("--------------HEYYYY");
                begin[id] = e.start;
                end[id] = e.finish;
                vmallocated[id] = chosenvm.getId();
                earliestFinishTimes.put(e.cloudlet, e.finish);
                e.cloudlet.setVmId(chosenvm.getId());

                //cloudlet.setVmId(chosenvm.getId());
                //Log.printLine("Cloudlet\t"+ id+"   Allocated on VM\t"+chosenvm.getId());
            }
        }

    }

    private List<Event> findFinishTime(List<Cloudlet> cluster, Vm vm) {
        List<Event> Schedule= new ArrayList<Event>(originalschedules.get(vm));
        
        System.out.println("\t In findfinishtime Schedule size is "+Schedule.size());
        
        double finishtime = 0.0;
        Map<Cloudlet, Double> FinishTimes = new HashMap<Cloudlet, Double>(earliestFinishTimes);

        //Log.printLine("\t\t\t\tSize of cluster is "+cluster.size());
        try {
           
            for (Cloudlet cloudlet : cluster) 
            {
            //Log.printLine("\t\tCloudlet No "+cloudlet.getCloudletId());

                double minReadyTime = 0.0;
                for (Cloudlet parent : Runner.getParentList(cloudlet)) 
                {
                    
                    double readyTime = Double.MAX_VALUE;
                    if (FinishTimes.containsKey(parent))
                    {
                        //Log.printLine("Exception may be here");
                        readyTime = FinishTimes.get(parent);
                        //Log.printLine("\t\t\t\t\tfinish time of parent received");
                    }
                    double transfercost=0.0;
                    boolean flag=false;
                    for(Event e:Schedule)
                    {
                        if(e.cloudlet.getCloudletId()==parent.getCloudletId())
                        {
                            flag=true;
                        }
                    }
                    if(!flag)
                    {
                        System.out.println("\t\tCondition true");
                        transfercost=Math.max( transferCosts.get(parent).get(cloudlet),transfercost);                       
                        
                    }

                    minReadyTime = Math.max(minReadyTime, readyTime+transfercost);
                }
                //finishTime = findFinishTime(cloudlet, vm, minReadyTime, false);
                System.out.println("\tMin ready time of cloudlet "+cloudlet.getCloudletId()+" is "+minReadyTime);
                int id = cloudlet.getCloudletId();
                double computationCost = computationCosts.get(cloudlet).get(vm);
                double start, finish = 0;
                int pos;

                if (Schedule.size()==0) 
                {
                    finish=minReadyTime + computationCost;
                    Schedule.add(new Event(minReadyTime, minReadyTime + computationCost, cloudlet, vm.getMips()));
                    //Log.printLine("\t\t\t\tCAse 1");
                } 
                else if (Schedule.size() == 1)
                {

                    try 
                    {
                        
                        if (minReadyTime >= Schedule.get(0).finish) 
                        {
                            pos = 1;
                            start = minReadyTime;
                        }
                        else if (minReadyTime + computationCost <= Schedule.get(0).start)
                        {
                            pos = 0;
                            start = minReadyTime;
                        } 
                        else
                        {
                            pos = 1;
                            start = Schedule.get(0).finish;
                        }
                        finish= start + computationCost;
                        Schedule.add(pos, new Event(start, start + computationCost, cloudlet, vm.getMips()));
                        //Log.printLine("\t\t\t\tCAse 2");
                    } 
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        //Log.printLine("------HERE in case 1");
                    }
                }
                else 
                {
                    // Trivial case: Start after the latest task scheduled
                    start = Math.max(minReadyTime, Schedule.get(Schedule.size() - 1).finish);
                    finish = start + computationCost;
                    int i = Schedule.size() - 1;
                    int j = Schedule.size() - 2;
                    pos = i + 1;
                    while (j >= 0) 
                    {
                        Event current = Schedule.get(i);
                        Event previous = Schedule.get(j);

                        if (minReadyTime > previous.finish&&minReadyTime + computationCost <= current.start) 
                        {
                            start = minReadyTime;
                            finish = minReadyTime + computationCost;
                            pos=i;
                        }
                        else if (minReadyTime>previous.start&&minReadyTime < previous.finish&&previous.finish + computationCost <= current.start)
                        {
                            start = previous.finish;
                            finish = previous.finish + computationCost;
                            pos = i;
                        }
                        
                        i--;
                        j--;
                    }

                    if (minReadyTime + computationCost <= Schedule.get(0).start) 
                    {
                        pos = 0;
                        start = minReadyTime;
                        finish= start + computationCost;
                        Schedule.add(pos, new Event(start, start + computationCost, cloudlet, vm.getMips()));
                        // Log.printLine("\t\t\t\tCAse 3");

                    } 
                    else 
                    {
                        Schedule.add(pos, new Event(start, finish, cloudlet, vm.getMips()));
                        //Log.printLine("\t\t\t\tCAse 4");

                    }
                }
                // Log.printLine("\t\t\t\t\tInputting finish times");
                FinishTimes.put(cloudlet, finish);
                // Log.printLine("\t\t\t\t\tfinish times entered");
            }
        }
        catch (Exception e) 
        {

        }
        return Schedule;
    }

}
