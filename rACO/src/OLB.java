//@Ashish gupta  National Institute of Technology, Kurukshetra
//OPPORTUNISTIC load Balancing


/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */



import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


/**
 * A simple example showing how to create
 * a datacenter with one host and run two
 * cloudlets on it. The cloudlets run in
 * VMs with the same MIPS requirements.
 * The cloudlets will take the same time to
 * complete the execution.
 */
public class OLB {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {

		Log.printLine("Starting CloudSimExample2...");
                
                Scanner sc= new Scanner(System.in);
        
         int noOfTasks ;
         System.out.println("\nEnter the NO. of task :\n");
         noOfTasks=sc.nextInt();
         int noOfProcessors ;
        System.out.println("\nEnter the NO. of Processor :\n");
             noOfProcessors=sc.nextInt();
        
        // double[] computationCost = new double[noOfProcessors];
          System.out.println("\nEnter the Computation Cost of Processors :\n");
         double[] computationCost =new double [noOfProcessors];
         for(int i=0;i<noOfProcessors;i++)
         {
         computationCost[i]=sc.nextDouble();
         }       
                

	        try {
	        	// First step: Initialize the CloudSim package. It should be called
	            	// before creating any entities.
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

	            	//Fourth step: Create one virtual machine
	            	vmlist = new ArrayList<Vm>();

                        int vms = 8;
                        int cloudlets = 20;
                        
                        
                        
        int [] mi = {

}; 
        
        int [] mips = {2,
3,
5,
2,
1,
5,
6,
7,
6,
5,
4,
6,
8,
9,
6,
4,
3,
5,
6,
7,
5,
4,
3,
5,
7,
4,
7,
9,
8,
7,
8,
4};
   int n=mi.length;
        int m=mips.length;
        System.out.println("\nTask Length:\t"+mi.length);
        System.out.println("\nMachine Length:\t"+mips.length);

        double [][] c= new double[n][m];
        
        double [][] et= new double[n][m];
        
            for(int i=0;i<n;i++){
                for(int j=0;j<m;j++) {
                    c[i][j]=(double)mi[i]/mips[j];
                    et[i][j]=c[i][j];
                }
            }        
                        
                        
                        
                        
                        
	            	//VM description
	        int vmid = 0;
	        long size = 1000; //image size (MB)
		int ram = 512; //vm memory (MB)
		//int mips = 250;
		long bw = 1000;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name

                        int jk=1;
	            	//create two VMs
                        
                        Vm vm[]=new Vm[vms];
	            	
                        for(int i=0; i<vms;i++)
                        {
                        if (i%2==0 )
				mips += jk;
			else 
				mips -= jk;
                            vm[i] = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
                            vmid++;
	            	jk+=2;
	            	vmlist.add(vm[i]);
	            	
                        }
                        vmid--;
	            	//add the VMs to the vmList
	            	//vmlist.add(vm[1]);
                        

	            	//submit vm list to the broker
	            	broker.submitVmList(vmlist);


	            	//Fifth step: Create two Cloudlets
	            	cloudletList = new ArrayList<Cloudlet>();

                        
                                               
	            	//Cloudlet properties
	            	int id = 0;
	            	long length = 40;
		long fileSize = 300;
		long outputSize = 300;
		
	            	UtilizationModel utilizationModel = new UtilizationModelFull();

                        Cloudlet[] cloudlet = new Cloudlet[cloudlets];
                        
                        for(int i=0;i<cloudlets;i++){
			if (i%2==0 || i<2)
				length +=  47;
			else 
				length -=  33;
			
			cloudlet[i] = new Cloudlet(++id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(brokerId);
			cloudletList.add(cloudlet[i]);
		}
                        
                        
	            	
	            	//submit cloudlet list to the broker
	            	broker.submitCloudletList(cloudletList);


	            	//bind the cloudlets to the vms. This way, the broker
	            	// will submit the bound cloudlets only to the specific VM
	            	
                        //for minimum completion time, counter flag variables for vms
                        long cloudletsize[] = new long[vmid+2];
                        for(int i = 0; i<=vmid; i++){
                            cloudletsize[i]+=cloudlet[i].getCloudletTotalLength();
                        //Log.printLine("jkhgjghjhgjgjhgj" +cloudlet[i].getCloudletTotalLength() );
                        broker.bindCloudletToVm(cloudlet[i].getCloudletId(),vm[i].getId());
                        }
                        
                        long least=0; int min=0;
                        
                        for (int i= vmid+1;i<cloudlets;i++)
                        {
                            least = cloudletsize[0];
                            for(int j=0;j<=vmid;j++)
                            {
                                if (least>=cloudletsize[j])
                                { min=j; least=cloudletsize[j];}   
                            }
                            //Log.printLine("jkhgjghjhgjgjhgj" +cloudlet[i].getCloudletTotalLength() );
                            broker.bindCloudletToVm(cloudlet[i].getCloudletId(),vm[min].getId());
                            cloudletsize[min]+=cloudlet[i].getCloudletTotalLength();
                        }
                        //broker.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
	            	//broker.bindCloudletToVm(cloudlet2.getCloudletId(),vm2.getId());

	            	// Sixth step: Starts the simulation
	            	CloudSim.startSimulation();


	            	// Final step: Print results when simulation is over
	            	List<Cloudlet> newList = broker.getCloudletReceivedList();

	            	CloudSim.stopSimulation();

	            	printCloudletList(newList);

	            	Log.printLine("CloudSimExample2 finished!");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            Log.printLine("The simulation has been terminated due to an unexpected error");
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

	    	int mips = 102400;

	        // 3. Create PEs and add these into a list.
	    	peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

	        //4. Create Host with its id and list of PEs and add them to the list of machines
	        int hostId=0;
		int ram = 102400; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 200000;

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
            
}
