/**
 * created by Alex Rosen
 * 7/13/2017
 */

package GUI;

import java.awt.*;
import javax.swing.*;

public class CellCanvasInfoNew extends JPanel {

	private final Color red = new Color(250, 30, 0);
	private final Color white = new Color(255, 255, 255);
	private final Color black = Color.BLACK;
	private final Color green = new Color(77, 212, 77);
	private final Color blue = new Color(0, 200, 255);
	private static final Color background = new Color(204, 204, 255);

	private int target = 1; // default target is the nucleus
	private int RC = 5;
	private int RN = 3;
        double n = 100.0, cy = 0.0, cs = 0.0;

	public CellCanvasInfoNew() {
		this.setPreferredSize(new Dimension(640, 491));
		this.setSize(new Dimension(640, 491));
		this.setBackground(background);
	}

	@Override
	public void paint(Graphics g) {

		//this.removeAll();
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(background);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		// target cell display
		switch (target) {
			case 0: /**< cell */
				g.setColor(blue);
				g.fillOval(60, 15, 120, 120);
				g.setColor(black);
				g.drawOval(60, 15, 120, 120);
				//g.drawOval(60 + 36, 15 + 36, 48, 48);
				break;
			case 1: /**< nucleus */
				g.setColor(black);
				g.drawOval(60, 15, 120, 120);
				g.setColor(blue);
				g.fillOval(60 + 36, 15 + 36, 48, 48);
				g.setColor(black);
				g.drawOval(60 + 36, 15 + 36, 48, 48);
				break;
			case 2: /**< cytoplasm */
				g.setColor(blue);
				g.fillOval(60, 15, 120, 120);
				g.setColor(black);
				g.drawOval(60, 15, 120, 120);
				g.setColor(background);
				g.fillOval(60 + 36, 15 + 36, 48, 48);
				g.setColor(black);
				g.drawOval(60 + 36, 16 + 36, 48, 48);
				break;
			case 3: /**< cytoplasm & nucleus */
				g.setColor(green);
				g.fillOval(60, 15, 120, 120);
				g.setColor(black);
				g.drawOval(60, 15, 120, 120);
				g.setColor(blue);
				g.fillOval(60 + 36, 15 + 36, 48, 48);
				g.setColor(black);
				g.drawOval(60 + 36, 15 + 36, 48, 48);
				break;
			default: /**< shouldn't be reachable */
				break;
		}

		if (target == 0) {
	    	/* cell surface */
                    if (cs > 0){
                       g.setColor(red); 
                    }			
                    else {
                        g.setColor(background);
                    }                      
                    g.fillOval(60 + 120 + 60 - 4, 15 - 4, 120 + 8, 120 + 8);
                    g.setColor(background);
                    g.fillOval(60 + 120 + 60, 15, 120, 120);
                    g.setColor(black);
                    g.drawOval(60 + 120 + 60, 15, 120, 120);

            /* cell */
                    if (cy > 0){
                       g.setColor(red); 
                    }			
                    else {
                        g.setColor(background);
                    }           
                    g.fillOval(60 + 120 + 60, 15 + 120 + 30, 120, 120);
                    g.setColor(black);
                    g.drawOval(60 + 120 + 60, 15 + 120 + 30, 120, 120);

		}
		else {
	        /* cell surface */
                    if (cs > 0){
                       g.setColor(red); 
                    }			
                    else {
                        g.setColor(background);
                    }           
                    g.fillOval(60 + 120 + 60 - 4, 15 - 4, 120 + 8, 120 + 8);
                    g.setColor(background);
                    g.fillOval(60 + 120 + 60, 15, 120, 120);
                    g.setColor(black);
                    g.drawOval(60 + 120 + 60, 15, 120, 120);
                    g.drawOval(60 + 120 + 60 + 36, 15 + 36, 48, 48);

                    /* cytoplasm */
                    if (cy > 0){
                       g.setColor(red); 
                    }			
                    else {
                       g.setColor(background);
                    }           
                    g.fillOval(60 + 120 + 60, 15 + 120 + 30, 120, 120);
                    g.setColor(black);
                    g.drawOval(60 + 120 + 60, 15 + 120 + 30, 120, 120);
                    g.setColor(background);
                    g.fillOval(60 + 120 + 60 + 36, 15 + 120 + 30 + 36, 48, 48);
                    g.setColor(black);
                    g.drawOval(60 + 120 + 60 + 36, 15 + 120 + 30 + 36, 48, 48);

                    /* nucleus */
                    
                    g.setColor(black);
                    g.drawOval(60 + 120 + 60, 15 + 120 + 30 + 120 + 30, 120, 120);
                    if (n > 0){
                       g.setColor(red); 
                    }			
                    else {
                        g.setColor(background);
                    }           
                    g.fillOval(60 + 120 + 60 + 36, 15 + 120 + 30 + 120 + 30 + 36, 48, 48);
                    g.setColor(black);
                    g.drawOval(60 + 120 + 60 + 36, 15 + 120 + 30 + 120 + 30 + 36, 48, 48);
		}

        /* draw the arrows for radii */
		// target
		Arrow arrow = new Arrow();
		arrow.drawArrow(g, 60 + 60, 75, Math.PI / 4D, 60, Arrow.SIDE_LEAD);
		arrow.drawArrow(g, 60 + 120 + 60 + 60, 15 + 60, Math.PI / 4D, 60, Arrow.SIDE_LEAD);
		arrow.drawArrow(g, 60 + 120 + 60 + 60, 15 + 60 + 30 + 120, Math.PI / 4D, 60, Arrow.SIDE_LEAD);

		if (target != 0) {
			//used when drawing the nucleus & third source cell only
			// RCs
			arrow.drawArrow(g, 60 + 120 + 60 + 60, 15 + 60 + 30 + 120 + 30 + 120, Math.PI / 4D, 60, Arrow.SIDE_LEAD);

			// RNs
			arrow.drawArrow(g, 60 + 60, 15 + 60, Math.PI / -4D, 24, Arrow.SIDE_LEAD);
			arrow.drawArrow(g, 60 + 120 + 60 + 60, 15 + 60, Math.PI / -4D, 24, Arrow.SIDE_LEAD);
			arrow.drawArrow(g, 60 + 120 + 60 + 60, 15 + 60 + 30 + 120, Math.PI / -4D, 24, Arrow.SIDE_LEAD);
			arrow.drawArrow(g, 60 + 120 + 60 + 60, 15 + 60 + 30 + 120 + 30 + 120, Math.PI / -4D, 24, Arrow.SIDE_LEAD);
		}

		/* draw the labels */
		g.drawString("" + RC + "μm", 60 + 60 + 48, 15 + 60 - 38);
		g.drawString("" + RC + "μm", 60 + 120 + 60 + 60 + 48, 15 + 60 - 43); // this one is different because the cell is slightly larger
		g.drawString("" + RC + "μm", 60 + 120 + 60 + 60 + 48, 15 + 60 + 30 + 120 - 38);
		if (target != 0) {
			// RC
			g.drawString("" + RC + "μm", 60 + 120 + 60 + 60 + 48, 15 + 60 + 30 + 120 + 30 + 120 - 38);

			// RNs
			g.drawString("" + RN + "μm", 60 + 60 + 20, 15 + 60 + 27);
			g.drawString("" + RN + "μm", 60 + 120 + 60 + 60 + 20, 15 + 60 + 27);
			g.drawString("" + RN + "μm", 60 + 120 + 60 + 60 + 20, 15 + 60 + 120 + 30 + 27);
			g.drawString("" + RN + "μm", 60 + 120 + 60 + 60 + 20, 15 + 60 + 120 + 30 + 120 + 30 + 27);
		}
		//g.drawString("" + target, 5, 5);
	}

	public static void main(String[] args) {
		// write your code here
		System.out.println("Hello, World");
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(686, 491));

		CellCanvasInfoNew m = new CellCanvasInfoNew();
		frame.setBackground(background);
		frame.add(m);
		frame.paint(null);
		frame.show();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.repaint();
	}

	public void setTarget(int t) {
		if (t < 0)
			return;
		if (t > 3)
			return;
		target = t;
		this.repaint();
	}

	public void setRC(int rc) {
		if (rc < 1) {
			this.RC = 2;
			return;
		}
		if (rc < 1 + this.RN) {
			this.RC = 1 + this.RN;
			return;
		}
		this.RC = rc;
		this.repaint();
	}

	public void setRN(int rn) {
		if (rn < 1) {
			this.RN = 1;
			return;
		}
		if (rn > this.RC - 1) {
			this.RN = this.RC - 1;
			return;
		}
		this.RN = rn;
		this.repaint();
	}
        public void setFractions(double n, double cy, double cs){
            try{
                this.n = n;
                this.cy = cy;
                this.cs = cs;
            } catch (NumberFormatException nfe){
                
            }
            
            this.repaint();
        }
}
