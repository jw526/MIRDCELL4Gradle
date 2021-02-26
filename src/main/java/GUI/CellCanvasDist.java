package GUI;

//Cell modeling on java.canvas
//functions based on Darshan's Tester6.java && DrawingCanvas.java
//authur:johnny@umdnj 04/08

import java.awt.*;
import javax.swing.*;

public class CellCanvasDist extends JPanel {

	int factor = 30;  //factor must always be an even number to avoid rounding
	//Consistance with int factor in program Tester6.java
	//must be preserved.
	public Cell source = new Cell( 5 * factor, 3 * factor );
	public Cell target = new Cell( 5 * factor, 3 * factor );
	//String isotope = "Ac-225";
	//String selfDose = "Self-Dose : No Entry";
	//String crossDose = "Cross-Dose : No Entry";
	String isotope = "Radionuclide/Emission Selected:";
	String selfDose = "Self-Dose to Source Cell :";
	String crossDose = "Cross-Dose to Target Cell :";
	String distance = "10";
	public int Dist = 10;
	int remainder = (source.getRC() - source.getRN()) / 2;
	int pro;
	Color red = new Color( 250, 30, 0 );
	Color white = new Color( 255, 255, 255 );
	Color black = new Color( 0, 0, 0 );
	Color blue = new Color( 0, 200, 255 );
        Color background = new Color(204, 204, 255);
	private final Color green = new Color(77, 212, 77);

	//process
	int process =1;
	//10/25/2010 add jTextField1, jTextField2
	String text1 = "", text2 = "";
	double N, Cy, CS;

	public CellCanvasDist() {

		this.pro = process;
		target.setX( 175 );
		target.setY( 200 );
		source.setX( target.getX() + target.getRC() );
		source.setY( 200 );
		N = 100.0;
		Cy = 0;
		CS = 0;
		//setSize(400,320); //set size of cell canvas
	}

