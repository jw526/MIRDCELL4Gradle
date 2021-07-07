/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Activity;

import java.util.Random;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * @author Jianchao Wang
 * 6/24/20
 */
public class Activity {
    private static void labelCells(boolean NECROTIC, boolean DEAD_NECROTIC,
			int cellnumber, int Height, int labelcellnum, int Radius, double shellWidth,
			String Shape, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
			//double AccuActivity,
			double[][] cell) {
		int TMC = 0, j;
		double rToCell;
		Random randomgen = new Random();
		if(NECROTIC){
			
			
			double innershort = Radius / 2.0 - shellWidth;
			double innerlong = Height / 2.0 - shellWidth;
			
			//constants needed for cone
			final double hypot = Math.sqrt(Radius * Radius + Height * Height);      // the length of the angled edge of the cone
			double rToTop = 0, rAtH = 0, h = 0;
			double distFromEdge;
			final double COM = 3.0 * Height / 4.0;
			
			for(int i = 0; i < labelcellnum; i++){
				j = randomgen.nextInt(cellnumber);
				
				// is the cell labelable?
				if(Shape.toLowerCase().equals("sphere")){
					rToCell = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][2], 2) + Math.pow(cell[j][3], 2));
                                        //cell is already labeled or in the cold zone
					if(cell[j][4] != 0 || rToCell <= (Radius - shellWidth)){
						i--;
						TMC++;
						if(TMC == cellnumber * 3){
							JOptionPane.showMessageDialog(null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE);
                                                        resetFlag[0] = true;
                                                        resetLabelCellNum[0] = i;
                                                        //label everything in the hot shell
//                                                        IntStream.range(0, cellnumber).parallel().forEach(n -> {
//                                                            double r2c = Math.sqrt(Math.pow(cell[n][1], 2) + Math.pow(cell[n][2], 2) + Math.pow(cell[n][3], 2));
//                                                            if (r2c <= Radius - shellWidth) {
//                                                               cell[n][4] = 0;
//                                                            }
//                                                            else {
//                                                               cell[n][4] = 1;
//                                                            }
//                                                        });
                                                        return;
						}
						continue;
					}
				}
				else if(Shape.toLowerCase().equals("rod")){
					rToCell = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][3], 2));
					rToTop = Math.min(-cell[j][2], Height + cell[j][2]);
					if(cell[j][4] != 0 || (rToCell <= (Radius - shellWidth) && -cell[j][2] <= (Height - shellWidth) && -cell[j][2] >= shellWidth)){						
						i--;
						TMC++;
						if(TMC == cellnumber * 3){
							JOptionPane.showMessageDialog(null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE);
                                                        resetFlag[0] = true;
                                                        resetLabelCellNum[0] = i;
                                                        //label everything in the hot shell
//                                                        IntStream.range(0, cellnumber).parallel().forEach(n -> {
//                                                            double r2c = Math.sqrt(Math.pow(cell[n][1], 2) + Math.pow(cell[n][3], 2));
//                                                            //double r2Top = Math.min(-cell[n][2], Height + cell[n][2]);
//                                                            if ((r2c <= (Radius - shellWidth) && -cell[n][2] <= (Height - shellWidth) && -cell[n][2] >= shellWidth)) {
//                                                               cell[n][4] = 0;
//                                                            }
//                                                            else {
//                                                               cell[n][4] = 1;
//                                                            }
//                                                        });
                                                        return;
						}
						continue;
					}
				}
				else if(Shape.toLowerCase().equals("ellipsoid")){
					rToCell = Math.sqrt(Math.pow(cell[j][1], 2) / Math.pow(innershort, 2) + Math.pow(cell[j][2], 2) / Math.pow(innerlong, 2) + Math.pow(cell[j][3], 2) / Math.pow(innershort, 2));
					if(cell[j][4] != 0 || rToCell < 1){
						i--;
						TMC++;
						if(TMC == cellnumber * 3){
							JOptionPane.showMessageDialog(null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE);
                                                        resetFlag[0] = true;
                                                        resetLabelCellNum[0] = i;
							return;
						}
						continue;
					}
				}
				else if(Shape.toLowerCase().equals("cone")){
					rToCell = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][3], 2));
					h = -cell[j][2];
					rAtH = (double) Radius / (double) Height * h;
					double de = (h*Radius - Height * rToCell )/ Math.sqrt(Height*Height + Radius*Radius);				
					if(cell[j][4] != 0 || (de >= shellWidth && h <= (Height - shellWidth) && h >= shellWidth)){
						i--;
						TMC++;
						if(TMC == cellnumber * 3){
							JOptionPane.showMessageDialog(null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE);
                                                        resetFlag[0] = true;
                                                        resetLabelCellNum[0] = i;
							return;
						}
						continue;
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "Unfortunately Necrotic Geometry is not yet implemented for this shape yet. Sorry for the inconvenience.", "We're Sorry", JOptionPane.WARNING_MESSAGE);
				}
				TMC = 0;
				
				//label the cell
				cell[j][4] = 1; // the cell is labeled

			}
					
			if(DEAD_NECROTIC){
				for(int i = 0; i < cellnumber; i++){
					if(Shape.toLowerCase().equals("sphere")){
						rToCell = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][2], 2) + Math.pow(cell[i][3], 2));
						if(rToCell <= (Radius - shellWidth)){
							cell[i][0] = 0; // label this cell dead
							continue;
						}
					}
					else if(Shape.toLowerCase().equals("rod")){
						rToCell = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][3], 2));
                                                if(rToCell <= (Radius - shellWidth) && -cell[i][2] <= (Height - shellWidth) && -cell[i][2] >= shellWidth){
							cell[i][0] = 0;
							continue;
						}
					}
					else if(Shape.toLowerCase().equals("ellipsoid")){
						innershort = Radius - shellWidth;
						innerlong = Height - shellWidth;
						rToCell = Math.sqrt(Math.pow(cell[i][1], 2) / Math.pow(innershort, 2) + Math.pow(cell[i][2], 2) / Math.pow(innerlong, 2) + Math.pow(cell[i][3], 2) / Math.pow(innershort, 2));
						if(rToCell < 1){
							cell[i][0] = 0;
							continue;
						}
					} else if(Shape.toLowerCase().equals("cone")){ //JCW 6/18/2020
                                                h = -cell[i][2];
                                                //rAtH = (double) Radius / (double) Height * h;
                                                rToCell = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][3], 2));
                                                double de = (h*Radius - Height * rToCell )/ Math.sqrt(Height*Height + Radius*Radius);				
                                                if(de >= shellWidth && h <= (Height - shellWidth) && h >= shellWidth){
                                                    cell[i][0] = 0;
                                                }
                                        }
				}
			}
						
		}
                else{
                    for(int i = 0; i < labelcellnum; i++){
			j = randomgen.nextInt(cellnumber);
                        if(cell[j][4] != 0){
                                i--;
                                continue;
                        }
                        cell[j][4] = 1; // this cell is labeled
                    }
                }
    }
    
     public static double[][] generateNormalActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
            int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
            String Shape,
            double MeanActivity, double ShapeFactor, double Tau, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
            double[][] cell) {

            labelCells(NECROTIC, DEAD_NECROTIC, cellnumber, Height, labelcellnum, Radius, shellWidth, Shape, coldcellnumber, resetFlag, resetLabelCellNum, cell);                        
            double sum1 = 0.0;
            NormalDistribution a = new NormalDistribution(0, ShapeFactor);
            double[] NormalD = new double[cellnumber];
            for(int i = 0; i < cellnumber; i++){
                if(cell[i][4] == 1){
                    NormalD[i] = a.sample();
                    if ((NormalD[i] + MeanActivity) < 0) {
                        JOptionPane.showMessageDialog( null, "Negative activity value, please decrease value of standard deviation", "error", JOptionPane.WARNING_MESSAGE );
                        break;
                    } else {
                        cell[i][5] = MeanActivity + NormalD[i];// this is the activity assigned to the cell
                    }    
                    sum1 += cell[i][5];
                }       
            }
		
            for(int i = 0; i < cellnumber; i++){
                    cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;
            }

            return cell;        
    }
     
     public static double[][] generateLogNormalActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
            int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
            String Shape,
            double MeanActivity, double ShapeFactor, double Tau, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
            double[][] cell) {
        
            labelCells(NECROTIC, DEAD_NECROTIC, cellnumber, Height, labelcellnum, Radius, shellWidth, Shape, coldcellnumber, resetFlag, resetLabelCellNum, cell );           
            double sum1 = 0.0;
            double sclaeFactor = Math.log( MeanActivity ) - Math.pow( ShapeFactor, 2 ) / 2;
            LogNormalDistribution a = new LogNormalDistribution( sclaeFactor, ShapeFactor );
            double[] lognormal = new double[cellnumber];
            for(int i = 0; i < cellnumber; i++){
                if(cell[i][4] == 1){
                    lognormal[i] = a.sample();
                    cell[i][5] = lognormal[i]; // this is the activity assigned to the cell   
                    sum1 += cell[i][5];
                }       
            }
		
            for(int i = 0; i < cellnumber; i++){
                    cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;
            }

            return cell;        
    }
     
    public static double[][] generateUniformActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
            int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
            String Shape,
            double MeanActivity, double Tau, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
            double[][] cell) {
        
            labelCells(NECROTIC, DEAD_NECROTIC, cellnumber, Height, labelcellnum, Radius, shellWidth, Shape, coldcellnumber, resetFlag, resetLabelCellNum, cell );           
            double sum1 = 0.0;
            
            for(int i = 0; i < cellnumber; i++){
                if(cell[i][4] == 1){       
                        cell[i][5] = 1;
                        sum1 += cell[i][5];
                    }       
                }
		
            for(int i = 0; i < cellnumber; i++){
                    cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;
            }

            return cell;        
    } 
    
    public static double[][] generateLinearActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
            int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
            String Shape, int rCell,
            double AccuActivity, double constantProvided, double MeanActivity, double Tau, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
            double[][] cell) {
        
            labelCells(NECROTIC, DEAD_NECROTIC, cellnumber, Height, labelcellnum, Radius, shellWidth, Shape, coldcellnumber, resetFlag, resetLabelCellNum, cell );           
            double rToCell, sum1 = 0.0, coldRadius = Radius - shellWidth, edgeActivity = 0;
            
            //assign edge activity
            if(Shape.toLowerCase().equals("sphere")){
                    edgeActivity = Radius;				
            }
            else if(Shape.toLowerCase().equals("rod")){
                    if(Radius > Height/2){ //flat rod
                        edgeActivity = Height/2;
                    } else{ //tall rod
                        edgeActivity = Radius; 
                    }
            }
            else if(Shape.toLowerCase().equals("ellipsoid")){
                    edgeActivity = Radius;
            }
            else if(Shape.toLowerCase().equals("cone")){ //cone
                    edgeActivity = Height - (Height/(1+Radius/Math.sqrt(Height*Height + Radius*Radius)));
            }
            for(int i = 0; i < cellnumber; i++){
                if(cell[i][4] == 1){       
                        if(Shape.toLowerCase().equals("sphere")){
                                rToCell = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow((cell[i][2]), 2) + Math.pow(cell[i][3], 2));
                                //JCW/RWH 5/1/20
                                if(NECROTIC){
                                    cell[i][5] = ((1-constantProvided) * (rToCell-coldRadius) + constantProvided*(Radius-coldRadius))*Radius/(Radius-coldRadius); 
                                } else {
                                    cell[i][5] = (1-constantProvided) * rToCell + constantProvided*Radius;
                                }                               
                        }
                        else if(Shape.toLowerCase().equals("rod")){                                       
                                //JCW 5/12/20. Algothm from Howell. 
                                double r = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][3], 2));
                                double dr = Radius - r;
                                double y = -cell[i][2];
                                double dt = Height - y;
                                if (Height/2.0 >= Radius){ //tall rod
                                    if(y <= Height-Radius && y >= Radius){ //mid section
                                        if(NECROTIC){
                                            cell[i][5] = ((1-constantProvided) * (r - coldRadius) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                        } else {
                                            cell[i][5] = (1-constantProvided) * r + constantProvided* edgeActivity;
                                        }  
                                    }
                                    if (y >= Height - Radius){ //top section
                                        if (dr >= dt) {
                                            if(NECROTIC){
                                               cell[i][5] = ((1-constantProvided) * (shellWidth - dt) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                            } else {
                                               cell[i][5] = (1-constantProvided) * (Radius - dt) + constantProvided* edgeActivity;
                                            }
                                            
                                        }else {
                                            if(NECROTIC){
                                                cell[i][5] = ((1-constantProvided) * (r - coldRadius) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                            } else {
                                                cell[i][5] = (1-constantProvided) * r + constantProvided* edgeActivity;
                                            }
                                        }
                                    }
                                    if (y <= Radius) { //bottom section
                                        if (dr >= y) {
                                            if(NECROTIC){
                                                cell[i][5] = ((1-constantProvided) * (shellWidth - y) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                            } else {
                                                cell[i][5] = (1-constantProvided) * (Radius - y) + constantProvided* edgeActivity;
                                            }
                                            
                                        } else {
                                            if(NECROTIC){
                                                cell[i][5] = ((1-constantProvided) * (r - coldRadius) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                            } else {
                                                cell[i][5] = (1-constantProvided) * r + constantProvided* edgeActivity;
                                            }
                                        }
                                    }
                                } else { //fat cone
                                    if (r <= Radius - Height/2.0){ //mid
                                        if(y >= Height/2.0){
                                            if(NECROTIC){
                                                cell[i][5] = ((1-constantProvided) * (shellWidth - dt) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                            } else {
                                                cell[i][5] = (1-constantProvided) * (y - Height/2.0) + constantProvided* edgeActivity;
                                            }
                                            
                                        }else {
                                            if(NECROTIC){
                                                cell[i][5] = ((1-constantProvided) * (shellWidth - y) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                            } else {
                                                cell[i][5] = (1-constantProvided) * (Height/2.0 - y) + constantProvided* edgeActivity;
                                            }
                                            
                                        }
                                    } else { //side section
                                        if (y >= Height/2.0){ //upper
                                            if (Radius - r >= Height - y){ //same as if dr >= dt
                                                if(NECROTIC){
                                                    cell[i][5] = ((1-constantProvided) * (shellWidth - dt) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                                } else {
                                                    cell[i][5] = (1-constantProvided) * (y - Height/2.0) + constantProvided* edgeActivity;
                                                }
                                                
                                            }else {
                                                if(NECROTIC){
                                                    cell[i][5] = ((1-constantProvided) * (shellWidth - dr) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                                } else {
                                                    cell[i][5] = (1-constantProvided) * (r - (Radius - Height/2.0)) + constantProvided* edgeActivity;
                                                }
                                                
                                            }
                                        }else { //lower
                                            if (Radius - r >= y){ //same as dr >= y
                                                if(NECROTIC){
                                                    cell[i][5] = ((1-constantProvided) * (shellWidth - y) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                                } else {
                                                    cell[i][5] = (1-constantProvided) * (Height/2.0 - y) + constantProvided* edgeActivity;
                                                }
                                                
                                            } else{
                                               if(NECROTIC){
                                                    cell[i][5] = ((1-constantProvided) * (shellWidth - dr) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                                } else {
                                                    cell[i][5] = (1-constantProvided) * (r - (Radius - Height/2.0)) + constantProvided* edgeActivity;
                                                }
                                                
                                            }
                                        }
                                    }
                                }
                        }
                        else if(Shape.toLowerCase().equals("ellipsoid")){
                            //NOT used
                                rToCell = Math.sqrt(Math.pow(cell[i][1] / Radius, 2) + Math.pow(cell[i][2] / Height, 2) + Math.pow(cell[i][3] / Radius, 2));
                                if (rToCell > Radius){
                                    cell[i][5] = Radius; 
                                } else{
                                    cell[i][5] = rToCell;
                                }			
                        }
                        else if(Shape.toLowerCase().equals("cone")){
                                double y = -cell[i][2];
                                double r = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][3], 2));
                                double de = (y*Radius - Height * r )/ Math.sqrt(Height*Height + Radius*Radius);
                                if (Height - y >= de ){
                                    if (NECROTIC) {
                                        cell[i][5] = ((1-constantProvided) * (shellWidth - de) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                    } else {
                                        cell[i][5] = (1-constantProvided) * (edgeActivity - de) + constantProvided* edgeActivity;
                                    }
                                    
                                } else {
                                    if (NECROTIC) {
                                        cell[i][5] = ((1-constantProvided) * (shellWidth - (Height-y)) + constantProvided* shellWidth)*edgeActivity/shellWidth;
                                    } else {
                                        cell[i][5] = (1-constantProvided) * (edgeActivity - (Height-y) ) + constantProvided* edgeActivity;
                                    }
                                    
                                }
                        }
                        sum1 += cell[i][5];
                    }       
                }
		
		for(int i = 0; i < cellnumber; i++){
			cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;
		}
		
		return cell;        
    }
        
    public static double[][] generateExponentialActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
	                                          int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
	                                          String Shape,
	                                          double AccuActivity, double b, double constantProvided, double MeanActivity, double Tau, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
	                                          double[][] cell){
            labelCells(NECROTIC, DEAD_NECROTIC, cellnumber, Height, labelcellnum, Radius, shellWidth, Shape, coldcellnumber, resetFlag, resetLabelCellNum, cell );           
            double rToCell, sum1 = 0.0, coldRadius = Radius - shellWidth, edgeActivity = 0, A1, A2;
            
            //assign edge activity
            if(Shape.toLowerCase().equals("sphere")){
                    edgeActivity = Radius;				
            }
            else if(Shape.toLowerCase().equals("rod")){
                    if(Radius > Height/2){ //flat rod
                        edgeActivity = Height/2;
                    } else{ //tall rod
                        edgeActivity = Radius; 
                    }
            }
            else if(Shape.toLowerCase().equals("ellipsoid")){
                    edgeActivity = Radius;
            }
            else if(Shape.toLowerCase().equals("cone")){ //cone
                    edgeActivity = Height - (Height/(1+Radius/Math.sqrt(Height*Height + Radius*Radius)));
            }
            for(int j = 0; j < cellnumber; j++) {
                if(cell[j][4] == 1){
                    if(Shape.toLowerCase().equals( "sphere" )){
                            rToCell = Math.sqrt( Math.pow( cell[j][1], 2 ) + Math.pow( (cell[j][2]), 2 ) + Math.pow( cell[j][3], 2 ) );
                            A1 = (NECROTIC? Math.exp(b*(rToCell-coldRadius)) : Math.exp(b*rToCell));
                            A2 = Math.pow( Math.E, b * Radius ) * (Radius * Radius / b - 2.0 * Radius / (b * b) + 2.0 / (b * b * b)) - 2.0 / (b * b * b);
                            cell[j][5] = A1 / (Math.PI * 4.0 * A2);
                    }
                    else if( Shape.toLowerCase().equals( "rod" ) ){

                            double r = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][3], 2));
                            double dr = Radius - r;
                            double y = -cell[j][2];
                            double dt = Height - y;
                            if (Height/2.0 >= Radius){ //tall rod
                                if(y <= Height-Radius && y >= Radius){
                                    if(NECROTIC) {
                                        cell[j][5] = Math.exp(b* (r-coldRadius));
                                    }else {
                                        cell[j][5] = Math.exp(b*r);
                                    }
                                    
                                }
                                if (y >= Height - Radius){
                                    if (dr >= dt) {
                                        if(NECROTIC) {
                                            cell[j][5] = Math.exp(b*(shellWidth - dt));
                                        } else{
                                            cell[j][5] = Math.exp(b*(Radius - dt));
                                        }
                                        
                                    }else {
                                         if(NECROTIC) {
                                            cell[j][5] = Math.exp(b*(r-coldRadius));
                                         }else {
                                            cell[j][5] = Math.exp(b*r);
                                         }
                                        
                                    }
                                }
                                if (y <= Radius) { 
                                    if (dr > y) {
                                        if(NECROTIC) {
                                            cell[j][5] = Math.exp(b*(shellWidth - y)) ;
                                        }else {
                                            cell[j][5] = Math.exp(b*(Radius - y));
                                        }
                                    } else {
                                        if(NECROTIC) {
                                            cell[j][5] = Math.exp(b*(r - coldRadius));
                                        } else{
                                            cell[j][5] = Math.exp(b*r);
                                        }
                                        
                                    }
                                }
                            } else { //fat rod
                                if (r <= Radius - Height/2.0){
                                    if(y >= Height/2.0){
                                        if(NECROTIC){
                                            cell[j][5] = Math.exp(b*(shellWidth - dt));
                                        } else{
                                            cell[j][5] = Math.exp(b*(y - Height/2.0));
                                        }
                                        
                                    }else {
                                        if(NECROTIC){
                                            cell[j][5] = Math.exp(b* (shellWidth - y));
                                        } else {
                                            cell[j][5] = Math.exp(b*(Height/2.0 - y));
                                        }
                                        
                                    }
                                } else {
                                    if (y >= Height/2.0){
                                        if (Radius - r >= Height - y){ //same as dr >= dt
                                            if(NECROTIC){
                                                cell[j][5] = Math.exp(b*(shellWidth - dt));
                                            } else {
                                                cell[j][5] = Math.exp(b*(y - Height/2.0));
                                            }
                                          
                                        }else {
                                            if (NECROTIC){
                                                cell[j][5] = Math.exp(b*(shellWidth - dr));
                                            } else {
                                                cell[j][5] = Math.exp(b*(r - (Radius - Height/2.0)));
                                            }
                                            
                                        }
                                    }else {
                                        if (Radius - r >= y){
                                            if (NECROTIC){
                                                cell[j][5] = Math.exp(b*(shellWidth - y)) ;
                                            } else {
                                                cell[j][5] = Math.exp(b*(Height/2.0 - y)) ;
                                            }
                                            
                                        } else{
                                            if (NECROTIC){
                                                cell[j][5] = Math.exp(b*(shellWidth - dr));
                                            } else {
                                                cell[j][5] = Math.exp(b*(r - (Radius - Height/2.0))) ;
                                            }
                                            
                                        }
                                    }
                                }
                            }
                    }
                    else if (Shape.toLowerCase().equals( "ellipsoid" )){
                        //NOT used
                            rToCell = Math.sqrt( Math.pow( cell[j][1] / Radius, 2 ) + Math.pow( cell[j][2] / Height, 2 ) + Math.pow( cell[j][3] / Radius, 2 ) );
                            A1 = Math.exp(b*rToCell) - 1.0 ;
                            A2 = Math.pow( Math.E, b * 1.0 ) * (1.0 / b - 2.0 / (b * b) + 2.0 / (b * b * b)) - 1.0 / 3.0 - 2.0 / (b * b * b);
                            cell[j][5] = A1 / (Math.PI * 4.0 * A2) + constantProvided / (1 - constantProvided);
                    }else{ //Cone
                            double y = -cell[j][2];
                            double r = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][3], 2));
                            double de = (y*Radius - Height * r )/ Math.sqrt(Height*Height + Radius*Radius);
                            if (Height - y >= de ){
                                if(NECROTIC){
                                    cell[j][5] = Math.exp(b*(shellWidth - de));
                                } else {
                                    cell[j][5] = Math.exp(b*(edgeActivity - de));
                                }
                                
                            } else {
                                if(NECROTIC){
                                    cell[j][5] = Math.exp(b*(shellWidth - (Height-y)) );
                                } else {
                                    cell[j][5] = Math.exp(b*(edgeActivity - (Height-y)) );
                                }                 
                            } 
                    }
                    sum1 += cell[j][5];
                }	
            }

            for(int i = 0; i < cellnumber; i++) {
                    if(cell[i][4] != 0){
                            cell[i][5] = cell[i][5] *  MeanActivity * labelcellnum / sum1 * Tau;
                    }
            }

            return cell;
    }
    
    public static double[][] generatePolynomialActivity(
                boolean NECROTIC, boolean DEAD_NECROTIC,
                int cellnumber, int Radius, int Height, int labelcellnum, int degree,
                double[] coefficients, double shellWidth, double AccuActivity,
                String Shape,
                double MeanActivity, double Tau, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
                double[][] cell
	){
            labelCells(NECROTIC, DEAD_NECROTIC, cellnumber, Height, labelcellnum, Radius, shellWidth, Shape, coldcellnumber, resetFlag, resetLabelCellNum, cell );           
            double rToCell, sum1 = 0.0, coldRadius = Radius - shellWidth, edgeActivity, rho;
            if (Shape.equalsIgnoreCase("rod")){
                    if(Radius > Height/2){ //flat rod
                        edgeActivity = Height/2;
                    } else{ //tall rod
                        edgeActivity = Radius; 
                    }
                } 
                else if (Shape.equalsIgnoreCase("Cone")){
                    edgeActivity = Height - (Height/(1+Radius/Math.sqrt(Height*Height + Radius*Radius)));
                } else{
                    edgeActivity = Radius;
                }
            for(int j = 0; j < cellnumber; j++) {
                if(cell[j][4] == 1){
                    if(Shape.toLowerCase().equals("sphere")){
                        rToCell = Math.sqrt( Math.pow( cell[j][1], 2 ) + Math.pow( (cell[j][2]), 2 ) + Math.pow( cell[j][3], 2 ) );
                        rho = 0.0;
                        if(NECROTIC){
                            for(int k = degree; k >= 0; k--) {
                                rho += coefficients[k]*Math.pow( rToCell-coldRadius, k );
                            }
                        } else {
                            for(int k = degree; k >= 0; k--) {
                                rho += coefficients[k]*Math.pow( rToCell, k );
                            }
                        }
                        
                        rho = (rho >= 0) ? rho : 0;
                        cell[j][5] = rho;
                    }
                    else if(Shape.toLowerCase().equals("rod")){                                       
                        //JCW 6/8/20. 
                        double r = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][3], 2));
                        double dr = Radius - r;
                        double y = -cell[j][2];
                        double dt = Height - y;
                        rho = 0.0;
                        if (Height/2.0 >= Radius){ //tall rod
                            if(y <= Height-Radius && y >= Radius){
                                if(NECROTIC){
                                    for(int k = degree; k >= 0; k--) {
                                        rho += coefficients[k]*Math.pow( r - coldRadius, k );
                                    }
                                } else{
                                    for(int k = degree; k >= 0; k--) {
                                        rho += coefficients[k]*Math.pow( r, k );
                                    }
                                }
                                
                                rho = (rho >= 0) ? rho : 0;
                            }
                            if (y >= Height - Radius){
                                if (dr >= dt) {
                                    if(NECROTIC){
                                        for(int k = degree; k >= 0; k--) {
                                        rho += coefficients[k]*Math.pow( shellWidth - dt, k );
                                        }   
                                    }else{
                                        for(int k = degree; k >= 0; k--) {
                                        rho += coefficients[k]*Math.pow( Radius - dt, k );
                                        }
                                    }
                                    
                                    rho = (rho >= 0) ? rho : 0;
                                    //cell[j][5] = (1-constantProvided) * (y - (Height - Radius)) + constantProvided* edgeActivity;
                                }else {
                                    if(NECROTIC){
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( r - coldRadius, k );
                                        }
                                    }else{
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( r, k );
                                        }
                                    }
                                    
                                    rho = (rho >= 0) ? rho : 0;
                                    //cell[j][5] = (1-constantProvided) * r + constantProvided* edgeActivity;
                                }
                            }
                            if (y <= Radius) { 
                                if (dr > y) {
                                    if(NECROTIC){
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( shellWidth - y, k );
                                        }
                                    }else{
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( Radius - y, k );
                                        }
                                    }
                                    
                                    rho = (rho >= 0) ? rho : 0;
                                    //cell[j][5] = (1-constantProvided) * (Radius - y) + constantProvided* edgeActivity;
                                } else {
                                    if(NECROTIC){
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( r - coldRadius, k );
                                        }
                                    }else{
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( r, k );
                                        }
                                    }
                                    
                                    rho = (rho >= 0) ? rho : 0;
                                    //cell[j][5] = (1-constantProvided) * r + constantProvided* edgeActivity;
                                }
                            }
                        } else { //fat rod
                            if (r <= Radius - Height/2.0){
                                if(y >= Height/2.0){
                                    if(NECROTIC){
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow(shellWidth - dt, k );
                                        }
                                    }else{
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( y - Height/2.0, k );
                                        }
                                    }
                                    
                                    rho = (rho >= 0) ? rho : 0;
                                    //cell[j][5] = (1-constantProvided) * (y - Height/2.0) + constantProvided* edgeActivity;
                                }else {
                                    if(NECROTIC){
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow(shellWidth - y, k );
                                        }
                                    }else{
                                        for(int k = degree; k >= 0; k--) {
                                            rho += coefficients[k]*Math.pow( Height/2.0 - y, k );
                                        }
                                    }
                                    
                                    rho = (rho >= 0) ? rho : 0;
                                    //cell[j][5] = (1-constantProvided) * (Height/2.0 - y) + constantProvided* edgeActivity;
                                }
                            } else {
                                if (y >= Height/2.0){
                                    if (Radius - r >= Height - y){
                                        if(NECROTIC){
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( shellWidth - dt, k );
                                            }
                                        }else{
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( y - Height/2.0, k );
                                            }
                                        }
                                        
                                    rho = (rho >= 0) ? rho : 0;
                                    //cell[j][5] = (1-constantProvided) * (y - Height/2.0) + constantProvided* edgeActivity;
                                    }else {
                                        if(NECROTIC) {
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( shellWidth - dr, k );
                                            }
                                        }else {
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( r - (Radius - Height/2.0), k );
                                            }
                                        }
                                        
                                        rho = (rho >= 0) ? rho : 0;
                                        //cell[j][5] = (1-constantProvided) * (r - (Radius - Height/2.0)) + constantProvided* edgeActivity;
                                    }
                                }else {
                                    if (Radius - r >= y){
                                        if(NECROTIC){
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( shellWidth - y , k );
                                            }
                                        }else{
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( Height/2.0 - y, k );
                                            }
                                        }
                                        
                                        rho = (rho >= 0) ? rho : 0;
                                        //cell[j][5] = (1-constantProvided) * (Height/2.0 - y) + constantProvided* edgeActivity;
                                    } else{
                                        if(NECROTIC){
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( shellWidth - dr , k );
                                            }
                                        }else{
                                            for(int k = degree; k >= 0; k--) {
                                                rho += coefficients[k]*Math.pow( r - (Radius - Height/2.0), k );
                                            }
                                        }
                                        
                                        rho = (rho >= 0) ? rho : 0;
                                        //cell[j][5] = (1-constantProvided) * (r - (Radius - Height/2.0)) + constantProvided* edgeActivity;
                                    }
                                }
                            }
                        }
                        cell[j][5] = rho;
                    }
                    else if(Shape.toLowerCase().equals("cone")){
                        double y = -cell[j][2];
                        double r = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][3], 2));
                        double de = (y*Radius - Height * r )/ Math.sqrt(Height*Height + Radius*Radius);
                        rho = 0;
                        if (Height - y >= de ){
                            if(NECROTIC){
                                for(int k = degree; k >= 0; k--) {
                                rho += coefficients[k]*Math.pow( shellWidth - de, k );
                                }
                            } else{
                                for(int k = degree; k >= 0; k--) {
                                rho += coefficients[k]*Math.pow( edgeActivity - de, k );
                                }
                            }
                            
                            rho = (rho >= 0) ? rho : 0;
                            //cell[j][5] = (1-constantProvided) * (edgeActivity - de) + constantProvided* edgeActivity;
                        } else {
                            if(NECROTIC){
                                for(int k = degree; k >= 0; k--) {
                                rho += coefficients[k]*Math.pow( shellWidth - (Height-y), k );
                                }
                            } else{
                                for(int k = degree; k >= 0; k--) {
                                rho += coefficients[k]*Math.pow( edgeActivity - (Height-y), k );
                                }
                            }
                            
                            rho = (rho >= 0) ? rho : 0;
                            //cell[j][5] = (1-constantProvided) * (edgeActivity - (Height-y) ) + constantProvided* edgeActivity;
                        }
                        cell[j][5] = rho;
                    }

                    sum1 += cell[j][5];
                }

            }
                
            for(int i = 0; i < cellnumber; i++) {
                    cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;
            }

            return cell;
    }
    
    public static double[][] generate4VarLogActivity(
                boolean NECROTIC, boolean DEAD_NECROTIC,
                double a, double b, double x0, double y0,
	        int cellnumber, int Radius, int labelcellnum, int Height,
	        String Shape, double shellWidth, int dBwtnCells,
	        double AccuActivity, double constantProvided, double MeanActivity, double Tau, int coldcellnumber, boolean[] resetFlag, int[] resetLabelCellNum,
	        double[][] cell
	){
        labelCells(NECROTIC, DEAD_NECROTIC, cellnumber, Height, labelcellnum, Radius, shellWidth, Shape, coldcellnumber, resetFlag, resetLabelCellNum, cell );
        double rToCell, sum1 = 0.0, coldRadius = Radius - shellWidth, edgeActivity, rho;
            if (Shape.equalsIgnoreCase("rod")){
                    if(Radius > Height/2){ //flat rod
                        edgeActivity = Height/2;
                    } else{ //tall rod
                        edgeActivity = Radius; 
                    }
                } 
                else if (Shape.equalsIgnoreCase("Cone")){
                    edgeActivity = Height - (Height/(1+Radius/Math.sqrt(Height*Height + Radius*Radius)));
                } else{
                    edgeActivity = Radius;
                }
            for(int i = 0; i < cellnumber; i++) {
                if(cell[i][4] == 1){
                    if(Shape.toLowerCase().equals("sphere")){
                        rToCell = Math.sqrt( Math.pow( cell[i][1], 2 ) + Math.pow( (cell[i][2]), 2 ) + Math.pow( cell[i][3], 2 ) );
                        // y = y0 + (a/x) * Math.exp((-1/2)*Math.pow((Math.log(x/x0)/b), 2));
                        /**
                         * assuming
                         * y0 = initial activity
                         * y = activity
                         * x = radial position
                         * x0 = edge of hte cell (max radial position
                         * a = ????
                         * b = ????
                         */
                        //double x = Radius - rToCell;
                        double x = (NECROTIC? rToCell-coldRadius: rToCell);
                        rho = y0 + (a/(x+dBwtnCells)) * Math.exp((-1.0/2.0)*Math.pow((Math.log((x+dBwtnCells)/x0)/b), 2));
                        if(rho < 0){
                                rho = 0;
                                System.err.println( "too low!!!" );
                        }
                        else if (Double.isNaN( rho )){
                                System.err.println( "whoops!?!?!?" );
                                rho = 0.0;
                        }
                        cell[i][5] = rho;
            
                    } else if(Shape.toLowerCase().equals("rod")){                                       
                                //JCW 5/12/20. Algothm from Howell. 
                                double r = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][3], 2));
                                double dr = Radius - r;
                                double y = -cell[i][2];
                                double dt = Height - y;
                                double x = r;
                                if (Height/2.0 >= Radius){ //tall rod
                                    if(y <= Height-Radius && y >= Radius){
                                        x = (NECROTIC? r-coldRadius: r);
                                        
                                    }
                                    if (y >= Height - Radius){
                                        if (dr >= dt) {
                                            x = (NECROTIC? shellWidth - dt : Radius - dt);
                                            
                                        }else {
                                            x = (NECROTIC? r - coldRadius: r);
                                            
                                        }
                                    }
                                    if (y <= Radius) { 
                                        if (dr > y) {
                                            x = (NECROTIC? shellWidth - y: Radius - y);
                                            
                                        } else {
                                            x = (NECROTIC? r - coldRadius: r);
                                           
                                        }
                                    }
                                } else { //fat rod
                                    if (r <= Radius - Height/2.0){
                                        if(y >= Height/2.0){
                                            x = (NECROTIC? shellWidth - dt: y - Height/2.0);
                                            
                                        }else {
                                            x = (NECROTIC? shellWidth - y: Height/2.0 - y);
                                            
                                        }
                                    } else {
                                        if (y >= Height/2.0){
                                            if (Radius - r >= Height - y){
                                                x = (NECROTIC? shellWidth - dt: y - Height/2.0);
                                                
                                            }else {
                                                x = (NECROTIC? shellWidth - dr: r - (Radius - Height/2.0));
                                                //x = (NECROTIC? shellWidth -(Radius - r): r - (Radius - Height/2.0)); 
                                         }
                                        }else {
                                            if (Radius - r >= y){
                                                x = (NECROTIC? shellWidth - y: Height/2.0 - y);
                                                
                                            } else{
                                                x = (NECROTIC? shellWidth - dr: r - (Radius - Height/2.0));
                                               //x = (NECROTIC? shellWidth -(Radius - r): r - (Radius - Height/2.0)); 
                                            }
                                        }
                                    }
                                }
                                rho = y0 + (a/(x+dBwtnCells)) * Math.exp((-1.0/2.0)*Math.pow((Math.log((x+dBwtnCells)/x0)/b), 2));
                                if(rho < 0){
                                        rho = 0;
                                        System.err.println( "too low!!!" );
                                }
                                else if (Double.isNaN( rho )){
                                        System.err.println( "whoops!?!?!?" );
                                        rho = 0.0;
                                }
                                cell[i][5] = rho;
                        }
                        else if(Shape.toLowerCase().equals("ellipsoid")){
                            //NOT used
                                rToCell = Math.sqrt(Math.pow(cell[i][1] / Radius, 2) + Math.pow(cell[i][2] / Height, 2) + Math.pow(cell[i][3] / Radius, 2));
                                if (rToCell > Radius){
                                    cell[i][5] = Radius; 
                                } else{
                                    cell[i][5] = rToCell;
                                }			
                        }
                        else if(Shape.toLowerCase().equals("cone")){
                                double y = -cell[i][2];
                                double r = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][3], 2));
                                double de = (y*Radius - Height * r )/ Math.sqrt(Height*Height + Radius*Radius);
                                double x = r;
                                if (Height - y >= de ){
                                    x = (NECROTIC? shellWidth - de: edgeActivity - de); 
                                    
                                } else {
                                    x = (NECROTIC? shellWidth - (Height-y): edgeActivity - (Height-y));
                                    
                                }
                                rho = y0 + (a/(x+dBwtnCells)) * Math.exp((-1.0/2.0)*Math.pow((Math.log((x+dBwtnCells)/x0)/b), 2));
                                if(rho < 0){
                                        rho = 0;
                                        System.err.println( "too low!!!" );
                                }
                                else if (Double.isNaN( rho )){
                                        System.err.println( "whoops!?!?!?" );
                                        rho = 0.0;
                                }
                                cell[i][5] = rho;      
                        } 
                    sum1 += cell[i][5]; 
                }
            }
            for(int i = 0; i < cellnumber; i++) {
                    cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum1 * Tau;
            }
            return cell;
    }
//    public static double[][][] generateDrugSpecificActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
//            int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
//            String Shape, int rCell,
//            double AccuActivity, double constantProvided, double MeanActivity, double Tau,
//            double[][] cell) {
//        return
//    }
                
}
