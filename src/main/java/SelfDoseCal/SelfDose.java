/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SelfDoseCal;


import File.AlphaRange;
import File.ArrayListIn2;
import File.MyList;
import File.MyList2;
import File.WriteToOutputFile;

//import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

//import GUI.Home;

import javax.swing.JTextArea;

/**
 * @author Johnny  Wu
 */
public class SelfDose {

	static Newton newton = new Newton();
	static String fileName = "alph-water2";
	static AlphaRange alphaRange = new AlphaRange();
	static WriteToOutputFile writeFile = new WriteToOutputFile();
	static NumberFormat formatter = new DecimalFormat( "0.00E00" );
	static SelfDoseCal self;
	static MyList ml;
	MyList2 mList2;
	ArrayList<MyList> list = new ArrayList<MyList>();
	ArrayList<MyList2> list2 = new ArrayList<MyList2>();
	ArrayListIn2 ali = new ArrayListIn2();
	static String s1 = "", s2 = "", s3 = "", s4 = "", s5 = "", s6 = "";
	static double rc = 0, rn = 0, enMev = 0, en = 0, xn = 0, range = 0, delta = 0;
	//static double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;
	static String directory = "";
	double cellmass = 0, xnuclmass = 0, cytomass = 0;

	static int num = 0;
	static String[] monoSelf = {};
	ArrayList list3 = new ArrayList();

	public SelfDose(ArrayList<MyList> list) {
		this.list = list;
		//this.list2 = list2;
		//added 09/09/2010
		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
	}

	public SelfDose() {
	}

	public SelfDose(String s1, Double en, int num, double rc, double rn, String directory, ArrayList<MyList2> list2) {
		this.s1 = s1;
		this.en = en;
		this.num = num;
		this.rc = rc;
		this.rn = rn;
		this.directory = directory;
		this.list2 = list2;

		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
	}

	public void getSelf(int num, double rc, double rn, URL directory, ArrayList<MyList2> list2) {
		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;

		for(int i = 6; i < num + 6; i++) { //testing

			MyList ml1 = list.get( i );
			s1 = ml1.getLine1();
			s2 = ml1.getLine2();
			s3 = ml1.getLine3();
			s4 = ml1.getLine4();
			//s5 = ml1.getLine5();
			if(s1.equals( "GAMMA" ) || s1.equals( "KXALPH" ) || s1.equals( "KXBETA" ) || s1.equals( "LXALPH" ) || s1.equals( "LXBETA" ) || s1.equals( "MXALPH" ) || s1.equals( "NXALPH" )) {
				//System.out.println("Gamma & X-ray not allowed.");
				//continue out;
			}
			else if(s1.equals( "BETA" ) || s1.equals( "KCONV" ) || s1.equals( "LCONV" ) || s1.equals( "MCONV" ) || s1.equals( "KLXAUG" ) || s1.equals( "LMMAUG" ) || s1.equals( "MXYAUG" ) || s1.equals( "LLXCK" ) || s1.equals( "KLLAUG" ) || s1.equals( "CONV" ) || s1.equals( "LMXAUG" ) || s1.equals( "LXYAUG" ) || s1.equals( "KXYAUG" ) || s1.equals( "MMXCK" ) || s1.equals( "NNXCK" ) || s1.equals( "NXYAUG" ) || s1.equals( "OOXCK" ) || s1.equals( "ALPHA" ) || s1.equals( "NCONV" ) || s1.equals( "OCONV" )) {
				// 4/16/2009 System.out.print("i="+i+" ");
				//introduce selfDose calculation functions

				xn = Double.parseDouble( s3 );

				en = Double.parseDouble( s4 );
				delta = xn * en * 2.13;
				en = en * (1.0e3);
				//delta = Double.parseDouble(s5);

				// 4/16/2009 //System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.equals( "ALPHA" )) {
					//alphaRange  = new AlphaRange(fileName);
					//if alpha, range(i)=(enkev(i)/390.0)**1.5
					range = alphaRange.getAlphaRange( en, list2 );
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
				//System.out.println("dcc="+dcc+"\n"+"dcs="+dcs+"\n"+"dnn="+dnn+"\n"+"dncy="+dncy+"\n"+"dns="+dns);
			}
			else {
				//System.out.println("Unknown radiation type.");
			}
		}//end loop i radiation type

	}

