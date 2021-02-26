/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package File;

public class Knee 
{ 
  
    public static class pair 
    {  
        double F, S;  
        public pair(double F, double S)  
        {  
            this.F = F;  
            this.S = S;  
        }
        public double getX()
        {
            return F;
        }
        public double getY()
        {
            return S;
        }
        public pair() { 
        }  
    } 

    // Function to return the minimum distance 
    // between a line segment AB and a point E 
    static double minDistance(pair A, pair B, pair E) 
    { 

        // vector AB 
        pair AB = new pair(); 
        AB.F = B.F - A.F; 
        AB.S = B.S - A.S; 

        // vector BP 
        pair BE = new pair(); 
        BE.F = E.F - B.F; 
        BE.S = E.S - B.S; 

        // vector AP 
        pair AE = new pair(); 
        AE.F = E.F - A.F; 
        AE.S = E.S - A.S; 

        // Variables to store dot product 
        double AB_BE, AB_AE; 

        // Calculating the dot product 
        AB_BE = (AB.F * BE.F + AB.S * BE.S); 
        AB_AE = (AB.F * AE.F + AB.S * AE.S); 

        // Minimum distance from 
        // point E to the line segment 
        double reqAns = 0; 

        // Case 1 
        if (AB_BE > 0)  
        { 

            // Finding the magnitude 
            double y = E.S - B.S; 
            double x = E.F - B.F; 
            reqAns = Math.sqrt(x * x + y * y); 
        } 

        // Case 2 
        else if (AB_AE < 0) 
        { 
            double y = E.S - A.S; 
            double x = E.F - A.F; 
            reqAns = Math.sqrt(x * x + y * y); 
        } 

        // Case 3: pts are in between the first and last
        else 
        { 

            // Finding the perpendicular distance 
            double x1 = AB.F; 
            double y1 = AB.S; 
            double x2 = AE.F; 
            double y2 = AE.S; 
            double mod = Math.sqrt(x1 * x1 + y1 * y1); 
            reqAns = Math.abs(x1 * y2 - y1 * x2) / mod; 
        } 
        return reqAns; 
    }
    
    public static int findKnee(double[] x, double[] y){
        double max = 0;
        int kneeIndex = -1;
        pair A = new pair(x[0], y[0]);
        pair B = new pair(x[x.length-1], y[y.length-1]);
        for(int i=1; i<x.length-1; ++i){
           if(max <= minDistance(A, B, new pair(x[i], y[i]))){
               max = minDistance(A, B, new pair(x[i], y[i]));
               kneeIndex = i;
           }           
        }
        return kneeIndex;
    }
}