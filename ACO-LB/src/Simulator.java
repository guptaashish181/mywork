import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

public class Simulator          {

    List<Integer> sch;

    double resultPSO = 0;
    double resultSim = 0;

    List<Cloudlet> cloudletList;
    List<Vm> vmlist;

    List<Resource> resources;
    List<Task> jobs;

    Problem prob;
    
    OutputMetric metric;
    

    public double run(Problem prob, Map<Integer, Integer> sch) {

        this.prob = prob;
        resources = prob.getResources();
        jobs = prob.getTasks();

        System.out.println("Schedule: ");
        int i = 0;

        System.out.println("SCH: " + sch);
        Log.printLine("Starting CloudSim...");

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
            Datacenter datacenter0 = createDatacenter("Datacenter_1");

            //Datacenter datacenter0 = createDatacenter1("Datacenter_1");
            //Datacenter datacenter1 = createDatacenter1("Datacenter_2");
            //Datacenter datacenter2 = createDatacenter1("Datacenter_3");
            //Third step: Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            //Fourth step: Create one virtual machine
            vmlist = new ArrayList<Vm>();

            int vmid = 1;
            int pesNumber = 1; //number of cpus

            for (int rs = 0; rs < resources.size(); rs++) {

                Resource res = resources.get(rs);

                //VM description
                int mips = res.getMips(); // 250
                long size = 10000; //image size (MB)
                int ram = 2048; //vm memory (MB)
                long bw = 1000;

                String vmm = "Xen"; //VMM name

                Vm vm1 = new Vm(res.getId(), brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());

                vmid++;

                //add the VMs to the vmList
                vmlist.add(vm1);

            }

            //submit vm list to the broker
            broker.submitVmList(vmlist);

            //Fifth step: Create two Cloudlets
            cloudletList = new ArrayList<Cloudlet>();

            //Cloudlet properties
            int id = 1;
            long length = 40000;
            long fileSize = 300;
            long outputSize = 300;

            for (Task j : jobs) {

                UtilizationModel utilizationModel = new UtilizationModelFull();

                Cloudlet cloudlet1 = new Cloudlet(j.getId(), j.getMi(), pesNumber, j.getInputFileSize(), j.getOutputFileSize(), utilizationModel, utilizationModel, utilizationModel);
                cloudlet1.setUserId(brokerId);

                cloudletList.add(cloudlet1);

            }

            //submit cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            for (Cloudlet c : cloudletList) {

                
                Vm vm1 = vmlist.get((int)sch.get(c.getCloudletId()) - 1);
                broker.bindCloudletToVm(c.getCloudletId(), vm1.getId());

            }

            // Sixth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(newList);

            Log.printLine("CloudSim finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }

        return resultSim;

    }

    private Datacenter createDatacenter(String name) {

        int hostId = 0;
        int ram = 2048 * 2; //host memory (MB)
        long storage = 1000000; //host storage
        int bw = 10000;
        List<Host> hostList = new ArrayList<Host>();
        for (int i = 0; i < resources.size(); i++) {

            Resource res = resources.get(i);

            List<Pe> peList = new ArrayList<Pe>();
            peList.add(new Pe(0, new PeProvisionerSimple(res.getMips()))); // need to store Pe id and MIPS Rating

            hostList.add(
                    new Host(
                            hostId,
                            new RamProvisionerSimple(ram),
                            new BwProvisionerSimple(bw),
                            storage,
                            peList,
                            new VmSchedulerTimeShared(peList))); // This is our first machine

        }
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

    private DatacenterBroker createBroker() {

        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    private void printCloudletList(List<Cloudlet> list) {
        
        int size = list.size();
        
        Cloudlet cloudlet;

        //Collections.sort(list, new VMComp());
        double makespan = 0;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "Machine" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        makespan = 0;

        
        Map<Integer,Integer> useCounter = new HashMap<Integer,Integer>();
        Map<Integer,Double> useTime = new HashMap<Integer,Double>();
        
        

        
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent + dft.format(cloudlet.getFinishTime()));

                if (cloudlet.getFinishTime() > makespan) {
                    makespan = cloudlet.getFinishTime();
                }

                int vmId=cloudlet.getVmId();
                
                if(useCounter.containsKey(vmId)) {
                    
                    useCounter.put(vmId, useCounter.get(vmId)+1);
                    useTime.put(vmId, useTime.get(vmId) + cloudlet.getActualCPUTime() );
                    
                } else {
                    
                    useCounter.put(vmId, 1);
                    
                    
                    useTime.put(vmId, cloudlet.getActualCPUTime() );
                }
                
                        //double costPerSec = 1;
                
                double costPerSec = resources.get(cloudlet.getVmId()-1).getTotalCost();
                        
        
            }
        }

        
        
        metric = new OutputMetric();
        
        metric.setMakespan(makespan);
        metric.setUseCounter(useCounter);
        metric.setUseTime(useTime);
        
        
        
        System.out.println("useCounter" +  useCounter);
        
        System.out.println("useTime" +  useTime);
        
        
        double sru = 0;
        for(Integer i: useTime.keySet()) {
            sru += (useTime.get(i)/makespan);
        }
        
        double avgRU = (sru/(double)useTime.size());
        
        metric.setAvgResourceUtilization(avgRU);
        
        
        // load balance level
        sru = 0.0;
        for(Integer i: useTime.keySet()) {
            double ru=(useTime.get(i)/makespan);
            sru += Math.pow((avgRU-ru),2);
        }        
        double d = Math.sqrt(sru/(double)useTime.size());        
        double beta = (1-(d/avgRU))*100.0;        
        metric.setLoadBalanceLevel(beta);
        
        
        resultSim = makespan;

    }
}
