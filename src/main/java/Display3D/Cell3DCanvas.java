/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Display3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;

/**
 * @author Behrooz
 */
public class Cell3DCanvas extends JPanel {

	public int azimuth = 35, elevation = 30;

	public double Zoom = 2;
	public double ZoomReset = 2;
	public double[][] cell;
	public int Radius = 200;
	public int r = 10;
	public boolean drawcircle = false;
        //JCW 5/14/20 added flags for the DL,DU, LL, LU (dead/live, labeled/unlabeled)
        public boolean dlFlag = true, duFlag =true, llFlag = true, luFlag = true;

	public Cell3DCanvas() {
		setSize( 100, 100 );
	}

	@Override
	public void paint(Graphics g) {
		super.paint( g );
		double theta = Math.PI * azimuth / 180.0;
		double phi = Math.PI * elevation / 180.0;
		float cosT = (float) Math.cos( theta ), sinT = (float) Math.sin( theta );
		float cosP = (float) Math.cos( phi ), sinP = (float) Math.sin( phi );
		float cosTcosP = cosT * cosP, cosTsinP = cosT * sinP, sinTcosP = sinT * cosP, sinTsinP = sinT * sinP;

		float near = 3000;  // distance from eye to near plane
		float nearToObj = 1500f;  // distance from near plane to center of object
		// project vertices onto the 2D viewport
		Point[] points;
		try {
			int rad = (int) (r * Zoom);
			points = new Point[cell.length];
			int j;

			for(j = 0; j < cell.length; ++j) {
				int x0 = (int) (cell[j][1]);
				int y0 = (int) (cell[j][2]);
				int z0 = (int) (cell[j][3]);
				// compute an orthographic projection
				float x1 = cosT * x0 + sinT * z0;
				float y1 = -sinTsinP * x0 + cosP * y0 + cosTsinP * z0;

				// now adjust things to get a perspective projection
				float z1 = cosTcosP * z0 - sinTcosP * x0 - sinP * y0;
				x1 = x1 * near / (z1 + near + nearToObj);
				y1 = y1 * near / (z1 + near + nearToObj);

				// the 0.5 is to round off when converting to int
				points[j] = new Point(
						(int) (this.getWidth() / 2 + (x1 * Zoom)),
						(int) (this.getHeight() / 2 + (y1 * Zoom)) );
			}

			// plots cells
			g.setColor( Color.white );
			g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
			Color DL = new Color( 255, 0, 0, 30 );
			Color LL = new Color( 255, 0, 0, 150 );
			Color DU = new Color( 0, 153, 0, 30 );
			Color LU = new Color( 0, 153, 0, 150 );
                        for(j = 0; j < cell.length; ++j){
                            if(cell[j][0] == 0 && cell[j][4] != 0 && dlFlag) {
                                    g.setColor( DL );
                                    if(Zoom > 2)
                                    g.fillOval( points[j].x - rad, points[j].y - rad, rad * 2, rad * 2 );
                                    else
                                    g.fillRect( points[j].x - 1, points[j].y - 1, 2, 2 );
                            }
                            else if(cell[j][0] == 0 && cell[j][4] == 0 && duFlag) {
                                    g.setColor( DU );
                                    if(Zoom > 2)
                                    g.fillOval( points[j].x - rad, points[j].y - rad, rad * 2, rad * 2 );
                                    else
                                    g.fillRect( points[j].x - 1, points[j].y - 1, 2, 2 );
                            }
                            else if(cell[j][0] != 0 && cell[j][4] != 0 && llFlag) {
                                    g.setColor( LL );
                                    if(Zoom > 2)
                                    g.fillOval( points[j].x - rad, points[j].y - rad, rad * 2, rad * 2 );
                                    else
                                    g.fillRect( points[j].x - 1, points[j].y - 1, 2, 2 );
                            }
                            else if(cell[j][0] != 0 && cell[j][4] == 0 && luFlag) {
                                    g.setColor( LU );
                                    if(Zoom > 2)
                                    g.fillOval( points[j].x - rad, points[j].y - rad, rad * 2, rad * 2 );
                                    else
                                    g.fillRect( points[j].x - 1, points[j].y - 1, 2, 2 );
                            }

                            //g.drawString(Integer.toString(cells[j].getCoord().x)+Integer.toString(cells[j].getCoord().y)+Integer.toString(cells[j].getCoord().z), points[j].x, points[j].y);
                        }
		}
		catch(Exception e) {
		}

		//Drawing the Axes
		//X
		int xAxe = Radius;
		float x1 = cosT * xAxe;
		float y1 = -sinTsinP * xAxe;
		float z1 = -sinTcosP * xAxe;
		x1 = x1 * near / (z1 + near + nearToObj);
		y1 = y1 * near / (z1 + near + nearToObj);
		g.setColor( Color.BLACK );
		paintArrow( g, this.getWidth() / 2, this.getHeight() / 2, (int) (this.getWidth() / 2 + (x1 * Zoom)), (int) (this.getHeight() / 2 - (y1 * Zoom)) );
		g.drawString( Radius + "\u00B5" + "M", (int) (this.getWidth() / 2 + (x1 * Zoom)), (int) (this.getHeight() / 2 - (y1 * Zoom)) );
		int yAxe = Radius;
		x1 = 0;
		y1 = cosP * yAxe;
		z1 = -sinP * yAxe;
		x1 = x1 * near / (z1 + near + nearToObj);
		y1 = y1 * near / (z1 + near + nearToObj);
		paintArrow( g, this.getWidth() / 2, this.getHeight() / 2, (int) (this.getWidth() / 2 + (x1 * Zoom)), (int) (this.getHeight() / 2 - (y1 * Zoom)) );
		int zAxe = -Radius;
		x1 = sinT * zAxe;
		y1 = cosTsinP * zAxe;
		z1 = cosTcosP * zAxe;
		x1 = x1 * near / (z1 + near + nearToObj);
		y1 = y1 * near / (z1 + near + nearToObj);
		paintArrow( g, this.getWidth() / 2, this.getHeight() / 2, (int) (this.getWidth() / 2 + (x1 * Zoom)), (int) (this.getHeight() / 2 - (y1 * Zoom)) );


	}

	// BEHROOZ 06/15/2012
	private void paintArrow(Graphics g, int x0, int y0, int x1, int y1) {
// BEHROOZ 06/15/2012

		int deltaX = x1 - x0;
		int deltaY = y1 - y0;
		double frac = 0.02;

		g.drawLine( x0, y0, x1, y1 );
		g.drawLine( x0 + (int) ((1 - frac) * deltaX + frac * deltaY),
				y0 + (int) ((1 - frac) * deltaY - frac * deltaX),
				x1, y1 );
		g.drawLine( x0 + (int) ((1 - frac) * deltaX - frac * deltaY),
				y0 + (int) ((1 - frac) * deltaY + frac * deltaX),
				x1, y1 );

	}
}
