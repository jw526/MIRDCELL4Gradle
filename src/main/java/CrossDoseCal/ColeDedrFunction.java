package CrossDoseCal;
/*
 * ColeDedrFunction.java
 * Fit to Cole's experimental data
 * Created on May 25, 2008, 3:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import static java.lang.Math.*;

/**
 *
 * @author Johnny  Wu
 */
public class ColeDedrFunction {
    
    /** Creates a new instance of ColeDedrFunction */
    double r;
    double a=10.5, b=1126.05, c=-9.251137e+05, d=2.59298e+08, e = 4.96439e+10;       
    
    public ColeDedrFunction() {
    }
    
    public double getDedr(double r) {
        double dedr = 0;
        if (r >= 0.02) {
            dedr = 3.3335*pow((r + 0.007),(-0.435)) + 0.0055*pow(r,0.33);
        }
        else if(r < 0.02 && r > 0.0038) {
            dedr = 29.5 - 666.67*r;
        }
        else if(r > 0.0 && r <= 0.0038) {
            dedr = a + b*r + c*pow(r,2) + d*pow(r,3) + e*pow(r,4);
        }
        else {
            dedr = 0.0;
        }
        return dedr;
    }
}
