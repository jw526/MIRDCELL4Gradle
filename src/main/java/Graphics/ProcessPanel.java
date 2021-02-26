package Graphics;


import java.awt.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Johnny  Wu
 */
//process selection function
public class ProcessPanel {
    
       List l;
       ScrollPane sp = new ScrollPane();
      
	public ProcessPanel(ScrollPane sp) {
            this.sp = sp;
           String[] processes = {"Cell <-- Cell","Cell <-- Cell Surface","Nucleus <-- Nucleus","Nucleus <-- Cytoplasm","Nucleus <-- Cell Surface"};
		
		l = new List();
		
                int processLength = processes.length;
                System.out.printf("processLength= "+processLength+"\n");
                
		for(int i = 0; i <processes.length; i++)
		{
			l.add(processes[i]);
                        //System.out.println("i= "+i);
                        //System.out.println("process ("+i+") = "+processes[i]);
		}
		
		l.select(0);
                l.setFont(new java.awt.Font("Tahoma", 1, 12));
		
		sp.add(l);
                

		//add(sp);
	}
	
	public List getList() {
		return l;
	}
	
	public int getProcess() {
		return l.getSelectedIndex();
	}
   }