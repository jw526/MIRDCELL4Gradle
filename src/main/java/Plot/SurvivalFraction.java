/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Plot;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author rusty
 */
public class SurvivalFraction {
    
    public void generateSurvivalCurve(JPanel panel, JComboBox xLabelComboBox, JComboBox yLabelComboBox, double miny, double[][] PlotOutput, double Tau, double cellnumber){
        XYSeries series = new XYSeries("XYGraph");
        String xLabel = xLabelComboBox.getSelectedItem().toString();
        String yLabel = yLabelComboBox.getSelectedItem().toString();
        
		
        for (int i = 0; i < PlotOutput[0].length; i++) {
            // Tumor control prob.
            if (yLabelComboBox.getSelectedIndex()==3){
                if (PlotOutput[7][i] > 0) {
                        
                        if(xLabelComboBox.getSelectedIndex()==5){ //mean decays per cell
                            series.add(PlotOutput[0][i] * Tau, Math.pow(1-PlotOutput[7][i], cellnumber));
                        } else if(xLabelComboBox.getSelectedIndex()==6){ //mean decays per labeled cell
                            series.add(PlotOutput[2][i] * Tau, Math.pow(1-PlotOutput[7][i], cellnumber));
                          }
                        else {
                            series.add(PlotOutput[xLabelComboBox.getSelectedIndex()][i], Math.pow(1-PlotOutput[7][i], cellnumber));
                        }
                } else {
                        
                        if(xLabelComboBox.getSelectedIndex()==5){ //mean decays
                            series.add(PlotOutput[0][i] * Tau, 1);
                        }else if(xLabelComboBox.getSelectedIndex()==6){ //mean decays per labeled cell
                            series.add(PlotOutput[2][i] * Tau, Math.pow(1-PlotOutput[7][i], cellnumber));
                          }                        
                        else{
                            series.add(PlotOutput[xLabelComboBox.getSelectedIndex()][i], 1);
                        }
                }
            } 
            // TCP 2nd algo: prod(1-SPi)
            else if(yLabelComboBox.getSelectedIndex()==4){
                                       
                if(xLabelComboBox.getSelectedIndex()==5){ //mean decays per cell
                    series.add(PlotOutput[0][i] * Tau, PlotOutput[8][i]);
                } else if(xLabelComboBox.getSelectedIndex()==6){ //mean decays per labeled cell
                    series.add(PlotOutput[2][i] * Tau, PlotOutput[8][i]);
                  }
                else {
                    series.add(PlotOutput[xLabelComboBox.getSelectedIndex()][i], PlotOutput[8][i]);
                }
                
            }
            else{ // SF of labeled, unlabeled, and mixed
                if (PlotOutput[yLabelComboBox.getSelectedIndex() + 5][i] > 0) {
                       
                        if(xLabelComboBox.getSelectedIndex()==5){ //mean decays
                            series.add(PlotOutput[0][i] * Tau, PlotOutput[yLabelComboBox.getSelectedIndex() + 5][i]);
                        } else if(xLabelComboBox.getSelectedIndex()==6){ //mean decays per labeled cell
                            series.add(PlotOutput[2][i] * Tau, PlotOutput[yLabelComboBox.getSelectedIndex() + 5][i]);
                          }                       
                        else{
                            series.add(PlotOutput[xLabelComboBox.getSelectedIndex()][i], PlotOutput[yLabelComboBox.getSelectedIndex() + 5][i]);
                        }
                } else if (PlotOutput[yLabelComboBox.getSelectedIndex() + 5][i] <= 0) {
                        //series.add(SurvCalcComplex.PlotOutput[jComboBox11.getSelectedIndex()][i], 1.0E-12D);
                }
            }
        }
       
		
        XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		// Generate the graph
		panel.removeAll();
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Surviving Fraction Curve", // Title
				xLabel, // x-axis Label
				yLabel, // y-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
		XYPlot plot = chart.getXYPlot();
		//plot.setRenderer(new XYSplineRenderer());

		NumberAxis domainAxis = new NumberAxis(xLabel);
                NumberAxis rangeAxis = new LogarithmicAxis(yLabel);
                if (yLabelComboBox.getSelectedIndex()==3){
                    rangeAxis = new NumberAxis(yLabel);
                } 
		
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		//if (miny < .00001) {
		//   miny = .00001;
		//}

		rangeAxis.setRange(miny, 1.1);
		plot.setDomainAxis(domainAxis);
		plot.setRangeAxis(rangeAxis);
		chart.setBackgroundPaint(Color.white);
		plot.setOutlinePaint(Color.black);
		ChartPanel CP = new ChartPanel(chart);
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CP, BorderLayout.CENTER);
		panel.validate();
    }
    
}
