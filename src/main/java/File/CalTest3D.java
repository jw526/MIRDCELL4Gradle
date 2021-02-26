package File;

import CrossDoseCal.CrossDoseCal;
import CrossDoseCal.Newton;
import Energy.Energy;
import SelfDoseCal.SelfDose;
import CrossDoseCal.RangeCalc;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.*;

/**
 * @author Behrooz
 */
public class CalTest3D {

	ArrayListIn2 ali;
	MyList ml = new MyList( "" );
	MyList2 mlist2 = new MyList2( "" );
	ArrayList<MyList> list;
	ArrayList<MyList2> list2;
	ArrayList list3 = new ArrayList();
	CrossDoseCal cross;
	SelfDose self;
	Newton newton;
	//AlphaRange alphaRange = new AlphaRange();
	String alphaFileName = "alph-water2";
	AlphaRange alphaRange = new AlphaRange();
	WriteToOutputFile writeFile = new WriteToOutputFile();
	String s1 = "", s2 = "", s3 = "", s4 = "";
	double rc = 0, rn = 0, en = 0, xn = 0;
	String iso = "";
	double range = 0;
	double delta;
	NumberFormat formatter = new DecimalFormat( "0.00E00" );
	double cellmass = 0, xnuclmass = 0, cytomass = 0;
	//updated 6/21/09
	//ArrayList doseList11 = new ArrayList();
	ArrayList doseList11, doseList22, doseList33, doseList44, doseList55;
	ArrayList<ArrayList> doseArrayList1, doseArrayList2, doseArrayList3, doseArrayList4, doseArrayList5;
	String[] doseList1 = {};
	//ArrayList sumList = new ArrayList();
	ArrayList sumList, sumList1, sumList2, sumList3, sumList4, sumList5, sumList6;
	ArrayListSum listSum, listSum1, listSum2, listSum3, listSum4, listSum5;

	public CalTest3D() {
	}

	//updated 08/04/09
	//dose cal for mono
	public void doseCalMono(String s1, Double en, Double xn, double rc, double rn, String iso, JTextArea jTextArea1, JPanel jPanel14, int MaxDist) throws IOException {
		//exception error?????????
		//0701
		Graphics progress = jPanel14.getGraphics();
		int width = jPanel14.getWidth();
		int height = jPanel14.getHeight();
		progress.clearRect( 0, 0, width, height );
		progress.setColor( Color.BLACK );
		progress.drawString( "Part(1/2) : 0%", width / 2 - 50, height / 2 + 6 );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, 5, height );
		ali = new ArrayListIn2( iso );

		list = new ArrayList<MyList>();
		list2 = new ArrayList<MyList2>();
		newton = new Newton();

		String directory = "";
		//07/01
		//writeFile.wirteToFile();