	//update 08/04/09
	//self dose for mono
	//revise 07/
	public void getSelfMono(String s1, Double en, Double xn, int num, double rc, double rn, JTextArea jTextArea1, ArrayList<MyList2> list2) {
		//added 09/09/2010
		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;
		//added 09/09/2020
		double dcyn = 0, dcycs = 0, dcycy = 0;

		//alphaRange  = new AlphaRange(fileName);
		//try{
		//   list2 = ali.readAlpha(fileName);
		//}catch(IOException ioe){
		//
		// }

		for(int i = 6; i < num + 6; i++) { //testing


			//System.out.println("mono iso= "+s1);

			//else if (s1.contains("BETA")||s1.contains("KCONV")||s1.contains("LCONV")||s1.contains("MCONV")||s1.contains("KLXAUG")||s1.contains("LMMAUG")||s1.contains("MXYAUG")||s1.contains("LLXCK")||s1.contains("KLLAUG")||s1.contains("CONV")||s1.contains("LMXAUG")||s1.contains("LXYAUG")||s1.contains("KXYAUG")||s1.contains("MMXCK")||s1.contains("NNXCK")||s1.contains("NXYAUG")||s1.contains("OOXCK")||s1.contains("ALPHA")||s1.contains("NCONV")||s1.contains("OCONV")) {
			// 4/16/2009 System.out.print("i="+i+" ");
			//introduce selfDose calculation functions
			//0702
			if(s1.contains( "Electron" ) || s1.contains( "Alpha" )) {
				//xn = 1; // 11/17/2010
				delta = xn * en * 2.13;
				en = en * (1.0e3);

				//System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.contains( "Alpha" )) {
					//alphaRange  = new AlphaRange(fileName);
					//if alpha, range(i)=(enkev(i)/390.0)**1.5
					range = alphaRange.getAlphaRange( en, list2 );
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}
				//System.out.println("11/05/2010 range ="+range);

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
				//System.out.println("dcc="+dcc+"\n"+"dcs="+dcs+"\n"+"dnn="+dnn+"\n"+"dncy="+dncy+"\n"+"dns="+dns);
			}
			else {
				//System.out.println("Unknown radiation type.");
			}
		}//end loop i radiation type
		//added 09/09/2010
		//System.out.println("09/09/2010 cellmass="+cellmass+"\n"+"nuclmass="+xnuclmass+"\n"+"cytomass="+cytomass);
		//Cy <- N , S(Cy<-N)=S(N<-Cy)
		dcyn = dncy;
		//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
		dcycs = (dcs * cellmass - dns * xnuclmass) / cytomass;
		//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
		dcycy = (dcc * cellmass * cellmass) / (cytomass * cytomass) - (2 * dncy * xnuclmass) / cytomass - (dnn * xnuclmass * xnuclmass) / (cytomass * cytomass);

		//System.out.println("\n");
		//System.out.println("dcc = "+ formatter.format(dcc));
		//System.out.println("dcs = "+ formatter.format(dcs));
		//System.out.println("dnn = "+ formatter.format(dnn));
		//System.out.println("dncy = "+ formatter.format(dncy));
		//System.out.println("dns = "+ formatter.format(dns));
		//System.out.println("dcyn = "+ formatter.format(dcyn));
		//System.out.println("dcycs = "+ formatter.format(dcycs));
		//System.out.println("dcycy = "+ formatter.format(dcycy));

