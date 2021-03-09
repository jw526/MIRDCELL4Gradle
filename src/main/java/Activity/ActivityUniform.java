package Activity;

import javax.swing.*;
import java.util.Random;
//import WebDose.*;

/**
 * Created by ar548 on 7/22/2016.
 */
public class ActivityUniform {
	public static double[][] generateActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
	                                          int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
	                                          String Shape,
	                                          double AccuActivity,
	                                          double[][] cell){
		int TMC = 0, j;
		Random randomgen = new Random();
		double rToCell;
		double rToTop = 0, distFromEdge, rAtH = 0, h = 0;
		final double cosT = Math.cos( Math.atan2( Radius, Height ) );
		final double COM = 3.0 * Height / 4.0;
                
                int labeledCell = labelcellnum;
		if(NECROTIC) {
			for(int i = 0; i < labelcellnum && AccuActivity > 0; i++) {
				j = randomgen.nextInt( cellnumber );

				// is the cell labelable?
				if(Shape.toLowerCase().equals( "sphere" )) {
					rToCell = Math.sqrt(Math.pow( cell[j][1], 2 ) + Math.pow( cell[j][2], 2 ) + Math.pow( cell[j][3], 2 ));
					if(cell[j][4] != 0 || rToCell <= Radius - shellWidth) {
						i--;
						TMC++;
						if(TMC == cellnumber * 2) {
							JOptionPane.showMessageDialog( null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE );
                                                        labeledCell = i;
							break;
						}
						continue;
					}
				}
				else if(Shape.toLowerCase().equals( "rod" )) {
					rToCell = Math.sqrt(Math.pow( cell[j][1], 2 ) + Math.pow( cell[j][3], 2 ));
					if(cell[j][4] != 0 || (rToCell <= (Radius - shellWidth) && -cell[j][2] <= (Height - shellWidth) && -cell[j][2] >= shellWidth)){
						i--;
						TMC++;
						if(TMC == cellnumber * 2) {
							JOptionPane.showMessageDialog( null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE );
							labeledCell = i;
                                                        break;
						}
						continue;
					}
				}
				else if(Shape.toLowerCase().equals( "ellipsoid" )) {
					double innershort = Radius  - shellWidth;
					double innerlong = Height - shellWidth;
					rToCell = Math.sqrt( Math.pow( cell[j][1], 2 ) / Math.pow( innershort, 2 ) + Math.pow( cell[j][2], 2 ) / Math.pow( innerlong, 2 ) + Math.pow( cell[j][3], 2 ) / Math.pow( innershort, 2 ) );
					if(cell[j][4] != 0 || rToCell < 1) {
						i--;
						TMC++;
						if(TMC == cellnumber * 2) {
							JOptionPane.showMessageDialog( null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE );
							labeledCell = i;
                                                        break;
						}
						continue;
					}
				}
				else if(Shape.toLowerCase().equals( "cone" )) {
					rToCell = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow(cell[j][3], 2));
					h = -cell[j][2];
					rAtH = (double) Radius / (double) Height * h;
					double de = (h*Radius - Height * rToCell )/ Math.sqrt(Height*Height + Radius*Radius);				
					if(cell[j][4] != 0 || (de >= shellWidth && h <= (Height - shellWidth) && h >= shellWidth)){
						i--;
						TMC++;
						if(TMC == cellnumber * 2) {
							JOptionPane.showMessageDialog( null, "You are trying to label more cells than are in the labeled zone.\nLabeled cells: " + i + "        % of cells labeled: " + ((double) i++ / (double) cellnumber * 100.0) + "\nwhen running future simulation with the same dimensions use these numbers", "Too many cells to label", JOptionPane.WARNING_MESSAGE );
							labeledCell = i;
                                                        break;
						}
						continue;
					}
				}
				else {
					JOptionPane.showMessageDialog( null, "Unfortunately Necrotic Geometry is not yet implemented for Conical shapes yet. Sorry for the inconvenience.", "We're Sorry", JOptionPane.WARNING_MESSAGE );
					return cell;
				}
				TMC = 0;

				//label the cell
				cell[j][4] = 1; // the cell is labeled
				
			}
                        
                        for(int i = 0; i < cellnumber; i++){
                            if(cell[i][4] == 1) 
                            cell[i][5] = AccuActivity * cellnumber / labeledCell;
                        }
                        
                        
                        if(DEAD_NECROTIC){
                            for(int i = 0; i < cellnumber; i++){
                                if(Shape.toLowerCase().equals( "sphere" )) {
					rToCell = Math.sqrt(Math.pow( cell[i][1], 2 ) + Math.pow( cell[i][2], 2 ) + Math.pow( cell[i][3], 2 ));
					if(rToCell <= (Radius - shellWidth)) {
						cell[i][0] = 0; // label this cell dead
						continue;
					}
				}
				else if(Shape.toLowerCase().equals( "rod" )) {
					rToCell = Math.sqrt(Math.pow(cell[i][1], 2) + Math.pow(cell[i][3], 2));
                                            if(rToCell <= (Radius - shellWidth) && -cell[i][2] <= (Height - shellWidth) && -cell[i][2] >= shellWidth){
                                            cell[i][0] = 0;
                                            continue;
					}
				}
				else if(Shape.toLowerCase().equals( "ellipsoid" )) {
					double innershort = Radius - shellWidth;
					double innerlong = Height - shellWidth;
					rToCell = Math.sqrt( Math.pow( cell[i][1], 2 ) / Math.pow( innershort, 2 ) + Math.pow( cell[i][2], 2 ) / Math.pow( innerlong, 2 ) + Math.pow( cell[i][3], 2 ) / Math.pow( innershort, 2 ) );
					if(rToCell < 1) {
                                                cell[i][0] = 0;
						continue;
					}
				}else if(Shape.toLowerCase().equals("cone")){ //JCW 6/18/2020
                                                h = -cell[i][2];
                                                rAtH = (double) Radius / (double) Height * h;
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
			for(int i = 0; i < labelcellnum && AccuActivity > 0; i++) {
				j = randomgen.nextInt( cellnumber );                                
				if(cell[j][4] != 0) {
					i--;
                                        continue;
				}				
                                cell[j][4] = 1;
                                cell[j][5] = AccuActivity;
			}
		}
		return cell;
	}
}
