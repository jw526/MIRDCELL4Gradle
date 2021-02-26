/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SelfDoseCal;

/**
 *
 * @author Johnny  Wu
 */
public class Dedr1Function {
    double r, x;
    
    public Dedr1Function() {
    }
    
    protected double getDedr1Func(double r, double x) {
        
        double dedr1 = 0;
        double z = r - x;
        
        /**
         *  Z = XRANGE-R                  ! Remaining range in tissue
	 if (z.gt.0.0)then
        DEDR1 = 260.0/Z**(0.333333)
	 else
	  dedr1=0.0
	 end if
         */
        if(z > 0) {
            dedr1 = Math.pow(260/z, 1/3);
        }
        else {
            dedr1 = 0;
        }
        return dedr1;
    }

}
