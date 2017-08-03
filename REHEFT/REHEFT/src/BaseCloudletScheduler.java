//@author Nidhi Rehani, nidhirehani@gmail.com, NIT Kurukshetra

package org.workflowsim.examples.planning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
//import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Vm;


public class BaseCloudletScheduler {


	public class Event {

		public double start;
		public double finish;
		Cloudlet cloudlet;
		double mips;

		public Event(double start, double finish, Cloudlet cloudlet, double mips) {
			this.start = start;
			this.finish = finish;
			this.cloudlet = cloudlet;
			this.mips = mips;
		}
	}

	public class VmParameters {
		public double coefficient;
		public double maxfreq;
		public double voltage;
                public double CPI;
		
		public VmParameters(double maxfreq,double coefficient,double voltage,double CPI){
			this.coefficient = coefficient;
			//this.maxMIPS = maxMIPS;
			//this.minMIPS = minMIPS;
                        this.voltage=voltage;
                        this.CPI=CPI;
                    //this.coefficient = 2.0;
                    this.maxfreq = maxfreq;
                  //  this.minMIPS =1.05;
                    //this.CPI=2.0;
		}
	}



	protected List<Cloudlet> Cloudletlist;
	protected List <Vm> vmlist;
	protected Map<Cloudlet, Map<Vm, Double>> computationCosts;
	protected Map<Cloudlet, Map<Cloudlet, Double>> transferCosts;
	protected double averageBandwidth;
	Map<Cloudlet,Double> averageComputationCost ;
	double makespan =0;
	double begin[], end[];
        int vmallocated[];
	//double ccr = 0.2;

	protected static Map<Cloudlet, Double> startTimes;
	protected static Map<Cloudlet, Double> durationTimes;
	protected static Map<Cloudlet, Integer > reservationIds;

	protected Map<Vm, List<Event>> schedules;

	public static Map<Vm, VmParameters> vmparams;
	
	double totalEnergyConsumption = 0.0;
	protected Map<Vm, Double> executionTimes;
	protected Map<Vm, Double> executionEnergies;
	
	
	public BaseCloudletScheduler(List<Cloudlet> cloudletlist, List<Vm> vmlist) {
		this.Cloudletlist = cloudletlist;
		this.vmlist = vmlist;
		computationCosts = new HashMap<Cloudlet, Map<Vm, Double>>();
		averageComputationCost=new HashMap<Cloudlet,Double>();
                transferCosts = new HashMap<Cloudlet, Map<Cloudlet, Double>>();
		begin = new double[cloudletlist.size()];
		end = new double[cloudletlist.size()];
		startTimes = new HashMap<Cloudlet, Double>();
		durationTimes = new HashMap<Cloudlet, Double>();
		//reservationIds = new HashMap<>();
		//vmnotavailable = new HashMap<Vm, List<Slot>>();
		vmparams = new HashMap<Vm, VmParameters>();
		executionTimes = new HashMap<Vm, Double>();
		executionEnergies = new HashMap<Vm, Double>();
                vmallocated=new int[cloudletlist.size()];

	}
	
	public static double getStartTime(Cloudlet cloudlet){
		return  startTimes.get(cloudlet);

	}

	public static double getDurationTime(Cloudlet cloudlet){
		return durationTimes.get(cloudlet);
	}

	public static int getReservationId(Cloudlet cloudlet){
		return reservationIds.get(cloudlet);
	}
	/*
	 * file vmparams.txt saves the informations as:
	 * for each vm:
	 * coefficient \t maxmips \t minmips
	 */

