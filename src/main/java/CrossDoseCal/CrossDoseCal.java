package CrossDoseCal;
/**
 * COMPUTE MULTICELLULAR DOSE
 * Created on September 6, 2007, 8:00 AM
 * <p/>
 * P		 DISTANCE BETWEEN CELLS IN MICRONS
 * <p/>
 * R		CELL RADIUS IN MICRONS
 * <p/>
 * A		RADIUS OF CELL NUCLEUS IN MICRONS
 * <p/>
 * E0		 ENERGY OF PARTICLE IN KEV
 * <p/>
 * ISTEP		NUMBER OF INTEGRATION STEPS
 */

import File.MyList2;
import File.WriteToOutputFile;

import java.io.IOException;
import java.lang.Math.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 * @author Johnny  Wu
 */
public class CrossDoseCal {

	AbsorbFraction absFraction;
	DoseFunc doseFunc;
	JStop jStop = new JStop();
	DepFunc depFunc;
	DepValueFunc depValue;
	WriteToOutputFile writeFile = new WriteToOutputFile();
	NumberFormat formatter = new DecimalFormat( "0.00E00" );
	ArrayList<MyList2> list = new ArrayList<MyList2>();

	double rc = 0, rn = 0, dx = 0;
	double x0, x = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;
	double a, a2, a3, a4, a5;
	double r, r2, r3, r4, r5;
	double p = 0, p2 = 0, p3 = 0, p4 = 0, p5 = 0;
	double range = 0, dr = 0.0; //range= r
	double e1 = 0.0, xn = 0, e0 = 0, delta = 0, conv = 0.00001; // e0=enkev
	//double dep = 0;
	double dedr = 0.0;

	// Alex Rosen changed istep from 1000 to 10000
	int istop, istep = 1000, maxabs = 2500, maxconfig = 5;
	int jstart, jstop;
	int i, j, k;
	double act = 1.0e-6;
	double cellmass = 0, xnuclmass = 0, cytomass = 0;
	double dose = 0, dose1 = 0, dose2 = 0, dose3 = 0, dose4 = 0, dose5 = 0;
	//added 09/09/2010
	double dose6 = 0, dose7 = 0, dose8 = 0;
	String doseValue1 = null, doseValue2 = null, doseValue3 = null, doseValue4 = null, doseValue5 = null;
	//add 09/09/2010
	String doseValue6 = null, doseValue7 = null, doseValue8 = null;
	double absf1 = 0, absf2 = 0, absf3 = 0, absf4 = 0, absf5 = 0, absfr = 0;
	Double[] doseArray1 = new Double[maxabs + 1];

	JTextArea jTextArea1 = new JTextArea();

	// may be unused
	public CrossDoseCal(double rc, double rn, double xn, double e0, double delta, double range, ArrayList<MyList2> list, JTextArea jTextArea1) {
		this.rc = rc;
		this.rn = rn;
		this.xn = xn;
		this.e0 = e0;
		this.delta = delta;
		this.range = range;
		this.list = list;
		this.jTextArea1 = jTextArea1;

		a = rn;
		a2 = a * a;
		a3 = a2 * a;
		a4 = a3 * a;
		a5 = a4 * a;

		r = rc;
		r2 = r * r;
		r3 = r2 * r;
		r4 = r3 * r;
		r5 = r4 * r;

		cellmass = 4.0 * 3.1415926535 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.1415926535 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		//delta = xn*e0*(1.0e-3)*2.13;
	}

	// used in calTest3d
	public CrossDoseCal(double rc, double rn, double xn, double e0, double delta, double range, ArrayList<MyList2> list, JTextArea jTextArea1, int MaxDist) {
		this.rc = rc;
		this.rn = rn;
		this.xn = xn;
		this.e0 = e0;
		this.delta = delta;
		this.range = range;
		this.list = list;
		this.jTextArea1 = jTextArea1;
		this.maxabs = MaxDist;
		jStop.SetMax( maxabs );

		a = rn;
		a2 = a * a;
		a3 = a2 * a;
		a4 = a3 * a;
		a5 = a4 * a;

		r = rc;
		r2 = r * r;
		r3 = r2 * r;
		r4 = r3 * r;
		r5 = r4 * r;

		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		//delta = xn*e0*(1.0e-3)*2.13;
	}

