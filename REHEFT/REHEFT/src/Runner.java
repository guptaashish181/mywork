package org.workflowsim.examples.planning;

import java.io.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.Math;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;


import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class Runner {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletlist;
	
	/** The vmlist. */
	private static List<Vm> vmlist;
	
	private static Map<Cloudlet, LinkedList<Cloudlet>> parentlist;
	private static Map<Cloudlet, LinkedList<Cloudlet>> childlist;
	
	private static int cloudlets;
	private static int vmno;
	private static int vmmips[];
        
	//specify computation requirement for each task
	private static int arrlength[];
        
	private static int arroutputsize[];
	
	private static double mat[][];
	public static int getVmMips(Vm vm){
		int id = vm.getId();
		return vmmips[id];
	}
        class Result{
            int cloudletId,vmId;
            double startTime,finishTime,execTime;
            Result(int cloudletId,int vmId,double startTime,double finishTime,double execTime)
            {
                this.cloudletId=cloudletId;
                this.vmId=vmId;
                this.startTime=startTime;
                this.finishTime=finishTime;
                this.execTime=execTime;
            }
            int getCloudletId()
            {
                return this.cloudletId;
            }
            int getvmId()
            {
                return this.vmId;
            }
            double getStartTime()
            {
                return this.startTime;
            }
            double getFinishTime()
            {
                return this.finishTime;
            }
            double getExecTime()
            {
                return this.execTime;
            }
        }
        private static List<Result> Results;
	/*file workflow.txt contains the following information about the workflow to be executed, 
	 * in the specified format:
	 * number of tasks
	 * number of virtual machines
	 * mean MIPS value for each virtual machine, separated by \t
	 * average computation cost for each task (MI value), separated by \t
	 * precedence matrix for the workflow (matrix to represent task dependencies) 	
	*/	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Log.printLine("Starting Simulation...");

        try {
        	//initialise the variables by reading them from the file
                  
        	File myFile =new File("example1.txt");
                System.out.println(myFile.getAbsolutePath());
        	BufferedReader reader = new BufferedReader(new FileReader(myFile));
                 
        	String line = null; 
        	line = reader.readLine();
        	cloudlets = Integer.parseInt(line);
        	line = reader.readLine();
        	vmno = Integer.parseInt(line);
                //Log.printLine("Number of cloudlets"+cloudlets+"NUMBER OF VMS "+vmno);
        	vmmips = new int[vmno];
        	line = reader.readLine();
        	String[] result = line.split(" ");
                /*for(int i =0; i<result.length; i++){
        		vmmips[i] = Integer.parseInt(result[i]);
        	}
        	line = reader.readLine();
        	result = line.split(" ");*/
        	arrlength = new int[cloudlets];
        	arroutputsize = new int[cloudlets];
        	for(int i =0; i<result.length; i++){
        		arrlength[i] = Integer.parseInt(result[i]);
        	}
        	mat = new double[100][100];
                System.out.println(cloudlets);
        	int index =0;
        	while((line = reader.readLine()) != null){
        		
        		result = line.split(" ");
                        System.out.println(Arrays.toString(result));
                        System.out.println(result.length);
        		for(int j=0; j < result.length ; j++){
                            
        			mat[index][j]= Double.parseDouble(result[j]);
        		}
        		index++;
        	}
        	
        	// First step: Initialize the CloudSim package. It should be called
            	// before creating any entities.
            	int num_user = 1;   // number of cloud users
            	Calendar calendar = Calendar.getInstance();
            	boolean trace_flag = false;  // mean trace events

            	// Initialize the CloudSim library
            	CloudSim.init(num_user, calendar, trace_flag);

            	// Second step: Create Datacenters
            	//Datacenters are the resource providers in CloudSim. We need atleast one of them to run a CloudSim simulation
            	@SuppressWarnings("unused")
				Datacenter datacenter0 = createDatacenter("Datacenter_0");

            	//Third step: Create Broker
            	DatacenterBroker broker = createBroker();
            	int brokerId = broker.getId();

            	//Fourth step: Create one virtual machine
            	vmlist = new ArrayList<Vm>();
            	
            	int i;
            	BufferedReader br=new BufferedReader( new InputStreamReader(System.in));
            	//input the no of virtual machines to be created
            	
            	//VM Parameters
        		
                long size = 10000; //image size (MB)
                int ram = 512; //vm memory (MB)
        	int mips = 1000;
        	long bw = 1;// in mbps
        	int pesNumber = 1; //number of cpus
        	String vmm = "Xen"; //VMM name

        	//create VMs
        	Vm[] vm = new Vm[vmno+1];
        	int idShift = 0;
                File myvmFile =new File("vmparams.txt");
                BufferedReader vmreader = new BufferedReader(new FileReader(myvmFile));
		String vmline = null;
			vmline = vmreader.readLine();
                         String[] vmresult;
                         System.out.println(vmno);
        	for(int v=0;v<vmno;v++)
                {
        		//enter the required mips for the vm
        		//System.out.println("Enter MIPS for the vm");
        		//mips = Integer.parseInt(br.readLine());
        		//mips = vmmips[v];
                         vmresult = vmline.split(" ");
                        System.out.println(Arrays.toString(vmresult));
                        
                        mips= (500+ mips)/2;
                        System.out.println(mips);
        		vm[v] = new Vm(idShift + v, brokerId, mips, pesNumber, ram, /*(int)(50 + (Math.random()* 100))*/bw, size, vmm, new CloudletSchedulerSpaceShared());
        		vmlist.add(vm[v]);
        	}
        		
            	int cloudletno  = cloudlets;
            	
      
            	Cloudlet[] cloudlet = new Cloudlet[cloudletno];
            	//cloudlet parameters
            	//specifying length, filesieze and outputsize in mips
                idShift = 0;	
                long length = 40;
                long fileSize = (long)(1500 + (Math.random()*7000));
        	long outputSize = 3000;
        	int pesNo = 1;
        	UtilizationModel utilizationModel = new UtilizationModelFull();
        		
        	//BufferedReader br1=new BufferedReader( new InputStreamReader(System.in));
        		
        	cloudletlist = new ArrayList<Cloudlet>();
        	for(int c=0;c<cloudletno;c++){
        		//System.out.println("Enter Cloudlet length");
        		//length = Integer.parseInt(br1.readLine());
        		//System.out.println("Enter Cloudlet Output File Size");
        		//outputSize = Integer.parseInt(br1.readLine());
        		length = arrlength[c];
        		//outputSize = arroutputsize[c];
        		cloudlet[c] = new Cloudlet(idShift + c, length, pesNo, fileSize, (long)(1500 + (Math.random() * 9000)), utilizationModel, utilizationModel, utilizationModel);
        		// setting the owner of these Cloudlets
        		cloudlet[c].setUserId(brokerId);
        		cloudletlist.add(cloudlet[c]);
        	}
        		
      
            	//submit vm list to the broker
            	broker.submitVmList(vmlist);
            	
            	DETSScheduler dets = new DETSScheduler(cloudletlist, vmlist);
            	dets.run();
            	//bind the cloudlets to the vms. This way, the broker
            	// will submit the bound cloudlets only to the specific VM
            	//broker.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
            	//broker.bindCloudletToVm(cloudlet2.getCloudletId(),vm2.getId());

            	//submit cloudlet list to the broker
            	broker.submitCloudletList(cloudletlist);
            	//create scheduler object
            	
            	
            	// Sixth step: Starts the simulation
            	CloudSim.startSimulation();


            	// Final step: Print results when simulation is over
            	List<Cloudlet> newList = broker.getCloudletReceivedList();
            	Log.printLine("RECEIVRED list size "+newList.size());
            	CloudSim.stopSimulation();

            	printCloudletList(newList);

            	Log.printLine(" Algorithm finished!");
            	reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

public List<Vm> getvmlist(){
	return vmlist;
}


public List<Cloudlet> getcloudletList(){
	return cloudletlist;
}
public static ArrayList<Cloudlet> getChildList(Cloudlet parent){
	ArrayList<Cloudlet> childlist = new ArrayList<Cloudlet>();
	//childlist = null;
	int p = parent.getCloudletId();
	for(int c =0 ; c< cloudlets; c++){
		if( mat[p][c]!=0){
			childlist.add(cloudletlist.get(c));
		}
	}
	return childlist;
}
public static double getCommunication(Cloudlet parent,Cloudlet child)
{
    return mat[parent.getCloudletId()][child.getCloudletId()];
}
public static ArrayList<Cloudlet> getParentList(Cloudlet cloudlet){
	ArrayList<Cloudlet> parentlist = new ArrayList<Cloudlet>();
	//parentlist = null;
	int c = cloudlet.getCloudletId();
	for(int p =0 ; p < cloudlets; p++){
		if( mat[p][c] != 0){
			parentlist.add(cloudletlist.get(p));
		}
	}
	return parentlist;
}

private static Datacenter createDatacenter(String name){

  // Here are the steps needed to create a PowerDatacenter:
  // 1. We need to create a list to store
	//    our machine
	List<Host> hostList = new ArrayList<Host>();

  // 2. A Machine contains one or more PEs or CPUs/Cores.
	// In this example, it will have only one core.
	List<Pe> peList = new ArrayList<Pe>();

	int mips = 1200;

  // 3. Create PEs and add these into a list.
	peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
        peList.add(new Pe(1, new PeProvisionerSimple(mips)));
        //peList.add(new Pe(2, new PeProvisionerSimple(mips)));
  //4. Create Host with its id and list of PEs and add them to the list of machines
        int hostId=0;
        int ram = 2048; //host memory (MB)
        long storage = 1000000; //host storage
        int bw = 1;

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
        hostId=1;
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
  try 
  {
	broker = new DatacenterBroker("Broker");
  } 
  catch (Exception e)
  {
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

  DecimalFormat dft = new DecimalFormat("##0.00");
  DecimalFormat dft2 = new DecimalFormat("######");
  for (int i = 0; i < size; i++) 
  {
      cloudlet = list.get(i);
      Log.print(indent + cloudlet.getCloudletId() + indent + indent);

      if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS)
      {
            Log.print(String.format( "%5s","SUCCESS"));

            Log.printLine( indent + indent + dft2.format(cloudlet.getResourceId()) + indent + indent + indent + cloudlet.getVmId() +
               indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
                   indent + indent + dft.format(cloudlet.getFinishTime()));
      }
  }

}

	}


