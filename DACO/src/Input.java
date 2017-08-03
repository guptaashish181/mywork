
public class Input {

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

 


}