		//07/01/2010 write to jTextArea
		//writeFile.writeSelfToFile(formatter.format(dcc), formatter.format(dcs), formatter.format(dnn), formatter.format(dncy), formatter.format(dns), jTextArea1);
		//09/09/2010
		writeFile.writeSelfToFile( formatter.format( dcc ), formatter.format( dcs ), formatter.format( dnn ), formatter.format( dncy ), formatter.format( dns ), formatter.format( dcyn ), formatter.format( dcycs ), formatter.format( dcycy ), jTextArea1 );

	}

	//get self-dose for new MIRD
	public void getSelfNewMIRD(int num, double rc, double rn, String directory, ArrayList<MyList2> list2) {
		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;

		for(int i = 121 + 12; i < num + 121 + 12; i++) { //testing

			MyList ml1 = list.get( i );
			s1 = ml1.getLine1();
			s2 = ml1.getLine2();
			s3 = ml1.getLine3();
			s4 = ml1.getLine4();
			//s5 = ml1.getLine5();
			if(s1.equals( "GAMMA-RAY " ) || s1.equals( "K-L2.X-RAY" ) || s1.equals( "K-L3.X-RAY " ) || s1.equals( "LXALPH" ) || s1.equals( "LXBETA" ) || s1.equals( "MXALPH" ) || s1.equals( "NXALPH" )) {
				//System.out.println("Gamma & X-ray not allowed.");
				//continue out;
			}
			else if(s1.equals( "BETA-MINUS" ) || s1.equals( "KCONV.ELECTRON,GAMMA-RAY" ) || s1.equals( "L1CONV.ELECTRON,GAMMA-RAY" ) || s1.equals( "MCONV" ) || s1.equals( "KLXAUG" ) || s1.equals( "LMMAUG" ) || s1.equals( "MXYAUG" ) || s1.equals( "LLXCK" ) || s1.equals( "KLLAUG" ) || s1.equals( "CONV" ) || s1.equals( "LMXAUG" ) || s1.equals( "LXYAUG" ) || s1.equals( "KXYAUG" ) || s1.equals( "MMXCK" ) || s1.equals( "NNXCK" ) || s1.equals( "NXYAUG" ) || s1.equals( "OOXCK" ) || s1.equals( "ALPHA" ) || s1.equals( "NCONV" ) || s1.equals( "OCONV" )) {

				xn = Double.parseDouble( s3 );
				en = Double.parseDouble( s4 );
				delta = xn * en * 2.13;
				en = en * (1.0e3);
				// 4/16/2009 //System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.equals( "ALPHA" )) {
					//alphaRange  = new AlphaRange(fileName);
					//if alpha, range(i)=(enkev(i)/390.0)**1.5
					range = alphaRange.getAlphaRange( en, list2 );
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
				//System.out.println("dcc="+dcc+"\n"+"dcs="+dcs+"\n"+"dnn="+dnn+"\n"+"dncy="+dncy+"\n"+"dns="+dns);
			}
			else {
				//System.out.println("Unknown radiation type.");
			}
		}//end loop i radiation type
		//System.out.println("\n");
		//System.out.println("dcc = "+ formatter.format(dcc));
		//System.out.println("dcs = "+ formatter.format(dcs));
		//System.out.println("dnn = "+ formatter.format(dnn));
		//System.out.println("dncy = "+ formatter.format(dncy));
		//System.out.println("dns = "+ formatter.format(dns));

//04/01                         writeFile.writeSelfToFile(formatter.format(dcc), formatter.format(dcs), formatter.format(dnn), formatter.format(dncy), formatter.format(dns), directory);
	}

	/********************
	 * 07/01/2010
	 * get mono self dose
	 ********************/
	public void getSelfMono1() {

		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;

		for(int i = 6; i < num + 6; i++) { //testing


			//System.out.println("mono iso= "+s1);
			if(s1.contains( "GAMMA" ) || s1.contains( "KXALPH" ) || s1.contains( "KXBETA" ) || s1.contains( "LXALPH" ) || s1.contains( "LXBETA" ) || s1.contains( "MXALPH" ) || s1.contains( "NXALPH" )) {
				//System.out.println("Gamma & X-ray not allowed.");
				//continue out;
			}
			else if(s1.contains( "BETA" ) || s1.contains( "KCONV" ) || s1.contains( "LCONV" ) || s1.contains( "MCONV" ) || s1.contains( "KLXAUG" ) || s1.contains( "LMMAUG" ) || s1.contains( "MXYAUG" ) || s1.contains( "LLXCK" ) || s1.contains( "KLLAUG" ) || s1.contains( "CONV" ) || s1.contains( "LMXAUG" ) || s1.contains( "LXYAUG" ) || s1.contains( "KXYAUG" ) || s1.contains( "MMXCK" ) || s1.contains( "NNXCK" ) || s1.contains( "NXYAUG" ) || s1.contains( "OOXCK" ) || s1.contains( "ALPHA" ) || s1.contains( "NCONV" ) || s1.contains( "OCONV" )) {
				// 4/16/2009 System.out.print("i="+i+" ");
				//introduce selfDose calculation functions

				xn = 1;
				delta = xn * en * 2.13;
				en = en * (1.0e3);

				// 4/16/2009 //System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.contains( "ALPHA" )) {
					//alphaRange  = new AlphaRange(fileName);
					//if alpha, range(i)=(enkev(i)/390.0)**1.5
					range = alphaRange.getAlphaRange( en, list2 );
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
				//System.out.println("dcc="+dcc+"\n"+"dcs="+dcs+"\n"+"dnn="+dnn+"\n"+"dncy="+dncy+"\n"+"dns="+dns);
			}
			else {
				//System.out.println("Unknown radiation type.");
			}
		}//end loop i radiation type

		//07/01/2010 write to jTextArea
		//writeFile.writeSelfToFile(formatter.format(dcc), formatter.format(dcs), formatter.format(dnn), formatter.format(dncy), formatter.format(dns));
	}

	/****************
	 * 071510 calculate self-dose value and write to jTextArea1 (output area)
	 */
	public void getSelf(int num, double rc, double rn, JTextArea jTextArea1, ArrayList<MyList2> list2) {
		//added 09/09/2010
		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;
		double dcyn = 0, dcycs = 0, dcycy = 0;
		/**
		 alphaRange  = new AlphaRange(fileName);
		 try{
		 list2 = ali.readAlpha(fileName);
		 }catch(IOException ioe){
		 //
		 }
		 */
		for(int i = 6; i < num + 6; i++) { //testing

			MyList ml1 = list.get( i );
			s1 = ml1.getLine1();
			s2 = ml1.getLine2();
			s3 = ml1.getLine3();
			s4 = ml1.getLine4();
			//s5 = ml1.getLine5();
			if(s1.equals( "GAMMA" ) || s1.equals( "KXALPH" ) || s1.equals( "KXBETA" ) || s1.equals( "LXALPH" ) || s1.equals( "LXBETA" ) || s1.equals( "MXALPH" ) || s1.equals( "NXALPH" )) {
				//System.out.println("Gamma & X-ray not allowed.");
				//continue out;
			}
			else if(s1.equals( "BETA" ) || s1.equals( "KCONV" ) || s1.equals( "LCONV" ) || s1.equals( "MCONV" ) || s1.equals( "KLXAUG" ) || s1.equals( "LMMAUG" ) || s1.equals( "MXYAUG" ) || s1.equals( "LLXCK" ) || s1.equals( "KLLAUG" ) || s1.equals( "CONV" ) || s1.equals( "LMXAUG" ) || s1.equals( "LXYAUG" ) || s1.equals( "KXYAUG" ) || s1.equals( "MMXCK" ) || s1.equals( "NNXCK" ) || s1.equals( "NXYAUG" ) || s1.equals( "OOXCK" ) || s1.equals( "ALPHA" ) || s1.equals( "NCONV" ) || s1.equals( "OCONV" )) {
				// 4/16/2009 System.out.print("i="+i+" ");
				//introduce selfDose calculation functions

				xn = Double.parseDouble( s3 );
				en = Double.parseDouble( s4 );
				delta = xn * en * 2.13;
				en = en * (1.0e3);
				// 4/16/2009 //System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.equals( "ALPHA" )) {
					//alphaRange  = new AlphaRange(fileName);
					//if alpha, range(i)=(enkev(i)/390.0)**1.5
					range = alphaRange.getAlphaRange( en, list2 );
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
				//System.out.println("dcc="+dcc+"\n"+"dcs="+dcs+"\n"+"dnn="+dnn+"\n"+"dncy="+dncy+"\n"+"dns="+dns);
			}
			else {
				//System.out.println("Unknown radiation type.");
			}
		}//end loop i radiation type
		//added 09/09/2010
		//Cy <- N , S(Cy<-N)=S(N<-Cy)
		dcyn = dncy;
		//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
		dcycs = (dcs * cellmass - dns * xnuclmass) / cytomass;
		//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
		dcycy = (dcc * cellmass * cellmass) / (cytomass * cytomass) - (2 * dncy * xnuclmass) / cytomass - (dnn * xnuclmass * xnuclmass) / (cytomass * cytomass);

		//System.out.println("\n");
		//System.out.println("dcc = "+ formatter.format(dcc));
		//System.out.println("dcs = "+ formatter.format(dcs));
		//System.out.println("dnn = "+ formatter.format(dnn));
		//System.out.println("dncy = "+ formatter.format(dncy));
		//System.out.println("dns = "+ formatter.format(dns));

		//09/09/2010
		writeFile.writeSelfToFile( formatter.format( dcc ), formatter.format( dcs ), formatter.format( dnn ), formatter.format( dncy ), formatter.format( dns ), formatter.format( dcyn ), formatter.format( dcycs ), formatter.format( dcycy ), jTextArea1 );
	}//end self

	/****************
	 * 10/26/2010 calculate self-dose value for User-Created file and write to jTextArea1 (output area)
	 */
	public void getSelfUser(int start, int num, double rc, double rn, JTextArea jTextArea1, ArrayList<MyList2> list2, ArrayList list3) {
		//added 09/09/2010
		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;
		double dcyn = 0, dcycs = 0, dcycy = 0;
		this.list3 = list3;
		/**
		 alphaRange  = new AlphaRange(fileName);
		 try{
		 list2 = ali.readAlpha(fileName);
		 }catch(IOException ioe){
		 //
		 }
		 */
		for(int i = start + 1; i < num + start + 1; i++) {
			//MyList ml1= list.get(i);
			s1 = list3.get( i ).toString().trim().split( "\\s+" )[3]; // radiation type
			s2 = list3.get( i ).toString().trim().split( "\\s+" )[0]; // icode
			s3 = list3.get( i ).toString().trim().split( "\\s+" )[1]; // yield/decay
			s4 = list3.get( i ).toString().trim().split( "\\s+" )[2]; // energy
			//System.out.println("11/01/10 s1 ="+s1);

			if(s1.equals( "G" ) || s1.equals( "X" )) {
				//System.out.println("Gamma & X-ray not allowed.");
				//continue out;
			}
			else if(s1.equals( "B+" ) || s1.equals( "B-" ) || s1.equals( "AQ" ) || s1.equals( "IE" ) || s1.equals( "AE" ) || s1.equals( "A" ) || s1.equals( "AR" ) || s1.equals( "FF" ) || s1.equals( "N" )) {
				// 4/16/2009 System.out.print("i="+i+" ");
				//introduce selfDose calculation functions

				xn = Double.parseDouble( s3 );
				en = Double.parseDouble( s4 );
				delta = xn * en * 2.13;
				en = en * (1.0e3);
				//System.out.println("10/26/2010 self xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.equals( "A" )) {
					//System.out.println("11/01/10 if s1 =A:  "+s1.equals("A"));
					//alphaRange  = new AlphaRange(fileName);
					//if alpha, range(i)=(enkev(i)/390.0)**1.5
					range = alphaRange.getAlphaRange( en, list2 );
					s1 = "Alpha";
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
				//System.out.println("dcc="+dcc+"\n"+"dcs="+dcs+"\n"+"dnn="+dnn+"\n"+"dncy="+dncy+"\n"+"dns="+dns);
			}
			else {
				//System.out.println("Unknown radiation type.");
			}
		}//end loop i radiation type
		//added 09/09/2010
		//Cy <- N , S(Cy<-N)=S(N<-Cy)
		dcyn = dncy;
		//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
		dcycs = (dcs * cellmass - dns * xnuclmass) / cytomass;
		//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
		dcycy = (dcc * cellmass * cellmass) / (cytomass * cytomass) - (2 * dncy * xnuclmass) / cytomass - (dnn * xnuclmass * xnuclmass) / (cytomass * cytomass);

		//System.out.println("\n");
		//System.out.println("dcc = "+ formatter.format(dcc));
		//System.out.println("dcs = "+ formatter.format(dcs));
		//System.out.println("dnn = "+ formatter.format(dnn));
		//System.out.println("dncy = "+ formatter.format(dncy));
		//System.out.println("dns = "+ formatter.format(dns));

		/**
		 * dcc   = dose1
		 * dcs   = dose2
		 * dnn   = dose3
		 * dncy  = dose4
		 * dns   = dose5
		 * dcyn  = dose6
		 * dcycs = dose7
		 * dcycy = dose8
		 */

		//09/09/2010
		writeFile.writeSelfToFile( formatter.format( dcc ), formatter.format( dcs ), formatter.format( dnn ), formatter.format( dncy ), formatter.format( dns ), formatter.format( dcyn ), formatter.format( dcycs ), formatter.format( dcycy ), jTextArea1 );
	}// 10/26/2010 end self user

	/****************
	 * 10/29/2010 calculate self-dose value for MIRD file
	 * and write to jTextArea1 (output area)
	 */
	public void getSelfMIRD(int num, double rc, double rn, JTextArea jTextArea1, ArrayList<MyList2> list2) {

		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;
		double dcyn = 0, dcycs = 0, dcycy = 0;
		/**
		 alphaRange  = new AlphaRange(fileName);
		 try{
		 list2 = ali.readAlpha(fileName);
		 }catch(IOException ioe){
		 //
		 }
		 */
		int startNum = list.size() - num - 1;
		for(int i = startNum; i < num + startNum; i++) {
			//System.out.print("10/29/2010 i="+i+" ");
			MyList ml1 = list.get( i );
			s1 = ml1.getLine4();// radition type
			s2 = ml1.getLine2();// yield/decay
			s3 = ml1.getLine3();// energy
			s4 = ml1.getLine1();// ICODE for MIRD
			//System.out.println("10/29/10 s1 MIRD = "+s1);

			if(s1.equals( "G" ) || s1.equals( "X" )) {
				//System.out.println("Gamma & X-ray not allowed for MIRD.");
			}
			else if(s1.equals( "B+" ) || s1.equals( "B-" ) || s1.equals( "AQ" ) || s1.equals( "IE" ) || s1.equals( "AE" ) || s1.equals( "A" ) || s1.equals( "AR" ) || s1.equals( "FF" ) || s1.equals( "N" )) {

				xn = Double.parseDouble( s2 );
				en = Double.parseDouble( s3 );
				delta = 2.13 * en * xn;
				en = en * (1.0e3);
				// 4/16/2009 //System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.equals( "A" )) {
					//alphaRange  = new AlphaRange(fileName);
					//if alpha, range(i)=(enkev(i)/390.0)**1.5
					range = alphaRange.getAlphaRange( en, list2 );
					s1 = "Alpha";
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
			}
			else {
				//System.out.println("Unknown radiation type.");
			}
		}//end loop i radiation type
		//added 09/09/2010
		//Cy <- N , S(Cy<-N)=S(N<-Cy)
		dcyn = dncy;
		//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
		dcycs = (dcs * cellmass - dns * xnuclmass) / cytomass;
		//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
		dcycy = (dcc * cellmass * cellmass) / (cytomass * cytomass) - (2 * dncy * xnuclmass) / cytomass - (dnn * xnuclmass * xnuclmass) / (cytomass * cytomass);

		//09/09/2010
		writeFile.writeSelfToFile( formatter.format( dcc ), formatter.format( dcs ), formatter.format( dnn ), formatter.format( dncy ), formatter.format( dns ), formatter.format( dcyn ), formatter.format( dcycs ), formatter.format( dcycy ), jTextArea1 );
	}//end self-dose cal for MIRD

	public void SelfDoseNew(int num, double rc, double rn, JTextArea jTextArea1, ArrayList<MyList2> list2){
		// this will be the self dose that deals with auger electrons xrays/gamma rays etc and also keeps track of all the new dosing stuff
		cellmass = 4.0 * Math.PI * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * Math.PI * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;
		double dncy = 0, dns = 0, dcs = 0, dcc = 0, dnn = 0;
		double dcyn = 0, dcycs = 0, dcycy = 0;

		int startNum = list.size() - num - 1;
		for(int i = startNum; i < startNum+num ; i++) {
			MyList ml1 = list.get( i );
			s1 = ml1.getLine4();// radition type
			s2 = ml1.getLine2();// yield/decay
			s3 = ml1.getLine3();// energy
			s4 = ml1.getLine1();// ICODE for MIRD

			if(s1.equals( "G" ) || s1.equals( "X" )) {
				//System.out.println("Gamma & X-ray not allowed for MIRD.");
				// TODO add the appropriate handling the X-ray and gammas here
			}
			else if(s1.equals( "B+" ) || s1.equals( "B-" ) || s1.equals( "AQ" ) || s1.equals( "IE" ) || s1.equals( "AE" ) || s1.equals( "A" ) || s1.equals( "AR" ) || s1.equals( "FF" ) || s1.equals( "N" )) {

				xn = Double.parseDouble( s2 );
				en = Double.parseDouble( s3 );
				delta = 2.13 * en * xn;
				en = en * (1.0e3);
				// 4/16/2009 //System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
				if(s1.equals( "A" )) {
					range = alphaRange.getAlphaRange( en, list2 );
					s1 = "Alpha";
				}//if Alpha
				else {
					range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
				}

				//self-dose calculation
				self = new SelfDoseCal( s1, rc, rn, xn, en, delta, range, list2 );

				dcc += self.getDcc( rc, rn, en, delta );
				dcs += self.getDcs( rc, rn, en, delta );
				dnn += self.getDnn( rc, rn, en, delta );
				dncy += self.getDncy( rc, rn, en, delta );
				dns += self.getDns( rc, rn, en, delta );
			}
			else {
				//System.out.println("Unknown radiation type.");
			}

			//Cy <- N , S(Cy<-N)=S(N<-Cy)
			dcyn = dncy;
			//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
			dcycs = (dcs * cellmass - dns * xnuclmass) / cytomass;
			//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
			dcycy = (dcc * cellmass * cellmass) / (cytomass * cytomass) - (2 * dncy * xnuclmass) / cytomass - (dnn * xnuclmass * xnuclmass) / (cytomass * cytomass);

			writeFile.writeSelfToFile( formatter.format( dcc ), formatter.format( dcs ), formatter.format( dnn ), formatter.format( dncy ), formatter.format( dns ), formatter.format( dcyn ), formatter.format( dcycs ), formatter.format( dcycy ), jTextArea1 );
		}
	}

}