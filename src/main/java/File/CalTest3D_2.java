package File;

import Energy.Energy;
import SelfDoseCal.SelfDose_2;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by ar548 on 8/18/2016.
 */
public class CalTest3D_2 {
    
    WriteToOutputFile writeFile = new WriteToOutputFile();
    
	public CalTest3D_2(){}

	public double[][][] calcDoses(double rc, double rn, String iso, String locale, String dataType, JTextArea jTextArea1, JPanel jPanel14, int MaxDist, double[] ranges/*, ArrayList<double[]> radiations*/) {

		// TODO read in the data outside this file and pass it here.  there is no reason for it to be done here

		// TODO move the self dose outside this file, it doesnt need to be here
		//SelfDose_2 selfDose_2 = new SelfDose_2();
		//double[][] selfDose = selfDose_2.calcSelfDose( rc, rn, data, jTextArea1 );

		System.out.println("entering calcDoses");
		double[][][] doses = new double[12][ranges.length][8];
		for(int i = 0; i < 11; i++) {
			for(int j = 0; j < ranges.length; j++) {
				for(int k = 0; k < 8; k++) {
					doses[i][j][k] = 0;
				}
			}
		}

		double range;

		/**
		 * @param rc = radius of the cell;
		 * @param rn = radius of the nucleus;
		 * @param iso = a string of the isotope name/file that the isotope data is contained in
		 * @param locale = the area where the dose is being delivered
		 * @param dataType = a string representing which dataset type you are reading in can be {"USER" || "BAVG" || "BFULL"}
		 * @param MaxDist = maximum distance achievable in the cluster
		 * @param numRadii = the number of possible ranges for the surviving fraction calculation
		 * @param ranges = an ordered list of all the possible distances for dosing
		 * @param radiations = the list of the radioactive decays with e0 and yield
		 */

		/**
		 * Cross-Dose Calculation for MIRD Alpha particles Electron ICODE
		 * Mnemonic for ICODE Description
		 * ICODE = 1 G PG- prompt DG-delayed Gamma rays
		 * ICODE = 2 X x-rays
		 * ICODE = 3 AQ Annihilation quanta
		 * ICODE = 4 B+ Beta + particles
		 * ICODE = 5 BD- delayed Beta- particles
		 * ICODE = 6 IE Internal conversion Electrons
		 * ICODE = 7 AE Auger electrons
		 * ICODE = 8 A Alpha particles
		 * ICODE = 9 AR Daughter Recoil (Alpha decay)
		 * ICODE = 10 FF Fission fragments
		 * ICODE = 11 N Neutrons *Prompt and delayed radiations of spontaneous fission.
		 */

		/**
		 * iterate over each isotope in the list
		 ***** generate a chart of energy deposited per distance to a specific locale for that isotope with that energy
		 ***** update the progress bar
		 ***** append the list to an Arraylist<double[]> (listParticle) of all of the energy deposited
		 * iterate over listParticle
		 ***** sum all  into a double[particle type][dose per distance] total dose per particle type at that distance
		 * return this double[][] back to calculate the survival based on
		 */

		Graphics progress = jPanel14.getGraphics();
		int width = jPanel14.getWidth();
		int height = jPanel14.getHeight();
		progress.clearRect( 0, 0, width, height );
		progress.setColor( Color.BLACK );
		progress.drawString( "Part(1/2) : 0%", width / 2 - 50, height / 2 + 6 );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, 5, height );

		// declare variables
		double cellmass = 4.0 * Math.PI * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		double xnuclmass = 4.0 * Math.PI * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		double cytomass = cellmass - xnuclmass;

		// Alex Rosen delete 1
		System.err.println("cellmass = " + cellmass + "   xnuclmass = " + xnuclmass + "   cytomass = " + cytomass);

		Energy energy = new Energy( cellmass, xnuclmass, cytomass );

		int ICODE;
		double ENERGY;
		double YEILD;
		ArrayList<double[]> data;
		//data order = icode, yeild, energy

		//TODO make it so that it gets the icode, energy, and yeild/decay for each type correctly
		if(dataType.equals( "USER" )) {
			data = ArrayListIn3.readMIRDdata( iso, true );
		}
		else if(dataType.equals( "BFUL" )) {
			data = ArrayListIn3.readOTHERdata( iso );
		}
		else if(dataType.equals( ("BAVG") )) {
			data = ArrayListIn3.readMIRDdata( iso, false );
		}
		else {
			data = new ArrayList<double[]>();
		}
		//System.exit( 0 );

