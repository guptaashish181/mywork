//@Ashish gupta  National Institute of Technology, Kurukshetra

import java.util.ArrayList;

public class Main {
	
	
	public static void main(String [] args) {
		
		Machine m1 = new Machine(1);
		Machine m2 = new Machine(2);
		Machine m3 = new Machine(3);
		
		
		ArrayList<Machine> machineList = new ArrayList<Machine>();
		
		
		machineList.add(m1);
		machineList.add(m2);
		machineList.add(m3);
		
		
		System.out.println("Size: " + machineList.size());
		
		
		for(int k=0;k<machineList.size();k++) {
			
			Machine m = machineList.get(k);
			
			System.out.println("Machine Name: " + m.getName());
			
		}
		
		
		
		for(Machine mm: machineList) {
			
			System.out.println("Machine Name: " + mm.getName());
			
		}
		
		
		
		
		
		
		
		
	}
	

}