	@Override
	public void paint(Graphics g) {
            super.paint(g);
		int sourceRC = source.getRC();
		int sourceRN = source.getRN();
		int targetRC = target.getRC();
		int targetRN = target.getRN();
		if(sourceRC > 6 * factor) {
			targetRC = 7 * factor;
			targetRN = (int) (sourceRN * 7 * factor / (double) sourceRC);
			System.err.println( targetRN );
			sourceRN = targetRN;
			sourceRC = targetRC;
		}
                g.setFont(new Font("Arial", Font.PLAIN, 13)); 


		//if(Dist<20)
		//{
		int nDist = (int) (Dist * factor * targetRC * .5 / (double) source.getRC());
		if(nDist < 400)
			source.setX( target.getX() + nDist );
		else
			source.setX( target.getX() + 400 );
		//}else
		// source.setX(175+(15)*factor);


		int targetX = target.getX() - targetRC / 2;
		int sourceX = source.getX() - sourceRC / 2;
		int targetY = target.getY() - targetRC / 2;
		int sourceY = source.getY() - sourceRC / 2;
		remainder = targetRC / 2 - targetRN / 2;
                int remaiderSrc = sourceRC/2 - sourceRN/2;

		// draw source cell regions from outside in
		// Cell surface
		g.setColor(background);
		g.fillOval(sourceX - 4, sourceY - 4, sourceRC + 8, sourceRC + 8);
		g.setColor(new Color(255, 0, 0, (int)(CS * 255/100.0) )); // red scaled to activity
		g.fillOval(sourceX - 4, sourceY - 4, sourceRC + 8, sourceRC + 8);
                g.setColor(background);
                g.fillOval(sourceX, sourceY, sourceRC, sourceRC);
		// Cytoplasm                 
                g.setColor(new Color(255, 0, 0, (int)(Cy * 255/100.0))); // red scaled to activity
                g.fillOval(sourceX, sourceY, sourceRC, sourceRC);
//                g.setColor(Color.WHITE); 
//                g.fillOval(sourceX + remainder, sourceY + remainder, sourceRN, sourceRN);
		
		//nucleus (if its there)
		if(pro != 0){
			g.setColor(background);
			g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
			g.setColor(new Color(255, 0, 0, (int)(N * 255/100.0) ));
			g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
                }

		//target regions
		if(pro == 0){
			//cell
                        g.setColor( blue );
			g.fillOval(targetX, targetY, targetRC + 1, targetRC + 1);
                        g.fillOval(sourceX, sourceY, sourceRC, sourceRC);                      
                        if (Cy != 0){
                            for (int i = sourceRC; i > 0; i = i-9 ){
                                if (i%2 == 0) {
                                    g.setColor( red );
                                } else {
                                    g.setColor( blue );
                                }
                                g.fillOval(sourceX + (sourceRC - i)/2, sourceY + (sourceRC - i)/2, i, i);
                            }
                        }
                        //g.fillOval(sourceX, sourceY, sourceRC, sourceRC);
		}
		else if(pro == 1){
			// nucleus
			g.setColor( blue );
			g.fillOval( targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1 );
                        g.fillOval(sourceX + remainder , sourceY + remainder, targetRN + 1, targetRN + 1);
                        if (N != 0){
                            for (int i = sourceRN; i > 0; i = i-9 ){
                                if (i%2 == 0) {
                                    g.setColor( red );
                                } else {
                                    g.setColor( blue );
                                }
                                g.fillOval(sourceX + remainder + (sourceRN - i)/2, sourceY + remainder + (sourceRN - i)/2, i, i);
                            }
                        }
		}
		else if(pro == 2){
			//cytoplasm
			g.setColor( blue );
			g.fillOval( targetX, targetY, targetRC + 1, targetRC + 1 );
			g.setColor( background );
			g.fillOval( targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1 );
                        //set source cyto blue as well
                        g.setColor( blue );
			g.fillOval( sourceX, sourceY, sourceRC + 1, sourceRC + 1 );
                        if(N == 0){
                            g.setColor( background );
                            g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
                        } else {
                            g.setColor(red);
                            g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
                        }
			
                        if (Cy > 0.0){
                            for (int i = sourceRC; i > sourceRN; i = i-9 ){
                                if (i%2 == 0) {
                                    g.setColor( red );
                                } else {
                                    g.setColor( blue );
                                }
                                g.fillOval(sourceX + (sourceRC - i)/2, sourceY + (sourceRC - i)/2, i, i);
                            }
                            if(N == 0){
                                g.setColor( background );
                                g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
                            } else {
                                g.setColor(red);
                                g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
                            }
                        }
                        
		}
		else if(pro == 3){
			// nucleus and cytoplasm
			g.setColor( green );
			g.fillOval( targetX, targetY, targetRC + 1, targetRC + 1 );
			g.setColor( blue );
			g.fillOval( targetX + remainder, targetY + remainder, targetRN + 1, targetRN + 1 );
                        //JCW 8/4/20
                        
                        if (Cy > 0.0){
                            for (int i = sourceRC; i > sourceRN; i = i-9 ){
                                if (i%2 == 0) {
                                    g.setColor( red );
                                } else {
                                    g.setColor( green );
                                }
                                g.fillOval(sourceX + (sourceRC - i)/2, sourceY + (sourceRC - i)/2, i, i);
                            }
                            if(N == 0){
                                g.setColor( blue );
                                g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
                            } else {
                                for (int i = sourceRN; i > 0; i = i-9 ){
                                    if (i%2 == 0) {
                                        g.setColor( red );
                                    } else {
                                        g.setColor( blue );
                                    }
                                    g.fillOval(sourceX + remainder + (sourceRN - i)/2, sourceY + remainder + (sourceRN - i)/2, i, i);
                                }
                            }
                        } else{
                            g.setColor( green );
                            g.fillOval( sourceX, sourceY, sourceRC + 1, sourceRC + 1 );
                            if(N == 0){
                                g.setColor( blue );
                                g.fillOval( sourceX + remainder, sourceY + remainder, sourceRN + 1, sourceRN + 1 );
                            } else {
                                for (int i = sourceRN; i > 0; i = i-9 ){
                                    if (i%2 == 0) {
                                        g.setColor( red );
                                    } else {
                                        g.setColor( blue );
                                    }
                                    g.fillOval(sourceX + remainder + (sourceRN - i)/2, sourceY + remainder + (sourceRN - i)/2, i, i);
                                }
                            }
                            
                        }
		}


		g.setColor( black );
		g.drawOval( sourceX, sourceY, sourceRC, sourceRC );
		g.drawOval( sourceX + remainder, sourceY + remainder, sourceRN, sourceRN );

		g.drawOval( targetX, targetY, targetRC, targetRC );
		g.drawOval( targetX + remainder, targetY + remainder, targetRN, targetRN );


        /*
         * g.drawString(isotope, 20,20); g.drawString(selfDose, 20,40);
         * g.drawString(crossDose, 20,60); g.drawString("Target Cell",
         * target.getX(),target.getY()-5); g.drawString("Source Cell",
         * source.getX(),source.getY()-5);
         */

		//Distance Line

		g.drawString( isotope, 20, 20 );
		g.drawString( selfDose, 20, 40 );
		g.drawString( crossDose, 20, 60 );
		g.drawString( "", target.getX(), target.getY() - 5 );
		g.drawString( "", source.getX(), source.getY() - 5 );
                

		targetX = target.getX();
		sourceX = source.getX();
		targetY = target.getY();
		sourceY = source.getY();
		g.drawLine( targetX, targetY, sourceX, sourceY );
		g.drawString( Integer.toString( Dist ) + " μm", targetX - 20 + (sourceX - targetX) / 2, targetY - 10 );
		//Inner Cell Arrows
		Arrow arrowTargetNucleus = new Arrow();
		arrowTargetNucleus.drawArrow( g, targetX, targetY, Math.PI / 4, targetRN / 2, Arrow.SIDE_LEAD );
		Arrow arrowTargetCell = new Arrow();
		arrowTargetCell.drawArrow( g, targetX, targetY, 7 * Math.PI / 4, targetRC / 2, Arrow.SIDE_LEAD );
		Arrow arrowSourceNucleus = new Arrow();
		arrowSourceNucleus.drawArrow( g, sourceX, sourceY, 3 * Math.PI / 4, sourceRN / 2, Arrow.SIDE_LEAD );
		Arrow arrowSourceCell = new Arrow();
		arrowSourceCell.drawArrow( g, sourceX, sourceY, 5 * Math.PI / 4, sourceRC / 2, Arrow.SIDE_LEAD );

		g.drawString( Integer.toString( target.rn / factor ) + " μm", target.getX() - 10, target.getY() - 20 );
		g.drawString( Integer.toString( source.rn / factor ) + " μm", source.getX() - 10, source.getY() - 20 );
		// 10/24/2010 show rc
		g.drawString( Integer.toString( target.rc / factor ) + " μm", target.getX() - 10, target.getY() + 30 );
		g.drawString( Integer.toString( source.rc / factor ) + " μm", source.getX() - 10, source.getY() + 30 );
		// 10/24/2010 show rn
		//g.drawString(text2+" μm",target.getX()+(target.getRC()/2)-10,target.getY()+(target.getRC()/2)-20);
		//g.drawString(text2+" μm",source.getX()+(source.getRC()/2)-10,source.getY()+(source.getRC()/2)-20);
		// 10/24/2010 show rc
		//g.drawString(text1+" μm",target.getX()+(target.getRC()/2)-10,target.getY()+(target.getRC()/2)+30);
		//g.drawString(text1+" μm",source.getX()+(source.getRC()/2)-10,source.getY()+(source.getRC()/2)+30);
		//System.out.println("10/25/2010 text on cell: "+text1);
                g.drawString("Source Cell", sourceX-30, 300);
                g.drawString("Neighbor Cell", targetX-35, 300);
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
		if(source.getRC() == source.getRN()) {
			source.setRN( source.getRN() - factor );
			target.setRN( target.getRN() - factor );
		}
		remainder = (source.getRC() - source.getRN()) / 2;
	}

