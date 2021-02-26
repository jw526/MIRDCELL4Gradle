package File;


import java.io.*;
import java.net.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import GUI.Home1;
import javax.swing.JTextArea;

/**
 *
 * @author Johnny  Wu
 */
public class WriteToOutputFile {
    
    String directory = "";
    URL url;
    URLConnection urlConn;

   // CellApplet cellApplet; //07/01
    
    public WriteToOutputFile(){  
        //constructor
    }

    //updated 07/20/09
    public void createNewIsoFile(String newIso){
        String dir = "/DATA FILES/data/";
        String fileName = newIso;
        File f = new File(dir, fileName);
        directory = f.getPath();
        

    }
    public URL createOutputFile(String iso, double rc, double rn){
        //String dir = "/output/";
        
        String dir = "https://mirdcell.njms.rutgers.edu/UMDNJ/OUTPUT/";  //04/01
        String fileName = dir+iso.toLowerCase(Locale.ENGLISH)+"."+String.valueOf((int)rc)+String.valueOf((int)rn);
        try {
            //04/01
            url = new URL(fileName);
            urlConn = url.openConnection();
            urlConn.setDoInput(true);
        } catch (IOException ex) {
            Logger.getLogger(WriteToOutputFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return url;
        //File f = new File(dir, fileName);
        //directory = f.getPath();
        //return directory;
    }
    
    public void wirteToFile(){
        try{
            //04/01
            System.out.println("04/01 web dir = "+ directory);
            BufferedWriter bw = new BufferedWriter(new FileWriter(directory));           
            //bw.write("C <-- C     C <-- CS     N <-- N      N <-- Cy      N <-- CS \n");
            bw.write("\n");
            bw.close();
        } catch(IOException ioe){
            //System.out.println(ioe);
        }
        
    }

    //write self-dose to file
    //07/01
    public void writeSelfToFile(String doseValue1, String doseValue2, String doseValue3, String doseValue4, String doseValue5, String doseValue6, String doseValue7, String doseValue8, JTextArea jTextArea1){
       //System.out.println("04/01 self-dir="+directory);
       //try{


          //BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
          //07/01
            jTextArea1.append("Self-S" +"   "+ doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"     " + doseValue6+"     " +doseValue7+"     " +doseValue8+"\n");
           //bw.write("Self-S" +"   "+ doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"     " + "\n");
          // bw.close();

      // }catch(IOException ioe){
           //
       //}
    }

    /**
     * 07/01 void for testing
    public void writeSelfToFile(String doseValue1, String doseValue2, String doseValue3, String doseValue4, String doseValue5, URL directory){
       System.out.println("04/01 self-dir="+directory);
       try{
           //04/01
           urlConn = directory.openConnection();
           urlConn.setDoInput(true);
           urlConn.setUseCaches(false);
           
          // BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
          // bw.write("Self-S" +"   "+ doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"     " + "\n");
          // bw.close();

       }catch(IOException ioe){
           //
       }
    }
     */

    public void writeToFile(int k, String doseValue1, String doseValue2, String doseValue3, String doseValue4, String doseValue5, String directory){
       //System.out.println("directory="+directory);
       try{
           BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
           bw.write(k +"       "+ doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"     " + "\n");
           bw.close();

       }catch(IOException ioe){
           //
       }
    }

    public void writeInBetweenToFile(String doseValue1, String doseValue2, String doseValue3, String doseValue4, String doseValue5, String doseValue6, String doseValue7, String doseValue8, JTextArea jTextArea1){
       //System.out.println("directory="+directory);
        /**
       try{
           BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));           
           bw.write("             "+ doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"     " + "\n");
           bw.close();
           
       }catch(IOException ioe){
           //
       }
         */
        //07/01
           jTextArea1.append("            " + doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"    "+doseValue5+"    " + doseValue6+"    " +doseValue7+"   " +doseValue8+"\n");

    }   

    /**
    //write doselist to output file
    public void writeToFile(ArrayList doseList1, String directory){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
            bw.write(doseList1.toString());
            bw.close();
        }catch(IOException ioe){
            //
        }
        
    }

    //write crossdose value1
    public void writeToFile(String doseValue1, String directory){
       //System.out.println("directory="+directory);
       try{
           BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
           bw.write(doseValue1+"     ");
           bw.close();

       }catch(IOException ioe){
           //
       }
    }
    public void writeToFile(String doseValue2, String directory){
       //System.out.println("directory="+directory);
       try{
           BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
           bw.write(doseValue2+"     ");
           bw.close();

       }catch(IOException ioe){
           //
       }
    }
    public void writeToFile(String doseValue1, String directory){
       //System.out.println("directory="+directory);
       try{
           BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
           bw.write(doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"\n");
           bw.close();

       }catch(IOException ioe){
           //
       }
    }
    public void writeToFile(String doseValue1, String directory){
       //System.out.println("directory="+directory);
       try{
           BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
           bw.write(doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"\n");
           bw.close();

       }catch(IOException ioe){
           //
       }
    }
    public void writeToFile(String doseValue1, String directory){
       //System.out.println("directory="+directory);
       try{
           BufferedWriter bw = new BufferedWriter(new FileWriter(directory, true));
           bw.write(doseValue1+"     "+doseValue2+"     "+doseValue3+"     "+doseValue4+"     "+doseValue5+"\n");
           bw.close();

       }catch(IOException ioe){
           //
       }
    }
    */

    public void writeToTextArea(String doseValue1, String doseValue2, String doseValue3, String doseValue4, String doseValue5) {


    }
}