	//may not be used
	public CrossDoseCal(double rc, double rn, double xn, double e0, double delta, double range, ArrayList<MyList2> list) {
		this.rc = rc;
		this.rn = rn;
		this.xn = xn;
		this.e0 = e0;
		this.delta = delta;
		this.range = range;
		this.list = list;

		a = rn;
		a2 = a * a;
		a3 = a2 * a;
		a4 = a3 * a;
		a5 = a4 * a;

		r = rc;
		r2 = r * r;
		r3 = r2 * r;
		r4 = r3 * r;
		r5 = r4 * r;

		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		//delta = xn*e0*(1.0e-3)*2.13;
	}


	//06/21/09
	public void getCrossDose(String radType, int num, String directory) throws IOException {
		jstart = 2 * (int) rc;
		jstop = jStop.getJStop( range, rc, rn );
		//System.out.println ("jstop= "+jstop);

		for(j = jstart; j <= jstop; j++) {
			doseArray1[j] = 0.0;
			//System.out.println("j=" +j);
			absf1 = 0;
			absf2 = 0;
			absf3 = 0;
			absf4 = 0;
			absf5 = 0;
			absfr = 0;

			p = (double) j;
			p2 = p * p;
			p3 = p2 * p;
			p4 = p3 * p;
			p5 = p4 * p;

			//	Select value of DX depending on whether Range > max distance
			if(range > p + r + r) {
				dx = 2.0 * (r + r) / (double) istep;
			}
			else {
				dx = (range - (p - r - r)) / (double) istep;
			}
			//System.out.println("dx="+dx); //testing dx

			//	Compute X(K) and DEP(K)
			x0 = p - r - r;
			double sum = 0;


			for(k = 1; k <= istep; k++) {
				// 4/17/09 System.out.println("k="+k);
				x = x0 + dx * (double) k;
				x2 = x * x;
				x3 = x2 * x;
				x4 = x3 * x;
				x5 = x4 * x;

				double dep = 0;

				depValue = new DepValueFunc( dep );

				//error or difference in dep in Alpha!!!!!!!!!!!!!!!!!!!!!!!!
				dep = depValue.getDepValue( radType, e0, range, x, dx, list );
				sum += dep;


				/**
				 *below is not working!!!!!
				 */
				if(sum > e0) {

					dep = e0 - (sum - dep);
					//sum = e0;

				}
				/**
				 * dep become negative when sum larger than energy
				 * dep value become 0 when j++
				 */
				/*
				 * could this cause the negative dx...? or be the cause of it?
				 */

				/**
				 * Absorb Fraction
				 * calculation functions
				 */
				absFraction = new AbsorbFraction( dep, x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5 );

				// 1 = Cell to Cell
				// 2 = Cell surface to Cell
				// 3 = Nucleus to Nucleus
				// 5 = Cell surface to Nucleus
				absf1 += absFraction.getAbsf1();
				absf2 += absFraction.getAbsf2();
				absf3 += absFraction.getAbsf3();
				absf5 += absFraction.getAbsf5();


				/**
				 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
				 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
				 */
				absfr += absFraction.getAbsfr();

			}//end loop k

			absf1 = absf1 / e0;
			absf2 = absf2 / e0;
			absf3 = absf3 / e0;
			absfr = absfr / e0;

			//revised by 09/08/2010
			absf4 = (xnuclmass / cytomass) * ((cellmass / xnuclmass) * absfr - absf3);
			absf5 = absf5 / e0;

			//loop radiation steps i!!!!!!!!!!!!!!!!????????????
			//inner loop i required!!!!!

			doseFunc = new DoseFunc( delta, cellmass, xnuclmass, cytomass );
			/**
			 * keep every dose value in ArrayList
			 * for each radiation type
			 * DOSE(J,1) = DOSE(J,1) + ABSF(1)*DELTA(I)*ACT/CELLMASS
			 *
			 i = 1, j = 10, range(1), jstop1
			 *  dose(10, 1) = 0 + f(delta(1))
			 *  ...
			 *  ..
			 *  .
			 *  dose(jstop1, 1) = dose(10, 1)+......+dose(jstop-1, 1)
			 *
			 * i = 2, j = 10, range(2), jstop2
			 * dose'(10, 1) = dose(10, 1)+ f(delta(2))
			 * dose
			 DOSE(J,2) = DOSE(J,2) + ABSF(2)*DELTA(I)*ACT/CELLMASS

			 DOSE(J,3) = DOSE(J,3) + ABSF(3)*DELTA(I)*ACT/XNUCLMASS

			 DOSE(J,4) = DOSE(J,4) + ABSF(4)*DELTA(I)*ACT/XNUCLMASS

			 DOSE(J,5) = DOSE(J,5) + ABSF(5)*DELTA(I)*ACT/XNUCLMASS
			 *
			 */

			//ArrayList doseList1 = new ArrayList();
			dose1 = doseFunc.DoseCal1( absf1 );
			dose2 = doseFunc.DoseCal2( absf2 );
			dose3 = doseFunc.DoseCal3( absf3 );
			dose4 = doseFunc.DoseCal4( absf4 );
			dose5 = doseFunc.DoseCal5( absf5 );
			//add 09/09/2010
			//Cy <- N , S(Cy<-N)=S(N<-Cy)
			//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
			//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
			dose6 = dose4;
			dose7 = (dose2 * cellmass - dose5 * xnuclmass) / cytomass;
			dose8 = (dose1 * cellmass * cellmass) / (cytomass * cytomass) - (2 * dose4 * xnuclmass) / cytomass - (dose3 * xnuclmass * xnuclmass) / (cytomass * cytomass);

			doseValue1 = formatter.format( dose1 );
			doseValue2 = formatter.format( dose2 );
			doseValue3 = formatter.format( dose3 );
			doseValue4 = formatter.format( dose4 );
			doseValue5 = formatter.format( dose5 );
			doseValue6 = formatter.format( dose6 );
			doseValue7 = formatter.format( dose7 );
			doseValue8 = formatter.format( dose8 );

			//writeFile.writeToFile(j, doseValue1, doseValue2, doseValue3, doseValue4, doseValue5, directory);
			jTextArea1.append( String.valueOf( j ) + "       " + "   " + doseValue1 + "     " + doseValue2 + "     " + doseValue3 + "     " + doseValue4 + "     " + doseValue5 + "     " + doseValue6 + "     " + doseValue7 + "     " + doseValue8 + "\n" );//0701

		}//end j loop

	}//end method getCrossDose()

