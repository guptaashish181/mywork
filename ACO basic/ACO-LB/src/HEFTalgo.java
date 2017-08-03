//@Nidhi Rehani, nidhirehani@gmail.com, NIT Kurukshetra

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
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

public class HEFTalgo {
	
    
    static int mat[][];
    static Cloudlet[] cloudlet;
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletlist;
	private static int cloudlets;

	/** The vmlist. */
	private static List<Vm> vmlist;
	//private static Cloudlet[] cloudlet = new Cloudlet[];
	
	private static Map<Cloudlet, LinkedList<Cloudlet>> parentlist;
	private static Map<Cloudlet, LinkedList<Cloudlet>> childlist;
	
	
	public static void main(String[] args) {

		Log.printLine("Starting Simulation...");

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
/*
	            	//VM description
	            	int vmid = 0;
	            	int mips = 25;
	            	long size = 10000; //image size (MB)
	            	int ram = 512; //vm memory (MB)
	            	long bw = 1000;
	            	int pesNumber = 1; //number of cpus
	            	String vmm = "Xen"; //VMM name
*/	            	
	            	int i;
	            	BufferedReader br2=new BufferedReader( new InputStreamReader(System.in));
	            	//input the no of virtual machines to be created
	            	int vmno;
	            	vmno = Integer.parseInt(br2.readLine());
	            	System.out.println("Enter the number of virtual machines to be created");
	            	
	            	//vmlist = createVM(brokerId, vmno, 1);//creating the number of virtual machines entered

	            	//LinkedList<Vm> list = new LinkedList<Vm>();
	        		BufferedReader br=new BufferedReader( new InputStreamReader(System.in));
	        		//VM Parameters
	        		long size = 10000; //image size (MB)
	        		int ram = 512; //vm memory (MB)
	        		int mips = 250;
	        		long bw = 1000;
	        		int pesNumber = 1; //number of cpus
	        		String vmm = "Xen"; //VMM name

	        		//create VMs
	        		Vm[] vm = new Vm[vmno];
	        		int idShift = 0;
	        		for(int v=0;v<vmno;v++){
	        			//enter the required mips for the vm
	        			System.out.println("Enter MIPS for the vm");
	        			mips = Integer.parseInt(br.readLine());
	        			vm[v] = new Vm(idShift + v, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
	        			vmlist.add(vm[v]);
	        		}
	        		
	            	int cloudletno;
	            	
	            	System.out.println("Enter the number of cloudlets to be created");
	            	cloudletno = Integer.parseInt(br2.readLine());
	            	cloudlets = cloudletno;
	            	
	            	cloudlet = new Cloudlet[cloudletno];
	            	//cloudlet parameters
	        		long length = 40000;
	        		long fileSize = 300;
	        		long outputSize = 300;
	        		int pesNo = 1;
                                
	        		UtilizationModel utilizationModel = new UtilizationModelFull();
	        		
	        		BufferedReader br1=new BufferedReader( new InputStreamReader(System.in));
	        		

	        		for(int c=0;c<cloudletno;c++){
	        			System.out.println("Enter Cloudlet length");
	        			length = Integer.parseInt(br1.readLine());
	        			System.out.println("Enter Cloudlet Output File Size");
	        			outputSize = Integer.parseInt(br1.readLine());
	        			cloudlet[c] = new Cloudlet(idShift + c, length, pesNo, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	        			// setting the owner of these Cloudlets
	        			cloudlet[c].setUserId(brokerId);
	        			cloudletlist.add(cloudlet[c]);
	        		}
	        		//create precedence relation matrix
	        		mat = new int[cloudletno][cloudletno];
	        		for(int p= 0; p<cloudletno; p++){
	        			for(int q= 0; q<cloudletno; q++){
	        				mat[p][q] = Integer.parseInt(br.readLine());
	        				if(mat[p][q] == 1){
	        					
	        				}
	        			}
	        		}
	        		
	        		
	        		
	            	//cloudletList = createCloudlet(brokerId, cloudletno, 100); // creating the number of cloudlets entered 
	            	/*
	            	vmid++;
	            	mips = 15;
	            	Vm vm2 = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());

	            	vmid++;
	            	mips = 20;
	            	Vm vm3 = new Vm(vmid, brokerId, mips, pesNumber, ram, bw , size, vmm, new CloudletSchedulerSpaceShared());
	            	
	            	//add the VMs to the vmList
	            	vmlist.add(vm1);
	            	vmlist.add(vm2);
	            	vmlist.add(vm3);
*/
	            	//submit vm list to the broker
	            	broker.submitVmList(vmlist);

/*
	            	//Fifth step: Create two Cloudlets
	            	cloudletList = new ArrayList<Cloudlet>();

	            	//Cloudlet properties
	            	int id = 0;
	            	pesNumber=1;
	            	long length = 2500;
	            	long fileSize = 300;
	            	long outputSize = 300;
	            	UtilizationModel utilizationModel = new UtilizationModelFull();

	            	Cloudlet cloudlet0 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet0.setUserId(brokerId);

	            	id++;
	            	length = 2000;
	            	Cloudlet cloudlet1 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet1.setUserId(brokerId);

	            	id++;
	            	length = 1000;
	            	Cloudlet cloudlet2 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet2.setUserId(brokerId);
	            	
	            	id++;
	            	length = 3000;
	            	Cloudlet cloudlet3 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet3.setUserId(brokerId);
	            	
	            	id++;
	            	length = 2300;
	            	Cloudlet cloudlet4 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet4.setUserId(brokerId);
	            	
	            	id++;
	            	length = 2900;
	            	Cloudlet cloudlet5 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet5.setUserId(brokerId);
	            	
	            	id++;
	            	length = 3400;
	            	Cloudlet cloudlet6 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet6.setUserId(brokerId);
	            	
	            	id++;
	            	length = 400;
	            	Cloudlet cloudlet7 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet7.setUserId(brokerId);
	            	
	            	id++;
	            	length = 1400;
	            	Cloudlet cloudlet8 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet8.setUserId(brokerId);
	            	
	            	id++;
	            	length = 5400;
	            	Cloudlet cloudlet9 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
	            	cloudlet9.setUserId(brokerId);
	            	
	            	
	            	//add the cloudlets to the list
	            	cloudletList.add(cloudlet0);
	            	cloudletList.add(cloudlet1);
	            	cloudletList.add(cloudlet2);
	            	cloudletList.add(cloudlet3);
	            	cloudletList.add(cloudlet4);
	            	cloudletList.add(cloudlet5);
	            	cloudletList.add(cloudlet6);
	            	cloudletList.add(cloudlet7);
	            	cloudletList.add(cloudlet8);
	            	cloudletList.add(cloudlet9);
	            	

*/	            	
/*	            	
	            	//specify the parent relationship for each cloudlet to be executed
	            	//ParentList = new HashMap<>();
	            	ArrayList<Cloudlet> plist1 = new ArrayList<Cloudlet>();
	            	//plist.add(null);
	            	ArrayList<Cloudlet> plist = new ArrayList<Cloudlet>();
	            	cloudlet0.setParentList(plist);
	            	
	            	//plist.clear();
	            	plist1.add(cloudlet0);
	            	cloudlet1.setParentList(plist1);
	            	cloudlet2.setParentList(plist1);
	            	cloudlet3.setParentList(plist1);
	            	cloudlet4.setParentList(plist1);
	            	cloudlet5.setParentList(plist1);
	            	
	            	//plist.clear();
	            	ArrayList<Cloudlet> plist2 = new ArrayList<Cloudlet>();
	            	plist2.add(cloudlet2);
	            	cloudlet6.setParentList(plist2);
	            	
	            	ArrayList<Cloudlet> plist3 = new ArrayList<Cloudlet>();
	            	//plist.clear();
	            	plist3.add(cloudlet1);
	            	plist3.add(cloudlet3);
	            	plist3.add(cloudlet5);
	            	cloudlet7.setParentList(plist3);
	            	
	            	//plist.clear();
	            	ArrayList<Cloudlet> plist4 = new ArrayList<Cloudlet>();
	            	plist4.add(cloudlet1);
	            	plist4.add(cloudlet3);
	            	plist4.add(cloudlet4);
	            	cloudlet8.setParentList( plist4);
	            	
	            	//plist.clear();
	            	ArrayList<Cloudlet> plist5 = new ArrayList<Cloudlet>();
	            	plist5.add(cloudlet6);
	            	plist5.add(cloudlet7);
	            	plist5.add(cloudlet8);
	            	cloudlet9.setParentList(plist5);
	            	
	            	//specify the child relationship for each cloudlet to be executed using clist	            	
	            	//ChildList = new HashMap<>();
	            	ArrayList<Cloudlet> clist1 = new ArrayList<Cloudlet>();
	            	//clist.add(null);
	            	ArrayList<Cloudlet> clist = new ArrayList<Cloudlet>();
	            	cloudlet9.setChildList(clist);
	            	
	            	//clist.clear();
	            	clist1.add(cloudlet1);
	            	clist1.add(cloudlet2);
	            	clist1.add(cloudlet3);
	            	clist1.add(cloudlet4);
	            	clist1.add(cloudlet5);
	            	cloudlet0.setChildList(clist1);
	            	
	            	//clist.clear();
	            	ArrayList<Cloudlet> clist2 = new ArrayList<Cloudlet>();
	            	clist2.add(cloudlet6);
	            	cloudlet2.setChildList(clist2);
	            	
	            	//clist.clear();
	            	ArrayList<Cloudlet> clist3 = new ArrayList<Cloudlet>();
	            	clist3.add(cloudlet8);
	            	clist3.add(cloudlet7);
	            	cloudlet3.setChildList(clist3);
	            	cloudlet1.setChildList(clist3);
	            	
	            	//clist.clear();
	            	ArrayList<Cloudlet> clist4 = new ArrayList<Cloudlet>();
	            	clist4.add(cloudlet8);
	            	cloudlet4.setChildList(clist4);
	            	
	            	//clist.clear();
	            	ArrayList<Cloudlet> clist5 = new ArrayList<Cloudlet>();
	            	clist5.add(cloudlet7);
	            	cloudlet5.setChildList(clist5);
	            	
	            	//clist.clear();
	            	ArrayList<Cloudlet> clist6 = new ArrayList<Cloudlet>();
	            	clist6.add(cloudlet9);
	            	cloudlet6.setChildList(clist6);
	            	cloudlet7.setChildList(clist6);
	            	cloudlet8.setChildList(clist6);
	    
	*/            	
	            	
	            	HEFTScheduler hfs = new HEFTScheduler(cloudletlist, vmlist);

	            	
	            	//bind the cloudlets to the vms. This way, the broker
	            	// will submit the bound cloudlets only to the specific VM
	            	//broker.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
	            	//broker.bindCloudletToVm(cloudlet2.getCloudletId(),vm2.getId());

	            	hfs.run();
	            	//submit cloudlet list to the broker
	            	broker.submitCloudletList(cloudletlist);
	            	
	            	
	            	// Sixth step: Starts the simulation
	            	CloudSim.startSimulation();


	            	// Final step: Print results when simulation is over
	            	List<Cloudlet> newList = broker.getCloudletReceivedList();
	            	
	            	CloudSim.stopSimulation();

	            	printCloudletList(newList);

	            	Log.printLine("HEFT Algorithm finished!");
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
		int p = parent.getCloudletId();
		for(int c =0 ; c< cloudlets; c++){
			if( mat[p][c] == 1){
				childlist.add(cloudlet[c]);
			}
		}
		return childlist;
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

    	int mips = 1000;

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
}
