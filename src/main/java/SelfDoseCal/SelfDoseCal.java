/*
 * SelfDoseCal.java
 *
 * Created on May 28, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package SelfDoseCal;

import File.MyList2;
import java.util.ArrayList;
import static java.lang.Math.*;

/**
 * @author Johnny  Wu
 */
public class SelfDoseCal {

	/**
	 * Creates a new instance of SelfDoseCal
	 */
	ArrayList<MyList2> list = new ArrayList<MyList2>();
	Newton newton = new Newton();
	Ency ncy = new Ency();
	Enn nn = new Enn();
	Ens ns = new Ens();

	double dedr2;
	double e, r = 0.0, dr, x, dx, x2, x3, x4, y, z;
	int ctpye, type;
	int maxdim = 200;
	double electron, xrange, xn, ekev, e0, delta;
	double radii, rn, rc;

	double conv = 7.508e-5; //Conversion Factor cGy/pCi-h to Gy/Bq.s ????????
	int istep = 100, jstep = 300, istop;
	double dcc = 0.0, dcs = 0.0, dnn = 0.0, dncy = 0.0, dns = 0.0;
	double ency = 0, ens = 0, ecs = 0, ecc = 0, enn = 0;
	double abscc, abscs, absnn, absns, absncy;

	int i, j;
	double act = 1.0e-6;
	double cellmass = 0, xnuclmass = 0, cytomass = 0;
	String radType = "";

	public SelfDoseCal(String radType, double rc, double rn, double xn, double e0, double delta, double r, ArrayList<MyList2> list) {

		this.list = list;
		this.radType = radType;
		this.rc = rc;
		this.rn = rn;
		this.xn = xn;
		this.e0 = e0;
		this.delta = delta;
		this.r = r;
		cellmass = 4.0 * 3.14159 * pow(rc * (1.0e-04), 3) / 3.0;
		xnuclmass = 4.0 * 3.14159 * pow(rn * (1.0e-04), 3) / 3.0;
		cytomass = cellmass - xnuclmass;
	}

	public double getDncy(double rc, double rn, double e0, double delta) {
		//System.out.println("Enkev used in ency=" + e0);
		ekev = e0;
		//r = newton.getRange(e0);
		/** implement class Ency
		 *  compute NCY(ENCY)
		 *  class Ency
		 */
		// ENCY
		ency = ncy.getEncy(radType, e0, r, rc, rn, list);
		//System.out.println("the ency = " + ency);
		absncy = ency / ekev;
		//double conv = 1.0;
		delta = xn*e0*(1.0e-3)*2.13;
		// 04/16/2009 System.out.println("delta=" +delta);
		dncy = conv * act * absncy * delta / xnuclmass;

		return dncy;
	}

	public double getDcc(double rc, double rn, double e0, double delta) {
		ekev = e0;
		if (ekev >= 1) {

			/** implement class Enn
			 * computes ECC which is defined above.
			 *
			 */
			ecc = nn.getEnn(radType, e0, r, rc, list);

		} else {
			ecc = ekev;
		}
		//System.out.println("0727 ecc =" + ecc);
		abscc = ecc / ekev;
		//System.out.println("0727 abscc =" + abscc);
		//double conv = 1.0;
		//delta = xn*e0*(1.0e-3)*2.13;
		dcc = conv * act * abscc * delta / cellmass;
		//System.out.println("0727 dcc=" + dcc);
		return dcc;
	}

	public double getDcs(double rc, double rn, double e0, double delta) {
		ekev = e0;
		if (ekev >= 1) {

			/** implement class Enn
			 * computes ECC which is defined above.
			 *
			 */
			ecs = ns.getEns(radType, e0, r, rc, rc, list);

		} else {
			ecs = ekev / 2.0;
		}
		abscs = ecs / ekev;
		//double conv = 1.0;
		//delta = xn*e0*(1.0e-3)*2.13;
		dcs = conv * act * abscs * delta / cellmass;

		return dcs;
	}