	public ArrayList getCrossDose1(String radType, int num, String directory) {
		ArrayList doseList1 = new ArrayList();

		jstart = 2 * (int) rc;
		jstop = jStop.getJStop( range, rc, rn );
		//System.out.println ("range= "+range);
		//System.out.println ("jstop= "+jstop);

		for(j = jstart; j <= jstop; j++) {
			doseArray1[j] = 0.0;
			//System.out.println("j=" +j);
			absf1 = 0;
			absf2 = 0;
			absf3 = 0;
			absf4 = 0;
			absf5 = 0;
			absfr = 0;
			p = (double) j;
			p2 = p * p;
			p3 = p2 * p;
			p4 = p3 * p;
			p5 = p4 * p;

			//	Select value of DX depending on whether Range > max distance
			if(range > p + r + r) {
				dx = 2.0 * (r + r) / (double) istep;
			}
			else {
				dx = (range - (p - r - r)) / (double) istep;
			}


			//	Compute X(K) and DEP(K)
			x0 = p - r - r;
			double sum = 0;


			for(k = 1; k <= istep; k++) {
				// 4/17/09 System.out.println("k="+k);
				x = x0 + dx * (double) k;
				x2 = x * x;
				x3 = x2 * x;
				x4 = x3 * x;
				x5 = x4 * x;

				double dep = 0;
				depValue = new DepValueFunc( dep );
				//error or difference in dep in Alpha!!!!!!!!!!!!!!!!!!!!!!!!
				dep = depValue.getDepValue( radType, e0, range, x, dx, list );
				sum += dep;
				/**
				 * below is not working!!!!!
				 */
				if(sum >= e0) {
					dep = e0 - (sum - dep);
					//sum = e0;
				}
				/**
				 * dep become - when sum larger than energy
				 * dep value become 0 when j++
				 */
				/**
				 * Absorb Fraction
				 * calculation functions
				 */
				absFraction = new AbsorbFraction( dep, x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5 );
				// 1 = Cell to Cell
				// 2 = Cell surface to Cell
				// 3 = Nucleus to Nucleus
				// 5 = Cell surface to Nucleus
				absf1 += absFraction.getAbsf1();
				//absf2 += absFraction.getAbsf2();
				//absf3 += absFraction.getAbsf3();
				//absf5 += absFraction.getAbsf5();
				/**
				 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
				 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
				 */
				//absfr += absFraction.getAbsfr();

			}//end loop k
			absf1 = absf1 / e0;
			//absf2=absf2/e0;
			//absf3=absf3/e0;
			//absfr=absfr/e0;
			//revised by 09/08/2010
			//absf4= (xnuclmass/cytomass)*((cellmass/xnuclmass)*absfr - absf3);
			//absf5=absf5/e0;

			//loop radiation steps i!!!!!!!!!!!!!!!!????????????
			//inner loop i required!!!!!

			doseFunc = new DoseFunc( delta, cellmass, xnuclmass, cytomass );
			/**
			 * keep every dose value in ArrayList
			 * for each radiation type
			 * DOSE(J,1) = DOSE(J,1) + ABSF(1)*DELTA(I)*ACT/CELLMASS
			 *
			 i = 1, j = 10, range(1), jstop1
			 *  dose(10, 1) = 0 + f(delta(1))
			 *  ...
			 *  ..
			 *  .
			 *  dose(jstop1, 1) = dose(10, 1)+......+dose(jstop-1, 1)
			 *
			 * i = 2, j = 10, range(2), jstop2
			 * dose'(10, 1) = dose(10, 1)+ f(delta(2))
			 * dose
			 DOSE(J,2) = DOSE(J,2) + ABSF(2)*DELTA(I)*ACT/CELLMASS

			 DOSE(J,3) = DOSE(J,3) + ABSF(3)*DELTA(I)*ACT/XNUCLMASS

			 DOSE(J,4) = DOSE(J,4) + ABSF(4)*DELTA(I)*ACT/XNUCLMASS

			 DOSE(J,5) = DOSE(J,5) + ABSF(5)*DELTA(I)*ACT/XNUCLMASS
			 *
			 */

			dose1 = doseFunc.DoseCal1( absf1 );
			doseList1.add( String.valueOf( dose1 ) );
		}//end j loop

		/**
		 * add method to store temporary dose value
		 */
		return doseList1;
	}//end method getCrossDose()

