package SelfDoseCal;

import Energy.Energy;
import File.ArrayListIn2;
import File.MyList2;
import File.WriteToOutputFile;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static java.lang.Math.pow;

/**
 * Created by ar548 on 8/25/2016.
 */
public class SelfDose_2 {
	int ICODE;
	double YEILD;
	double ENERGY;
	double delta;
	double range = 0.0; //dnn = 0.0; Jianchao wang 10/21/18 dnn should not be global
	double rc;
	double rn;
	private static final double conv = 7.508e-5;
	private static final double act = 1.0e-6;
	public static final double NucStep = 300; // change this to change the number of steps and increase or decrease precision
	private static final String alphaFileName = "alph-water2";

	double cellmass;
	double xnuclmass;
	double cytomass;

	// please efficency gods forgive my laziness in not recreating the new functions
	ArrayList<MyList2> ListAlph = new ArrayListIn2( "test" ).readAlpha( alphaFileName );


	public double[][] calcSelfDose(double rcell, double rnuc, ArrayList<double[]> data, JTextArea jTextArea1 ){
		this.rc = rcell;
		this.rn = rnuc;
		double[][] doses = new double[12][8];  // numICODES x numTargets
		for(int i = 0; i < 12; i++) {
			for(int j = 0; j < 8; j++) {
				doses[i][j] = 0.0;
			}
		}

		cellmass = 4.0 * 3.14159 * Math.pow( rc * (1.0e-04), 3 ) / 3.0;
		xnuclmass = 4.0 * 3.14159 * Math.pow( rn * (1.0e-04), 3 ) / 3.0;
		cytomass = cellmass - xnuclmass;

		double dncy = 0;    // dose: cytoplasm to nucleus
		double dns = 0;     // dose: surface to nucleus
		double dcs = 0;     // dose: surface to cell
		double dcc = 0;     // dose: cell to cell
		double dnn = 0;     // dose: nucleus to nucleus
		double dcyn = 0;    // dose: nucleus to cytoplasm
		double dcycs = 0;   // dose: cell surface to cytoplasm
		double dcycy = 0;   // dose: cytoplasm to cytoplasm

		for(int i = 0; i < data.size(); i++) {
			// for each radionuclide
			ICODE = (int)data.get( i )[0];
			YEILD = data.get( i )[1];
			ENERGY = data.get( i )[2]*1000; // energy in the program is handled in KeV and is read in in MeV
			delta = 2.13 * ENERGY * YEILD * 1e-3;
			if(ICODE == 1 || ICODE == 2 || ICODE == 3 || ICODE == 9 || ICODE == 10 || ICODE == 11) {
				// these particles are ignored for now
				continue;
			}
			else if(ICODE == 4 || ICODE == 5 || ICODE == 6 || ICODE == 7) {
				range = Energy.electronRange( ENERGY );
			}
			else if(ICODE == 8){
				range = Energy.AlphaRange( ENERGY );
			}

			dcc = this.getDcc();
			dcs = this.getDcs();
			dnn = this.getDnn();
			dncy = this.getDncy();
			dns = this.getDns();

			doses[ICODE][0] += dcc ; doses[0][0] += dcc ;
			doses[ICODE][1] += dcs ; doses[0][1] += dcs ;
			doses[ICODE][2] += dnn ; doses[0][2] += dnn ;
			doses[ICODE][3] += dncy; doses[0][3] += dncy;
			doses[ICODE][4] += dns ; doses[0][4] += dns ;
			//System.out.println("dcc="+dcc+"\t"+"dcs="+dcs+"\t"+"dnn="+dnn+"\t"+"dncy="+dncy+"\t"+"dns="+dns);
			//System.out.println("dcc="+doses[ICODE][0]+"\t"+"dcs="+doses[ICODE][1]+"\t"+"dnn="+doses[ICODE][2]+"\t"+"dncy="+doses[ICODE][3]+"\t"+"dns="+doses[ICODE][4]);


		}
		//System.out.println("dcc="+dcc+"\t"+"dcs="+dcs+"\t"+"dnn="+dnn+"\t"+"dncy="+dncy+"\t"+"dns="+dns);

		for(int icode = 0; icode < 12; icode++) {
			doses[icode][5] = doses[icode][3];
			doses[icode][6] = (doses[icode][1] * cellmass - doses[icode][4] * xnuclmass) / cytomass;
			doses[icode][7] = (doses[icode][0] * cellmass * cellmass) / (cytomass * cytomass) - (2 * doses[icode][3] * xnuclmass) / cytomass - (doses[icode][2] * xnuclmass * xnuclmass) / (cytomass * cytomass);
		}

		//System.out.println("AAAAAAAAAA" + doses[0][0] + "\t" + doses[0][1] + "\t" + doses[0][2] + "\t" + doses[0][3] + "\t" + doses[0][4] + "\t" + doses[0][5] + "\t" + doses[0][6] + "\t" + doses[0][7] + "\t");
		WriteToOutputFile w = new WriteToOutputFile();
		NumberFormat formatter = new DecimalFormat( "0.00E00" );
		w.writeSelfToFile( formatter.format( doses[0][0] ), formatter.format( doses[0][1] ), formatter.format( doses[0][2] ), formatter.format( doses[0][3] ), formatter.format( doses[0][4] ), formatter.format( doses[0][5] ), formatter.format( doses[0][6] ), formatter.format( doses[0][7] ), jTextArea1 );

		return doses;
	}

	private double getDcc(){
		double ecc = 0;
		double dcc;
		double abscc;
		if(ENERGY > 1){
			Enn nn = new Enn();
			ecc = nn.getEnn( (ICODE == 8) ? "ALPHA":"ELECTRON", ENERGY, range, rc, ListAlph );
		}
		else{
			ecc = ENERGY;
		}
		abscc = ecc / ENERGY;
		dcc = conv * act * abscc * delta / cellmass;

		return dcc;
	}

	private double getDcs(){
		double ecs = 0;
		if(ENERGY >= 1){
			Ens ns = new Ens();
			ecs = ns.getEns( (ICODE == 8) ? "ALPHA":"ELECTRON", ENERGY, range, rc, rc, ListAlph );
		}
		else{
			ecs = ENERGY/2.0;
		}
		double abscs = ecs/ENERGY;
		double dcs = conv * act * abscs * delta / cellmass;
		return dcs;
	}

	private double getDnn(){
		double enn;
                double dnn = 0; //jianchao wang 10/21/18 dnn should be local viable
                
		if(ENERGY >= 1){
			Enn nn = new Enn();
			enn = nn.getEnn((ICODE == 8) ? "ALPHA":"ELECTRON", ENERGY, range, rn, ListAlph);
		}
		else{
			enn = ENERGY;
		}
		double absnn = enn / ENERGY;
		dnn = dnn + conv * act * absnn * delta / xnuclmass;
		return dnn;
	}

	private double getDncy(){
		Ency ncy = new Ency();
		double ency = ncy.getEncy( (ICODE == 8) ? "ALPHA":"ELECTRON", ENERGY, range, rc, rn, ListAlph );
		double absncy = ency / ENERGY;
		double dncy = conv * act * absncy * delta / xnuclmass;
		return dncy;
	}

	private double getDns(){
		double ens;
		if(ENERGY >= 1 ){
			Ens ns = new Ens();
			ens = ns.getEns( (ICODE == 8) ? "ALPHA":"ELECTRON", ENERGY, range, rc, rn, ListAlph );
		}
		else{
			ens = 0;
		}
		double absns = ens/ENERGY;
		double dns = conv * act * absns * delta / xnuclmass;
		return dns;
	}

}
