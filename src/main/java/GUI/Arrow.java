package GUI;

import java.awt.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/** * @(#)Arrow.java * * Copyright (c) 2000 by Sundar Dorai-Raj
 * * @author Sundar Dorai-Raj
 * * Email: sdoraira@vt.edu
 * * This program is free software; you can redistribute it and/or
 * * modify it under the terms of the GNU General Public License
 * * as published by the Free Software Foundation; either version 2
 * * of the License, or (at your option) any later version,
 * * provided that any use properly credits the author.
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details at http://www.gnu.org * *
 * */

public class Arrow {

	public static final int SIDE_LEAD  = 0;
	public static final int SIDE_TRAIL = 1;
	public static final int SIDE_BOTH  = 2;
	public static final int SIDE_NONE  = 3;
	public static final double pi = Math.PI;

	public Arrow() {
	}

	public void drawArrow(Graphics g,
	                      int x, int y,
	                      double theta,
	                      int length,
	                      int side) {
		try {
			if (length < 0) {
				theta += pi;
				length *= -1;
			}
			int x1, y1;
			x1 = (int) Math.ceil(x + length * Math.cos(theta));
			y1 = (int) Math.ceil(y - length * Math.sin(theta));
			g.drawLine(x, y, x1, y1);

			switch (side) {
				case SIDE_LEAD:
					drawArrow(g, x1, y1, theta + 5 * pi / 4, 5, SIDE_NONE);
					drawArrow(g, x1, y1, theta + 3 * pi / 4, 5, SIDE_NONE);
					break;
				case SIDE_TRAIL:
					drawArrow(g, x, y, theta - pi / 4, 5, SIDE_NONE);
					drawArrow(g, x, y, theta + pi / 4, 5, SIDE_NONE);
					break;
				case SIDE_BOTH:
					drawArrow(g, x, y, theta - pi / 4, 5, SIDE_NONE);
					drawArrow(g, x, y, theta + pi / 4, 5, SIDE_NONE);
					drawArrow(g, x1, y1, theta + 5 * pi / 4, 5, SIDE_NONE);
					drawArrow(g, x1, y1, theta + 3 * pi / 4, 5, SIDE_NONE);
					break;
				case SIDE_NONE:
					break;
				default:
					throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException iae) {
			System.out.println("Invalid value for variable side.");
		}
	}
}


