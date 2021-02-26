package GUI;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Johnny  Wu
 */
public class Cell {

	public int x, y, rn, rc;
	
	public Cell(int rc, int rn)
	{
		this.x = 40;
		this.y = 150;
		this.rc = rc;
		this.rn = rn;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setRC(int rc)
	{
		this.rc = rc;
	}
	
	public int getRC()
	{
		return rc;
	}
	
	public void setRN(int rn)
	{
		this.rn = rn;
	}
	
	public int getRN()
	{
		return rn;
	}
}