		// TODO get locales
		double[] dep = new double[8];
		for(int i = 0; i < data.size(); i++) {

			//double[] q = data.get(i);
			//ICODE = (int)q[0];
			//YEILD = q[1];
			//ENERGY = q[2]*1000.0;

			ICODE = (int) data.get( i )[0];
			YEILD = data.get( i )[1];
			ENERGY = data.get( i )[2]*1000; // energy in the program is handled in KeV and is read in in MeV

			System.out.println("ICODE: " + ICODE + "\tENERGY: " + ENERGY + "\tYEILD: " + YEILD);

			if(ICODE == 1 || ICODE == 2) {
				// photons have basically unlimited range
				//range = 1.0E10;
				continue;
			}
			else if(ICODE == 4 || ICODE == 5 || ICODE == 6 || ICODE == 7 || ICODE == 3 || ICODE == 9 || ICODE == 10 || ICODE == 11 || ICODE == 1 || ICODE == 2) {
				range = Energy.electronRange( ENERGY );
			}
			else if(ICODE == 8){
				range = Energy.AlphaRange( ENERGY );
			}
			else{
				// fragmentation and neutrons are not handled yet
				continue;
			}

			System.out.println("CalTest3d_2 range = " + range);


			for(int j = 0; j < ranges.length && ranges[j] <= range+rc+rc; j++) {
				//System.out.println(ranges[j]);
				if(ICODE == 1 || ICODE == 2 || ICODE == 3) {
					//dep = energy.getPhotonEnergy( ENERGY, YEILD, rc, rn, ranges[j], locale );
					//if(dep.equals( null )) break;
				}
				else if(ICODE == 4 || ICODE == 5 || ICODE == 6 || ICODE == 7 ){
					dep = energy.getElectronEnergy( ENERGY, YEILD, rc, rn, ranges[j], MaxDist );
					//if(dep == null ) {System.out.println( "problem1" );break;}
				}
				else if(ICODE == 8) {
					dep = energy.getAlphaEnergy( ENERGY, YEILD, rc, rn, ranges[j], MaxDist );
					//if(dep == null ) {System.out.println( "problem1" );break;}
				}
				else {
					// fragmentation and neutrons are not handled yet
					// dep = 0;
					break;  // this should never be called but just in case
				}

				if(dep != null) {
					doses[ICODE][j][0] += dep[0]; doses[0][j][0] += dep[0];
					doses[ICODE][j][1] += dep[1]; doses[0][j][1] += dep[1];
					doses[ICODE][j][2] += dep[2]; doses[0][j][2] += dep[2];
					doses[ICODE][j][3] += dep[3]; doses[0][j][3] += dep[3];
					doses[ICODE][j][4] += dep[4]; doses[0][j][4] += dep[4];
					doses[ICODE][j][5] += dep[5]; doses[0][j][5] += dep[5];
					doses[ICODE][j][6] += dep[6]; doses[0][j][6] += dep[6];
					doses[ICODE][j][7] += dep[7]; doses[0][j][7] += dep[7];
				}
			}

			//update the progress bar
			//double percent = ((i - 6.0) / data.size()) * width / 2.0;   // this will fill the progress bar to 50%.  the other 50 will be filled when calculating the dose instead of the doses
			double percent = ((i - 6.0) / data.size()) * width;
			progress.clearRect( 0, 0, width, height );
			progress.setColor( Color.GREEN );
			progress.fillRect( 1, 1, (int) percent, 28 );
			progress.setColor( Color.BLACK );
                        if(i<6){
                            progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", 100.0 ) + "%", width / 2 - 50, height / 2 + 6 );
                        }else{
                            progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", percent / width * 100 ) + "%", width / 2 - 50, height / 2 + 6 );
                        }

		}

		NumberFormat formatter = new DecimalFormat( "0.00E00" );
		String crossDose0 = "0.00E+00";
		for(int k = 0; k < (2 * (int) rc - 2); k++) {
			jTextArea1.append("            " + crossDose0+"     "+crossDose0+"     "+crossDose0+"     "+crossDose0+"    "+crossDose0+"    " + crossDose0+"    " +crossDose0+"   " +crossDose0+"\n");
		}
		for(int i = 0; i < ranges.length; i++) {
			jTextArea1.append( formatter.format( ranges[i] ) + "       " + "   " + formatter.format( doses[0][i][0] ) + "     " + formatter.format( doses[0][i][1] ) + "     " + formatter.format( doses[0][i][2] ) + "     " + formatter.format( doses[0][i][3] ) + "     " + formatter.format( doses[0][i][4] ) + "     " + formatter.format( doses[0][i][5] ) + "     " + formatter.format( doses[0][i][6] ) + "     " + formatter.format( doses[0][i][7] ) + "\n" );
		}
		formatter = null;

