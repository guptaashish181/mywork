
public class MatrixHelper {

	static int b=100;  // 3000
	static int r=1000;
	
		
	public static Matrix getExecutionTimeMatrix(int noOfTasks, int noOfMachines) {
		
		double [] temp = new  double [noOfTasks];
		
		for(int k=0;k<noOfTasks;k++){
			temp[k]= (Math.random()*b) +1; 
		}
		
		
		Matrix mat  = new Matrix(noOfTasks,noOfMachines,0);
		
		
		for(int row=0;row<mat.getNoOfRows();row++) {
		
			for(int col=0;col<mat.getNoOfCols();col++) {
				
				double rand= ((Math.random()*r)+1);
				
				double t= temp[row]*rand;
				
				mat.setValue(row, col, t);
				
			}
			
		}
		
				
		return mat;
		
	}
	


public static Matrix getCostMatrix(Matrix et, int noOfTasks, int noOfMachines) {
		
	
			
	
		double [] c = new  double [noOfMachines];
		
		

		for(int k=0;k<noOfMachines;k++){
			c[k]= 100 + (Math.random()*150) +1 ; //////////////
			//c[k]= 1+ (Math.random()*10)  ; //////////////
		}
		
		/*
		for(int k=0;k<noOfTasks;k++){
			p[k]= 1 + (Math.random()*20) ; /////////////// 
		}*/
		
		
		Matrix mat  = new Matrix(noOfTasks,noOfMachines,0);
		
		
		for(int row=0;row<mat.getNoOfRows();row++) {
		
			for(int col=0;col<mat.getNoOfCols();col++) {
				
								
				double t= c[col]*et.getValue(row, col);
				
				mat.setValue(row, col, t);
				
			}
			
		}
		
				
		return mat;
		
	}
	
	
	public static void main(String [] args) {
		
		Matrix m = getExecutionTimeMatrix(2,3);
		
		m.printMatrix("ET",false);
		
		//Matrix c = getCostMatrix(m,2,3);
		
		//c.printMatrix("cost",false);
		
		
	}
	
}