	public void setDistance(boolean b) {
		int tempD = 0;

		if(b) {
			tempD = Integer.parseInt( distance );
			tempD += 1;
			distance = Integer.toString( tempD );
			if(tempD < 50) //Adjust These Value to alter Max Length of Seperating Distance
			{
				source.setX( source.getX() + (factor / 2) );
			}
			//System.out.println("tempD="+tempD);
		}

		else {

			tempD = Integer.parseInt( distance );   // could be error!!!!!!!!!!!
			//System.out.println("tempD2="+tempD);  //testing purpose

			//if temporary distance value = 2* source radius
			//System.out.println("2RC="+2*target.getRC()/factor);  //testing purpose
			if(tempD > 2 * target.getRC() / factor) {
				tempD -= 1;
				distance = Integer.toString( tempD );

				if(tempD < 50) {
					source.setX( source.getX() - (factor / 2) );
				}
			}
			else if(tempD == 2 * target.getRC() / factor) {
				tempD = 2 * target.getRC() / factor;
				distance = Integer.toString( tempD );
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
		if(d > 20) //Adjust These Value to alter Max Length of Seperating Distance
		{
			//revised 070810 when distance reach the max length of canvas
			source.setX( target.getX() + target.getRC() + (20 * factor / 2) - (source.getRC()) );
		}
		else if(d < 21) {
			source.setX( target.getX() + target.getRC() + (d * factor / 2) - (source.getRC()) );
		}
		distance = Integer.toString( d );
	}

	//distance reset: possible error happens! when rc changes!
	public void resetDistance() {
		//should add 2 cases!!
		source.setX( target.getX() + target.getRC() );
		distance = Integer.toString( 2 * target.getRC() / factor );

		/**
		 *distance value:
		 *case 1:
		 */
	}

	public void updateSource(double N, double Cy, double CS){
                this.N = N;
                this.Cy = Cy;
                this.CS = CS;
	}

	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setSize(900, 900);
		CellCanvasDist cc = new CellCanvasDist();
		frame.add(cc);
		frame.show();
		cc.repaint();
	}
}
