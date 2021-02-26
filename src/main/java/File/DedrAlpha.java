/*
 * DeDr function for Alpha particles
 * 
 * INTRAPOLATES THE dE/dX VALUES FROM THE ICRU REPORT # 49
 * 
 */

package File;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Johnny  Wu
 */
public class DedrAlpha {
    
    ArrayListIn2 ali = new ArrayListIn2();
    MyList2 mList2;    
    ArrayList<MyList2> list = new ArrayList<MyList2>();
    String fileName = "";
    double ekev = 0, range = 0, ener, rang, ded;
    double r, x, dedr = 0;
    
    public DedrAlpha(ArrayList<MyList2> list2){
        //this.fileName = alphaFile;
        this.list = list2;
        //ali = new ArrayListIn2(alphaFile);                
    }
    
    public double getDedrAlpha(double range, double r1){
        double z = range - r1;
        //3/25/09 System.out.println("z= "+z);
       // try {
            //list.addAll(ali.readAlpha(fileName));
           // list = ali.readAlpha(fileName);
            
            if(z>0){
                    
                for(int j = 1; j < 78; j++){

                    MyList2 mList21 = list.get(j);
                    MyList2 mList22 = list.get(j+1);
                    ener = Double.parseDouble(mList21.getLine1());
                    rang = Double.parseDouble(mList21.getLine2());
                    ded = Double.parseDouble(mList21.getLine3());
                     //System.out.println("ded= "+ded +"  rang="+rang);
                    //System.out.println("j="+j+" rang= "+rang + "  rang+1= "+ Double.parseDouble(mList22.getLine2()));
                    if(z == rang){
                        dedr = ded;
                    } 
                    else if(z > rang && z < Double.parseDouble(mList22.getLine2())){
                        
                        dedr = ded + (Double.parseDouble(mList22.getLine3())- ded)*(z - rang) /(Double.parseDouble(mList22.getLine2()) - rang);
                        //System.out.println("z fit"+" dedr="+dedr);
                    } 
                    //error for same dedr from j=55-77
                    // System.out.println("ded="+ded+"  ded+1 "+Double.parseDouble(mList22.getLine3())+"   dedr for Alpha = "+dedr+"\n");
                    //System.out.println("ded="+ded+"  ded+1 "+Double.parseDouble(mList22.getLine3()));
                    //System.out.println("z fit"+" dedr="+dedr); //error show , result repeated
                }//end loop
                //System.out.println("DEDR2 = "+dedr); 
            } else{
                dedr = 0;
                //System.out.println("dedr for Alpha = "+dedr);
            }
            //System.out.println("dedr="+dedr+"\n");
            //3/25/09  System.out.println("z fit"+" dedr="+dedr);
            
        /**
         * j=1,78
	if(z.eq.rang(j))then
	dedr1=ded(j)
	else if(z.gt.rang(j).and.z.lt.rang(j+1)) then
	dedr1=ded(j)+(ded(j+1)-ded(j))*(z-rang(j))/(rang(j+1)-rang(j))
         *
         */
        //} catch(IOException ioe){
        //    System.out.println(ioe);
            //
        //}
        return dedr;
    }

}