		return doses;
	}

	public double[][][] calcDoses_test(double rc, double rn, JTextArea jTextArea1, JPanel jPanel14, int MaxDist, double[] ranges, ArrayList<double[]> data) {

		System.out.println("entering calcDoses");
		double[][][] doses = new double[12][ranges.length][8]; //[Icode(0 is the sum of all types of radiation)][rad range][src-target]
//		for(int i = 0; i < 11; i++) {
//			for(int j = 0; j < ranges.length; j++) {
//				for(int k = 0; k < 8; k++) {
//					doses[i][j][k] = 0;
//				}
//			}
//		}

		double range, maxRadRange = 0;

		/**
		 * @param rc = radius of the cell;
		 * @param rn = radius of the nucleus;
		 * @param iso = a string of the isotope name/file that the isotope data is contained in
		 * @param locale = the area where the dose is being delivered
		 * @param dataType = a string representing which dataset type you are reading in can be {"USER" || "BAVG" || "BFULL"}
		 * @param MaxDist = maximum distance achievable in the cluster
		 * @param numRadii = the number of possible ranges for the surviving fraction calculation
		 * @param ranges = an ordered list of all the possible distances for dosing
		 * @param radiations = the list of the radioactive decays with e0 and yeild
		 */

		/**
		 * Cross-Dose Calculation for MIRD Alpha particles Electron ICODE
		 * Mnemonic for ICODE Description
		 * ICODE = 1 G PG- prompt DG-delayed Gamma rays
		 * ICODE = 2 X x-rays
		 * ICODE = 3 AQ Annihilation quanta
		 * ICODE = 4 B+ Beta + particles
		 * ICODE = 5 BD- delayed Beta- particles
		 * ICODE = 6 IE Internal conversion Electrons
		 * ICODE = 7 AE Auger electrons
		 * ICODE = 8 A Alpha particles
		 * ICODE = 9 AR Daughter Recoil (Alpha decay)
		 * ICODE = 10 FF Fission fragments
		 * ICODE = 11 N Neutrons *Prompt and delayed radiations of spontaneous fission.
		 */

		/**
		 * iterate over each isotope in the list
		 ***** generate a chart of energy deposited per distance to a specific locale for that isotope with that energy
		 ***** update the progress bar
		 ***** append the list to an Arraylist<double[]> (listParticle) of all of the energy deposited
		 * iterate over listParticle
		 ***** sum all  into a double[particle type][dose per distance] total dose per particle type at that distance
		 * return this double[][] back to calculate the survival based on
		 */

		Graphics progress = jPanel14.getGraphics();
		int width = jPanel14.getWidth();
		int height = jPanel14.getHeight();
		progress.clearRect( 0, 0, width, height );
		progress.setColor( Color.BLACK );
		progress.drawString( "Part(1/2) : 0%", width / 2 - 50, height / 2 + 6 );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, 5, height );

		// declare variables
		double cellmass = 4.0 * Math.PI * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		double xnuclmass = 4.0 * Math.PI * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		double cytomass = cellmass - xnuclmass;

		// Alex Rosen delete 1
		System.err.println("cellmass = " + cellmass + "   xnuclmass = " + xnuclmass + "   cytomass = " + cytomass);

		Energy energy = new Energy( cellmass, xnuclmass, cytomass );

		int ICODE;
		double ENERGY;
		double YEILD;
		//data order = icode, yield, energy
                
            String crossDose0 = "0.00E+00";
            for (int k = 0; k < (2 * (int) rc - 2); k++) {
                writeFile.writeInBetweenToFile(crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, crossDose0, jTextArea1);
                //jTextArea1.append("             "+ crossDose0+"     "+crossDose0+"     "+crossDose0+"     "+crossDose0+"     "+crossDose0+"     " + "\n");//0701
            }
                

		double[] dep = new double[8];
		for(int i = 0; i < data.size(); i++) {

			ICODE = (int) data.get( i )[0];
			YEILD = data.get( i )[1];
			ENERGY = data.get( i )[2]*1000; // energy in the program is handled in KeV and is read in in MeV

			if(ICODE == 1 || ICODE == 2 || ICODE == 3) {
				// photons have basically unlimited range
				//range = 1.0E10;
				continue;
			}
			else if(ICODE == 4 || ICODE == 5 || ICODE == 6 || ICODE == 7) {
				range = Energy.electronRange( ENERGY );
			}
			else if(ICODE == 8){
				range = Energy.AlphaRange( ENERGY );
			}
			else if(ICODE == 9 || ICODE == 10 || ICODE == 11){
				// fragmentation and neutrons are not handled yet
				continue;
			}
			else{
				System.out.println("Unknown Radiation type - ICODE: " + ICODE);
				continue;
			}
                        maxRadRange = Math.max(maxRadRange, range);
			//System.out.println("CalTest3d_2 range = " + range);


			for(int j = 0; j < ranges.length && ranges[j] <= range+rc+rc; j++) {
				//System.out.println(ranges[j]);
				if(ICODE == 1 || ICODE == 2 || ICODE == 3) {
					//dep = energy.getPhotonEnergy( ENERGY, YEILD, rc, rn, ranges[j], locale );
					//if(dep.equals( null )) break;
				}
				else if(ICODE == 4 || ICODE == 5 || ICODE == 6 || ICODE == 7) {
					dep = energy.getElectronEnergy( ENERGY, YEILD, rc, rn, ranges[j], MaxDist );
					//if(dep == null ) {System.out.println( "problem1" );break;}
				}
				else if(ICODE == 8) {
					dep = energy.getAlphaEnergy( ENERGY, YEILD, rc, rn, ranges[j], MaxDist );
					//if(dep == null ) {System.out.println( "problem1" );break;}
				}
				else {
					// fragmentation and neutrons are not handled yet
					// dep = 0;
					break;  // this should never be called but just in case
				}

				if(dep != null) {
					doses[ICODE][j][0] += dep[0]; doses[0][j][0] += dep[0];
					doses[ICODE][j][1] += dep[1]; doses[0][j][1] += dep[1];
					doses[ICODE][j][2] += dep[2]; doses[0][j][2] += dep[2];
					doses[ICODE][j][3] += dep[3]; doses[0][j][3] += dep[3];
					doses[ICODE][j][4] += dep[4]; doses[0][j][4] += dep[4];
					doses[ICODE][j][5] += dep[5]; doses[0][j][5] += dep[5];
					doses[ICODE][j][6] += dep[6]; doses[0][j][6] += dep[6];
					doses[ICODE][j][7] += dep[7]; doses[0][j][7] += dep[7];
				}
			}

			//update the progress bar
			//double percent = ((i - 6.0) / data.size()) * width / 2.0;   // this will fill the progress bar to 50%.  the other 50 will be filled when calculating the dose instead of the doses
			double percent = ((i - 6.0) / data.size()) * width;
			progress.clearRect( 0, 0, width, height );
			progress.setColor( Color.GREEN );
			progress.fillRect( 1, 1, (int) percent, 28 );
			progress.setColor( Color.BLACK );
			if(i<6){
                            progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", 100.0 ) + "%", width / 2 - 50, height / 2 + 6 );
                        }else{
                            progress.drawString( "Part(1/2) : " + String.format( "%1$.1f", percent / width * 100 ) + "%", width / 2 - 50, height / 2 + 6 );
                        }

		}

		NumberFormat formatter = new DecimalFormat( "0.00E00" );
