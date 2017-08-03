
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Resource {
    
    private int id;
    
    private String architecture;
    private String os;
    private double timeZone;
    private String vmm;
    
    private int mips;
    private int pesNumber;
    private int ram;
    private long bw;
    private long size;
    
    private double costPerSecond=1;
    private double costPerMem=1;
    private double costPerStorage=1;
    private double costPerBw=1;
    
    public Resource() {
        
    }

    public Resource(int id, int mips) {
        this.id = id;
        this.mips = mips;
        this.bw=100;
        this.pesNumber=1;
        this.ram=128;
    }
    
    
    
    
    public static List<Resource> random(int n) {
        
            int [] mipsRange = {1000,2000,3000}; 
        
        Random r1= new Random();
        
            List<Resource> list=new ArrayList<Resource>();
        
        for(int i=0;i<n;i++) {
            
            int r = r1.nextInt(mipsRange.length);
            
            Resource res = new Resource();
            res.setId(i+1);
            res.setMips(mipsRange[r]);
            res.setRam(2048);
            res.setBw(1000);
            
            list.add(res);
             
        }
        
    
        
        
        
        return list;
        
        
    }

    
    public static List<Resource> getResourceList() {
        
                    
           List<Resource> list=new ArrayList<Resource>();
        
            list.add(new Resource(1,1000));
            list.add(new Resource(2,2000));
            list.add(new Resource(3,3000));
             
        
        return list;
        
        
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    public String getVmm() {
        return vmm;
    }

    public void setVmm(String vmm) {
        this.vmm = vmm;
    }

    public int getMips() {
        return mips;
    }

    public void setMips(int mips) {
        this.mips = mips;
    }

   

    public int getPesNumber() {
        return pesNumber;
    }

    public void setPesNumber(int pesNumber) {
        this.pesNumber = pesNumber;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public long getBw() {
        return bw;
    }

    public void setBw(long bw) {
        this.bw = bw;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getCostPerSecond() {
        return costPerSecond;
    }

    public void setCostPerSecond(double costPerSecond) {
        this.costPerSecond = costPerSecond;
    }

    public double getCostPerMem() {
        return costPerMem;
    }

    public void setCostPerMem(double costPerMem) {
        this.costPerMem = costPerMem;
    }

    public double getCostPerStorage() {
        return costPerStorage;
    }

    public void setCostPerStorage(double costPerStorage) {
        this.costPerStorage = costPerStorage;
    }

    public double getCostPerBw() {
        return costPerBw;
    }

    public void setCostPerBw(double costPerBw) {
        this.costPerBw = costPerBw;
    }
    
    
    public double getTotalCost() {
        return (costPerSecond+costPerBw+costPerMem+costPerStorage);
    }
    
}
