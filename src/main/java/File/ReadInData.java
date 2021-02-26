/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package File;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Johnny  Wu
 */
public class ReadInData {
     ArrayListIn2 ali;
     MyList ml;    
     ArrayList<MyList> list;
     String iso = "", selfDose = " ", crossDose = " ";
     int pro = 0, rc = 0, rn = 0, d = 0;
     
     public ReadInData(String iso){
         this.iso = iso;
         ali = new ArrayListIn2(iso);
         list = new ArrayList();
     }
     public int getMaxRow(int rc, int rn){
         return ali.getMaxRow(rc, rn);
     }
     
     //read whole output data file
     public ArrayList readAll(int rc, int rn){
         try{
             list.addAll(ali.read2(rc, rn));
         }catch(IOException ioe){
             System.out.println(ioe);
         }
         return list;
     }
     //read output file for self-dose value
     public String readSelf(int rc, int rn, int pro){
         try {
             list.addAll(ali.read2(rc, rn));
             //System.out.println(list);
             //self-dose
             MyList ml2 = list.get(1);
             //System.out.println(ml2);
             //System.out.println("process="+pro);
             if(pro == 0){
                 selfDose = ml2.getLine2();
             } else if(pro == 1){
                 selfDose = ml2.getLine3();
             } else if(pro == 2){
                 selfDose = ml2.getLine4();
             } else if(pro == 3){
                 selfDose = ml2.getLine5();
             } else {
                 selfDose = ml2.getLine6();
             }
         }catch(IOException ioe){
             //
         }
         return selfDose;
     }
     
     //read output file for cross-dose value
     public String readCross(int rc, int rn, int d, int pro){
         try{
             list.addAll(ali.read2(rc, rn));
             MyList ml1= list.get(d);
                 //System.out.println(ml1);
  
                 if(pro == 0){
                     crossDose = ml1.getLine2();
                 } else if(pro == 1){
                     crossDose = ml1.getLine3();
                 } else if(pro == 2){
                     crossDose = ml1.getLine4();
                 } else if(pro == 3){
                     crossDose = ml1.getLine5();
                 } else {
                     crossDose = ml1.getLine6();
                 }
                 
         }catch(IOException ioe){
             //  
         }
         return crossDose;
     }//end method

}//end class
