//Nidhi Rehani, nidhirehani@gmail.com, NIT Kuru

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
//import org.cloudbus.cloudsim.examples.HEFTPlanningAlgorithmExample;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HEFTScheduler {

	private List<Cloudlet> Cloudletlist;
	private List <Vm> vmlist;
	private Map<Cloudlet, Map<Vm, Double>> computationCosts;
    private Map<Cloudlet, Map<Cloudlet, Double>> transferCosts;
    private Map<Cloudlet, Double> rank;
    private Map<Vm, List<Event>> schedules;
    private Map<Cloudlet, Double> earliestFinishTimes;
    private double averageBandwidth;
    double averageComputationCost = 0.0;

    private class Event {

        public double start;
        public double finish;

        public Event(double start, double finish) {
            this.start = start;
            this.finish = finish;
        }
    }

    private class CloudletRank implements Comparable<CloudletRank> {

        public Cloudlet cloudlet;
        public Double rank;

        public CloudletRank(Cloudlet cloudlet, Double rank) {
            this.cloudlet = cloudlet;
            this.rank = rank;
        }

        @Override
        public int compareTo(CloudletRank o) {
            return o.rank.compareTo(rank);
        }
    }

	/*cloudlet_length[];
	private double execution_time [][];
	private double avg_execution_time[];
	private double communication_time[][];
	private double rank[];
	private double start_time[];
	private double exp_finish_time[];
	private double finish_time[];
	private int schedule_list[];
	private double total_execution_time;
	List <Vm> vmlist;

	private int cloudletlist_size;
	private int reslist_size;
	*/





	public HEFTScheduler(List<Cloudlet> cloudletlist, List<Vm> vmlist) {


		this.Cloudletlist = cloudletlist;
		this.vmlist = vmlist;
		computationCosts = new HashMap<>();
        transferCosts = new HashMap<>();
        rank = new HashMap<>();
        earliestFinishTimes = new HashMap<>();
        schedules = new HashMap<>();


	}

	public void run() {
        Log.printLine("HEFT scheduler running with " + Cloudletlist.size()
                + " cloudlets.");

        averageBandwidth = calc_avg_bw();

        for (Object vmObject : vmlist) {
            Vm vm = (Vm) vmObject;
            schedules.put(vm, new ArrayList<Event>());
        }


        // Prioritization phase
        calc_ComputationCosts();
        calc_TransferCosts();
        calculateRanks();
        Log.printLine("Ranks calculated");
        // Selection phase
        allocateCloudlets();
    }


	public double calc_avg_bw(){
		double avg = 0.0;
	        for (Object vmObject : vmlist) {
	            Vm vm = (Vm) vmObject;
	            avg += vm.getBw();
	        }
	        return avg / vmlist.size();
	}

    private void calc_ComputationCosts() {
        for (Object cloudletObject : Cloudletlist) {
            Cloudlet cloudlet = (Cloudlet) cloudletObject;

            Map<Vm, Double> costsVm = new HashMap<Vm, Double>();

            for (Object vmObject : vmlist) {
                Vm vm = (Vm) vmObject;
                if (vm.getNumberOfPes() < cloudlet.getNumberOfPes()) {
                    costsVm.put(vm, Double.MAX_VALUE);
                }
                else {
                    costsVm.put(vm,cloudlet.getCloudletTotalLength() / vm.getMips());
                }
            }
            computationCosts.put(cloudlet, costsVm);
        }
    }

    private void calc_TransferCosts() {
        // Initializing the matrix
        for (Object cloudletObject1 : Cloudletlist) {
            Cloudlet cloudlet1 = (Cloudlet) cloudletObject1;
            Map<Cloudlet, Double> cloudletTransferCosts = new HashMap<Cloudlet, Double>();

            for (Object cloudletObject2 : Cloudletlist) {
                Cloudlet cloudlet2 = (Cloudlet) cloudletObject2;
                cloudletTransferCosts.put(cloudlet2, 0.0);
            }

            transferCosts.put(cloudlet1, cloudletTransferCosts);
        }

        // Calculating the actual values
        for (Cloudlet parentObject : Cloudletlist) {
            Cloudlet parent = parentObject;

            for (Cloudlet child : HEFTalgo.getChildList(parent)) {

                transferCosts.get(parent).put(child, calculateTransferCost(parent, child));
            }

        }
    }



	private double calculateTransferCost(Cloudlet parent, Cloudlet child) {

        double filesize = 0.0;
        //data to be transferred is equal to the output file size of the parent cloudlet in bytes
        filesize = parent.getCloudletOutputSize();
        //file Size is in Bytes, acc in MB
        //filesize = filesize / Consts.MILLION;
        // acc in MB, averageBandwidth in Mb/s
        return (filesize ) / averageBandwidth;
    }

    private void calculateRanks() {
        for (Object cloudletObject : Cloudletlist) {
            Cloudlet cloudlet = (Cloudlet) cloudletObject;
            calculateRank(cloudlet);
        }
    }

    private double calculateRank(Cloudlet cloudlet) {
        if (rank.containsKey(cloudlet)) {
            return rank.get(cloudlet);
        }

        for (Double cost : computationCosts.get(cloudlet).values()) {
            averageComputationCost += cost;
        }

        averageComputationCost /= computationCosts.get(cloudlet).size();

        double max = 0.0;
        for (Cloudlet child : HEFTalgo.getChildList(cloudlet)) {
        	double rankval = calculateRank(child);
        	double childCost = transferCosts.get(cloudlet).get(child)+ rankval;
        	max = Math.max(max, childCost);
        }

        rank.put(cloudlet, averageComputationCost + max);

        return rank.get(cloudlet);
    }

    private void allocateCloudlets() {
        List<CloudletRank> cloudletRank = new ArrayList<>();
        for (Cloudlet cloudlet : rank.keySet()) {
            cloudletRank.add(new CloudletRank(cloudlet, rank.get(cloudlet)));
        }

        // Sorting in non-ascending order of rank
        Collections.sort(cloudletRank);
        for (CloudletRank cr : cloudletRank) {
            allocateCloudlet(cr.cloudlet);
        }

    }

    private void allocateCloudlet(Cloudlet cloudlet) {
        Vm chosenvm = null;
        double earliestFinishTime = Double.MAX_VALUE;
        double bestReadyTime = 0.0;
        double finishTime;

        for (Object vmObject : vmlist) {
            Vm vm = (Vm) vmObject;
            double minReadyTime = 0.0;

            for (Cloudlet parent : HEFTalgo.getParentList(cloudlet)) {
                double readyTime = earliestFinishTimes.get(parent);
                if (parent.getVmId() != vm.getId()) {
                    readyTime += transferCosts.get(parent).get(cloudlet);
                }

                minReadyTime = Math.max(minReadyTime, readyTime);
            }

            finishTime = findFinishTime(cloudlet, vm, minReadyTime, false);

            if (finishTime < earliestFinishTime) {
                bestReadyTime = minReadyTime;
                earliestFinishTime = finishTime;
                chosenvm = vm;
            }
        }

        findFinishTime(cloudlet, chosenvm, bestReadyTime, true);
        earliestFinishTimes.put(cloudlet, earliestFinishTime);

        cloudlet.setVmId(chosenvm.getId());
    }

    private double findFinishTime(Cloudlet cloudlet, Vm vm, double readyTime,
            boolean occupySlot) {
        List<Event> sched = schedules.get(vm);
        double computationCost = computationCosts.get(cloudlet).get(vm);
        double start, finish;
        int pos;

        if (sched.size() == 0) {
            if (occupySlot) {
                sched.add(new Event(readyTime, readyTime + computationCost));
            }
            return readyTime + computationCost;
        }

        if (sched.size() == 1) {
            if (readyTime >= sched.get(0).finish) {
                pos = 1;
                start = readyTime;
            } else if (readyTime + computationCost <= sched.get(0).start) {
                pos = 0;
                start = readyTime;
            } else {
                pos = 1;
                start = sched.get(0).finish;
            }

            if (occupySlot) {
                sched.add(pos, new Event(start, start + computationCost));
            }
            return start + computationCost;
        }

        // Trivial case: Start after the latest task scheduled
        start = Math.max(readyTime, sched.get(sched.size() - 1).finish);
        finish = start + computationCost;
        int i = sched.size() - 1;
        int j = sched.size() - 2;
        pos = i + 1;
        while (j >= 0) {
            Event current = sched.get(i);
            Event previous = sched.get(j);

            if (readyTime > previous.finish) {
                if (readyTime + computationCost <= current.start) {
                    start = readyTime;
                    finish = readyTime + computationCost;
                }

                break;
            }

            if (previous.finish + computationCost <= current.start) {
                start = previous.finish;
                finish = previous.finish + computationCost;
                pos = i;
            }

            i--;
            j--;
        }

        if (readyTime + computationCost <= sched.get(0).start) {
            pos = 0;
            start = readyTime;

            if (occupySlot) {
                sched.add(pos, new Event(start, start + computationCost));
            }
            return start + computationCost;
        }
        if (occupySlot) {
            sched.add(pos, new Event(start, finish));
        }
        return finish;
    }
}
