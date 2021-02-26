package SelfDoseCal;
/*
 * Enn.java
 *
 * Created on July 25, 2008, 12:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import File.DedrAlpha;
import File.MyList2;

import java.util.ArrayList;

import static java.lang.Math.*;

/**
 * @author Johnny  Wu
 */
public class Enn {

	String fileName = "alph-water2";
	DedrAlpha dedrAlpha;
	Dedr2Function dedr2Func = new Dedr2Function();

	/**
	 * Creates a new instance of Enn
	 */
	double dedr;
	double e, f, p, q, r = 0.0, dr, x, dx, x2, x3, x4, y, z;
	int maxdim = 200;

	double electron, xrange, ekev, e0;
	double radii, rn, rc;
	int step;
	double ency = 0.0;
	int istep = 100, jstep = 300;

	double rc2, rc3, rc4, rn2, rn3, rn4;

	double surfpsi, ens, ecs, ecc, enn = 0;

	int i;

	public Enn() {
	}

	public double getEnn(String radType, double e0, double r, double rn, ArrayList<MyList2> list) {
		//System.out.println("r="+r);
		if(r > (2.0 * rn)) {
			dx = 2.0 * rn / (double) jstep;
		}
		else {
			dx = r / (double) jstep;
		}

		for(i = 1; i <= jstep; i++) {

			x = dx * (double) i;
			//System.out.println("the initial result of range x = "+x);

			if(x >= 0.0 && x <= (2.0 * rn)) {
				surfpsi = (1.0 - (3.0 / 4.0) * (x / rn) + (1.0 / 16.0) * pow( (x / rn), 3 ));
			}
			else {
				surfpsi = 0.0;
			}

			x = x - dx / 2.0;
			// System.out.println(radType.equals("ALPHA"));
			if(radType.contains( "Alpha" ) || radType.contains( "ALPHA" )) {
				dedrAlpha = new DedrAlpha( list );
				dedr = dedrAlpha.getDedrAlpha( r, x );
			}

			else {    //function dedr2
				dedr = dedr2Func.getDedr2Func( r, x );
				//System.out.println("the result of DEDR2 = "+dedr2);
			}
			//System.out.println("surfpsi = "+surfpsi);
			//System.out.println("the result of DEDR = "+dedr);
			enn += dedr * surfpsi;
			//System.out.println("the energy value between nucleus and nucleus = "+enn);
		}// end for
		//System.out.println("enn1 = "+enn);
		enn = enn * dx;
		if(enn > e0) {
			enn = e0;
		}

		return enn;
	}
}
