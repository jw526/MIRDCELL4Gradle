package GUI;

import java.awt.Graphics;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SurvivalCalculation {

	public double activitytotal;
	public double tempsurvall, tempsurvlabel, tempsurvunlabel;
	public double[][] PlotOutput;
        public double[][] selfDose;
        public boolean[] alive;
	double SurvFrac;
        Random randomgen;
	private final double step = 100D; // change this number to make the surviving fraction curve more accurate / have a more fine step ratio

	public SurvivalCalculation(){
		activitytotal = 0;
		tempsurvall = 0;
		tempsurvlabel = 0;
		tempsurvunlabel = 0;
		PlotOutput = new double[9][(int)step + 1];
		SurvFrac = 0;
                randomgen = new Random();
	}

	public double[][] calculateSurvival(
			double[][] cell, double[][]celllabel, double[][][] SValues, double[] activityFractions,
			double CrossAlpha, double CrossBeta, double SelfAlpha, double SelfBeta, double MAC, double rCell,
			int cellnumber, int DistSq, int jheight, int jwidth, int llengthSq, int radiationtarget,
	        Graphics progress
	){
		double d1, labelRad = 0.0, MeanABD, MeanABDL, MeanABDUL = 0.0, percent, rand, ratio, SurvFrac = 0, Survival, totalRad = 0.0, x, y, z;
		int lablength = celllabel.length-1;
		int labelcell = celllabel.length;
		int unlabelcell = cellnumber - labelcell;
		int length = cell.length - 1;
		int alllive, labellive, LiveCell = 0, PlotPoints = 0, unlabellive;
		

		progress.clearRect( 0, 0, jwidth, jheight );
		progress.setColor( Color.BLACK );
		progress.drawString( "Part(2/2) : 0%", jwidth / 2 - 50, jheight / 2 + 6 );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, 5, jheight );
                
                //System.err.println("############");

		for(int i = cellnumber - 1; i >= 0  ; i--) {
			for(int j = celllabel.length - 1; j >= 0; j--) {
				// get the distance between the cells
				x = celllabel[j][1] - cell[i][1];
				y = celllabel[j][2] - cell[i][2];
				z = celllabel[j][3] - cell[i][3];
				d1 = x * x + y * y + z * z;
                                
                                //Jianchao 2/21/20 for testing distance between cells
                                //System.out.println ( i+ " " +j + " " + x +" " + y + " " + z + " "+ Math.sqrt(d1));
                                //SValues[a][b][c]: a=0 is the sum of radiation for all ICODE, b is the distance for cross-dose, c is the src-tgt
                                
				if(d1 >= DistSq && d1 <= SValues[0].length * SValues[0].length) {  
					// Alex Rosen 7/27/2017
					// the following if statements are to make the old model work with the the new layout requested by dr howell
					if(radiationtarget == 0){
						// Radiation Target: Cell Jianchao Wang 9/6/18
//                                                cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][0]) * celllabel[j][5] * activityFractions[2];  /* C->C */
//						cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][1]) * celllabel[j][5] * activityFractions[3];  /* CS->C */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][0]) * celllabel[j][5] * activityFractions[2];  /* C->C */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][1]) * celllabel[j][5] * activityFractions[3];  /* CS->C */

					}
					else if(radiationtarget == 1){
						// Radiation Target: Nucleus
//                                                cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][2]) * celllabel[j][5] * activityFractions[1];  /* N->N */
//						cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][3]) * celllabel[j][5] * activityFractions[2];  /* Cy->N */
//						cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][4]) * celllabel[j][5] * activityFractions[3];  /* CS->N */
                                                
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][2]) * celllabel[j][5] * activityFractions[1];  /* N->N */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][3]) * celllabel[j][5] * activityFractions[2];  /* Cy->N */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][4]) * celllabel[j][5] * activityFractions[3];  /* CS->N */
					}
					else if(radiationtarget == 2){
						// Radiation Target: Cytoplasm
//                                                cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][5]) * celllabel[j][5] * activityFractions[1];  /* N->Cy */
//						cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][7]) * celllabel[j][5] * activityFractions[2];  /* Cy->Cy */
//						cell[i][7] += (SValues[0][(int) Math.sqrt(d1) - 1][6]) * celllabel[j][5] * activityFractions[3];  /* CS->Cy */
                                                
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][5]) * celllabel[j][5] * activityFractions[1];  /* N->Cy */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][7]) * celllabel[j][5] * activityFractions[2];  /* Cy->Cy */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][6]) * celllabel[j][5] * activityFractions[3];  /* CS->Cy */
					}
					else if(radiationtarget == 3){
						// Radiation Target: Nucleus & Cytoplasm
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][2]) * celllabel[j][5] * activityFractions[1];  /* N->N */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][3]) * celllabel[j][5] * activityFractions[2];  /* Cy->N */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][4]) * celllabel[j][5] * activityFractions[3];  /* CS->N */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][5]) * celllabel[j][5] * activityFractions[1];  /* N->Cy */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][7]) * celllabel[j][5] * activityFractions[2];  /* Cy->Cy */
						cell[i][7] += (SValues[0][(int) (Math.sqrt(d1) - 2 * rCell)][6]) * celllabel[j][5] * activityFractions[3];  /* CS->Cy */

					}
				}
                                //System.out.println("cell[i][5]: " + cell[j][5]); //activity
			}
                        //System.out.println("2rCell " + 2*rCell); 
                        
                        // cell[cellID][7]: cross-dose; cell[cellID][6]: self-dose
                        totalRad +=  cell[i][7] + cell[i][6];
                        //System.out.println("total rad: " + totalRad);
