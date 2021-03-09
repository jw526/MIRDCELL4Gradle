package GUI;

//Cell modeling on java.canvas
//functions based on Darshan's Tester6.java && DrawingCanvas.java
//authur:johnny@umdnj 04/08

import java.awt.*;
import javax.swing.JPanel;


public class CellCanvas extends JPanel {

	int factor = 26;  //factor must always be an even number to avoid rounding
	//Consistance with int factor in program Tester6.java
	//must be preserved.
	Cell source = new Cell(5 * factor, 3 * factor);
	Cell target = new Cell(5 * factor, 3 * factor);
	//String isotope = "Ac-225";
	//String selfDose = "Self-Dose : No Entry";
	//String crossDose = "Cross-Dose : No Entry";

	String isotope = "Radionuclide/Emission Selected:";
	String selfDose = "Self-Dose to Source Cell :";
	String crossDose = "Cross-Dose to Target Cell :";

	String distance = "10";
	int remainder = (source.getRC() - source.getRN()) / 2;
	int pro;

	Color red = new Color(250, 30, 0);

	Color white = new Color(255, 255, 255);
	Color black = new Color(0, 0, 0);
	Color blue = new Color(0, 200, 255);
	//process
	int process;
	//10/25/2010 add jTextField1, jTextField2
	String text1 = "", text2 = "";

	public CellCanvas() {
		source.setX(target.getX() + target.getRC());
		this.pro = process;

		setSize(400, 320); //set size of cell canvas
	}

