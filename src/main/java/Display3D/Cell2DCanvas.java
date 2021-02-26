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
public class Cell2DCanvas extends JPanel {
	public int jPanelWidth = 500;
	public double Zoom = 2;
	public double ZoomReset = 2;
	public double[][] cell;
	public int Radius = 200;
	public int r = 10;
	public Cell2DCanvas() {
		setSize( 100, 100 );
	}

	@Override
	public void paint(Graphics g) {
		super.paint( g );


		// project vertices onto the 2D viewport
		Point[] points;
		try {
			int rad = r;
			points = new Point[cell.length];
			int j;

			for(j = 0; j < cell.length; ++j) {
				int x1 = (int) (cell[j][1]);
				int y1 = (int) (cell[j][2]);
				//System.err.println(x1 + "---->" + y1);

				// compute an orthographic projection


				// the 0.5 is to round off when converting to int
				points[j] = new Point(
						(int) (jPanelWidth / 2 + (x1 - rad) * Zoom),
						(int) (jPanelWidth / 2 + (y1 - rad) * Zoom) );

				//System.err.println(points[j].x + "---->" + points[j].y);
			}

			// plots cells
			g.setColor( Color.white );
			g.fillRect( 0, 0, jPanelWidth, jPanelWidth );
			Color DL = new Color( 255, 0, 0, 30 );
			Color LL = new Color( 255, 0, 0, 150 );
			Color DU = new Color( 0, 153, 0, 30 );
			Color LU = new Color( 0, 153, 0, 150 );
			for(j = 0; j < cell.length; ++j) {

				if(cell[j][0] == 0 && cell[j][4] != 0) {
					g.setColor( DL );
				}
				else if(cell[j][0] == 0 && cell[j][4] == 0) {
					g.setColor( DU );
				}
				else if(cell[j][0] != 0 && cell[j][4] != 0) {
					g.setColor( LL );
				}
				else if(cell[j][0] != 0 && cell[j][4] == 0) {
					g.setColor( LU );
				}
				rad = (int) (r * Zoom);
				if(Zoom > .35)
					g.fillOval( points[j].x, points[j].y, rad * 2, rad * 2 );
				else
					g.fillRect( points[j].x, points[j].y, 2, 2 );
				//g.drawString(Integer.toString(cells[j].getCoord().x)+Integer.toString(cells[j].getCoord().y)+Integer.toString(cells[j].getCoord().z), points[j].x, points[j].y);
			}
		} catch(Exception e) {
		}

		//Drawing the Axes
		//X
		int xAxe = Radius;
		float x1 = xAxe;
		float y1 = 0;

		g.setColor( Color.BLACK );
		paintArrow( g, (int) (jPanelWidth / 2), (int) (jPanelWidth / 2), (int) (jPanelWidth / 2 + (x1 * Zoom)), (int) (jPanelWidth / 2 - (y1 * Zoom)) );
		g.drawString( Radius + "\u00B5" + "M", (int) (jPanelWidth / 2 + (x1 * Zoom)), (int) (jPanelWidth / 2 - (y1 * Zoom)) );
		int yAxe = Radius;
		x1 = 0;
		y1 = yAxe;

		paintArrow( g, (int) (jPanelWidth / 2), (int) (jPanelWidth / 2), (int) (jPanelWidth / 2 + (x1 * Zoom)), (int) (jPanelWidth / 2 - (y1 * Zoom)) );


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
