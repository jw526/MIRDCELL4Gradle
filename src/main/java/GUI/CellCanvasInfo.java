package GUI;

//Cell modeling on java.canvas
//functions based on Darshan's Tester6.java && DrawingCanvas.java
//authur:johnny@umdnj 04/08

import java.awt.*;
import javax.swing.JPanel;

public class CellCanvasInfo extends JPanel {

	int factor = 30;  //factor must always be an even number to avoid rounding
	//Consistance with int factor in program Tester6.java
	//must be preserved.
	public Cell source = new Cell(5 * factor, 3 * factor);
	public Cell target = new Cell(5 * factor, 3 * factor);
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

	public CellCanvasInfo() {

		this.pro = process; //?? what the heck does this mean anyways neither of these are set at this point
		target.setX(175);
		target.setY(150);
		source.setX(525);
		source.setY(150);
		//setSize(400,320); //set size of cell canvas
	}

	@Override
	public void paint(Graphics g) {


		int sourceRC = source.getRC();
		int sourceRN = source.getRN();
		int targetRC = target.getRC();
		int targetRN = target.getRN();
		if (sourceRC > 10 * factor) {
			targetRC = 10 * factor;
			targetRN = (int) (sourceRN * 10 * factor / (double) sourceRC);
			System.err.println(targetRN);
			sourceRN = targetRN;
			sourceRC = targetRC;


		}

		int targetX = target.getX() - targetRC / 2;
		int sourceX = source.getX() - sourceRC / 2;
		int targetY = target.getY() - targetRC / 2;
		int sourceY = source.getY() - sourceRC / 2;


		remainder = targetRC / 2 - targetRN / 2;
		if (pro == 0) //Whole Cell to Whole Cell
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(sourceX, sourceY, sourceRC + 1, sourceRC + 1);
			g.setColor(blue);
			g.fillOval(targetX, targetY, targetRC + 1, targetRC + 1);
		} else if (pro == 1) //Cell Surface to Whole Cell
		{
			//Source coloring
			super.paint(g);
			g.setColor(red);
			g.fillOval(sourceX - 3, sourceY - 3, sourceRC + 6, sourceRC + 6);
			g.setColor(white);
			g.fillOval(sourceX, sourceY, sourceRC + 1, sourceRC + 1);

			//Target coloring
			g.setColor(blue);
			g.fillOval(targetX, targetY, targetRC + 1, targetRC + 1);
		} else if (pro == 2) //Nucleus to Nucleus
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1);
			g.setColor(blue);
			g.fillOval(targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1);


		} else if (pro == 3) //Cytoplasm to Nucleus
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(sourceX, sourceY, sourceRC + 1, sourceRC + 1);
			g.setColor(white);
			g.fillOval(sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1);

			//Target
			g.setColor(blue);
			g.fillOval(targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1);
		} else if (pro == 4) //Surface to Nucleus
		{
			super.paint(g);
			g.setColor(red);
			g.fillOval(sourceX - 3, sourceY - 3, sourceRC + 6, sourceRC + 6);
			g.setColor(white);
			g.fillOval(sourceX, sourceY, sourceRC + 1, sourceRC + 1);

			//Target
			g.fillOval(targetX, targetY, targetRC + 1, targetRC + 1);
			g.setColor(blue);
			g.fillOval(targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1);
		} /**
		 * **************
		 * 07/27/10 add Cytoplasm<-Nucleus, Cytoplasm<-Cell Surface,
		 * Cytoplasm<-Cytoplasm
		 ***************
		 */
		else if (pro == 5) //Cytoplasm<-Nucleus
		{
			super.paint(g);

			//Source Nucleus
			g.setColor(red);
			g.fillOval(sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1);

			//Target Cytoplasm
			g.setColor(blue);
			g.fillOval(targetX, targetY, targetRC + 1, targetRC + 1);
			g.setColor(white);
			g.fillOval(targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1);
		} else if (pro == 6) //Cytoplasm<-Cell Surface
		{
			super.paint(g);

			//Source Cell Surface
			g.setColor(red);
			g.fillOval(sourceX - 3, sourceY - 3, sourceRC + 6, sourceRC + 6);
			g.setColor(white);
			g.fillOval(sourceX, sourceY, sourceRC + 1, sourceRC + 1);

			//Target Cytoplasm
			g.setColor(blue);
			g.fillOval(targetX, targetY, targetRC + 1, targetRC + 1);
			g.setColor(white);
			g.fillOval(targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1);
		} else if (pro == 7) //Cytoplasm to Cytoplasm
		{
			super.paint(g);
			//Cytoplasm
			g.setColor(red);
			g.fillOval(sourceX, sourceY, sourceRC + 1, sourceRC + 1);
			g.setColor(white);
			g.fillOval(sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1);

			//Target Cytoplasm
			g.setColor(blue);
			g.fillOval(targetX, targetY, targetRC + 1, targetRC + 1);
			g.setColor(white);
			g.fillOval(targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1);
		}

		g.setColor(black);
		g.drawOval(sourceX, sourceY, sourceRC, sourceRC);
		g.drawOval(sourceX + remainder, sourceY + remainder, sourceRN, sourceRN);

		g.drawOval(targetX, targetY, targetRC, targetRC);
		g.drawOval(targetX + remainder, targetY + remainder, targetRN, targetRN);

        /*
         * g.drawString(isotope, 20,20); g.drawString(selfDose, 20,40);
         * g.drawString(crossDose, 20,60); g.drawString("Target Cell",
         * target.getX(),target.getY()-5); g.drawString("Source Cell",
         * source.getX(),source.getY()-5);
         */

		targetX = target.getX();
		sourceX = source.getX();
		targetY = target.getY();
		sourceY = source.getY();

		//Inner Cell Arrows
		Arrow arrowTargetNucleus = new Arrow();
		arrowTargetNucleus.drawArrow(g, targetX, targetY, Math.PI / 4, targetRN / 2, Arrow.SIDE_LEAD);
		Arrow arrowTargetCell = new Arrow();
		arrowTargetCell.drawArrow(g, targetX, targetY, 7 * Math.PI / 4, targetRC / 2, Arrow.SIDE_LEAD);
		Arrow arrowSourceNucleus = new Arrow();
		arrowSourceNucleus.drawArrow(g, sourceX, sourceY, 3 * Math.PI / 4, sourceRN / 2, Arrow.SIDE_LEAD);
		Arrow arrowSourceCell = new Arrow();
		arrowSourceCell.drawArrow(g, sourceX, sourceY, 5 * Math.PI / 4, sourceRC / 2, Arrow.SIDE_LEAD);

		g.drawString(Integer.toString(target.rn / factor) + " μm", target.getX() - 10, target.getY() - 20);
		g.drawString(Integer.toString(source.rn / factor) + " μm", source.getX() - 10, source.getY() - 20);
		g.drawString(Integer.toString(target.rc / factor) + " μm", target.getX() - 10, target.getY() + 30);
		g.drawString(Integer.toString(source.rc / factor) + " μm", source.getX() - 10, source.getY() + 30);
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
	}

	//distance reset: possible error happens! when rc changes!
	public void resetDistance() {
	}
}
