package GUI;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.Graphics;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Arrays;

public class SurvivalCalculationComplex {

	public double activitytotal;
	public double tempsurvall, tempsurvlabel, tempsurvunlabel;
	public double[][] PlotOutput;
	double SurvFrac;
	private final double step = 100D; // change this number to make the surviving fraction curve more accurate / have a more fine step ratio
	public String output;
        double TCP;

	public SurvivalCalculationComplex() {
		activitytotal = 0;
		tempsurvall = 0;
		tempsurvlabel = 0;
		tempsurvunlabel = 0;
		PlotOutput = new double[9][(int) step + 1];
		SurvFrac = 0;

		//PlotOutput[7][0] = 1;
		//PlotOutput[6][0] = 1;
		//PlotOutput[5][0] = 1;
	}

	public double[][] calculateSurvival(
			double[][] cell, double[][] celllabel, double[][][] SValues, double[][] selfSValues, double[][] complexRadiobiologicalParams, double[] activityFractions,
			double MAC, int d, int rCell,
			int cellnumber, int jHeight, int jWidth, int radiationtarget,
			JTextArea jTextArea5,
			Graphics progress
	) {
		double dist;
		double labelRad = 0D;   // A counter for the radiation dose to all the labeled cells
		double MeanABD;         // Average dose to each cell
		double MeanABDL;        // Average dose to all cells
		double MeanABDUL = 0D;  // Average dose to unlabled cells
		double percent;         // The perccent completion for all the progress bar
		double rand;
		double ratio;
		double survFrac = 0D;   // the field that keeps track of the surviving fraction
		//double Survival;      // the field where the Pval is stored (probability of survival between 0 and 1)
		double totalRad = 0D;   // A counter for the radiation dose of all the cells
		double x, y, z;         // The cells' position in the cluster
                double selfRad = 0D;
                double labelRadSelf = 0D;
                double temp;

		output = "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";

		// TODO see below
		// clear out a huge block og contiguous memory.  this is a terrible way to do this but i dont have the time to code a doubly linked list of dose objects
		double[][] crossDoses = new double[cellnumber][11];             // [index][ICODE]
		double[] Survival = new double[cellnumber];                     // the field where the Pval is stored (probability of survival between 0 and 1)
		Arrays.fill(Survival, 1.0);

		//int labelLength = celllabel.length - 1; // = just put this in manually
		int labelCell = celllabel.length;           // the number of labled cells
		int unlableCell = cellnumber - labelCell;   // the number of unlabled cells
		//int length = cell.length - 1; // = cellnumber
		int allLive = 0;                            // the number of cells that live
		int labeledLive = 0;                              // the number of labled cells that live
		int unlabeledLive;                           // the number of unlabled cells that live
		int PlotPoints = 0;
		
		boolean[] coldLiving = new boolean[cellnumber];
		for(int i = 0; i < cellnumber; i++){
			if(cell[i][0] != 1){
				coldLiving[i] = true;
				//cell[i][0] = 1;
			}
			else{
				coldLiving[i] = false;
			}
		}

		Random random = new Random();

		// prepare the progress bar for part 2 of 2
		progress.clearRect(0, 0, jWidth, jHeight);
		progress.setColor(Color.BLACK);
		progress.drawString("Part 2 of 2 : 0%", jWidth / 2 - 50, jHeight / 2 + 6);
		progress.setColor(Color.GREEN);
		progress.fillRect(1, 1, 1, jHeight);

		/*
		 * SValues[][][] = [ICODE][distance][dose region]
		 * selfSVals[][] = [ICODE][dose region]
		 * for dose regions:
		 *      0 = cell to cell;
		 *      1 = cell surface to cell
		 *      2 = Nucleus to Nucleus
		 *      3 = Cytoplasm to Nucleus
		 *      4 = Cell Surface to Nucleus
		 *      5 = Nucleus to Cytoplasm
		 *      7 = Cell Surface to Cytoplasm
		 *      6 = Cytoplasm to Cytoplasm
		 */
                // Jianchao 3/24/20 variables to keep track of dose type/region for each cell
                // uncomment for debugging purposes
 /*               
                double[][] crossDose = new double[12][8]; //[ICODE][TARGET<-SRC]
                double[][] selfDose = new double[12][8];    //[ICODE][TARGET<-SRC]
*/                
		if (radiationtarget == 0) {
			// radiation target: cell
                        totalRad = 0;
                        labelRad = 0;
                        selfRad = 0;
                        labelRadSelf = 0;
			for (int i = 0; i < cellnumber; i++) {
				/* for each cell */
				if (cell[i][4] != 0) {
					/* if that cell is labeled with radiation calculate self dose */
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						/* self, C->C */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * selfSValues[ICODE + 1][0] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * selfSValues[ICODE + 1][0] * selfSValues[ICODE + 1][0] * activityFractions[2] * activityFractions[2] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][0] * activityFractions[2] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][0] * activityFractions[2] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][0] += selfSValues[ICODE + 1][0] * activityFractions[2] * cell[i][5];
                                                        selfRad += selfSValues[ICODE + 1][0] * activityFractions[2] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][0] * activityFractions[2] * cell[i][5];
                                                        
						}
						/* self, CS->C */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * selfSValues[ICODE + 1][1] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * selfSValues[ICODE + 1][1] * selfSValues[ICODE + 1][1] * activityFractions[3] * activityFractions[3] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][1] * activityFractions[3] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][1] * activityFractions[3] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][1] += selfSValues[ICODE + 1][1] * activityFractions[3] * cell[i][5];
                                                        selfRad += selfSValues[ICODE + 1][1] * activityFractions[3] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][1] * activityFractions[3] * cell[i][5];                                                        
						}
					}
				}

				// then dose that cell feom each labeled cell
                                double tempdose = 0;
				for (int ICODE = 0; ICODE < 11; ICODE++) {
                                    
					for (int j = 0; j < labelCell; j++) {
						x = celllabel[j][1] - cell[i][1];
						y = celllabel[j][2] - cell[i][2];
						z = celllabel[j][3] - cell[i][3];
						dist = Math.sqrt(x * x + y * y + z * z);

						if (dist == 0){
							continue;
						}

						if (dist > SValues[0].length - 1) {
							dist = SValues[0].length - 1;
						}

						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][0] * celllabel[j][5] * activityFractions[2]; /* cross, C->C */
						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][1] * celllabel[j][5] * activityFractions[3]; /* cross, CS->C */                                            
					}
                                       
                                        
                                        //complexRadiobiologicalParams[ICODE][10]: alpha; complexRadiobiologicalParams[ICODE][11]: beta
					if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0) {
						Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * crossDoses[i][ICODE] - complexRadiobiologicalParams[ICODE][5] * crossDoses[i][ICODE] * crossDoses[i][ICODE]);
						totalRad += crossDoses[i][ICODE];
						if (cell[i][4] != 0) {
							labelRad += crossDoses[i][ICODE];
						}
					}
                                        tempdose += crossDoses[i][ICODE];
				}
                                cell[i][7] = tempdose;
                                

				rand = random.nextDouble();
				if (rand >= Survival[i] || coldLiving[i]) {
					cell[i][0] = 0;
				} 
				//update the progress bar
				percent = ((double) i / (double) cellnumber) * (double) jWidth;
				progress.clearRect(0, 0, jWidth, jHeight);
				progress.setColor(Color.GREEN);
				progress.fillRect(1, 1, (int) percent, 28);
				progress.setColor(Color.BLACK);
				progress.drawString("Part 2 of 2 : " + String.format("%1$.2f", percent / jWidth * 100) + "%", jWidth / 2 - 50, jHeight / 2 + 6);
			}
                        //adding self and cross dose stats
                        double MeanABDSelf = selfRad / cellnumber;
                        double MeanABDSelfL = labelRadSelf / labelCell;
                        double MeanABDSelfUL = 0;
                        double MeanABDCross = (totalRad - selfRad) / cellnumber;
                        double MeanABDCrossL = (totalRad - selfRad) / labelCell;
                        double MeanABDCrossUL = 0;
                                
			MeanABD = totalRad / cellnumber;
			MeanABDL = labelRad / labelCell;
			if ((cellnumber - labelCell) != 0) {
				MeanABDUL = (totalRad - labelRad) / (cellnumber - labelCell);
                                
                                MeanABDSelfUL = (selfRad - labelRadSelf) / (cellnumber - labelCell);
                                MeanABDCrossUL = (totalRad - selfRad) / (cellnumber - labelCell);
			}
			System.err.println("MeanABDUL = " + MeanABDUL);
			System.err.println("totalRad = " + totalRad);
			System.err.println("labelRad = " + labelRad);

			//unlabeledLive = allLive - labeledLive;

			for (int i = cell.length - 1; i >= 0; i--) { //DO NOT need?
				activitytotal = activitytotal + cell[i][5];
			}

			progress.clearRect(0, 0, jWidth, jHeight);
			progress.setColor(Color.GREEN);
			progress.fillRect(1, 1, jWidth, jHeight);
			progress.setColor(Color.BLACK);
			progress.drawString("Plotting Data - please wait", jWidth / 2 - 50, jHeight / 2 + 6);

			double[][] totalDoses = new double[11][3]; // totalDoses[ICODE][mixed | labeled | unlabeled]
                        double[][] selfDoses = new double[11][3];
                        double[][] crossDose = new double[11][3];
			for (int i = 0; i < cellnumber; i++) {
				for (int ICODE = 1; ICODE < 12; ICODE++) {
					totalDoses[ICODE - 1][0] += crossDoses[i][ICODE - 1] + selfSValues[ICODE][0] * activityFractions[2] * cell[i][5] + selfSValues[ICODE][1] * activityFractions[3] * cell[i][5];
                                        
                                        selfDoses[ICODE - 1][0] += selfSValues[ICODE][0] * activityFractions[2] * cell[i][5] + selfSValues[ICODE][1] * activityFractions[3] * cell[i][5];
                                        crossDose[ICODE-1][0] += crossDoses[i][ICODE - 1];
					if(cell[i][4] != 0) {
						totalDoses[ICODE - 1][1] += crossDoses[i][ICODE - 1] + selfSValues[ICODE][0] * activityFractions[2] * cell[i][5] + selfSValues[ICODE][1] * activityFractions[3] * cell[i][5];
                                                
                                                selfDoses[ICODE - 1][1] += selfSValues[ICODE][0] * activityFractions[2] * cell[i][5] + selfSValues[ICODE][1] * activityFractions[3] * cell[i][5];
                                                crossDose[ICODE - 1][1] += crossDoses[i][ICODE - 1];
					}
					else {
						totalDoses[ICODE - 1][2] += crossDoses[i][ICODE - 1];
                                                
                                                selfDoses[ICODE - 1][2] = 0;
                                                crossDose[ICODE - 1][2] += crossDoses[i][ICODE - 1];
					}
				}
			}
                        
                        String output_self = "Self Absorbed Doses to cell (from decays anywhere within the same cell)\n";
			output_self += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
                        String output_cross = "Cross Absorbed Doses to cell (from decays anywhere within neighboring cells)\n";
			output_cross += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";

			for (double k = 0; k <= step; k++) {
				Arrays.fill(Survival, 1.0);
				allLive = 0;
				labeledLive = 0;
				unlabeledLive = 0;
				ratio = k / step;
                                TCP = 1;

				for (int i = 0; i < cellnumber; i++) {
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						/* self, C->C */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * ratio * selfSValues[ICODE + 1][0] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * ratio * ratio * selfSValues[ICODE + 1][0] * selfSValues[ICODE + 1][0] * activityFractions[2] * activityFractions[2] * cell[i][5] * cell[i][5]);
						/* self, CS->C */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * ratio * selfSValues[ICODE + 1][1] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * ratio * ratio * selfSValues[ICODE + 1][1] * selfSValues[ICODE + 1][1] * activityFractions[3] * activityFractions[3] * cell[i][5] * cell[i][5]);
						/* cross, C->C & CS->C */
						if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * ratio * crossDoses[i][ICODE] - complexRadiobiologicalParams[ICODE][5] * ratio * ratio * crossDoses[i][ICODE] * crossDoses[i][ICODE]);
					}
                                        TCP *= 1 - Survival[i];
					rand = random.nextDouble();
					if (rand < Survival[i] && !coldLiving[i]) {
						//allLive++;
						if (cell[i][4] == 0) {
							unlabeledLive++;
						} else {
							labeledLive++;
						}
					}
				}
                                allLive = labeledLive + unlabeledLive;
                                
				tempsurvall = (double) allLive / (double) cellnumber;
				if (labelCell != 0) {
					tempsurvlabel = (double) labeledLive / (double) labelCell;
				}
				if (unlableCell != 0) {
					tempsurvunlabel = (double) unlabeledLive / (double) unlableCell;
				}
                                
                                PlotOutput[0][PlotPoints] = (MAC * ratio);                          //MAC
				PlotOutput[1][PlotPoints] = (MeanABD * ratio);                      //MDC
				PlotOutput[2][PlotPoints] = (MAC * ratio * cellnumber / labelCell); //MALC
				PlotOutput[3][PlotPoints] = (MeanABDL * ratio);                     //MDLC
				PlotOutput[4][PlotPoints] = (MeanABDUL * ratio);                    //MDULC
				PlotOutput[5][PlotPoints] = (tempsurvlabel);                        //SF of Labeled
				PlotOutput[6][PlotPoints] = (tempsurvunlabel);                      //SF of unlabeled
				PlotOutput[7][PlotPoints] = (tempsurvall);                          //SF of all
                                PlotOutput[8][PlotPoints] = TCP;                                    //Tumor Control Prob. = prod(1 - SPi)
                                //System.err.println(tempsurvall + " " + PlotOutput[7][PlotPoints]);

				PlotPoints++;

				NumberFormat nf = new DecimalFormat("0.00E00");
				output += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABD * ratio) ) + "\t"
						+ nf.format(totalDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDL * ratio) + "\t"
						+ nf.format(totalDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDUL * ratio) + "\t"
						+ nf.format(totalDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_self += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDSelf * ratio) ) + "\t"
						+ nf.format(selfDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDSelfL * ratio) + "\t"
						+ nf.format(selfDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDSelfUL * ratio) + "\t"
						+ nf.format(selfDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_cross += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDCross * ratio) ) + "\t"
						+ nf.format(crossDose[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDCrossL * ratio) + "\t"
						+ nf.format(crossDose[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDCrossUL * ratio) + "\t"
						+ nf.format(crossDose[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
			}
			totalDoses = null; // free up some memory space
                        output += "\n\n" + output_self +"\n\n" + output_cross;
                        output_self = null;
                        output_cross = null;
		} else if (radiationtarget == 1) {
			// radiation target: Nucleus
                        totalRad = 0;
                        labelRad = 0;
                        selfRad = 0;
                        labelRadSelf = 0;
                        
			for (int i = 0; i < cellnumber; i++) {
				if (cell[i][4] != 0) {
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						/* self, N->N */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5] * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][2] += selfSValues[ICODE + 1][2] * activityFractions[0] * cell[i][5];
                                                        selfRad += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
						}
						/* self, Cy->N */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5] * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][3] += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
                                                        selfRad += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
						}
						/* se;f, CS->N */
						if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][5] * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5] * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][4] = selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
                                                        selfRad += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
						}
					}
				}
                                double tempdose = 0;
				for (int ICODE = 0; ICODE < 11; ICODE++) {
					for (int j = 0; j < labelCell; j++) {
						x = celllabel[j][1] - cell[i][1];
						y = celllabel[j][2] - cell[i][2];
						z = celllabel[j][3] - cell[i][3];
						dist = Math.sqrt(x * x + y * y + z * z);

						if(dist == 0){
							continue;
						}

						if (dist > SValues[0].length - 1) {
							dist = SValues[0].length - 1;
						}

						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][2] * celllabel[j][5] * activityFractions[1]; /* cross, N->N */
						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][3] * celllabel[j][5] * activityFractions[2]; /* cross, Cy->N */
						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][4] * celllabel[j][5] * activityFractions[3]; /* cross, CS->N */						
                                                
					}

					if (complexRadiobiologicalParams[ICODE][6] != 0 || complexRadiobiologicalParams[ICODE][7] != 0) {
						Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][6] * crossDoses[i][ICODE] - complexRadiobiologicalParams[ICODE][7] * crossDoses[i][ICODE] * crossDoses[i][ICODE]);
						totalRad += crossDoses[i][ICODE];
						if (cell[i][4] != 0) {
							labelRad += crossDoses[i][ICODE];
						}
					}
                                        tempdose += crossDoses[i][ICODE];
				}
                                cell[i][7] = tempdose;
				rand = random.nextDouble();
				if (rand >= Survival[i] || coldLiving[i]) {
					cell[i][0] = 0;
				} 

				//update the progress bar
				percent = ((double) i / (double) cellnumber) * (double) jWidth;
				progress.clearRect(0, 0, jWidth, jHeight);
				progress.setColor(Color.GREEN);
				progress.fillRect(1, 1, (int) percent, 28);
				progress.setColor(Color.BLACK);
				progress.drawString("Part 2 of 2 : " + String.format("%1$.2f", percent / jWidth * 100) + "%", jWidth / 2 - 50, jHeight / 2 + 6);
			}
                        
                        //adding self and cross dose stats
                        double MeanABDSelf = selfRad / cellnumber;
                        double MeanABDSelfL = labelRadSelf / labelCell;
                        double MeanABDSelfUL = 0;
                        double MeanABDCross = (totalRad - selfRad) / cellnumber;
                        double MeanABDCrossL = (totalRad - selfRad) / labelCell;
                        double MeanABDCrossUL = 0;

			MeanABD = totalRad / cellnumber;
			MeanABDL = labelRad / labelCell;
			if ((cellnumber - labelCell) != 0) {
				MeanABDUL = (totalRad - labelRad) / (cellnumber - labelCell);
                                
                                MeanABDSelfUL = (selfRad - labelRadSelf) / (cellnumber - labelCell);
                                MeanABDCrossUL = (totalRad - selfRad) / (cellnumber - labelCell);
			}
			System.err.println("MeanABDUL = " + MeanABDUL);
			System.err.println("totalRad = " + totalRad);
			System.err.println("labelRad = " + labelRad);

			unlabeledLive = allLive - labeledLive;

			for (int i = cell.length - 1; i >= 0; i--) {
				activitytotal = activitytotal + cell[i][5];
			}

			progress.clearRect(0, 0, jWidth, jHeight);
			progress.setColor(Color.GREEN);
			progress.fillRect(1, 1, jWidth, jHeight);
			progress.setColor(Color.BLACK);
			progress.drawString("Plotting Data - please wait", jWidth / 2 - 50, jHeight / 2 + 6);

			double[][] totalDoses = new double[11][3]; // totalDoses[ICODE][mixed | labeled | unlabeled]
                        double[][] selfDoses = new double[11][3];
                        double[][] crossDose = new double[11][3];
                        