	public double getDnn(double rc, double rn, double e0, double delta) {
		ekev = e0;
		if (ekev >= 1) {

			/**
			 * implement class Enn
			 * computes ECC which is defined above.
			 */
			enn = nn.getEnn(radType, e0, r, rn, list);

		} else {
			enn = ekev;
		}
		absnn = enn / ekev;
		//double conv = 1.0;
		//delta = xn*e0*(1.0e-3)*2.13;
		dnn = dnn + conv * act * absnn * delta / xnuclmass;

		return dnn;
	}

	public double getDns(double rc, double rn, double e0, double delta) {
		ekev = e0;
		if (ekev >= 1.0) {

			/**
			 * implement class Enn
			 * computes ECC which is defined above.
			 */
			ens = ns.getEns(radType, e0, r, rc, rn, list);

		} else {
			ens = 0; //4/24/09
		}
		absns = ens / ekev;
		//double conv = 1.0;
		// delta = xn*e0*(1.0e-3)*2.13;
		dns = conv * act * absns * delta / xnuclmass;

		return dns;
	}
	/**
	 public double getSelfDose(double rc, double rn, double e0) {
	 ekev = e0;
	 //public class SelfDose {

	 // public SelfDose() {

	 //double dcc = 0;

	 //Begining Cycle through j radiations

	 //  for (j=1; j<=istop; j++) {
	 r = newton.getRange(e0); //function Newton.java, where r is the optimal range value, xrange, e0 is the energy value in Kev.

	 //System.out.println("i="+i);

	 /** implement class Ency
	 *  compute NCY(ENCY)
	 *  class Ency


	 // ENCY
	 ency = ncy.getEncy(e0, r, rc, rn);
	 System.out.println("the ency = "+ency);

	 if (ekev >= 1) {

	 /** implement class Enn
	 * computes ECC which is defined above.
	 *
	 */
	//ecc = nn.getEnn(e0, r, rc);

	/** implement class Ens
	 *  to compute ecs
	 *
	 */
	//ecs = ns.getEns(e0, r, rc, rc);

	/** implement class Enn
	 *  computes ENN which is defined above.
	 *
	 */
	//enn = nn.getEnn(e0, r, rn);

	/** implement class Ens
	 *  to compute ens
	 *

	 //ens = ns.getEns(e0, r, rc, rn);

	 }//end if enkev >= 1

	 else {
	 ecc = ekev;
	 ecs = ekev/2.0;
	 enn = ekev;
	 ens = 0.0;
	 } //end if


	 System.out.println("the optimized energy value between nucleus and cytoplasm = "+ency); // ency
	 System.out.println("the adjusted energy value ECC = "+ecc);
	 System.out.println("the optimized energy value ECS= "+ecs);
	 System.out.println("the adjusted energy value between nucleus and nucleus = "+enn);
	 System.out.println("the optimized energy value between nucleus and surface = "+ens);


	 // }//end loop istop

	 //System.out.println("ekev="+ekev);

	 abscc = ecc/ekev;
	 abscs = ecs/ekev;
	 absnn = enn/ekev;
	 absncy = ency/ekev;
	 absns = ens/ekev;

	 conv = 1.0;
	 delta = xn*e0*(1.0e-3)*2.13;
	 System.out.println("cellmass="+cellmass);
	 dcc = dcc + conv*act*abscc*delta/cellmass;
	 dcs = dcs + conv*act*abscs*delta/cellmass;
	 dnn = dnn + conv*act*absnn*delta/xnuclmass;
	 dncy = dncy + conv*act*absncy*delta/xnuclmass;
	 dns =  dns + conv*act*absns*delta/xnuclmass;
	 String dccValue = formatter.format(dcc);
	 String dcsValue = formatter.format(dcs);
	 String dnnValue = formatter.format(dnn);
	 String dncyValue = formatter.format(dncy);
	 String dnsValue = formatter.format(dns);

	 System.out.println("dcc="+dccValue+", dcs="+dcsValue+", dnn="+dnnValue+", dncy="+dncyValue+", dns="+dnsValue);

	 return dncy;
	 }
	 // public double getDncy() {
	 //return dncy;
	 //}
	 // }
	 */

}
