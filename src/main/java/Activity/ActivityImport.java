package Activity;

import javax.swing.*;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Jianchao on 4/10/2020.
 */
public class ActivityImport {
	public static double[][] generateActivity(boolean NECROTIC, boolean DEAD_NECROTIC,
	                                          int cellnumber, int Height, int labelcellnum, int longestaxis, int Radius, double shellWidth,
	                                          String Shape,
	                                          TreeMap radiusActivityMap, double MeanActivity, double Tau,
	                                          double[][] cell){
            int TMC = 0, j;
            final double cosT = Math.cos(Math.atan2(Radius, Height));
            double y1, y2, x1, x2, rToCell, sum = 0.0;
            Random randomgen = new Random();
            
            for(int i = 0; i < cellnumber; i++){
			cell[i][5] = MeanActivity * Tau;
		}

            for(int i = 0; i < labelcellnum; i++){
                    // pick a cell to label if its already labeled try again
                    j = randomgen.nextInt(cellnumber);
                    if(cell[j][4] != 0){
                            i--;
                            continue;
                    }
                    cell[j][4] = 1; // this cell is labeled 
                    rToCell = Math.sqrt(Math.pow(cell[j][1], 2) + Math.pow((cell[j][2]), 2) + Math.pow(cell[j][3], 2));
                    if (rToCell >= (double)radiusActivityMap.firstKey() && rToCell <= (double)radiusActivityMap.lastKey()) {
                        
                        x1 = (double)radiusActivityMap.floorKey(rToCell);
                        x2 = (double) radiusActivityMap.ceilingKey(rToCell);
                        y1 = (double) radiusActivityMap.get(x1);
                        y2 = (double) radiusActivityMap.get(x2);
                        if (rToCell == x1) {
                            cell[j][5] = y1;
                        }
                        if (rToCell == x2) {
                            cell[j][5] = y2;
                        } else{
                            cell[j][5] = interpolate(x1, y1, x2, y2, rToCell);
                        }                 
                    }
                    sum += cell[j][5];
            }
            for(int i = 0; i < cellnumber; i++){
			cell[i][5] = cell[i][5] * MeanActivity * labelcellnum / sum * Tau;
            }
            
            return cell;
        }
        
	public static double interpolate(double x1, double y1, double x2, double y2, double x) {       
            double activity = 0.0;
            activity = y1 + (x - x1) * (y2 - y1) / (x2- x1);           
            return activity;
        }	

}