//			for (int i = 0; i < 11; i++) {
//				for (int j = 0; j < 3; j++) {
//					totalDoses[i][j] = 0;
//				}
//			}
			for (int i = 0; i < cellnumber; i++) {
				for (int ICODE = 1; ICODE < 12; ICODE++) {
					totalDoses[ICODE - 1][0] += crossDoses[i][ICODE - 1]
							+ selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
                                        
                                        selfDoses[ICODE - 1][0] += selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
                                        crossDose[ICODE-1][0] += crossDoses[i][ICODE - 1];
                                        
					if(cell[i][4] != 0) {
						totalDoses[ICODE - 1][1] += crossDoses[i][ICODE - 1]
								+ selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
								+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
								+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
                                                
                                                selfDoses[ICODE - 1][1] += selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
                                                crossDose[ICODE-1][1] += crossDoses[i][ICODE - 1];
					}
					else {
						totalDoses[ICODE - 1][2] += crossDoses[i][ICODE - 1];
                                                
                                                selfDoses[ICODE - 1][2] = 0;
                                                crossDose[ICODE - 1][2] += crossDoses[i][ICODE - 1];
					}
				}
			}
                        
                        String output_self = "Self Absorbed Doses to nucleus(from decays anywhere within the same cell)\n";
			output_self += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
                        String output_cross = "Cross Absorbed Doses to nucleus (from decays anywhere within neighboring cells)\n";
			output_cross += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";

			for (double k = 0; k <= step; k++) {
				Arrays.fill(Survival, 1);
				allLive = 0;
				labeledLive = 0;
				unlabeledLive = 0;
				ratio = k / step;
                                TCP = 1;

				for (int i = 0; i < cellnumber; i++) {
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						/* self, N->N */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * ratio * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * ratio * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5] * ratio * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5]);
						/* self, Cy->N */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * ratio * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * ratio * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5] * ratio * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5]);
						/* self, CS->N */
						if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * ratio * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][5] * ratio * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5] * ratio * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5]);
						/* cross, N->N & Cy->N & CS->N */
						if (complexRadiobiologicalParams[ICODE][6] != 0 || complexRadiobiologicalParams[ICODE][7] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][6] * ratio * crossDoses[i][ICODE] - complexRadiobiologicalParams[ICODE][7] * ratio * crossDoses[i][ICODE] * ratio * crossDoses[i][ICODE]);
					}
                                        TCP *= 1 - Survival[i];
					rand = random.nextDouble();
					if (rand < Survival[i] && !coldLiving[i]) {
						//allLive++;
						if (cell[i][4] == 0) {
							unlabeledLive++;
						} else {
							labeledLive++;
						}
					}
				}
                                
                                allLive = labeledLive + unlabeledLive;

				tempsurvall = (double) allLive / (double) cellnumber;
				if (labelCell != 0) {
					tempsurvlabel = (double) labeledLive / (double) labelCell;
				}
				if (unlableCell != 0) {
					tempsurvunlabel = (double) unlabeledLive / (double) unlableCell;
				}

				NumberFormat nf = new DecimalFormat("0.00E00");
				output += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABD * ratio) ) + "\t"
						+ nf.format(totalDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDL * ratio) + "\t"
						+ nf.format(totalDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDUL * ratio) + "\t"
						+ nf.format(totalDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_self += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDSelf * ratio) ) + "\t"
						+ nf.format(selfDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDSelfL * ratio) + "\t"
						+ nf.format(selfDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDSelfUL * ratio) + "\t"
						+ nf.format(selfDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_cross += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDCross * ratio) ) + "\t"
						+ nf.format(crossDose[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDCrossL * ratio) + "\t"
						+ nf.format(crossDose[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDCrossUL * ratio) + "\t"
						+ nf.format(crossDose[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";

				PlotOutput[0][PlotPoints] = (MAC * ratio);                          //MAC
				PlotOutput[1][PlotPoints] = (MeanABD * ratio);                      //MDC
				PlotOutput[2][PlotPoints] = (MAC * ratio * cellnumber / labelCell); //MALC
				PlotOutput[3][PlotPoints] = (MeanABDL * ratio);                     //MDLC
				PlotOutput[4][PlotPoints] = (MeanABDUL * ratio);                    //MDULC
				PlotOutput[5][PlotPoints] = (tempsurvlabel);                        //SF of Labeled
				PlotOutput[6][PlotPoints] = (tempsurvunlabel);                      //SF of unlabeled
				PlotOutput[7][PlotPoints] = (tempsurvall);                          //SF of all
                                PlotOutput[8][PlotPoints] = TCP; 

				PlotPoints++;
			}
			totalDoses = null; // free up some memory space
                        output += "\n\n" + output_self +"\n\n" + output_cross;
                        output_self = null;
                        output_cross = null;
                        
		}
		else if (radiationtarget == 2) {
			// radiation target: Cytoplasm.
                        totalRad = 0;
                        labelRad = 0;
                        selfRad = 0;
                        labelRadSelf = 0;
                        
                        
			for (int i = 0; i < cellnumber; i++) {
			/* for each cell */
				if (cell[i][4] != 0) {
				/* if the cell is labeled calculate self dose exponential and decrease survival rate */
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						// the if statements below are to not do useless work
					/* self, N->Cy */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * selfSValues[ICODE + 1][5] * selfSValues[ICODE + 1][5] * activityFractions[1] * activityFractions[1] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][5] = selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];

                                                        selfRad += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
						}
					/* self, Cy->Cy */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * selfSValues[ICODE + 1][7] * selfSValues[ICODE + 1][7] * activityFractions[2] * activityFractions[2] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][7] = selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
                                                        selfRad += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
						}
					/* self, CS->Cy */
						if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][5] * selfSValues[ICODE + 1][6] * selfSValues[ICODE + 1][6] * activityFractions[3] * activityFractions[3] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][6] = selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
                                                        selfRad += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
						}
					}
				}
                                double tempdose = 0;
				for (int ICODE = 0; ICODE < 11; ICODE++) {
					for (int j = 0; j < labelCell; j++) {
						/* dose that cell from each labled cell */
						x = celllabel[j][1] - cell[i][1];
						y = celllabel[j][2] - cell[i][2];
						z = celllabel[j][3] - cell[i][3];
						dist = Math.sqrt(x * x + y * y + z * z);
						if(dist == 0){
							continue;
						}

						if (dist > SValues[0].length - 1) {
							dist = SValues[0].length - 1;
						}

						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][5] * celllabel[j][5] * activityFractions[1]; /* cross, N->Cy */
						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][7] * celllabel[j][5] * activityFractions[2]; /* cross, Cy->Cy */
						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][6] * celllabel[j][5] * activityFractions[3]; /* cross, CS->Cy */
					}

					if (complexRadiobiologicalParams[ICODE][6] != 0 || complexRadiobiologicalParams[ICODE][7] != 0) {
						Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][6] * crossDoses[i][ICODE] - complexRadiobiologicalParams[ICODE][7] * crossDoses[i][ICODE] * crossDoses[i][ICODE]);
						totalRad += crossDoses[i][ICODE];
						if (cell[i][4] != 0) {
							labelRad += crossDoses[i][ICODE];
						}
					}
				tempdose += crossDoses[i][ICODE];
				}
                                cell[i][7] = tempdose;

				rand = random.nextDouble();
				if (rand >= Survival[i] || coldLiving[i]) {
					cell[i][0] = 0;
				} 
