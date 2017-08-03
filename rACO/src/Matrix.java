//@Ashish gupta  National Institute of Technology, Kurukshetra

import java.util.Arrays;


public class Matrix {

	
	double [][] matrix={ };
	
	public Matrix() {
	
            
	}
	
	public Matrix(int rows,int cols) {
		
		matrix = new double[rows][cols];
		
		for(int row=0;row<rows;row++) {
			for(int col=0;col<cols;col++) {
				//matrix[row][col]= (int) (Math.random()*10);  
                                matrix[row][col]= 0;  
			}
		} 
		
		
	}

        public Matrix(double [][] matrix) {

		this.matrix=matrix;

	}
	
	public Matrix(int rows,int cols, int value) {
		
		matrix = new double[rows][cols];
		
		for(int row=0;row<rows;row++) {
			for(int col=0;col<cols;col++) {
				matrix[row][col]=value;
			}
		}
		
	}
    
public Matrix(int rows,int cols, double value) {
		
		matrix = new double[rows][cols];
		
		for(int row=0;row<rows;row++) {
			for(int col=0;col<cols;col++) {
				matrix[row][col]=value;
			}
		}
		
	}

public double getMaxColValue(int col) {

    double max=0;
    for(int row=0;row<matrix.length;row++) {
        if(matrix[row][col]>max) {
            max=matrix[row][col];
        }
    }

    return max;

}
	
	public double [][] getMatrix() {
		return matrix;
	}
	
	public double getValue(int row,int col) {
		return matrix[row][col]; //matrix[][]
	}
	
	public void setValue(int row,int col, double value) {
		matrix[row][col]=value;
	}
	
	
	public int getNoOfRows() {
		return matrix.length;
	}
	
	public int getNoOfCols() {
		return matrix[0].length;
	}
	
	public void printMatrix(String title, boolean converted) {
		
		System.out.println("\n" + title);
		for(int row=0;row<this.getNoOfRows();row++) {
			for(int col=0;col<this.getNoOfCols();col++) {
				if(converted) {
					System.out.print( (int) matrix[row][col] + "  " );
				} else {
					//System.out.print(matrix[row][col] + "  " );
					System.out.printf("%.3f ", matrix[row][col] );
				}
			}
			System.out.println();
		}
		
		
	}
	
	
	
	public static Matrix getExecutionTimeMatrix() {
		
		double m [][] =  { {3,2} , {1,4}, {3,4}, {7,6}};
		
		Matrix mat  = new Matrix();
		mat.matrix=m;
		
		return mat;
		
	}
	
	public static Matrix getCostMatrix() {
		
		double m [][] =  { {1,2} , {4,1}, {2,6}, {2,4}};
		
		Matrix mat  = new Matrix();
		mat.matrix=m;
		
		return mat;
		
	}
	
	public static Matrix getPheromoneMatrix() {
		
		double m [][] =  { {1,1} , {1,1}, {1,1}, {1,1}};
		
		Matrix mat  = new Matrix();
		mat.matrix=m;
		
		return mat;
		
	}
	
	public static Matrix getFreeTimeMatrix() {
		
		double m [][] =  {{1,1} , {1,1}};
		
		Matrix mat  = new Matrix();
		mat.matrix=m;
		
		return mat;
		
	}
	
	
	public double getMaxColumnValue(int column) {
		double result=0;
		
		double [] array=new double[getNoOfRows()]; 
		
		for(int k=0;k<getNoOfRows();k++) {
			array[k]= matrix[k][column];
		}
		
		Arrays.sort(array);
		
		result=array[array.length-1];	
		
		return result;
	}
	
	
	public static void main(String [] args) {
		
		//Matrix m1 = new Matrix(2,3);
		
		//m1.printMatrix("Matrix", true);
		
		
		Matrix m = Matrix.getExecutionTimeMatrix();
		
		m.printMatrix("ET",true);
		
		
		System.out.println("Max: " + m.getMaxColumnValue(1));
		
		
	}
	
	
	
	
}
