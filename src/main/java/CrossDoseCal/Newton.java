package CrossDoseCal;
/*
 * Newton.java
 *
 * Created on May 25, 2008, 2:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @author Johnny  Wu
 */
public class Newton {

	/**
	 * Creates a new instance of Newton
	 */
	double e = 0.0;
	double conv = 0.00001;

	int i = 0;

	double dr = 0;
	double r = 0;

	double r0 = 0.0;

	public Newton() {
	}

	public double getRange(double e0) {

		if (e0 > 0.6) {
			r = 0.0431 * Math.pow((e0 + 0.367), 1.77) - 0.007;
			dr = r / 100.0;
			out:
			while (true) {
				for (i = 1; i < 10000; i++) {
					e = 5.9 * Math.pow((r + 0.007), 0.565) + 0.00413 * Math.pow(r, 1.33) - 0.367;
					if (Math.abs((e - e0) / e0) < conv) {
						break out;
					}
					if ((e - e0) > 0.0) {
						dr = -dr / 3.0;
						r = r + dr;
						break;
					}
					r = r + dr;
				}


				for (i = 1; i < 10000; i++) {
					e = 5.9 * Math.pow((r + 0.007), 0.565) + 0.00413 * Math.pow(r, 1.33) - 0.367;
					if (Math.abs((e - e0) / e0) < conv) {
						break out;
					}
					if ((e - e0) < 0.0) {
						dr = -dr / 3.0;
						r = r + dr;
						break;
					}
					r = r + dr;
				}
			}//end out
			//System.out.println("the result of range R= "+r);
		} else if (e0 > 0.06 && e0 <= 0.6) {
			r = 1.523805e-03 + 0.038154 * e0 - 7.01803e-04 * Math.pow(e0, 2) + 0.036283 * Math.pow(e0, 3);
			//System.out.println("the result of range R= "+r);
		} else {
			r = 0.01233 * e0 + 2.25 * Math.pow(e0, 2) - 23.333 * Math.pow(e0, 3);
		}


		return r;

	}
}