	protected void allocateVmPowerParameters(){
		try{
			File myFile =new File("vmparams.txt");
			BufferedReader reader = new BufferedReader(new FileReader(myFile));
			String line = null;
			for(Object vmObject: vmlist){
				Vm vm = (Vm) vmObject;
				line = reader.readLine();
				String[] result = line.split(" ");
				vmparams.put(vm, new VmParameters(Double.parseDouble(result[0]), Double.parseDouble(result[1]),Double.parseDouble(result[2]),Double.parseDouble(result[3])));
				
                                //vmparams.put(vm, new VmParameters(0.0,0.0,0.0));
				System.out.println(Double.parseDouble(result[0])+","+ Double.parseDouble(result[1])+","+ Double.parseDouble(result[2]));
			}
			//reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*protected double findEnergyConsumption(Vm vm, double time, double slackedmips){
		double executionEnergy = 0.0;
		executionEnergy = vmparams.get(vm).coefficient * Math.pow(slackedmips/1000, 3)* (time/3600);
		return executionEnergy;
	}
	
	protected void findExecutionEnergyConsumption(){
		
		for(Vm vmObject: vmlist){
			Vm vm = (Vm) vmObject;
			double executionEnergy = 0.0;
			double executionTime = 0.0;
			List<Event> ev = schedules.get(vm);
			for(Event eventObject: ev){
				Event event = (Event) eventObject;
				double mips = event.mips;
				executionTime += event.finish - event.start;
				executionEnergy += findEnergyConsumption(vm, event.finish - event.start, mips);
			}
			executionTimes.put(vm, executionTime);
			executionEnergies.put(vm, executionEnergy);
		}
	}*/
        protected double findEnergyConsumption(Vm vm, double time){
		double executionEnergy = 0.0;
                time=time*vmparams.get(vm).CPI;
                
		executionEnergy = vmparams.get(vm).coefficient * Math.pow(1.5, 2)*(time);
		return executionEnergy;
	}
         protected double findEnergyConsumptionidle(Vm vm, double time){
		double idleEnergy = 0.0;
                time=time*vmparams.get(vm).CPI;
		idleEnergy = vmparams.get(vm).coefficient * Math.pow(1, 2)* (time);
		return idleEnergy;
	}
         protected double compute_energy(Vm vm,double voltage,double time)
         {
             double executionEnergy = 0.0;
	     time=time*vmparams.get(vm).CPI;
             executionEnergy = vmparams.get(vm).coefficient * Math.pow(voltage, 2)* (time);
             System.out.println(executionEnergy);
	     return executionEnergy;
         }
         
         protected double compute_idle_energy(Vm vm,double time)
         {
             double idleEnergy = 0.0;
	     time=time*vmparams.get(vm).CPI;
             idleEnergy = vmparams.get(vm).coefficient * Math.pow(1, 2)* (time);
	     return idleEnergy;
         }
         
/*	protected double findTotalEnergyConsumption(){
		double totalEnergy = 0.0;
		for(Vm vmObject: vmlist){
			Vm vm = (Vm) vmObject;
			List<Event> ev = schedules.get(vm);
			//idleTime stores the idle time and the communication time for the vm since 
			//both operate at the lowest voltage level
			double idleTime = 0.0;
			double executionTime = executionTimes.get(vm);
			//double failureTime = findFailureTime(vm);
			//idle time is equal to last task finish time on that vm - (execution and failure time)
			if(ev.isEmpty()){
				idleTime = 0.0;
			}else{
				idleTime =  ev.get(ev.size()-1).finish - (executionTime);
			}
			double idleEnergy = findEnergyConsumption(vm, idleTime*(int)vmparams.get(vm).maxfreq);
			totalEnergy = totalEnergy + (executionEnergies.get(vm)+ idleEnergy);

		}

		return totalEnergy;
	} */

	public double calc_avg_bw(){
		double avg = 0.0;
		for (Object vmObject : vmlist) {
			Vm vm = (Vm) vmObject;
			avg += vm.getBw();
		}
		return avg / vmlist.size();
	}

	public void calc_ComputationCosts() {
		for (Object cloudletObject : Cloudletlist) {
			Cloudlet cloudlet = (Cloudlet) cloudletObject;

			Map<Vm, Double> costsVm = new HashMap<Vm, Double>();
			// System.out.println("\n\nCloudlet: " + cloudlet.getCloudletId() + "  computationCost on vms:");
			for (Object vmObject : vmlist) {
				Vm vm = (Vm) vmObject;
				if (vm.getNumberOfPes() < cloudlet.getNumberOfPes()) {
					costsVm.put(vm, Double.MAX_VALUE);
				} 
				else {
					costsVm.put(vm,
							cloudlet.getCloudletTotalLength()*vmparams.get(vm).CPI / vmparams.get(vm).maxfreq);
				}
				//System.out.print("\tvm: " + vm.getId() + "  " + costsVm.get(vm));
			}
                        for(Vm vm: vmlist)
                        {
                            System.out.print(costsVm.get(vm)+"\t");
                        }
                        System.out.println();
			computationCosts.put(cloudlet, costsVm);
		}
	}

	public void calc_TransferCosts() {
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
                //System.out.println("Transfer costs is");
		// Calculating the actual values
		for (Cloudlet parentObject : Cloudletlist) {
			Cloudlet parent = parentObject;

			for (Cloudlet child : Runner.getChildList(parent)) {

				transferCosts.get(parent).put(child, calculateTransferCost(parent, child));
                                //System.out.print(transferCosts.get(parent).get(child)+" ");
			}
                        //System.out.println();
		}
		
	}

	private double calculateTransferCost(Cloudlet parent, Cloudlet child) {

		double filesize = 0.0;
		//data to be transferred is equal to the output file size of the parent cloudlet in bytes
		filesize = Runner.getCommunication(parent,child);
		//file Size is in  MB
		//filesize = filesize/ 1000000;
		// acc in MB, averageBandwidth in Mb/s
		return (filesize ) / averageBandwidth;
	}

}
