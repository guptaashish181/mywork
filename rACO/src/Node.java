//@Ashish gupta  National Institute of Technology, Kurukshetra

public class Node {
	
	int rank;
	int distance;
	
	
	int id;
	int x;
	int y;
	String result;

	boolean marked;
	
	static int c=0;
	
	public Node() {
		
	}
	
	public Node(int x,int y) {
		
	   c++;
	   this.id=c;
		this.x=x;
		this.y=y;
		//this.x= (int)(Math.random()*10);
		//this.y= (int)(Math.random()*10);;
	}
	
	public Node(int id, int x,int y) {
		
		   
		   this.id=id;
			this.x=x;
			this.y=y;
			
		}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		
		marked=true;
		
		this.result = result;
	}
	
	
	
	

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}


 	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	


	public String toString() {
		return "[n" + id + " " + x + " " + y +"]";
	}
	
}