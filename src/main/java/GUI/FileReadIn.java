package GUI;

import java.io.*;
import java.util.*;
/*
 * FileReadIn.java
 *
 * Created on July 17, 2008, 3:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @author Johnny  Wu
 */
public class FileReadIn {

	/**
	 * Creates a new instance of FileReadIn
	 */
	String cellRadius = "0";
	//String nuclearRadius = "0";
	//int distance = 0;

	//file directory, change if neccessary
	String directory = "/CellDose/rc";
	//String directory = "D:/STUDY/java doc/CellDoseInterface/dist/radNucs/rc";
	//String directory = "radNucs/rc";
	String isotope = " ";
	String radiationType = "0";
	int yieldPerDecay;
	int energy;
	int delta;

	int readPosition = 0;
	int gibberish = 0;
	File f;

	public FileReadIn(int rc, String iso, int pos) {
		this.cellRadius = Integer.toString(rc);
		this.isotope = iso;
		this.readPosition = pos;
	}

	public void setCellRadius(int rc) {
		this.cellRadius = Integer.toString(rc);
	}

	/**
	 * if (tpye == ' BETA '' KCONV'. ' LCONV'.' MCONV'. .'KLXAUG' +'LMMAUG'. 'MXYAUG'. ' LLXCK'.+	'KLLAUG'. '  CONV'. 'LMXAUG'.     +
	 * 'LXYAUG'. 'KXYAUG'. ' MMXCK'.+ ' NNXCK'. 'NXYAUG'.' OOXCK'.' ALPHA'. 'NCONV'. ' OCONV')  {
	 * <p/>
	 * CellDoseCal cdCal = new CellDoseCal();
	 * }
	 * <p/>
	 * <p/>
	 * else if (' GAMMA'. 'KXALPH'. 'KXBETA'.'LXALPH'.'LXBETA'.+'MXALPH'.'NXALPH') {
	 * <p/>
	 * System.out.println(" Gamma & X-ray not allowed.");
	 * <p/>
	 * }
	 */

	public void init() throws Exception {

		directory = directory.concat(cellRadius);
		directory = directory.concat("/");
		directory = directory.concat(isotope + ".dat");
		//should add directory path change function

		String firstEntry = "0.000000000E+00";
		f = new File(directory);

		if (f.exists()) {
			//FileInputStream fisA = new FileInputStream(f);
			//InputStreamReader isrA = new InputStreamReader(fisA);
			//BufferedReader rdrA = new BufferedReader(isrA);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

			StringTokenizer stA;
			String inputA, tokenA;
			boolean foundFirstEntry = false;

			while ((inputA = br.readLine()) != null) {
				stA = new StringTokenizer(inputA);
				while (stA.hasMoreTokens()) {
					tokenA = stA.nextToken();
					if (tokenA.equals(firstEntry)) {
						foundFirstEntry = true;
					}
				}
				if (!foundFirstEntry) {
					gibberish++;
				}
			}
		}
	}

	//get values from readin file
	public void retrieve() throws Exception {
		//int cellRadiusInt = Integer.parseInt(cellRadius);
		int offset;


		offset = gibberish - 1;


		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader rdr = new BufferedReader(isr);

		StringTokenizer st;
		String input, token;
		int i = 0;
		int j = 0;
		int k = 0;

		while ((input = rdr.readLine()) != null) {
			st = new StringTokenizer(input);
			if (i == (gibberish - 1)) {
				while (st.hasMoreTokens()) {
					token = st.nextToken();
					if (j == readPosition) {
						yieldPerDecay = Integer.parseInt(token);
					}
					j++;
				}
			}
			if (i == offset) {
				while (st.hasMoreTokens()) {
					token = st.nextToken();
					if (k == readPosition) {
						energy = Integer.parseInt(token);
					}
					k++;
				}
			}
			i++;
		}
	}//end of readin

	/**
	 * this.radiationType = rad;
	 * this.yieldPerDecay = xn;
	 * this.energy = en;
	 * this.delta = delta;
	 * this.isotope = iso;
	 * this.readPosition = pos;
	 */
	public String getRadiationType() {
		return radiationType;
	}

	public int getYieldPerDecay() {
		return yieldPerDecay;
	}

	public int getEnergy() {
		return energy;
	}

	public int getDelta() {
		return delta;
	}

	//get max number of integration loops

	public int getMaxIstop() throws Exception {
		String input, token = " ";
		StringTokenizer st;

		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader rdr = new BufferedReader(isr);
		int i = 0;

		while ((input = rdr.readLine()) != null) {
			st = new StringTokenizer(input);
			if (i == (gibberish - 2)) {
				token = st.nextToken();
				break;
			}
			i++;
		}
		return Integer.parseInt(token);
	}

}
