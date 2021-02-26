/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package File;

import java.io.*;
import javax.swing.JTextArea;
import java.net.*;
import java.net.URI;
import java.util.ArrayList;


/**
 *
 * @author Johnny  Wu
 */
public class FileReadWrite {
    
    String fileName="NoName";
    //Clipboard clipboard=toolkit.getSystemClipboard();
    
    public FileReadWrite(){
        
    }
    
    public void readFile(String fileName, JTextArea jTextArea1) {
        
        try {
            //update 07/22/09
            System.out.println("mon0 open file name: "+ fileName);

            File file=new File(fileName);
            FileReader readIn=new FileReader(file); 
            int size=(int)file.length(); 
            int charsRead=0; 
            char[] content=new char[size]; 
            while(readIn.ready()) 
                charsRead+=readIn.read(content,charsRead,size-charsRead); 
            readIn.close(); 
            jTextArea1.setText(new String(content,0,charsRead)); 
        } catch(IOException e) { 
            System.out.println("Error Opening file"); //if io exception
        } 
    } 
    
    public void writeFile(String fileName, JTextArea jTextArea1) { 
        
        try { 
            File file=new File(fileName); 
            FileWriter writeOut=new FileWriter(file); 
            writeOut.write(jTextArea1.getText()); 
            writeOut.close(); 
        } catch(IOException e) { 
            System.out.println("Error writing file"); 
        } 
    }
    
    public String readFileName(String fileName, JTextArea jTextArea1) {
        
        try { 
            File file=new File(fileName);
            FileReader readIn=new FileReader(file); 
            int size=(int)file.length(); 
            int charsRead=0; 
            char[] content=new char[size]; 
            while(readIn.ready()) 
                charsRead+=readIn.read(content,charsRead,size-charsRead); 
            readIn.close(); 
            jTextArea1.setText(new String(content,0,charsRead)); 
        } catch(IOException e) { 
            System.out.println("Error Opening file"); //if io exception
        } 
        return fileName;
    }

    //02/19/2010
    //read file using URL
    public void readURLFile(String fileName, JTextArea jTextArea1){
        URL                url;
        URLConnection      urlConn;
        //0224
        StringBuffer strBuff;
        String line ="";

        try {
            
            url = new URL(fileName);
            //0224
            //System.out.println("URL:  "+url);

            urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);

            InputStream in = url.openStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            strBuff = new StringBuffer();
            while((line = bf.readLine()) != null){
                 strBuff.append(line + "\n");
            }

            jTextArea1.append(strBuff.toString());
            //0224
            //System.out.println("textarea2  "+ jTextArea1);
        } catch (MalformedURLException mue) {}
        catch(IOException ioe){
            ioe.printStackTrace();
        }

    }

}
