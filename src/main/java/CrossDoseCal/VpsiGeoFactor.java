package CrossDoseCal;
/*
 * VpsiGeoFactor.java
 *
 * Created on July 25, 2008, 2:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @author Johnny  Wu
 */
public class VpsiGeoFactor {
	public VpsiGeoFactor() {
	}

	public double getVpsiGeoFactor(double x, double x2, double x3, double x4, double x5, double p, double p2, double p3, double p4, double p5, double r, double r2, double r3, double r4, double r5, double a, double a2, double a3, double a4, double a5) {

		double vpsiGeoFactor;

		if(x >= (p - r - a) && x <= (p - r + a)) {
			vpsiGeoFactor = (x5 - 5.0D * p * x4 + 10.0D * x3 * (-r2 + p2 - a2) + 10.0D * x2 * (3.0D * a2 * p - 2.0D * a3) + x * (30.0D * a2 * r2 - 30.0D * a2 * p2 + 40.0D * a3 * p - 15.0D * a4) + 20.0D * a3 * r2 - 20.0D * a3 * p2 + 15.0D * a4 * p - 4.0D * a5 - x2 * (20.0D * r3 - 30.0D * p * r2 + 10.0D * p3) - x * (15.0D * r4 - 40.0D * p * r3 + 30.0D * p2 * r2 - 5.0D * p4) - 4.0D * r5 + 15.0D * p * r4 - 20.0D * r3 * (p2 - a2) - r2 * (30.0D * a2 * p - 10.0D * p3) - p5 + 10.0D * a2 * p3) / (160.0D * p * x * r3);
		}
		else if(x >= (p - r + a) && x <= (p + r - a) && r != a) {
			vpsiGeoFactor = (a3 / (20.0D * p * x * r3)) * (-5.0D * x2 + 10.0D * p * x + 5.0D * r2 - 5.0D * p2 - a2);
		}
		else if(x >= (p + r - a) && x <= (p + r + a)) {
			vpsiGeoFactor = (-x5 + 5.0D * p * x4 - 10.0D * x3 * (-r2 + p2 - a2) - 10.0D * x2 * (3.0D * a2 * p + 2.0D * a3) - x * (30.0D * a2 * r2 - 30.0D * a2 * p2 - 40.0D * a3 * p - 15.0D * a4) + 20.0D * a3 * r2 - 20.0D * a3 * p2 - 15.0D * a4 * p - 4.0D * a5 - x2 * (20.0D * r3 + 30.0D * p * r2 - 10.0D * p3) - x * (-15.0D * r4 - 40.0D * p * r3 - 30.0D * p2 * r2 + 5.0D * p4) - 4.0D * r5 - 15.0D * p * r4 - 20.0D * r3 * (p2 - a2) - r2 * (-30.0D * a2 * p + 10.0D * p3) + p5 - 10.0D * a2 * p3) / (160.0D * p * x * r3);
		}
		else {
			vpsiGeoFactor = 0.0D;
		}
		return vpsiGeoFactor;

	}
}