//			totalRad = totalRad + cell[i][7] + cell[i][6];
			if(cell[i][4] == 1) {
				labelRad += cell[i][7] + cell[i][6];
			}

			// update the progress bar
			percent = (1 - (double) i / (double) length) * (double) jwidth;
			progress.clearRect( 0, 0, jwidth, jheight );
			progress.setColor( Color.GREEN );
			progress.fillRect( 1, 1, (int) percent, 28 );
			progress.setColor( Color.BLACK );
			progress.drawString( "Part(2/2) : " + String.format( "%1$.1f", percent / jwidth * 100 ) + "%", jwidth / 2 - 50, jheight / 2 + 6 );
		}

		MeanABD = totalRad / cellnumber;
		MeanABDL = labelRad / labelcell;
		if( (cellnumber-labelcell) != 0 ) {
			MeanABDUL = (totalRad - labelRad) / (cellnumber - labelcell);
		}
		
		boolean[] coldLiving = new boolean[cellnumber];
		for(int i = 0; i < cellnumber; i++){
			if(cell[i][0] != 1){
				coldLiving[i] = true;
				cell[i][0] = 1;
			}
			else{
				coldLiving[i] = false;
			}
		}
		
		// for each cell calculate its survival chance and then whether it survived or not
		for(int i = 0; i < cellnumber; i++) {
			Survival = Math.exp( -1D * cell[i][7] * CrossAlpha - cell[i][7] * cell[i][7] * CrossBeta ) * Math.exp( -1D * cell[i][6] * SelfAlpha - cell[i][6] * cell[i][6] * SelfBeta );
			rand = randomgen.nextDouble();
			if(rand >= Survival  || coldLiving[i]) {
				cell[i][0] = 0;
			}
			else {
				LiveCell++;
			}
		}

		progress.clearRect( 0, 0, jwidth, jheight );
		progress.setColor( Color.GREEN );
		progress.fillRect( 1, 1, jwidth, jheight );
		progress.setColor( Color.BLACK );
		progress.drawString( "Plotting Data - please wait", jwidth / 2 - 50, jheight / 2 + 6 );

		for(int i = cell.length - 1; i >= 0; i--) {
			activitytotal += cell[i][5];
		}


		for(double k = 0; k <= step; k++) {
			alllive = 0;
			labellive = 0;
			unlabellive = 0;
			ratio = k / step;
			for(int i = 0; i < cellnumber; i++) {
				Survival = Math.exp( -1.0 * cell[i][7] * ratio * CrossAlpha - (cell[i][7] * ratio) * (cell[i][7] * ratio) * CrossBeta ) * Math.exp( -1.0 * cell[i][6] * ratio * SelfAlpha - (cell[i][6] * ratio) * (cell[i][6] * ratio) * SelfBeta );
				rand = randomgen.nextDouble();
				if(rand < Survival && !coldLiving[i]) {
					//alllive++;
					if(cell[i][4] == 0){
						unlabellive++;
					}
					else{
						labellive++;
					}
				}
			}
			alllive = labellive + unlabellive;

			tempsurvall = ((double) alllive / (double) cellnumber);
			if(labelcell != 0) {
				tempsurvlabel = ((double) labellive / (double) labelcell);
			}
			if(unlabelcell != 0) {
				tempsurvunlabel = ((double) unlabellive / (double) unlabelcell);
			}

			PlotOutput[0][PlotPoints] = (MAC * ratio);                          //MAC
			PlotOutput[1][PlotPoints] = (MeanABD * ratio);                      //MDC
			PlotOutput[2][PlotPoints] = (MAC * ratio * cellnumber / labelcell); //MALC
			PlotOutput[3][PlotPoints] = (MeanABDL * ratio);                     //MDLC
			PlotOutput[4][PlotPoints] = (MeanABDUL * ratio);                    //MDULC
			PlotOutput[5][PlotPoints] = (tempsurvlabel);                        //SF of Labeled
			PlotOutput[6][PlotPoints] = (tempsurvunlabel);                      //SF of unlabeled
			PlotOutput[7][PlotPoints] = (tempsurvall);                          //SF of all

			//System.err.println(PlotOutput[1][PlotPoints]);
                        PlotPoints++;
                        
                        
		}
		return cell;
	}
}
