import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


class XYSort implements Comparator<Node> {
	public int compare(Node one, Node two) {
		
		int result=one.getX()-two.getX();
		
		if(result==0) {
			//result=one.getY()-two.getY();
                        result=two.getY()-one.getY();
		}		
		return result;
	}
}

public class NDS {
	
	ArrayList<Solution> solutions;
	int minSolutions=0;
	
	public NDS() { }
	
	
	public NDS(int minSolutions,ArrayList<Solution> list) { 
		this.minSolutions=minSolutions;
		this.solutions=list;
		
	}
	
	
	public  ArrayList<Solution> run() {
	
		
		ArrayList<Node> nodes = new ArrayList<Node>();
				
		int k=0;         // index for solutions
		for(Solution s : solutions) {
			nodes.add(new Node(k,(int)s.makespan,(int)s.Loadbalancing));
			k++;
		}
	
				
		
		XSort xs=new XSort();
			
		Collections.sort(nodes,xs);
		
		
		
		
		
		final int INF = Integer.MAX_VALUE;
		ArrayList<Solution> nds = new ArrayList<Solution>();
		
                if(nodes.size()>  minSolutions) {
		
		for(int t=0;t<minSolutions;t++) {
					
			Solution s=solutions.get(  nodes.get(t).getId() );
			
			nds.add(s);
		}
		}else {
		
		
		for(Node n: nodes) {
			
			Solution s=solutions.get(  n.getId() );
			
			nds.add(s);
		}
		
		
	}

                System.out.println("Performing Non Dominated Sorting"  );
		System.out.println("\n\nNDS : " + nds.size() + "\n");
		return nds;

	}
}
