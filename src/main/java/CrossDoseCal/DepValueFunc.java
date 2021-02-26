/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CrossDoseCal;

import File.DedrAlpha;
import File.MyList2;

import java.util.ArrayList;

/**
 * @author Johnny  Wu
 */
public class DepValueFunc {
	String alphaFile = "";
	DedrAlpha dedrAlpha;
	ColeDedrFunction dedrCole = new ColeDedrFunction();
	double dedr, dep = 0, sum = 0;

	public DepValueFunc(double dep) {

		this.dep = dep;
	}

	public double getDepValue(String radType, double e0, double range, double x, double dx, ArrayList<MyList2> list) {
		double r1, dedr1Alpha, dedr2;
		r1 = range - x + dx / 2;
		//System.out.println("rad="+radType);
		//System.out.println("the result of DEP1 = "+dep);
		//out:
		//while(true) {
		if(radType.toLowerCase().contains( "alpha" ) || radType.equals( "A" )) {
			dedrAlpha = new DedrAlpha( list );
			dedr = dedrAlpha.getDedrAlpha( range, r1 );
			//dep = dedr1Alpha * dx;
			//03/25/09 System.out.println("Alpha: dedr="+dedr1Alpha);
			//System.out.println("the result of DEP1 = "+dep);
		}
		else {
			dedr = dedrCole.getDedr( r1 );
		}
		// 4/17/09 System.out.println("dedr 4/9/09 = "+dedr);
		dep = dedr * dx;

		//System.out.println("the result of DEP1 = "+dep);   //test error found!!
		//the result of DEP1 = -0.015074645123341827
		//the result of SUM  = -0.015074645123341827
		return dep;
	}

}
