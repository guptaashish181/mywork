package org.workflowsim.examples.planning;





import org.cloudbus.cloudsim.examples.*;
import java.io.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class MaxMinModified {
	/** The cloudlet list. */
	private static ArrayList<Cloudlet> cloudletlist ;
	private static ArrayList<Cloudlet> sortedCloudletlist= new ArrayList<Cloudlet>();
	/** The vmlist. */
	private static ArrayList<Vm> vmlist;

	//private static HashMap<Cloudlet, Boolean> assigned;
	private static List hasChecked = new ArrayList<Boolean>();
	private static List vmforCloudlet = new ArrayList<Integer>();
	private static HashMap<Vm, Double> readyTime = new HashMap();
	private static int vmno, cloudletno;

	public static void main(String[] args) {
        try {
            // TODO Auto-generated method stub
			Log.printLine("Starting Simulation...");

			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create virtual machines
			vmlist = new ArrayList<Vm>();

			BufferedReader br=new BufferedReader( new InputStreamReader(System.in));
			//input the no of virtual machines to be created
			System.out.println("Enter the number of virtual machines to be created");
			vmno = Integer.parseInt(br.readLine());

			//VM Parameters

			long size = 10000; //image size (MB)
			int ram = 512; //vm memory (MB)
			int mips = 2000;
			long bw = 40;
			int pesNumber = 1; //number of cpus
			String vmm = "Xen"; //VMM name

			//create VMs
			Vm[] vm = new Vm[vmno];
			int idShift = 0;
			for(int v=0;v<vmno;v++){
				mips = (int)Math.ceil((1500 + (Math.random()* 3500)));
				System.out.println("Mips of VM " + v + " =  " + mips);
				vm[v] = new Vm(idShift + v, brokerId, mips, pesNumber, ram, (int)(50 + (Math.random()* 100)), size, vmm, new CloudletSchedulerSpaceShared());
				vmlist.add(vm[v]);
			}

			System.out.println("Enter the number of cloudlets to be created");
			cloudletno = Integer.parseInt(br.readLine());

			Cloudlet[] cloudlet = new Cloudlet[cloudletno];
			//cloudlet parameters
			//specifying length, filesieze and outputsize in mips
			long length = 400000;
			long fileSize = 300;
			long outputSize = 3000;
			int pesNo = 1;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			cloudletlist = new ArrayList<Cloudlet>();
			for(int c=0;c<cloudletno;c++){
				//System.out.println("Enter Cloudlet length");
				length = (long)(200000 + (Math.random()*700000));
				System.out.println("length of Cloudlet " + c+ " =  " + length);
				//outputSize = Integer.parseInt(br.readLine());
				cloudlet[c] = new Cloudlet(idShift + c, length, pesNo, fileSize, (long)(150 + (Math.random() * 900)), utilizationModel, utilizationModel, utilizationModel);
				// setting the owner of these Cloudlets
				cloudlet[c].setUserId(brokerId);
				cloudletlist.add(cloudlet[c]);
			}
			//submit vm list to the broker
			broker.submitVmList(vmlist);

			//min-min scheduling procedure
			hasChecked.clear();
			vmforCloudlet.clear();
			for (int t = 0; t < cloudletno; t++) {
				hasChecked.add(false);
				vmforCloudlet.add(0);
			}
			for (int t = 0; t < vmno; t++) {
				Vm v = vmlist.get(t);
				readyTime.put(v, 0.0);
			}
			for (int i = 0; i < cloudletno; i++) {
				int minIndex = 0;
				Cloudlet minCloudlet = null;
				for (int j = 0; j < size; j++) {
					Cloudlet cloud = (Cloudlet) cloudletlist.get(j);
					boolean chk = (Boolean) (hasChecked.get(j));
					if (!chk) {
						minCloudlet = cloud;
						minIndex = j;
						break;
					}
				}
				if (minCloudlet == null) {
					break;
				}


				for (int j = 0; j < cloudletno; j++) {
					Cloudlet cloud = (Cloudlet) cloudletlist.get(j);
					boolean chk = (Boolean) (hasChecked.get(j));

					if (chk) {
						continue;
					}
                                        
					long cloudletlength = cloud.getCloudletLength();

					if (cloudletlength < minCloudlet.getCloudletLength()) {
						minCloudlet = cloud;
						minIndex = j;
					}
				}
				hasChecked.set(minIndex, true);

				Vm assignedVm = null;
				double minFinishTime= Double.MAX_VALUE;
				for(Object vmObject: vmlist){
					Vm v = (Vm) vmObject;
					double finishTime =0.0;

					finishTime = readyTime.get(v)+ minCloudlet.getCloudletLength()/v.getMips();

					if(finishTime < minFinishTime){
						minFinishTime = finishTime;
						assignedVm = v;
					}
				}
				minCloudlet.setVmId(assignedVm.getId());
				//broker.bindCloudletToVm(minCloudlet.getCloudletId(),assignedVm.getId());
				vmforCloudlet.set(minCloudlet.getCloudletId(), assignedVm.getId());
				readyTime.put(assignedVm, minFinishTime);
				System.out.println("Cloudlet " + minCloudlet.getCloudletId() + " bound to Vm " + assignedVm.getId());
				sortedCloudletlist.add(minCloudlet);
			}    
			
			//submit cloudlet list to the broker
			broker.submitCloudletList(sortedCloudletlist);
			
			//bind the cloudlets to the vms. This way, the broker
			// will submit the bound cloudlets only to the specific VM
			for(Object cloudletObject: cloudletlist){
				Cloudlet cloudlet1 = (Cloudlet) cloudletObject;
				broker.bindCloudletToVm(cloudlet1.getCloudletId(),(int)vmforCloudlet.get(cloudlet1.getCloudletId()));
			}
			
			
			
			// Sixth step: Starts the simulation
			CloudSim.startSimulation();


			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			printCloudletList(newList);
		}catch(Exception e){
			e.printStackTrace();
			Log.printLine("Input Exception");
		}
	}


	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		//4. Create Host with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;

		hostList.add(
				new Host(
						hostId,
						new RamProvisionerSimple(ram),
						new BwProvisionerSimple(bw),
						storage,
						peList,
						new VmSchedulerTimeShared(peList)
						)
				); // This is our machine


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}

	}

    MaxMinModified(List<Cloudlet> cluster, List<Vm> vmlist) {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void main() {
        try{
			// TODO Auto-generated method stub
			Log.printLine("Starting Simulation...");

			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create virtual machines
			vmlist = new ArrayList<Vm>();

			BufferedReader br=new BufferedReader( new InputStreamReader(System.in));
			//input the no of virtual machines to be created
			System.out.println("Enter the number of virtual machines to be created");
			vmno = Integer.parseInt(br.readLine());

			//VM Parameters

			long size = 10000; //image size (MB)
			int ram = 512; //vm memory (MB)
			int mips = 2000;
			long bw = 40;
			int pesNumber = 1; //number of cpus
			String vmm = "Xen"; //VMM name

			//create VMs
			Vm[] vm = new Vm[vmno];
			int idShift = 0;
			for(int v=0;v<vmno;v++){
				mips = (int)Math.ceil((1500 + (Math.random()* 3500)));
				System.out.println("Mips of VM " + v + " =  " + mips);
				vm[v] = new Vm(idShift + v, brokerId, mips, pesNumber, ram, (int)(50 + (Math.random()* 100)), size, vmm, new CloudletSchedulerSpaceShared());
				vmlist.add(vm[v]);
			}

			System.out.println("Enter the number of cloudlets to be created");
			cloudletno = Integer.parseInt(br.readLine());

			Cloudlet[] cloudlet = new Cloudlet[cloudletno];
			//cloudlet parameters
			//specifying length, filesieze and outputsize in mips
			long length = 400000;
			long fileSize = 300;
			long outputSize = 3000;
			int pesNo = 1;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			cloudletlist = new ArrayList<Cloudlet>();
			for(int c=0;c<cloudletno;c++){
				//System.out.println("Enter Cloudlet length");
				length = (long)(200000 + (Math.random()*700000));
				System.out.println("length of Cloudlet " + c+ " =  " + length);
				//outputSize = Integer.parseInt(br.readLine());
				cloudlet[c] = new Cloudlet(idShift + c, length, pesNo, fileSize, (long)(150 + (Math.random() * 900)), utilizationModel, utilizationModel, utilizationModel);
				// setting the owner of these Cloudlets
				cloudlet[c].setUserId(brokerId);
				cloudletlist.add(cloudlet[c]);
			}
			//submit vm list to the broker
			broker.submitVmList(vmlist);

			//min-min scheduling procedure
			hasChecked.clear();
			vmforCloudlet.clear();
			for (int t = 0; t < cloudletno; t++) {
				hasChecked.add(false);
				vmforCloudlet.add(0);
			}
			for (int t = 0; t < vmno; t++) {
				Vm v = vmlist.get(t);
				readyTime.put(v, 0.0);
			}
			for (int i = 0; i < cloudletno; i++) {
				int minIndex = 0;
				Cloudlet minCloudlet = null;
				for (int j = 0; j < size; j++) {
					Cloudlet cloud = (Cloudlet) cloudletlist.get(j);
					boolean chk = (Boolean) (hasChecked.get(j));
					if (!chk) {
						minCloudlet = cloud;
						minIndex = j;
						break;
					}
				}
				if (minCloudlet == null) {
					break;
				}


				for (int j = 0; j < cloudletno; j++) {
					Cloudlet cloud = (Cloudlet) cloudletlist.get(j);
					boolean chk = (Boolean) (hasChecked.get(j));

					if (chk) {
						continue;
					}

					long cloudletlength = cloud.getCloudletLength();

					if (cloudletlength < minCloudlet.getCloudletLength()) {
						minCloudlet = cloud;
						minIndex = j;
					}
				}
				hasChecked.set(minIndex, true);

				Vm assignedVm = null;
				double minFinishTime= Double.MAX_VALUE;
				for(Object vmObject: vmlist){
					Vm v = (Vm) vmObject;
					double finishTime =0.0;

					finishTime = readyTime.get(v)+ minCloudlet.getCloudletLength()/v.getMips();

					if(finishTime < minFinishTime){
						minFinishTime = finishTime;
						assignedVm = v;
					}
				}
				minCloudlet.setVmId(assignedVm.getId());
				//broker.bindCloudletToVm(minCloudlet.getCloudletId(),assignedVm.getId());
				vmforCloudlet.set(minCloudlet.getCloudletId(), assignedVm.getId());
				readyTime.put(assignedVm, minFinishTime);
				System.out.println("Cloudlet " + minCloudlet.getCloudletId() + " bound to Vm " + assignedVm.getId());
				sortedCloudletlist.add(minCloudlet);
			}    
			
			//submit cloudlet list to the broker
			broker.submitCloudletList(sortedCloudletlist);
			
			//bind the cloudlets to the vms. This way, the broker
			// will submit the bound cloudlets only to the specific VM
			for(Object cloudletObject: cloudletlist){
				Cloudlet cloudlet1 = (Cloudlet) cloudletObject;
				broker.bindCloudletToVm(cloudlet1.getCloudletId(),(int)vmforCloudlet.get(cloudlet1.getCloudletId()));
			}
			
			
			
			// Sixth step: Starts the simulation
			CloudSim.startSimulation();


			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			printCloudletList(newList);
		}catch(Exception e){
			e.printStackTrace();
			Log.printLine("Input Exception");
		}
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
