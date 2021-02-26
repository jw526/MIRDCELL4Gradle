/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CrossDoseCal;

import File.DedrAlpha;
import File.MyList2;
import java.util.ArrayList;

/**
 *
 * @author Johnny  Wu
 */
public class DepFunc {
    String alphaFile = "";
    DedrAlpha dedrAlpha;
    ColeDedrFunction dedrCole = new ColeDedrFunction();
    double dep = 0, sum = 0;
    ArrayList<MyList2> list = new ArrayList<MyList2>();
    
    public DepFunc(double sum, double dep){
        this.sum = sum;
        this.dep = dep;
    }
    
    public double getDep(String radType, double e0, double range, double x, double dx, ArrayList<MyList2> list){
        
        double r1, dedr1Alpha, dedr2;
        r1 = range-x+dx/2;    
                       //System.out.println("rad="+radType);
                       //System.out.println("the result of DEP1 = "+dep); 
                      //out:
                          //while(true) {
                           if(radType.equals("ALPHA")) {
                               //dedrAlpha = new DedrAlpha(alphaFile);
                               dedrAlpha = new DedrAlpha(list);
                               dedr1Alpha = dedrAlpha.getDedrAlpha(range, r1);
                               dep = dedr1Alpha * dx;
                               //03/25/09 System.out.println("Alpha: dedr="+dedr1Alpha);
                               //System.out.println("the result of DEP1 = "+dep);
                           } else {
                               dedr2 = dedrCole.getDedr(r1);
                               dep = dedr2 * dx;
                               //03/25/09 System.out.println("other dedr="+dedr2);
                           }

                           //System.out.println("the result of DEP1 = "+dep);   //test error found!!
                           //the result of DEP1 = -0.015074645123341827
                           //the result of SUM = -0.015074645123341827
                           
                           sum += dep;
                           //System.out.println("the result of SUM = "+sum);
                           
                           //test e0
                           //System.out.println("e0="+e0);
                              if (sum > e0) {
                                  System.out.println("sum > en");
                                  //dep= e0 - (sum-dep);
                                  //System.out.println("the result of DEP when sum larger than intial energy = "+dep)  
                                  //break out; jump out of the loop k when break out
                                  //dep = dedrCole.getDedr(r1) * dx;
                                  dep = e0 - (sum - dep);
                                  //System.out.println("dep="+dep);
                                 //??????????????????
                                  
                                  //jump out of the loop, return dep value for next use
                                  //return dep;

                              }  
                          //}//end while
        return dep;
    }

}
