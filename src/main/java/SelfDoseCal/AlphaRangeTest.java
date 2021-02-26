/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SelfDoseCal;

/**
 *
 * @author Johnny  Wu
 */
public class AlphaRangeTest {
    double range = 0;
    
    public AlphaRangeTest(){
        
    }
    
    public double getAlphaRange(double en, double rc){
        
        //altenate with modified function in Alpha package
        range = Math.pow(en/390, rc);
        return range;
    }

}
