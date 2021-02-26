package Graphics;


import java.awt.*;
import javax.swing.*; 


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Johnny  Wu
 */
 //define class for cell changes through buttons
   //repaint CellCanvas  
public class CellModify {
       
	int minimum = 0;
	int maximum= 100;
	int text1=0, text2=0, text3 = 0;
	boolean linked = false;
	boolean distanceLinked = false;
	CellModify distanceOther, other;
        JTextField jTextField1, jTextField2, jTextField3;
	Button button3, button4, button5, button6, button8, button9;


	public CellModify(String s, int min, int max)
	{
		Label label = new Label(s);
		this.minimum = min;
		this.maximum = max;
		
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//plus = new Button("+");
		//button9.addActionListener(this);
		
		//minus = new Button("-");
		//minus.addActionListener(this);
		
		jTextField1 = new JTextField();
                jTextField1.setEditable(true);
                jTextField2 = new JTextField();
                jTextField2.setEditable(true);
                jTextField3 = new JTextField();
                jTextField3.setEditable(true);
		//jTextField1.setText(Integer.toString(minimum));
		//jTextField1.setFocusable(true);
		
			
	}
	
        //get value from textField1 for rc
	public int getValue1()
	{
		return Integer.parseInt(jTextField1.getText());
	}	
	public void setValue1(int x)
	{
		this.text1 = x;
	}
        public void setEditable1(boolean b)
	{
		jTextField1.setEditable(b); //error: jTextField could not be accessed
	}
        //get value from textField2 for rn
        public int getValue2()
	{
		return Integer.parseInt(jTextField2.getText());
	}
	public void setValue2(int x)
	{
		this.text2 = x;
	}
        public void setEditable2(boolean b){
            jTextField2.setEditable(b);
        }
        //get value from textField3 for distance
        public int getValue3()
	{
		return Integer.parseInt(jTextField3.getText());
	}	
	public void setValue3(int x)
	{
		this.text3 = x;
	}
        public void setEditable3(boolean b){
            jTextField3.setEditable(b);
        }
        
	public void link(CellModify cm)
	{
		linked = true;
		this.other = cm;
	}
	
	public void distanceLink(CellModify cm)
	{
		distanceLinked = true;
		this.distanceOther = cm;
	}
	
	public void setMaximum(int x)
	{
		this.maximum = x;
	}
	
	public int getMaximum()
	{
		return maximum;
	}
	
	public void setMinimum(int x)
	{
		this.minimum = x;
	}
	
	public int getMinimum()
	{
		return minimum;
	}
	
        //buttons for rc
	public Button getPlusButton1()
	{
		return button9;
	}
	
	public Button getMinusButton1()
	{
		return button5;
	}
       
        //button for rn
        public Button getPlusButton2()
	{
		return button8;
	}
	
	public Button getMinusButton2()
	{
		return button3;
	}
       
        //button for distance
        public Button getPlusButton3()
	{
		return button4;
	}
	
	public Button getMinusButton3()
	{
		return button6;
	}
	
	public JTextField getTextField1()
	{
		return jTextField1;
	}
        public void setEditable(boolean b)
	{
		//this.jTextField1.setEditable(b);
	}
        
}
