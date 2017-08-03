import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 class DSort implements Comparator<Node>{
	 
	public int compare(Node one, Node two) {
		
		int result=one.getDistance()-two.getDistance();
		return result;
	}
}
*/
class RDSort implements Comparator<Node> {
	public int compare(Node one, Node two) {
		
		int result=one.getRank()-two.getRank();
		
		if(result==0) {
			result=one.getDistance()-two.getDistance();
		}
		
		return result;
	}
}

class XSort implements Comparator<Node> {
	public int compare(Node one, Node two) {
		
		int result=one.getX()-two.getX();
		
		if(result==0) {
			//result=one.getY()-two.getY();
                        result=two.getY()-one.getY();
		}		
		return result;
	}
}

class XOnlySort implements Comparator<Node> {
	public int compare(Node one, Node two) {
		
		int result=one.getX()-two.getX();		
		
		return result;
	}
}

class YOnlySort implements Comparator<Node> {
	public int compare(Node one, Node two) {
		
		int result=one.getY()-two.getY();		
		
		return result;
	}
}

public class NonDominantSolutionSolver {
	
	ArrayList<Solution> solutions;
	int minSolutions=0;
	
	public NonDominantSolutionSolver() { }
	
	
	public NonDominantSolutionSolver(int minSolutions,ArrayList<Solution> list) { 
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
	
				
		ArrayList<Node> ndf = new ArrayList<Node>();
				
		
		XSort xs=new XSort();
			
		Collections.sort(nodes,xs);
		
				
		int rank=1;
		
		int count=  minSolutions;
		do {
			
		int index=0, recentIndex=0;
		if(nodes.size()==0) {
		 break;
		}
		Node recent=nodes.get(index);
		
		/*
		boolean allowed=true;
		if(rank>1) {
		
			for(int ni=0;ni<ndf.size();ni++) {
				Node tn= ndf.get(ni);
				
				if(tn.getX() <= recent.getX()) {
					
					if(tn.getY()<=recent.getY()) {
						allowed=false;
						nodes.remove(recent);
					} 
					
				}
				
			}
			
		}*/
		
	   //if(allowed) {
		 ndf.add(recent);
		 
		//}
		
		
		
		boolean found=false;
		boolean exit=false;
		while(!exit) {
		
		index=recentIndex;	
		found=false;
		while(!found) {
			
			index++;
			
			if(index>=nodes.size()) {
				exit=true;
				break;
			}
			
			//System.out.println("Index: " + index + " " + nodes.size());
			
			Node n = nodes.get(index);
			
			if(n.y<=recent.y) {
		
				n.setRank(rank);
			    ndf.add(n);

			    /*
			    allowed=true;
				if(rank>1) {
				
					for(int ni=0;ni<ndf.size();ni++) {
						Node tn= ndf.get(ni);
						
						if(tn.getX() <= n.getX()) {
							
							if(tn.getY()<=n.getY()) {
								allowed=false;
								nodes.remove(n);
							} 
							
						}
						
					}
					
				}
				
			   if(allowed) {
				 ndf.add(n);
				 
				}
			    */
			    
			    recent=n;
				recentIndex=index;
			    found=true;
			    
			}
			
		}
		
		}
		
			
		
		
		/////////////////
		/*
		if(rank>1) {
			
			
			
			for(int i=0;i<ndf.size();i++) {
				Node n1= ndf.get(i);
				for(int j=0;j<ndf.size();j++) {
					Node n2=ndf.get(j);
					if(n1!=n2) {
						
						if(n1.getX()>=n2.getX()) {
							if(n1.getY()>=n2.getY()) {
								
								ndf.remove(n1);
								nodes.remove(n1);
							}
						}
					}
				}
				
			}
			
			
		}*/
		
		
		
		
		
		
		nodes.removeAll(ndf);
		
			
			
			rank++;
		
		
			
		} while(ndf.size()<count);
		
		
		
		
		System.out.println("\n\nNDF size : " + ndf.size() + "\n");
		
		
		
		
		final int INF = Integer.MAX_VALUE;
		ArrayList<Solution> nds = new ArrayList<Solution>();
		
                if(ndf.size()>  minSolutions) {
			/*if(rank>1) {
				
				
				
				for(int i=0;i<ndf.size();i++) {
					Node n1= ndf.get(i);
					for(int j=0;j<ndf.size();j++) {
						Node n2=ndf.get(j);
						if(n1!=n2) {
							
							if(n1.getX()>=n2.getX()) {
								if(n1.getY()>=n2.getY()) {
									
									ndf.remove(n1);
									//nodes.remove(n1);
								}
							}
						}
					}
					
				}
			}*/
			
			
			for(Node n: ndf) {
				n.setDistance(0);
			}
			
			for(int objective=1;objective<=2;objective++) {
				
				if(objective==1) {
				
					XOnlySort xs1=new XOnlySort();
					
					Collections.sort(ndf,xs1);
					
					
					
				} else {
					
					YOnlySort ys1=new YOnlySort();
		
					
					Collections.sort(ndf,ys1);
					
					
				}
				
				
				ndf.get(0).setDistance(INF);
				ndf.get(ndf.size()-1).setDistance(INF);
				
				for(int i=2;i<ndf.size()-1;i++) {
					
					int o1 = (objective==1)?ndf.get(i+1).getX():ndf.get(i+1).getY();
					int o2 = (objective==1)?ndf.get(i-1).getX():ndf.get(i-1).getY();
					
					int d = ndf.get(i).getDistance() + (o1-o2);
					
					ndf.get(i).setDistance(d);
					
				}
				
				
			} // objective
		
		RDSort rd1=new RDSort();
		//DSort rd1=new DSort();
		Collections.sort(ndf,rd1);
		
		
		for(int t=0;t<minSolutions;t++) {
					
			Solution s=solutions.get(  ndf.get(t).getId() );
			
			nds.add(s);
		}
		}else {
		
		
		for(Node n: ndf) {
			
			Solution s=solutions.get(  n.getId() );
			
			nds.add(s);
		}
		
		
	}

                System.out.println("Performing Non Dominated Sorting"  );
		System.out.println("\n\nNDS : " + ndf.size() + "\n");
		return nds;

	}
}
