/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Display3D;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author Behrooz
 */
public class Cell3D {
    
int x=0;
int y=0;
int z=0;
int rc, rn;
public Color color = Color.GREEN;
public boolean RadioActive=false , live = true;
public double CrossAbsorbedDose=0;
public double SelfAbsorbedDose=0;
public double iRadioactivity=0;


double Survival = 1.0;



    public Cell3D ()
    {
 
    }
    
    public void setCoord (int x, int y , int z)
    {
    this.x=x;
    this.y=y;
    this.z=z;
    }
    
    public Point3D getCoord()
    {
    Point3D point = new Point3D(x,y,z);
    return point ;
    }
    
    public void setRadioActive(boolean Radioactive)
    {
    RadioActive=Radioactive;

    }
    public boolean getRadioActive()
    {
    return RadioActive;
    }
    
    public void setCrossAbsorbedDose(double d)
    {
    CrossAbsorbedDose= d;
    }
        
    public double getCrossAbsorbedDose()
    {
    return CrossAbsorbedDose;
    }
    
    public void setSelfAbsorbedDose(double d)
    {
    SelfAbsorbedDose= d;
    }
        
    public double getSelfAbsorbedDose()
    {
    return SelfAbsorbedDose;
    }
    
    public double getSurvival(double D37Cross ,double D37Self,Random randomGenerator )
    {
    Survival=Math.pow(Math.E, (-1*CrossAbsorbedDose/D37Cross))*Math.pow(Math.E, (-1*SelfAbsorbedDose/D37Self));
    double rand = randomGenerator.nextDouble();
            //System.out.println(rand + "-----------"+Survival);
    if(rand>Survival)
    {this.color=Color.RED;
    live=false;}
    
    
    return Survival;
    }
    
    public Color getColor()
    {
    return color;
    }
    
    
    
    
    
}