	public ArrayList getCrossDose2(String radType, int num, String directory) {
		//ArrayList doseList1 = new ArrayList();
		ArrayList doseList2 = new ArrayList();
		//ArrayList doseList3 = new ArrayList();
		//ArrayList doseList4 = new ArrayList();
		//ArrayList doseList5 = new ArrayList();

		jstart = 2 * (int) rc;
		jstop = jStop.getJStop( range, rc, rn );
		//System.out.println ("jstop= "+jstop);

		for(j = jstart; j <= jstop; j++) {
			doseArray1[j] = 0.0;
			absf1 = 0;
			absf2 = 0;
			absf3 = 0;
			absf4 = 0;
			absf5 = 0;
			absfr = 0;
			//System.out.println("j=" +j);
			p = (double) j;
			p2 = p * p;
			p3 = p2 * p;
			p4 = p3 * p;
			p5 = p4 * p;

			//	Select value of DX depending on whether Range > max distance
			if(range > p + r + r) {
				dx = 2.0 * (r + r) / (double) istep;
			}
			else {
				dx = (range - (p - r - r)) / (double) istep;
			}
			//System.out.println("dx="+dx); //testing dx

			//	Compute X(K) and DEP(K)
			x0 = p - r - r;
			double sum = 0;


			for(k = 1; k <= istep; k++) {
				// 4/17/09 System.out.println("k="+k);
				x = x0 + dx * (double) k;
				x2 = x * x;
				x3 = x2 * x;
				x4 = x3 * x;
				x5 = x4 * x;

				double dep = 0;
				//depFunc = new DepFunc(sum, dep);
				depValue = new DepValueFunc( dep );
				//error or difference in dep in Alpha!!!!!!!!!!!!!!!!!!!!!!!!
				//dep = depFunc.getDep(radType, e0, range, x, dx, list);  //compute deposit value dep
				dep = depValue.getDepValue( radType, e0, range, x, dx, list );
				// 4/17/09 System.out.println("03/31/09 dep = "+dep);
				sum += dep;

				//3/31/09 System.out.println("the result of SUM = "+sum);
				// 4/17/09 System.out.println("sum 4/7/09 = " +sum);
				//test e0
				//System.out.println("e0="+e0);
				/**
				 *below is not working!!!!!
				 *
				 */


				if(sum > e0) {
					dep = e0 - (sum - dep);
					sum = e0;
				}
				/**
				 * dep become - when sum larger than energy
				 * dep value become 0 when j++
				 */
				/**
				 * Absorb Fraction
				 * calculation functions
				 */
				absFraction = new AbsorbFraction( dep, x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5 );
				// Cell to Cell
				//absf1 += absFraction.getAbsf1();
				// Cell surface to Cell
				absf2 += absFraction.getAbsf2();
				// Nucleus to Nucleus
				//absf3 += absFraction.getAbsf3();
				// Cell surface to Nucleus
				//absf5 += absFraction.getAbsf5();
				/**
				 *
				 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
				 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
				 */
				//absfr += absFraction.getAbsfr();

			}//end loop k
			//absf1=absf1/e0;
			absf2 = absf2 / e0;
			//absf3=absf3/e0;
			//absfr=absfr/e0;
			//revised by 09/08/2010
			//absf4= (xnuclmass/cytomass)*((cellmass/xnuclmass)*absfr - absf3);
			//absf5=absf5/e0;

			//loop radiation steps i!!!!!!!!!!!!!!!!????????????
			//inner loop i required!!!!!

			doseFunc = new DoseFunc( delta, cellmass, xnuclmass, cytomass );

			dose2 = doseFunc.DoseCal2( absf2 );
			//writeFile.wirteToFile();
			//doseList1.add(formatter.format(dose1));
			//doseList1.add(formatter.format(doseFunc.DoseCal1(absf1, delta, cellmass, xnuclmass, cytomass)));
			doseList2.add( String.valueOf( dose2 ) );
			//doseList3.add(formatter.format(doseFunc.DoseCal3(absf3, delta, cellmass, xnuclmass, cytomass)));
			//doseList4.add(formatter.format(doseFunc.DoseCal4(absf4, delta, cellmass, xnuclmass, cytomass)));
			//doseList5.add(formatter.format(doseFunc.DoseCal5(absf5, delta, cellmass, xnuclmass, cytomass)));
			//System.out.println(doseList1);
			//writeFile.writeToFile(doseList1, directory);
			//doseArray1[j] = doseArray1[j] + doseFunc.DoseCal1(absf1, delta, cellmass, xnuclmass, cytomass);
			//System.out.println("dose array("+j+")="+doseArray1[j]);

		}//end j loop
		/**
		 * add method to store temporary dose value
		 */
		return doseList2;
	}//end method getCrossDose()

