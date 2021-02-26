/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CrossDoseCal;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.io.*;
/**
 * @author Johnny  Wu
 */
class DoseFunc {
    
    NumberFormat formatter = new DecimalFormat("0.00E00");
    
    double absf1, absf2, absf3, absf4, absf5, absfr;
    double act = 1.0e-6, delta;
    double cellmass, xnuclmass, cytomass;
    double dose=0, dose1=0, dose2=0, dose3=0, dose4=0, dose5=0;
    String doseValue1="", doseValue2="", doseValue3="", doseValue4="", doseValue5="";
    double conv = 7.508e-5;
    
    public DoseFunc(double delta, double cellmass, double xnuclmass, double cytomass){
        //this.absf1 = absf1;
        //this.absf2 = absf2;
        //this.absf3 = absf3;
        //this.absf4 = absf4;
        //this.absf5 = absf5;
        this.cellmass = cellmass;
        this.xnuclmass = xnuclmass;
        this.cytomass = cytomass;
        this.delta = delta;      
        
    }
    
    public double DoseCal1(double absf1) {
        //System.out.println("xnuclmass="+xnuclmass +" " +"cellmass="+ cellmass); 
        //System.out.println("absf1 = "+absf1 );     
        dose1 = conv*absf1*delta*act/cellmass;
        //doseValue1 = formatter.format(dose1);
        //return doseValue1;
        return dose1;
    }
    
    public double DoseCal2(double absf2) {
        
        dose2 = conv*absf2*delta*act/cellmass;
        return dose2;
    }
       
    public double DoseCal3(double absf3) {

        dose3 = conv*absf3*delta*act/xnuclmass;
        return dose3;
    }
          
    public double DoseCal4(double absf4) {

        dose4 = conv*absf4*delta*act/xnuclmass;
	    return dose4;
    }
             
    public double DoseCal5(double absf5) {

        dose5 = conv*absf5*delta*act/xnuclmass;
        return dose5;
    }
    
  
        /**
        File out = new File( "C:/Documents and Settings/johnnywu/My Documents/UMDNJ/output/CellDose_Output_11_03.txt" );
        FileWriter fw = new FileWriter(out);
        PrintWriter pw = new PrintWriter( fw );

        pw.println( "dose(j,1) = "+doseValue1);
        fw.close();
         */
     

}
