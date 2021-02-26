package File;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Johnny  Wu
 */
 public class MyList {
    
    String[] str ={};
    String line ="";
    
    public MyList(String line) {
        //this.line = line;
        str = line.trim().split("\\s+"); //seperate each line by "whitespaces"
       
    }
    
    public String[] getAllList(){
        str = line.split("\\s+");
        return str;
    }
     
    
    public String getLine1() {
        return str[0];
    }
    public String getLine2() {
        return str[1];
    }
    public String getLine3() {
        return str[2];
    }
    public String getLine4(){
        return str[3];
    }
    public String getLine5(){
        return str[4];
    }

    //update 07/06
    public String getLine6(){
        return str[5];
    }
     
 }

