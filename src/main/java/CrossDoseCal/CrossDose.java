/*
 * CrossDose.java
 *
 * COMPUTE MULTICELLULAR DOSE 
 * Created on September 6, 2007, 8:00 AM
 *
 * P    DISTANCE BETWEEN CELLS IN MICRONS
 *
 * R	CELL RADIUS IN MICRONS
 *
 * A	RADIUS OF CELL NUCLEUS IN MICRONS
 *
 * E0	ENERGY OF PARTICLE IN KEV
 *
 * ISTEP	NUMBER OF INTEGRATION STEPS
 */
package CrossDoseCal;

import java.lang.*;
import java.lang.Math.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class CrossDose {
    
    /** Creates a new instance of CrossDose */
    Newton newton = new Newton();
    NumberFormat formatter = new DecimalFormat("0.00E00");
    
    ColeDedrFunction dedrCole = new ColeDedrFunction();
    SpsiGeoFactor spsiGeoFactor = new SpsiGeoFactor();
    VpsiGeoFactor vpsiGeoFactor = new VpsiGeoFactor();
        
    double rc, rn, dx;
    double x0, x = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;
    double a, a2, a3, a4, a5;
    double r, r2, r3, r4, r5;
    double p = 0, p2 = 0, p3 = 0, p4 = 0, p5 = 0;
    double spsi, spsirr, vpsiaa, vpsirr, vpsi, vpsira;
       
    double range = 0, dr= 0.0; //range of the dose = r
    double xn, e0, delta, conv = 0.00001; // e0 = enkev = ekev (energy in KeV)
    double electron, ekev, dep = 0;              
    double dedr = 0.0, sum = 0.0;

    int istop, maxabs = 130, istep = 1000, maxconfig = 5, jstopmax = 0, jstart, jstop, jsep;
    double act = 1.0e-6;
    double cellmass, xnuclmass, cytomass;
    int i, j, k;
                
    public CrossDose(double rc, double rn, double xn, double e0) {
        this.rc = rc;
        this.rn = rn;
        this.xn = xn;
        this.e0 = e0;
      
        a = rn;
        a2 = a*a;
        a3 = a2*a;
        a4 = a3*a;
        a5 = a4*a;
        
        r = rc;
        r2 = r*r;
        r3 = r2*r;
        r4 = r3*r;
        r5 = r4*r;
        
        //calculation of the mass of cell and nucleus
        cellmass = 4.0 * 3.14159 * Math.pow(rc*(1.0e-04),3) /3.0;
        xnuclmass = 4.0 * 3.14159 * Math.pow(rn*(1.0e-04),3) /3.0;
        cytomass = cellmass - xnuclmass;
                 
        delta = xn*e0*(1.0e-3)*2.13;        
        jstart = 2*(int)rc;
        	        
        /**
         * implement function Newton.java 
         * using e0 get initial range R 
         * get better energy in Kev
         * then keep computing
         * get best range in the end
         */
        range = newton.getRange(e0);        
        //System.out.println("range R = "+range);
        
        if (range < (double)(maxabs)) {
            jstop = (int)(range + rn + rc) + 5;
        } else {
            jstop = maxabs;
        }
        if (jstop > jstopmax) {
            jstopmax =  jstop;
        }
        //System.out.println ("jstop= "+jstop);
    }
    
    public double getCrossDose1(double rc, double rn, double e0)  {
        
        double dose1 = 0;
        //double crossDose = 0;       
        System.out.println ("rc="+rc+"rn="+rn+"e0="+e0+"jstop= "+jstop);
       
        //Computation of the integral    
        // Cycle through i radiations
        // for(i=1; i <= istop; i++)
        for (j = jstart; j <= jstop; j++) {
            
            double absf1 = 0.0, absf2 = 0.0, absf3 = 0.0, absf4 = 0.0, absf5 = 0.0, absfr = 0.0;
            System.out.println("j=" +j);
            p = (double)j;
            p2 = p*p;
            p3 = p2*p;
            p4 = p3*p;
            p5 = p4*p;
            
            //Select value of DX depending on whether Range > max distance
            if (range > p+r+r) {
                dx = 2.0*(r+r)/(double)(istep);
            } else {
                dx = (range-(p-r-r))/(double)(istep);
            }            
            //Compute X(K) and DEP(K)
            x0 = p-r-r;
            
            for (k=1; k <= istep; k++) {
                
                x = x0 + dx*(double)k;
                x2 = x*x;
                x3= x2*x;
                x4= x3*x;
                x5= x4*x;


	            /**
				 * if ( ctype[i] == ' ALPHA')
				 * dep[k] = dedral(range[i]-x[k]+dx/2)*dx
				 * consider Alpha particle cases
				 * if compute Alpha case
				 * using substitute function
				 */
                
                double r1 = range-x+dx/2;
     
                //use function ColeDedrFunction  
                dedr = dedrCole.getDedr(r1);  
                 
                dep = dedr*dx;                           
                sum = sum + dep;
                //System.out.println("the result of DEDR = " + dedr + "the result of DEP = "+dep +  "the result of SUM = " + sum);
                
                if (sum > e0) { 
                    //dep= e0 - (sum-dep);
                    //System.out.println("the result of DEP when sum larger than intial energy = "+dep)  
                    //break out; // jump out of the loop k when break out
                    dep = dedr*dx;
                }                        
 
                // Cell to Cell
                vpsirr = vpsiGeoFactor.getVpsiGeoFactor(x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, r, r2, r3, r4, r5);
                absf1 = absf1 + vpsirr*dep;
               
                // Cell surface to Cell
                spsirr = spsiGeoFactor.getSpsiGeoFactor(x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, r, r2, r3, r4, r5);
                absf2 = absf2 + spsirr*dep;
                         
                // Nucleus to Nucleus          
                // vpsi(x,p,a,a)
                vpsiaa = vpsiGeoFactor.getVpsiGeoFactor(x, x2, x3, x4, x5, p, p2, p3, p4, p5, a, a2, a3, a4, a5, a, a2, a3, a4, a5);
                absf3 = absf3 + vpsiaa*dep;
	            //System.out.println("dx: " + dx + " dedr: " + dedr + " dep: " + dep + " vpsiaa: " + vpsiaa + " absf3: " + absf3);

	            // Cell surface to Nucleus
                spsi = spsiGeoFactor.getSpsiGeoFactor(x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5);              
                absf5= absf5+ spsi*dep;
              
                /**
                 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
                 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
                 */                
                // vpsi(x,p,r,a)
                vpsi = vpsiGeoFactor.getVpsiGeoFactor(x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5);                
                absfr = absfr + vpsi*dep;
               
               
            //testing point       
            //System.out.println("vpsirr="+vpsirr+ "spsirr = "+spsirr +"vpsiaa="+vpsiaa +"spsi="+spsi+ "vpsi="+vpsi);
            }//end loop k istep
           //System.out.println( "dep = "+dep);
            absf4= (xnuclmass/cytomass)*((cellmass/xnuclmass)*absfr - absf3);
            
            absf1 /= e0;
            absf2 /= e0;
            absf3 /= e0;
            absf4 /= e0;
            absf5 /= e0;
               
        /** 
        System.out.println( "absf(1) = "+absf1);
        System.out.println( "absf(2) = "+absf2);
        System.out.println( "absf(3) = "+absf3);
        System.out.println( "absf(4) = "+absf4);
        System.out.println( "absf(5) = "+absf5);
        //System.out.println( "absfr = "+absfr);
       */
            double dose=0.0, dose2=0.0, dose3=0.0, dose4=0.0, dose5=0.0;  
            dose1 += absf1*delta*act/cellmass;
            dose2 += absf2*delta*act/cellmass;
            dose3 += absf3*delta*act/xnuclmass;
            dose4 += absf4*delta*act/xnuclmass;
            dose5 += absf5*delta*act/xnuclmass;
            
            String doseValue1 = formatter.format(dose1);
            String doseValue2 = formatter.format(dose2);
            String doseValue3 = formatter.format(dose3);
            String doseValue4 = formatter.format(dose4);
            String doseValue5 = formatter.format(dose5);
       /** 
        File out = new File( "C:/Documents and Settings/johnnywu/My Documents/UMDNJ/output/celldose2 output.txt" );
        FileWriter fw = new FileWriter(out);
        PrintWriter pw = new PrintWriter( fw );
        
        pw.println( "the final result of cell to cell absorb fraction value absf[1] :      "+absfr1 );
        pw.println( "the final result of surface to cell absorb fraction value absf[2] :   "+absfr2 );
        pw.println( "the final result of nucleus to nucleus absorb fraction value absf[3] :   "+absfr3 );
        pw.println( "the final result of cytoplasm to nucleus absorb fraction value absf[4] :  "+absfr4 );
        pw.println( "the final result of surface to nucleus absorb fraction value absf[5] :   "+absfr5 );
        pw.println( "the final result of absorb fraction value absfr :            "+absfr );
        
        pw.println( "  " );
        pw.println( "  " );
        pw.println( "dose(j,1) = "+dose1);
        pw.println( "dose(j,2) = "+dose2);
        pw.println( "dose(j,3) = "+dose3);
        pw.println( "dose(j,4) = "+dose4);
        pw.println( "dose(j,5) = "+dose5);
        fw.close();
        **/
           //System.out.println("dose(j,1) = "+doseValue1 );
        
        }//end j loop
          
        return dose1;
        
    }//end class
    
}