//                                else {
//					allLive++;
//					if (cell[i][4] != 0) {
//						labeledLive++;
//					}
//				}

				// update the progress bar
				percent = ((double) i / (double) cellnumber) * (double) jWidth;
				progress.clearRect(0, 0, jWidth, jHeight);
				progress.setColor(Color.GREEN);
				progress.fillRect(1, 1, (int) percent, 28);
				progress.setColor(Color.BLACK);
				progress.drawString("Part 2 of 2 : " + String.format("%1$.2f", percent / jWidth * 100) + "%", jWidth / 2 - 50, jHeight / 2 + 6);
			}
                        
                        //adding self and cross dose stats
                        double MeanABDSelf = selfRad / cellnumber;
                        double MeanABDSelfL = labelRadSelf / labelCell;
                        double MeanABDSelfUL = 0;
                        double MeanABDCross = (totalRad - selfRad) / cellnumber;
                        double MeanABDCrossL = (totalRad - selfRad) / labelCell;
                        double MeanABDCrossUL = 0;

			MeanABD = totalRad / cellnumber;
			MeanABDL = labelRad / labelCell;
			if ((cellnumber - labelCell) != 0) {
				MeanABDUL = (totalRad - labelRad) / (cellnumber - labelCell);
                                
                                MeanABDSelfUL = (selfRad - labelRadSelf) / (cellnumber - labelCell);
                                MeanABDCrossUL = (totalRad - selfRad) / (cellnumber - labelCell);
			}
			System.err.println("MeanABDUL = " + MeanABDUL);
			System.err.println("totalRad = " + totalRad);
			System.err.println("labelRad = " + labelRad);

			unlabeledLive = allLive - labeledLive;

			for (int i = cell.length - 1; i >= 0; i--) {
				activitytotal = activitytotal + cell[i][5];
			}

			progress.clearRect(0, 0, jWidth, jHeight);
			progress.setColor(Color.GREEN);
			progress.fillRect(1, 1, jWidth, jHeight);
			progress.setColor(Color.BLACK);
			progress.drawString("Plotting Data - please wait", jWidth / 2 - 50, jHeight / 2 + 6);

			double[][] totalDoses = new double[11][3]; // totalDoses[ICODE][mixed | labeled | unlabeled]
                        double[][] selfDoses = new double[11][3];
                        double[][] crossDose = new double[11][3];
                        
			for (int i = 0; i < cellnumber; i++) {
				for (int ICODE = 1; ICODE < 12; ICODE++) {
					totalDoses[ICODE - 1][0] += crossDoses[i][ICODE - 1]
							+ selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                        
                                        selfDoses[ICODE - 1][0] += selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                        crossDose[ICODE-1][0] += crossDoses[i][ICODE - 1];
					if(cell[i][4] != 0) {
						totalDoses[ICODE - 1][1] += crossDoses[i][ICODE - 1]
								+ selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
								+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
								+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                                
                                                selfDoses[ICODE - 1][1] += selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                                crossDose[ICODE-1][1] += crossDoses[i][ICODE - 1];
					}
					else {
						totalDoses[ICODE - 1][2] += crossDoses[i][ICODE - 1];
                                                
                                                selfDoses[ICODE - 1][2] = 0;
                                                crossDose[ICODE - 1][2] += crossDoses[i][ICODE - 1];
					}
				}
			}
                        
                        String output_self = "Self Absorbed Doses to cytoplasm (from decays anywhere within the same cell)\n";
			output_self += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
                        String output_cross = "Cross Absorbed Doses to cytoplasm (from decays anywhere within neighboring cells)\n";
			output_cross += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";

			for (double k = 0; k <= step; k++) {
				Arrays.fill(Survival, 1);
				allLive = 0;
				labeledLive = 0;
				unlabeledLive = 0;
				ratio = k / step;
                                TCP = 1;

				for (int i = 0; i < cellnumber; i++) {
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						/* self, N->Cy */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * activityFractions[1] * ratio * selfSValues[ICODE + 1][5] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * ratio * selfSValues[ICODE + 1][5] * cell[i][5] * activityFractions[1] * ratio * selfSValues[ICODE + 1][5] * cell[i][5] * activityFractions[1]);
						/* self, Cy->Cy */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * activityFractions[2] * ratio * selfSValues[ICODE + 1][7] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * ratio * selfSValues[ICODE + 1][7] * cell[i][5] * activityFractions[2] * ratio * selfSValues[ICODE + 1][7] * cell[i][5] * activityFractions[2]);
						/* self, Cs->Cy */
						if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * activityFractions[3] * ratio * selfSValues[ICODE + 1][6] * cell[i][5] - complexRadiobiologicalParams[ICODE][5] * ratio * selfSValues[ICODE + 1][6] * cell[i][5] * activityFractions[3] * ratio * selfSValues[ICODE + 1][6] * cell[i][5] * activityFractions[3]);
						/* cross, N->Cy & Cy->Cy & CS->Cy */
						if (complexRadiobiologicalParams[ICODE][6] != 0 || complexRadiobiologicalParams[ICODE][7] != 0)
							Survival[i] *= Math.exp(-1D * crossDoses[i][ICODE] * ratio * complexRadiobiologicalParams[ICODE][6] - complexRadiobiologicalParams[ICODE][7] * crossDoses[i][ICODE] * ratio * crossDoses[i][ICODE] * ratio);
					}
                                        TCP *= 1 - Survival[i];
					rand = random.nextDouble();
					if (rand < Survival[i] && !coldLiving[i]) {
						//allLive++;
						if (cell[i][4] == 0) {
							unlabeledLive++;
						} else {
							labeledLive++;
						}
					}
				}
                                
                                allLive = labeledLive + unlabeledLive;

				tempsurvall = (double) allLive / (double) cellnumber;
				if (labelCell != 0) {
					tempsurvlabel = (double) labeledLive / (double) labelCell;
				}
				if (unlableCell != 0) {
					tempsurvunlabel = (double) unlabeledLive / (double) unlableCell;
				}

				NumberFormat nf = new DecimalFormat("0.00E00");
				output += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABD * ratio) ) + "\t"
						+ nf.format(totalDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDL * ratio) + "\t"
						+ nf.format(totalDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDUL * ratio) + "\t"
						+ nf.format(totalDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_self += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDSelf * ratio) ) + "\t"
						+ nf.format(selfDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDSelfL * ratio) + "\t"
						+ nf.format(selfDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDSelfUL * ratio) + "\t"
						+ nf.format(selfDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_cross += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDCross * ratio) ) + "\t"
						+ nf.format(crossDose[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDCrossL * ratio) + "\t"
						+ nf.format(crossDose[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDCrossUL * ratio) + "\t"
						+ nf.format(crossDose[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";

				PlotOutput[0][PlotPoints] = (MAC * ratio);                          //MAC
				PlotOutput[1][PlotPoints] = (MeanABD * ratio);                      //MDC
				PlotOutput[2][PlotPoints] = (MAC * ratio * cellnumber / labelCell); //MALC
				PlotOutput[3][PlotPoints] = (MeanABDL * ratio);                     //MDLC
				PlotOutput[4][PlotPoints] = (MeanABDUL * ratio);                    //MDULC
				PlotOutput[5][PlotPoints] = tempsurvlabel;                          //SF of Labeled
				PlotOutput[6][PlotPoints] = tempsurvunlabel;                        //SF of unlabeled
				PlotOutput[7][PlotPoints] = tempsurvall;                            //SF of all
                                PlotOutput[8][PlotPoints] = TCP; 

				PlotPoints++;
			}
			totalDoses = null; // free up some memory space
                        output += "\n\n" + output_self +"\n\n" + output_cross;
                        output_self = null;
                        output_cross = null;
		}
		else if (radiationtarget == 3) {
			// radiation target: Nucleus & Cytoplasm.
                        
                        totalRad = 0;
                        labelRad = 0;
                        selfRad = 0;
                        labelRadSelf = 0;

			/* This section needs a second crossdoses array[][] to deal with the dosing of 2 separate targets*/
			double crossDoses2[][] = new double[cellnumber][11];
			double totalRad2 = 0;
			double labelRad2 = 0;
                        double selfRad2 = 0;
                        double labelRadSelf2 = 0;

			for (int i = 0; i < cellnumber; i++) {
			/* for each cell */
				if (cell[i][4] != 0) {
				/* if the cell is labeled calculate self dose exponential and decrease survival rate */
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						// the if statements below are to not do useless work
						/* self, N->N */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * selfSValues[ICODE + 1][2] * selfSValues[ICODE + 1][2] * activityFractions[1] * activityFractions[1] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][2] = selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
                                                        //System.out.print(i+" "+ (ICODE+1) + " N<-N " + cell[i][5] + " " + activityFractions[1]+ " " +selfDose[i][ICODE + 1][2] + " ");
                                                        selfRad += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
                                                        labelRadSelf += selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5];
                                                
						}
						/* self, Cy->N */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * selfSValues[ICODE + 1][3] * selfSValues[ICODE + 1][3] * activityFractions[2] * activityFractions[2] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][3] = selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
                                                        //System.out.print(i+" "+ (ICODE+1) + " N<-Cy " + cell[i][5] + " "+ activityFractions[2] + " "+selfDose[i][ICODE + 1][3] + " ");
                                                        selfRad += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
							labelRadSelf += selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5];
						}
						/* self, CS->N */
						if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][5] * selfSValues[ICODE + 1][4] * selfSValues[ICODE + 1][4] * activityFractions[3] * activityFractions[3] * cell[i][5] * cell[i][5]);
							totalRad += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
							labelRad += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][4] = selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
                                                        //System.out.print(i+" "+ (ICODE+1) + " N<-CS " + cell[i][5] + " "+ activityFractions[3] + " "+selfDose[i][ICODE + 1][4] + " ");
                                                        selfRad += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
							labelRadSelf += selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5];
						}
						/* self, N->Cy */ 
						if (complexRadiobiologicalParams[ICODE][6] != 0 || complexRadiobiologicalParams[ICODE][7] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][6] * selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5] - complexRadiobiologicalParams[ICODE][7] * selfSValues[ICODE + 1][5] * selfSValues[ICODE + 1][5] * activityFractions[1] * activityFractions[1] * cell[i][5] * cell[i][5]);
							totalRad2 += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
							labelRad2 += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][5] = selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
                                                        //System.out.print(i+" "+(ICODE+1) + " Cy<-N "+ cell[i][5] + " "+ activityFractions[1]+ " " +selfDose[i][ICODE + 1][5] + " ");
                                                        selfRad2 += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
							labelRadSelf2 += selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5];
						}
						/* self, Cy->Cy */
						if (complexRadiobiologicalParams[ICODE][8] != 0 || complexRadiobiologicalParams[ICODE][9] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][8] * selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][9] * selfSValues[ICODE + 1][7] * selfSValues[ICODE + 1][7] * activityFractions[2] * activityFractions[2] * cell[i][5] * cell[i][5]);
							totalRad2 += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
							labelRad2 += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][7] = selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
                                                        //System.out.print(i+" "+(ICODE+1) + " Cy<-Cy " + cell[i][5] + " "+ activityFractions[2]+ " "+selfDose[i][ICODE + 1][7] + " ");
                                                        selfRad2 += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
							labelRadSelf2 += selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5];
						}
						/* self, CS->Cy */
						if (complexRadiobiologicalParams[ICODE][10] != 0 || complexRadiobiologicalParams[ICODE][11] != 0) {
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][10] * selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][11] * selfSValues[ICODE + 1][6] * selfSValues[ICODE + 1][6] * activityFractions[3] * activityFractions[3] * cell[i][5] * cell[i][5]);
							totalRad2 += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
							labelRad2 += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
                                                        //jianchao 3/24/20
