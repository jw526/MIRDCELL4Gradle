package SelfDoseCal;

import File.DedrAlpha;

/*
 * Ency.java
 *
 * Created on July 25, 2008, 11:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import File.MyList2;
import java.util.ArrayList;

/**
 *
 * @author Johnny  Wu
 */
public class Ency {

	Newton newton = new Newton();
	String fileName = "alph-water2";
	DedrAlpha dedrAlpha;
	Dedr2Function dedr2Func = new Dedr2Function();

	/** Creates a new instance of Ency */
	double dedr;
	double e,f,p,q,r = 0.0, dr, x,dx,x2,x3,x4,y,z;
	int ctpye, type;
	int maxdim=200;

	double electron, xrange, ekev, e0;
	double radii,rn,rc;
	int step;
	double ency = 0;
	double conv =0.00001;
	int istep = 100, jstep = 300;

	double rc2,rc3,rc4,rn2,rn3,rn4;

	double surfpsi,ens,ecs,ecc,enn;

	int i;

	public Ency() {}



	protected double getEncy(String radType, double e0, double r, double rc, double rn, ArrayList<MyList2> list) {

		rc2=rc*rc;
		rc3=rc2*rc;
		rc4=rc3*rc;
		rn2=rn*rn;
		rn3=rn2*rn;
		rn4=rn3*rn;


		if ( r >= (rn+rc) ) {
			dx=(rc+rn)/(float)(jstep-5);
		}
		else {
			dx=r/(float)(jstep-5);
		}
		//System.out.println("dx for ency="+dx); //03/31/09 test
		double x0 = 0.0;

		if (rc < (3.0*rn)) {


			for (i=1; i<=jstep; i++) {
				// 04/16/09 System.out.println("loop i = "+i);
				x = x0 + dx*(float)i;
				x2 = x*x;
				x4 = x2*x2;
				q = 3.0/(4.0*x*(rc3-rn3));

				if (x >= 0 && x <= (rc-rn)){
					surfpsi = q*x2*(rn2-x2/12.0);
				}
				else if (x > (rc-rn) && x <= (2.0*rn)) {
					surfpsi = q*(0.5*(rc2-rn2)*(rn2-x2)+(2.0/3.0)*x*(rc3-rn3)- 0.25*(rc4-rn4));
				}
				else if (x > (2.0*rn)&& x <= (rc+rn)) {
					surfpsi = (q/12.0)*(x4-3.0*(rc4+rn4)+6.0*(rc2*rn2-x2*rn2-x2*rc2)+ 8.0*x*(rc3+rn3));
				}
				else {
					surfpsi = 0.0;
				}
				//System.out.println("surfpsi = "+surfpsi);

				x = x-dx/2.0;

				if (radType.contains("Alpha") || radType.contains("ALPHA")) {
					dedrAlpha = new DedrAlpha(list);
					dedr = dedrAlpha.getDedrAlpha(r, x);
				}
				else{
					//function dedr2
					dedr = dedr2Func.getDedr2Func(r, x);
				}
				//System.out.println("dedr="+dedr);
				ency = ency + dedr*surfpsi;
			}//end for loop
		}//end if
		else {
			for (i=1; i <= jstep; i++) {
				x = x0+dx*(float)i;
				x2=x*x;
				x4=x2*x2;
				q = 3.0/(4.0*x*(rc3-rn3));

				if (x >= 0 && x <= (2.0*rn)){
					surfpsi = q*x2*(rn2-x2/12.0);
				}
				else if (x >(2.0*rn) && x <= (rc-rn)) {
					surfpsi = rn3/(rc3-rn3);
				}
				else if (x > (rc-rn) && x <= (rc+rn)) {
					surfpsi = (q/12.0)*(x4-3.0*(rc4+rn4)+6.0*(rc2*rn2-x2*rn2-x2*rc2)+8.0*x*(rc3+rn3));
				}
				else {
					surfpsi = 0.0;
				}
				// System.out.println("surfpsi = "+surfpsi);
				x=x-dx/2.0;


				if (radType.contains("Alpha") || radType.contains("ALPHA")) {
					dedrAlpha = new DedrAlpha(list);
					dedr = dedrAlpha.getDedrAlpha(r, x);
				}
				else{
					//function dedr2
					dedr = dedr2Func.getDedr2Func(r, x);
				}

				// 04/16/09 System.out.println("DEDR = "+dedr);
				ency = ency + dedr*surfpsi;
				/**
				 if (type = ' ALPHA') {
				 ency = ency + dedr1(x,p,rx)*surfpsi;
				 }

				 // else {
				 ency = ency + dedr2*surfpsi;
				 // }
				 System.out.println("the energy value between nucleus and cytoplasm = "+ency);
				 */
			} //end for
		}// end if
		ency = ency*dx;
		if (ency > e0){
			ency=e0;
		}
		return ency;

	}

}
