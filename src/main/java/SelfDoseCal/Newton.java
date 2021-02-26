package SelfDoseCal;
/*
 * Newton.java
 *
 * Created on May 25, 2008, 10:48 AM
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
	double conv = 0.00001;
	double dr = 0, r = 0, r0 = 0.0;


	public Newton() {

	}

	protected double getRange(double e0) {
		double e;
		if (e0 > 0.6) {
			r = 0.0431 * Math.pow((e0 + 0.367), 1.77) - 0.007;
			dr = r / 100.0;

			//System.out.println("the initial result of range R = "+r);
			//System.out.println("the initial result of dr="+dr);

			out:
			while (true) {

				for (int i = 1; i < 10000; i++) {
					e = 5.9 * Math.pow((r + 0.007), 0.565) + 0.00413 * Math.pow(r, 1.33) - 0.367;
					if (Math.abs((e - e0) / e0) < conv) {
						break out;
					}
					if ((e - e0) > 0.0) {

						//System.out.println("r = "+r);

						dr = -dr / 3.0;
						r = r + dr;
						break;
					}
					r = r + dr;
				}


				for (int i = 1; i < 10000; i++) {
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

			//System.out.println("the result of range R= "+r);
		}


		return r;

	}
}