//                                                        selfDose[ICODE + 1][6] = selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
                                                        //System.out.println(i+" "+(ICODE+1) + " Cy<-CS "+ cell[i][5] + " "+ activityFractions[3]+ " " +selfDose[i][ICODE + 1][6] + " ");
                                                        selfRad2 += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
							labelRadSelf2 += selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5];
						}
					}
				}
                                double tempdose = 0;
				for (int ICODE = 0; ICODE < 11; ICODE++) {
					for (int j = 0; j < labelCell; j++) {
						/* dose that cell from each labled cell */
						x = celllabel[j][1] - cell[i][1];
						y = celllabel[j][2] - cell[i][2];
						z = celllabel[j][3] - cell[i][3];
						dist = Math.sqrt(x * x + y * y + z * z);
						if(dist == 0){
							continue;
						}

						if (dist > SValues[0].length - 1) {
							dist = SValues[0].length - 1;
						}

						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][2] * celllabel[j][5] * activityFractions[1]; /* cross, N->N */
						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][3] * celllabel[j][5] * activityFractions[2]; /* cross, Cy->N */
						crossDoses[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][4] * celllabel[j][5] * activityFractions[3]; /* cross, CS->N */

						crossDoses2[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][5] * celllabel[j][5] * activityFractions[1]; /* cross, N->Cy */
						crossDoses2[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][7] * celllabel[j][5] * activityFractions[2]; /* cross, Cy->Cy */
						crossDoses2[i][ICODE] += SValues[ICODE + 1][(int)dist - 2 * rCell][6] * celllabel[j][5] * activityFractions[3]; /* cross, CS->Cy */

                                                // jianchao wang for testing 
                                                //System.out.print(i+" "+(ICODE+1) + " " +crossDoses[i][ICODE] + " ");
                                                //System.out.print(crossDoses2[i][ICODE] + " ");
                                                //System.out.println(cell[i][7]);
					}

					if (complexRadiobiologicalParams[ICODE][12] != 0 || complexRadiobiologicalParams[ICODE][13] != 0) {
						Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][12] * crossDoses[i][ICODE] - complexRadiobiologicalParams[ICODE][13] * crossDoses[i][ICODE] * crossDoses[i][ICODE]);
						totalRad += crossDoses[i][ICODE];
						if (cell[i][4] != 0) {
							labelRad += crossDoses[i][ICODE];
						}
					}
					if (complexRadiobiologicalParams[ICODE][14] != 0 || complexRadiobiologicalParams[ICODE][15] != 0) {
						Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][14] * crossDoses2[i][ICODE] - complexRadiobiologicalParams[ICODE][15] * crossDoses2[i][ICODE] * crossDoses2[i][ICODE]);
						totalRad2 += crossDoses2[i][ICODE];
						if (cell[i][4] != 0) {
							labelRad2 += crossDoses2[i][ICODE];
						}
					}
				tempdose += crossDoses[i][ICODE] + crossDoses2[i][ICODE];
				}
                                cell[i][7] = tempdose;
                                
				rand = random.nextDouble();
				if (rand >= Survival[i] || coldLiving[i]) {
					cell[i][0] = 0;
				} 
