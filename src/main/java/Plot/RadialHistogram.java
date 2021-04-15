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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author rusty
 */
public class RadialHistogram {
    public void generateHistogram(String Shape, double[] avgActivity, double[] avgDose, double[] avgSelfDose, double[] avgCrossDose, double[] ulAvgCrossDose,
        int[] numCellsAt, int[] ulNumCellsAt, double Tau, int longestaxis, int Radius, JPanel panel, JComboBox cb){
                String Ylab = "";
		XYSeries seriesR = new XYSeries("Radial Dataset");
                if(Shape.equals("Sphere")){
                    switch(cb.getSelectedIndex()){
                    case (0): 
                        for (int i = 0; i < longestaxis + 1; i++) {
                                if (avgActivity[i] != 0) {
                                        seriesR.add(i, avgActivity[i]);
                                }
                        }
                        Ylab = "Mean Activity Per Labeled Cell (Bq)";
                        break;
                    case(1):
                         for (int i = 0; i < longestaxis + 1; i++) {
                                if (avgDose[i] != 0) {
                                        seriesR.add(i, avgDose[i]);
                                }
                        }
                        Ylab = "Mean Absorbed Dose To Labeled Cells (Gy)";
                        break;
                    case(2): 
                        for (int i = 0; i < longestaxis + 1; i++) {
                                if (avgDose[i] != 0) {
                                        seriesR.add(i, avgSelfDose[i]);
                                }
                        }
                        Ylab = "Mean Self Absorbed Dose To Labeled Cells (Gy)";
                        break;
                    case(3):
                        for (int i = 0; i < longestaxis + 1; i++) {
                                if (avgDose[i] != 0) {
                                        seriesR.add(i, avgCrossDose[i]);
                                }
                        }
                        Ylab = "Mean Cross Absorbed Dose To Labeled Cells (Gy)";
                        break;
                    case(4):
                        for (int i = 0; i < longestaxis + 1; i++) {
                                if (avgActivity[i] != 0) {
                                        seriesR.add(i, avgActivity[i]*Tau);
                                }
                        }
                        Ylab = "Mean Decays Per Labeled Cell";
                        break;
                    case(5):
                        for (int i = 0; i < longestaxis + 1; i++) {
                            if (ulAvgCrossDose[i] != 0) {
                                seriesR.add(i, ulAvgCrossDose[i]);
                            }
                        }
                        Ylab = " Mean Cross Absorbed Dose To Unlabeled Cells (Gy)";
                        break;
                    case(6):
                        for (int i = 0; i < longestaxis + 1; i++) {
                            if (avgDose[i] + ulAvgCrossDose[i] != 0) {
                                if (numCellsAt[i] + ulNumCellsAt[i] > 0){
                                    seriesR.add(i, avgDose[i] * numCellsAt[i]/(numCellsAt[i] + ulNumCellsAt[i]) + ulAvgCrossDose[i]* ulNumCellsAt[i]/(numCellsAt[i] + ulNumCellsAt[i]));
                                }
                            }
                        }
                        Ylab = " Mean Absorbed Dose To Cells (Gy)";
                        break;    
                    
                    }
                    
                }
                else{
                    switch(cb.getSelectedIndex()){
                    case (0): 
                        for (int i = 0; i < Radius; i++) {
                                if (avgActivity[i] != 0) {
                                        seriesR.add(i, avgActivity[i]);
                                }
                        }
                        Ylab = "Mean Activity Per Labeled Cell (Bq)";
                        break;
                    case(1):
                         for (int i = 0; i < Radius; i++) {
                                if (avgDose[i] != 0) {
                                        seriesR.add(i, avgDose[i]);
                                }
                        }
                        Ylab = "Mean Absorbed Dose To Labeled Cells (Gy)";
                        break;
                    case(2): 
                        for (int i = 0; i < Radius; i++) {
                                if (avgDose[i] != 0) {
                                        seriesR.add(i, avgSelfDose[i]);
                                }
                        }
                        Ylab = "Mean Self Absorbed Dose To Labeled Cells (Gy)";
                        break;
                    case(3):
                        for (int i = 0; i < Radius; i++) {
                                if (avgDose[i] != 0) {
                                        seriesR.add(i, avgCrossDose[i]);
                                }
                        }
                        Ylab = "Mean Cross Absorbed Dose To Labeled Cells (Gy)";
                        break;
                    case(4):
                        for (int i = 0; i < Radius; i++) {
                                if (avgActivity[i] != 0) {
                                        seriesR.add(i, avgActivity[i]*Tau);
                                }
                        }
                        Ylab = "Mean Decays Per Labeled Cell";
                        break;
                    case(5):
                        for (int i = 0; i < Radius; i++) {
                            if (ulAvgCrossDose[i] != 0) {
                                seriesR.add(i, ulAvgCrossDose[i]);
                            }
                        }
                        Ylab = "Mean Cross Absorbed Dose To Unlabeled Cells (Gy)";
                        break;
                    case(6):
                        for (int i = 0; i < Radius; i++) {
                            if (avgDose[i] + ulAvgCrossDose[i] != 0) {
                                if (numCellsAt[i] + ulNumCellsAt[i] > 0){
                                    seriesR.add(i, avgDose[i] * numCellsAt[i]/(numCellsAt[i] + ulNumCellsAt[i]) + ulAvgCrossDose[i]* ulNumCellsAt[i]/(numCellsAt[i] + ulNumCellsAt[i]));
                                }
                            }
                        }
                        Ylab = "Mean Absorbed Dose To Cells (Gy)";
                        break;
                    }
                }
                seriesR.add(0.0, Double.MIN_NORMAL);
		XYSeriesCollection RadialSet = new XYSeriesCollection();
		RadialSet.addSeries(seriesR);


		// generate the chart
		JFreeChart radial = ChartFactory.createXYBarChart(
				"",                             // Title
				"r (μm)",                       // x-axis Label
				false,                          // time label shown?
				Ylab,   // y-axis Label
				RadialSet,                      // Dataset
				PlotOrientation.VERTICAL,       // Plot Orientation
				false,                          // Show Legend?
				true,                           // Use tooltips?
				false                           // Configure chart to generate URLs?
		);
		radial.setBackgroundPaint(new Color(230, 230, 230));
		XYPlot xyplotR = (XYPlot) radial.getPlot();

		// asethetics stuff for the charts
		NumberAxis RangeAxisR = (NumberAxis) xyplotR.getRangeAxis();
		RangeAxisR.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		NumberAxis DomainAxisR = (NumberAxis) xyplotR.getDomainAxis();
		xyplotR.setForegroundAlpha(0.7F);
		xyplotR.setBackgroundPaint(Color.WHITE);
		xyplotR.setDomainGridlinePaint(new Color(150, 150, 150));
		xyplotR.setRangeGridlinePaint(new Color(150, 150, 150));


		// Display the chart
		XYBarRenderer xybarrendererR = (XYBarRenderer) xyplotR.getRenderer();
		xybarrendererR.setShadowVisible(false);
		ChartPanel CPHR = new ChartPanel(radial);
		panel.removeAll();
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CPHR, BorderLayout.CENTER);
		panel.validate();
    }
}