	public ArrayList getCrossDose3(String radType, int num, String directory) {
		ArrayList doseList3 = new ArrayList();

		jstart = 2 * (int) rc;
		jstop = jStop.getJStop( range, rc, rn );

		for(j = jstart; j <= jstop; j++) {
			doseArray1[j] = 0.0;
			//System.out.println("j=" +j);
			absf1 = 0;
			absf2 = 0;
			absf3 = 0;
			absf4 = 0;
			absf5 = 0;
			absfr = 0;
			p = (double) j;
			p2 = p * p;
			p3 = p2 * p;
			p4 = p3 * p;
			p5 = p4 * p;

			//	Select value of DX depending on whether Range > max distance
			if(range > p + r + r) {
				dx = 2.0 * (r + r) / (double) istep;
			}
			else {
				dx = (range - (p - r - r)) / (double) istep;
			}
			//System.out.println("dx="+dx); //testing dx

			//	Compute X(K) and DEP(K)
			x0 = p - r - r;
			double sum = 0;


			for(k = 1; k <= istep; k++) {
				x = x0 + dx * (double) k;
				x2 = x * x;
				x3 = x2 * x;
				x4 = x3 * x;
				x5 = x4 * x;

				double dep = 0;
				depValue = new DepValueFunc( dep );
				dep = depValue.getDepValue( radType, e0, range, x, dx, list );
				sum += dep;

				/**
				 *below is not working!!!!!
				 */
				if(sum >= e0) {
					dep = e0 - (sum - dep);
					//sum = e0;
				}
				/**
				 * dep become - when sum larger than energy
				 * dep value become 0 when j++
				 */
				/**
				 * Absorb Fraction
				 * calculation functions
				 */
				absFraction = new AbsorbFraction( dep, x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5 );
				// Cell to Cell
				//absf1 += absFraction.getAbsf1();
				// Cell surface to Cell
				//absf2 += absFraction.getAbsf2();
				// Nucleus to Nucleus
				absf3 += absFraction.getAbsf3();
				// Cell surface to Nucleus
				//absf5 += absFraction.getAbsf5();
				/**
				 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
				 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
				 */
				//absfr += absFraction.getAbsfr();

			}//end loop k
			//absf1=absf1/e0;
			//absf2=absf2/e0;
			absf3 = absf3 / e0;
			//absfr=absfr/e0;
			//revised by 09/08/2010
			//absf4= (xnuclmass/cytomass)*((cellmass/xnuclmass)*absfr - absf3);
			//absf5=absf5/e0;

			doseFunc = new DoseFunc( delta, cellmass, xnuclmass, cytomass );

			dose3 = doseFunc.DoseCal3( absf3 );
			doseList3.add( String.valueOf( dose3 ) );

		}//end j loop
		/**
		 * add method to store temporary dose value
		 */
		return doseList3;
	}//end method getCrossDose()