//                                else {
//					allLive++;
//					if (cell[i][4] != 0) {
//						labeledLive++;
//					}
//				}

				// update the progress bar
				percent = ((double) i / (double) cellnumber) * (double) jWidth;
				progress.clearRect(0, 0, jWidth, jHeight);
				progress.setColor(Color.GREEN);
				progress.fillRect(1, 1, (int) percent, 28);
				progress.setColor(Color.BLACK);
				progress.drawString("Part 2 of 2 : " + String.format("%1$.2f", percent / jWidth * 100) + "%", jWidth / 2 - 50, jHeight / 2 + 6);
			}
                        //jianchao 4/24/19 adding MeanABD for self/cross dose for nucleus/cyto
                        double MeanABDSelf = selfRad / cellnumber; //self nucleus
                        double MeanABDSelf2 = selfRad2 / cellnumber; //self cyto
                        double MeanABDCross = (totalRad - selfRad) / cellnumber; //cross nucleus
                        double MeanABDCross2 = (totalRad2 - selfRad2) / cellnumber; //cross cyto
                        double MeanABDLSelf = selfRad / labelCell; //self nucleus labled
                        double MeanABDLSelf2 = selfRad2 / labelCell; //self cyto labled
                        double MeanSelfABDUL = 0; //self nucleus unlabled
                        double MeanSelfABDUL2 = 0; //self cyto unlabled
                        double MeanABDCrossL = (totalRad - selfRad) / labelCell; // cross nucleus labeled
                        double MeanABDCrossUL = 0; //cross nucleus unlabeled
                        double MeanABDCrossL2 = (totalRad2 - selfRad2) / labelCell; // cross cyto labeled 
                        double MeanABDCrossUL2 = 0;// cross cyto unlabeled 

			MeanABD = totalRad / cellnumber;
			double MeanABD2 = totalRad2 / cellnumber;
			MeanABDL = labelRad / labelCell;
			double MeanABDL2 = labelRad2 / labelCell;
			double MeanABDUL2 = 0;
			if ((cellnumber - labelCell) != 0) {
				MeanABDUL = (totalRad - labelRad) / (cellnumber - labelCell);
				MeanABDUL2 = (totalRad2 - labelRad2) / (cellnumber - labelCell);
                                MeanSelfABDUL = (selfRad - labelRadSelf) / (cellnumber - labelCell);
                                MeanSelfABDUL2 = (selfRad2 - labelRadSelf2) / (cellnumber - labelCell);
                                MeanABDCrossUL = (totalRad - selfRad) / (cellnumber - labelCell);
                                MeanABDCrossUL2 = (totalRad2 - selfRad2) / (cellnumber - labelCell);
			}
			System.err.println("MeanABDUL = " + MeanABDUL);
			System.err.println("totalRad = " + totalRad);
			System.err.println("labelRad = " + labelRad);

			unlabeledLive = allLive - labeledLive;

			for (int i = cell.length - 1; i >= 0; i--) {
				activitytotal = activitytotal + cell[i][5];
			}

			progress.clearRect(0, 0, jWidth, jHeight);
			progress.setColor(Color.GREEN);
			progress.fillRect(1, 1, jWidth, jHeight);
			progress.setColor(Color.BLACK);
			progress.drawString("Plotting Data - please wait", jWidth / 2 - 50, jHeight / 2 + 6);

			double[][] totalDoses = new double[11][3]; // totalDoses[ICODE][mixed | labeled | unlabeled]
			double[][] totalDoses2 = new double[11][3]; // totalDoses[ICODE][mixed | labeled | unlabeled]
                        // jianchao 4/24/20 added variable to seperate output self and cross dose
                        double[][] selfDoses = new double[11][3];
                        double[][] selfDoses2 = new double[11][3];
                        double[][] crossDose = new double[11][3];
                        double[][] crossDose2 = new double[11][3];
			for (int i = 0; i < cellnumber; i++) {
				for (int ICODE = 1; ICODE < 12; ICODE++) {
					totalDoses[ICODE - 1][0] += crossDoses[i][ICODE - 1]
							+ selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
                                        selfDoses[ICODE - 1][0] += selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
                                        crossDose[ICODE -1][0] += crossDoses[i][ICODE - 1];
                                        
					totalDoses2[ICODE - 1][0] += crossDoses2[i][ICODE - 1]
							+ selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                        selfDoses2[ICODE - 1][0] += selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                        crossDose2[ICODE -1][0] += crossDoses2[i][ICODE - 1];
					if(cell[i][4] != 0) {
						totalDoses[ICODE - 1][1] += crossDoses[i][ICODE - 1]
								+ selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
								+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
								+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
						totalDoses2[ICODE - 1][1] += crossDoses2[i][ICODE - 1]
								+ selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
								+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
								+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                                
                                                selfDoses[ICODE - 1][1] += selfSValues[ICODE][2] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][3] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][4] * activityFractions[3] * cell[i][5];
                                                selfDoses2[ICODE - 1][1] += selfSValues[ICODE][5] * activityFractions[1] * cell[i][5]
							+ selfSValues[ICODE][7] * activityFractions[2] * cell[i][5]
							+ selfSValues[ICODE][6] * activityFractions[3] * cell[i][5];
                                                crossDose[ICODE -1][1] += crossDoses[i][ICODE - 1];
                                                crossDose2[ICODE -1][1] += crossDoses2[i][ICODE - 1];
                                        }
					else {
						totalDoses[ICODE - 1][2] += crossDoses[i][ICODE - 1];
						totalDoses2[ICODE - 1][2] += crossDoses2[i][ICODE - 1];
                                                
                                                selfDoses[ICODE - 1][2] = 0.0;
                                                selfDoses2[ICODE - 1][2] = 0.0;
                                                crossDose[ICODE -1][2] += crossDoses[i][ICODE - 1];
                                                crossDose2[ICODE -1][2] += crossDoses2[i][ICODE - 1];
					}
				}
			}

			output = "Absorbed Doses to the nucleus and cytoplasm are shown seperately when using both as the target region.\n";
			output += "Absorbed Doses to nucleus\n";
			output += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
			String output2 = "Absorbed Doses to cytoplasm\n";
			output2 += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
                        String output_self = "Self Absorbed Doses to nucleus (from decays anywhere within the same cell)\n";
			output_self += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
                        String output_cross = "Cross Absorbed Doses to nucleus (from decays anywhere within neighboring cells)\n";
			output_cross += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
                        String output_self2 = "Self Absorbed Doses to cytoplasm (from decays anywhere within the same cell)\n";
			output_self2 += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";
                        String output_cross2 = "Cross Absorbed Doses to cytoplasm (from decays anywhere within neighboring cells)\n";
			output_cross2 += "MAC(Bq)\tMDC(all)\tMDC(γ)\tMDC(X)\tMDC(AQ)\tMDC(β+)\tMDC(β-)\tMDC(IE)\tMDC(Auger)\tMDC(α)\tMDC(αR)\tMDC(FF)\tMDC(N)\tMALC(Bq)\tMDLC(all)\tMDLC(γ)\tMDLC(X)\tMDLC(AQ)\tMDLC(β+)\tMDLC(β-)\tMDLC(IE)\tMDLC(Auger)\tMDLC(α)\tMDLC(αR)\tMDLC(FF)\tMDLC(N)\t MDUC(all)\tMDUC(γ)\tMDUC(X)\tMDUC(AQ)\tMDUC(β+)\tMDUC(β-)\tMDUC(IE)\tMDUC(Auger)\tMDUC(α)\tMDUC(αR)\tMDUC(FF)\tMDUC(N)\tSF(labeled)\tSF(unlabeled)\tSF(all cells)" + "\n";

			for (double k = 0; k <= step; k++) {
				Arrays.fill(Survival, 1);
				allLive = 0;
				labeledLive = 0;
				unlabeledLive = 0;
				ratio = k / step;
                                TCP = 1;

				for (int i = 0; i < cellnumber; i++) {
					for (int ICODE = 0; ICODE < 11; ICODE++) {
						/* self, N->N */
						if (complexRadiobiologicalParams[ICODE][0] != 0 || complexRadiobiologicalParams[ICODE][1] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][0] * ratio * selfSValues[ICODE + 1][2] * activityFractions[1] * cell[i][5] - complexRadiobiologicalParams[ICODE][1] * ratio * ratio * selfSValues[ICODE + 1][2] * selfSValues[ICODE + 1][2] * activityFractions[1] * activityFractions[1] * cell[i][5] * cell[i][5]);
						/* self, Cy->N */
						if (complexRadiobiologicalParams[ICODE][2] != 0 || complexRadiobiologicalParams[ICODE][3] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][2] * ratio * selfSValues[ICODE + 1][3] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][3] * ratio * ratio * selfSValues[ICODE + 1][3] * selfSValues[ICODE + 1][3] * activityFractions[2] * activityFractions[2] * cell[i][5] * cell[i][5]);
						/* self, CS->N */
						if (complexRadiobiologicalParams[ICODE][4] != 0 || complexRadiobiologicalParams[ICODE][5] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][4] * ratio * selfSValues[ICODE + 1][4] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][5] * ratio * ratio * selfSValues[ICODE + 1][4] * selfSValues[ICODE + 1][4] * activityFractions[3] * activityFractions[3] * cell[i][5] * cell[i][5]);
						/* self, N->Cy */
						if (complexRadiobiologicalParams[ICODE][6] != 0 || complexRadiobiologicalParams[ICODE][7] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][6] * ratio * selfSValues[ICODE + 1][5] * activityFractions[1] * cell[i][5] - complexRadiobiologicalParams[ICODE][7] * ratio * ratio * selfSValues[ICODE + 1][5] * selfSValues[ICODE + 1][5] * activityFractions[1] * activityFractions[1] * cell[i][5] * cell[i][5]);
						/* self, Cy->Cy */
						if (complexRadiobiologicalParams[ICODE][8] != 0 || complexRadiobiologicalParams[ICODE][9] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][8] * ratio * selfSValues[ICODE + 1][7] * activityFractions[2] * cell[i][5] - complexRadiobiologicalParams[ICODE][9] * ratio * ratio * selfSValues[ICODE + 1][7] * selfSValues[ICODE + 1][7] * activityFractions[2] * activityFractions[2] * cell[i][5] * cell[i][5]);
						/* self, CS->Cy */
						if (complexRadiobiologicalParams[ICODE][10] != 0 || complexRadiobiologicalParams[ICODE][11] != 0)
							Survival[i] *= Math.exp(-1D * complexRadiobiologicalParams[ICODE][10] * ratio * selfSValues[ICODE + 1][6] * activityFractions[3] * cell[i][5] - complexRadiobiologicalParams[ICODE][11] * ratio * ratio * selfSValues[ICODE + 1][6] * selfSValues[ICODE + 1][6] * activityFractions[3] * activityFractions[3] * cell[i][5] * cell[i][5]);
						/* cross, N->N & Cy->N & CS->N */
						if (complexRadiobiologicalParams[ICODE][12] != 0 || complexRadiobiologicalParams[ICODE][13] != 0)
							Survival[i] *= Math.exp(-1D * crossDoses[i][ICODE] * ratio * complexRadiobiologicalParams[ICODE][12] - complexRadiobiologicalParams[ICODE][13] * crossDoses[i][ICODE] * crossDoses[i][ICODE] * ratio * ratio);
						/* cross, N->Cy & Cy->Cy & CS->Cy */
						if (complexRadiobiologicalParams[ICODE][14] != 0 || complexRadiobiologicalParams[ICODE][15] != 0)
							Survival[i] *= Math.exp(-1D * crossDoses2[i][ICODE] * ratio * complexRadiobiologicalParams[ICODE][14] - complexRadiobiologicalParams[ICODE][15] * crossDoses2[i][ICODE] * crossDoses2[i][ICODE] * ratio * ratio);
					}
                                        TCP *= 1 - Survival[i];
					rand = random.nextDouble();
					if (rand < Survival[i] && !coldLiving[i]) {
						allLive++;
						if (cell[i][4] == 0) {
							unlabeledLive++;
						} else {
							labeledLive++;
						}
					}
				}
                                
                                allLive = labeledLive + unlabeledLive;

				tempsurvall = (double) allLive / (double) cellnumber;
				if (labelCell != 0) {
					tempsurvlabel = (double) labeledLive / (double) labelCell;
				}
				if (unlableCell != 0) {
					tempsurvunlabel = (double) unlabeledLive / (double) unlableCell;
				}

				NumberFormat nf = new DecimalFormat("0.00E00");
				output += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABD * ratio) ) + "\t"
						+ nf.format(totalDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDL * ratio) + "\t"
						+ nf.format(totalDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDUL * ratio) + "\t"
						+ nf.format(totalDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                //Jianchao 4/23/20 adding self-dose to the output
                                output_self += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDSelf * ratio) ) + "\t"
						+ nf.format(selfDoses[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDLSelf * ratio) + "\t"
						+ nf.format(selfDoses[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanSelfABDUL * ratio) + "\t"
						+ nf.format(selfDoses[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                output_cross += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDCross * ratio) ) + "\t"
						+ nf.format(crossDose[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDCrossL * ratio) + "\t"
						+ nf.format(crossDose[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDCrossUL * ratio) + "\t"
						+ nf.format(crossDose[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";

				output2 += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABD2 * ratio) ) + "\t"
						+ nf.format(totalDoses2[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDL2 * ratio) + "\t"
						+ nf.format(totalDoses2[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDUL2 * ratio) + "\t"
						+ nf.format(totalDoses2[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(totalDoses2[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_self2 += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDSelf2 * ratio) ) + "\t"
						+ nf.format(selfDoses2[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDLSelf2 * ratio) + "\t"
						+ nf.format(selfDoses2[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanSelfABDUL2 * ratio) + "\t"
						+ nf.format(selfDoses2[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(selfDoses2[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";
                                
                                output_cross2 += ""
						+ nf.format( (MAC * ratio) ) + "\t"
						+ nf.format( (MeanABDCross2 * ratio) ) + "\t"
						+ nf.format(crossDose2[0][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[1][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[2][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[3][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[4][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[5][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[6][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[7][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[8][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[9][0] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[10][0] / cellnumber * ratio ) + "\t"
						+ nf.format(MAC * ratio * cellnumber / labelCell) + "\t"
						+ nf.format(MeanABDCrossL2 * ratio) + "\t"
						+ nf.format(crossDose2[0][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[1][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[2][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[3][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[4][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[5][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[6][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[7][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[8][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[9][1] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[10][1] / cellnumber * ratio ) + "\t"
						+ nf.format(MeanABDCrossUL2 * ratio) + "\t"
						+ nf.format(crossDose2[0][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[1][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[2][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[3][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[4][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[5][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[6][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[7][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[8][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[9][2] / cellnumber * ratio ) + "\t"
						+ nf.format(crossDose2[10][2] / cellnumber * ratio ) + "\t"
						+ nf.format(tempsurvlabel) + "\t"
						+ nf.format(tempsurvunlabel) + "\t"
						+ nf.format(tempsurvall) + "\n";

				PlotOutput[0][PlotPoints] = (MAC * ratio);                          //MAC
				PlotOutput[1][PlotPoints] = (MeanABD * ratio);                      //MDC
				PlotOutput[2][PlotPoints] = (MAC * ratio * cellnumber / labelCell); //MALC
				PlotOutput[3][PlotPoints] = (MeanABDL * ratio);                     //MDLC
				PlotOutput[4][PlotPoints] = (MeanABDUL * ratio);                    //MDULC
				PlotOutput[5][PlotPoints] = (tempsurvlabel);                        //SF of Labeled
				PlotOutput[6][PlotPoints] = (tempsurvunlabel);                      //SF of unlabeled
				PlotOutput[7][PlotPoints] = (tempsurvall);                          //SF of all
                                PlotOutput[8][PlotPoints] = TCP; 

				PlotPoints++;
			}
			totalDoses = null; // free up some memory space
			output += "\n\n" + output_self +"\n\n" + output_cross +"\n\n" + output2 +"\n\n"+ output_self2+"\n\n" + output_cross2;
			output2 = null;
                        output_self = null;
                        output_cross = null;
                        output_self2 = null;
                        output_cross2 = null;
		}
		return cell;
	}
}
