
package org.workflowsim.examples.planning;

//import static Algorithm.BaseCloudletScheduler.vmparams;
import org.cloudbus.cloudsim.examples.*;
import org.workflowsim.examples.planning.HEFTalgo1;
import org.workflowsim.examples.planning.Runner;
import java.io.*;
import java.util.Arrays;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 *
 * @author Neha
 */
public class EESA extends BaseCloudletScheduler{
        private static ArrayList<Cloudlet> cloudletlist ;
	private static ArrayList<Cloudlet> sortedCloudletlist= new ArrayList<Cloudlet>();
	/** The vmlist. */
        
	private static ArrayList<Vm> vmlist;
        private static HashMap<Cloudlet, Boolean> assigned;
	private static List hasChecked = new ArrayList<Boolean>();
	private static List vmforCloudlet = new ArrayList<Integer>();
	private static HashMap<Vm, Double> readyTime = new HashMap();
        private static HashMap<Vm, Double> rankval = new HashMap();
        private static HashMap<Cloudlet, Double> priority = new HashMap();
        private Map<Cloudlet, Double> rank;
        protected Map<Vm, List<Cloudlet>> originalschedules;
        private Map<Vm, List<Event>> schedules;
        protected Map<Cloudlet, Double> earliestFinishTimes;
        private static long tp;
        private static HashMap<Vm, VmParameters> vmparams = new HashMap();
        private static double i=0;
	private static int vmno, cloudletno, h , vmmips[];
        private static int arrlength[];
        private static int arroutputsize[];
	private static double mat[][], makespan , makespannew ,alpha=2, D;
        private static  List<Host> hostList = new ArrayList<Host>();
        private static List<Pe> peList = new ArrayList<Pe>();
        private static Map<Cloudlet, LinkedList<Cloudlet>> parentlist;
	private static Map<Cloudlet, LinkedList<Cloudlet>> childlist;
    public EESA(List<Cloudlet> cloudletlist, List<Vm> vmlist) {
        super(cloudletlist, vmlist);
        this.Cloudletlist = cloudletlist;
		
	computationCosts = new HashMap<>();
        transferCosts = new HashMap<>();
        rank = new HashMap<>();
        earliestFinishTimes = new HashMap<>();
        schedules = new HashMap<>();
        //rank = new HashMap<Cloudlet, Double>();
        
          //originalschedules = new HashMap<Vm, List<Cloudlet>>();
        //schedules = new HashMap<Vm, List<Cloudlet>>();
    }

