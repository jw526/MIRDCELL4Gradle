package CrossDoseCal;
/*
 * SpsiGeoFactor.java
 *
 * Created on July 25, 2008, 2:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @author Johnny  Wu
 */
public class SpsiGeoFactor {

	/**
	 * Creates a new instance of SpsiGeoFactor
	 */
	double x0, x = 0.0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;
	double a, a2, a3, a4, a5;
	double r, r2, r3, r4, r5;
	double p = 0.0, p2 = 0, p3 = 0, p4 = 0, p5 = 0;

	public SpsiGeoFactor() {
	}

	public double getSpsiGeoFactor(double x, double x2, double x3, double x4, double x5, double p, double p2, double p3, double p4, double p5, double r, double r2, double r3, double r4, double r5, double a, double a2, double a3, double a4, double a5) {
		double spsiGeoFactor = 0;

		// SURFACE TO NUCLEUS
		if((x >= (p - r - a)) && (x <= (p - r + a))) {
			spsiGeoFactor = (-x3 - 3.0 * x2 * (r - p) - 3.0 * x * (-a2 + r2 - 2.0 * p * r + p2) - r3 + 3.0 * p * r2 - 3.0 * r * (p2 - a2) + p3 + 2.0 * a3 - 3.0 * a2 * p) / (24.0 * p * r * x);
		}
		else if((x >= (p - r + a)) && (x <= (p - a + r)) && (r != a)) {
			spsiGeoFactor = a3 / (6.0 * p * r * x);
		}
		else if((x >= (p - a + r)) && (x <= (p + a + r))) {
			spsiGeoFactor = (x3 - 3.0 * x2 * (r + p) - 3.0 * x * (a2 - r2 - 2.0 * p * r - p2) - r3 - 3.0 * p * r2 - 3.0 * r * (p2 - a2) - p3 + 2.0 * a3 + 3.0 * a2 * p) / (24.0 * p * r * x);
		}
		else {
			spsiGeoFactor = 0.0;
		}
		//  System.out.println ("the result of SPSI = "+spsi);
		return spsiGeoFactor;
	}
}