//		String crossDose0 = "0.00E+00";
//		for(int k = 0; k < (2 * (int) rc - 2); k++) {
//			jTextArea1.append("               " + crossDose0 + "    " + crossDose0 + "    " + crossDose0 + "    " + crossDose0 + "    " + crossDose0 + "    " + crossDose0 + "    " + crossDose0 + "    " + crossDose0 + "\n");
//		}

                //i < ranges.length - 2*rc + 1               
                // Jianchao 2/26/20 limit the output of s-values to the maximum rad range or the longest axis of the cluster, whichever is smaller
                // check maxRadRange should +1 or not
		for(int i = 0; i < Math.min((int)maxRadRange +1, ranges.length - 2*rc + 1); i++) {
			jTextArea1.append( (int)ranges[i] + "             " + formatter.format( doses[0][i][0] ) + "    " + formatter.format( doses[0][i][1] ) + "    " + formatter.format( doses[0][i][2] ) + "    " + formatter.format( doses[0][i][3] ) + "    " + formatter.format( doses[0][i][4] ) + "    " + formatter.format( doses[0][i][5] ) + "    " + formatter.format( doses[0][i][6] ) + "    " + formatter.format( doses[0][i][7] ) + "\n" );
		}
		formatter = null;

		return doses;
	}
}