	public ArrayList getCrossDose4(String radType, int num, String directory) {
		ArrayList doseList4 = new ArrayList();

		jstart = 2 * (int) rc;
		jstop = jStop.getJStop( range, rc, rn );

		for(j = jstart; j <= jstop; j++) {
			doseArray1[j] = 0.0;
			absf1 = 0;
			absf2 = 0;
			absf3 = 0;
			absf4 = 0;
			absf5 = 0;
			absfr = 0;
			p = (double) j;
			p2 = p * p;
			p3 = p2 * p;
			p4 = p3 * p;
			p5 = p4 * p;

			//	Select value of DX depending on whether Range > max distance
			if(range > p + r + r) {
				dx = 2.0 * (r + r) / (double) istep;
			}
			else {
				dx = (range - (p - r - r)) / (double) istep;
			}

			//	Compute X(K) and DEP(K)
			x0 = p - r - r;
			double sum = 0;

			for(k = 1; k <= istep; k++) {
				// 4/17/09 System.out.println("k="+k);
				x = x0 + dx * (double) k;
				x2 = x * x;
				x3 = x2 * x;
				x4 = x3 * x;
				x5 = x4 * x;

				double dep = 0;
				depValue = new DepValueFunc( dep );
				dep = depValue.getDepValue( radType, e0, range, x, dx, list );
				sum += dep;
				/**
				 *below is not working!!!!!
				 */

				if(sum >= e0) {
					dep = e0 - (sum - dep);
					sum = e0;
				}
				/**
				 * dep become - when sum larger than energy
				 * dep value become 0 when j++
				 */
				/**
				 * Absorb Fraction
				 * calculation functions
				 */
				absFraction = new AbsorbFraction( dep, x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5 );
				// Cell to Cell
				//absf1 += absFraction.getAbsf1();
				// Cell surface to Cell
				//absf2 += absFraction.getAbsf2();
				// Nucleus to Nucleus
				absf3 += absFraction.getAbsf3();
				// Cell surface to Nucleus
				//absf5 += absFraction.getAbsf5();
				/**
				 *
				 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
				 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
				 */
				absfr += absFraction.getAbsfr();

			}//end loop k
			//absf1=absf1/e0;
			//absf2=absf2/e0;
			absf3 = absf3 / e0;
			absfr = absfr / e0;
			absf4 = (xnuclmass / cytomass) * ((cellmass / xnuclmass) * absfr - absf3);
			//absf5=absf5/e0;

			doseFunc = new DoseFunc( delta, cellmass, xnuclmass, cytomass );

			dose4 = doseFunc.DoseCal4( absf4 );
			doseList4.add( String.valueOf( dose4 ) );
		}//end j loop
		/**
		 * add method to store temporary dose value
		 */
		return doseList4;
	}//end method getCrossDose()

