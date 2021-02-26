/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package File;

/**
 *
 * @author Johnny  Wu
 */
public class MyList2 {
    
    private String[] str;
    
    public MyList2(String line) {
	    str = line.trim().split("\\s+"); //seperate each line by "whitespaces"
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

}
