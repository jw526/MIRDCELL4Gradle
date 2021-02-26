/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CrossDoseCal;

/**
 * @author Johnny  Wu
 */
public class AbsorbFraction {

	SpsiGeoFactor spsiGeoFactor = new SpsiGeoFactor();
	VpsiGeoFactor vpsiGeoFactor = new VpsiGeoFactor();

	double spsi, spsirr, vpsiaa, vpsirr, vpsi, vpsira;
	double abs1 = 0, abs2 = 0, abs3 = 0, abs4 = 0, abs5 = 0, absr = 0;
	double dep, x1 = 0.0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;
	double a1, a2, a3, a4, a5;
	double r1 , r2, r3, r4, r5;
	double p1 = 0.0, p2 = 0, p3 = 0, p4 = 0, p5 = 0;

	public AbsorbFraction(double dep, double x1, double x2, double x3, double x4, double x5, double p1, double p2, double p3, double p4, double p5, double r1, double r2, double r3, double r4, double r5, double a1, double a2, double a3, double a4, double a5) {
		this.dep = dep;
		this.x1  = x1 ;
		this.x2 = x2;
		this.x3 = x3;
		this.x4 = x4;
		this.x5 = x5;
		this.p1  = p1 ;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.r1  = r1 ;
		this.r2 = r2;
		this.r3 = r3;
		this.r4 = r4;
		this.r5 = r5;
		this.a1  = a1 ;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a5 = a5;

	}

	public double getAbsf1() {
		/**
		 * Cell to Cell
		 */
		vpsirr = vpsiGeoFactor.getVpsiGeoFactor(x1, x2, x3, x4, x5, p1, p2, p3, p4, p5, r1 , r2, r3, r4, r5, r1 , r2, r3, r4, r5);
		abs1 = vpsirr * dep; //absorb fraction value for cell to cell.
		return abs1;
	}

	public double getAbsf2() {
		/**
		 * Cell surface to Cell
		 */
		spsirr = spsiGeoFactor.getSpsiGeoFactor(x1, x2, x3, x4, x5, p1, p2, p3, p4, p5, r1 , r2, r3, r4, r5, r1 , r2, r3, r4, r5);
		abs2 = spsirr * dep;
		return abs2;
	}

	public double getAbsf3() {
		/**
		 * Nucleus to Nucleus
		 */
		vpsiaa = vpsiGeoFactor.getVpsiGeoFactor(x1, x2, x3, x4, x5, p1, p2, p3, p4, p5, a1, a2, a3, a4, a5, a1, a2, a3, a4, a5);
		abs3 = vpsiaa * dep;
		return abs3;
	}

	public double getAbsf5() {
		/**
		 * Cell surface to Nucleus
		 */
		spsi = spsiGeoFactor.getSpsiGeoFactor(x1, x2, x3, x4, x5, p1, p2, p3, p4, p5, r1 , r2, r3, r4, r5, a1, a2, a3, a4, a5);
		abs5 = spsi * dep;
		return abs5;
	}

	public double getAbsfr() {
		/**
		 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
		 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
		 */
		vpsi = vpsiGeoFactor.getVpsiGeoFactor(x1, x2, x3, x4, x5, p1, p2, p3, p4, p5, r1 , r2, r3, r4, r5, a1, a2, a3, a4, a5);
		absr = vpsi * dep;
		return absr;
	}
}
