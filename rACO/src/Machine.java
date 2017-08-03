//@Ashish gupta  National Institute of Technology, Kurukshetra

public class Machine {
	
	private int id;
	
	public Machine() {
		super();
	}

	public Machine(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getName() {
		return "m" + id;
	}
	
	
	

}
