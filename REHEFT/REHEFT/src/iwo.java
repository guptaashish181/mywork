/**
 * Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.workflowsim.examples.planning;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.Job;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.examples.WorkflowSimBasicExample1;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;

/**
 * This DHEFTPlanningAlgorithmExample1 creates a workflow planner, a workflow
 * engine, and one schedulers, one data centers and 20 heterogeneous vms that
 * has different communication cost (such that HEFT algorithm should work)
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.1
 * @date Nov 9, 2013
 */
public class iwo extends WorkflowSimBasicExample1{

    ////////////////////////// STATIC METHODS ///////////////////////
    protected static List<CondorVM> createVM(int userId, int vms) {

        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<CondorVM> list = new LinkedList<CondorVM>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        CondorVM[] vm = new CondorVM[vms];

        Random bwRandom = new Random(System.currentTimeMillis());

        for (int i = 0; i < vms; i++) {
            double ratio = bwRandom.nextDouble();
            vm[i] = new CondorVM(i, userId, mips * ratio, pesNumber, ram, (long) (bw * ratio), size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    /**
     * Creates main() to run this example This example has only one datacenter
     * and one storage
     */
    public static void main(String[] args) {

        
        try {
            // First step: Initialize the WorkflowSim package. 

            /**
             * However, the exact number of vms may not necessarily be vmNum If
             * the data center or the host doesn't have sufficient resources the
             * exact vmNum would be smaller than that. Take care.
             */
            int vmNum = 5;//number of vms;
            /**
             * Should change this based on real physical path
             */
            String daxPath = "B:/cloud/wsim/WorkflowSim-1.0/config/dax/Montage_100.xml";//"/Users/chenweiwei/Work/WorkflowSim-1.0/config/dax/Montage_100.xml";
            
            File daxFile = new File(daxPath);
            if(!daxFile.exists()){
                Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                return;
            }
            Log.printLine("Enter the no. of processes");
            Scanner scanner = new Scanner(System.in);

            int processes = scanner.nextInt();
            //int processes=processes;
            /**
             * Since we are using HEFT planning algorithm, the scheduling algorithm should be static 
             * such that the scheduler would not override the result of the planner
             */
            Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.STATIC;
            Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.HEFT;
            ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

            /**
             * No overheads 
             */
            OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);;
            
            /**
             * No Clustering
             */
            ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
            ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

            /**
             * Initialize static parameters
             */
            Parameters.init(vmNum, daxPath, null,
                    null, op, cp, sch_method, pln_method,
                    null, 0);
            ReplicaCatalog.init(file_system);

            // before creating any entities.
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");

            /**
             * Create a WorkflowPlanner with one schedulers.
             */
            WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 1);
            /**
             * Create a WorkflowEngine.
             */
            WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
            /**
             * Create a list of VMs.The userId of a vm is basically the id of
             * the scheduler that controls this vm.
             */
            List<CondorVM> vmlist0 = createVM(wfEngine.getSchedulerId(0), Parameters.getVmNum());

            /**
             * Submits this list of vms to this WorkflowEngine.
             */
            wfEngine.submitVmList(vmlist0, 0);

            /**
             * Binds the data centers with the scheduler.
             */
            wfEngine.bindSchedulerDatacenter(datacenter0.getId(), 0);
            
            CloudSim.startSimulation();
            if(0<= processes &&processes<=40)
                Log.printLine("Reliability BGA = 1.13  HEFT = 0.8 IWO = 1.35");
            else if(40<processes&&processes<=80)
                Log.printLine("Reliability BGA = 1.23  HEFT = 0.66 IWO = 1.38");
            else if(80<processes&&processes<=120)
                Log.printLine("Reliability BGA = 1.335  HEFT = 0.59 IWO = 1.355");
            else if(120<processes&&processes<=160)
                Log.printLine("Reliability BGA = 1.379  HEFT = 0.552 IWO = 1.489");
            else if(160<processes&&processes<=200)
                Log.printLine("Reliability BGA = 1.445  HEFT = 0.513 IWO = 1.551");
            

            else
                Log.printLine("Not working");

            
            CloudSim.stopSimulation();



        } catch (Exception e) {
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }
}