	public ArrayList getCrossDose5(String radType, int num, String directory) {
		ArrayList doseList5 = new ArrayList();

		jstart = 2 * (int) rc;
		jstop = jStop.getJStop( range, rc, rn );
		//System.out.println ("jstop= "+jstop);

		for(j = jstart; j <= jstop; j++) {
			doseArray1[j] = 0.0;
			absf1 = 0;
			absf2 = 0;
			absf3 = 0;
			absf4 = 0;
			absf5 = 0;
			absfr = 0;
			//System.out.println("j=" +j);
			p = (double) j;
			p2 = p * p;
			p3 = p2 * p;
			p4 = p3 * p;
			p5 = p4 * p;

			//	Select value of DX depending on whether Range > max distance
			if(range > p + r + r) {
				dx = 2.0 * (r + r) / (double) istep;
			}
			else {
				dx = (range - (p - r - r)) / (double) istep;
			}
			//System.out.println("dx="+dx); //testing dx

			//	Compute X(K) and DEP(K)
			x0 = p - r - r;
			double sum = 0;


			for(k = 1; k <= istep; k++) {
				x = x0 + dx * (double) k;
				x2 = x * x;
				x3 = x2 * x;
				x4 = x3 * x;
				x5 = x4 * x;

				double dep = 0;
				depValue = new DepValueFunc( dep );
				dep = depValue.getDepValue( radType, e0, range, x, dx, list );
				sum += dep;

				/**
				 *below is not working!!!!!
				 */
				if(sum >= e0) {
					dep = e0 - (sum - dep);
					sum = e0;
				}
				/**
				 * dep become - when sum larger than energy
				 * dep value become 0 when j++
				 */
				/**
				 * Absorb Fraction
				 * calculation functions
				 */
				absFraction = new AbsorbFraction( dep, x, x2, x3, x4, x5, p, p2, p3, p4, p5, r, r2, r3, r4, r5, a, a2, a3, a4, a5 );
				// Cell to Cell
				//absf1 += absFraction.getAbsf1();
				// Cell surface to Cell
				//absf2 += absFraction.getAbsf2();
				// Nucleus to Nucleus
				//absf3 += absFraction.getAbsf3();
				// Cell surface to Nucleus
				absf5 += absFraction.getAbsf5();
				/**
				 * Cytoplasm to Nucleus - ABSFR is the fraction of energy emitted from the cell and absorbed in the nucleus.
				 * The reciprocity rule is used to obtain the absorb fraction between cytoplasm to nucleus.
				 */
				//absfr += absFraction.getAbsfr();

			}//end loop k
			//absf1=absf1/e0;
			//absf2=absf2/e0;
			//absf3=absf3/e0;
			//absfr=absfr/e0;
			//absf4= (xnuclmass/cytomass)*((cellmass/xnuclmass)*absfr - absf3);
			absf5 = absf5 / e0;

			doseFunc = new DoseFunc( delta, cellmass, xnuclmass, cytomass );

			dose5 = doseFunc.DoseCal5( absf5 );
			doseList5.add(String.valueOf( dose5 ));
		}//end j loop
		return doseList5;
	}//end method getCrossDose()
	/**********************
	 * new cross-dose 10/13/2010
	 **********************/
}//end class