	@Override
	public void paint(Graphics g) {
		if (pro == 0) //Whole Cell to Whole Cell
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(source.getX(), source.getY(), source.getRC() + 1, source.getRC() + 1);
			g.setColor(blue);
			g.fillOval(target.getX(), target.getY(), target.getRC() + 1, target.getRC() + 1);
		} else if (pro == 1) //Cell Surface to Whole Cell
		{
			//Source coloring
			super.paint(g);
			g.setColor(red);
			g.fillOval(source.getX() - 3, source.getY() - 3, source.getRC() + 6, source.getRC() + 6);
			g.setColor(white);
			g.fillOval(source.getX(), source.getY(), source.getRC() + 1, source.getRC() + 1);

			//Target coloring
			g.setColor(blue);
			g.fillOval(target.getX(), target.getY(), target.getRC() + 1, target.getRC() + 1);
		} else if (pro == 2) //Nucleus to Nucleus
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(source.getX() + remainder, source.getY() + remainder, source.getRN() + 1, source.getRN() + 1);
			g.setColor(blue);
			g.fillOval(target.getX() + remainder, target.getY() + remainder, target.getRN() + 1, target.getRN() + 1);


		} else if (pro == 3) //Cytoplasm to Nucleus
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(source.getX(), source.getY(), source.getRC() + 1, source.getRC() + 1);
			g.setColor(white);
			g.fillOval(source.getX() + remainder, source.getY() + remainder, source.getRN() + 1, source.getRN() + 1);

			//Target
			g.setColor(blue);
			g.fillOval(target.getX() + remainder, target.getY() + remainder, target.getRN() + 1, target.getRN() + 1);
		} else if (pro == 4) //Surface to Nucleus
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(source.getX() - 3, source.getY() - 3, source.getRC() + 6, source.getRC() + 6);
			g.setColor(white);
			g.fillOval(source.getX(), source.getY(), source.getRC() + 1, source.getRC() + 1);

			//Target
			g.fillOval(target.getX(), target.getY(), target.getRC() + 1, target.getRC() + 1);
			g.setColor(blue);
			g.fillOval(target.getX() + remainder, target.getY() + remainder, target.getRN() + 1, target.getRN() + 1);
		}

		/****************
		 * 07/27/10
		 * add Cytoplasm<-Nucleus, Cytoplasm<-Cell Surface, Cytoplasm<-Cytoplasm
		 ****************/
		else if (pro == 5) //Cytoplasm<-Nucleus
		{
			super.paint(g);

			//Source Nucleus
			g.setColor(red);
			g.fillOval(source.getX() + remainder, source.getY() + remainder, source.getRN() + 1, source.getRN() + 1);

			//Target Cytoplasm
			g.setColor(blue);
			g.fillOval(target.getX(), target.getY(), target.getRC() + 1, target.getRC() + 1);
			g.setColor(white);
			g.fillOval(target.getX() + remainder, target.getY() + remainder, target.getRN() + 1, target.getRN() + 1);
		} else if (pro == 6) //Cytoplasm<-Cell Surface
		{
			super.paint(g);

			//Source Cell Surface
			g.setColor(red);
			g.fillOval(source.getX() - 3, source.getY() - 3, source.getRC() + 6, source.getRC() + 6);
			g.setColor(white);
			g.fillOval(source.getX(), source.getY(), source.getRC() + 1, source.getRC() + 1);

			//Target Cytoplasm
			g.setColor(blue);
			g.fillOval(target.getX(), target.getY(), target.getRC() + 1, target.getRC() + 1);
			g.setColor(white);
			g.fillOval(target.getX() + remainder, target.getY() + remainder, target.getRN() + 1, target.getRN() + 1);
		} else if (pro == 7) //Cytoplasm to Cytoplasm
		{
			super.paint(g);
			//Cytoplasm
			g.setColor(red);
			g.fillOval(source.getX(), source.getY(), source.getRC() + 1, source.getRC() + 1);
			g.setColor(white);
			g.fillOval(source.getX() + remainder, source.getY() + remainder, source.getRN() + 1, source.getRN() + 1);

			//Target Cytoplasm
			g.setColor(blue);
			g.fillOval(target.getX(), target.getY(), target.getRC() + 1, target.getRC() + 1);
			g.setColor(white);
			g.fillOval(target.getX() + remainder, target.getY() + remainder, target.getRN() + 1, target.getRN() + 1);
		}

		g.setColor(black);
		g.drawOval(source.getX(), source.getY(), source.getRC(), source.getRC());
		g.drawOval(source.getX() + remainder, source.getY() + remainder, source.getRN(), source.getRN());

		g.drawOval(target.getX(), target.getY(), target.getRC(), target.getRC());
		g.drawOval(target.getX() + remainder, target.getY() + remainder, target.getRN(), target.getRN());

		g.drawString(isotope, 20, 20);
		g.drawString(selfDose, 20, 40);
		g.drawString(crossDose, 20, 60);
		g.drawString("Target Cell", target.getX(), target.getY() - 5);
		g.drawString("Source Cell", source.getX(), source.getY() - 5);

		int targetX = target.getX() + target.getRC() / 2;
		int sourceX = source.getX() + source.getRC() / 2;
		int targetY = target.getY() + target.getRC() / 2;
		int sourceY = source.getY() + source.getRC() / 2;

		//Distance Line
		g.drawLine(targetX, targetY, sourceX, sourceY);
		g.drawString(distance + " μm", targetX - 20 + (sourceX - targetX) / 2, targetY - 10);

		//Inner Cell Arrows
		Arrow arrowTargetNucleus = new Arrow();
		arrowTargetNucleus.drawArrow(g, targetX, targetY, Math.PI / 4, target.getRN() / 2, Arrow.SIDE_LEAD);
		Arrow arrowTargetCell = new Arrow();
		arrowTargetCell.drawArrow(g, targetX, targetY, 7 * Math.PI / 4, target.getRC() / 2, Arrow.SIDE_LEAD);
		Arrow arrowSourceNucleus = new Arrow();
		arrowSourceNucleus.drawArrow(g, sourceX, sourceY, 3 * Math.PI / 4, source.getRN() / 2, Arrow.SIDE_LEAD);
		Arrow arrowSourceCell = new Arrow();
		arrowSourceCell.drawArrow(g, sourceX, sourceY, 5 * Math.PI / 4, source.getRC() / 2, Arrow.SIDE_LEAD);

		// 10/24/2010 show rn
		g.drawString(Integer.toString(target.getRN() / factor) + " μm", target.getX() + (target.getRC() / 2) - 10, target.getY() + (target.getRC() / 2) - 20);
		g.drawString(Integer.toString(source.getRN() / factor) + " μm", source.getX() + (source.getRC() / 2) - 10, source.getY() + (source.getRC() / 2) - 20);
		// 10/24/2010 show rc
		g.drawString(Integer.toString(target.getRC() / factor) + " μm", target.getX() + (target.getRC() / 2) - 10, target.getY() + (target.getRC() / 2) + 30);
		g.drawString(Integer.toString(source.getRC() / factor) + " μm", source.getX() + (source.getRC() / 2) - 10, source.getY() + (source.getRC() / 2) + 30);

		// 10/24/2010 show rn
		//g.drawString(text2+" μm",target.getX()+(target.getRC()/2)-10,target.getY()+(target.getRC()/2)-20);
		//g.drawString(text2+" μm",source.getX()+(source.getRC()/2)-10,source.getY()+(source.getRC()/2)-20);
		// 10/24/2010 show rc
		//g.drawString(text1+" μm",target.getX()+(target.getRC()/2)-10,target.getY()+(target.getRC()/2)+30);
		//g.drawString(text1+" μm",source.getX()+(source.getRC()/2)-10,source.getY()+(source.getRC()/2)+30);
		//System.out.println("10/25/2010 text on cell: "+text1);
	}

	public void setProcess(int p) {
		this.pro = p;
	}

	public void setIsotope(String s) {
		this.isotope = s;
	}

	public void setSelfDose(String s) {
		this.selfDose = s;
	}

	public void setCrossDose(String s) {
		this.crossDose = s;
	}

	public Cell getSourceCell() {
		return source;
	}

	public Cell getTargetCell() {
		return target;
	}

	public void resetNuclei() {
		if (source.getRC() == source.getRN()) {
			source.setRN(source.getRN() - factor);
			target.setRN(target.getRN() - factor);
		}
		remainder = (source.getRC() - source.getRN()) / 2;
	}

	public void setDistance(boolean b) {
		int tempD = 0;

		if (b) {
			tempD = Integer.parseInt(distance);
			tempD += 1;
			distance = Integer.toString(tempD);
			if (tempD < 50) //Adjust These Value to alter Max Length of Seperating Distance
			{
				source.setX(source.getX() + (factor / 2));
			}
			//System.out.println("tempD="+tempD);
		} else {

			tempD = Integer.parseInt(distance);   // could be error!!!!!!!!!!!
			//System.out.println("tempD2="+tempD);  //testing purpose

			//if temporary distance value = 2* source radius
			//System.out.println("2RC="+2*target.getRC()/factor);  //testing purpose
			if (tempD > 2 * target.getRC() / factor) {
				tempD -= 1;
				distance = Integer.toString(tempD);

				if (tempD < 50) {
					source.setX(source.getX() - (factor / 2));
				}
			} else if (tempD == 2 * target.getRC() / factor) {
				tempD = 2 * target.getRC() / factor;
				distance = Integer.toString(tempD);
				//distance remains to be 2*rc;
			}

			/**
			 if (tempD > 2* source.getRC() && tempD < 50 ) {

			 tempD -= 1;
			 distance = Integer.toString(tempD);
			 //source.setX(source.getX()-(factor/2));//Adjust These Value to alter Max Length of Seperating Distance

			 }

			 //distance < = 2*rc not allowed, two cells cannot be crossed
			 else if ( tempD == 2* source.getRC() ) {
			 tempD = 2* source.getRC();
			 distance = Integer.toString(tempD);
			 //distance remains to be 2rc;
			 }

			 // tempD
			 if(tempD >2*source.getRC()) {
			 tempD -= 1;
			 distance = Integer.toString(tempD);

			 if(tempD < 50) //Adjust These Value to alter Max Length of Seperating Distance
			 {
			 source.setX(source.getX()-(factor/2));
			 }
			 }
			 */

		}
	}

	public void enterDistance(int d) {
		if (d > 20) //Adjust These Value to alter Max Length of Seperating Distance
		{
			//revised 070810 when distance reach the max length of canvas
			source.setX(target.getX() + target.getRC() + (20 * factor / 2) - (source.getRC()));
		} else if (d < 21) {
			source.setX(target.getX() + target.getRC() + (d * factor / 2) - (source.getRC()));
		}
		distance = Integer.toString(d);
	}

	//distance reset: possible error happens! when rc changes!
	public void resetDistance() {
		//should add 2 cases!!
		source.setX(target.getX() + target.getRC());
		distance = Integer.toString(2 * target.getRC() / factor);

		/**
		 *distance value:
		 *case 1:
		 */
	}


}
