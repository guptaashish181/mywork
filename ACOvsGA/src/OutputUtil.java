/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class OutputUtil {
    
    
    public static void print(String title, int [] a ) {
        System.out.println("\n" + title + ":");
        for(int t: a) {
            System.out.print(t + " ");
        }
        
        System.out.println("");
    }
    
    public static void print(String title, double [] a ) {
        System.out.println("\n" + title + ":");
        for(double t: a) {
            System.out.print(t + " ");
        }
        
        System.out.println("");
    }
    
     public static void print(String title, double [][] a ) {
        
         System.out.println("\n" + title + ":");
        
        for(double [] t: a) {
            
            for(double tt : t) {
            
                System.out.print(tt + " ");
            
            }
            
            System.out.println("");
        }
        
        System.out.println("");
    }
    
     public static void print(String title, int [][] a ) {
        
         System.out.println("\n" + title + ":");
        
        for(int [] t: a) {
            
            for(int tt : t) {
            
                System.out.print(tt + " ");
            
            }
            
            System.out.println("");
        }
        
        System.out.println("");
    }
    
}
