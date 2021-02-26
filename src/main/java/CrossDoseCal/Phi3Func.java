/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CrossDoseCal;

/**
 *
 * @author Johnny  Wu
 */
public class Phi3Func {
    
    SpsiGeoFactor spsiGeoFactor = new SpsiGeoFactor();
    VpsiGeoFactor vpsiGeoFactor = new VpsiGeoFactor();
    
    double e0, rc, rn, xn, istep = 1000, dep = 0, x0,x=0.0,x2=0,x3=0,x4=0,x5=0;
    double a,a2,a3,a4,a5;    
    double r,r2,r3,r4,r5;
    double p=0.0,p2=0,p3=0,p4=0,p5=0;  
    double spsi, spsirr, vpsiaa, vpsirr, vpsi, vpsira;
    double absf1, absf2, absf3, absf4, absf5, absfr;
    double cellmass, xnuclmass, cytomass;
    
    public Phi3Func(double x, double x2, double x3, double x4, double x5, double p, double p2, double p3, double p4, double p5, double rc, double rn, double xn, double e0, double dep) {
       this.rc = rc;
       this.rn = rn;
       this.xn = xn;
       this.e0 = e0;
       this.dep = dep;
       
       this.x = x;
       this.x2 = x2;
       this.x3 = x3;
       this.x4 = x5;
       this.x5 = x5;
       this.p = p;
       this.p2 = p2;
       this.p3 = p3;
       this.p4 = p4;
       this.p5 = p5;
       
       
        a=rn;
        a2=a*a;
        a3=a2*a;
        a4=a3*a;
        a5=a4*a;
        
        r=rc;
        r2=r*r;
        r3=r2*r;
        r4=r3*r;
        r5=r4*r;
        
       
        cellmass = 4.0 * 3.14159 * Math.pow(rc*(1.0e-04),3) /3.0;
        xnuclmass = 4.0 * 3.14159 * Math.pow(rn*(1.0e-04),3) /3.0;
        cytomass = cellmass - xnuclmass;
    }
    
    public void Phi3Cal(double e0, double dep) {
        
        //x, p are missing!!!!!!!!!!!!!!!!!
        //System.out.println("x = "+x);
       
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
                
        absf4= (xnuclmass/cytomass)*((cellmass/xnuclmass)*absfr - absf3);
       
                    
        absf1=absf1/e0;
        absf2=absf2/e0;
        absf3=absf3/e0;
        absf4=absf4/e0;
        absf5=absf5/e0;
        //System.out.println(absf1);
        //System.out.println("vpsi ="+vpsi+" e0= "+e0+" dep ="+dep+  " absf1="+absf1 +" " +"cellmass="+ cellmass); 
    }             
    
}
