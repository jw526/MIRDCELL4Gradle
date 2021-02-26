package SelfDoseCal;
/*
 * Dedr2Function.java
 * Created on April 20, 2008, 3:48 PM
 *
 * Function DEDR2 computes the electron energy loss dE/dR as given 
 * by the Cole publication on experimental range-energy relations.
 * A. Cole.  Absorption of 20 eV to 50,000 eV electron beams in  air
 * and plastic.  Radiat. Res. 38, 7-33 (1969).
 * At very small energies, Coles dE/dR expression is quite far off
 * from the experimental data so other functions have been developed 
 * to fit the experimental data in these energy ranges. 
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import static java.lang.Math.*;

/**
 * @author Johnny  Wu
 */
public class Dedr2Function {

	/**
	 * Creates a new instance of Dedr2Function
	 */
	double a = 10.5, b = 1126.05, c = -9.251137e+05, d = 2.59298e+08, e = 4.96439e+10;
	double r, x;


	public Dedr2Function(){}

	protected double getDedr2Func(double r, double x) {

		double dedr2 = 0;
		double z = r - x;

		if (z >= 0.02) {
			dedr2 = 3.3335 * pow((z + 0.007), (-0.435)) + 0.0055 * pow(z, 0.33);
		}
		else if (z < 0.02 && z > 0.0038) {
			dedr2 = 29.5 - 666.67 * z;
		}
		else if (z > 0.0 && z <= 0.0038) {
			dedr2 = a + b * z + c * pow(z, 2) + d * pow(z, 3) + e * pow(z, 4);
		}
		else {
			dedr2 = 0.0;
		}

		return dedr2;
	}
}
