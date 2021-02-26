package SelfDoseCal;

import File.DedrAlpha;

/*
 * Ens.java
 *
 * Created on July 25, 2008, 12:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import File.MyList2;
import java.util.ArrayList;

/**
 *
 * @author Johnny  Wu
 */
public class Ens {

    String fileName = "alph-water";
    DedrAlpha dedrAlpha;
    Dedr2Function dedr2Func = new Dedr2Function();
    /** Creates a new instance of Ens */
    double dedr, dedr2;
	double e,f,p,q,r = 0.0, dr, x,dx,x2,x3,x4,y,z;
	int ctpye, type;
    int maxdim = 200;	

	double electron, xrange, ekev, e0;
	double rn,rc;
	int step;		
    double ency = 0.0;
	int istep = 100, jstep = 300;
    
    double rc2,rc3,rc4,rn2,rn3,rn4;
    double surfpsi,ecs,ecc,enn;
 
    int i;
        
    public Ens() {
    }
    
    public double getEns(String radType, double e0, double r, double rc, double rn, ArrayList<MyList2> list) {
        
        double x0 = 0.0, ens = 0;

 //out:
     //while(true) {

         if(r < (rc-rn)) {
              //System.out.println(" r< rc-rn" );
             // break out;  // 4/24/09 problem found here
             ens = 0;
         }

         if(r >(rc+rn)) {
             dx=(rn+rn)/(double)jstep;
         }
	     if(r >= (rc-rn) && r <= (rc+rn)) {
                 dx=(r-(rc-rn))/(double)(jstep-5);
         }
          //System.out.println("the initial result of dx="+dx);
	      //double x0=rc-rn;
              x0 = rc- rn;
              for (i=1; i <= jstep; i++) {

	          x = x0+dx*(double)i;
                  // System.out.println("the initial result of range x = "+x); 
                   
	          if(x >= (rc-rn) && x <= (rc+rn)) {
	               surfpsi = (2.0*x*rc-rc*rc-x*x+rn*rn)/(4.0*x*rc);
	          }
              else {
	               surfpsi = 0.0;
              }
              
	           //System.out.println("surfpsi = "+surfpsi); 
                   x=x-dx/2.0;
                  // System.out.println("the adjusted value of range x = "+x);
                    
                       if(radType.contains("Alpha") || radType.contains("ALPHA")){
                           dedrAlpha = new DedrAlpha(list);
                           dedr = dedrAlpha.getDedrAlpha(r, x);
                       }  
                       else {
                           dedr = dedr2Func.getDedr2Func(r, x);
                       }
	        ens = ens + dedr*surfpsi;
	      
              //System.out.println("the energy value between nucleus and surface = "+ens);
              }// end for
              //break out;
      //} //end out
      //System.out.println("ens 4/24/09 = "+ens);
              ens = ens*dx;
	      if (ens > e0){
	         ens = e0;
	      }
              
        return ens;
    }
}