  public static ArrayList<Cloudlet> getParentList(Cloudlet cloudlet){
	ArrayList<Cloudlet> parentlist = new ArrayList<Cloudlet>();
	//parentlist = null;
	int c = cloudlet.getCloudletId();
	for(int p =0 ; p < cloudletno; p++){
		if( mat[p][c] != 0){
			parentlist.add(cloudletlist.get(p));
		}
	}
	return parentlist;
}
  public static ArrayList<Cloudlet> getChildList(Cloudlet parent){
	ArrayList<Cloudlet> childlist = new ArrayList<Cloudlet>();
	//childlist = null;
	int p = parent.getCloudletId();
	for(int c =0 ; c< cloudletno; c++){
		if( mat[p][c]!=0){
			childlist.add(cloudletlist.get(c));
		}
	}
	return childlist;
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
    
   

   private class Event {

        public double start;
        public double finish;

        public Event(double start, double finish) {
            this.start = start;
            this.finish = finish;
        }
    }

    

   

   /* private EESA() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/
        
        
       public class VmParameters {
		public double coefficient;
		public int maxfreq;
		//public double minfreq;
                public double CPI;
		
		public VmParameters(double coefficient,int maxfreq,double CPI){
			this.coefficient = coefficient;
			//this.maxMIPS = maxMIPS;
			//this.minMIPS = minMIPS;
                        this.CPI=CPI;
                    //this.coefficient = 2.0;
                    this.maxfreq = maxfreq;
                     //  this.minMIPS =1.05;
                    //this.CPI=2.0;
		}
	}

      
        protected void allocateVmPowerParameters(){
		try{
			File myFile =new File("vmparams.txt");
			BufferedReader reader = new BufferedReader(new FileReader(myFile));
			String line = null;
			for(Object vmObject: vmlist){
				Vm vm = (Vm) vmObject;
				line = reader.readLine();
				String[] result = line.split(" ");
				vmparams.put(vm, new VmParameters(Double.parseDouble(result[0]), Integer.parseInt(result[1]), Double.parseDouble(result[2])));
				
                                //vmparams.put(vm, new VmParameters(0.0,0.0,0.0));
				System.out.println("Parameters"+Double.parseDouble(result[0])+","+ Double.parseDouble(result[1])+","+ Double.parseDouble(result[2]));
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
        }
	public  static void main(String[] args) {
        try {
             // TODO Auto-generated method stub
			Log.printLine("Starting Simulation...");
                  File myFile =new File("example3.txt");
        	BufferedReader reader = new BufferedReader(new FileReader(myFile));
        	String line = null; 
        	line = reader.readLine();
        	cloudletno = Integer.parseInt(line);
        	line = reader.readLine();
        	vmno = Integer.parseInt(line);
                vmmips = new int[vmno];
        	line = reader.readLine();
        	String[] result = line.split(" ");
                arrlength = new int[cloudletno];
        	arroutputsize = new int[cloudletno];
        	for(int i =0; i<result.length; i++){
        		arrlength[i] = Integer.parseInt(result[i]);
        	}
        	mat = new double[cloudletno+1][cloudletno+1];
        	int index =0;
        	while((line = reader.readLine()) != null){
        		
        		result = line.split(" ");
        		for(int j=0; j<result.length; j++){
        			mat[index][j]= Double.parseDouble(result[j]);
        		}
        		index++;
        	}
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events
                        // Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);
                           @SuppressWarnings("unused")
			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			/*BufferedReader b=new BufferedReader( new InputStreamReader(System.in));
                        System.out.println("Enter the number of host");
                        h = Integer.parseInt(b.readLine());
                        for(int j=0; j<h ;j++) 
                        {
			Datacenter datacenter = createDatacenter("Datacenter_"+i);
                        }*/
                        Datacenter datacenter0 = createDatacenter("Datacenter_0");
                        Datacenter datacenter1 = createDatacenter("Datacenter_1");
			//Third step: Create Broker

                        DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create virtual machines
			vmlist = new ArrayList<Vm>();
                        BufferedReader br=new BufferedReader( new InputStreamReader(System.in));
			//input the no of virtual machines to be created
			//System.out.println("Enter the number of virtual machines to be created");
			//vmno = Integer.parseInt(br.readLine());

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
                File myvmFile =new File("vmparams.txt");
                BufferedReader vmreader = new BufferedReader(new FileReader(myvmFile));
		String vmline = null;
			vmline = vmreader.readLine();
                         String[] vmresult;
        	for(int v=0;v<vmno;v++)
                {
        		//enter the required mips for the vm
        		//System.out.println("Enter MIPS for the vm");
        		//mips = Integer.parseInt(br.readLine());
        		//mips = vmmips[v];
                        
                       vmresult = vmline.split(" ");
                        System.out.println(Arrays.toString(vmresult));
                       mips= (500+ mips)/2;
        		vm[v] = new Vm(idShift + v, brokerId, mips, pesNumber, ram, /*(int)(50 + (Math.random()* 100))*/bw, size, vmm, new CloudletSchedulerSpaceShared());
        		vmlist.add(vm[v]);
        	}

			//System.out.println("Enter the number of cloudlets to be created");
			//cloudletno = Integer.parseInt(br.readLine());

			Cloudlet[] cloudlet = new Cloudlet[cloudletno];
			//cloudlet parameters
			//specifying length, filesieze and outputsize in mips
			long length = 40000;
			long fileSize = 300;
			long outputSize = 3000;
			int pesNo = 1;
                       UtilizationModel utilizationModel = new UtilizationModelFull();

			cloudletlist = new ArrayList<Cloudlet>();
			for(int c=0;c<cloudletno;c++){
				//System.out.println("Enter Cloudlet length");
				length = (long)(2000 + (Math.random()*7000));
				System.out.println("length of Cloudlet " + c+ " =  " + length);
				//outputSize = Integer.parseInt(br.readLine());
				cloudlet[c] = new Cloudlet(idShift + c, length, pesNo, fileSize, (long)(150 + (Math.random() * 900)), utilizationModel, utilizationModel, utilizationModel);
				// setting the owner of these Cloudlets
				cloudlet[c].setUserId(brokerId);
				cloudletlist.add(cloudlet[c]);
			}
			//submit vm list to the broker
			broker.submitVmList(vmlist);
                        //scheduling procedure
                        
                        //DETSScheduler dets = new DETSScheduler(cloudletlist, vmlist);
            	//dets.run();
                        EESA esa = new EESA(cloudletlist, vmlist);
                        esa.allocateVmPowerParameters();
                         esa.calc_ComputationCosts();
                        esa.calculateRanks();
                         esa.allocateCloudlets();
                         esa.merging();
                      //  esa.run();
                        
                        
                      // esa.calc_ComputationCosts();
                       // esa.calculateRanks();
                       
                        
                      
                        
                       /*  for(Cloudlet L : cloudletlist )
                        {
                            joblevel.put(L,i);   
                            System.out.println(joblevel.get(L));
                            i++;
                        }
                     
                  //    Arrays.sort(cloudletlist);
                       double total_energy = 0.0;
                        for(int i=0;i<cloudletno;i++)
                        {
                           Cloudlet cloud = (Cloudlet) cloudletlist.get(i);
                              
					boolean chk = (Boolean) (hasChecked.get(i));
                                        if (chk) {
						continue;
					}
                   long cloudletlength = cloud.getCloudletLength();
                       
                       System.out.println(cloudletlength);
                    Vm assignedVm = null;
				double minFinishTime= Double.MAX_VALUE;
				for(Object vmObject: vmlist){
					Vm v = (Vm) vmObject;
					double finishTime =0.0;

					finishTime = readyTime.get(v)+ cloudletlength/v.getMips();

					if(finishTime < minFinishTime){
						minFinishTime = finishTime;
						assignedVm = v;
                                                total_energy+= findEnergyConsumption(cloudletlength);
					}
				}
                       
                        cloud.setVmId(assignedVm.getId());
                        
				//broker.bindCloudletToVm(cloud.getCloudletId(),assignedVm.getId());
				vmforCloudlet.set(cloud.getCloudletId(), assignedVm.getId());
				readyTime.put(assignedVm, minFinishTime);
				System.out.println("Cloudlet " + cloud.getCloudletId() + " bound to Vm " + assignedVm.getId());
				sortedCloudletlist.add(cloud);
                        }*/  //broker.submitCloudletList(cloudletlist);
	            	
	            	
	            	
	            


	                	
	            	
	            	

	            	

	            	
                        
                        
                       /*      broker.submitCloudletList(sortedCloudletlist);
			
			//bind the cloudlets to the vms. This way, the broker
			// will submit the bound cloudlets only to the specific VM
			for(Object cloudletObject: cloudletlist){
				Cloudlet cloudlet1 = (Cloudlet) cloudletObject;
				broker.bindCloudletToVm(cloudlet1.getCloudletId(),(int)vmforCloudlet.get(cloudlet1.getCloudletId()));
			}
                        // Sixth step: Starts the simulation
			CloudSim.startSimulation();


			// Final step: Print results when simulation is over
                 //       printCloudletList(newList);
			List<Cloudlet> newList = broker.getCloudletReceivedList();
 //                      System.out.println("Total energy " + total_energy );
			CloudSim.stopSimulation();

			printCloudletList(newList);
                        */
                       
        }
        catch(Exception e){
			e.printStackTrace();
			Log.printLine("Input Exception");
		}
        }
        
         static double  findEnergyConsumption( long time){
		double executionEnergy = 0.0;
                time=time*2;
                
		executionEnergy = 2.4 * Math.pow(1.5, 2)*(time);
                
                return executionEnergy;
		
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
                            System.out.print("Computational cost" +costsVm.get(vm)+"\t");
                        }
                        System.out.println();
			computationCosts.put(cloudlet, costsVm);
		}
	}
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
    
     
     
 private double findFinishTime(Cloudlet cloudlet, Vm vm, double readyTime, boolean occupySlot) {
          
        List<Event> sched = schedules.get(vm);
        double computationCost = computationCosts.get(cloudlet).get(vm);
        double start, finish;
        int pos;
        
        if (sched.isEmpty()) {
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
      
      
      
      
       private void allocateCloudlets() {
        List<CloudletRank> cloudletRank = new ArrayList<>();
        for (Cloudlet cloudlet : rank.keySet()) {
            cloudletRank.add(new CloudletRank(cloudlet, rank.get(cloudlet)));
          //  System.out.println("vh  "+ rank.get(cloudlet));
        }

        // Sorting in non-ascending order of rank
        Collections.sort(cloudletRank);
        for (CloudletRank cr : cloudletRank) {
          //  System.out.println("vh  "+ cr.cloudlet);
            allocateCloudlet(cr.cloudlet);
        }

    }
      
      
      
      
      
      
     private void allocateCloudlet(Cloudlet cloudlet) {
         try{
             //System.out.println("vh  "+ cloudlet);
        
         Vm chosenvm = null;
        double earliestFinishTime = Double.MAX_VALUE;
       
        double bestReadyTime = 0.0;
        double finishTime;
        makespan=0;

        for (Object vmObject : vmlist) {
            Vm vm = (Vm) vmObject;
            double minReadyTime = 0.0;

           for (Cloudlet parent : getParentList(cloudlet)) {
               
               double readyTime = parent.getExecStartTime();
                System.out.println(readyTime);
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
            makespan = makespan + finishTime ;
            earliestFinishTimes.put(cloudlet, earliestFinishTime);

        cloudlet.setVmId(chosenvm.getId());
        vmforCloudlet.set(cloudlet.getCloudletId(), chosenvm.getId());
				readyTime.put(chosenvm, earliestFinishTime);
				System.out.println("Cloudlet " + cloudlet.getCloudletId() + " bound to Vm " + chosenvm.getId());
       sortedCloudletlist.add(cloudlet);
        }

        
        D = makespan *(1+ alpha);
                                
				//broker.bindCloudletToVm(cloud.getCloudletId(),assignedVm.getId());
				
				
                        
                         //    broker.submitCloudletList(sortedCloudletlist);
            
        
       
           
            }
       
           
       
       catch(Exception e){
			e.printStackTrace();
			Log.printLine("Input Exception");
		}
        
    }
     private void merging()
     {
         makespannew = makespan;
         
         for (Cloudlet cloud : Cloudletlist) 
         {
          rank.put(cloud, 0.0);
         }
        /* for( Pe p: peList)
         {
             p.getStatus();
            
         }*/
         
         while( makespannew < D )
         {
         for(Cloudlet cloud : Cloudletlist)
         {
              rank.put(cloud, (double)cloud.getNumberOfPes() );  
         }
         for(Vm v : vmlist)
         {
             for(Cloudlet cloud : Cloudletlist)
             {
                 if(v.equals(cloud.getVmId()))
                 {
                     tp  = tp + cloud.getCloudletLength();
                     rank.put(cloud, rank.get(v)+1 );
                 }
             }
         }
         List<CloudletRank> cloudletRank = new ArrayList<>();
        for (Cloudlet cloudlet : rank.keySet()) {
            cloudletRank.add(new CloudletRank(cloudlet, rank.get(cloudlet)));
          //  System.out.println("vh  "+ rank.get(cloudlet));
        }

        // Sorting in non-ascending order of rank
        Collections.sort(cloudletRank);
        
         for(Cloudlet cloud : Cloudletlist)
         {
         allocateCloudlet(cloud);
         
         }   
          makespannew = makespan;
          if(makespannew<D)
          {
             for( Pe p: peList)
            {
            if( p.getStatus()==0 ) 
              {
                  p.setStatusFree();
              }   
          }
         }
          
      }
     }
     
     
  
        private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		//List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		//List<Pe> peList = new ArrayList<Pe>();

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