		try {

			//list.addAll(ali.read()); //disabled 07/22/10
			//System.out.println("list = "+list+"\n"+list.size());
			int num = 1;

			/**
			 * ******************************************
			 * Self-Dose Calculation Using Alpha-water data
			 * *******************************************
			 */
			list2 = ali.readAlpha( alphaFileName );
			//System.out.println("alpha file list2 = "+list2); //071510
			self = new SelfDose( list );
			self.getSelfMono( s1, en, xn, num, rc, rn, jTextArea1, list2 );
			//System.out.println(list);
			//07/01
			//self = new SelfDose(s1, en, num, rc, rn, directory, list2);

			//self.getSelfMono(s1, en, num, rc, rn, directory, list2);


			/**
			 * *********************************************
			 * Cross-Dose Calculation Alpha particles Electron
			 * *********************************************
			 */
			String crossDose0 = "0.00E+00";
			for(int k = 0; k < (2 * (int) rc - 2); k++) {
				writeFile.writeInBetweenToFile( crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, jTextArea1 );
				//jTextArea1.append("             "+ crossDose0+"     "+crossDose0+"     "+crossDose0+"     "+crossDose0+"     "+crossDose0+"     " + "\n");//0701
			}

			for(int i = 6; i < num + 6; i++) {

				/**
				 * if ( s1="GAMMA" or "KXALPH" or "KXBETA" or "LXALPH" or
				 * "LXBETA" or "MXALPH" or "NXALPH") "Gamma & X-ray not
				 * allowed." delete row element from ArrayList out: while() {
				 * break out; }
				 */
				if(s1.contains( "Electron" ) || s1.contains( "Alpha" )) {
					//xn = 1; // 11/17/2010
					delta = xn * en * 2.13;
					en = en * (1.0e3);

					// 4/16/2009 System.out.println("xn="+xn+"  EnKeV="+en+"  delta="+delta);
					if(s1.contains( "Alpha" )) {
						/**
						 * ******
						 * 07/22/2010 !!!!!!!!!!!!!! add pop-up message telling
						 * users Absorbed Source: water
						 */
						range = alphaRange.getAlphaRange( en, list2 );
					}//if Alpha
					else {
						range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
					}
					//System.out.println("range for corss-dose ="+range);

					//cross dose calculation
					cross = new CrossDoseCal( rc, rn, xn, en, delta, range, list2, jTextArea1, MaxDist );
					cross.getCrossDose( s1, num, directory );

					//get cross.doseList1
				}
				else {
					System.out.println( "Unknown radiation type." );
				}
				/**
				 * 03/20/2012 launch new thread for progress bar, be aware of
				 * Swing EventDispatchThread
				 */
				progress.clearRect( 0, 0, width, height );
				progress.setColor( Color.GREEN );
				double percent = ((i - 6.0) / num) * width;
				progress.fillRect( 1, 1, (int) percent, 28 );
				progress.setColor( Color.BLACK );

				progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", percent / width * 100 ) + "%", width / 2 - 50, height / 2 + 6 );

			}//end loop i radiation type

			//updated 06/21/09
			/**
			 * doseList11 = cross.getCrossDose1(s1, num, directory); doseList22
			 * = cross.getCrossDose2(s1, num, directory); doseList33 =
			 * cross.getCrossDose2(s1, num, directory); doseList44 =
			 * cross.getCrossDose2(s1, num, directory); doseList55 =
			 * cross.getCrossDose2(s1, num, directory); System.out.println("Cell
			 * <-- Cell " + doseList11 +"\n" + "Cell <-- CellSurf " + doseList22
			 * + "\n" + "Nuc <-- Nuc " + doseList33 + "\n"+ "Nuc <-- Cyto " +
			 * doseList44 + "\n" + "Nuc <-- CellSurf " + doseList55 );
			 */
			/**
			 *
			 * get cross.doseList output for 2-D array doseList[i][j]
			 *
			 * for(j=0) for(i=0) sum += doseList[i][j]
			 *
			 * /
			 */
		} catch(IOException ioe) {
			System.out.println( "e:" + ioe );
		}


	}//end method doseCalMono

	public void doseCalUser(double rc, double rn, ArrayList list3, JTextArea jTextArea1, JPanel jPanel14, int MaxDist) throws IOException {
		Graphics progress = jPanel14.getGraphics();
		int width = jPanel14.getWidth();
		int height = jPanel14.getHeight();
		progress.clearRect( 0, 0, width, height );
		progress.setColor( Color.BLACK );
		progress.drawString( "Part(1/2) : 0%", width / 2 - 50, height / 2 + 6 );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, 5, height );

		this.list3 = list3; //10/26/2010
		ali = new ArrayListIn2( iso );
		list = new ArrayList<MyList>();
		list2 = new ArrayList<MyList2>();
		newton = new Newton();
		//String directory = writeFile.createOutputFile(iso, rc, rn);
		String directory = "";
		//writeFile.wirteToFile();
		// 10/27/2010
		Double dose7 = 0.0, dose8 = 0.0;
		doseList11 = new ArrayList();
		doseArrayList1 = new ArrayList<ArrayList>();
		sumList = new ArrayList();

		doseList22 = new ArrayList();
		doseList33 = new ArrayList();
		doseList44 = new ArrayList();
		doseList55 = new ArrayList();

		doseArrayList2 = new ArrayList<ArrayList>();
		doseArrayList3 = new ArrayList<ArrayList>();
		doseArrayList4 = new ArrayList<ArrayList>();
		doseArrayList5 = new ArrayList<ArrayList>();

		sumList1 = new ArrayList();
		sumList2 = new ArrayList();
		sumList3 = new ArrayList();
		sumList4 = new ArrayList();
		sumList5 = new ArrayList();

		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;


		try {
			//list.addAll(ali.readUser()); //08/05/09 read from new dir: /user/ // 10/26/2010 disabled
			//System.out.println("10/26/2010 when compute list3 size = "+list3+"  "+list3.size());
			//MyList mlGetNum = list.get(5);
			int start = list3.indexOf( "START RADIATION RECORDS" );
			int end = list3.indexOf( "END RADIATION RECORDS" );
			System.out.print( "11/01/2010 start line num=" + start + "\n" );

			int num = end - start - 1;
			System.out.print( "10/29/2010 when compute user's num=" + num + "\n" );

			/**
			 * Self-Dose Calculation Using Alpha-water data
			 */
			list2 = ali.readAlpha( alphaFileName );
			self = new SelfDose( list3 );
			//System.out.println(list);
			self.getSelfUser( start, num, rc, rn, jTextArea1, list2, list3 ); //071510
			//self.getSelf(num, rc, rn, directory, list2);

			String crossDose0 = "0.00E+00";
			for(int k = 0; k < (2 * (int) rc - 2); k++) {
				writeFile.writeInBetweenToFile( crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, jTextArea1 );
			}
			/**
			 * Cross-Dose Calculation Alpha particles Electron
			 */
			for(int i = start + 1; i < num + start + 1; i++) {

				s1 = list3.get( i ).toString().trim().split( "\\s+" )[3]; // radiation type
				s2 = list3.get( i ).toString().trim().split( "\\s+" )[0]; // icode
				s3 = list3.get( i ).toString().trim().split( "\\s+" )[1]; // yield/decay
				s4 = list3.get( i ).toString().trim().split( "\\s+" )[2]; // energy
				//s5 = list3.get(i).toString().trim().split("\\s+")[4]; // delta
				if(s1.equals( "G" ) || s1.equals( "X" )|| s1.equals( "AQ" ) || s1.equals( "AR" ) || s1.equals( "FF" ) || s1.equals( "N" )) {
					System.out.println( "Gamma-ray,  X-ray, Annihilation Quanta, Alpha Recoil, Fission Fragment, && Neutrons not allowed." );
					continue;
				}
				else if(s1.equals( "B+" ) || s1.equals( "B-" ) /*|| s1.equals( "AQ" ) */|| s1.equals( "IE" ) || s1.equals( "AE" ) || s1.equals( "A" ) /*|| s1.equals( "AR" ) || s1.equals( "FF" ) || s1.equals( "N" )*/) {
					//System.out.print("i="+i+" ");

					xn = Double.parseDouble( s3 );
					en = Double.parseDouble( s4 );
					delta = xn * en * 2.13;
					en = en * (1.0e3);

					System.out.println( "10/26/2010 xn=" + xn + " en=" + en );
					if(s1.equals( "A" )) {
						//alphaRange = new AlphaRange(alphaFileName);
						range = alphaRange.getAlphaRange( en, list2 );
					}//if Alpha
					else {
						range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
					}
					//System.out.println("range for corss-dose ="+range);

					//cross dose calculation
					cross = new CrossDoseCal( rc, rn, xn, en, delta, range, list2, jTextArea1, MaxDist );

					doseList11 = cross.getCrossDose1( s1, num, directory ); // new cross-dose 10/13/2010
					doseList22 = cross.getCrossDose2( s1, num, directory );
					doseList33 = cross.getCrossDose3( s1, num, directory );
					doseList44 = cross.getCrossDose4( s1, num, directory );
					doseList55 = cross.getCrossDose5( s1, num, directory );
					//System.out.println(doseList11);
					/**
					 * 03/20/2012 launch new thread for progress bar, be aware
					 * of Swing EventDispatchThread
					 */
					//System.out.println("03/20/2012 i = "+ i);
					progress.clearRect( 0, 0, width, height );
					progress.setColor( Color.GREEN );
					double percent = ((i - start - 1) / (double) num) * width;
					progress.fillRect( 1, 1, (int) percent, 28 );
					progress.setColor( Color.BLACK );
					progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", percent / width * 100 ) + "%", width / 2 - 50, height / 2 + 6 );

				}//end else if
				else {
					System.out.println( "Unknown radiation type." );
				}
				doseArrayList1.add( doseList11 );
				doseArrayList2.add( doseList22 );
				doseArrayList3.add( doseList33 );
				doseArrayList4.add( doseList44 );
				doseArrayList5.add( doseList55 );
				//System.out.println("10/17/2010 doseArraylist1 of string[j]"+doseArrayList1);
			}//end loop i radiation types
			/**
			 * ********************************************
			 * 10/17/2010 new loop for sum, j, i
			 * *******************************************
			 */
			//System.out.println("10/17/2010 doseArraylist1 = "+doseArrayList1);
			listSum1 = new ArrayListSum( doseArrayList1 );
			listSum2 = new ArrayListSum( doseArrayList2 );
			listSum3 = new ArrayListSum( doseArrayList3 );
			listSum4 = new ArrayListSum( doseArrayList4 );
			listSum5 = new ArrayListSum( doseArrayList5 );

			sumList1 = listSum1.ListSum();
			sumList2 = listSum2.ListSum();
			sumList3 = listSum3.ListSum();
			sumList4 = listSum4.ListSum();
			sumList5 = listSum5.ListSum();

			/**
			 * *******
			 * 10/27/2010 Cy <- N , S(Cy<-N)=S(N<-Cy) dose6 = dose4;
			 */
			sumList6 = sumList4;
			//output to jTextArea1
			//for loop j distance
			//writeTo(listSum1.get(j)+listSum2.get(j)+listSum3.get(j)+listSum4.get(j)+listSum5.get(j)"\n");
			//for(int m = 2*(int)rc ; m<= ( listSum1.maxSize()+2*(int)rc ); m++){
			int n = listSum1.maxSize();
			//System.out.println("10/21/2010 n = "+n);

			for(int m = 0; m < n; m++) {
				//System.out.println("10/21/2010 m = "+m);
				//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
				dose7 = (Double.parseDouble( sumList2.get( m ).toString() ) * cellmass - Double.parseDouble( sumList5.get( m ).toString() ) * xnuclmass) / cytomass;
				//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
				dose8 = (Double.parseDouble( sumList1.get( m ).toString() ) * cellmass * cellmass) / (cytomass * cytomass) - (2 * Double.parseDouble( sumList4.get( m ).toString() ) * xnuclmass) / cytomass - (Double.parseDouble( sumList3.get( m ).toString() ) * xnuclmass * xnuclmass) / (cytomass * cytomass);
				jTextArea1.append( String.valueOf( m + 2 * (int) rc ) + "       " + "   " + formatter.format( sumList1.get( m ) ) + "     " + formatter.format( sumList2.get( m ) ) + "     " + formatter.format( sumList3.get( m ) ) + "     " + formatter.format( sumList4.get( m ) ) + "     " + formatter.format( sumList5.get( m ) ) + "     " + formatter.format( sumList6.get( m ) ) + "     " + formatter.format( dose7 ) + "     " + formatter.format( dose8 ) + "\n" );
				//jTextArea1.append(String.valueOf(m+2*(int)rc )+"       "+"   "+ sumList1.get(m)+"\n");
			}//0701 revised by 10/27/2010

		} catch(Exception ioe) {
			System.out.println( "e:" + ioe );
		}


	}//end method doseCalUser

	public void doseCalMIRD(double rc, double rn, String iso, JTextArea jTextArea1, JPanel jPanel14, int MaxDist) throws IOException {
		//beta full energy spectrum
		Graphics progress = jPanel14.getGraphics();
		int width = jPanel14.getWidth();
		int height = jPanel14.getHeight();
		progress.clearRect( 0, 0, width, height );
		progress.setColor( Color.BLACK );
		progress.drawString( "Part(1/2) : 0%", width / 2 - 50, height / 2 + 6 );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, 5, height );

		ali = new ArrayListIn2( iso );
		list = new ArrayList<MyList>();
		list2 = new ArrayList<MyList2>();
		newton = new Newton();
		//String directory = writeFile.createOutputFile(iso, rc, rn);
		String directory = "";
		//writeFile.wirteToFile();
		// 10/27/2010
		Double dose7 = 0.0, dose8 = 0.0;
		doseList11 = new ArrayList();
		doseArrayList1 = new ArrayList<ArrayList>();

		doseList22 = new ArrayList();
		doseList33 = new ArrayList();
		doseList44 = new ArrayList();
		doseList55 = new ArrayList();

		doseArrayList2 = new ArrayList<ArrayList>();
		doseArrayList3 = new ArrayList<ArrayList>();
		doseArrayList4 = new ArrayList<ArrayList>();
		doseArrayList5 = new ArrayList<ArrayList>();

		sumList = new ArrayList();
		sumList1 = new ArrayList();
		sumList2 = new ArrayList();
		sumList3 = new ArrayList();
		sumList4 = new ArrayList();
		sumList5 = new ArrayList();

		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;


		try {
			list = ali.readMIRD();  //read MIRD
			//System.out.println("list size 07152010 = "+ list.size());
			MyList mlNum = list.get( 1 );
			//System.out.println("read in list Num 071510 = "+ list.get(5));
			final int num = Integer.parseInt( mlNum.getLine3() );
			int startNum = list.size() - num - 1;
			System.out.print( "10/29/2010 first index num=" + startNum + "\n" );
			System.out.print( "10/29/2010 when compute MIRD's num=" + num + "\n" );

			/**
			 * Self-Dose Calculation for MIRD Using Alpha-water data
			 */
			list2 = ali.readAlpha( alphaFileName );
			self = new SelfDose( list );
			//System.out.println(list);
			self.getSelfMIRD( num, rc, rn, jTextArea1, list2 ); //10/29/10

			String crossDose0 = "0.00E+00";
			for(int k = 0; k < (2 * (int) rc - 2); k++) {
				writeFile.writeInBetweenToFile( crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, jTextArea1 );
			}

			/**
			 * Cross-Dose Calculation for MIRD Alpha particles Electron ICODE
			 * Mnemonic for ICODE Description
			 * 1 G PG- prompt DG-delayed Gamma rays
			 * 2 X x-rays
			 * 3 AQ Annihilation quanta
			 * 4 B+ Beta + particles
			 * 5 BD- delayed Beta- particles
			 * 6 IE Internal conversion Electrons
			 * 7 AE Auger electrons
			 * 8 A Alpha particles
			 * 9 AR Daughter Recoil (Alpha decay)
			 * 10 FF Fission fragments
			 * 11 N Neutrons *Prompt and
			 * delayed radiations of spontaneous fission.
			 */

			for(int i = startNum; i < num + startNum; i++) {
				MyList ml1 = list.get( i );
				s1 = ml1.getLine4();// radition type
				s2 = ml1.getLine2();// yield/decay
				s3 = ml1.getLine3();// energy
				s4 = ml1.getLine1();// ICODE for MIRD
				//System.out.println("10/29/10 s1 MIRD = " + s1);

				// updated 8/1/2016 (Alex Rosen)
				if(s1.equals( "AR" ) || s1.equals( "FF" ) || s1.equals( "N" ) || s1.equals( "G" ) || s1.equals( "X" ) || s1.equals( "AQ" )) {
					// TODO fix this for the other 2 dosing methods as well.  find out why theyre differently named because its a little ridicolous
					System.out.println( "Daughter Recoil, Fission Fragment, & Neutrons not processed in this application." );
					System.out.println( s1 );
					continue;
				}
				/*else if() {
					// TODO put in code from "FRAC" from files that dr howell sent
				}*/
				else if(s1.equals( "B+" ) || s1.equals( "B-" ) /*|| s1.equals( "AQ" ) */ || s1.equals( "IE" ) || s1.equals( "AE" ) || s1.equals( "A" ) /*|| s1.equals( "AR" ) || s1.equals( "FF" ) || s1.equals( "N" )*/) {
					xn = Double.parseDouble( s2 );
					en = Double.parseDouble( s3 );
					delta = 2.13 * en * xn;
					en = en * (1.0e3);

					//System.out.println("10/26/2010 xn=" + xn + " en=" + en);
					if(s1.equals( "A" )) {
						//if Alpha
						range = alphaRange.getAlphaRange( en, list2 );
						System.out.println("calTest3d Range = " + range);
					}
					else {
						// if not alpha
						// TODO this needs to be expanded to cover all the other electron types more specifically
						range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
					}

					//cross dose calculation
					// TODO calculate the Cross Dose all in the constructor for crossDoseCal then just have getters for doseList11-55
					cross = new CrossDoseCal( rc, rn, xn, en, delta, range, list2, jTextArea1, MaxDist );
					doseList11 = cross.getCrossDose1( s1, num, directory ); // new cross-dose 10/13/2010
					doseList22 = cross.getCrossDose2( s1, num, directory );
					doseList33 = cross.getCrossDose3( s1, num, directory );
					doseList44 = cross.getCrossDose4( s1, num, directory );
					doseList55 = cross.getCrossDose5( s1, num, directory );
				}//end else if
				else {
					System.out.println( "Unknown radiation type." );
				}
				doseArrayList1.add( doseList11 );
				doseArrayList2.add( doseList22 );
				doseArrayList3.add( doseList33 );
				doseArrayList4.add( doseList44 );
				doseArrayList5.add( doseList55 );
				//System.out.println("10/17/2010 doseArraylist1 of string[j]"+doseArrayList1);
				/**
				 * 03/20/2012 launch new thread for progress bar, be aware of
				 * Swing EventDispatchThread
				 */

				progress.clearRect( 0, 0, width, height );
				progress.setColor( Color.GREEN );
				double percent = ((i - startNum) / (double) num) * width;
				//System.out.println("03/20/2012 i = "+ i +"aaa"+percent);
				progress.fillRect( 1, 1, (int) percent, 28 );
				progress.setColor( Color.BLACK );
				progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", percent / width * 100 ) + "%", width / 2 - 50, height / 2 + 6 );

			}//end loop i radiation types
			/**
			 * ********************************************
			 * 10/17/2010 new loop for sum, j, i
			 * *******************************************
			 */
			//System.out.println("10/17/2010 doseArraylist1 = "+doseArrayList1);
			listSum1 = new ArrayListSum( doseArrayList1 );
			listSum2 = new ArrayListSum( doseArrayList2 );
			listSum3 = new ArrayListSum( doseArrayList3 );
			listSum4 = new ArrayListSum( doseArrayList4 );
			listSum5 = new ArrayListSum( doseArrayList5 );

			sumList1 = listSum1.ListSum();
			sumList2 = listSum2.ListSum();
			sumList3 = listSum3.ListSum();
			sumList4 = listSum4.ListSum();
			sumList5 = listSum5.ListSum();

			/**
			 * 10/27/2010 Cy <- N , S(Cy<-N)=S(N<-Cy) dose6 = dose4;
			 */

			sumList6 = sumList4;
			int n = listSum1.maxSize();
			//System.out.println("10/21/2010 n = "+n);

			for(int m = 0; m < n; m++) {
				//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
				dose7 = (Double.parseDouble( sumList2.get( m ).toString() ) * cellmass - Double.parseDouble( sumList5.get( m ).toString() ) * xnuclmass) / cytomass;

				//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
				dose8 = (Double.parseDouble( sumList1.get( m ).toString() ) * cellmass * cellmass) / (cytomass * cytomass) - (2 * Double.parseDouble( sumList4.get( m ).toString() ) * xnuclmass) / cytomass - (Double.parseDouble( sumList3.get( m ).toString() ) * xnuclmass * xnuclmass) / (cytomass * cytomass);

				jTextArea1.append( String.valueOf( m + 2 * (int) rc ) + "       " + "   " + formatter.format( sumList1.get( m ) ) + "     " + formatter.format( sumList2.get( m ) ) + "     " + formatter.format( sumList3.get( m ) ) + "     " + formatter.format( sumList4.get( m ) ) + "     " + formatter.format( sumList5.get( m ) ) + "     " + formatter.format( sumList6.get( m ) ) + "     " + formatter.format( dose7 ) + "     " + formatter.format( dose8 ) + "\n" );
			}
		} catch(Exception ioe) {
			System.out.println( "e:" + ioe );
		}
	}//end method doseCalMIRD

	public void doseCal(double rc, double rn, String iso, JTextArea jTextArea1, JPanel jPanel14, int MaxDist) throws IOException {

		Graphics progress = jPanel14.getGraphics();
		int width = jPanel14.getWidth();
		int height = jPanel14.getHeight();
		progress.clearRect( 0, 0, width, height );
		progress.setColor( Color.BLACK );
		progress.drawString( "Part(1/2) : 0%", width / 2 - 50, height / 2 + 6 );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, 5, height );


		ali = new ArrayListIn2( iso );

		list = new ArrayList<MyList>();
		list2 = new ArrayList<MyList2>();
		newton = new Newton();
		String directory = "";

		// 10/27/2010
		Double dose7 = 0.0, dose8 = 0.0;
		cellmass = 4.0 * Math.PI * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * Math.PI * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;

		doseList11 = new ArrayList();
		doseArrayList1 = new ArrayList<ArrayList>();
		sumList = new ArrayList();

		doseList22 = new ArrayList();
		doseList33 = new ArrayList();
		doseList44 = new ArrayList();
		doseList55 = new ArrayList();

		doseArrayList2 = new ArrayList<ArrayList>();
		doseArrayList3 = new ArrayList<ArrayList>();
		doseArrayList4 = new ArrayList<ArrayList>();
		doseArrayList5 = new ArrayList<ArrayList>();

		sumList1 = new ArrayList();
		sumList2 = new ArrayList();
		sumList3 = new ArrayList();
		sumList4 = new ArrayList();
		sumList5 = new ArrayList();


		try {

			//04/01
			//URL directory = writeFile.createOutputFile(iso, rc, rn);
			//System.out.println("0401 created output url: "+ directory);
			//writeFile.wirteToFile();


			//list.addAll(ali.read());
			list = ali.read();
			//System.out.println("list size 07152010 = "+ list.size());
			MyList mlNum = list.get( 5 );
			//System.out.println("read in list Num 071510 = "+ list.get(5));
			final int num = Integer.parseInt( mlNum.getLine1() );
			//04/01 error?? list= [], num = 0
			System.out.print( "num=" + num + "\n" );

			/**
			 * Self-Dose Calculation Using Alpha-water data
			 */
			list2 = ali.readAlpha( alphaFileName );
			//System.out.println("0702 alpha file size: "+list2.size()); //0702 test
			self = new SelfDose( list );
			//System.out.println(list);
			self.getSelf( num, rc, rn, jTextArea1, list2 ); //071510
			//self.getSelf(num, rc, rn, directory, list2);
			/**
			 * 03/20/2012
			 *
			 * Thread t = new Thread( new Runnable() { public void run() {
			 * jProgressBar5.setIndeterminate(true);
			 *
			 * }
			 * }); t.start();
			 *
			 */
			String crossDose0 = "0.00E+00";
			for(int k = 0; k < (2 * (int) rc - 2); k++) {
				writeFile.writeInBetweenToFile( crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, jTextArea1 );
			}
			int maxabs = 1500;
			Double[] doseArray1 = new Double[maxabs];
			System.out.println( "array size=" + doseArray1.length );

			/**
			 * Cross-Dose Calculation Alpha particles Electron
			 */
			for(int i = 6; i < num + 6; i++) {

				//int maxloop = num; // 03/20/2012
				MyList ml1 = list.get( i );
				/**
				 * if ( s1="GAMMA" or "KXALPH" or "KXBETA" or "LXALPH" or
				 * "LXBETA" or "MXALPH" or "NXALPH") "Gamma & X-ray not
				 * allowed." delete row element from ArrayList out: while() {
				 * break out; }
				 */
				s1 = ml1.getLine1();
				s2 = ml1.getLine2();
				s3 = ml1.getLine3();
				s4 = ml1.getLine4();

				/*int ICODE = Integer.parseInt( s2 );
				xn = Double.parseDouble( s3 );
				en = Double.parseDouble( s4 );
				delta = xn * en * 2.13;
				en = en * (1.0e3);*/
				//s5 = ml1.getLine5();

				/*
				if(ICODE == 1 || ICODE == 2 || ICODE == 3) {
					// X-ray, Gamma Ray, Annihilation Quanta
				}
				else if(ICODE == 4 || ICODE == 5 || ICODE == 6 || ICODE == 7) {
					// Beta +/-, Internal Conversion, Auger
				}
				else if(ICODE == 8) {
					// Alphas
					range = alphaRange.getAlphaRange( en, list2 );
				}
				else if(ICODE == 9 || ICODE == 10 || ICODE == 11) {
					// Daughter recoil, Fission Fragment, Neutrons
					System.out.println( "Daughter Recoil, Fission Fragmentation, & Neutrons are ignored in this model." );
					//continue
				}
				else {
					System.out.println( "Unknown Radiation Type:  ICODE=" + ICODE );
				}
				*/

				// TODO replace the below with the ICODE values rather than the names
				if(s1.equals( "GAMMA" ) || s1.equals( "KXALPH" ) || s1.equals( "KXBETA" ) || s1.equals( "LXALPH" ) || s1.equals( "LXBETA" ) || s1.equals( "MXALPH" ) || s1.equals( "NXALPH" )) {
					System.out.println( "Gamma & X-ray not allowed." );
					continue;
				}
				else if(s1.equals( "BETA" ) || s1.equals( "KCONV" ) || s1.equals( "LCONV" ) || s1.equals( "MCONV" ) || s1.equals( "KLXAUG" ) || s1.equals( "LMMAUG" ) || s1.equals( "MXYAUG" ) || s1.equals( "LLXCK" ) || s1.equals( "KLLAUG" ) || s1.equals( "CONV" ) || s1.equals( "LMXAUG" ) || s1.equals( "LXYAUG" ) || s1.equals( "KXYAUG" ) || s1.equals( "MMXCK" ) || s1.equals( "NNXCK" ) || s1.equals( "NXYAUG" ) || s1.equals( "OOXCK" ) || s1.equals( "ALPHA" ) || s1.equals( "NCONV" ) || s1.equals( "OCONV" )) {
					//System.out.print("i="+i+" ");

					xn = Double.parseDouble( s3 );
					en = Double.parseDouble( s4 );
					delta = xn * en * 2.13;
					en = en * (1.0e3);
					//System.out.println("xn="+xn+" en="+en);
					if(s1.equals( "ALPHA" )) {
						//if Alpha
						// alphaRange = new AlphaRange(alphaFileName);
						range = alphaRange.getAlphaRange( en, list2 );
					}
					else {
						range = newton.getRange( en );//implement Newton.java, using e0 get range R, get enkev, got best range in the end
					}
					//System.out.println("range for cross-dose ="+range);

					//cross dose calculation
					cross = new CrossDoseCal( rc, rn, xn, en, delta, range, list2, jTextArea1, MaxDist );
					//cross.getCrossDose(s1, num, directory);
					/**
					 * *************
					 * cross-dose arraylist 10/13/2010 need to define
					 * 2-dimensional ArrayList doseList[i, j] to store each
					 * doseList d, then make sum
					 * doseList[1,1]+doseList[2,1]+.......+doseList[i,1] .....
					 * ... doseList[1,j]+doseList[2,j]+........+doseList[i,j]
					 * *************
					 */
					doseList11 = cross.getCrossDose1( s1, num, directory ); // new cross-dose 10/13/2010
					doseList22 = cross.getCrossDose2( s1, num, directory );
					doseList33 = cross.getCrossDose3( s1, num, directory );
					doseList44 = cross.getCrossDose4( s1, num, directory );
					doseList55 = cross.getCrossDose5( s1, num, directory );
					//System.out.println(doseList11);
					//doseList1 = (String[]) doseList11.toArray(new String[doseList11.size()]);
					//System.out.println("10/17/2010 list to String[]: "+ doseList1);
					//get cross.doseList1
					/**
					 * **************
					 * test on doseList[i].get(j)++
					 *
					 * doseList1(i).get(1)++
					 *
					 * return ArrayListDose(i,j) **************
					 */
				}//end else if
				else {
					System.out.println( "Unknown radiation type." );
				}
				doseArrayList1.add( doseList11 );
				doseArrayList2.add( doseList22 );
				doseArrayList3.add( doseList33 );
				doseArrayList4.add( doseList44 );
				doseArrayList5.add( doseList55 );
				//System.out.println("10/17/2010 doseArraylist1 of string[j]"+doseArrayList1);


				/**
				 * 03/20/2012 launch new thread for progress bar, be aware of
				 * Swing EventDispatchThread
				 */
				//System.out.println("03/20/2012 i = "+ i);
				progress.clearRect( 0, 0, width, height );
				progress.setColor( Color.GREEN );
				double percent = ((i - 6.0) / num) * width;
				progress.fillRect( 1, 1, (int) percent, 28 );
				progress.setColor( Color.BLACK );
				progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", percent / width * 100 ) + "%", width / 2 - 50, height / 2 + 6 );


				//System.out.println("03/20/2012 percent = "+ percent);

			}//end loop i radiation types
			/**
			 * ********************************************
			 * 10/17/2010 new loop for sum, j, i
			 * *******************************************
			 */
			//System.out.println("10/17/2010 doseArraylist1 = "+doseArrayList1);
			listSum1 = new ArrayListSum( doseArrayList1 );
			listSum2 = new ArrayListSum( doseArrayList2 );
			listSum3 = new ArrayListSum( doseArrayList3 );
			listSum4 = new ArrayListSum( doseArrayList4 );
			listSum5 = new ArrayListSum( doseArrayList5 );
			sumList1 = listSum1.ListSum();
			sumList2 = listSum2.ListSum();
			sumList3 = listSum3.ListSum();
			sumList4 = listSum4.ListSum();
			sumList5 = listSum5.ListSum();
			//output to jTextArea1
			//for loop j distance
			//writeTo(listSum1.get(j)+listSum2.get(j)+listSum3.get(j)+listSum4.get(j)+listSum5.get(j)"\n");
			//for(int m = 2*(int)rc ; m<= ( listSum1.maxSize()+2*(int)rc ); m++){
			int n = listSum1.maxSize();
			//System.out.println("10/21/2010 n = "+n);
			sumList6 = sumList4;

			for(int m = 0; m < n; m++) {
				//System.out.println("10/21/2010 m = "+m);
				//Cy <- Cell Surface, S(Cy<-CS)= (S(C<-CS)*cellmass - S(N<-CS)*Nucmass)/Cytomass
				dose7 = (Double.parseDouble( sumList2.get( m ).toString() ) * cellmass - Double.parseDouble( sumList5.get( m ).toString() ) * xnuclmass) / cytomass;
				//Cy <- Cy, S(Cy<-Cy)= S(C<-C)*cellmass*cellmass/cytomass*cytomass - 2* S(N<-Cy)*xnuclmass/cytomass - S(N<-N)*xnuclmass*xnuclmass/cytomass*cytomass
				dose8 = (Double.parseDouble( sumList1.get( m ).toString() ) * cellmass * cellmass) / (cytomass * cytomass) - (2 * Double.parseDouble( sumList4.get( m ).toString() ) * xnuclmass) / cytomass - (Double.parseDouble( sumList3.get( m ).toString() ) * xnuclmass * xnuclmass) / (cytomass * cytomass);
				jTextArea1.append( String.valueOf( m + 2 * (int) rc ) + "       " + "   " + formatter.format( sumList1.get( m ) ) + "     " + formatter.format( sumList2.get( m ) ) + "     " + formatter.format( sumList3.get( m ) ) + "     " + formatter.format( sumList4.get( m ) ) + "     " + formatter.format( sumList5.get( m ) ) + "     " + formatter.format( sumList6.get( m ) ) + "     " + formatter.format( dose7 ) + "     " + formatter.format( dose8 ) + "\n" );
				//jTextArea1.append(String.valueOf(m+2*(int)rc )+"       "+"   "+ sumList1.get(m)+"\n");
			}//0701 revised by 10/27/2010
			//System.out.println("10/17/2010 doseArrayList ss length = "+ss.length);
			//updated 06/21/09
			/**
			 * doseList11 = cross.getCrossDose1(s1, num, directory); doseList22
			 * = cross.getCrossDose2(s1, num, directory); doseList33 =
			 * cross.getCrossDose2(s1, num, directory); doseList44 =
			 * cross.getCrossDose2(s1, num, directory); doseList55 =
			 * cross.getCrossDose2(s1, num, directory); System.out.println("Cell
			 * <-- Cell " + doseList11 +"\n" + "Cell <-- CellSurf " + doseList22
			 * + "\n" + "Nuc <-- Nuc " + doseList33 + "\n"+ "Nuc <-- Cyto " +
			 * doseList44 + "\n" + "Nuc <-- CellSurf " + doseList55 );
			 */
		} catch(IOException ioe) {
			System.out.println( "e:" + ioe );
		}


	}//end method doseCal

}